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

import java.security.AllPermission;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.event.ElWikiEventsConstants;
import org.apache.wiki.api.event.WikiEvent;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiSecurityEvent;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.acl.AclManager;
import org.apache.wiki.auth.user0.UserProfile;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.ui.TemplateManager;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.IPreferenceStore;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.IAuthenticationManager;
//import org.elwiki.api.IAuthorizationManager;
//import org.elwiki.api.IElWikiSession;
import org.osgi.service.useradmin.Group;
import org.elwiki.IWikiConstants.AuthenticationStatus;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.IGroupManager;
//import org.elwiki.api.authorization.user.IUserDatabase;
//import org.elwiki.api.authorization.user.UserProfile;
//import org.elwiki.api.event.WikiEvent;
//import org.elwiki.api.event.WikiEventListener;
//import org.elwiki.api.event.WikiEventManager;
//import org.elwiki.api.event.WikiSecurityEvent;
//import org.elwiki.api.exceptions.NoRequiredPropertyException;
//import org.elwiki.api.exceptions.NoSuchPrincipalException;
//import org.elwiki.api.exceptions.WikiException;
//import org.elwiki.api.exceptions.WikiSecurityException;

import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.Aprincipal;
import org.elwiki.data.authorize.GroupPrincipal;
import org.elwiki.data.authorize.UnresolvedPrincipal;
import org.elwiki.data.authorize.WikiPrincipal;
import org.elwiki.permissions.GroupPermission;
import org.elwiki.permissions.PagePermission;
import org.elwiki.permissions.WikiPermission;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.WikiPage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventHandler;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

/**
 * <p>
 * Manages all access control and authorization; determines what authenticated users are allowed
 * to do.
 * </p>
 * <p>
 * Privileges in JSPWiki are expressed as Java-standard {@link java.security.Permission}
 * classes. There are two types of permissions:
 * </p>
 * <ul>
 * <li>{@link org.elwiki.permissions.WikiPermission} - privileges that apply to an entire wiki
 * instance: <em>e.g.,</em> editing user profiles, creating pages, creating groups</li>
 * <li>{@link org.elwiki.permissions.PagePermission} - privileges that apply to a single wiki
 * page or range of pages: <em>e.g.,</em> reading, editing, renaming
 * </ul>
 * <p>
 * Calling classes determine whether they are entitled to perform a particular action by
 * constructing the appropriate permission first, then passing it and the current
 * {@link org.elwiki.api.IElWikiSession} to the
 * {@link #checkPermission(IElWikiSession, Permission)} method. If the session's Subject
 * possesses the permission, the action is allowed.
 * </p>
 * <p>
 * For WikiPermissions, the decision criteria is relatively simple: the caller either possesses
 * the permission, as granted by the wiki security policy -- or not.
 * </p>
 * <p>
 * For PagePermissions, the logic is exactly the same if the page being checked does not have an
 * access control list. However, if the page does have an ACL, the authorization decision is
 * made based the <em>union</em> of the permissions granted in the ACL and in the security
 * policy. In other words, the user must be named in the ACL (or belong to a group or role that
 * is named in the ACL) <em>and</em> be granted (at least) the same permission in the security
 * policy. We do this to prevent a user from gaining more permissions than they already have,
 * based on the security policy.
 * </p>
 * <p>
 * See the {@link #checkPermission(WikiSession, Permission)} and
 * {@link #hasRoleOrPrincipal(WikiSession, Principal)} methods for more information on the
 * authorization logic.
 * </p>
 * 
 * @see AuthenticationManager
 */
@SuppressWarnings("unused")
//@formatter:off
@Component(
	name = "elwiki.DefaultAuthorizationManager",
	service = {AuthorizationManager.class, EventHandler.class},
	factory = "elwiki.AuthorizationManager.factory")
	//:FVK: property = EventConstants.EVENT_TOPIC + "=" + ElWikiEventsConstants.TOPIC_LOGGING_ALL)
//@formatter:on
public class DefAuthorizationManager implements AuthorizationManager, WikiEventListener, EventHandler {

	private static final Logger log = Logger.getLogger(DefAuthorizationManager.class);

	/** The extension ID for access to implementation set of {@link IGroupManager}. */
	private static final String ID_EXTENSION_AUTHORIZER = "authorizer";

	/** Extension's specific ID of default external Authorizer. Current value - {@value} */
	protected static final String DEFAULT_AUTHORIZER = "WebContainerAuthorizer";

	/** The property name in jspwiki.properties for specifying the external {@link IGroupManager}. */
	protected static final String PROP_AUTHORIZER = "jspwiki.authorizer";

	/** Name of the default security policy file, as bundle resource. */
	protected static final String DEFAULT_POLICY = "jspwiki.policy";

	private static final Class<?>[] permissionMethodArgs = new Class[] {String.class, String.class};
	
	private final Map<String, Class<? extends IGroupManager>> authorizerClasses = new HashMap<>();

	private IGroupManager m_authorizer = null;

	/** Cache for storing PermissionCollections used to evaluate the local policy. */
	private Map<String, PermissionCollection> cachedPermissions = new HashMap<>();

	private Engine m_engine;

	
	// == CODE ================================================================

	/**
	 * Constructs a new AuthorizationManager instance.
	 */
	public DefAuthorizationManager() {
		//
	}

	// -- service handling ---------------------------(start)--

	/** Stores configuration. */
	@Reference //(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private IGroupManager groupManager;

	@WikiServiceReference
	private AclManager aclManager;

	/**
	 * This component activate routine. Does all the real initialization.
	 * Initializes security policy of AuthorizationManager.
	 * 
	 * @param componentContext
	 * @throws WikiException if the AuthorizationManager failed on startup.
	 */
	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		Object obj = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
		if (obj instanceof Engine engine) {
			initialize(engine);
		}
	}

	/**
	 * Looks up and obtains a policy configuration file inside this bundle area.
	 * 
	 * @param name
	 *             the file to obtain, <em>e.g.</em>, <code>jspwiki.policy</code>
	 * @param bc
	 *             context of this bundle.
	 *
	 * @return the URL to the file
	 */
	protected URL findConfigFile(String name, BundleContext bc) {
		log.debug("Looking for '" + name + "' inside 'org.elwiki.authorize' bundle area.");
		// Try creating an absolute path first
		File defaultFile = null;
		IPath cfgPath = null;
		URL url = FileLocator.find(bc.getBundle(), new Path(""), null);
		try {
			url = FileLocator.toFileURL(url);
			cfgPath = new Path(url.getPath()).makeAbsolute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.fatal(e.getMessage(), e);
			return null;
		}

		defaultFile = cfgPath.append(name).toFile();
		if (defaultFile.exists()) {
			try {
				return defaultFile.toURI().toURL();
			} catch (MalformedURLException e) {
				// Shouldn't happen, but log it if it does
				log.debug("Malformed URL: " + e.getMessage());
			}
		}

		return null;
	}
	
	@Deactivate
	protected void shutdown() {
		//
	}

	/**
	 * Initializes AuthorizationManager with an ApplicationSession and set of parameters.
	 * 
	 * Expects to find extension 'org.elwiki.auth.authorizer' with a valid Authorizer implementation
	 * to take care of role lookup operations.
	 */
	@Override
	public void initialize(Engine engine1) throws WikiException {
		log.debug("Initialize.");
		this.m_engine = engine1;

		IPreferenceStore properties = this.wikiConfiguration.getWikiPreferences();

		//
		//  JAAS authorization continues.
		//
		String authorizerName = properties.getString(PROP_AUTHORIZER);
		if (authorizerName.length() == 0) {
			authorizerName = DEFAULT_AUTHORIZER;
		}
		this.m_authorizer = getAuthorizerImplementation(authorizerName);
		/*:FVK:
		this.m_authorizer.initialize(this.m_engine);

		// Make the AuthorizationManager listen for WikiEvents
		// from AuthenticationManager (WikiSecurityEvents for changed user profiles)
		m_engine.getAuthenticationManager().addWikiEventListener(this);
		*/
	}

	/**
	 * Attempts to locate and initialize a Authorizer to use with this manager. Throws a
	 * WikiException if no entry is found, or if one fails to initialize.
	 * 
	 * @param defaultAuthorizerId
	 *                            default authorizer Id of extension point.
	 * @return a Authorizer used to get page authorization information
	 * @throws WikiException
	 */
	private IGroupManager getAuthorizerImplementation(String defaultAuthorizerId) throws WikiException {
		String namespace = AuthorizePluginActivator.getDefault().getBundle().getSymbolicName();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep;

		//
		// Load an Authorizer from Equinox extensions.
		//
		ep = registry.getExtensionPoint(namespace, ID_EXTENSION_AUTHORIZER);
		if (ep != null) {
			for (IConfigurationElement el : ep.getConfigurationElements()) {
				String contributorName = el.getContributor().getName();
				String className = el.getAttribute("class");
				String authorizerId = el.getAttribute("id");
				try {
					final Bundle bundle = Platform.getBundle(contributorName);
					Class<?> clazz = bundle.loadClass(className);
					try {
						Class<? extends IGroupManager> cl = clazz.asSubclass(IGroupManager.class);
						this.authorizerClasses.put(authorizerId, (Class<? extends IGroupManager>) cl);
					} catch (ClassCastException e) {
						log.fatal("Authorizer " + className + " is not extends Authorizer interface.", e);
						throw new WikiException("Authorizer " + className + " is not extends Authorizer interface.", e);
					}
				} catch (ClassNotFoundException e) {
					log.fatal("Authorizer " + className + " cannot be found.", e);
					throw new WikiException("Authorizer " + className + " cannot be found.", e);
				}
			}
		}

		Class<? extends IGroupManager> clazzAuthorizer = this.authorizerClasses.get(defaultAuthorizerId);
		if (clazzAuthorizer == null) {
			// TODO: это сообщение не к месту (логика не адекватна).
			throw new NoRequiredPropertyException("Unable to find an entry in the preferences.", PROP_AUTHORIZER);
		}

		IGroupManager authorizer;
		try {
			authorizer = clazzAuthorizer.newInstance();
		} catch (InstantiationException e) {
			log.fatal("Authorizer " + clazzAuthorizer + " cannot be created.", e);
			throw new WikiException("Authorizer " + clazzAuthorizer + " cannot be created.", e);
		} catch (IllegalAccessException e) {
			log.fatal("You are not allowed to access authorizer class " + clazzAuthorizer, e);
			throw new WikiException("You are not allowed to access authorizer class " + clazzAuthorizer, e);
		}

		return authorizer;
	}

	// -- service handling -----------------------------(end)--

	@Override
	public boolean checkPermission(Session session, Permission permission) {
		//
		//  A slight sanity check.
		//
		if (session == null || permission == null) {
			fireEvent(WikiSecurityEvent.ACCESS_DENIED, null, permission);
			return false;
		}
		
		Principal user = session.getLoginPrincipal();

		// Always allow the action if user has AllPermission
		Permission allPermission = new org.elwiki.permissions.AllPermission(
				this.wikiConfiguration.getApplicationName(), null);
		boolean hasAllPermission = checkStaticPermission(session, allPermission);
		if (hasAllPermission) {
			fireEvent(WikiSecurityEvent.ACCESS_ALLOWED, user, permission);
			return true;
		}

		// If the user doesn't have *at least* the permission
		// granted by policy, return false.
		boolean hasPolicyPermission = checkStaticPermission(session, permission);
		if (!hasPolicyPermission) {
			fireEvent(WikiSecurityEvent.ACCESS_DENIED, user, permission);
			return false;
		}

		// If this isn't a PagePermission, it's allowed
		if (!(permission instanceof PagePermission)) {
			fireEvent(WikiSecurityEvent.ACCESS_ALLOWED, user, permission);
			return true;
		}

		//
		// If the page or ACL is null, it's allowed.
		//
		String pageName = ((PagePermission) permission).getPage();
		WikiPage page = ServicesRefs.getPageManager().getPage(pageName);
		Acl acl = (page == null) ? null : this.aclManager.getPermissions(page);
		if (page == null || acl == null || acl.getAclEntries().isEmpty()) {
			fireEvent(WikiSecurityEvent.ACCESS_ALLOWED, user, permission);
			return true;
		}
		

		//
		//  Next, iterate through the Principal objects assigned
		//  this permission. If the context's subject possesses
		//  any of these, the action is allowed.

		Principal[] aclPrincipals = acl.findPrincipals(permission);

		log.debug("Checking ACL entries...");
		log.debug("Acl for this page is: " + acl);
		log.debug("Checking for principals: " + Arrays.toString(aclPrincipals));
		log.debug("Permission: " + permission);

		for (Principal aclPrincipal : aclPrincipals) {
			// If the ACL principal we're looking at is unresolved,
			// try to resolve it here & correct the Acl
			if (aclPrincipal instanceof UnresolvedPrincipal) {
				AclEntry aclEntry = acl.getEntry(aclPrincipal);
				aclPrincipal = resolvePrincipal(aclPrincipal.getName());
				if (aclEntry != null && !(aclPrincipal instanceof UnresolvedPrincipal)) {
					aclEntry.setPrincipal(aclPrincipal);
				}
			}

			if (hasRoleOrPrincipal(session, aclPrincipal)) {
				fireEvent(WikiSecurityEvent.ACCESS_ALLOWED, user, permission);
				return true;
			}
		}

		fireEvent(WikiSecurityEvent.ACCESS_DENIED, user, permission);
		return false;
	}

	/* (non-Javadoc)
	 * @see org.elwiki.core.auth.IAuthorizationManager#isUserInRole(org.elwiki.core.common.WikiSession, java.security.Principal)
	 */
	@Override
	public boolean isUserInRole(Session session, Principal principal) {
		if (session == null || principal == null || IIAuthenticationManager.isUserPrincipal(principal)) {
			return false;
		}

		// Any type of user can possess a built-in role
		if (principal instanceof GroupPrincipal && GroupPrincipal.isBuiltInGroup((GroupPrincipal) principal)) {
			return session.hasPrincipal(principal);
		}

		// Only authenticated users can possess groups or custom roles
		if (session.isAuthenticated() && IIAuthenticationManager.isRolePrincipal(principal)) {
			return session.hasPrincipal(principal);
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.elwiki.core.auth.IAuthorizationManager#getAuthorizer()
	 */
	@Override
	public IGroupManager getAuthorizer() throws WikiSecurityException {
		if (this.m_authorizer != null) {
			return this.m_authorizer;
		}
		throw new WikiSecurityException("Authorizer did not initialize properly. Check the logs.");
	}

	/**
	 * <p>
	 * Determines if the Subject associated with a supplied WikiSession contains a desired user
	 * Principal or built-in Role principal, OR is a member a Group or external Role. The rules are
	 * as follows:
	 * </p>
	 * <ol>
	 * <li>First, if desired Principal is a Role or GroupPrincipal, delegate to
	 * {@link #isUserInRole(WikiSession, Group)} and return the result.</li>
	 * <li>Otherwise, we're looking for a user Principal, so iterate through the Principal set and
	 * see if any share the same name as the one we are looking for.</li>
	 * </ol>
	 * <p>
	 * <em>Note: if the Principal parameter is a user principal, the session must be authenticated
	 * in order for the user to "possess it". Anonymous or asserted sessions will never posseess a
	 * named user principal.</em>
	 * </p>
	 * 
	 * @param session
	 *                  the current wiki session, which must be non-null. If null, the result of
	 *                  this method always returns <code>false</code>
	 * @param principal
	 *                  the Principal (role, group, or user principal) to look for, which must be
	 *                  non-null. If null, the result of this method always returns
	 *                  <code>false</code>
	 * @return <code>true</code> if the Subject supplied with the IWikiContext posesses the Role,
	 *             GroupPrincipal or desired user Principal, <code>false</code> otherwise
	 */
	public boolean hasRoleOrPrincipal(Session session, Principal principal) {
		// If either parameter is null, always deny
		if (session == null || principal == null) {
			return false;
		}

		// If principal is role, delegate to isUserInRole
		if (IIAuthenticationManager.isRolePrincipal(principal)) {
			return isUserInRole(session, principal);
		}

		// We must be looking for a user principal, assuming that the user
		// has been properly logged in.
		// So just look for a name match.
		if (session.isAuthenticated() && IIAuthenticationManager.isUserPrincipal(principal)) {
			String principalName = principal.getName();
			Principal[] userPrincipals = session.getPrincipals();
			for (Principal userPrincipal : userPrincipals) {
				if (userPrincipal.getName().equals(principalName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines whether a Subject possesses a given "static" Permission as defined in the security
	 * policy file.
	 * 
	 * @param session    the WikiSession whose permission status is being queried
	 * @param permission the Permission the Subject must possess
	 * @return <code>true</code> if the Subject possesses the permission, <code>false</code>
	 *         otherwise
	 */
	public boolean checkStaticPermission(Session session, Permission permission) {
		// Try the local policy - check each Role/Group and User Principal
		return allowedByLocalPolicy(session.getRoles(), permission);
	}

	/**
	 * Checks to see if the wiki security policy allows a particular static Permission. Do not use
	 * this method for normal permission checks; use
	 * {@link #checkPermission(Session, Permission)} instead.
	 * 
	 * @param principals
	 *                   the Principals to check. Only handles wiki's principals (Role, Group).
	 *                   User principals can not has permission info - they can't be handled.   
	 * @param permission
	 *                   the Permission.
	 * @return the result
	 */
	public boolean allowedByLocalPolicy(Principal[] principals, Permission permission) {
		for (Principal principal : principals) {
			if(principal instanceof Aprincipal) {
				String roleName = principal.getName();
				PermissionCollection permCollection;
				if( cachedPermissions.containsKey(roleName) ) {
					permCollection = cachedPermissions.get(roleName);
				} else {
					// Instantiates permissions from groups configuration.
					permCollection = new Permissions();
					PermissionInfo[] permInfos = this.groupManager.getRolePermissionInfo(roleName);
					for (PermissionInfo permInfo : permInfos) {
						String typePermission = permInfo.getType();
						String name = permInfo.getName();
						String actions = permInfo.getActions();

						// Create specified permission.
						Class<?> clazz;
						Permission perm = null;
						try {
							clazz = Class.forName(typePermission);
							Constructor<?> constructor = getPermissionConstructor(clazz);
							Object[] args = { name, actions };
							perm = (Permission) constructor.newInstance(args);
						} catch (Exception e) {
							/* If the class isn't there,
							 * or if the constructor isn't corrected - we fail. */
							e.printStackTrace();//:FVK:
							continue;
						}
						permCollection.add(perm);
					}
					this.cachedPermissions.put(roleName, permCollection);
				}

				// Check permissions of Role.
				if (permCollection.implies(permission)) {
		            return true;
		        }
			}
		}
		return false;
	}	

	private Constructor<?> getPermissionConstructor(Class<?> clazz) {
		for (Constructor<?> checkConstructor : clazz.getConstructors()) {
			if (checkParameterTypes(checkConstructor.getParameterTypes())) {
				return checkConstructor;
			}
		}
		return null;
	}
	
	private boolean checkParameterTypes(Class<?>[] foundTypes) {
		if (foundTypes.length != permissionMethodArgs.length) {
			return false;
		}

		for (int i = 0; i < foundTypes.length; i++) {
			if (!foundTypes[i].isAssignableFrom(permissionMethodArgs[i])) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.elwiki.core.auth.IAuthorizationManager#resolvePrincipal(java.lang.String)
	 */
	@Override
	public Principal resolvePrincipal(String name) {
		GroupPrincipal role;
		Principal principal;

		// Check built-in Roles first
		role = new GroupPrincipal(name);
		if (GroupPrincipal.isBuiltInGroup(role)) {
			return role;
		}

		// Check Authorizer Roles
		principal = this.m_authorizer.findRole(name);
		if (principal != null) {
			return principal;
		}

		/*:FVK:
		// Check Groups
		principal = ServicesRefs.getGroupManager().findRole(name); // IGroupManager.class
		if (principal != null) {
			return principal;
		}

		// Ok, no luck---this must be a user principal
		UserProfile profile = null;
		IUserDatabase db = this.m_engine.getUserManager().getUserDatabase();
		try {
			profile = db.find(name);
			return new WikiPrincipal(profile.getUid(), WikiPrincipal.USED_ID);
		} catch (NoSuchPrincipalException e) {
			// We couldn't find the user...
		}
		*/

		// Ok, no luck---mark this as unresolved and move on
		return new UnresolvedPrincipal(name);
	}

	// -- events processing ---------------------------------------------------

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		switch (topic) {//:FVK:
		case ElWikiEventsConstants.TOPIC_LOGIN_ANONYMOUS: {
			break;
		}
		case ElWikiEventsConstants.TOPIC_LOGIN_ASSERTED: {
			break;
		}
		case ElWikiEventsConstants.TOPIC_LOGIN_AUTHENTICATED:
			break;
		}
	}

	@Override
	public /*:FVK: synchronized*/ void addWikiEventListener(WikiEventListener listener) {
		WikiEventManager.addWikiEventListener(this, listener);
	}

	@Override
	public /*:FVK: synchronized*/ void removeWikiEventListener(WikiEventListener listener) {
		WikiEventManager.removeWikiEventListener(this, listener);
	}

	/**
	 * Fires a WikiSecurityEvent of the provided type, user, and permission to all registered
	 * listeners.
	 *
	 * @see org.apache.wiki.event.WikiSecurityEvent
	 * @param type
	 *                   the event type to be fired
	 * @param user
	 *                   the user associated with the event
	 * @param permission
	 *                   the permission the subject must possess
	 */
	public void fireEvent(int type, Principal user, Object permission) {
		if (WikiEventManager.isListening(this)) {
			WikiEventManager.fireEvent(this, new WikiSecurityEvent(this, type, user, permission));
		}
	}

	@Deprecated
	@Override
	public void actionPerformed(WikiEvent event) {
	}

	@Override
	public boolean hasAccess(Context context, HttpServletResponse response, boolean redirect) throws IOException {
        //:FVK: final boolean allowed = checkPermission( context.getWikiSession(), context.requiredPermission() );

        // Stash the wiki context
        if ( context.getHttpRequest() != null && context.getHttpRequest().getAttribute( Context.ATTR_WIKI_CONTEXT ) == null ) {
            context.getHttpRequest().setAttribute( Context.ATTR_WIKI_CONTEXT, context );
        }

		return true;
		//:FVK: - здесь был код:
		/*
        final ResourceBundle rb = Preferences.getBundle( context, InternationalizationManager.CORE_BUNDLE );
        
        // If access not allowed, redirect
        if( !allowed && redirect ) {
            final Principal currentUser  = context.getWikiSession().getUserPrincipal();
            final String pageurl = context.getPage().getName();
            if( context.getWikiSession().isAuthenticated() ) {
                log.info( "User " + currentUser.getName() + " has no access - forbidden (permission=" + context.requiredPermission() + ")" );
                context.getWikiSession().addMessage( MessageFormat.format( rb.getString( "security.error.noaccess.logged" ),
                                                     context.getName()) );
            } else {
                log.info( "User " + currentUser.getName() + " has no access - redirecting (permission=" + context.requiredPermission() + ")" );
                context.getWikiSession().addMessage( MessageFormat.format( rb.getString("security.error.noaccess"), context.getName() ) );
            }
            response.sendRedirect( m_engine.getURL( ContextEnum.WIKI_LOGIN.getRequestContext(), pageurl, null ) );
        }
        
        return allowed;
		 */
	}
	
}
