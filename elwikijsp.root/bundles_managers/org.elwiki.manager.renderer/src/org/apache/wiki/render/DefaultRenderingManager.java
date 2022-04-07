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
package org.apache.wiki.render;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.wiki.StringTransmutator;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.elwiki_data.PageAttachment;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.wiki.api.event.WikiEvent;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiPageEvent;
import org.apache.wiki.api.exceptions.FilterException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.providers.PageProvider;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.parser.JSPWikiMarkupParser;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.parser0.WikiDocument;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.render0.WikiRenderer;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.util.ClassUtil;
import org.apache.wiki.util.TextUtil;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.configuration.ScopedPreferenceStore;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Properties;


/**
 *  This class provides a facade towards the differing rendering routines.  You should use the routines in this manager
 *  instead of the ones in Engine, if you don't want the different side effects to occur - such as WikiFilters.
 *  <p>
 *  This class also manages a rendering cache, i.e. documents are stored between calls. You may control the cache by
 *  tweaking the ehcache.xml file.
 *  <p>
 *
 *  @since  2.4
 */
@Component(name = "elwiki.DefaultRenderingManager", service = RenderingManager.class, //
		factory = "elwiki.RenderingManager.factory")
public class DefaultRenderingManager implements RenderingManager {

    private static final Logger log = Logger.getLogger( DefaultRenderingManager.class );

    /** The capacity of the caches, if you want something else, tweak ehcache.xml. */
    private static final int    DEFAULT_CACHESIZE     = 1_000;
    private static final String VERSION_DELIMITER     = "::";

    /** The name of the default renderer. */
    private static final String DEFAULT_PARSER = JSPWikiMarkupParser.class.getName();
    /** The name of the default renderer. */
    private static final String DEFAULT_RENDERER = XHTMLRenderer.class.getName();
    /** The name of the default WYSIWYG renderer. */
    private static final String DEFAULT_WYSIWYG_RENDERER = WysiwygEditingRenderer.class.getName();

    private Engine m_engine;

    private boolean m_useCache = true;
    private final CacheManager m_cacheManager = CacheManager.getInstance();
    private final int m_cacheExpiryPeriod = 24*60*60; // This can be relatively long

    /** If true, all titles will be cleaned. */
    private boolean m_beautifyTitle = false;

    /** Stores the WikiDocuments that have been cached. */
    private Cache m_documentCache;

    private Constructor< ? > m_rendererConstructor;
    private Constructor< ? > m_rendererWysiwygConstructor;
    private String m_markupParserClass = DEFAULT_PARSER;

	// -- service handling ---------------------------{start}--

    @WikiServiceReference
	private AttachmentManager attachmentManager;

    @WikiServiceReference
	private FilterManager filterManager;

    @WikiServiceReference
	private PageManager pageManager;
	
    @WikiServiceReference
	private ReferenceManager referenceManager;

    @WikiServiceReference
	private VariableManager variableManager;

	/**
	 * This component activate routine. Does all the real initialization.
	 *
	 * @param componentContext
	 */
	@Activate
	protected void startup(ComponentContext componentContext) {
		try {
			Object engine = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
			if (engine instanceof Engine) {
				initialize((Engine) engine);
			}
		} catch (WikiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	// -- service handling -----------------------------{end}--

    /**
     *  {@inheritDoc}
     *
     *  Checks for cache size settings, initializes the document cache. Looks for alternative WikiRenderers, initializes one, or the
     *  default XHTMLRenderer, for use.
     */
    @Override
    public void initialize( final Engine engine ) throws WikiException {
        m_engine = engine;
        IPreferenceStore properties = engine.getWikiPreferences();
        m_markupParserClass = TextUtil.getStringProperty(properties, PROP_PARSER, DEFAULT_PARSER );
        /*:FVK:
        if( !ClassUtil.assignable( m_markupParserClass, MarkupParser.class.getName() ) ) {
        	log.warn( m_markupParserClass + " does not subclass " + MarkupParser.class.getName() + " reverting to default markup parser." );
        	m_markupParserClass = DEFAULT_PARSER;
        }*/
        log.info( "Using " + m_markupParserClass + " as markup parser." );

        m_beautifyTitle  = TextUtil.getBooleanProperty( properties, PROP_BEAUTIFYTITLE, m_beautifyTitle );
        m_useCache = TextUtil.getBooleanProperty(properties, PageManager.PROP_USECACHE, true );
        if( m_useCache ) {
            final String documentCacheName = engine.getWikiConfiguration().getApplicationName() + "." + DOCUMENTCACHE_NAME;
            if (m_cacheManager.cacheExists(documentCacheName)) {
                m_documentCache = m_cacheManager.getCache(documentCacheName);
            } else {
                log.info( "cache with name " + documentCacheName + " not found in ehcache.xml, creating it with defaults." );
                m_documentCache = new Cache( documentCacheName, DEFAULT_CACHESIZE, false, false, m_cacheExpiryPeriod, m_cacheExpiryPeriod );
                m_cacheManager.addCache( m_documentCache );
            }
        }

        final String renderImplName = TextUtil.getStringProperty(properties, PROP_RENDERER, DEFAULT_RENDERER );
        final String renderWysiwygImplName = TextUtil.getStringProperty(properties, PROP_WYSIWYG_RENDERER, DEFAULT_WYSIWYG_RENDERER );

        final Class< ? >[] rendererParams = { Context.class, WikiDocument.class };
        m_rendererConstructor = initRenderer( renderImplName, rendererParams );
        m_rendererWysiwygConstructor = initRenderer( renderWysiwygImplName, rendererParams );

        log.info( "Rendering content with " + renderImplName + "." );

        WikiEventManager.getInstance().addWikiEventListener( this.filterManager,this );
    }

    private Constructor< ? > initRenderer( final String renderImplName, final Class< ? >[] rendererParams ) throws WikiException {
        Constructor< ? > c = null;
        try {
            final Class< ? > clazz = Class.forName( renderImplName );
            c = clazz.getConstructor( rendererParams );
        } catch( final ClassNotFoundException e ) {
            log.error( "Unable to find WikiRenderer implementation " + renderImplName );
        } catch( final SecurityException e ) {
            log.error( "Unable to access the WikiRenderer(WikiContext,WikiDocument) constructor for "  + renderImplName );
        } catch( final NoSuchMethodException e ) {
            log.error( "Unable to locate the WikiRenderer(WikiContext,WikiDocument) constructor for "  + renderImplName );
        }
        if( c == null ) {
            throw new WikiException( "Failed to get WikiRenderer '" + renderImplName + "'." );
        }
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String beautifyTitle( final String title ) {
        if( m_beautifyTitle ) {
            try {
                final PageAttachment att = this.attachmentManager.getAttachmentInfo( title );
                if( att == null ) {
                    return TextUtil.beautifyString( title );
                }

                final String parent = ":FVK:"; //:FVK: TextUtil.beautifyString( att.getParentName() );
                return parent + "/" + att.getName();
            } catch( final ProviderException e ) {
                return title;
            }
        }

        return title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String beautifyTitleNoBreak( final String title ) {
        if( m_beautifyTitle ) {
            return TextUtil.beautifyString( title, "&nbsp;" );
        }

        return title;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public MarkupParser getParser( final Context context, final String pagedata ) {
    	// :FVK: WORKAROUND.
		if (1 == 1) {
			JSPWikiMarkupParser parser = new JSPWikiMarkupParser(context, new StringReader(pagedata));
			return parser;
		}

    	try {
			return ClassUtil.getMappedObject( m_markupParserClass, context, new StringReader( pagedata ) );
		} catch( final ReflectiveOperationException | IllegalArgumentException e ) {
			log.error( "unable to get an instance of " + m_markupParserClass + " (" + e.getMessage() + "), returning default markup parser.", e );
			return new JSPWikiMarkupParser( context, new StringReader( pagedata ) );
		} catch ( Exception e) {
			System.out.println(e.getMessage());
		}
    	return null; // :FVK: WORKAROUND.
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    // FIXME: The cache management policy is not very good: deleted/changed pages should be detected better.
    public WikiDocument getRenderedDocument( final Context context, final String pagedata ) {
        final String pageid = context.getRealPage().getName() + VERSION_DELIMITER +
                              //:FVK: context.getRealPage().getVersion() + VERSION_DELIMITER +
                              context.getVariable( Context.VAR_EXECUTE_PLUGINS );

        if( useCache( context ) ) {
            final Element element = m_documentCache.get( pageid );
            if ( element != null ) {
                final WikiDocument doc = ( WikiDocument )element.getObjectValue();

                //
                //  This check is needed in case the different filters have actually changed the page data.
                //  FIXME: Figure out a faster method
                if( pagedata.equals( doc.getPageData() ) ) {
                    if( log.isDebugEnabled() ) {
                        log.debug( "Using cached HTML for page " + pageid );
                    }
                    return doc;
                }
            } else if( log.isDebugEnabled() ) {
                log.debug( "Re-rendering and storing " + pageid );
            }
        }

        //  Refresh the data content
        try {
            final MarkupParser parser = getParser( context, pagedata );
            final WikiDocument doc = parser.parse();
            doc.setPageData( pagedata );
            if( useCache( context ) ) {
                m_documentCache.put( new Element( pageid, doc ) );
            }
            return doc;
        } catch( final IOException ex ) {
            log.error( "Unable to parse", ex );
        }

        return null;
    }

    boolean useCache( final Context context ) {
        return m_useCache && ContextEnum.PAGE_VIEW.getRequestContext().equals( context.getRequestContext() );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getHTML( final Context context, final WikiDocument doc ) throws IOException {
        final Boolean wysiwygVariable = context.getVariable( Context.VAR_WYSIWYG_EDITOR_MODE );
        final boolean wysiwygEditorMode;
        if( wysiwygVariable != null ) {
            wysiwygEditorMode = wysiwygVariable;
        } else {
            wysiwygEditorMode = false;
        }
        final WikiRenderer rend;
        if( wysiwygEditorMode ) {
            rend = getWysiwygRenderer( context, doc );
        } else {
            rend = getRenderer( context, doc );
        }

        return rend.getString();
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getHTML( final Context context, final WikiPage page ) {
        final String pagedata = this.pageManager.getPureText( page.getName(), -1); //:FVK: page.getVersion() );
        return textToHTML( context, pagedata );
    }

    /**
     *  Returns the converted HTML of the page's specific version. The version must be a positive integer, otherwise the current
     *  version is returned.
     *
     *  @param pagename WikiName of the page to convert.
     *  @param version Version number to fetch
     *  @return HTML-rendered page text.
     */
    @Override
    public String getHTML( final String pagename, final int version ) {
        final WikiPage page = this.pageManager.getPage( pagename, version );
        final Context context = Wiki.context().create( m_engine, page );
        context.setRequestContext( ContextEnum.PAGE_NONE.getRequestContext() );
        return getHTML( context, page );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String textToHTML( final Context context, String pagedata ) {
        String result = "";

        String valueRunFilters = this.variableManager.getValue( context,VariableManager.VAR_RUNFILTERS,"true" ); 
        final boolean runFilters = "true".equals(valueRunFilters);

        final StopWatch sw = new StopWatch();
        sw.start();
        try {
            if( runFilters ) {
                pagedata = this.filterManager.doPreTranslateFiltering( context, pagedata );
            }

            result = getHTML( context, pagedata );

            if( runFilters ) {
                result = this.filterManager.doPostTranslateFiltering( context, result );
            }
        } catch( final FilterException e ) {
            log.error( "page filter threw exception: ", e );
            // FIXME: Don't yet know what to do
        }
        sw.stop();
        if( log.isDebugEnabled() ) {
            log.debug( "Page " + context.getRealPage().getName() + " rendered, took " + sw );
        }

        return result;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String textToHTML( final Context context,
                              String pagedata,
                              final StringTransmutator localLinkHook,
                              final StringTransmutator extLinkHook,
                              final StringTransmutator attLinkHook,
                              final boolean parseAccessRules,
                              final boolean justParse ) {
        String result = "";

        if( pagedata == null ) {
            log.error("NULL pagedata to textToHTML()");
            return null;
        }

        final boolean runFilters = "true".equals( this.variableManager.getValue( context, VariableManager.VAR_RUNFILTERS,"true" ) );

        try {
            final StopWatch sw = new StopWatch();
            sw.start();

            if( runFilters && this.filterManager != null ) {
                pagedata = this.filterManager.doPreTranslateFiltering( context, pagedata );
            }

            final MarkupParser mp = getParser( context, pagedata );
            mp.addLocalLinkHook( localLinkHook );
            mp.addExternalLinkHook( extLinkHook );
            mp.addAttachmentLinkHook( attLinkHook );

            if( !parseAccessRules ) {
                mp.disableAccessRules();
            }

            final WikiDocument doc = mp.parse();

            //  In some cases it's better just to parse, not to render
            if( !justParse ) {
                result = getHTML( context, doc );

                if( runFilters && this.filterManager != null ) {
                    result = this.filterManager.doPostTranslateFiltering( context, result );
                }
            }

            sw.stop();

            if( log.isDebugEnabled() ) {
                log.debug( "Page " + context.getRealPage().getName() + " rendered, took " + sw );
            }
        } catch( final IOException e ) {
            log.error( "Failed to scan page data: ", e );
        } catch( final FilterException e ) {
            log.error( "page filter threw exception: ", e );
            // FIXME: Don't yet know what to do
        }

        return result;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public WikiRenderer getRenderer( final Context context, final WikiDocument doc ) {
        final Object[] params = { context, doc };
        return getRenderer( params, m_rendererConstructor );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public WikiRenderer getWysiwygRenderer( final Context context, final WikiDocument doc ) {
        final Object[] params = { context, doc };
        return getRenderer( params, m_rendererWysiwygConstructor );
    }

    @SuppressWarnings("unchecked")
    private < T extends WikiRenderer > T getRenderer( final Object[] params, final Constructor<?> rendererConstructor ) {
        try {
            return ( T )rendererConstructor.newInstance( params );
        } catch( final Exception e ) {
            log.error( "Unable to create WikiRenderer", e );
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Flushes the document cache in response to a POST_SAVE_BEGIN event.
     *
     * @see WikiEventListener#actionPerformed(WikiEvent)
     */
    @Override
    public void actionPerformed( final WikiEvent event ) {
        log.debug( "event received: " + event.toString() );
        if( m_useCache ) {
            if( ( event instanceof WikiPageEvent ) && ( event.getType() == WikiPageEvent.POST_SAVE_BEGIN ) ) {
                if( m_documentCache != null ) {
                    final String pageName = ( ( WikiPageEvent ) event ).getPageName();
                    m_documentCache.remove( pageName );
                    final Collection< String > referringPages = this.referenceManager.findReferrers( pageName );

                    //
                    //  Flush also those pages that refer to this page (if an nonexistent page
                    //  appears, we need to flush the HTML that refers to the now-existent page)
                    //
                    if( referringPages != null ) {
                        for( final String page : referringPages ) {
                            if( log.isDebugEnabled() ) {
                                log.debug( "Flushing latest version of " + page );
                            }
                            // as there is a new version of the page expire both plugin and pluginless versions of the old page
                            m_documentCache.remove( page + VERSION_DELIMITER + PageProvider.LATEST_VERSION  + VERSION_DELIMITER + Boolean.FALSE );
                            m_documentCache.remove( page + VERSION_DELIMITER + PageProvider.LATEST_VERSION  + VERSION_DELIMITER + Boolean.TRUE );
                            m_documentCache.remove( page + VERSION_DELIMITER + PageProvider.LATEST_VERSION  + VERSION_DELIMITER + null );
                        }
                    }
                }
            }
        }
    }

}
 