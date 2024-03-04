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

import java.security.Permission;
import java.security.Principal;
import java.util.HashMap;
import java.util.PropertyPermission;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.ui.CommandResolver;
import org.apache.wiki.api.ui.PageCommand;
import org.apache.wiki.api.ui.WikiCommand;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.ui.Installer;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.WikiPrincipal;
import org.elwiki.permissions.AllPermission;
import org.elwiki_data.WikiPage;

/*TODO: удалить engine в аргументах конструкоров и т.д. */

/**
 * <p>
 * Provides state information throughout the processing of a page.</br>
 * A WikiContext is born when triggered filter before handle of servlet request. The JSPWiki
 * engine creates the new WikiContext, which basically holds information about the page, the
 * handling engine, and in which context (view, edit, etc) the call was done.
 * </p>
 * <p>
 * A WikiContext also provides request-specific variables, which can be used to communicate
 * between plugins on the same page, or between different instances of the same plugin. A
 * WikiContext variable is valid until the processing of the page has ended. For an example,
 * please see the Counter plugin.
 * </p>
 * <p>
 * When a WikiContext is created, it automatically associates a {@link WikiSessionImpl} object with
 * the user's HttpSession. The WikiSession contains information about the user's authentication
 * status, and is consulted by {@link #getCurrentUser()} object.
 * </p>
 * <p>
 * Do not cache the page object that you get from the WikiContext; always use getPage()!
 * </p>
 *
 * @see org.elwiki.plugins.CounterPlugin
 */
public class WikiContextImpl implements WikiContext, Command {

	private record PageInfo(WikiPage wikiPage, int pageVersion) {}

	private static final Logger log = Logger.getLogger(WikiContextImpl.class);

	private static final Permission DUMMY_PERMISSION = new PropertyPermission("os.name", "read");

	private Stack<PageInfo> stackPageInfo = new Stack<>();

	private Command m_command;
	private WikiPage m_page;
	private WikiPage m_realPage;
	private Engine m_engine; //TODO: удалить?.
	private String shape = "default";

	private HashMap<String, Object> m_variableMap = new HashMap<>();

	/**
	 * Stores the HttpServletRequest. May be null, if the request did not come from a servlet.
	 */
	protected HttpServletRequest m_request;

	private WikiSession m_session;
	final private IWikiConfiguration wikiConfiguration;
	private @NonNull GlobalPreferences globalPrefs;

	/** The page version in question. (or attachment version) */
	private int m_version = 0;

	/**
	 * Create a new WikiContext for the given WikiPage. Delegates to
	 * {@link #WikiContext(Engine, HttpServletRequest, WikiPage)}.
	 *
	 * @param engine The Engine that is handling the request.
	 * @param page   The WikiPage. If you want to create a WikiContext for an older version of a
	 *               page, you must use this constructor.
	 */
	public WikiContextImpl(final Engine engine, final WikiPage page) {
		this(engine, null, findCommand(engine, null, page));
	}

	/**
	 * Creates a new WikiContext for the given Engine, WikiPage and HttpServletRequest. This method
	 * simply looks up the appropriate Command using
	 * {@link #findCommand(Engine, HttpServletRequest, WikiPage)} and delegates to
	 * {@link #WikiContext(Engine, HttpServletRequest, Command)}.
	 *
	 * @param engine  The Engine that is handling the request
	 * @param request The HttpServletRequest that should be associated with this context. This
	 *                parameter may be <code>null</code>.
	 * @param page    The WikiPage. If you want to create a WikiContext for an older version of a
	 *                page, you must supply this parameter
	 */
	public WikiContextImpl(final Engine engine, final HttpServletRequest request, final WikiPage page) {
		this(engine, request, findCommand(engine, request, page));
	}

	/**
	 * Creates a new WikiContext from a supplied HTTP request, using a default wiki context.
	 *
	 * @param engine         The Engine that is handling the request
	 * @param request        the HTTP request
	 * @param requestContext the default context to use
	 * @see org.apache.wiki.api.ui.CommandResolver
	 * @see org.apache.wiki.api.core.Command
	 * @since 2.1.15.
	 */
	public WikiContextImpl(final Engine engine, final HttpServletRequest request, final String requestContext) {
		this(engine, request, engine.getManager(CommandResolver.class).findCommand(request, requestContext));
		if (!engine.isConfigured()) {
			throw new InternalWikiException(
					"Engine has not been properly started.  It is likely that the configuration is faulty.  Please check all logs for the possible reason.");
		}
	}

	/**
	 * <p>
	 * Creates a new WikiContext for the given Engine, Command and HttpServletRequest.
	 * </p>
	 * <p>
	 * This constructor will also look up the HttpSession associated with the request, and determine
	 * if a Session object is present. If not, a new one is created.
	 * </p>
	 * 
	 * @param engine  The Engine that is handling the request
	 * @param request The HttpServletRequest that should be associated with this context. This
	 *                parameter may be <code>null</code>.
	 * @param command the command
	 * @throws IllegalArgumentException if <code>engine</code> or <code>command</code> are
	 *                                  <code>null</code>
	 */
	public WikiContextImpl(final Engine engine, final HttpServletRequest request, final Command command)
			throws IllegalArgumentException {
		if (engine == null || command == null) {
			throw new IllegalArgumentException("Parameter engine and command must not be null.");
		}
		this.wikiConfiguration = engine.getWikiConfiguration();
		this.globalPrefs = engine.getManager(GlobalPreferences.class);

		m_engine = engine;
		m_request = request;
		ISessionMonitor sessionMonitor = engine.getManager(ISessionMonitor.class);
		m_session = sessionMonitor.getWikiSession(request);
		m_command = command;

		// If PageCommand, get the WikiPage
		if (command instanceof PageCommand) {
			m_page = (WikiPage) command.getTarget();
		}

		// If page not supplied, default to front page to avoid NPEs
		if (m_page == null) {
			PageManager pageManager = engine.getManager(PageManager.class);
			m_page = (WikiPage) pageManager.getPage(this.wikiConfiguration.getFrontPage());

			// Front page does not exist?
			if (m_page == null) {
				m_page = (WikiPage) Wiki.contents().page(this.wikiConfiguration.getFrontPage());
			}
		}

		m_realPage = m_page;

		// Special case: retarget any empty 'view' PageCommands to the front page
		if (PageCommand.VIEW.equals(command) && command.getTarget() == null) {
			m_command = command.targetedCommand(m_page);
		}

		// Debugging...
		if (log.isDebugEnabled()) {
			final HttpSession session = (request == null) ? null : request.getSession(false);
			final String sid = session == null ? "(null)" : session.getId();
			log.debug("Creating WikiContext for\n session ID=" + sid + ";\n target=" + getName() + ";\n command="
					+ getCommand());
		}

		// Set required version of the WikiPage.
		if (request != null) {
			String rqVersion = request.getParameter("version");
			if (rqVersion != null) {
				try {
					m_version = Integer.parseInt(rqVersion);
				} catch (Exception e) {
				}
			}
		}
		if (m_version == 0) {
			m_version = m_page.getLastVersion();
		}

		// Figure out what shape to use.
		setDefaultShape(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPageVersion() {
		return m_version;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.wiki.api.core.Command#getContentTemplate()
	 */
	@Override
	public String getContentTemplate() {
		return m_command.getContentTemplate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.wiki.api.core.Command#getJSP()
	 */
	@Override
	public String getJSP() {
		return m_command.getContentTemplate();
	}

	/**
	 * Sets a reference to the real page whose content is currently being rendered.
	 * <p>
	 * Sometimes you may want to render the page using some other page's context. In those cases, it
	 * is highly recommended that you set the pushRealPage() to point at the real page you are
	 * rendering. Please see InsertPageTag for an example.
	 * <p>
	 * Also, if your plugin e.g. does some variable setting, be aware that if it is embedded in the
	 * LeftMenu or some other page added with InsertPageTag, you should consider what you want to do
	 * - do you wish to really reference the "master" page or the included page.
	 *
	 * @param page The real page which is being rendered.
	 * @since 2.3.14
	 * @see org.apache.wiki.tags.InsertPageTag
	 */
	@Override
	public void pushRealPage(WikiPage page) {
		PageInfo pageInfo = new PageInfo(this.m_realPage, this.m_version);
		this.stackPageInfo.push(pageInfo);

		this.m_realPage = page;
		this.m_version = page.getLastVersion();
		updateCommand(m_command.getRequestContext());
	}
	
	@Override
	public void popRealPage() {
		if (this.stackPageInfo.size() > 0) {
			PageInfo pageInfo = this.stackPageInfo.pop();
			this.m_realPage = pageInfo.wikiPage;
			this.m_version = pageInfo.pageVersion;
		}
	}

	/** {@inheritDoc} */
	@Override
	public WikiPage getPageById(String pageId) throws ProviderException {
		WikiPage wikiPage = null;
		wikiPage = this.m_engine.getPageById(pageId);
		return wikiPage;
	}

	/**
	 * Gets a reference to the real page whose content is currently being rendered. If your plugin
	 * e.g. does some variable setting, be aware that if it is embedded in the LeftMenu or some
	 * other page added with InsertPageTag, you should consider what you want to do - do you wish to
	 * really reference the "master" page or the included page.
	 * <p>
	 * For example, in the default template, there is a page called "LeftMenu". Whenever you access
	 * a page, e.g. "Main", the master page will be Main, and that's what the getPage() will return
	 * - regardless of whether your plugin resides on the LeftMenu or on the Main page. However,
	 * getRealPage() will return "LeftMenu".
	 *
	 * @return A reference to the real page.
	 * @see org.apache.wiki.tags.InsertPageTag
	 * @see org.apache.wiki.parser.JSPWikiMarkupParser
	 */
	@Override
	public WikiPage getRealPage() {
		return m_realPage;
	}

	@Override
	public String getRedirectURL() {
		final String pagename = m_page.getName();
		String redirectURL = null; //:FVK: WikiEngine.getCommandResolver().getSpecialPageReference(pagename);
		if (redirectURL == null) {
			final String alias = m_page.getAlias();
			/* TODO: :FVK: в `if` - добавил `&& !alias.isEmpty()` - чтоб не рестартовал вход через Wiki.jsp, без указания страницы. */
			if (alias != null && !alias.isEmpty()) {
				redirectURL = getViewURL(alias);
			} else {
				redirectURL = m_page.getRedirect();
			}
		}

		return (redirectURL == null || redirectURL.isEmpty()) ? null : redirectURL;
	}

	/** {@inheritDoc} */
	@Override
	public IWikiConfiguration getConfiguration() {
		return this.wikiConfiguration;
	}

	/** {@inheritDoc} */
	@Override
	public Engine getEngine() {
		return this.m_engine;
	}

	/**
	 * Returns the page that is being handled.
	 *
	 * @return the page which was fetched.
	 */
	@Override
	public WikiPage getPage() {
		return m_page;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getPageName() {
		String pageName = null;
		try {
			if (m_page != null) {
				pageName = m_page.getName();
			}
		} catch (Exception ex) {
			//:FVK: unexpected error.
		}

		if (pageName == null) {
			pageName = "System Info"; //:FVK: workaround - name of "SystemInfo" page.
		}

		return pageName;
	}

	@Override
	public String getPageId() {
		String pageId = null;
		try {
			if (m_page != null) {
				pageId = m_page.getId();
			} else {
				pageId = getEngine().getManager(PageManager.class).getMainPageId();
			}
		} catch (Exception ex) {
			//:FVK: unexpected error.
			pageId = "w1"; //:FVK: workaround - id of "SystemInfo" page.
		}

		return pageId;
	}

	/**
	 * Sets the page that is being handled.
	 *
	 * @param page The wikipage
	 * @since 2.1.37.
	 */
	@Override
	public void setPage(final WikiPage page) {
		m_page = (WikiPage) page;
		updateCommand(m_command.getRequestContext());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.wiki.api.core.Command#getRequestContext()
	 */
	@Override
	public String getRequestContext() {
		return m_command.getRequestContext();
	}

	/**
	 * Sets the request context. See above for the different request contexts (VIEW, EDIT, etc.)
	 *
	 * @param arg The request context (one of the predefined contexts.)
	 */
	@Override
	public void setRequestContext(final String arg) {
		updateCommand(arg);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.wiki.api.core.Command#getTarget()
	 */
	@Override
	public Object getTarget() {
		return m_command.getTarget();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.wiki.api.core.Command#getURLPattern()
	 */
	@Override
	public String getURLPattern() {
		return m_command.getURLPattern();
	}

	/**
	 * Gets a previously set variable.
	 *
	 * @param key The variable name.
	 * @return The variable contents.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getVariable(final String key) {
		return (T) m_variableMap.get(key);
	}

	/**
	 * Sets a variable. The variable is valid while the WikiContext is valid, i.e. while page
	 * processing continues. The variable data is discarded once the page processing is finished.
	 *
	 * @param key  The variable name.
	 * @param data The variable value.
	 */
	@Override
	public void setVariable(final String key, final Object data) {
		m_variableMap.put(key, data);
		updateCommand(m_command.getRequestContext());
	}

	/**
	 * This is just a simple helper method which will first check the context if there is already an
	 * override in place, and if there is not, it will then check the given properties.
	 *
	 * @param key      What key are we searching for?
	 * @param defValue Default value for the boolean
	 * @return {@code true} or {@code false}.
	 */
	@Override
	public boolean getBooleanWikiProperty(String key, boolean defValue) {
		var value = BooleanUtils.toBooleanObject((String) getVariable(key));

		return (value != null) ? value : wikiConfiguration.getBooleanProperty(key, defValue);
	}

	/**
	 * This method will safely return any HTTP parameters that might have been defined. You should
	 * use this method instead of peeking directly into the result of getHttpRequest(), since this
	 * method is smart enough to do all of the right things, figure out UTF-8 encoded parameters,
	 * etc.
	 *
	 * @since 2.0.13.
	 * @param paramName Parameter name to look for.
	 * @return HTTP parameter, or null, if no such parameter existed.
	 */
	@Override
	public String getHttpParameter(final String paramName) {
		String result = null;
		if (m_request != null) {
			result = m_request.getParameter(paramName);
		}

		return result;
	}

	/**
	 * If the request did originate from a HTTP request, then the HTTP request can be fetched here.
	 * However, it the request did NOT originate from a HTTP request, then this method will return
	 * null, and YOU SHOULD CHECK FOR IT!
	 *
	 * @return Null, if no HTTP request was done.
	 * @since 2.0.13.
	 */
	@Override
	public HttpServletRequest getHttpRequest() {
		return m_request;
	}

	/**
	 * Sets the template to be used for this request.
	 *
	 * @param dir The shape name
	 * @since 2.1.15.
	 */
	@Override
	public void setShape(String dir) {
		this.shape = dir;
	}

	/**
	 * Returns the target of this wiki context: a page, group name or JSP. If the associated Command
	 * is a PageCommand, this method returns the page's name. Otherwise, this method delegates to
	 * the associated Command's {@link org.apache.wiki.api.core.Command#getName()} method. Calling
	 * classes can rely on the results of this method for looking up canonically-correct page or
	 * group names. Because it does not automatically assume that the wiki context is a PageCommand,
	 * calling this method is inherently safer than calling {@code getPage().getName()}.
	 *
	 * @return the name of the target of this wiki context
	 * @see org.apache.wiki.api.ui.PageCommand#getName()
	 * @see org.apache.wiki.api.ui.GroupCommand#getName()
	 */
	@Override
	public String getName() {
		if (m_command instanceof PageCommand) {
			return m_page != null ? m_page.getName() : "<no page>";
		}
		return m_command.getName();
	}

	/**
	 * Gets the template that is to be used throughout this request.
	 *
	 * @since 2.1.15.
	 * @return template name
	 */
	@Override
	public String getShape() {
		return this.shape;
	}

	/**
	 * Convenience method that gets the current user. Delegates the lookup to the WikiSession
	 * associated with this WikiContect. May return null, in case the current user has not yet been
	 * determined; or this is an internal system. If the WikiSession has not been set,
	 * <em>always</em> returns null.
	 *
	 * @return The current user; or maybe null in case of internal calls.
	 */
	@Override
	public Principal getCurrentUser() {
		if (m_session == null) {
			// This shouldn't happen, really...
			return WikiPrincipal.GUEST;
		}
		return m_session.getUserPrincipal();
	}

	/**
	 * Creates an URL for the given request context.
	 *
	 * @param context e.g. WikiContext.PAGE_EDIT
	 * @param page    The page to which to link
	 * @return An URL to the page, honours the absolute/relative setting in preferences.ini
	 */
	@Override
	public String getURL(final String context, final String page) {
		return getURL(context, page, null);
	}

	/**
	 * Returns an URL from a page. It this WikiContext instance was constructed with an actual
	 * HttpServletRequest, we will attempt to construct the URL using HttpUtil, which preserves the
	 * HTTPS portion if it was used.
	 *
	 * @param context The request context (e.g. WikiContext.ATTACHMENT_UPLOAD)
	 * @param page    The page to which to link
	 * @param params  A list of parameters, separated with "&amp;"
	 *
	 * @return An URL to the given context and page.
	 */
	@Override
	public String getURL(final String context, final String page, final String params) {
		// FIXME: is rather slow
		return m_engine.getURL(context, page, params);
	}

	/**
	 * Returns the Command associated with this WikiContext.
	 *
	 * @return the command
	 */
	public Command getCommand() {
		return m_command;
	}

	/**
	 * Returns a shallow clone of the WikiContext.
	 *
	 * @since 2.1.37.
	 * @return A shallow clone of the WikiContext
	 */
	@Override
	public WikiContextImpl clone() {
		try {
			// super.clone() must always be called to make sure that inherited objects
			// get the right type
			final WikiContextImpl copy = (WikiContextImpl) super.clone();

			copy.m_engine = m_engine;
			copy.m_command = m_command;

			copy.shape = this.shape;
			copy.m_variableMap = m_variableMap;
			copy.m_request = m_request;
			copy.m_session = m_session;
			copy.m_page = m_page;
			copy.m_realPage = m_realPage;
			return copy;
		} catch (final CloneNotSupportedException e) {
		} // Never happens

		return null;
	}

	/**
	 * Creates a deep clone of the WikiContext. This is useful when you want to be sure that you
	 * don't accidentally mess with page attributes, etc.
	 *
	 * @since 2.8.0
	 * @return A deep clone of the WikiContext.
	 */
	@SuppressWarnings("unchecked")
	public WikiContextImpl deepClone() {
		try {
			// super.clone() must always be called to make sure that inherited objects
			// get the right type
			final WikiContextImpl copy = (WikiContextImpl) super.clone();

			//  No need to deep clone these
			copy.m_engine = m_engine;
			copy.m_command = m_command; // Static structure

			copy.shape = this.shape;
			copy.m_variableMap = (HashMap<String, Object>) m_variableMap.clone();
			copy.m_request = m_request;
			copy.m_session = m_session;
			/*:FVK:
			copy.m_page        = m_page.clone();
			copy.m_realPage    = m_realPage.clone();
			*/
			return copy;
		} catch (final CloneNotSupportedException e) {
		} // Never happens

		return null;
	}

	/**
	 * Returns the Session associated with the context. This method is guaranteed to always return a
	 * valid Session. If this context was constructed without an associated HttpServletRequest, it
	 * will return {@link org.apache.api.wiki.WikiSessionImpl#guestSession(Engine)}.
	 *
	 * @return The Session associated with this context.
	 */
	@Override
	public WikiSessionImpl getWikiSession() {
		return (WikiSessionImpl) m_session;
	}

	/**
	 * Returns the permission required to successfully execute this context. For example, the a wiki
	 * context of VIEW for a certain page means that the PagePermission "view" is required for the
	 * page. In some cases, no particular permission is required, in which case a dummy permission
	 * will be returned ({@link java.util.PropertyPermission}<code> "os.name", "read"</code>). This
	 * method is guaranteed to always return a valid, non-null permission.
	 *
	 * @return the permission
	 * @since 2.4
	 */
	@Override
	public Permission requiredPermission() {
		// This is a filthy rotten hack -- absolutely putrid
		if (WikiCommand.INSTALL.equals(m_command)) {
			// See if admin users exists
			try {
				final AccountRegistry accountRegistry = this.m_engine.getManager(AccountRegistry.class);
				accountRegistry.findByLoginName(Installer.ADMIN_ID);
			} catch (final NoSuchPrincipalException e) {
				return DUMMY_PERMISSION;
			}
			return new AllPermission(globalPrefs.getApplicationName(), null);
		}

		// TODO: we should really break the contract so that this
		// method returns null, but until then we will use this hack
		if (m_command.requiredPermission() == null) {
			return DUMMY_PERMISSION;
		}

		return m_command.requiredPermission();
	}

	/**
	 * Associates a target with the current Command and returns the new targeted Command. If the
	 * Command associated with this WikiContext is already "targeted", it is returned instead.
	 *
	 * @see org.apache.wiki.api.core.Command#targetedCommand(java.lang.Object)
	 *
	 *      {@inheritDoc}
	 */
	@Override
	public Command targetedCommand(final Object target) {
		if (m_command.getTarget() == null) {
			return m_command.targetedCommand(target);
		}
		return m_command;
	}

	/**
	 * Returns true, if the current user has administrative permissions (i.e. the omnipotent
	 * AllPermission).
	 *
	 * @since 2.4.46
	 * @return true, if the user has all permissions.
	 */
	@Override
	public boolean hasAdminPermissions() {
		AuthorizationManager authorizationManager = this.m_engine.getManager(AuthorizationManager.class);
		return authorizationManager.checkPermission(getWikiSession(),
				new AllPermission(globalPrefs.getApplicationName(), null));
	}

	/**
	 * Figures out which template a new WikiContext should be using.
	 * 
	 * @param request the HTTP request
	 */
	protected void setDefaultShape(final HttpServletRequest request) {
		final String defaultTemplate = m_engine.getManager(GlobalPreferences.class).getTemplateDir();

		//:FVK: workaround - assign admin shape.
		if (m_command != null) {
			String currentContext = m_command.getContextCmd().getRequestContext();
			if (WikiContext.WIKI_ADMIN.equals(currentContext)) {
				setShape("admin");
				return;
			} else if (WikiContext.WIKI_SECURE.equals(currentContext)) {
				setShape("security");
				return;
			}
		}

		//  Figure out which shape we should be using for this page.
		String shape = null;
		if (request != null) {
			String value = request.getParameter("shape");
			if (value != null && WikiContext.allowedShapes.contains(value)) {
				shape = value;
			}
		}

		// If request doesn't supply the value, extract from wiki page
		if (shape == null) {
			final WikiPage page = getPage();
			if (page != null) {
				shape = null; //:FVK: page.getAttribute( Engine.PROP_TEMPLATEDIR );
			}
		}

		// If something over-wrote the default, set the new value.
		if (shape != null) {
			setShape(shape);
		} else {
			setShape(defaultTemplate);
		}
	}

	/**
	 * Looks up and returns a PageCommand based on a supplied WikiPage and HTTP request. First, the
	 * appropriate Command is obtained by examining the HTTP request; the default is
	 * {@link ContextEnum#PAGE_VIEW}. If the Command is a PageCommand (and it should be, in most
	 * cases), a targeted Command is created using the (non-<code>null</code>) WikiPage as target.
	 *
	 * @param engine  the wiki engine
	 * @param request the HTTP request
	 * @param page    the wiki page
	 * @return the correct command
	 */
	protected static Command findCommand(final Engine engine, final HttpServletRequest request, final WikiPage page) {
		final String defaultContext = ContextEnum.PAGE_VIEW.getRequestContext();
		CommandResolver commandResolver = engine.getManager(CommandResolver.class);
		Command command = commandResolver.findCommand(request, defaultContext);
		if (command instanceof PageCommand && page != null) {
			command = command.targetedCommand(page);
		}
		return command;
	}

	/**
	 * Protected method that updates the internally cached Command. Will always be called when the
	 * page name, request context, or variable changes.
	 *
	 * @param requestContext the desired request context
	 * @since 2.4
	 */
	protected void updateCommand(final String requestContext) {
		if (requestContext == null) {
			m_command = PageCommand.NONE;
		} else {
			CommandResolver commandResolver = this.m_engine.getManager(CommandResolver.class);
			m_command = commandResolver.findCommand(m_request, requestContext);
		}

		if (m_command instanceof PageCommand && m_page != null) {
			m_command = m_command.targetedCommand(m_page);
		}
	}

	@Override
	public ContextEnum getContextCmd() {
		return this.m_command.getContextCmd();
	}

	@Override
	public String toString() {
		return (this.m_command != null) ? this.m_command.getContextCmd().getRequestContext() : super.toString();
	}
	
}
