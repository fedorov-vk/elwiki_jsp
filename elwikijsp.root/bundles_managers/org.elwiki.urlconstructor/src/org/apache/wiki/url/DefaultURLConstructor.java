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
package org.apache.wiki.url;

import org.apache.commons.lang3.StringUtils;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.ui.CommandResolver;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.TextUtil;
import org.elwiki.configuration.IWikiConfiguration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.charset.Charset;


/**
 *  Implements the default URL constructor using links directly to the JSP pages.  This is what JSPWiki by default is using.  For example,
 *  WikiContext.VIEW points at "Wiki.jsp", etc.
 *
 *  @since 2.2
 */
@Component(name = "elwiki.DefaultUrlConstructor", service = URLConstructor.class, //
factory = "elwiki.UrlConstructor.factory")
public class DefaultURLConstructor implements URLConstructor {

    protected Engine m_engine;

    /** Contains the absolute path of the JSPWiki Web application without the actual servlet (which is the m_urlPrefix). */
    protected String m_pathPrefix = "";

	// -- service handling --------------------------< start --
    
	/** Stores configuration. */
	@Reference //(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	/**
	 * This component activate routine. Does all the real initialization.
	 * 
	 * @param componentContext passed the Engine.
	 */
	@Activate
	protected void startup(ComponentContext componentContext) {
		Object engine = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
		if (engine instanceof Engine) {
			initialize((Engine) engine);
		}
	}

	// -- service handling ---------------------------- end >--

	/**
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void initialize( final Engine engine ) {
		m_engine = engine;
		m_pathPrefix = this.wikiConfiguration.getBaseURL() + "/";
	}
    
    /**
     *  Does replacement of some particular variables.  The variables are:
     *
     *  <ul>
     *  <li> "%u" - inserts either the base URL (when absolute is required), or the base path (which is an absolute path without the host name).
     *  <li> "%U" - always inserts the base URL
     *  <li> "%p" - always inserts the base path
     *  <li> "%n" - inserts the page name
     *  </ul>
     *
     * @param baseptrn  The pattern to use
     * @param name The page name
     * @return A replacement.
     */
    protected final String doReplacement( String baseptrn, final String name ) {
    	final String baseurl = this.wikiConfiguration.getBaseURL();
    	final String pathPrefix = baseurl + "/";

        baseptrn = TextUtil.replaceString( baseptrn, "%u", pathPrefix );
        baseptrn = TextUtil.replaceString( baseptrn, "%U", baseurl );
        baseptrn = TextUtil.replaceString( baseptrn, "%n", encodeURI(name) );
        baseptrn = TextUtil.replaceString( baseptrn, "%p", m_pathPrefix );

        return baseptrn;
    }

    /**
     *  URLEncoder returns pluses, when we want to have the percent encoding.  See http://issues.apache.org/bugzilla/show_bug.cgi?id=39278
     *  for more info.
     *
     *  We also convert any %2F's back to slashes to make nicer-looking URLs.
     */
    private String encodeURI( String uri ) {
        try {
			uri = this.wikiConfiguration.encodeName(uri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        uri = StringUtils.replace( uri, "+", "%20" );
        uri = StringUtils.replace( uri, "%2F", "/" );

        return uri;
    }

    /**
     * Returns the URL pattern for a supplied wiki request context.
     * @param context the wiki context
     * @param name the wiki page
     * @return A pattern for replacement.
     * @throws IllegalArgumentException if the context cannot be found
     */
    public static String getURLPattern( final String context, final String name ) throws IllegalArgumentException {
        if( context.equals( ContextEnum.PAGE_VIEW.getRequestContext() ) && name == null) {
            // FIXME
            return "%uWiki.jsp";
        }

        // Find the action matching our pattern (could throw exception)
        final Command command = CommandResolver.findCommand( context );

        return command.getURLPattern();
    }

    /**
     *  Constructs the actual URL based on the context.
     */
    private String makeURL( final String context, final String name ) {
        return doReplacement( getURLPattern( context, name ), name );
    }

    /**
     *  Constructs the URL with a bunch of parameters.
     *  @param parameters If null or empty, no parameters are added.
     *
     *  {@inheritDoc}
     */
    @Override
    public String makeURL( final String context, final String name, String parameters ) {
        if( parameters != null && parameters.length() > 0 ) {
            if( context.equals( ContextEnum.PAGE_ATTACH.getRequestContext() ) ) {
                parameters = "?" + parameters;
            } else if( context.equals( ContextEnum.PAGE_NONE.getRequestContext() ) ) {
                parameters = name.indexOf( '?' ) != -1 ? "&amp;" : "?" + parameters;
            } else {
                parameters = "&amp;" + parameters;
            }
        } else {
            parameters = "";
        }
        return makeURL( context, name ) + parameters;
    }

    /**
     *  Should parse the "page" parameter from the actual request.
     *
     *  {@inheritDoc}
     */
    @Override
    public String parsePage( final String context, final HttpServletRequest request, final Charset encoding ) {
        String pagereq = request.getParameter( "page" );
        if( context.equals( ContextEnum.PAGE_ATTACH.getRequestContext() ) ) {
            pagereq = URLConstructor.parsePageFromURL( request, encoding );
        }

        return pagereq;
    }

	@Override
	public String parsePageId(HttpServletRequest request) {
		String pageId = request.getParameter("pageId");
		return pageId;
	}
    
    /**
     *  This method is not needed for the DefaultURLConstructor.
     *
     * @param request The HTTP Request that was used to end up in this page.
     * @return "Wiki.jsp", "PageInfo.jsp", etc.  Just return the name, JSPWiki will figure out the page.
     */
    @Override
    public String getForwardPage( final HttpServletRequest request ) {
        return "Wiki.jsp";
    }

}
