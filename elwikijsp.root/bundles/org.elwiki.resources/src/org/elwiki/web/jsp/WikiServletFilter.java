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

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.wiki.Wiki;
import org.apache.wiki.WikiContextImpl;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.auth.AuthenticationManager;
import org.apache.wiki.auth.SessionMonitor;
import org.apache.wiki.auth.WikiSecurityException;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.resources.ResourcesActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Filter that verifies that the {@link org.apache.wiki.api.core.Engine} is running, and sets the authentication status for the user's
 * Session. Each HTTP request processed by this filter is wrapped by a {@link WikiRequestWrapper}. The wrapper's primary responsibility
 * is to return the correct <code>userPrincipal</code> and <code>remoteUser</code> for authenticated JSPWiki users (whether authenticated
 * by container or by JSPWiki's custom system). The wrapper's other responsibility is to incorporate JSPWiki built-in roles
 * into the role-checking algorithm for {@link  HttpServletRequest#isUserInRole(String)}. Just before the request is wrapped, the method
 * {@link org.apache.wiki.auth.AuthenticationManager#login(HttpServletRequest)} executes; this method contains all of the logic needed to
 * grab any user login credentials set by the container or by cookies.
 */
//@formatter:off
@Component(
	property = {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN + "=/attach/*",
		HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=("
		+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=eclipse)",
		Constants.SERVICE_RANKING + ":Integer=1000"},
  scope=ServiceScope.PROTOTYPE,
  name = "part10.WikiServletFilter"
)
//@formatter:on
public class WikiServletFilter implements Filter {

    private static final Logger log = Logger.getLogger( WikiServletFilter.class );

    @Reference
    private Engine m_engine;

    @Reference
    private GlobalPreferences globalPreferences;

    @Reference
    private IWikiConfiguration wikiConfiguration;

	@Activate
	protected void startup() {
		log.debug("«web» start " + WikiServletFilter.class.getSimpleName());
	}
    
    public IWikiConfiguration getWikiConfiguration() {
		return wikiConfiguration;
	}

    public GlobalPreferences getGlobalPreferences() {
		return globalPreferences;
	}

	/**
     * Initializes the WikiServletFilter.
     * 
     * @param config The FilterConfig.
     * @throws ServletException If a Engine cannot be started.
     */
    @Override
    public void init( final FilterConfig config ) throws ServletException {
        final ServletContext context = config.getServletContext();

        // TODO REMOVEME when resolving JSPWIKI-129
        if( System.getSecurityManager() != null ) {
            context.log( "== JSPWIKI WARNING ==  : This container is running with a security manager. JSPWiki does not yet really support that right now. See issue JSPWIKI-129 for details and information on how to proceed." );
        }

		{// TODO: это - workaround. :FVK: (хотя, BaseURL - настроен в конфигурации. И можно ли получить его другим путем?)
			ServletContext servletContext = config.getServletContext();
			m_engine.getWikiConfiguration().setBaseURL(servletContext.getContextPath());
		}
    }

    /**
     * Destroys the WikiServletFilter.
     */
    @Override
    public void destroy() {
    }

    /**
    * Checks that the Engine is running ok, wraps the current HTTP request, and sets the correct authentication state for the users's
    * Session. First, the method {@link org.apache.wiki.auth.AuthenticationManager#login(HttpServletRequest)}
    * executes, which sets the authentication state. Then, the request is wrapped with a
    * {@link WikiRequestWrapper}.
    * @param request the current HTTP request object
    * @param response the current HTTP response object
    * @param chain The Filter chain passed down.
    * @throws ServletException if {@link org.apache.wiki.auth.AuthenticationManager#login(HttpServletRequest)} fails for any reason
    * @throws IOException If writing to the servlet response fails. 
    */
    @Override
    public void doFilter( final ServletRequest request, final ServletResponse response, final FilterChain chain ) throws IOException, ServletException {
    	String uri = "«unknown URL»";
    	if(request instanceof HttpServletRequest httpServletRequest) {
    		uri = httpServletRequest.getRequestURI();
    	}
		log.debug("◄►doFilter◄► " + uri);    	

        //  Sanity check; it might be true in some conditions, but we need to know where.
        if( chain == null ) {
            throw new ServletException("FilterChain is null, even if it should not be.  Please report this to the jspwiki development team.");
        }

        if( m_engine == null ) {
            final PrintWriter out = response.getWriter();
            out.print("<!DOCTYPE html><html lang=\"en\"><head><title>Fatal problem with JSPWiki</title></head>");
            out.print("<body>");
            out.print("<h1>JSPWiki has not been started</h1>");
            out.print("<p>JSPWiki is not running.  This is probably due to a configuration error in your preferences.ini file, ");
            out.print("or a problem with your servlet container.  Please double-check everything before issuing a bug report ");
            out.print("at jspwiki.apache.org.</p>");
            out.print("<p>We apologize for the inconvenience.  No, really, we do.  We're trying to ");
            out.print("JSPWiki as easy as we can, but there is only so much we have time to test ");
            out.print("platforms.</p>");
            out.print( "<p>Please go to the <a href='Install.jsp'>installer</a> to continue.</p>" );
            out.print("</body></html>");
            return;
        }   
        chain.doFilter(request, response);

        /*:FVK:		
        // If we haven't done so, wrap the request
        HttpServletRequest httpRequest = ( HttpServletRequest )request;
        
        // Set the character encoding
        httpRequest.setCharacterEncoding( engine.getContentEncoding().displayName() );

        if ( !isWrapped( request ) ) {
            // Prepare the Session
            try {
            	WikiEngine.getAuthenticationManager().login( httpRequest );
                final Session wikiSession = WikiEngine.getSessionMonitor().getWikiSession(httpRequest);
                httpRequest = new WikiRequestWrapper( m_engine, httpRequest );
                if ( log.isDebugEnabled() ) {
                    log.debug( "Executed security filters for user=" + wikiSession.getLoginPrincipal().getName() + ", path=" + httpRequest.getRequestURI() );
                }
            } catch( final WikiSecurityException e ) {
                throw new ServletException( e );
            }
        }

        try {
            NDC.push( getGlobalPreferences().getApplicationName() + ":" + httpRequest.getRequestURL() );
            chain.doFilter( httpRequest, response );
        } finally {
            NDC.pop();
            NDC.remove();
        }
        */
    }

    /**
     *  Figures out the wiki context from the request.  This method does not create the context if it does not exist.
     *  
     *  @param request The request to examine
     *  @return A valid WikiContext value (or null, if the context could not be located).
     */
    protected WikiContextImpl getWikiContext( final ServletRequest request ) {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        return ( WikiContextImpl )httpRequest.getAttribute( WikiContext.ATTR_WIKI_CONTEXT );
    }

    /** 
     * Determines whether the request has been previously wrapped with a WikiRequestWrapper. 
     * We find the wrapper by recursively unwrapping successive request wrappers, if they have been supplied.
     *
     * @param request the current HTTP request
     * @return <code>true</code> if the request has previously been wrapped; <code>false</code> otherwise
     */
    private boolean isWrapped( ServletRequest request ) {
        while( !(request instanceof WikiRequestWrapper )
        		&& request instanceof HttpServletRequestWrapper httpRequestWrapper) {
            request = httpRequestWrapper.getRequest();
        }
        return request instanceof WikiRequestWrapper;
    }

}
