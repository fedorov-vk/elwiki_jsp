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
package org.apache.wiki;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.wiki.api.Release;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.diff.DifferenceManager;
import org.apache.wiki.api.engine.Initializable;
import org.apache.wiki.api.event.WikiEngineEvent;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiPageEvent;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.api.plugin.PluginManager;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.api.rss.RSSGenerator;
import org.apache.wiki.api.search.SearchManager;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.api.ui.CommandResolver;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.api.ui.progress.ProgressManager;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.apache.wiki.auth.UserManager;
import org.apache.wiki.auth.acl.AclManager;
//import org.apache.wiki.auth.authorize.GroupManager;
import org.apache.wiki.content0.PageRenamer;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.ui.admin0.AdminBeanManager;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.ClassUtil;
//:FVK:import org.apache.wiki.util.PropertyReader;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.workflow0.WorkflowManager;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.authorization.IAuthorizer;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.resources.ResourcesActivator;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 *  Main implementation for {@link Engine}.
 *
 *  <P>
 *  Using this class:  Always get yourself an instance from JSP page by using the {@code WikiEngine.getInstance(..)} method.  Never create
 *  a new WikiEngine() from scratch, unless you're writing tests.
 *
 *  <p>
 *  {@inheritDoc}
 */
public class WikiEngine implements Engine {

    private static final String ATTR_WIKIENGINE = "org.apache.wiki.WikiEngine";
    private static final Logger log = Logger.getLogger( WikiEngine.class );

    /** Stores configuration. */
	private IWikiConfiguration wikiConfiguration;

    /** Should the user info be saved with the page data as well? */
    private boolean m_saveUserInfo = true;

    /** Store the file path to the basic URL.  When we're not running as a servlet, it defaults to the user's current directory. */
    private String m_rootPath = System.getProperty( "user.dir" );

    /** Store the ServletContext that we're in.  This may be null if WikiEngine is not running inside a servlet container (i.e. when testing). */
    private ServletContext   m_servletContext = null;

    /** Stores the template path.  This is relative to "templates". */
    private String           m_templateDir;

    /** The time when this engine was started. */
    private Date             m_startTime;

    /** The location where the work directory is. */
    private String           m_workDir;

    /** Each engine has their own application id. */
    private String           m_appid = "";

    /** engine is up and running or not */
    private boolean          m_isConfigured = false;

    /** Stores wikiengine attributes. */
    private Map< String, Object > m_attributes = new ConcurrentHashMap<>();

    /** Stores WikiEngine's associated managers. */
    protected Map< Class< ? >, Object > managers = new ConcurrentHashMap<>();

    /**
     *  Gets a WikiEngine related to this servlet.  Since this method is only called from JSP pages (and JspInit()) to be specific,
     *  we throw a RuntimeException if things don't work.
     *
     *  @param config The ServletConfig object for this servlet.
     *  @return A WikiEngine instance.
     *  @throws InternalWikiException in case something fails. This is a RuntimeException, so be prepared for it.
     */
    public static synchronized WikiEngine getInstance( final ServletConfig config ) throws InternalWikiException {
        return getInstance( config.getServletContext() );
    }

    /**
     *  Gets a WikiEngine related to the servlet. Works just like getInstance( ServletConfig )
     *
     *  @param context The ServletContext of the webapp servlet/JSP calling this method.
     *  @return One fully functional, properly behaving WikiEngine.
     *  @throws InternalWikiException If the WikiEngine instantiation fails.
     */
    public static synchronized WikiEngine getInstance( final ServletContext context ) throws InternalWikiException {
        WikiEngine engine = ( WikiEngine )context.getAttribute( ATTR_WIKIENGINE );
        if( engine == null ) {
            final String appid = Integer.toString( context.hashCode() );
            context.log(" Assigning new engine to "+appid);
            try {
                engine = new WikiEngine( context, appid );
                context.setAttribute( ATTR_WIKIENGINE, engine );
            } catch( final Exception e ) {
                context.log( "ERROR: Failed to create a Wiki engine: " + e.getMessage() );
                log.error( "ERROR: Failed to create a Wiki engine, stacktrace follows ", e );
                throw new InternalWikiException( "No wiki engine, check logs.", e );
            }
        }
        return engine;
    }

    /**
     *  Instantiate using this method when you're running as a servlet and WikiEngine will figure out where to look for the property file.
     *  Do not use this method - use WikiEngine.getInstance() instead.
     *
     *  @param context A ServletContext.
     *  @param appid   An Application ID.  This application is an unique random string which is used to recognize this WikiEngine.
     *  @param props   The WikiEngine configuration.
     *  @throws WikiException If the WikiEngine construction fails.
     */
    protected WikiEngine( final ServletContext context, final String appid ) throws WikiException {
        m_servletContext = context;
        m_appid          = appid;

        // :FVK: workaround - доступ к конфигурации.  
		BundleContext bc = ResourcesActivator.getContext();
		ServiceReference<?> ref = bc.getServiceReference(IWikiConfiguration.class.getName());
		if (ref != null) {
			this.wikiConfiguration = (IWikiConfiguration) bc.getService(ref);

			// далее - workaround
			this.wikiConfiguration.setBaseURL(m_servletContext.getContextPath());
		}
        
        // Stash the WikiEngine in the servlet context
        if ( context != null ) {
            context.setAttribute( ATTR_WIKIENGINE,  this );
            m_rootPath = context.getRealPath("/");
        }

        try {
            //  Note: rootPath - may be null, if JSPWiki has been deployed in a WAR file.
            initialize();
            log.info( "Root path for this Wiki is: '" + m_rootPath + "'" );
        } catch( final Exception e ) {
            final String msg = Release.APPNAME+": Unable to load and setup properties from jspwiki.properties. "+e.getMessage();
            if ( context != null ) {
                context.log( msg );
            }
            throw new WikiException( msg, e );
        }
    }

    /**
     *  Does all the real initialization.
     */
    private void initialize() throws WikiException {
        m_startTime  = new Date();

        log.info( "*******************************************" );
        log.info( Release.APPNAME + " " + Release.getVersionString() + " starting. Whee!" );

        fireEvent( WikiEngineEvent.INITIALIZING ); // begin initialization

        log.debug( "Java version: " + System.getProperty( "java.runtime.version" ) );
        log.debug( "Java vendor: " + System.getProperty( "java.vm.vendor" ) );
        log.debug( "OS: " + System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ) + " " + System.getProperty( "os.arch" ) );
        log.debug( "Default server locale: " + Locale.getDefault() );
        log.debug( "Default server timezone: " + TimeZone.getDefault().getDisplayName( true, TimeZone.LONG ) );

        if( m_servletContext != null ) {
            log.info( "Servlet container: " + m_servletContext.getServerInfo() );
            if( m_servletContext.getMajorVersion() < 3 || ( m_servletContext.getMajorVersion() == 3 && m_servletContext.getMinorVersion() < 1 ) ) {
                throw new InternalWikiException( "JSPWiki requires a container which supports at least version 3.1 of Servlet specification" );
            }
        }

        log.debug( "Configuring WikiEngine..." );

        IPreferenceStore preferences = this.wikiConfiguration.getWikiPreferences();

        //  Create and find the default working directory.
        m_workDir = TextUtil.getStringProperty(preferences, PROP_WORKDIR, null);

		if (m_workDir == null || m_workDir.length() == 0) {
            m_workDir = System.getProperty( "java.io.tmpdir", "." );
            m_workDir += File.separator + Release.APPNAME + "-" + m_appid;
        }

        try {
            final File f = new File( m_workDir );
            f.mkdirs();

            //
            //  A bunch of sanity checks
            //
            if( !f.exists() ) {
                throw new WikiException( "Work directory does not exist: " + m_workDir );
            }
            if( !f.canRead() ) {
                throw new WikiException( "No permission to read work directory: " + m_workDir );
            }
            if( !f.canWrite() ) {
                throw new WikiException( "No permission to write to work directory: " + m_workDir );
            }
            if( !f.isDirectory() ) {
                throw new WikiException( "jspwiki.workDir does not point to a directory: " + m_workDir );
            }
        } catch( final SecurityException e ) {
            log.fatal( "Unable to find or create the working directory: "+m_workDir, e );
            throw new IllegalArgumentException( "Unable to find or create the working dir: " + m_workDir, e );
        }

        log.info( "JSPWiki working directory is '" + m_workDir + "'" );

        m_saveUserInfo   = TextUtil.getBooleanProperty( preferences, PROP_STOREUSERNAME, m_saveUserInfo );
        m_templateDir    = TextUtil.getStringProperty( preferences, PROP_TEMPLATEDIR, "default" );
        enforceValidTemplateDirectory();

        //
        //  Initialize the important modules.  Any exception thrown by the managers means that we will not start up.
        //
        try {
        	String def = "org.apache.wiki.auth.acl.AclManager"; //:FVK: ClassUtil.getMappedClass( AclManager.class.getName() ).getName();
            final String aclClassName = TextUtil.getStringProperty( preferences, PROP_ACL_MANAGER_IMPL, def);

            initService(CommandResolver.class);
            //initComponent( CommandResolver.class, this, props );

            /*final String urlConstructorClassName = TextUtil.getStringProperty( props, PROP_URLCONSTRUCTOR, "DefaultURLConstructor" );
            final Class< ? > urlclass; //:FVK: = ClassUtil.findClass( "org.apache.wiki.url", urlConstructorClassName );
            urlclass = DefaultURLConstructor.class;*/

            initService(URLConstructor.class); // org.apache.wiki.url.DefaultURLConstructor
            //initComponent( urlclass.getName(), URLConstructor.class );

            initService(PageManager.class);
            //initComponent( PageManager.class, this, props );

            initService(PluginManager.class);
            //initComponent( PluginManager.class, this, props );

            initService(DifferenceManager.class);
            //initComponent( DifferenceManager.class, this, props );

            initService(AttachmentManager.class);
            //initComponent( AttachmentManager.class, this, props );

            initService(VariableManager.class);
            //initComponent( VariableManager.class, props );

            initService(SearchManager.class);
            //initComponent( SearchManager.class, this, props );

            initService(IIAuthenticationManager.class);
            //initComponent( AuthenticationManager.class );

            initService(AuthorizationManager.class);
            //initComponent( AuthorizationManager.class );

            initService(UserManager.class);
            //initComponent( UserManager.class );

          /*:FVK: 
            initService(GroupManager.class);
            //initComponent( GroupManager.class );
             */
            initService(IAuthorizer.class);

//:FVK: отладить:
            initService(EditorManager.class);
            //initComponent( EditorManager.class, this );

            initService(ProgressManager.class);
            //initComponent( ProgressManager.class, this );

            initService(AclManager.class);
            //initComponent( aclClassName, AclManager.class );

            initService(WorkflowManager.class);
            //initComponent( WorkflowManager.class );

            initService(TasksManager.class);
            //initComponent( TasksManager.class );

            initService(InternationalizationManager.class);
            //initComponent( InternationalizationManager.class, this );
            
            initService(TemplateManager.class);
            //initComponent( TemplateManager.class, this, props );

            initService(FilterManager.class);
            //initComponent( FilterManager.class, this, props );

            initService(AdminBeanManager.class);
            //initComponent( AdminBeanManager.class, this );

            initService(PageRenamer.class);
            //initComponent( PageRenamer.class, this, props );

            // RenderingManager depends on FilterManager events.
            initService(RenderingManager.class);
            //initComponent( RenderingManager.class );
//TODO
            //  ReferenceManager has the side effect of loading all pages.  Therefore after this point, all page attributes are available.
            //  initReferenceManager is indirectly using m_filterManager, therefore it has to be called after it was initialized.
            initReferenceManager();

            //  Hook the different manager routines into the system.
            ServicesRefs.getFilterManager().addPageFilter( ServicesRefs.getReferenceManager(), -1001 );
            ServicesRefs.getFilterManager().addPageFilter( ServicesRefs.getSearchManager(), -1002 );
        } catch( final RuntimeException e ) {
            // RuntimeExceptions may occur here, even if they shouldn't.
            log.fatal( "Failed to start managers.", e );
            throw new WikiException( "Failed to start managers: " + e.getMessage(), e );
        } catch( final ClassNotFoundException e ) {
            log.fatal( "JSPWiki could not start, URLConstructor was not found: " + e.getMessage(), e );
            throw new WikiException( e.getMessage(), e );
        } catch( final InstantiationException e ) {
            log.fatal( "JSPWiki could not start, URLConstructor could not be instantiated: " + e.getMessage(), e );
            throw new WikiException( e.getMessage(), e );
        } catch( final IllegalAccessException e ) {
            log.fatal( "JSPWiki could not start, URLConstructor cannot be accessed: " + e.getMessage(), e );
            throw new WikiException( e.getMessage(), e );
        } catch( final Exception e ) {
            // Final catch-all for everything
            log.fatal( "JSPWiki could not start, due to an unknown exception when starting.",e );
            throw new WikiException( "Failed to start. Caused by: " + e.getMessage() + "; please check log files for better information.", e );
        }

        //  Initialize the good-to-have-but-not-fatal modules.
        try {
            if( TextUtil.getBooleanProperty( preferences, RSSGenerator.PROP_GENERATE_RSS,false ) ) {
                initComponent( RSSGenerator.class, this, preferences);
            }
        } catch( final Exception e ) {
            log.error( "Unable to start RSS generator - JSPWiki will still work, but there will be no RSS feed.", e );
        }

        /*:FVK: WORKAROUND.
        final Map< String, String > extraComponents = ClassUtil.getExtraClassMappings();
        initExtraComponents( extraComponents );
        */

        fireEvent( WikiEngineEvent.INITIALIZED ); // initialization complete

        log.info( "WikiEngine configured." );
        m_isConfigured = true;
    }

    private <T> T initService(Class<T> clazz) throws Exception {
        try {
			T service = ResourcesActivator.getService(clazz);
			managers.put( clazz, service );
			if(service instanceof Initializable) {
				((Initializable) service).initialize(this);
			}
			return service;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(":FVK: -- ERROR");
		}
        return null;
	}

	void initExtraComponents( final Map< String, String > extraComponents ) {
        for( final Map.Entry< String, String > extraComponent : extraComponents.entrySet() ) {
            try {
                log.info( "Registering on WikiEngine " + extraComponent.getKey() + " as " + extraComponent.getValue() );
                initComponent( extraComponent.getKey(), Class.forName( extraComponent.getValue() ) );
            } catch( final Exception e ) {
                log.error( "Unable to start " + extraComponent.getKey(), e );
            }
        }
    }

    < T > void initComponent( final Class< T > componentClass, final Object... initArgs ) throws Exception {
        initComponent( componentClass.getName(), componentClass, initArgs );
    }

    
	/**
	 * :FVK: сделанл для JSPwiki менеджеров. (и, возможно, каких-то еще компонентов).
	 * 
	 * @param <T>
	 * @param componentInitClass
	 * @param componentClass
	 * @param initArgs
	 * @throws Exception
	 */
    < T > void initComponent( final String componentInitClass, final Class< T > componentClass, final Object... initArgs ) throws Exception {
        final T component;
        if( initArgs == null || initArgs.length == 0 ) {
            component = ClassUtil.getMappedObject( componentInitClass );
        } else {
            component = ClassUtil.getMappedObject( componentInitClass, initArgs );
        }
        managers.put( componentClass, component );
        if( Initializable.class.isAssignableFrom( componentClass ) ) {
            ( ( Initializable )component ).initialize( this );
        }
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings( "unchecked" )
    @NonNull
    public < T > T getManager( final Class< T > manager ) {
    	T result = ( T )managers.entrySet().stream()
                                       .filter( e -> manager.isAssignableFrom( e.getKey() ) )
                                       .map( Map.Entry::getValue )
                                       .findFirst().orElse( null );
    	return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings( "unchecked" )
    public < T > List< T > getManagers( final Class< T > manager ) {
        return ( List< T > )managers.entrySet().stream()
                                               .filter( e -> manager.isAssignableFrom( e.getKey() ) )
                                               .map( Map.Entry::getValue )
                                               .collect( Collectors.toList() );
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConfigured() {
        return m_isConfigured;
    }

    /**
     * Checks if the template directory specified in the wiki's properties actually exists. If it doesn't, then {@code m_templateDir} is
     * set to {@link #DEFAULT_TEMPLATE_NAME}.
     * <p>
     * This checks the existence of the <tt>ViewTemplate.jsp</tt> file, which exists in every template using {@code m_servletContext.getRealPath("/")}.
     * <p>
     * {@code m_servletContext.getRealPath("/")} can return {@code null} on certain servers/conditions (f.ex, packed wars), an extra check
     * against {@code m_servletContext.getResource} is made.
     */
    void enforceValidTemplateDirectory() {
        if( m_servletContext != null ) {
            final String viewTemplate = "templates" + File.separator + getTemplateDir() + File.separator + "ViewTemplate.jsp";
            boolean exists = new File( m_servletContext.getRealPath("/") + viewTemplate ).exists();
            if( !exists ) {
                try {
                    final URL url = m_servletContext.getResource( viewTemplate );
                    exists = url != null && !url.getFile().isEmpty();
                } catch( final MalformedURLException e ) {
                    log.warn( "template not found with viewTemplate " + viewTemplate );
                }
            }
            if( !exists ) {
                log.warn( getTemplateDir() + " template not found, updating WikiEngine's default template to " + DEFAULT_TEMPLATE_NAME );
                m_templateDir = DEFAULT_TEMPLATE_NAME;
            }
        }
    }

    /**
     *  Initializes the reference manager. Scans all existing WikiPages for
     *  internal links and adds them to the ReferenceManager object.
     *
     *  @throws WikiException If the reference manager initialization fails.
     */
    public void initReferenceManager() throws WikiException {
        try {
            // Build a new manager with default key lists.
            if( ServicesRefs.getReferenceManager() == null ) {
                final ArrayList< WikiPage > pages = new ArrayList<>();
                pages.addAll( getManager( PageManager.class ).getAllPages() );
                //:FVK: pages.addAll( getManager( AttachmentManager.class ).getAllAttachments() );

				ReferenceManager manager = initService(ReferenceManager.class);
				if (manager != null)
					manager.initialize(pages);
                //initComponent( ReferenceManager.class, this );
                //getManager( ReferenceManager.class ).initialize( pages );
            }

        } catch( final ProviderException e ) {
            log.fatal("PageProvider is unable to list pages: ", e);
        } catch( final Exception e ) {
            throw new WikiException( "Could not instantiate ReferenceManager: " + e.getMessage(), e );
        }
    }

    /** {@inheritDoc} */
    @Override
    public IPreferenceStore getWikiPreferences() {
        return this.wikiConfiguration.getWikiPreferences();
    }

    /** {@inheritDoc} */
    @Override
    public String getWorkDir() {
        return m_workDir;
    }

    /** {@inheritDoc} */
    @Override
    public String getTemplateDir() {
        return m_templateDir;
    }

    /** {@inheritDoc} */
    @Override
    public Date getStartTime() {
        return ( Date )m_startTime.clone();
    }

    /** {@inheritDoc} */
    @Override
    public String getGlobalRSSURL() {
        final RSSGenerator rssGenerator = ServicesRefs.getRssGenerator();
        if( rssGenerator != null && rssGenerator.isEnabled() ) {
            return this.wikiConfiguration.getBaseURL() + "/" + rssGenerator.getRssFile();
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getURL( final String context, String pageName, final String params ) {
        if( pageName == null ) {
            pageName = this.wikiConfiguration.getFrontPage();
        }
        final URLConstructor urlConstructor = ServicesRefs.getUrlConstructor();
        return urlConstructor.makeURL( context, pageName, params );
    }

    /** {@inheritDoc} */
    @Override
    public ServletContext getServletContext() {
        return m_servletContext;
    }

    /** {@inheritDoc} */
    @Override
    public String getSpecialPageReference( final String original ) {
        return ServicesRefs.getCommandResolver().getSpecialPageReference( original );
    }

    /** {@inheritDoc} */
    @Override
    public String getFinalPageName( final String page ) throws ProviderException {
        return ServicesRefs.getCommandResolver().getFinalPageName( page );
    }

    /** {@inheritDoc} */
    @Override
    public Charset getContentEncoding() {
         return StandardCharsets.UTF_8; // :FVK: WORKAROUND.
    }

    /**
     * {@inheritDoc}
     * <p>It is called by {@link WikiServlet#destroy()}. When this method is called, it fires a "shutdown" WikiEngineEvent to
     * all registered listeners.
     */
    @Override
    public void shutdown() {
        fireEvent( WikiEngineEvent.SHUTDOWN );
        ServicesRefs.getFilterManager().destroy();
    }

    /** {@inheritDoc} */
    @Override
    public String getRootPath() {
        return m_rootPath;
    }

    /** {@inheritDoc} */
    @Override
    public final synchronized void addWikiEventListener( final WikiEventListener listener ) {
        WikiEventManager.addWikiEventListener( this, listener );
    }

    /** {@inheritDoc} */
    @Override
    public final synchronized void removeWikiEventListener( final WikiEventListener listener ) {
        WikiEventManager.removeWikiEventListener( this, listener );
    }

    /**
     * Fires a WikiEngineEvent to all registered listeners.
     *
     * @param type  the event type
     */
    protected final void fireEvent( final int type ) {
        if( WikiEventManager.isListening(this ) ) {
            WikiEventManager.fireEvent( this, new WikiEngineEvent(this, type ) );
        }
    }

    /**
     * Fires a WikiPageEvent to all registered listeners.
     *
     * @param type  the event type
     */
    protected final void firePageEvent( final int type, final String pageName ) {
        if( WikiEventManager.isListening(this ) ) {
            WikiEventManager.fireEvent(this,new WikiPageEvent(this, type, pageName ) );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setAttribute( final String key, final Object value ) {
        m_attributes.put( key, value );
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings( "unchecked" )
    public < T > T getAttribute( final String key ) {
        return ( T )m_attributes.get( key );
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings( "unchecked" )
    public < T > T removeAttribute( final String key ) {
        return ( T )m_attributes.remove( key );
    }

    @Override
    public IWikiConfiguration getWikiConfiguration() {
    	return this.wikiConfiguration;
    }

	@Override
	public WikiPage getPageById(String pageId) {
		WikiPage wikiPage;
		PageManager pageManager = ServicesRefs.getPageManager();
		wikiPage = pageManager.getPageById(pageId);

		return wikiPage;
	}
}