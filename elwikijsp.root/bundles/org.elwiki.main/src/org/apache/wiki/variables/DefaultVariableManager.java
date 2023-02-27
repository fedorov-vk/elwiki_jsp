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
package org.apache.wiki.variables;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.wiki.api.Release;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.NoSuchVariableException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.filters.PageFilter;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.api.modules.InternalModule;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Manages variables. Variables are case-insensitive. A list of all available
 * variables is on a Wiki page called "WikiVariables".
 *
 * @since 1.9.20.
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultVariableManager",
	service = { VariableManager.class, WikiManager.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultVariableManager implements VariableManager, WikiManager, EventHandler {

	private static final Logger log = Logger.getLogger(DefaultVariableManager.class);

	/**
	 * Contains a list of those properties that shall never be shown. Put names here in lower case.
	 */
	static final String[] THE_BIG_NO_NO_LIST = { "jspwiki.auth.masterpassword" };

	/**
	 * Creates a VariableManager object using the property list given.
	 * 
	 * @param props The properties.
	 */
	public DefaultVariableManager() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private PageManager pageManager;

	@WikiServiceReference
	private AttachmentManager attachmentManager;

	@WikiServiceReference
	private FilterManager filterManager;

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// doesn't used.
	}

	// -- OSGi service handling ------------------------(end)--

    /**
     *  {@inheritDoc}
     */
    @Override
    public String parseAndGetValue( final WikiContext context, final String link ) throws IllegalArgumentException, NoSuchVariableException {
        if( !link.startsWith( "{$" ) ) {
            throw new IllegalArgumentException( "Link does not start with {$" );
        }
        if( !link.endsWith( "}" ) ) {
            throw new IllegalArgumentException( "Link does not end with }" );
        }
        final String varName = link.substring( 2, link.length() - 1 );

        return getValue( context, varName.trim() );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    // FIXME: somewhat slow.
    public String expandVariables( final WikiContext context, final String source ) {
        final StringBuilder result = new StringBuilder();
        for( int i = 0; i < source.length(); i++ ) {
            if( source.charAt(i) == '{' ) {
                if( i < source.length()-2 && source.charAt(i+1) == '$' ) {
                    final int end = source.indexOf( '}', i );

                    if( end != -1 ) {
                        final String varname = source.substring( i+2, end );
                        String value;

                        try {
                            value = getValue( context, varname );
                        } catch( final NoSuchVariableException | IllegalArgumentException e ) {
                            value = e.getMessage();
                        }

                        result.append( value );
                        i = end;
                    }
                } else {
                    result.append( '{' );
                }
            } else {
                result.append( source.charAt(i) );
            }
        }

        return result.toString();
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getValue( final WikiContext context, final String varName, final String defValue ) {
        try {
            return getValue( context, varName );
        } catch( final NoSuchVariableException e ) {
            return defValue;
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getVariable( final WikiContext context, final String name ) {
        return getValue( context, name, null );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getValue( final WikiContext context, final String varName ) throws IllegalArgumentException, NoSuchVariableException {
        if( varName == null ) {
            throw new IllegalArgumentException( "Null variable name." );
        }
        if( varName.length() == 0 ) {
            throw new IllegalArgumentException( "Zero length variable name." );
        }
        // Faster than doing equalsIgnoreCase()
        final String name = varName.toLowerCase();

        for( final String value : THE_BIG_NO_NO_LIST ) {
            if( name.equals( value ) ) {
                return ""; // FIXME: Should this be something different?
            }
        }

        try {
            //
            //  Using reflection to get system variables adding a new system variable
            //  now only involves creating a new method in the SystemVariables class
            //  with a name starting with get and the first character of the name of
            //  the variable capitalized. Example:
            //    public String getMysysvar(){
            //      return "Hello World";
            //    }
            //
            final SystemVariables sysvars = new SystemVariables( context );
            final String methodName = "get" + Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 );
            final Method method = sysvars.getClass().getMethod( methodName );
            return ( String )method.invoke( sysvars );
        } catch( final NoSuchMethodException e1 ) {
            //
            //  It is not a system var. Time to handle the other cases.
            //
            //  Check if such a context variable exists, returning its string representation.
            //
            if( ( context.getVariable( varName ) ) != null ) {
                return context.getVariable( varName ).toString();
            }

            //
            //  Well, I guess it wasn't a final straw.  We also allow variables from the session and the request (in this order).
            //
            final HttpServletRequest req = context.getHttpRequest();
            if( req != null && req.getSession() != null ) {
                final HttpSession session = req.getSession();

                try {
                    String s = ( String )session.getAttribute( varName );

                    if( s != null ) {
                        return s;
                    }

                    s = context.getHttpParameter( varName );
                    if( s != null ) {
                        return s;
                    }
                } catch( final ClassCastException e ) {
                    log.debug( "Not a String: " + varName );
                }
            }

            //
            // And the final straw: see if the current page has named metadata.
            //
            final WikiPage pg = context.getPage();
            if( pg != null ) {
                final Object metadata = pg.getAttribute(varName);
                if( metadata != null ) {
                    return metadata.toString();
                }
            }

            //
            // And the final straw part 2: see if the "real" current page has named metadata. This allows
            // a parent page to control a inserted page through defining variables
            //
            final WikiPage rpg = context.getRealPage();
            if( rpg != null ) {
                final Object metadata = rpg.getAttribute(varName);
                if( metadata != null ) {
                    return metadata.toString();
                }
            }

            //
            // Next-to-final straw: attempt to fetch using property name. We don't allow fetching any other
            // properties than those starting with "jspwiki.".  I know my own code, but I can't vouch for bugs
            // in other people's code... :-)
            //
            if( varName.startsWith("jspwiki.") ) {
                final String s = wikiConfiguration.getWikiPreferences().getString(varName);
                if( s != null && s.length()>0) {
                    return s;
                }
            }

            //
            //  Final defaults for some known quantities.
            //
            if( varName.equals( VAR_ERROR ) || varName.equals( VAR_MSG ) ) {
                return "";
            }

            throw new NoSuchVariableException( "No variable " + varName + " defined." );
        } catch( final Exception e ) {
            log.info("Interesting exception: cannot fetch variable value", e );
        }
        return "";
    }

    /**
     *  This class provides the implementation for the different system variables.
     *  It is called via Reflection - any access to a variable called $xxx is mapped
     *  to getXxx() on this class.
     *  <p>
     *  This is a lot neater than using a huge if-else if branching structure
     *  that we used to have before.
     *  <p>
     *  Note that since we are case insensitive for variables, and VariableManager
     *  calls var.toLowerCase(), the getters for the variables do not have
     *  capitalization anywhere.  This may look a bit odd, but then again, this
     *  is not meant to be a public class.
     *
     *  @since 2.7.0
     */
    @SuppressWarnings( "unused" )
	private /*:FVK:static*/ class SystemVariables {

        private final WikiContext m_context;

        public SystemVariables( final WikiContext context )
        {
            m_context=context;
        }

        public String getPageid()
        {
            return m_context.getPage().getId();
        }

        public String getPagename()
        {
            return m_context.getPage().getName();
        }

        public String getApplicationname()
        {
            return wikiConfiguration.getApplicationName();
        }

        public String getElwikiversion()
        {
            return Release.getVersionString();
        }

        public String getEncoding() {
            return m_context.getConfiguration().getContentEncodingCs().displayName();
        }

        public String getTotalpages() {
            return Integer.toString( pageManager.getTotalPageCount() );
        }

        public String getPageprovider() {
            return pageManager.getCurrentProvider();
        }

        public String getPageproviderdescription() {
            return pageManager.getProviderDescription();
        }

        public String getAttachmentprovider() {
            final WikiProvider p = attachmentManager.getCurrentProvider();
            return (p != null) ? p.getClass().getName() : "-";
        }

        public String getAttachmentproviderdescription() {
            final WikiProvider p = attachmentManager.getCurrentProvider();
            return (p != null) ? p.getProviderInfo() : "-";
        }

        public String getInterwikilinks() {
            final StringBuilder res = new StringBuilder();

            for( final String link : wikiConfiguration.getAllInterWikiLinks() ) {
                if( res.length() > 0 ) {
                    res.append( ", " );
                }
                res.append( link );
                res.append( " --> " );
                res.append( wikiConfiguration.getInterWikiURL( link ) );
            }
            return res.toString();
        }

        public String getInlinedimages() {
            final StringBuilder res = new StringBuilder();
            for( final String ptrn : wikiConfiguration.getAllInlinedImagePatterns() ) {
                if( res.length() > 0 ) {
                    res.append( ", " );
                }

                res.append( ptrn );
            }

            return res.toString();
        }

        public String getPluginpath() {
            final String s = m_context.getEngine().getPluginSearchPath();

            return ( s == null ) ? "-" : s;
        }

        public String getBaseurl()
        {
            return m_context.getConfiguration().getBaseURL();
        }

        public String getUptime() {
            final Date now = new Date();
            long secondsRunning = ( now.getTime() - m_context.getEngine().getStartTime().getTime() ) / 1_000L;

            final long seconds = secondsRunning % 60;
            final long minutes = (secondsRunning /= 60) % 60;
            final long hours = (secondsRunning /= 60) % 24;
            final long days = secondsRunning /= 24;

            return days + "d, " + hours + "h " + minutes + "m " + seconds + "s";
        }

        public String getLoginstatus() {
            final Session session = m_context.getWikiSession();
            ResourceBundle rcBundle = Preferences.getBundle( m_context, InternationalizationManager.CORE_BUNDLE );
            return rcBundle.getString( "varmgr." + session.getLoginStatus().getId());
        }

        public String getUsername() {
            final Principal wup = m_context.getCurrentUser();
            final ResourceBundle rcBundle = Preferences.getBundle( m_context, InternationalizationManager.CORE_BUNDLE );
            return wup != null ? wup.getName() : rcBundle.getString( "varmgr.not.logged.in" );
        }

        public String getRequestcontext()
        {
            return m_context.getRequestContext();
        }

        public String getPagefilters() {
            final List< PageFilter > filters = filterManager.getFilterList();
            final StringBuilder sb = new StringBuilder();
            for( final PageFilter pf : filters ) {
                final String f = pf.getClass().getName();
                if( pf instanceof InternalModule ) {
                    continue;
                }

                if( sb.length() > 0 ) {
                    sb.append( ", " );
                }
                sb.append( f );
            }
            return sb.toString();
        }
    }

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		/*switch (topic) {
		}*/
	}

}
