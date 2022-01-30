package org.elwiki.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpRequest;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.ajax.WikiAjaxDispatcherServlet;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.util.TextUtil;
import org.eclipse.core.runtime.Platform;
import org.elwiki.configuration.IWikiPreferences;
import org.elwiki.internal.CmdCode;
import org.elwiki.services.ServicesRefs;

public class PageFilter extends HttpFilter {

	private static final Logger log = Logger.getLogger(PageFilter.class);

	private Engine engine;
	private ServletContext context;
    private String m_wiki_encoding;
    private boolean useEncoding;

	@Override
	public void init(FilterConfig config) throws ServletException {
		final ServletContext context = config.getServletContext();
		engine = Wiki.engine().find(context);
		this.context = context;
		
        m_wiki_encoding = engine.getWikiPreferences().getString( IWikiPreferences.PROP_ENCODING );

        useEncoding = !TextUtil.getBooleanProperty(engine.getWikiPreferences(), Engine.PROP_NO_FILTER_ENCODING, false);
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//:FVK: из кода JSPwiki, класс WikiJSPFilter: ... : final WatchDog w = WatchDog.getCurrentWatchDog( m_engine );
		
		log.debug("doFilter()");
		String uri = httpRequest.getRequestURI();
		if( false == uri.matches(".+?(\\.cmd|\\.jsp)$") ) { //:FVK: uri.matches(".+?(\\.js|\\.css)$")
			System.err.println("PF original resource: " + uri);
			super.doFilter(httpRequest, response, chain);
		}
		System.out.println("PageFilter, URI: " + uri);

        final HttpServletResponseWrapper responseWrapper = new JSPWikiServletResponseWrapper(response, m_wiki_encoding, useEncoding );
		
		CmdCode cmdCode = null;
		try {
			// final HttpServletResponseWrapper responseWrapper = new MyWikiServletResponseWrapper( (
			// HttpServletResponse )response, "UTF-8", true );
			{
				HttpSession session = httpRequest.getSession();

				log.debug("HttpSession ID: " + session.getId());
				log.debug("    RequestURI: " + httpRequest.getRequestURI());
			}

			{// Code for context`s command: get it, execute.
				Command cmd = ServicesRefs.getCurrentContext().getCommand();
				cmdCode = Platform.getAdapterManager().getAdapter(cmd, CmdCode.class);
				if (cmdCode != null) {
					try {
						cmdCode.applyPrologue(httpRequest,responseWrapper);
					} catch (Exception e) {
						// TODO: Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

//		chain.doFilter(request, response);
			String path;
			RequestDispatcher requestDispatcher;
			path = "/page/PageHead.cmd";
			requestDispatcher = httpRequest.getRequestDispatcher(path);
			requestDispatcher.include(httpRequest, responseWrapper);

			path = "/page/PageMiddle.cmd";
			requestDispatcher = httpRequest.getRequestDispatcher(path);
			requestDispatcher.include(httpRequest, responseWrapper);

			path = "/page/PageBottom.cmd";
			requestDispatcher = httpRequest.getRequestDispatcher(path);
			requestDispatcher.include(httpRequest, responseWrapper);
			
			try {
                //:FVK: w.enterState( "Delivering response", 30 );
                final Context wikiContext = ServicesRefs.getCurrentContext();
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

                // fire PAGE_DELIVERED event
                //:FVK: fireEvent( WikiPageEvent.PAGE_DELIVERED, pagename );

            } finally {
                //:FVK: w.exitState();
            }
		} finally {
			if (cmdCode != null) {
				try {
					cmdCode.applyEpilogue();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			ServicesRefs.removeCurrentContext();
		}
	}

    /**
     * Goes through all types and writes the appropriate response.
     *
     * @param wikiContext The usual processing context
     * @param response The source string
     * @return The modified string with all the insertions in place.
     */
    private String filter( final Context wikiContext, final HttpServletResponse response ) {
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
    private String insertResources( final Context wikiContext, final String string, final String type ) {
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
    
	private static class MyWikiServletResponseWrapper extends HttpServletResponseWrapper {

		ByteArrayOutputStream m_output;
		private ByteArrayServletOutputStream m_servletOut;
		private PrintWriter m_writer;
		private HttpServletResponse m_response;
		private boolean useEncoding;

		/**
		 * How large the initial buffer should be. This should be tuned to achieve a balance in speed
		 * and memory consumption.
		 */
		private static final int INIT_BUFFER_SIZE = 0x8000;

		public MyWikiServletResponseWrapper(final HttpServletResponse r, final String wikiEncoding,
				final boolean useEncoding) throws UnsupportedEncodingException {
			super(r);
			m_output = new ByteArrayOutputStream(INIT_BUFFER_SIZE);
			m_servletOut = new ByteArrayServletOutputStream(m_output);
			m_writer = new PrintWriter(new OutputStreamWriter(m_servletOut, wikiEncoding), true);
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

			public ByteArrayServletOutputStream(final ByteArrayOutputStream byteArrayOutputStream) {
				super();
				m_buffer = byteArrayOutputStream;
			}

			//
			/** {@inheritDoc} */
			@Override
			public void write(final int aInt) {
				m_buffer.write(aInt);
			}

			/*:FVK: - от servlet 3.0
			/ **{@inheritDoc} * /
			@Override
			public boolean isReady() {
				return false;
			}
			
			/ **{@inheritDoc} * /
			@Override
			public void setWriteListener( final WriteListener writeListener ) {
			}*/

		}

		/** Returns whatever was written so far into the Writer. */
		@Override
		public String toString() {
			try {
				flushBuffer();
			} catch (final IOException e) {
				// log.error( e );
				return "";
			}

			try {
				if (useEncoding) {
					return m_output.toString(m_response.getCharacterEncoding());
				}

				return m_output.toString();
			} catch (final UnsupportedEncodingException e) {
				// log.error( e );
				return "";
			}
		}

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

            //
            /**{@inheritDoc} */
            @Override
            public void write( final int aInt ) {
                m_buffer.write( aInt );
            }

            /*:FVK: - от servlet 3.0
            / **{@inheritDoc} * /
            @Override
			public boolean isReady() {
				return false;
			}

            / **{@inheritDoc} * /
            @Override
			public void setWriteListener( final WriteListener writeListener ) {
			}*/
			
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
