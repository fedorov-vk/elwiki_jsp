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

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiSecurityEvent;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.rss.RSSGenerator;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.Authorizer;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.apache.wiki.auth.SessionMonitor;
//import org.apache.wiki.auth.SessionMonitor;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.ui.TemplateManager;
//import org.apache.wiki.auth.authorize.WebAuthorizer;
//import org.apache.wiki.auth.authorize.WebContainerAuthorizer;
//import org.apache.wiki.auth.login.AnonymousLoginModule;
//import org.apache.wiki.auth.login.CookieAssertionLoginModule;
//import org.apache.wiki.auth.login.CookieAuthenticationLoginModule;
//import org.apache.wiki.auth.login.UserDatabaseLoginModule;
//import org.apache.wiki.auth.login.WebContainerCallbackHandler;
//import org.apache.wiki.auth.login.WebContainerLoginModule;
//import org.apache.wiki.auth.login.WikiCallbackHandler;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.util.TimedCounterList;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.IAuthorizer;
import org.elwiki.authorize.WebContainerAuthorizer;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.authorize.login.AnonymousLoginModule;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki.authorize.login.CookieAuthenticationLoginModule;
import org.elwiki.authorize.login.UserDatabaseLoginModule;
import org.elwiki.authorize.login.WebContainerCallbackHandler;
import org.elwiki.authorize.login.WebContainerLoginModule;
import org.elwiki.authorize.login.WikiCallbackHandler;
import org.elwiki.services.ServicesRefs;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Default implementation for {@link IIAuthenticationManager}
 *
 * {@inheritDoc}
 * 
 * @since 2.3
 */
@Component(name = "elwiki.DefaultAuthenticationManager", service = IIAuthenticationManager.class, //
factory = "elwiki.AuthenticationManager.factory")
public class DefaultAuthenticationManager implements IIAuthenticationManager {

    /** How many milliseconds the logins are stored before they're cleaned away. */
    private static final long LASTLOGINS_CLEANUP_TIME = 10 * 60 * 1_000L; // Ten minutes

    private static final long MAX_LOGIN_DELAY = 20 * 1_000L; // 20 seconds

    private static final Logger log = Logger.getLogger( DefaultAuthenticationManager.class );

    /** Empty Map passed to JAAS {@link #doJAASLogin(Class, CallbackHandler, Map)} method. */
    protected static final Map< String, String > EMPTY_MAP = Collections.unmodifiableMap( new HashMap<>() );

    /** Class (of type LoginModule) to use for custom authentication. */
    protected Class< ? extends LoginModule > m_loginModuleClass = UserDatabaseLoginModule.class;

    /** Options passed to {@link LoginModule#initialize(Subject, CallbackHandler, Map, Map)};
     * initialized by {@link #initialize(Engine, Properties)}. */
    protected Map< String, String > m_loginModuleOptions = new HashMap<>();

    /** The default {@link LoginModule} class name to use for custom authentication. */
    private static final String DEFAULT_LOGIN_MODULE = UserDatabaseLoginModule.class.getCanonicalName(); 
    		//:FVK: "org.apache.wiki.auth.login.UserDatabaseLoginModule";

    /** Empty principal set. */
    private static final Set<Principal> NO_PRINCIPALS = new HashSet<>();

    /** Static Boolean for lazily-initializing the "allows assertions" flag */
    private boolean m_allowsCookieAssertions = true;

    private boolean m_throttleLogins = true;

    /** Static Boolean for lazily-initializing the "allows cookie authentication" flag */
    private boolean m_allowsCookieAuthentication = false;

    private Engine m_engine = null;

    /** If true, logs the IP address of the editor */
    private boolean m_storeIPAddress = true;

    /** Keeps a list of the usernames who have attempted a login recently. */
    private TimedCounterList< String > m_lastLoginAttempts = new TimedCounterList<>();

    ///////////////////////////////////////////////////////////////////////////
    
	private static final String ID_EXTENSION_LOGIN_MODULE = "loginModule";

	private final Map<String, Class<? extends LoginModule>> loginModuleClasses = new HashMap<>();

	/** Class (of type LoginModule) to use for custom authentication. */
	protected Class<? extends LoginModule> loginModuleClass = UserDatabaseLoginModule.class;

	// -- service handling ---------------------------{start}--

	@WikiServiceReference
	private AuthorizationManager authorizationManager;

	@Activate
	public synchronized void startup() throws WikiException {
		//
	}

	@Deactivate
	public synchronized void shutdown() {
		//
	}
	
	// -- service handling -----------------------------{end}--
    
	/**
     * {@inheritDoc}
     */
    @Override
    public void initialize( final Engine engine ) throws WikiException {
        m_engine = engine;
        m_storeIPAddress = TextUtil.getBooleanProperty( engine.getWikiPreferences(), PROP_STOREIPADDRESS, m_storeIPAddress );

        // Should we allow cookies for assertions? (default: yes)
        m_allowsCookieAssertions = TextUtil.getBooleanProperty( engine.getWikiPreferences(), PROP_ALLOW_COOKIE_ASSERTIONS,true );

        // Should we allow cookies for authentication? (default: no)
        m_allowsCookieAuthentication = TextUtil.getBooleanProperty( engine.getWikiPreferences(), PROP_ALLOW_COOKIE_AUTH, false );

        // Should we throttle logins? (default: yes)
        m_throttleLogins = TextUtil.getBooleanProperty( engine.getWikiPreferences(), PROP_LOGIN_THROTTLING, true );

        // Look up the LoginModule class
        //final String loginModuleClassName = TextUtil.getStringProperty( props, PROP_LOGIN_MODULE, DEFAULT_LOGIN_MODULE );
        final String loginModuleClassName = DEFAULT_LOGIN_MODULE; //:FVK:
        /*:FVK:
        try {
            m_loginModuleClass = ( Class< ? extends LoginModule > )Class.forName( loginModuleClassName );
        } catch( final ClassNotFoundException e ) {
            log.error( e.getMessage(), e );
            throw new WikiException( "Could not instantiate LoginModule class.", e );
        }
        */
		this.loginModuleClass = getLoginModule(loginModuleClassName);

        // Initialize the LoginModule options
        initLoginModuleOptions( engine.getWikiPreferences() );
    }

	/**
	 * Look up the LoginModule class, via extension point "org.elwiki.authorize.loginModule".
	 * 
	 * @param loginModuleId
	 * @return
	 * @throws WikiException
	 */
	private Class<? extends LoginModule> getLoginModule(String loginModuleId) throws WikiException {
		//
		// Загрузка из расширений модулей регистрации пользователя.
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContainerAuthenticated() {
    	/*:FVK:
        try {
            final Authorizer authorizer = this.authorizationManager.getAuthorizer();
            if ( authorizer instanceof WebContainerAuthorizer ) {
                 return ( ( WebContainerAuthorizer )authorizer ).isContainerAuthorized();
            }
        } catch ( final WikiException e ) {
            // It's probably ok to fail silently...
        }*/
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean login( final HttpServletRequest request ) throws WikiSecurityException {
        final HttpSession httpSession = request.getSession();
        final Session session = SessionMonitor.getInstance().find( httpSession );

//:FVK: - ссылка сам на себя !!! ??        
        final IIAuthenticationManager authenticationMgr = ServicesRefs.getAuthenticationManager();
        
        CallbackHandler handler = null;
        final Map< String, String > options = EMPTY_MAP;

        // If user not authenticated, check if container logged them in, or if there's an authentication cookie
        if ( !session.isAuthenticated() ) {
            // Create a callback handler
            handler = new WebContainerCallbackHandler( m_engine, request );

            // Execute the container login module, then (if that fails) the cookie auth module
            Set< Principal > principals = authenticationMgr.doJAASLogin( WebContainerLoginModule.class, handler, options );
            if ( principals.size() == 0 && authenticationMgr.allowsCookieAuthentication() ) {
                principals = authenticationMgr.doJAASLogin( CookieAuthenticationLoginModule.class, handler, options );
            }

            // If the container logged the user in successfully, tell the Session (and add all of the Principals)
            if ( principals.size() > 0 ) {
                fireEvent( WikiSecurityEvent.LOGIN_AUTHENTICATED, getLoginPrincipal( principals ), session );
                for( final Principal principal : principals ) {
                    fireEvent( WikiSecurityEvent.PRINCIPAL_ADD, principal, session );
                }

                // Add all appropriate Authorizer roles
                injectAuthorizerRoles( session, this.authorizationManager.getAuthorizer(), request );
            }
        }

        // If user still not authenticated, check if assertion cookie was supplied
        if ( !session.isAuthenticated() && authenticationMgr.allowsCookieAssertions() ) {
            // Execute the cookie assertion login module
            final Set< Principal > principals = authenticationMgr.doJAASLogin( CookieAssertionLoginModule.class, handler, options );
            if ( principals.size() > 0 ) {
                fireEvent( WikiSecurityEvent.LOGIN_ASSERTED, getLoginPrincipal( principals ), session);
            }
        }

        // If user still anonymous, use the remote address
        if( session.isAnonymous() ) {
            final Set< Principal > principals = authenticationMgr.doJAASLogin( AnonymousLoginModule.class, handler, options );
            if( principals.size() > 0 ) {
                fireEvent( WikiSecurityEvent.LOGIN_ANONYMOUS, getLoginPrincipal( principals ), session );
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
    public boolean login( final Session session, final HttpServletRequest request, final String username, final String password ) throws WikiSecurityException {
        if ( session == null ) {
            log.error( "No wiki session provided, cannot log in." );
            return false;
        }

        // Protect against brute-force password guessing if configured to do so
        if ( m_throttleLogins ) {
            delayLogin( username );
        }

		final CallbackHandler handler = new WikiCallbackHandler(m_engine, null, username, password);

        // Execute the user's specified login module
        final Set< Principal > principals = doJAASLogin( m_loginModuleClass, handler, m_loginModuleOptions );
        if( principals.size() > 0 ) {
            fireEvent(WikiSecurityEvent.LOGIN_AUTHENTICATED, getLoginPrincipal( principals ), session );
            for ( final Principal principal : principals ) {
                fireEvent( WikiSecurityEvent.PRINCIPAL_ADD, principal, session );
            }

            // Add all appropriate Authorizer roles
            injectAuthorizerRoles( session, this.authorizationManager.getAuthorizer(), null );

            return true;
        }

        return false;
    }

    /**
     *  This method builds a database of login names that are being attempted, and will try to delay if there are too many requests coming
     *  in for the same username.
     *  <p>
     *  The current algorithm uses 2^loginattempts as the delay in milliseconds, i.e. at 10 login attempts it'll add 1.024 seconds to the login.
     *
     *  @param username The username that is being logged in
     */
    private void delayLogin( final String username ) {
        try {
            m_lastLoginAttempts.cleanup( LASTLOGINS_CLEANUP_TIME );
            final int count = m_lastLoginAttempts.count( username );

            final long delay = Math.min( 1 << count, MAX_LOGIN_DELAY );
            log.debug( "Sleeping for " + delay + " ms to allow login." );
            Thread.sleep( delay );

            m_lastLoginAttempts.add( username );
        } catch( final InterruptedException e ) {
            // FALLTHROUGH is fine
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout( final HttpServletRequest request ) {
        if( request == null ) {
            log.error( "No HTTP reqest provided; cannot log out." );
            return;
        }

        final HttpSession session = request.getSession();
        final String sid = ( session == null ) ? "(null)" : session.getId();
        if( log.isDebugEnabled() ) {
            log.debug( "Invalidating Session for session ID=" + sid );
        }
        // Retrieve the associated Session and clear the Principal set
        final Session wikiSession = Wiki.session().find( m_engine, request );
        final Principal originalPrincipal = wikiSession.getLoginPrincipal();
        wikiSession.invalidate();

        // Remove the wikiSession from the WikiSession cache
        Wiki.session().remove( m_engine, request );

        // We need to flush the HTTP session too
        if( session != null ) {
            session.invalidate();
        }

        // Log the event
        fireEvent( WikiSecurityEvent.LOGOUT, originalPrincipal, null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowsCookieAssertions() {
        return m_allowsCookieAssertions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowsCookieAuthentication() {
        return m_allowsCookieAuthentication;
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public Set< Principal > doJAASLogin( final Class< ? extends LoginModule > clazz,
                                         final CallbackHandler handler,
                                         final Map< String, String > options ) throws WikiSecurityException {
        // Instantiate the login module
    	//@NonNull //:FVK: workaround - commented.
        final LoginModule loginModule;
        try {
       		loginModule = clazz.getDeclaredConstructor().newInstance();
        } catch( final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e ) {
            throw new WikiSecurityException( e.getMessage(), e );
        }
        
		if (loginModule == null) { // :FVK: workaround - вместо @NonNull
			return Collections.emptySet();
		}

        // Initialize the LoginModule
        final Subject subject = new Subject();
        loginModule.initialize( subject, handler, EMPTY_MAP, options );

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
        if( loginSucceeded && commitSucceeded ) {
            return subject.getPrincipals();
        }
        return NO_PRINCIPALS;
    }

    // events processing .......................................................

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addWikiEventListener( final WikiEventListener listener ) {
        WikiEventManager.addWikiEventListener( this, listener );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeWikiEventListener( final WikiEventListener listener ) {
        WikiEventManager.removeWikiEventListener( this, listener );
    }

    /**
     * Initializes the options Map supplied to the configured LoginModule every time it is invoked. The properties and values extracted from
     * <code>jspwiki.properties</code> are of the form <code>jspwiki.loginModule.options.<var>param</var> = <var>value</var>, where
     * <var>param</var> is the key name, and <var>value</var> is the value.
     *
     * @param props the properties used to initialize JSPWiki
     * @throws IllegalArgumentException if any of the keys are duplicated
     */
    private void initLoginModuleOptions( IPreferenceStore props ) {
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

    /**
     * After successful login, this method is called to inject authorized role Principals into the Session. To determine which roles
     * should be injected, the configured Authorizer is queried for the roles it knows about by calling  {@link Authorizer#getRoles()}.
     * Then, each role returned by the authorizer is tested by calling {@link Authorizer#isUserInRole(Session, Principal)}. If this
     * check fails, and the Authorizer is of type WebAuthorizer, the role is checked again by calling
     * {@link WebAuthorizer#isUserInRole(HttpServletRequest, Principal)}). Any roles that pass the test are injected into the Subject by
     * firing appropriate authentication events.
     *
     * @param session the user's current Session
     * @param authorizer the Engine's configured Authorizer
     * @param request the user's HTTP session, which may be <code>null</code>
     */
    private void injectAuthorizerRoles( final Session session, final IAuthorizer authorizer, final HttpServletRequest request ) {
    	/*:FVK:
    	Authorizer authorizer1 = (Authorizer)authorizer; //:FVK: cast -- это неверно !!!
        // Test each role the authorizer knows about
        for( final Principal role : authorizer1.getRoles() ) {
            // Test the Authorizer
            if( authorizer1.isUserInRole( session, role ) ) {
                fireEvent( WikiSecurityEvent.PRINCIPAL_ADD, role, session );
                if( log.isDebugEnabled() ) {
                    log.debug( "Added authorizer role " + role.getName() + "." );
                }
            }
            // If web authorizer, test the request.isInRole() method also
            else if ( request != null && authorizer instanceof WebAuthorizer ) {
                final WebAuthorizer wa = ( WebAuthorizer )authorizer;
                if ( wa.isUserInRole( request, role ) ) {
                    fireEvent( WikiSecurityEvent.PRINCIPAL_ADD, role, session );
                    if ( log.isDebugEnabled() ) {
                        log.debug( "Added container role " + role.getName() + "." );
                    }
                }
            }
        }*/
    }

}
