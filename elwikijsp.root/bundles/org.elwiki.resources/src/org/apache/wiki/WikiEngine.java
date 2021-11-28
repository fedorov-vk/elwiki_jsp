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

import org.apache.log4j.Logger;
import org.apache.wiki.api.Release;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
import org.apache.wiki.api.search.SearchManager;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.api.ui.CommandResolver;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.UserManager;
import org.apache.wiki.auth.acl.AclManager;
//import org.apache.wiki.auth.authorize.GroupManager;
import org.apache.wiki.content0.PageRenamer;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.rss.RSSGenerator;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.ui.admin0.AdminBeanManager;
import org.apache.wiki.ui.progress.ProgressManager;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.ClassUtil;
//:FVK:import org.apache.wiki.util.PropertyReader;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.workflow0.WorkflowManager;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.IWikiConstants;
import org.elwiki.api.authorization.IAuthorizer;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.resources.ResourcesActivator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


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
            getManager( FilterManager.class ).addPageFilter( getManager( ReferenceManager.class ), -1001 );
            getManager( FilterManager.class ).addPageFilter( getManager( SearchManager.class ), -1002 );
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
        T service = ResourcesActivator.getService(clazz);
        managers.put( clazz, service );
		if(service instanceof Initializable) {
			((Initializable) service).initialize(this);
		}
		return service;
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
            if( getManager( ReferenceManager.class ) == null ) {
                final ArrayList< WikiPage > pages = new ArrayList<>();
                pages.addAll( getManager( PageManager.class ).getAllPages() );
                //:FVK: pages.addAll( getManager( AttachmentManager.class ).getAllAttachments() );

                ReferenceManager manager = initService(ReferenceManager.class);
                manager.initialize( pages );
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
        final RSSGenerator rssGenerator = getManager( RSSGenerator.class );
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
        final URLConstructor urlConstructor = getManager( URLConstructor.class );
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
        return getManager( CommandResolver.class ).getSpecialPageReference( original );
    }

    /** {@inheritDoc} */
    @Override
    public String getFinalPageName( final String page ) throws ProviderException {
        return getManager( CommandResolver.class ).getFinalPageName( page );
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
        getManager( FilterManager.class ).destroy();
    }

    /**
     *  Returns the current TemplateManager.
     *
     *  @return A TemplateManager instance.
     * @deprecated use {@code getManager( TemplateManager.class )} instead.
     */
    @Deprecated
    public TemplateManager getTemplateManager() {
        return getManager( TemplateManager.class );
    }

    /**
     * Returns the {@link org.apache.wiki.workflow0.WorkflowManager} associated with this WikiEngine. If the WikiEngine has not been
     * initialized, this method will return <code>null</code>.
     *
     * @return the task queue
     * @deprecated use {@code getManager( WorkflowManager.class )} instead.
     */
    @Deprecated
    public WorkflowManager getWorkflowManager() {
        return getManager( WorkflowManager.class );
    }

    /**
     * Returns this object's ReferenceManager.
     *
     * @return The current ReferenceManager instance.
     * @since 1.6.1
     * @deprecated use {@code getManager( ReferenceManager.class )} instead.
     */
    @Deprecated
    public ReferenceManager getReferenceManager() {
        return getManager( ReferenceManager.class );
    }

    /**
     * Returns the current rendering manager for this wiki application.
     *
     * @since 2.3.27
     * @return A RenderingManager object.
     * @deprecated use {@code getManager( RenderingManager.class )} instead.
     */
    @Deprecated
    public RenderingManager getRenderingManager() {
        return getManager( RenderingManager.class );
    }

    /**
     * Returns the current plugin manager.
     *
     * @since 1.6.1
     * @return The current PluginManager instance
     * @deprecated use {@code getManager( PluginManager.class )} instead.
     */
    @Deprecated
    public PluginManager getPluginManager() {
        return getManager( PluginManager.class );
    }

    /**
     *  Returns the current variable manager.
     *
     *  @return The current VariableManager.
     * @deprecated use {@code getManager( VariableManager.class )} instead.
     */
    @Deprecated
    public VariableManager getVariableManager()  {
        return getManager( VariableManager.class );
    }

    /**
     * Returns the current PageManager which is responsible for storing and managing WikiPages.
     *
     * @return The current PageManager instance.
     * @deprecated use {@code getManager( PageManager.class )} instead.
     */
    @Deprecated
    public PageManager getPageManager() {
        return getManager( PageManager.class );
    }

    /**
     * Returns the CommandResolver for this wiki engine.
     *
     * @return the resolver
     * @deprecated use {@code getManager( CommandResolver.class )} instead.
     */
    @Deprecated
    public CommandResolver getCommandResolver() {
        return getManager( CommandResolver.class );
    }

    /**
     * Returns the current AttachmentManager, which is responsible for storing and managing attachments.
     *
     * @since 1.9.31.
     * @return The current AttachmentManager instance
     * @deprecated use {@code getManager( AttachmentManager.class )} instead.
     */
    @Deprecated
    public AttachmentManager getAttachmentManager() {
        return getManager( AttachmentManager.class );
    }

    /**
     * Returns the currently used authorization manager.
     *
     * @return The current AuthorizationManager instance.
     * @deprecated use {@code getManager( AuthorizationManager.class )} instead.
     */
    @Deprecated
    public AuthorizationManager getAuthorizationManager()  {
        return getManager( AuthorizationManager.class );
    }

    /**
     * Returns the currently used authentication manager.
     *
     * @return The current AuthenticationManager instance.
     * @deprecated use {@code getManager( AuthenticationManager.class )} instead.
     */
    @Deprecated
    public IIAuthenticationManager getAuthenticationManager() {
        return getManager( IIAuthenticationManager.class );
    }

    /**
     * Returns the manager responsible for the filters.
     *
     * @since 2.1.88
     * @return The current FilterManager instance.
     * @deprecated use {@code getManager( FilterManager.class )} instead.
     */
    @Deprecated
    public FilterManager getFilterManager() {
        return getManager( FilterManager.class );
    }

    /**
     * Returns the manager responsible for searching the Wiki.
     *
     * @since 2.2.21
     * @return The current SearchManager instance.
     * @deprecated use {@code getManager( SearchManager.class )} instead.
     */
    @Deprecated
    public SearchManager getSearchManager() {
        return getManager( SearchManager.class );
    }

    /**
     * Returns the progress manager we're using
     *
     * @return A ProgressManager.
     * @since 2.6
     * @deprecated use {@code getManager( ProgressManager.class )} instead.
     */
    @Deprecated
    public ProgressManager getProgressManager() {
        return getManager( ProgressManager.class );
    }

    /** {@inheritDoc} */
    @Override
    public String getRootPath() {
        return m_rootPath;
    }

    /**
     * @since 2.2.6
     * @return the URL constructor.
     * @deprecated use {@code getManager( URLConstructor.class )} instead.
     */
    @Deprecated
    public URLConstructor getURLConstructor() {
        return getManager( URLConstructor.class );
    }

    /**
     * Returns the RSSGenerator. If the property <code>jspwiki.rss.generate</code> has not been set to <code>true</code>, this method
     * will return <code>null</code>, <em>and callers should check for this value.</em>
     *
     * @since 2.1.165
     * @return the RSS generator
     * @deprecated use {@code getManager( RSSGenerator.class )} instead.
     */
    @Deprecated
    public RSSGenerator getRSSGenerator() {
        return getManager( RSSGenerator.class );
    }

    /**
     *  Returns the PageRenamer employed by this WikiEngine.
     *
     *  @since 2.5.141
     *  @return The current PageRenamer instance.
     * @deprecated use {@code getManager( PageRenamer.class )} instead.
     */
    @Deprecated
    public PageRenamer getPageRenamer() {
        return getManager( PageRenamer.class );
    }

    /**
     *  Returns the UserManager employed by this WikiEngine.
     *
     *  @since 2.3
     *  @return The current UserManager instance.
     * @deprecated use {@code getManager( UserManager.class )} instead.
     */
    @Deprecated
    public UserManager getUserManager() {
        return getManager( UserManager.class );
    }

    /**
     *  Returns the TasksManager employed by this WikiEngine.
     *
     *  @return The current TasksManager instance.
     * @deprecated use {@code getManager( TaskManager.class )} instead.
     */
    @Deprecated
    public TasksManager getTasksManager() {
        return getManager( TasksManager.class );
    }

    /**
     *  Returns the GroupManager employed by this WikiEngine.
     *
     *  @since 2.3
     *  @return The current GroupManager instance.
     * @deprecated use {@code getManager( GroupManager.class )} instead.
     */
    /*:FVK:
    @Deprecated
    public GroupManager getGroupManager() {
        return getManager( GroupManager.class ); -- new: IAuthorizer.class
    }
    */

    /**
     *  Returns the current {@link AdminBeanManager}.
     *
     *  @return The current {@link AdminBeanManager}.
     *  @since  2.6
     * @deprecated use {@code getManager( AdminBeanManager.class )} instead.
     */
    @Deprecated
    public AdminBeanManager getAdminBeanManager() {
        return getManager( AdminBeanManager.class );
    }

    /**
     *  Returns the AclManager employed by this WikiEngine. The AclManager is lazily initialized.
     *  <p>
     *  The AclManager implementing class may be set by the System property {@link #PROP_ACL_MANAGER_IMPL}.
     *  </p>
     *
     * @since 2.3
     * @return The current AclManager.
     * @deprecated use {@code getManager( AclManager.class )} instead.
     */
    @Deprecated
    public AclManager getAclManager()  {
        return getManager( AclManager.class );
    }

    /**
     *  Returns the DifferenceManager so that texts can be compared.
     *
     *  @return the difference manager.
     * @deprecated use {@code getManager( DifferenceManager.class )} instead.
     */
    @Deprecated
    public DifferenceManager getDifferenceManager() {
        return getManager( DifferenceManager.class );
    }

    /**
     *  Returns the current EditorManager instance.
     *
     *  @return The current EditorManager.
     * @deprecated use {@code getManager( EditorManager.class )} instead.
     */
    @Deprecated
    public EditorManager getEditorManager() {
        return getManager( EditorManager.class );
    }

    /**
     *  Returns the current i18n manager.
     *
     *  @return The current Intertan... Interante... Internatatializ... Whatever.
     * @deprecated use {@code getManager( InternationalizationManager.class )} instead.
     */
    @Deprecated
    public InternationalizationManager getInternationalizationManager() {
        return getManager( InternationalizationManager.class );
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
		PageManager pageManager = this.getPageManager();
		wikiPage = pageManager.getPageById(pageId);

		return wikiPage;
	}
}