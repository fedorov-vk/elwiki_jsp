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
package org.elwiki.authorize.internal.services;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.AuthenticationManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.util.TimedCounterList;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.Authorizer;
import org.elwiki.api.authorization.WebAuthorizer;
import org.elwiki.api.component.WikiComponent;
import org.elwiki.api.event.WikiEvent;
import org.elwiki.api.event.LoginEvent;
import org.elwiki.authorize.internal.authorizer.WebContainerAuthorizer;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.authorize.login.AccountRegistryLoginModule;
import org.elwiki.authorize.login.AnonymousLoginModule;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki.authorize.login.CookieAuthenticationLoginModule;
import org.elwiki.authorize.login.WebContainerCallbackHandler;
import org.elwiki.authorize.login.WebContainerLoginModule;
import org.elwiki.authorize.login.WikiCallbackHandler;
import org.elwiki.configuration.IWikiConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.service.useradmin.UserAdmin;

/**
 * Default implementation for {@link AuthenticationManager}
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultAuthenticationManager",
	service = { AuthenticationManager.class, WikiComponent.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultAuthenticationManager implements AuthenticationManager, WikiComponent, EventHandler {

	private static final Logger log = Logger.getLogger(DefaultAuthenticationManager.class);

	/** How many milliseconds the logins are stored before they're cleaned away. */
	private static final long LASTLOGINS_CLEANUP_TIME = 10 * 60 * 1_000L; // Ten minutes

	private static final long MAX_LOGIN_DELAY = 20 * 1_000L; // 20 seconds

	/** Class (of type LoginModule) to use for custom authentication. */
	protected Class<? extends LoginModule> m_loginModuleClass = AccountRegistryLoginModule.class;

	/**
	 * Options passed to {@link LoginModule#initialize(Subject, CallbackHandler, Map, Map)}; initialized
	 * by {@link #initialize(Engine, Properties)}.
	 */
	protected Map<String, String> m_loginModuleOptions = new HashMap<>();

	/** Keeps a list of the usernames who have attempted a login recently. */
	private TimedCounterList<String> m_lastLoginAttempts = new TimedCounterList<>();

	///////////////////////////////////////////////////////////////////////////

	private static final String ID_EXTENSION_LOGIN_MODULE = "loginModule";

	private final Map<String, Class<? extends LoginModule>> loginModuleClasses = new HashMap<>();

	/** The default {@link LoginModule} class to use for custom authentication. */
	private static final Class<? extends LoginModule> DEFAULT_LOGIN_MODULE_CLASS = AccountRegistryLoginModule.class;

	/** Class (of type LoginModule) to use for custom authentication. */
	protected Class<? extends LoginModule> loginModuleClass;

	private BundleContext bundleContext;

	// -- OSGi service handling ----------------------(start)--

	@Reference
	protected UserAdmin userAdminService;

	@Reference
	protected EventAdmin eventAdmin;

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private Engine m_engine = null;

	@WikiServiceReference
	private AuthorizationManager authorizationManager;

	@WikiServiceReference
	private ISessionMonitor sessionMonitor;

	@Activate
	protected void startup(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// Look up the LoginModule class
		String loginModuleId = getPreference(AuthenticationManager.Prefs.LOGIN_MODULE_ID, String.class);
		loginModuleClass = getLoginModule(loginModuleId);
		if (loginModuleClass == null) {
			loginModuleClass = DEFAULT_LOGIN_MODULE_CLASS;
		}

		// Initialize the LoginModule options
		initLoginModuleOptions(wikiConfiguration.getWikiPreferences());
	}

	/**
	 * Initializes the options Map supplied to the configured LoginModule every time it is invoked. The
	 * properties and values extracted from <code>preferences.ini</code> are of the form
	 * <code>jspwiki.loginModule.options.<var>param</var> = <var>value</var>, where <var>param</var> is
	 * the key name, and <var>value</var> is the value.
	 *
	 * @param props the properties used to initialize JSPWiki
	 * @throws IllegalArgumentException if any of the keys are duplicated
	 */
	private void initLoginModuleOptions(IPreferenceStore props) {
		/*:FVK:
		for( final Object key : props.keySet() ) {
		    final String propName = key.toString();
		    if( propName.startsWith( PREFIX_LOGIN_MODULE_OPTIONS ) ) {
		        // Extract the option name and value
		        final String optionKey = propName.substring( PREFIX_LOGIN_MODULE_OPTIONS.length() ).trim();
		        if( optionKey.length() > 0 ) {
		            final String optionValue = props.getProperty( propName );
		
		            // Make sure the key is unique before stashing the key/value pair
		            if ( m_loginModuleOptions.containsKey( optionKey ) ) {
		                throw new IllegalArgumentException( "JAAS LoginModule key " + propName + " cannot be specified twice!" );
		            }
		            m_loginModuleOptions.put( optionKey, optionValue );
		        }
		    }
		}
		*/
	}

	// -- OSGi service handling ------------------------(end)--

	/**
	 * Looks up the LoginModule class, via extension point "org.elwiki.authorize.loginModule".
	 *
	 * @param loginModuleId
	 * @return
	 * @throws WikiException
	 */
	@Override
	public Class<? extends LoginModule> getLoginModule(String loginModuleId) throws WikiException {
		//
		// Load an Authorizer from Equinox extension "org.elwiki.authorize.loginModule". 
		//
		String namespace = AuthorizePluginActivator.getDefault().getBundle().getSymbolicName();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep;

		ep = registry.getExtensionPoint(namespace, ID_EXTENSION_LOGIN_MODULE);
		if (ep != null) {
			for (IConfigurationElement el : ep.getConfigurationElements()) {
				String contributorName = el.getContributor().getName();
				String className = el.getAttribute("class");
				String wikiPluginId = el.getAttribute("id");
				try {
					final Bundle bundle = Platform.getBundle(contributorName);
					Class<?> clazz = bundle.loadClass(className);
					try {
						Class<? extends LoginModule> cl = clazz.asSubclass(LoginModule.class);
						this.loginModuleClasses.put(wikiPluginId, (Class<? extends LoginModule>) cl);
					} catch (ClassCastException e) {
						log.fatal("LoginModule " + className + " is not extends javax LoginModule interface.", e);
						throw new WikiException(
								"LoginModule " + className + " is not extends javax LoginModule interface.", e);
					}
				} catch (ClassNotFoundException e) {
					log.fatal("LoginModule " + className + " cannot be found.", e);
					throw new WikiException("LoginModule " + className + " cannot be found.", e);
				}
			}
		}

		return this.loginModuleClasses.get(loginModuleId);
	}

	@Override
	public BundleContext getBundleContext() {
		return this.bundleContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAllowsCookieAssertions() {
		return getPreference(AuthenticationManager.Prefs.ALLOW_COOKIE_ASSERTIONS, Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAllowsCookieAuthentication() {
		return getPreference(AuthenticationManager.Prefs.ALLOW_COOKIE_AUTH, Boolean.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isContainerAuthenticated() {
		try {
			final Authorizer authorizer = this.authorizationManager.getAuthorizer();
			if (authorizer instanceof WebContainerAuthorizer) {
				return ((WebContainerAuthorizer) authorizer).isContainerAuthorized();
			}
		} catch (final WikiException e) {
			// It's probably ok to fail silently...
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean login(final HttpServletRequest request) throws WikiSecurityException {
		final WikiSession session = sessionMonitor.getWikiSession(request);
		return login(request, session);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean login(final HttpServletRequest request, WikiSession session) throws WikiSecurityException {
		CallbackHandler handler = null;
		final Map<String, String> options = Collections.emptyMap();

		// If user not authenticated, check if container logged them in,
		// or if there's an authentication cookie
		if (!session.isAuthenticated()) {
			// Create a callback handler
			handler = new WebContainerCallbackHandler(m_engine, request);

			// Execute the container login module, then (if that fails) the cookie auth module
			Set<Principal> principals = this.doJAASLogin(WebContainerLoginModule.class, handler, options);
			if (principals.size() == 0 && this.isAllowsCookieAuthentication()) {
				principals = this.doJAASLogin(CookieAuthenticationLoginModule.class, handler, options);
			}

			// If the container logged the user in successfully,
			// tell the Session (and add all of the Principals)
			if (principals.size() > 0) {
				eventAdmin.sendEvent(new Event(LoginEvent.Topic.AUTHENTICATED, Map.of( //
						WikiEvent.PROPERTY_KEY_TARGET, request.getSession().getId(), //
						WikiEvent.PROPERTY_PRINCIPALS, principals)));

				// Add all appropriate Authorizer roles
				injectAuthorizerRoles(session, this.authorizationManager.getAuthorizer(), request);

				return true;
			}
		}

		// If user still not authenticated, check if assertion cookie was supplied
		if (!session.isAuthenticated() && this.isAllowsCookieAssertions()) {
			// Execute the cookie assertion login module
			final Set<Principal> principals = this.doJAASLogin(CookieAssertionLoginModule.class, handler, options);
			if (principals.size() > 0) {
				eventAdmin.sendEvent(new Event(LoginEvent.Topic.ASSERTED, Map.of( //
						WikiEvent.PROPERTY_KEY_TARGET, request.getSession().getId(), //
						WikiEvent.PROPERTY_PRINCIPALS, principals)));
				return true;
			}
		}

		// If user still anonymous, use the remote address
		if (session.isAnonymous()) {
			final Set<Principal> principals = this.doJAASLogin(AnonymousLoginModule.class, handler, options);
			if (principals.size() > 0) {
				eventAdmin.sendEvent(new Event(LoginEvent.Topic.ANONYMOUS, Map.of( //
						WikiEvent.PROPERTY_KEY_TARGET, request.getSession().getId(), //
						WikiEvent.PROPERTY_PRINCIPALS, principals)));
				return true;
			}
		}

		// If by some unusual turn of events the Anonymous login module doesn't work, login failed!
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean loginAsserted(final WikiSession session, final HttpServletRequest request, final String username,
			final String password) throws WikiSecurityException {
		if (session == null) {
			log.error("No wiki session provided, cannot log in.");
			return false;
		}

		// Protect against brute-force password guessing if configured to do so
		if (getPreference(AuthenticationManager.Prefs.LOGIN_THROTTLING, Boolean.class)) {
			delayLogin(username);
		}

		final CallbackHandler handler = new WikiCallbackHandler(m_engine, null, username, password);

		// Execute the user's specified login module
		final Set<Principal> principals = this.doJAASLogin(m_loginModuleClass, handler, m_loginModuleOptions);
		if (principals.size() > 0) {
			String httpSessionId = (request != null) ? request.getSession().getId() : "";
			eventAdmin.sendEvent(
					new Event(LoginEvent.Topic.AUTHENTICATED, Map.of(WikiEvent.PROPERTY_KEY_TARGET,
							httpSessionId, WikiEvent.PROPERTY_PRINCIPALS, principals)));

			// Add all appropriate Authorizer roles
			injectAuthorizerRoles(session, this.authorizationManager.getAuthorizer(), null);

			return true;
		}

		return false;
	}

	/**
	 * This method builds a database of login names that are being attempted, and will try to delay if
	 * there are too many requests coming in for the same username.
	 * <p>
	 * The current algorithm uses 2^loginattempts as the delay in milliseconds, i.e. at 10 login
	 * attempts it'll add 1.024 seconds to the login.
	 *
	 * @param username The username that is being logged in
	 */
	private void delayLogin(final String username) {
		try {
			m_lastLoginAttempts.cleanup(LASTLOGINS_CLEANUP_TIME);
			final int count = m_lastLoginAttempts.count(username);

			final long delay = Math.min(1 << count, MAX_LOGIN_DELAY);
			log.debug("Sleeping for " + delay + " ms to allow login.");
			Thread.sleep(delay);

			m_lastLoginAttempts.add(username);
		} catch (final InterruptedException e) {
			// FALLTHROUGH is fine
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logout(final HttpServletRequest request) {
		if (request == null) {
			log.error("No HTTP reqest provided; cannot log out.");
			return;
		}

		HttpSession httpSession = request.getSession();
		String httpSessionId = (httpSession != null) ? httpSession.getId() : null;
		eventAdmin.sendEvent(new Event(LoginEvent.Topic.LOGOUT, Map.of( //
				WikiEvent.PROPERTY_KEY_TARGET, httpSessionId)));

		// We need to flush the HTTP session too
		if (httpSession != null) {
			httpSession.invalidate();
		}
	}

	/**
	 * Instantiates and executes a single JAAS {@link LoginModule}, and returns a Set of Principals that
	 * results from a successful login. The LoginModule is instantiated, then its
	 * {@link LoginModule#initialize(Subject, CallbackHandler, Map, Map)} method is called. The
	 * parameters passed to <code>initialize</code> is a dummy Subject, an empty shared-state Map, and
	 * an options Map the caller supplies.
	 *
	 * @param clazz   the LoginModule class to instantiate
	 * @param handler the callback handler to supply to the LoginModule
	 * @param options a Map of key/value strings for initializing the LoginModule
	 * @return the set of Principals returned by the JAAS method {@link Subject#getPrincipals()}
	 * @throws WikiSecurityException if the LoginModule could not be instantiated for any reason
	 */
	protected Set<Principal> doJAASLogin(final Class<? extends LoginModule> clazz, final CallbackHandler handler,
			final Map<String, String> options) throws WikiSecurityException {
		// Instantiate the login module
		// @NonNull //:FVK: workaround - commented.
		final LoginModule loginModule;
		try {
			loginModule = clazz.getDeclaredConstructor().newInstance();
		} catch (final InstantiationException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException e) {
			throw new WikiSecurityException(e.getMessage(), e);
		}

		if (loginModule == null) { // :FVK: workaround - вместо @NonNull
			return Collections.emptySet();
		}

		// Initialize the LoginModule
		final Subject subject = new Subject();
		loginModule.initialize(subject, handler, Collections.emptyMap(), options);

		// Try to log in:
		boolean loginSucceeded = false;
		boolean commitSucceeded = false;
		try {
			loginSucceeded = loginModule.login();
			if (loginSucceeded) {
				commitSucceeded = loginModule.commit();
			}
		} catch (final LoginException e) {
			// Login or commit failed! No principal for you!
		}

		// If we successfully logged in & committed, return all the principals
		if (loginSucceeded && commitSucceeded) {
			return subject.getPrincipals();
		}

		return Collections.emptySet();
	}

	/**
	 * After successful login, this method is called to inject authorized role Principals into the
	 * Session. To determine which roles should be injected, the configured Authorizer is queried for
	 * the roles it knows about by calling {@link Authorizer#getRoles()}. Then, each role returned by
	 * the authorizer is tested by calling {@link Authorizer#isUserInRole(WikiSession, Principal)}. If this
	 * check fails, and the Authorizer is of type IWebAuthorizer, the role is checked again by calling
	 * {@link WebAuthorizer#isUserInRole(HttpServletRequest, Principal)}). Any roles that pass the test
	 * are injected into the Subject by firing appropriate authentication events.
	 *
	 * @param session    the user's current Session
	 * @param authorizer the Engine's configured Authorizer
	 * @param request    the user's HTTP session, which may be <code>null</code>
	 */
	private void injectAuthorizerRoles(WikiSession session, Authorizer authorizer, HttpServletRequest request) {
		Set<Principal> principals = new HashSet<>();
		// Test each role the authorizer knows about
		for (final Principal role : authorizer.getRoles()) {
			// Test the Authorizer
			if (authorizer.isUserInRole(session, role)) {
				principals.add(role);
				if (log.isDebugEnabled()) {
					log.debug("Added authorizer role " + role.getName() + ".");
				}
			}
			// If web authorizer, test the request.isInRole() method also
			else if (request != null && authorizer instanceof WebAuthorizer) {
				final WebAuthorizer wa = (WebAuthorizer) authorizer;
				if (wa.isUserInRole(request, role)) {
					principals.add(role);
					if (log.isDebugEnabled()) {
						log.debug("Added container role " + role.getName() + ".");
					}
				}
			}
		}
		if (request != null) {
			String wikiSessionId = request.getSession().getId();
			eventAdmin.sendEvent(new Event(LoginEvent.Topic.PRINCIPALS_ADD, Map.of( //
					WikiEvent.PROPERTY_KEY_TARGET, wikiSessionId, //
					WikiEvent.PROPERTY_PRINCIPALS, principals)));
		}
	}

	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
			break;
		}*/
	}

}
