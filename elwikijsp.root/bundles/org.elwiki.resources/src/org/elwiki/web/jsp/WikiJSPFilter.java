/*
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
 */
package org.elwiki.web.jsp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.wiki.WatchDog;
import org.apache.wiki.WikiContextImpl;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.TextUtil;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.component.IWikiPreferencesConstants;
import org.elwiki.api.event.PageEvent;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;


/**
 * This filter goes through the generated page response prior and places requested resources at the appropriate inclusion markers.
 * This is done to let dynamic content (e.g. plugins, editors) include custom resources, even after the HTML head section is
 * in fact built. This filter is typically the last filter to execute, and it <em>must</em> run after servlet or JSP code that performs
 * redirections or sends error codes (such as access control methods).
 * <p>
 * Inclusion markers are placed by the IncludeResourcesTag; the default content shapes (see .../shapes/default/commonheader.jsp)
 * are configured to do this. As an example, a JavaScript resource marker is added like this:
 * <pre>
 * &lt;wiki:IncludeResources type="script"/&gt;
 * </pre>
 * Any code that requires special resources must register a resource request with the TemplateManager. For example:
 * <pre>
 * &lt;wiki:RequestResource type="script" path="scripts/custom.js" /&gt;
 * </pre>
 * or programmatically,
 * <pre>
 * TemplateManager.addResourceRequest( context, TemplateManager.RESOURCE_SCRIPT, "scripts/customresource.js" );
 * </pre>
 *
 * @see TemplateManager
 * @see org.apache.wiki.tags.RequestResourceTag
 */
//@formatter:off
@Component(
	property = {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN + "=/wiki/*",
		HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=("
		+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=eclipse)",
		Constants.SERVICE_RANKING + ":Integer=1100"},
//	scope=ServiceScope.PROTOTYPE,
	name = "part11.WikiJSPFilter"
)
//@formatter:on
@Deprecated//:FVK: --!!!-- он шлет PageEvent.Topic.REQUESTED, DELIVERED
public class WikiJSPFilter extends WikiServletFilter {

    private static final Logger log = Logger.getLogger( WikiJSPFilter.class );

    @Reference
    EventAdmin eventAdmin;
    
    @Reference
    private Engine m_engine;

    private String m_wiki_encoding;
    private boolean useEncoding;

	@Activate
	protected void startup() {
		log.debug("«web» start " + WikiJSPFilter.class.getSimpleName());
	}

    /** {@inheritDoc} */
    @Override
    public void init( final FilterConfig config ) throws ServletException {
        super.init( config );
        
		@NonNull
		GlobalPreferences globalPrefs = m_engine.getManager(GlobalPreferences.class);
		m_wiki_encoding = globalPrefs.getPreference(IWikiPreferencesConstants.PROP_ENCODING, String.class);
		useEncoding = !globalPrefs.getPreference(IWikiPreferencesConstants.PROP_NO_FILTER_ENCODING, Boolean.class);
    }

    @Override
    public void doFilter( final ServletRequest  request, final ServletResponse response, final FilterChain chain ) throws ServletException, IOException {
        final WatchDog w = WatchDog.getCurrentWatchDog( m_engine );
        try {
            NDC.push( getGlobalPreferences().getApplicationName()+":"+((HttpServletRequest)request).getRequestURI() );
            w.enterState("Filtering for URL "+((HttpServletRequest)request).getRequestURI(), 90 );
            final HttpServletResponseWrapper responseWrapper = new JSPWikiServletResponseWrapper( ( HttpServletResponse )response, m_wiki_encoding, useEncoding );

            // fire PAGE REQUESTED event
            final String pagename = URLConstructor.parsePageFromURL( ( HttpServletRequest )request, Charset.forName( response.getCharacterEncoding() ) );
    		this.eventAdmin.sendEvent(new Event(PageEvent.Topic.REQUESTED,
    				Map.of(PageEvent.PROPERTY_PAGE_ID, pagename)));
            super.doFilter( request, responseWrapper, chain );

            // The response is now complete. Lets replace the markers now.

            // WikiContext is only available after doFilter! (That is after interpreting the jsp)

            try {
                w.enterState( "Delivering response", 30 );
                final WikiContextImpl wikiContext = getWikiContext( request );
                final String r = filter( wikiContext, responseWrapper );

                if( useEncoding ) {
                    final OutputStreamWriter out = new OutputStreamWriter( response.getOutputStream(), response.getCharacterEncoding() );
                    out.write( r );
                    out.flush();
                    out.close();
                } else {
                    response.getWriter().write(r);
                }

                // Clean up the UI messages and loggers
                if( wikiContext != null ) {
                    wikiContext.getWikiSession().clearMessages();
                }

                // fire PAGE DELIVERED event
        		this.eventAdmin.sendEvent(new Event(PageEvent.Topic.DELIVERED,
        				Map.of(PageEvent.PROPERTY_PAGE_ID, pagename)));
            } finally {
                w.exitState();
            }
        } finally {
            w.exitState();
            NDC.pop();
            NDC.remove();
        }
    }

    /**
     * Goes through all types and writes the appropriate response.
     *
     * @param wikiContext The usual processing context
     * @param response The source string
     * @return The modified string with all the insertions in place.
     */
    private String filter( final WikiContextImpl wikiContext, final HttpServletResponse response ) {
        String string = response.toString();

        if( wikiContext != null ) {
            final String[] resourceTypes = TemplateManager.getResourceTypes( wikiContext );
            for( final String resourceType : resourceTypes ) {
                string = insertResources( wikiContext, string, resourceType );
            }

            //  Add HTTP header Resource Requests
            final String[] headers = TemplateManager.getResourceRequests( wikiContext, TemplateManager.RESOURCE_HTTPHEADER );

            for( final String header : headers ) {
                String key = header;
                String value = "";
                final int split = header.indexOf( ':' );
                if( split > 0 && split < header.length() - 1 ) {
                    key = header.substring( 0, split );
                    value = header.substring( split + 1 );
                }

                response.addHeader( key.trim(), value.trim() );
            }
        }

        return string;
    }

    /**
     *  Inserts whatever resources were requested by any plugins or other components for this particular type.
     *
     *  @param wikiContext The usual processing context
     *  @param string The source string
     *  @param type Type identifier for insertion
     *  @return The filtered string.
     */
    private String insertResources( final WikiContextImpl wikiContext, final String string, final String type ) {
        if( wikiContext == null ) {
            return string;
        }

        final String marker = TemplateManager.getMarker( wikiContext, type );
        final int idx = string.indexOf( marker );
        if( idx == -1 ) {
            return string;
        }

        log.debug("...Inserting...");

        final String[] resources = TemplateManager.getResourceRequests( wikiContext, type );
        final StringBuilder concat = new StringBuilder( resources.length * 40 );

        for( final String resource : resources ) {
            log.debug( "...:::" + resource );
            concat.append( resource );
        }

        return TextUtil.replaceString( string, idx, idx + marker.length(), concat.toString() );
    }

    /**
     *  Simple response wrapper that just allows us to gobble through the entire
     *  response before it's output.
     */
    private static class JSPWikiServletResponseWrapper extends HttpServletResponseWrapper {

        ByteArrayOutputStream m_output;
        private ByteArrayServletOutputStream m_servletOut;
        private PrintWriter m_writer;
        private HttpServletResponse m_response;
        private boolean useEncoding;

        /** How large the initial buffer should be.  This should be tuned to achieve a balance in speed and memory consumption. */
        private static final int INIT_BUFFER_SIZE = 0x8000;

        public JSPWikiServletResponseWrapper( final HttpServletResponse r, final String wikiEncoding, final boolean useEncoding ) throws UnsupportedEncodingException {
            super( r );
            m_output = new ByteArrayOutputStream( INIT_BUFFER_SIZE );
            m_servletOut = new ByteArrayServletOutputStream( m_output );
            m_writer = new PrintWriter( new OutputStreamWriter( m_servletOut, wikiEncoding ), true );
            this.useEncoding = useEncoding;

            m_response = r;
        }

        /** Returns a writer for output; this wraps the internal buffer into a PrintWriter. */
        @Override
        public PrintWriter getWriter() {
            return m_writer;
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return m_servletOut;
        }

        @Override
        public void flushBuffer() throws IOException {
            m_writer.flush();
            super.flushBuffer();
        }

        class ByteArrayServletOutputStream extends ServletOutputStream {

            ByteArrayOutputStream m_buffer;

            public ByteArrayServletOutputStream( final ByteArrayOutputStream byteArrayOutputStream ) {
                super();
                m_buffer = byteArrayOutputStream;
            }

            /**{@inheritDoc} */
            @Override
            public void write( final int aInt ) {
                m_buffer.write( aInt );
            }

            /**{@inheritDoc} */
            @Override
			public boolean isReady() {
				return false;
			}

            /**{@inheritDoc} */
            @Override
			public void setWriteListener( final WriteListener writeListener ) {
			}			
        }

        /** Returns whatever was written so far into the Writer. */
        @Override
        public String toString() {
            try {
				flushBuffer();
			} catch( final IOException e ) {
                log.error( e );
                return "";
			}

            try {
				if( useEncoding ) {
					return m_output.toString( m_response.getCharacterEncoding() );
				}

				return m_output.toString();
			} catch( final UnsupportedEncodingException e ) {
                log.error( e );
                return "";
             }
        }

    }

}
