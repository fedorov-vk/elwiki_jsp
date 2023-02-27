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
package org.apache.wiki.filters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiPageEvent;
import org.apache.wiki.api.exceptions.FilterException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.filters.ISpamFilter;
import org.apache.wiki.api.filters.PageFilter;
import org.apache.wiki.api.modules.BaseModuleManager;
import org.apache.wiki.api.modules.WikiModuleInfo;
import org.apache.wiki.filters.internal.FiltersActivator;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.util.ClassUtil;
import org.apache.wiki.util.PriorityList;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.util.XmlUtil;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.jdom2.Element;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


/**
 *  Manages the page filters.  Page filters are components that can be executed at certain places:
 *  <ul>
 *    <li>Before the page is translated into HTML.
 *    <li>After the page has been translated into HTML.
 *    <li>Before the page is saved.
 *    <li>After the page has been saved.
 *  </ul>
 *
 *  Using page filters allows you to modify the page data on-the-fly, and do things like adding your own custom WikiMarkup.
 *
 *  <p>
 *  The initial page filter configuration is kept in a file called "filters.xml".  The format is really very simple:
 *  <pre>
 *  <?xml version="1.0"?>
 *  &lt;pagefilters>
 *
 *    &lt;filter>
 *      &lt;class>org.apache.wiki.filters.ProfanityFilter&lt;/class>
 *    &lt;filter>
 *
 *    &lt;filter>
 *      &lt;class>org.apache.wiki.filters.TestFilter&lt;/class>
 *
 *      &lt;param>
 *        &lt;name>foobar&lt;/name>
 *        &lt;value>Zippadippadai&lt;/value>
 *      &lt;/param>
 *
 *      &lt;param>
 *        &lt;name>blatblaa&lt;/name>
 *        &lt;value>5&lt;/value>
 *      &lt;/param>
 *
 *    &lt;/filter>
 *  &lt;/pagefilters>
 *  </pre>
 *
 *  The &lt;filter> -sections define the filters.  For more information, please see the PageFilterConfiguration page in the JSPWiki distribution.
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultFilterManager",
	service = { FilterManager.class, WikiManager.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultFilterManager extends BaseModuleManager implements FilterManager, WikiManager, EventHandler {

    private PriorityList< PageFilter > m_pageFilters = new PriorityList<>();

    private Map< String, PageFilterInfo > m_filterClassMap = new HashMap<>();

    private static final Logger log = Logger.getLogger(DefaultFilterManager.class);

    /**
     *  Constructs a new FilterManager object.
     *
     *  @param engine The Engine which owns the FilterManager
     *  @param props Properties to initialize the FilterManager with
     *  @throws WikiException If something goes wrong.
     */
    /*:FVK:
    public DefaultFilterManager( final Engine engine, final Properties props ) throws WikiException {
        super( engine );
        initialize( props );
    }
    */

	// -- OSGi service handling ----------------------(start)--

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;
    
    @WikiServiceReference
    private Engine m_engine;

    /**
     * {@inheritDoc}
     * <p>
     * Initializes the filters from an XML file.
     *
     *  @throws WikiException If something goes wrong.
     */
	public void initialize() throws WikiException {
        InputStream xmlStream = null;
		final String xmlFile = TextUtil.getStringProperty(wikiConfiguration.getWikiPreferences(), PROP_FILTERXML, null);

        try {
            registerFilters();

            if( m_engine.getServletContext() != null ) {
            	//TODO: этот код устарел (в ElWiki инициализация относительно OSGi, а не сервлета. ServletContext==null).
                log.debug( "Attempting to locate " + DEFAULT_XMLFILE + " from servlet context." );
                if( xmlFile == null ) {
                    xmlStream = m_engine.getServletContext().getResourceAsStream( DEFAULT_XMLFILE );
                } else {
                    xmlStream = m_engine.getServletContext().getResourceAsStream( xmlFile );
                }
            }

            if( xmlStream == null ) {
                // just a fallback element to the old behaviour prior to 2.5.8
                log.debug( "Attempting to locate filters.xml from class path." );

                if( xmlFile == null ) {
                    xmlStream = getClass().getResourceAsStream( "/filters.xml" );
                } else {
                    xmlStream = getClass().getResourceAsStream( xmlFile );
                }
            }

            if( (xmlStream == null) && (xmlFile != null) ) {
                log.debug("Attempting to load property file "+xmlFile);
                xmlStream = new FileInputStream( new File(xmlFile) );
            }

            if( xmlStream == null ) {
                log.info( "Cannot find property file for filters (this is okay, expected to find it as: '" + DEFAULT_XMLFILE + "')" );
                return;
            }

            parseConfigFile( xmlStream );
        } catch( final IOException e ) {
            log.error("Unable to read property file", e);
        } finally {
            try {
                if( xmlStream != null ) {
                    xmlStream.close();
                }
            } catch( final IOException ioe ) {
                // ignore
            }
        }
    }
    
	// -- OSGi service handling ------------------------(end)--
    
    /**
     *  Adds a page filter to the queue.  The priority defines in which order the page filters are run, the highest priority filters go
     *  in the queue first.
     *  <p>
     *  In case two filters have the same priority, their execution order is the insertion order.
     *
     *  @since 2.1.44.
     *  @param f PageFilter to add
     *  @param priority The priority in which position to add it in.
     *  @throws IllegalArgumentException If the PageFilter is null or invalid.
     */
    @Override
    public void addPageFilter( final PageFilter f, final int priority ) throws IllegalArgumentException {
        if( f == null ) {
            throw new IllegalArgumentException("Attempt to provide a null filter - this should never happen.  Please check your configuration (or if you're a developer, check your own code.)");
        }

        m_pageFilters.add( f, priority );
    }

    private void initPageFilter( final String className, final Properties props ) {
        try {
            final PageFilterInfo info = m_filterClassMap.get( className );
            if( info != null && !checkCompatibility( info ) ) {
                log.warn( "Filter '" + info.getName() + "' not compatible with this version of JSPWiki" );
                return;
            }

            final int priority = 0; // FIXME: Currently fixed.
            final Class< ? > cl = ClassUtil.findClass( "org.apache.wiki.filters", className );
            final PageFilter filter = (PageFilter)cl.newInstance();
			filter.initialize( m_engine );

            addPageFilter( filter, priority );
            log.info("Added page filter "+cl.getName()+" with priority "+priority);
        } catch( final ClassNotFoundException e ) {
            log.error("Unable to find the filter class: "+className);
        } catch( final InstantiationException e ) {
            log.error("Cannot create filter class: "+className);
        } catch( final IllegalAccessException e ) {
            log.error("You are not allowed to access class: "+className);
        } catch( final ClassCastException e ) {
            log.error("Suggested class is not a PageFilter: "+className);
        } catch( final WikiException e ) {
            log.error("Filter "+className+" failed to initialize itself.", e);
        }
    }

    /**
     *  Parses the XML filters configuration file.
     *
     * @param xmlStream stream to parse
     */
    private void parseConfigFile( final InputStream xmlStream ) {
    	final List< Element > pageFilters = XmlUtil.parse( xmlStream, "/pagefilters/filter" );
        for( final Element f : pageFilters ) {
            final String filterClass = f.getChildText( "class" );
            final Properties props = new Properties();
            final List<Element> params = f.getChildren( "param" );
            for( final Element p : params ) {
                props.setProperty( p.getChildText( "name" ), p.getChildText( "value" ) );
            }

            initPageFilter( filterClass, props );
        }
    }


    /**
     *  Does the filtering before a translation.
     *
     *  @param context The WikiContext
     *  @param pageData WikiMarkup data to be passed through the preTranslate chain.
     *  @throws FilterException If any of the filters throws a FilterException
     *  @return The modified WikiMarkup
     *
     *  @see PageFilter#preTranslate(WikiContext, String)
     */
    @Override
    public String doPreTranslateFiltering( final WikiContext context, String pageData ) throws FilterException {
        fireEvent( WikiPageEvent.PRE_TRANSLATE_BEGIN, context );
        for( final PageFilter f : m_pageFilters ) {
            pageData = f.preTranslate( context, pageData );
        }

        fireEvent( WikiPageEvent.PRE_TRANSLATE_END, context );

        return pageData;
    }

    /**
     *  Does the filtering after HTML translation.
     *
     *  @param context The WikiContext
     *  @param htmlData HTML data to be passed through the postTranslate
     *  @throws FilterException If any of the filters throws a FilterException
     *  @return The modified HTML
     *  @see PageFilter#postTranslate(WikiContext, String)
     */
    @Override
    public String doPostTranslateFiltering( final WikiContext context, String htmlData ) throws FilterException {
        fireEvent( WikiPageEvent.POST_TRANSLATE_BEGIN, context );
        for( final PageFilter f : m_pageFilters ) {
            htmlData = f.postTranslate( context, htmlData );
        }

        fireEvent( WikiPageEvent.POST_TRANSLATE_END, context );

        return htmlData;
    }

    /**
     *  Does the filtering before a save to the page repository.
     *
     *  @param context The WikiContext
     *  @param pageData WikiMarkup data to be passed through the preSave chain.
     *  @throws FilterException If any of the filters throws a FilterException
     *  @return The modified WikiMarkup
     *  @see PageFilter#preSave(WikiContext, String)
     */
    @Override
    public String doPreSaveFiltering( final WikiContext context, String pageData ) throws FilterException {
        fireEvent( WikiPageEvent.PRE_SAVE_BEGIN, context );
        for( final PageFilter f : m_pageFilters ) {
            pageData = f.preSave( context, pageData );
        }

        fireEvent( WikiPageEvent.PRE_SAVE_END, context );

        return pageData;
    }

    /**
     *  Does the page filtering after the page has been saved.
     *
     *  @param context The WikiContext
     *  @param pageData WikiMarkup data to be passed through the postSave chain.
     *  @see PageFilter#postSave(WikiContext, String)
     */
    @Override
    public void doPostSaveFiltering( final WikiContext context, final String pageData ) throws WikiException {
        fireEvent( WikiPageEvent.POST_SAVE_BEGIN, context );
        for( final PageFilter f : m_pageFilters ) {
            // log.info("POSTSAVE: "+f.toString() );
            f.postSave( context, pageData );
        }

        fireEvent( WikiPageEvent.POST_SAVE_END, context );
    }

    /**
     *  Returns the list of filters currently installed.  Note that this is not
     *  a copy, but the actual list.  So be careful with it.
     *
     *  @return A List of PageFilter objects
     */
    @Override
    public List< PageFilter > getFilterList()
    {
        return m_pageFilters;
    }

	@Override
	public ISpamFilter getSpamFilter() {
		PageFilter spamFilter = m_pageFilters.stream().filter(filter -> filter instanceof ISpamFilter).findFirst()
				.orElse(null);

		return (ISpamFilter) spamFilter;
	}

    /**
     *
     * Notifies PageFilters to clean up their ressources.
     *
     */
    @Override
    public void destroy() {
        for( final PageFilter f : m_pageFilters ) {
            f.destroy( m_engine );
        }
    }

    // events processing .......................................................

    /**
     *  Fires a WikiPageEvent of the provided type and WikiContext. Invalid WikiPageEvent types are ignored.
     *
     * @see org.apache.wiki.api.event.WikiPageEvent
     * @param type      the WikiPageEvent type to be fired.
     * @param context   the WikiContext of the event.
     */
    public void fireEvent( final int type, final WikiContext context ) {
        if( WikiEventManager.isListening(this ) && WikiPageEvent.isValidType( type ) )  {
            WikiEventManager.fireEvent(this, new WikiPageEvent( m_engine, type, context.getPage().getName() ) );
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Collection< WikiModuleInfo > modules() {
        return modules( m_filterClassMap.values().iterator() );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public PageFilterInfo getModuleInfo( final String moduleName ) {
        return m_filterClassMap.get(moduleName);
    }

    private void registerFilters() {
    	{//:FVK: workaround. (due to SpamFilter now has interface...)
    		addPageFilter(new SpamFilter(), -999);
    	}
    	
        log.info( "Registering filters" );
        final List< Element > filters = XmlUtil.parse(FiltersActivator.getContext().getBundle(), PLUGIN_RESOURCE_LOCATION, "/modules/filter" );

        //
        // Register all filters which have created a resource containing its properties.
        //
        // Get all resources of all plugins.
        //
        for( final Element pluginEl : filters ) {
            final String className = pluginEl.getAttributeValue( "class" );
            final PageFilterInfo filterInfo = PageFilterInfo.newInstance( className, pluginEl );
            if( filterInfo != null ) {
                registerFilter( filterInfo );
            }
        }
    }

    private void registerFilter( final PageFilterInfo pluginInfo ) {
        m_filterClassMap.put( pluginInfo.getName(), pluginInfo );
    }

    /**
     *  Stores information about the filters.
     *
     *  @since 2.6.1
     */
    private static final class PageFilterInfo extends WikiModuleInfo {
        private PageFilterInfo( final String name ) {
            super( name );
        }

        protected static PageFilterInfo newInstance( final String className, final Element pluginEl ) {
            if( className == null || className.length() == 0 ) {
                return null;
            }
            final PageFilterInfo info = new PageFilterInfo( className );

            info.initializeFromXML( pluginEl );
            return info;
        }
    }

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		/*switch (topic) {
			break;
		}*/		
	}

}
