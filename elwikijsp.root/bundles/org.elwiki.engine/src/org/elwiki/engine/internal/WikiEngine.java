package org.elwiki.engine.internal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.Release;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.api.rss.RssGenerator;
import org.apache.wiki.api.search.SearchManager;
import org.apache.wiki.api.ui.CommandResolver;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.url0.URLConstructor;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.IWikiPreferencesConstants;
import org.elwiki.api.component.WikiManager;
import org.elwiki.api.event.WikiEngineEventTopic;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki_data.WikiPage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

//@formatter:off
@Component(
	name = "elwiki.Engine",
	service = Engine.class,
	scope = ServiceScope.SINGLETON,
	immediate = true)
//@formatter:on
public class WikiEngine implements Engine {

	private static final Logger log = Logger.getLogger(WikiEngine.class);

	// -- OSGi service handling ----------------------(start)--

	private BundleContext bundleContext;

	/** Custimized ServiceTracker of {@link Session} component. */
	private ServiceTracker<?, ?> sessionServiceTracker;

	@Reference
	EventAdmin eventAdmin;

	/** Stores ElWiki configuration. */
	@Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
	private IWikiConfiguration wikiConfiguration;

	@Activate
	protected void startupService(ComponentContext componentContext) throws WikiException {
		this.bundleContext = componentContext.getBundleContext();
		createServiceTracker(bundleContext);

		ServiceReference<?>[] refs = null;
		try {
			refs = bundleContext.getAllServiceReferences(WikiManager.class.getName(), null);
		} catch (InvalidSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int counterOfRegistered = 0;
		for (ServiceReference<?> ref : refs) {
			@SuppressWarnings("unchecked")
			WikiManager service = bundleContext.getService((ServiceReference<WikiManager>) ref);
			Class<?> clazz = service.getClass();
			counterOfRegistered++;
			for (Class<?> iface : clazz.getInterfaces()) {
				// check that super interface is WikiManager.
				boolean isWikiManager = Arrays.stream(iface.getInterfaces()).anyMatch(WikiManager.class::equals);
				if (isWikiManager) {
					managers.put(iface, service);
					// log.debug(" ~~ clazz: " + clazz.getSimpleName() + " instance of " + iface.getSimpleName());
				}
			}
		}
		log.debug(" ~~ Registered (" + counterOfRegistered + ") ElWiki components.");

		/* Processing the @WikiServiceReference annotation for ElWiki components fields.
		 */
		for (Object serviceInstance : managers.values()) {
			initialiseAnnotatedFileds(serviceInstance);
		}

		try {
			initialize();
			log.info("Root path for this Wiki is: '" + m_rootPath + "'");
		} catch (final Exception e) {
			log.error("Failer WikiEngine activate. Intialization stage errored.", e);
			//:FVK: final String msg = Release.APPNAME + ": Unable to initialise service. " + e.getMessage();
			//throw new WikiException(msg, e);
		}
	}

	@Deactivate
	protected void shutdown() {
		//
	}

	/**
	 * Initialise fields with @WikiServiceReference annotation.
	 * 
	 * @param serviceInstance
	 */
	private void initialiseAnnotatedFileds(Object serviceInstance) {
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

	/**
	 * Creates ServiceTracker for tracking component of Session type.
	 * 
	 * @param bundleContext
	 */
	private void createServiceTracker(BundleContext bundleContext) {
		Filter filter = null;
		try {
			filter = bundleContext.createFilter(
					"(&(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + Session.class.getName() + "))");
		} catch (InvalidSyntaxException e) {
			// should never happen.
		}
		ServiceTrackerCustomizer<Session, ServiceReference<Session>> stCustomizer = createStCustomizer();
		this.sessionServiceTracker = new ServiceTracker<Session, ServiceReference<Session>>(bundleContext, filter,
				stCustomizer);
		this.sessionServiceTracker.open(true);
	}

	/**
	 * Returns customized ServiceTracker for tracking component of Session type.
	 * 
	 * @return ServiceTracker for tracking component of Session type.
	 */
	private ServiceTrackerCustomizer<Session, ServiceReference<Session>> createStCustomizer() {
		return new ServiceTrackerCustomizer<Session, ServiceReference<Session>>() {

			@Override
			public ServiceReference<Session> addingService(ServiceReference<Session> serviceRef) {
				//for(String key:reference.getPropertyKeys()) {}
				log.debug("~~ ~~ adding service: " + serviceRef);
				Session serviceInstance = bundleContext.getService((ServiceReference<Session>) serviceRef);
				initialiseAnnotatedFileds(serviceInstance);
				return null;
			}

			@Override
			public void modifiedService(ServiceReference<Session> reference, ServiceReference<Session> service) {
				log.debug("~~ ~~ modified service");
			}

			@Override
			public void removedService(ServiceReference<Session> reference, ServiceReference<Session> service) {
				log.debug("~~ ~~ removed service");
			}
		};
	}

	// -- OSGi service handling ------------------------(end)--

	/**
	 * Store the file path to the basic URL. When we're not running as a servlet, it defaults to the
	 * user's current directory.
	 */
	private String m_rootPath = System.getProperty("user.dir");

	/**
	 * Store the ServletContext that we're in. This may be null if WikiEngine is not running inside a
	 * servlet container (i.e. when testing).
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
	protected Map<Class<?>, Object> managers = new ConcurrentHashMap<>() {
		private static final long serialVersionUID = 8475550897265370262L;
		{
			put(Engine.class, WikiEngine.this);
		}
	};

	/** If true, uses UTF8 encoding for all data */
	private boolean isUtf8Encoding;

	// =========================================================================

	/**
	 * Does all the real initialization.
	 */
	private void initialize() throws WikiException {
		m_startTime = new Date();

		log.info("** initialization ** WikiEngine **");
		log.info(Release.APPNAME + " " + Release.getVersionString() + " starting. Whee!");

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

		log.debug("◄►initialization◄► STAGE ONE.");
		
		//workaround - added flag "isUtf8Encoding".
		GlobalPreferences globalPrefs = getManager(GlobalPreferences.class);
		isUtf8Encoding = StandardCharsets.UTF_8.name()
				.equalsIgnoreCase(globalPrefs.getPreference(IWikiPreferencesConstants.PROP_ENCODING, String.class));

		eventAdmin.sendEvent(new Event(WikiEngineEventTopic.TOPIC_ENGINE_INIT_STAGE_ONE, Collections.emptyMap()));

		Set<Object> managers = new HashSet<>(this.managers.values());
		for (Object managerInstance : managers) {
			if (managerInstance instanceof WikiManager wikiManager) {
				try {
					wikiManager.initialize();
				} catch (Exception e) {
					log.error("Failed intialization of " + managerInstance.getClass().getSimpleName(), e);
				}
			}
		}

		log.debug("◄►initialization◄► STAGE TWO.");
		eventAdmin.sendEvent(new Event(WikiEngineEventTopic.TOPIC_ENGINE_INIT_STAGE_TWO, Collections.emptyMap()));

		//
		// Initialize the important modules. Any exception thrown by the managers means that we will not
		// start up.
		//
		try {
			String def = "org.apache.wiki.auth.acl.AclManager"; // :FVK: ClassUtil.getMappedClass(
																// AclManager.class.getName() ).getName();
			final String aclClassName = wikiConfiguration.getStringProperty(PROP_ACL_MANAGER_IMPL, def);

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

			// Hook the different manager routines into the system.
			FilterManager filterManager = this.getManager(FilterManager.class);
			filterManager.addPageFilter(this.getManager(ReferenceManager.class), -1001);
			filterManager.addPageFilter(this.getManager(SearchManager.class), -1002);
		} catch (final RuntimeException e) {
			// RuntimeExceptions may occur here, even if they shouldn't.
			log.fatal("Failed to start managers.", e);
			throw new WikiException("Failed to start managers: " + e.getMessage(), e);
		} catch (final Exception e) {
			// Final catch-all for everything
			log.fatal("ElWiki could not start, due to an unknown exception when starting.", e);
			throw new WikiException("Failed to start. Caused by: " + e.getMessage()
					+ "; please check log files for better information.", e);
		}

		log.info("◄►initialization◄► DONE.");
		m_isConfigured = true;
	}

	/** {@inheritDoc} */
	@Override
	@Deprecated
	public boolean isConfigured() {
		return m_isConfigured;
	}

	/** {@inheritDoc} */
	@Override
	public Date getStartTime() {
		return (Date) m_startTime.clone();
	}

	/** {@inheritDoc} */
	@Override
	public String getGlobalRSSURL() {
		final RssGenerator rssGenerator = this.getManager(RssGenerator.class);
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
		final URLConstructor urlConstructor = this.getManager(URLConstructor.class);
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
	public String getFinalPageName(final String page) throws ProviderException {
		return this.getManager(CommandResolver.class).getFinalPageName(page);
	}

	/** {@inheritDoc} */
	@Override
	public Charset getContentEncoding() {
		GlobalPreferences globalPrefs = getManager(GlobalPreferences.class);
		String wiki_encoding = globalPrefs.getPreference(IWikiPreferencesConstants.PROP_ENCODING, String.class);
		return Charset.forName(wiki_encoding);
	}

	/** {@inheritDoc} */
	@Override
	public String encodeName(String pagename) throws IOException {
		try {
			return URLEncoder.encode(pagename, isUtf8Encoding ? "UTF-8" : "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new IOException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
		}
	}

	/** {@inheritDoc} */
	@Override
	public String decodeName(String pagerequest) throws IOException {
		try {
			return URLDecoder.decode(pagerequest, isUtf8Encoding ? "UTF-8" : "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new IOException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public String getRootPath() {
		return m_rootPath;
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

	@Deprecated
	@Override
	public WikiPage getPageById(String pageId) throws ProviderException {
		WikiPage wikiPage;
		PageManager pageManager = this.getManager(PageManager.class);
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

}
