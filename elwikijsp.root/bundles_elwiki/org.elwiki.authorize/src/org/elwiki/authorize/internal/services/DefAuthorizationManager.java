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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.AuthenticationManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.TextUtil;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.Authorizer;
import org.elwiki.api.component.WikiManager;
import org.elwiki.api.component.WikiPrefs;
import org.elwiki.api.event.WikiEventTopic;
import org.elwiki.api.event.WikiLoginEventTopic;
import org.elwiki.api.event.WikiSecurityEventTopic;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.Aprincipal;
import org.elwiki.data.authorize.GroupPrincipal;
import org.elwiki.data.authorize.UnresolvedPrincipal;
import org.elwiki.permissions.PagePermission;
import org.elwiki.permissions.PermissionFactory;
import org.elwiki_data.PageAclEntry;
import org.elwiki_data.WikiPage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.permissionadmin.PermissionInfo;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.IAuthenticationManager;
//import org.elwiki.api.IAuthorizationManager;
//import org.elwiki.api.IElWikiSession;
import org.osgi.service.useradmin.Group;

/**
 * <p>
 * Manages all access control and authorization; determines what authenticated users are allowed to
 * do.
 * </p>
 * <p>
 * Privileges in JSPWiki are expressed as Java-standard {@link java.security.Permission} classes.
 * There are two types of permissions:
 * </p>
 * <ul>
 * <li>{@link org.elwiki.permissions.WikiPermission} - privileges that apply to an entire wiki
 * instance: <em>e.g.,</em> editing user profiles, creating pages, creating groups</li>
 * <li>{@link org.elwiki.permissions.PagePermission} - privileges that apply to a single wiki page
 * or range of pages: <em>e.g.,</em> reading, editing, renaming
 * </ul>
 * <p>
 * Calling classes determine whether they are entitled to perform a particular action by
 * constructing the appropriate permission first, then passing it and the current
 * {@link org.elwiki.api.IElWikiSession} to the {@link #checkPermission(IElWikiSession, Permission)}
 * method. If the session's Subject possesses the permission, the action is allowed.
 * </p>
 * <p>
 * For WikiPermissions, the decision criteria is relatively simple: the caller either possesses the
 * permission, as granted by the wiki security policy -- or not.
 * </p>
 * <p>
 * For PagePermissions, the logic is exactly the same if the page being checked does not have an
 * access control list. However, if the page does have an ACL, the authorization decision is made
 * based the <em>union</em> of the permissions granted in the ACL and in the security policy. In
 * other words, the user must be named in the ACL (or belong to a group or role that is named in the
 * ACL) <em>and</em> be granted (at least) the same permission in the security policy. We do this to
 * prevent a user from gaining more permissions than they already have, based on the security
 * policy.
 * </p>
 * <p>
 * See the {@link #checkPermission(WikiSession, Permission)} and
 * {@link #hasRoleOrPrincipal(WikiSession, Principal)} methods for more information on the
 * authorization logic.
 * </p>
 * 
 * @see AuthorizationManager
 */
@SuppressWarnings("unused")
//@formatter:off
@Component(
	name = "elwiki.DefaultAuthorizationManager",
	service = {AuthorizationManager.class, WikiManager.class, EventHandler.class},
	//property = {
		//:FVK: property = EventConstants.EVENT_TOPIC + "=" + ElWikiEventsConstants.TOPIC_LOGGING_ALL)
	//},
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefAuthorizationManager implements AuthorizationManager, WikiPrefs, EventHandler {

	private static final Logger log = Logger.getLogger(DefAuthorizationManager.class);

	/** The extension ID for access to implementation set of {@link Authorizer}. */
	private static final String ID_EXTENSION_AUTHORIZER = "authorizer";

	/** Name of the default security policy file, as bundle resource. */
	protected static final String DEFAULT_POLICY = "jspwiki.policy";

	private static final Class<?>[] permissionMethodArgs = new Class[] { String.class, String.class };

	private final Map<String, Class<? extends Authorizer>> authorizerClasses = new HashMap<>();

	private Authorizer m_authorizer = null;

	/** Cache for storing PermissionCollections used to evaluate the local policy. */
	private Map<String, PermissionCollection> cachedPermissions = new HashMap<>();

	AuthorizationManagerOptions options;

	// == CODE ================================================================

	/**
	 * Constructs a new AuthorizationManager instance.
	 */
	public DefAuthorizationManager() {
		//
	}

	// -- OSGi service handling ----------------------(start)--

	@Reference
	protected EventAdmin eventAdmin;

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private Engine m_engine;
	
	@WikiServiceReference
	GlobalPreferences globalPrefs;

	@WikiServiceReference
	private AccountManager accountManager;

	@WikiServiceReference
	PageManager pageManager;

	@Activate
	protected void startup(BundleContext bundleContext) {
		options = new AuthorizationManagerOptions(bundleContext);
	}

	/**
	 * Initializes AuthorizationManager with an ApplicationSession and set of parameters.
	 * 
	 * Expects to find extension 'org.elwiki.auth.authorizer' with a valid Authorizer implementation to
	 * take care of role lookup operations.
	 */
	@Override
	public void initialize() throws WikiException {
		log.debug("Initialize.");
		options.initialize(m_engine);

		//
		//  JAAS authorization continues.
		//
		String authorizerName = options.getAuthorizer(); 
		this.m_authorizer = getAuthorizerImplementation(authorizerName);

		/*:FVK:
		this.m_authorizer.initialize(this.m_engine);

		// Make the AuthorizationManager listen for WikiEvents
		// from AuthenticationManager (WikiSecurityEvents for changed user profiles)
		m_engine.getAuthenticationManager().addWikiEventListener(this);
		*/
	}

	/**
	 * Looks up and obtains a policy configuration file inside this bundle area.
	 * 
	 * @param name the file to obtain, <em>e.g.</em>, <code>jspwiki.policy</code>
	 * @param bc   context of this bundle.
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

	/**
	 * Attempts to locate and initialize an Authorizer to use with this manager. Throws a WikiException
	 * if no entry is found, or if one fails to initialize.
	 * 
	 * @param requiredId required Authorizer ID for extension point.
	 * @return a Authorizer according to required ID.
	 * @throws WikiException
	 */
	private Authorizer getAuthorizerImplementation(String requiredId) throws WikiException {
		String namespace = AuthorizePluginActivator.getDefault().getBundle().getSymbolicName();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep;

		//
		// Load an Authorizer from Equinox extension "org.elwiki.authorize.authorizer".
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
						Class<? extends Authorizer> cl = clazz.asSubclass(Authorizer.class);
						this.authorizerClasses.put(authorizerId, (Class<? extends Authorizer>) cl);
					} catch (ClassCastException e) {
						log.fatal("Authorizer " + className + " is not extends interface "
								+ Authorizer.class.getSimpleName(), e);
						throw new WikiException("Authorizer " + className + " is not extends interface "
								+ Authorizer.class.getSimpleName(), e);
					}
				} catch (ClassNotFoundException e) {
					log.fatal("Authorizer " + className + " cannot be found.", e);
					throw new WikiException("Authorizer " + className + " cannot be found.", e);
				}
			}
		}

		Class<? extends Authorizer> clazzAuthorizer = this.authorizerClasses.get(requiredId);
		if (clazzAuthorizer == null) {
			throw new NoRequiredPropertyException("Unable to find Authorizer with ID=" + requiredId,
					options.getAuthorizerKey());
		}

		Authorizer authorizer = null;
		try {
			Class<?>[] parameterType = new Class[] { Engine.class };
			authorizer = clazzAuthorizer.getDeclaredConstructor(parameterType).newInstance(this.m_engine);
		} catch (InstantiationException | IllegalArgumentException e) {
			log.fatal("Authorizer " + clazzAuthorizer + " cannot be created.", e);
			throw new WikiException("Authorizer " + clazzAuthorizer + " cannot be created.", e);
		} catch (IllegalAccessException e) {
			log.fatal("You are not allowed to access authorizer class " + clazzAuthorizer, e);
			throw new WikiException("You are not allowed to access authorizer class " + clazzAuthorizer, e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return authorizer;
	}

	// -- OSGi service handling ------------------------(end)--

	@Override
	public boolean checkPermission(Session session, Permission permission) {
		//
		//  A slight sanity check.
		//
		if (session == null || permission == null) {
			eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_ACCESS_DENIED, Map.of( //
					WikiSecurityEventTopic.PROPERTY_USER, null, //
					WikiSecurityEventTopic.PROPERTY_PERMISSION, permission)));
			return false;
		}

		Principal user = session.getLoginPrincipal();

		// Always allow the action if user has AllPermission
		Permission allPermission = new org.elwiki.permissions.AllPermission(this.globalPrefs.getApplicationName(),
				null);
		boolean hasAllPermission = checkStaticPermission(session, allPermission);
		if (hasAllPermission) {
			eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_ACCESS_ALLOWED, Map.of( //
					WikiSecurityEventTopic.PROPERTY_USER, user, //
					WikiSecurityEventTopic.PROPERTY_PERMISSION, permission)));
			return true;
		}

		// If the user doesn't have *at least* the permission
		// granted by policy, return false.
		boolean hasPolicyPermission = checkStaticPermission(session, permission);
		if (!hasPolicyPermission) {
			eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_ACCESS_DENIED, Map.of( //
					WikiSecurityEventTopic.PROPERTY_USER, user, //
					WikiSecurityEventTopic.PROPERTY_PERMISSION, permission)));
			return false;
		}

		// If this isn't a PagePermission, it's allowed
		if (!(permission instanceof PagePermission)) {
			eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_ACCESS_ALLOWED, Map.of( //
					WikiSecurityEventTopic.PROPERTY_USER, user, //
					WikiSecurityEventTopic.PROPERTY_PERMISSION, permission)));
			return true;
		}

		//
		// If the page or ACL is null, it's allowed.
		//
		String pageName = ((PagePermission) permission).getPage();
		WikiPage page = pageManager.getPage(pageName);
		if (page == null || page.getPageAcl().size() == 0) {
			eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_ACCESS_ALLOWED, Map.of( //
					WikiSecurityEventTopic.PROPERTY_USER, user, //
					WikiSecurityEventTopic.PROPERTY_PERMISSION, permission)));
			return true;
		}

		//
		//  Next, iterate through the Principal objects assigned
		//  this permission. If the context's subject possesses
		//  any of these, the action is allowed.

		List<Principal> aclPrincipals = findPrincipals(page, permission);

		log.debug("Checking ACL entries...");
		log.debug("Checking for principals: " + aclPrincipals.toString());
		log.debug("Permission: " + permission);

		for (Principal aclPrincipal : aclPrincipals) {
			if (hasRoleOrPrincipal(session, aclPrincipal)) {
				eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_ACCESS_ALLOWED, Map.of( //
						WikiSecurityEventTopic.PROPERTY_USER, user, //
						WikiSecurityEventTopic.PROPERTY_PERMISSION, permission)));
				return true;
			}
		}

		eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_ACCESS_DENIED, Map.of( //
				WikiSecurityEventTopic.PROPERTY_USER, user, //
				WikiSecurityEventTopic.PROPERTY_PERMISSION, permission)));
		return false;
	}

	/**
	 * Get Principals of page ACL implied to specified permission.
	 * 
	 * @param page
	 * @param permission
	 * @return
	 */
	protected List<Principal> findPrincipals(WikiPage page, Permission permission) {
		List<Principal> principals = new ArrayList<>();

		for (PageAclEntry aclEntry : page.getPageAcl() ) {
			String permissionAction = aclEntry.getPermission();
			PagePermission pagePermission = PermissionFactory.getPagePermission(page, permissionAction);
			if (pagePermission.implies(permission)) {
				for (String role : aclEntry.getRoles()) {
					Principal principal = resolvePrincipal(role);
					principals.add(principal);
				}
			}
		}

		return principals;
	}

	/* (non-Javadoc)
	 * @see org.elwiki.core.auth.IAuthorizationManager#isUserInRole(org.elwiki.core.common.WikiSession, java.security.Principal)
	 */
	@Override
	public boolean isUserInRole(Session session, Principal principal) {
		if (session == null || principal == null || AuthenticationManager.isUserPrincipal(principal)) {
			return false;
		}

		// Any type of user can possess a built-in role
		if (principal instanceof GroupPrincipal && GroupPrincipal.isBuiltInGroup((GroupPrincipal) principal)) {
			return session.hasPrincipal(principal);
		}

		// Only authenticated users can possess groups or custom roles
		if (session.isAuthenticated() && AuthenticationManager.isRolePrincipal(principal)) {
			return session.hasPrincipal(principal);
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.elwiki.core.auth.IAuthorizationManager#getAuthorizer()
	 */
	@Override
	public Authorizer getAuthorizer() throws WikiSecurityException {
		if (this.m_authorizer != null) {
			return this.m_authorizer;
		}
		throw new WikiSecurityException("Authorizer did not initialize properly. Check the logs.");
	}

	/**
	 * <p>
	 * Determines if the Subject associated with a supplied WikiSession contains a desired user
	 * Principal or built-in Role principal, OR is a member a Group or external Role. The rules are as
	 * follows:
	 * </p>
	 * <ol>
	 * <li>First, if desired Principal is a Role or GroupPrincipal, delegate to
	 * {@link #isUserInRole(WikiSession, Group)} and return the result.</li>
	 * <li>Otherwise, we're looking for a user Principal, so iterate through the Principal set and see
	 * if any share the same name as the one we are looking for.</li>
	 * </ol>
	 * <p>
	 * <em>Note: if the Principal parameter is a user principal, the session must be authenticated in
	 * order for the user to "possess it". Anonymous or asserted sessions will never posseess a named
	 * user principal.</em>
	 * </p>
	 * 
	 * @param session   the current wiki session, which must be non-null. If null, the result of this
	 *                  method always returns <code>false</code>
	 * @param principal the Principal (role, group, or user principal) to look for, which must be
	 *                  non-null. If null, the result of this method always returns <code>false</code>
	 * @return <code>true</code> if the Subject supplied with the IWikiContext posesses the Role,
	 *         GroupPrincipal or desired user Principal, <code>false</code> otherwise
	 */
	public boolean hasRoleOrPrincipal(Session session, Principal principal) {
		// If either parameter is null, always deny
		if (session == null || principal == null) {
			return false;
		}

		// If principal is role, delegate to isUserInRole
		if (AuthenticationManager.isRolePrincipal(principal)) {
			return isUserInRole(session, principal);
		}

		// We must be looking for a user principal, assuming that the user
		// has been properly logged in.
		// So just look for a name match.
		if (session.isAuthenticated() && AuthenticationManager.isUserPrincipal(principal)) {
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
	 * @return <code>true</code> if the Subject possesses the permission, <code>false</code> otherwise
	 */
	public boolean checkStaticPermission(Session session, Permission permission) {
		// Try the local policy - check each Role/Group and User Principal
		return allowedByLocalPolicy(session.getRoles(), permission);
	}

	/**
	 * Checks to see if the wiki security policy allows a particular static Permission. Do not use this
	 * method for normal permission checks; use {@link #checkPermission(Session, Permission)} instead.
	 * 
	 * @param principals the Principals to check. Only handles wiki's principals (Role, Group). User
	 *                   principals can not has permission info - they can't be handled.
	 * @param permission the Permission.
	 * @return the result
	 */
	public boolean allowedByLocalPolicy(Principal[] principals, Permission permission) {
		for (Principal principal : principals) {
			if (principal instanceof Aprincipal aprincipal) {
				String roleName = aprincipal.getUid();
				PermissionCollection permCollection;
				if (cachedPermissions.containsKey(roleName)) {
					permCollection = cachedPermissions.get(roleName);
				} else {
					// Instantiates permissions from groups configuration.
					permCollection = new Permissions();
					PermissionInfo[] permInfos = this.accountManager.getRolePermissionInfo(roleName);
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
	public Principal resolvePrincipal(String groupName) {
		GroupPrincipal role;
		Principal principal = null;

		// Check built-in Roles first
		String uid = this.accountManager.getGroupUid(groupName); //:FVK: workaround - get group by its name, for take group UID
		if (uid == null) {
			return new UnresolvedPrincipal(groupName);
		}

		role = new GroupPrincipal(groupName, uid);
		if (GroupPrincipal.isBuiltInGroup(role)) {
			return role;
		}

		// Check Authorizer Roles
		principal = this.m_authorizer.findRole(groupName);
		if (principal != null) {
			return principal;
		}

		/*:FVK:
		// Check Groups
		principal = Engine.getAccountManager().findRole(name); // IGroupManager.class
		if (principal != null) {
			return principal;
		}

		// Ok, no luck---this must be a user principal
		UserProfile profile = null;
		IUserDatabase db = this.m_engine.getUserManager().get User Database();
		try {
			profile = db.find(name);
			return new WikiPrincipal(profile.getUid(), WikiPrincipal.USED_ID);
		} catch (NoSuchPrincipalException e) {
			// We couldn't find the user...
		}
		*/

		// Ok, no luck---mark this as unresolved and move on
		return new UnresolvedPrincipal(groupName);
	}

	@Override
	public void checkAccess(WikiContext wikiContext, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws Exception {
		boolean isAllowed = checkPermission(wikiContext.getWikiSession(), wikiContext.requiredPermission());

		if (!isAllowed) {
			Session wikiSession = wikiContext.getWikiSession();
			Permission requiredPermission = wikiContext.requiredPermission();
			Principal currentUser = wikiSession.getUserPrincipal();
			ResourceBundle rb = Preferences.getBundle(wikiContext);
			if (wikiContext.getWikiSession().isAuthenticated()) {
				log.info("User " + currentUser.getName() + " has no access - forbidden (permission="
						+ requiredPermission + ")");
				httpRequest.setAttribute(WikiContext.ATTR_MESSAGE,
						MessageFormat.format(rb.getString("security.error.pageaccess.logged"), wikiContext.getName()));
			} else {
				log.info("User " + currentUser.getName() + " has no access - redirecting (permission="
						+ requiredPermission + ")");
				httpRequest.setAttribute(WikiContext.ATTR_MESSAGE,
						MessageFormat.format(rb.getString("security.error.pageaccess"), wikiContext.getName()));
			}
			String url = m_engine.getURL(ContextEnum.WIKI_MESSAGE.getRequestContext(), wikiContext.getPage().getId(),
					null);
			ServletContext sc = httpRequest.getServletContext().getContext(url);
			RequestDispatcher rd = sc.getRequestDispatcher(url);
			httpRequest.setAttribute(WikiContext.ATTR_FORWARD_REQUEST, url);
			rd.forward(httpRequest, httpResponse);
		}
	}

	@Deprecated
	@Override
	public boolean hasAccess(WikiContext context, HttpServletResponse response, boolean redirect) throws IOException {
		boolean isAllowed = checkPermission(context.getWikiSession(), context.requiredPermission());
		ResourceBundle rb = Preferences.getBundle(context);

		// Stash the wiki context (:FVK: this is same in the JspServletFilter - here should be removed.)
		/*:FVK:
		if (context.getHttpRequest() != null
				&& context.getHttpRequest().getAttribute(WikiContext.ATTR_WIKI_CONTEXT) == null) {
			context.getHttpRequest().setAttribute(WikiContext.ATTR_WIKI_CONTEXT, context);
		}
		*/

		// If access not allowed, redirect
		if (!isAllowed && redirect) {
			Principal currentUser = context.getWikiSession().getUserPrincipal();
			String pageurl = context.getPage().getName();
			if (context.getWikiSession().isAuthenticated()) {
				log.info("User " + currentUser.getName() + " has no access - forbidden (permission="
						+ context.requiredPermission() + ")");
				context.getWikiSession().addMessage(
						MessageFormat.format(rb.getString("security.error.noaccess.logged"), context.getName()));
			} else {
				log.info("User " + currentUser.getName() + " has no access - redirecting (permission="
						+ context.requiredPermission() + ")");
				context.getWikiSession()
						.addMessage(MessageFormat.format(rb.getString("security.error.noaccess"), context.getName()));
			}
			response.sendRedirect(m_engine.getURL(ContextEnum.WIKI_LOGIN.getRequestContext(), pageurl, null));
		}

		return isAllowed;
	}

	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
		}*/
	}

	@Override
	public String getConfigurationEntry() {
		String jspItems = options.getConfigurationJspPage();
		return jspItems;
	}

}
