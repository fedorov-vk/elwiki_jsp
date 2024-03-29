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

import org.apache.log4j.Logger;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.TextUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.configuration.IWikiConfiguration;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Properties;


/**
 *  Provides a way to do short URLs of the form /wiki/PageName.
 *
 *  @since 2.2
 */
public class ShortURLConstructor extends DefaultURLConstructor {
    
    private static final String DEFAULT_PREFIX = "wiki/";
    private static final Logger log = Logger.getLogger( ShortURLConstructor.class );
    
    /** Contains the path part after the JSPWiki base URL */
    protected String m_urlPrefix = "";
    
    /**
     *  This corresponds to your WikiServlet path.  By default, it is assumed to be "wiki/", but you can set it to whatever you
     *  like - including an empty name.
     */
    public static final String PROP_PREFIX = "jspwiki.shortURLConstructor.prefix";

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

    /** {@inheritDoc} */
    public void initialize() throws WikiException {
        super.initialize();
        
        m_urlPrefix = wikiConfiguration.getStringProperty(PROP_PREFIX, null);
        
        if( m_urlPrefix == null ) {
            m_urlPrefix = DEFAULT_PREFIX;
        }

        log.info("Short URL prefix path="+m_urlPrefix+" (You can use "+PROP_PREFIX+" to override this)");
    }

    /**
     *  Constructs the actual URL based on the context.
     */
    private String makeURL( final String context, final String name ) {
        final String viewurl = "%p" + m_urlPrefix + "%n";

        if( context.equals( ContextEnum.PAGE_VIEW.getRequestContext() ) ) {
            if( name == null ) {
                return doReplacement("%u","" );
            }
            return doReplacement( viewurl, name );
        } else if( context.equals( ContextEnum.PAGE_PREVIEW.getRequestContext() ) ) {
            if( name == null ) {
                return doReplacement("%u","" );
            }
            return doReplacement( viewurl + "?do=Preview", name );
        } else if( context.equals( ContextEnum.PAGE_EDIT.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=Edit", name );
        } else if( context.equals( ContextEnum.ATTACHMENT_DOGET.getRequestContext() ) ) {
            return doReplacement( "%uattach/%n", name );
        } else if( context.equals( ContextEnum.PAGE_INFO.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=PageInfo", name );
        } else if( context.equals( ContextEnum.PAGE_DIFF.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=Diff", name );
        } else if( context.equals( ContextEnum.PAGE_NONE.getRequestContext() ) ) {
            return doReplacement( "%u%n", name );
        } else if( context.equals( ContextEnum.ATTACHMENT_UPLOAD.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=Upload", name ); 
        } else if( context.equals( ContextEnum.PAGE_COMMENT.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=Comment", name ); 
        } else if( context.equals( ContextEnum.WIKI_LOGIN.getRequestContext() ) ) {
            final String loginUrl = "%pLogin.jsp?redirect=%n";
            return doReplacement( loginUrl, name ); 
        } else if( context.equals( ContextEnum.PAGE_DELETE.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=Delete", name ); 
        } else if( context.equals( ContextEnum.PAGE_CONFLICT.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=PageModified", name ); 
        } else if( context.equals( ContextEnum.WIKI_PREFS.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=UserPreferences", name ); 
        } else if( context.equals( ContextEnum.WIKI_FIND.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=Search", name ); 
        } else if( context.equals( ContextEnum.WIKI_ERROR.getRequestContext() ) ) {
            return doReplacement( "%uError.jsp", name );
        } else if( context.equals( ContextEnum.GROUP_CREATE.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=NewGroup", name );
        } else if( context.equals( ContextEnum.GROUP_DELETE.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=DeleteGroup", name );
        } else if( context.equals( ContextEnum.GROUP_EDIT.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=EditGroup", name );
        } else if( context.equals( ContextEnum.GROUP_VIEW.getRequestContext() ) ) {
            return doReplacement( viewurl + "?do=Group&group=%n", name );
        }
        
        throw new InternalWikiException( "Requested unsupported context " + context );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String makeURL( final String context, final String name, String parameters ) {
        if( parameters != null && parameters.length() > 0 ) {
            if( context.equals( ContextEnum.ATTACHMENT_DOGET.getRequestContext() ) || context.equals( ContextEnum.PAGE_VIEW.getRequestContext() ) ) {
                parameters = "?" + parameters;
            } else if( context.equals(ContextEnum.PAGE_NONE.getRequestContext()) ) {
                parameters = (name.indexOf('?') != -1 ) ? "&amp;" : "?" + parameters;
            } else {
                parameters = "&amp;"+parameters;
            }
        } else {
            parameters = "";
        }
        return makeURL( context, name )+parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parsePage( final String context, final HttpServletRequest request, final Charset encoding ) {
        final String pagereq = request.getParameter( "page" );
        if(pagereq!=null) {
        	//:FVK: параметр page - устарел!!! и приводит к ошибке - вывод содержимого страницы по имени, а не по pageId.
        	System.out.println(":FVK: deprecated (2)");
        	log.error(":FVK: deprecated (2)");
        }
        if( pagereq == null ) {
            return URLConstructor.parsePageFromURL( request, encoding );
        }

        return pagereq;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getForwardPage( final HttpServletRequest req ) {
        String jspPage = req.getParameter( "do" );
        if( jspPage == null ) {
            jspPage = "Wiki";
        }
    
        return jspPage + ".jsp";
    }

}
