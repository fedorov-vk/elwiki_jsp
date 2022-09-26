package org.elwiki.services;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.api.IStorageCdo;
import org.apache.wiki.api.Release;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Context;
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
import org.apache.wiki.api.internal.ApiActivator;
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
import org.apache.wiki.auth.IPermissionManager;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.auth.UserManager;
import org.apache.wiki.auth.acl.AclManager;
import org.apache.wiki.content0.PageRenamer;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.ui.admin0.AdminBeanManager;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.ClassUtil;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.workflow0.WorkflowManager;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.IAuthorizer;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki_data.WikiPage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(name = "elwiki.ResourcesServicesRefs", service = Engine.class, immediate = true)
public class ServicesRefs implements Engine {

	private static WikiAjaxDispatcher wikiAjaxDispatcher;
	private static IPermissionManager permissionManager;
	private static AclManager aclManager;
	private static AttachmentManager attachmentManager;
	private static PageManager pageManager;
	private static PageRenamer pageRenamer;
	private static AuthorizationManager authorizationManager;
	private static IAuthorizer groupManager;
	private static ProgressManager progressManager;
	private static UserManager userManager;
	private static AdminBeanManager adminBeanManager;
	private static IIAuthenticationManager authenticationManager;
	private static RenderingManager renderingManager;
	private static ReferenceManager referenceManager;
	private static VariableManager variableManager;
	private static DifferenceManager differenceManager;
	private static TemplateManager templateManager;
	private static EditorManager editorManager;
	private static PluginManager pluginManager;
	private static FilterManager filterManager;
	private static SearchManager searchManager;
	private static CommandResolver commandResolver;
	private static InternationalizationManager internationalizationManager;
	private static WorkflowManager workflowManager;
	private static URLConstructor urlConstructor;
	private static RSSGenerator rssGenerator;
	private static TasksManager tasksManager;
	private static IStorageCdo storageCdo;
	private static ISessionMonitor sessionMonitor;

	public static ServicesRefs Instance;

	private BundleContext bundleContext;

	private static ThreadLocal<Context> thWikiContext = new ThreadLocal<>();

	// -- service handling ---------------------------(start)--

	/** Stores configuration. */
	@Reference // (cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	// -- start code block -- Services reference setters

	@Reference(target = "(component.factory=" + WikiAjaxDispatcher.WIKI_AJAX_DISPATCHER_FACTORY + ")")
	private ComponentFactory<WikiAjaxDispatcher> factoryWikiAjaxDispatcher;
	
	@Reference(target = "(component.factory=" + IPermissionManager.COMPONENT_FACTORY +")")
	private ComponentFactory<IPermissionManager> factoryPermissionManager;

	@Reference(target = "(component.factory=elwiki.AclManager.factory)")
	private ComponentFactory<AclManager> factoryAclManager;

	@Reference(target = "(component.factory=elwiki.AttachmentManager.factory)")
	private ComponentFactory<AttachmentManager> factoryAttachmentManager;

	@Reference(target = "(component.factory=elwiki.PageManager.factory)")
	private ComponentFactory<PageManager> factoryPageManager;

	@Reference(target = "(component.factory=elwiki.PageRenamer.factory)")
	private ComponentFactory<PageRenamer> factoryPageRenamer;

	@Reference(target = "(component.factory=elwiki.AuthorizationManager.factory)")
	private ComponentFactory<AuthorizationManager> factoryAuthorizationManager;

	@Reference(target = "(component.factory=elwiki.GroupManager.factory)")
	private ComponentFactory<IAuthorizer> factoryGroupManager;

	@Reference(target = "(component.factory=elwiki.ProgressManager.factory)")
	private ComponentFactory<ProgressManager> factoryProgressManager;

	@Reference(target = "(component.factory=elwiki.UserManager.factory)")
	private ComponentFactory<UserManager> factoryUserManager;

	@Reference(target = "(component.factory=elwiki.AdminBeanManager.factory)")
	private ComponentFactory<AdminBeanManager> factoryAdminBeanManager;

	@Reference(target = "(component.factory=elwiki.AuthenticationManager.factory)")
	private ComponentFactory<IIAuthenticationManager> factoryAuthenticationManager;

	@Reference(target = "(component.factory=elwiki.RenderingManager.factory)")
	private ComponentFactory<RenderingManager> factoryRenderingManager;

	@Reference(target = "(component.factory=elwiki.ReferenceManager.factory)")
	private ComponentFactory<ReferenceManager> factoryReferenceManager;

	@Reference(target = "(component.factory=elwiki.VariableManager.factory)")
	private ComponentFactory<VariableManager> factoryVariableManager;

	@Reference(target = "(component.factory=elwiki.DifferenceManager.factory)")
	private ComponentFactory<DifferenceManager> factoryDifferenceManager;

	@Reference(target = "(component.factory=elwiki.TemplateManager.factory)")
	private ComponentFactory<TemplateManager> factoryTemplateManager;

	@Reference(target = "(component.factory=elwiki.EditorManager.factory)")
	private ComponentFactory<EditorManager> factoryEditorManager;

	@Reference(target = "(component.factory=elwiki.PluginManager.factory)")
	private ComponentFactory<PluginManager> factoryPluginManager;

	@Reference(target = "(component.factory=elwiki.FilterManager.factory)")
	private ComponentFactory<FilterManager> factoryFilterManager;

	@Reference(target = "(component.factory=elwiki.SearchManager.factory)")
	private ComponentFactory<SearchManager> factorySearchManager;

	@Reference(target = "(component.factory=elwiki.CommandResolver.factory)")
	private ComponentFactory<CommandResolver> factoryCommandResolver;

	@Reference(target = "(component.factory=elwiki.InternationalizationManager.factory)")
	private ComponentFactory<InternationalizationManager> factoryInternationalizationManager;

	@Reference(target = "(component.factory=elwiki.WorkflowManager.factory)")
	private ComponentFactory<WorkflowManager> factoryWorkflowManager;

	@Reference(target = "(component.factory=elwiki.UrlConstructor.factory)")
	private ComponentFactory<URLConstructor> factoryUrlConstructor;

	@Reference(target = "(component.factory=elwiki.TasksManager.factory)")
	private ComponentFactory<TasksManager> factoryTasksManager;

	@Reference(target = "(component.factory=elwiki.StorageCdo.factory)")
	private ComponentFactory<IStorageCdo> factoryStorageCdo;

	@Reference(target = "(component.factory=elwiki.SessionMonitor.factory)")
	private ComponentFactory<ISessionMonitor> factorySessionMonitor;

	// :FVK:
	public void setRssGenerator(RSSGenerator rssGenerator) {
		ServicesRefs.rssGenerator = rssGenerator;
	}

	@Activate
	protected void startupService(BundleContext bc) throws WikiException {
		this.bundleContext = bc;
		ServicesRefs.Instance = this;

		/* Initialize this instance.
		 */
		IPreferenceStore preferences = this.wikiConfiguration.getWikiPreferences();

		try {
			// Note: rootPath - may be null, if JSPWiki has been deployed in a WAR file.
			servicesMaker();
			initialize();
			log.info("Root path for this Wiki is: '" + m_rootPath + "'");
		} catch (final Exception e) {
			final String msg = Release.APPNAME + ": Unable to initialise service. "
					+ e.getMessage();
			throw new WikiException(msg, e);
		}
	}

	private void servicesMaker() {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(ENGINE_REFERENCE, this);

		managers.put(WikiAjaxDispatcher.class,
				ServicesRefs.wikiAjaxDispatcher = this.factoryWikiAjaxDispatcher.newInstance(properties).getInstance());
		managers.put(IPermissionManager.class,
				ServicesRefs.permissionManager = this.factoryPermissionManager.newInstance(properties).getInstance());
		managers.put(PageRenamer.class,
				ServicesRefs.pageRenamer = this.factoryPageRenamer.newInstance(properties).getInstance());
		managers.put(ProgressManager.class,
				ServicesRefs.progressManager = this.factoryProgressManager.newInstance(properties).getInstance());
		managers.put(AdminBeanManager.class,
				ServicesRefs.adminBeanManager = this.factoryAdminBeanManager.newInstance(properties).getInstance());
		managers.put(TemplateManager.class,
				ServicesRefs.templateManager = this.factoryTemplateManager.newInstance(properties).getInstance());
		managers.put(EditorManager.class,
				ServicesRefs.editorManager = this.factoryEditorManager.newInstance(properties).getInstance());
		managers.put(PluginManager.class,
				ServicesRefs.pluginManager = this.factoryPluginManager.newInstance(properties).getInstance());
		managers.put(InternationalizationManager.class,
				ServicesRefs.internationalizationManager = this.factoryInternationalizationManager
						.newInstance(properties).getInstance());
		managers.put(WorkflowManager.class,
				ServicesRefs.workflowManager = this.factoryWorkflowManager.newInstance(properties).getInstance());
		managers.put(URLConstructor.class,
				ServicesRefs.urlConstructor = this.factoryUrlConstructor.newInstance(properties).getInstance());
		managers.put(CommandResolver.class,
				ServicesRefs.commandResolver = this.factoryCommandResolver.newInstance(properties).getInstance());
		managers.put(VariableManager.class,
				ServicesRefs.variableManager = this.factoryVariableManager.newInstance(properties).getInstance());
		managers.put(SearchManager.class,
				ServicesRefs.searchManager = this.factorySearchManager.newInstance(properties).getInstance());
		managers.put(RenderingManager.class,
				ServicesRefs.renderingManager = this.factoryRenderingManager.newInstance(properties).getInstance());
		managers.put(AttachmentManager.class,
				ServicesRefs.attachmentManager = this.factoryAttachmentManager.newInstance(properties).getInstance());
		managers.put(UserManager.class,
				ServicesRefs.userManager = this.factoryUserManager.newInstance(properties).getInstance());
		managers.put(FilterManager.class,
				ServicesRefs.filterManager = this.factoryFilterManager.newInstance(properties).getInstance());
		managers.put(IIAuthenticationManager.class,
				ServicesRefs.authenticationManager = this.factoryAuthenticationManager.newInstance(properties)
						.getInstance());
		managers.put(AuthorizationManager.class, ServicesRefs.authorizationManager = this.factoryAuthorizationManager
				.newInstance(properties).getInstance());
		managers.put(IAuthorizer.class,
				ServicesRefs.groupManager = this.factoryGroupManager.newInstance(properties).getInstance());
		managers.put(PageManager.class,
				ServicesRefs.pageManager = this.factoryPageManager.newInstance(properties).getInstance());
		managers.put(DifferenceManager.class,
				ServicesRefs.differenceManager = this.factoryDifferenceManager.newInstance(properties).getInstance());
		managers.put(ReferenceManager.class,
				ServicesRefs.referenceManager = this.factoryReferenceManager.newInstance(properties).getInstance());
		managers.put(TasksManager.class,
				ServicesRefs.tasksManager = this.factoryTasksManager.newInstance(properties).getInstance());
		managers.put(AclManager.class,
				ServicesRefs.aclManager = this.factoryAclManager.newInstance(properties).getInstance());
		managers.put(IStorageCdo.class,
				ServicesRefs.storageCdo = this.factoryStorageCdo.newInstance(properties).getInstance());
		managers.put(ISessionMonitor.class,
				ServicesRefs.sessionMonitor = this.factorySessionMonitor.newInstance(properties).getInstance());

		// Обработка аннотаций @WikiServiceReference.
		for (Object serviceInstance : managers.values()) {
			for (Field field : serviceInstance.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(WikiServiceReference.class)) {
					try {
						Class<?> typeField = field.getType();
						Object targetService = managers.get(typeField);
						field.setAccessible(true);
						field.set(serviceInstance, targetService);
					} catch (Exception e) {
						// TODO:
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Deactivate
	protected void shutdownService() {
		//
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * It is called by {@link WikiServlet#destroy()}. When this method is called, it fires a
	 * "shutdown" WikiEngineEvent to all registered listeners.
	 */
	@Override
	public void shutdown() {
		fireEvent(WikiEngineEvent.SHUTDOWN);
		ServicesRefs.getFilterManager().destroy();
	}

	// -- service handling -----------------------------(end)--

	public static IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public static AclManager getAclManager() {
		return aclManager;
	}

	public static AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public static PageManager getPageManager() {
		return pageManager;
	}

	public static PageRenamer getPageRenamer() {
		return pageRenamer;
	}

	public static AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public static IAuthorizer getGroupManager() {
		return groupManager;
	}

	public static ProgressManager getProgressManager() {
		return progressManager;
	}

	public static UserManager getUserManager() {
		return userManager;
	}

	public static AdminBeanManager getAdminBeanManager() {
		return adminBeanManager;
	}

	public static IIAuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public static RenderingManager getRenderingManager() {
		return renderingManager;
	}

	public static ReferenceManager getReferenceManager() {
		return referenceManager;
	}

	public static VariableManager getVariableManager() {
		return variableManager;
	}

	public static DifferenceManager getDifferenceManager() {
		return differenceManager;
	}

	public static TemplateManager getTemplateManager() {
		return templateManager;
	}

	public static EditorManager getEditorManager() {
		return editorManager;
	}

	public static PluginManager getPluginManager() {
		return pluginManager;
	}

	public static FilterManager getFilterManager() {
		return filterManager;
	}

	public static SearchManager getSearchManager() {
		return searchManager;
	}

	public static CommandResolver getCommandResolver() {
		return commandResolver;
	}

	public static InternationalizationManager getInternationalizationManager() {
		return internationalizationManager;
	}

	public static WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	public static URLConstructor getUrlConstructor() {
		return urlConstructor;
	}

	public static RSSGenerator getRssGenerator() {
		return rssGenerator;
	}

	public static ISessionMonitor getSessionMonitor() {
		return sessionMonitor;
	}
	
	// =========================================================================

	private static final Logger log = Logger.getLogger(ServicesRefs.class);

	/**
	 * Store the file path to the basic URL. When we're not running as a servlet, it defaults to the
	 * user's current directory.
	 */
	private String m_rootPath = System.getProperty("user.dir");

	/**
	 * Store the ServletContext that we're in. This may be null if WikiEngine is not running inside
	 * a servlet container (i.e. when testing).
	 */
	//TODO: этот код устарел (в ElWiki инициализация относительно OSGi, а не сервлета. ServletContext==null).
	private ServletContext m_servletContext = null;

	/** The time when this engine was started. */
	private Date m_startTime;

	/** Each engine has their own application id. */
	private String m_appid = "";

	/** engine is up and running or not */
	private boolean m_isConfigured = false;

	/** Stores wikiengine attributes. */
	private Map<String, Object> m_attributes = new ConcurrentHashMap<>();

	/** Stores WikiEngine's associated managers- <interface, instance>. */
	protected Map<Class<?>, Object> managers = new ConcurrentHashMap<>();

	// == code =================================================================

	@SuppressWarnings("unchecked")
	private <T> T getService(Class<T> clazz) {
		T service = null;
		ServiceReference<?> ref = this.bundleContext.getServiceReference(clazz.getName());
		if (ref != null) {
			Object obj = this.bundleContext.getService(ref);
			if (clazz.isInstance(obj)) {
				service = (T) obj;
			}
		}
		return service;
	}

	/**
	 * Does all the real initialization.
	 */
	private void initialize() throws WikiException {
		m_startTime = new Date();

		log.info("*******************************************");
		log.info(Release.APPNAME + " " + Release.getVersionString() + " starting. Whee!");

//:FVK:		fireEvent(WikiEngineEvent.INITIALIZING); // begin initialization

		log.debug("Java version: " + System.getProperty("java.runtime.version"));
		log.debug("Java vendor: " + System.getProperty("java.vm.vendor"));
		log.debug("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " "
				+ System.getProperty("os.arch"));
		log.debug("Default server locale: " + Locale.getDefault());
		log.debug("Default server timezone: " + TimeZone.getDefault().getDisplayName(true, TimeZone.LONG));

		if (m_servletContext != null) {
			//TODO: этот код устарел (в ElWiki инициализация относительно OSGi, а не сервлета. ServletContext==null). 
			log.info("Servlet container: " + m_servletContext.getServerInfo());
			if (m_servletContext.getMajorVersion() < 3
					|| (m_servletContext.getMajorVersion() == 3 && m_servletContext.getMinorVersion() < 1)) {
				throw new InternalWikiException(
						"JSPWiki requires a container which supports at least version 3.1 of Servlet specification");
			}
		}

		log.debug("Configuring WikiEngine...");
		IPreferenceStore preferences = this.wikiConfiguration.getWikiPreferences();

		//
		// Initialize the important modules. Any exception thrown by the managers means that we will not
		// start up.
		//
		try {
			String def = "org.apache.wiki.auth.acl.AclManager"; // :FVK: ClassUtil.getMappedClass(
																// AclManager.class.getName() ).getName();
			final String aclClassName = TextUtil.getStringProperty(preferences, PROP_ACL_MANAGER_IMPL, def);

			// initService(CommandResolver.class);
			// initComponent( CommandResolver.class, this, props );

			/*final String urlConstructorClassName = TextUtil.getStringProperty( props, PROP_URLCONSTRUCTOR, "DefaultURLConstructor" );
			final Class< ? > urlclass; //:FVK: = ClassUtil.findClass( "org.apache.wiki.url", urlConstructorClassName );
			urlclass = DefaultURLConstructor.class;*/

			// initService(URLConstructor.class); // org.apache.wiki.url.DefaultURLConstructor
			// initComponent( urlclass.getName(), URLConstructor.class );

			// initService(PageManager.class);
			// initComponent( PageManager.class, this, props );

			// initService(PluginManager.class);
			// initComponent( PluginManager.class, this, props );

			// initService(DifferenceManager.class);
			// initComponent( DifferenceManager.class, this, props );

			// initService(AttachmentManager.class);
			// initComponent( AttachmentManager.class, this, props );

			// initService(VariableManager.class);
			// initComponent( VariableManager.class, props );

			// initService(SearchManager.class);
			// initComponent( SearchManager.class, this, props );

			// initService(IIAuthenticationManager.class);
			// initComponent( AuthenticationManager.class );

			// initService(AuthorizationManager.class);
			// initComponent( AuthorizationManager.class );

			/*:FVK: 
			initService(GroupManager.class);
			//initComponent( GroupManager.class );
			 */
			// initService(IAuthorizer.class);

//:FVK: отладить:
			// initService(EditorManager.class);
			// initComponent( EditorManager.class, this );

			// initService(ProgressManager.class);
			// initComponent( ProgressManager.class, this );

			// initService(AclManager.class);
			// initComponent( aclClassName, AclManager.class );

			// initService(WorkflowManager.class);
			// initComponent( WorkflowManager.class );

			// initService(TasksManager.class);
			// initComponent( TasksManager.class );

			// initService(InternationalizationManager.class);
			// initComponent( InternationalizationManager.class, this );

			// initService(TemplateManager.class);
			// initComponent( TemplateManager.class, this, props );

			// initService(FilterManager.class);
			// initComponent( FilterManager.class, this, props );

			// initService(AdminBeanManager.class);
			// initComponent( AdminBeanManager.class, this );

			// initService(PageRenamer.class);
			// initComponent( PageRenamer.class, this, props );

			// RenderingManager depends on FilterManager events.
			// initService(RenderingManager.class);
			// initComponent( RenderingManager.class );
//TODO
			// ReferenceManager has the side effect of loading all pages. Therefore after this point, all
			// page attributes are available.
			// initReferenceManager is indirectly using m_filterManager, therefore it has to be called after
			// it was initialized.
			initReferenceManager();

			// Hook the different manager routines into the system.
			ServicesRefs.getFilterManager().addPageFilter(ServicesRefs.getReferenceManager(), -1001);
			ServicesRefs.getFilterManager().addPageFilter(ServicesRefs.getSearchManager(), -1002);
		} catch (final RuntimeException e) {
			// RuntimeExceptions may occur here, even if they shouldn't.
			log.fatal("Failed to start managers.", e);
			throw new WikiException("Failed to start managers: " + e.getMessage(), e);
		}
//		catch (final ClassNotFoundException e) {
//			log.fatal("JSPWiki could not start, URLConstructor was not found: " + e.getMessage(), e);
//			throw new WikiException(e.getMessage(), e);
//		} catch (final InstantiationException e) {
//			log.fatal("JSPWiki could not start, URLConstructor could not be instantiated: " + e.getMessage(), e);
//			throw new WikiException(e.getMessage(), e);
//		} catch (final IllegalAccessException e) {
//			log.fatal("JSPWiki could not start, URLConstructor cannot be accessed: " + e.getMessage(), e);
//			throw new WikiException(e.getMessage(), e);
//		}
		catch (final Exception e) {
			// Final catch-all for everything
			log.fatal("JSPWiki could not start, due to an unknown exception when starting.", e);
			throw new WikiException("Failed to start. Caused by: " + e.getMessage()
					+ "; please check log files for better information.", e);
		}

		// Initialize the good-to-have-but-not-fatal modules.
		try {
			if (TextUtil.getBooleanProperty(preferences, RSSGenerator.PROP_GENERATE_RSS, false)) {
				initComponent(RSSGenerator.class, this, preferences);
			}
		} catch (final Exception e) {
			log.error("Unable to start RSS generator - JSPWiki will still work, but there will be no RSS feed.", e);
		}

		/*:FVK: WORKAROUND.
		final Map< String, String > extraComponents = ClassUtil.getExtraClassMappings();
		initExtraComponents( extraComponents );
		*/

//:FVK:		fireEvent(WikiEngineEvent.INITIALIZED); // initialization complete

		log.info("WikiEngine configured.");
		m_isConfigured = true;
	}

	<T> void initComponent(final Class<T> componentClass, final Object... initArgs) throws Exception {
		initComponent(componentClass.getName(), componentClass, initArgs);
	}

	/**
	 * :FVK: сделано для JSPwiki менеджеров. (и, возможно, каких-то еще компонентов).
	 * 
	 * @param <T>
	 * @param componentInitClass
	 * @param componentClass
	 * @param initArgs
	 * @throws Exception
	 */
	<T> void initComponent(final String componentInitClass, final Class<T> componentClass, final Object... initArgs)
			throws Exception {
		final T component;
		if (initArgs == null || initArgs.length == 0) {
			component = ClassUtil.getMappedObject(componentInitClass);
		} else {
			component = ClassUtil.getMappedObject(componentInitClass, initArgs);
		}
		managers.put(componentClass, component);
		if (Initializable.class.isAssignableFrom(componentClass)) {
			((Initializable) component).initialize(this);
		}
	}

	@Deprecated
	private <T> T initService(Class<T> clazz) throws Exception {
		try {
			T service = this.getService(clazz);
			Assert.isNotNull(service, ":FVK: Internal error - initService()");

			managers.put(clazz, service);
			if (service instanceof Initializable) {
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

	/** {@inheritDoc} */
	@Override
	public boolean isConfigured() {
		return m_isConfigured;
	}

	/**
	 * Initializes the reference manager. Scans all existing WikiPages for internal links and adds
	 * them to the ReferenceManager object.
	 *
	 * @throws WikiException If the reference manager initialization fails.
	 */
	public void initReferenceManager() throws WikiException {
		try {
			// Build a new manager with default key lists.
			if (ServicesRefs.getReferenceManager() == null) {
				final ArrayList<WikiPage> pages = new ArrayList<>();
				pages.addAll(getManager(PageManager.class).getAllPages());
				// :FVK: pages.addAll( getManager( AttachmentManager.class ).getAllAttachments() );

				ReferenceManager manager = getReferenceManager();
				if (manager != null)
					manager.initialize(pages);
				// initComponent( ReferenceManager.class, this );
				// getManager( ReferenceManager.class ).initialize( pages );
			}

		} catch (final ProviderException e) {
			log.fatal("PageProvider is unable to list pages: ", e);
		} catch (final Exception e) {
			throw new WikiException("Could not instantiate ReferenceManager: " + e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IPreferenceStore getWikiPreferences() {
		return this.wikiConfiguration.getWikiPreferences();
	}

	/** {@inheritDoc} */
	@Override
	public Date getStartTime() {
		return (Date) m_startTime.clone();
	}

	/** {@inheritDoc} */
	@Override
	public String getGlobalRSSURL() {
		final RSSGenerator rssGenerator = ServicesRefs.getRssGenerator();
		if (rssGenerator != null && rssGenerator.isEnabled()) {
			return this.wikiConfiguration.getBaseURL() + "/" + rssGenerator.getRssFile();
		}

		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String getURL(final String context, String pageName, final String params) {
		if (pageName == null) {
			pageName = this.wikiConfiguration.getFrontPage();
		}
		final URLConstructor urlConstructor = ServicesRefs.getUrlConstructor();
		return urlConstructor.makeURL(context, pageName, params);
	}

	public void setServletContext(ServletContext context) {
		this.m_servletContext = context;
		/*:FVK: System.err.println("---> setServletContext()");
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		for (int n = 1; n < 12; n++) {
			if (st.length > n) {
				StackTraceElement sTE = st[n];
				System.err.println("    " + sTE.getClassName() + "::" + sTE.getMethodName() + "()");
			}
		}*/
	}

	/** {@inheritDoc} */
	@Override
	public ServletContext getServletContext() {
		//TODO: этот код устарел (в ElWiki инициализация относительно OSGi, а не сервлета. ServletContext==null).
		return m_servletContext;
	}

	/** {@inheritDoc} */
	@Override
	public String getSpecialPageReference(final String original) {
		return ServicesRefs.getCommandResolver().getSpecialPageReference(original);
	}

	/** {@inheritDoc} */
	@Override
	public String getFinalPageName(final String page) throws ProviderException {
		return ServicesRefs.getCommandResolver().getFinalPageName(page);
	}

	/** {@inheritDoc} */
	@Override
	public Charset getContentEncoding() {
		return StandardCharsets.UTF_8; // :FVK: WORKAROUND.
	}

	/** {@inheritDoc} */
	@Override
	public String getRootPath() {
		return m_rootPath;
	}

	/** {@inheritDoc} */
	@Override
	public final synchronized void addWikiEventListener(final WikiEventListener listener) {
		WikiEventManager.addWikiEventListener(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public final synchronized void removeWikiEventListener(final WikiEventListener listener) {
		WikiEventManager.removeWikiEventListener(this, listener);
	}

	/**
	 * Fires a WikiEngineEvent to all registered listeners.
	 *
	 * @param type the event type
	 */
	protected final void fireEvent(final int type) {
		if (WikiEventManager.isListening(this)) {
			WikiEventManager.fireEvent(this, new WikiEngineEvent(this, type));
		}
	}

	/**
	 * Fires a WikiPageEvent to all registered listeners.
	 *
	 * @param type the event type
	 */
	protected final void firePageEvent(final int type, final String pageName) {
		if (WikiEventManager.isListening(this)) {
			WikiEventManager.fireEvent(this, new WikiPageEvent(this, type, pageName));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(final String key, final Object value) {
		m_attributes.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(final String key) {
		return (T) m_attributes.get(key);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T removeAttribute(final String key) {
		return (T) m_attributes.remove(key);
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
	////////////////////////////////////////////////////////////////////////////
	/// ? новое для ElWiki :FVK: /// static методы и др.

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	@NonNull
	public <T> @NonNull T getManager(Class<T> manager) {
		T result = (T) managers.entrySet().stream().filter(e -> manager.isAssignableFrom(e.getKey()))
				.map(Map.Entry::getValue).findFirst().orElse(null);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> getManagers(Class<T> manager) {
		return (List<T>) managers.entrySet().stream().filter(e -> manager.isAssignableFrom(e.getKey()))
				.map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/**
	 * Возвращает контекст Wiki для текущего процесса.
	 *
	 * @return контекста Wiki для текущего процесса.
	 */
	@Deprecated //:FVK: - результат вполне может быть NULL. (в RAP)
	public static Context getCurrentContext() {
		return thWikiContext.get();
	}

	/**
	 * Сохранение контекста Wiki для текущего процесса.
	 * :FVK: workaround.
	 * @param wikiContext
	 */
	@Deprecated
	public static void setCurrentContext(Context wikiContext) {
		thWikiContext.set(wikiContext);
	}

	@Deprecated
	public static void removeCurrentContext() {
		thWikiContext.remove();
	}

}
