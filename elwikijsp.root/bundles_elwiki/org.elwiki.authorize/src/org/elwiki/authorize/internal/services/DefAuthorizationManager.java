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
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
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
//import org.elwiki.api.IWikiConstants.StatusType;
//import org.elwiki.api.authorization.Group;
import org.osgi.service.useradmin.Group;
import org.elwiki.IWikiConstants.StatusType;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.IAuthorizer;
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
import org.elwiki.authorize.authenticated.AuthenticatedContextActivator;
import org.elwiki.authorize.check.AuthorizeCheckActivator;
import org.elwiki.authorize.condition.SessionTypeCondition;
//import org.elwiki.authorize.condition.SessionTypeCondition;
//import org.elwiki.authorize.anonymous.AnonymousActivator;
//import org.elwiki.authorize.asserted.AssertedActivator;
import org.elwiki.authorize.context.anonymous.AnonymousContextActivator;
import org.elwiki.authorize.context.asserted.AssertedContextActivator;
//import org.elwiki.authorize.authenticated.AuthenticatedActivator;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.authorize.internal.check.PolicyControl;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.Role;
import org.elwiki.data.authorize.UnresolvedPrincipal;
import org.elwiki.data.authorize.WikiPrincipal;
import org.elwiki.permissions.GroupPermission;
import org.elwiki.permissions.PagePermission;
import org.elwiki.permissions.WikiPermission;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.WikiPage;
import org.freshcookies.security.policy.LocalPolicy;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;

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
@Component(name = "elwiki.DefaultAuthorizationManager", service = AuthorizationManager.class, //
		factory = "elwiki.AuthorizationManager.factory")
public class DefAuthorizationManager implements AuthorizationManager , WikiEventListener {

	private static final Logger log = Logger.getLogger(DefAuthorizationManager.class);

	/** The extension ID for access to implementation set of {@link IAuthorizer}. */
	private static final String ID_EXTENSION_AUTHORIZER = "authorizer";

	/** Extension's specific ID of default external Authorizer. Current value - {@value} */
	protected static final String DEFAULT_AUTHORIZER = "WebContainerAuthorizer";

	/** The property name in jspwiki.properties for specifying the external {@link IAuthorizer}. */
	protected static final String PROP_AUTHORIZER = "jspwiki.authorizer";

	/** Name of the default security policy file, as bundle resource. */
	protected static final String DEFAULT_POLICY = "jspwiki.policy";

	private final Map<String, Class<? extends IAuthorizer>> authorizerClasses = new HashMap<>();

	private IAuthorizer m_authorizer = null;

	/** Cache for storing ProtectionDomains used to evaluate the local policy. */
	private Map<Principal, ProtectionDomain> m_cachedPds = new WeakHashMap<Principal, ProtectionDomain>();

	private LocalPolicy m_localPolicy = null;

	private Engine m_engine;

	private ConditionalPermissionAdmin cpaService;

	// == CODE ================================================================

	/**
	 * Constructs a new AuthorizationManager instance.
	 */
	public DefAuthorizationManager() {
		//
	}

	// -- service handling ---------------------------{start}--

	/** Stores configuration. */
	@Reference //(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private IAuthorizer groupManager;

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
		BundleContext bc = componentContext.getBundleContext();
		cpaService = bc.getService(bc.getServiceReference(ConditionalPermissionAdmin.class));

		/* TODO: place principals into file *.properties
		//
		// If preference data of InstantScope is empty - initialize it from resource. 
		//
		String[] prefsKeys;
		try {
			prefsKeys = this.preferences.keys();
		} catch (BackingStoreException | IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			prefsKeys = new String[0];
		}
		if (prefsKeys.length == 0) {
		}
		*/

		// Initialize security policy, from definitions in the resource file.
		try {
			String policyFileName = DEFAULT_POLICY;
			URL policyURL = findConfigFile(policyFileName, bc);
			if (policyURL != null) {
				File policyFile = new File(policyURL.toURI().getPath());
				log.debug("We found security policy URL:\n\t" + policyURL + "\n and transformed it to file name\n\t"
						+ policyFile.getAbsolutePath());
				String contentEncoding = this.wikiConfiguration.getContentEncoding();
				this.m_localPolicy = new LocalPolicy(policyFile, contentEncoding);
				this.m_localPolicy.refresh();
				log.info("Initialized default security policy, from: " + policyFile.getAbsolutePath());
			} else {
				String msg = "ElWiki was unable to initialize the default security policy (jspwiki.policy) file.\nInternal error.";
				WikiSecurityException wse = new WikiSecurityException(msg);
				log.fatal(msg, wse);
				throw wse;
			}
		} catch (Exception e) {
			log.error("Could not initialize local security policy: " + e.getMessage());
			throw new WikiException("Could not initialize local security policy: " + e.getMessage(), e);
		}

		/*
		{ // WORKAROUND - :FVK: вывод считанных принциаплов, определителей доступа для них.
			Class<? extends LocalPolicy> cl = this.m_localPolicy.getClass();
			Object v = null;
			try {
				Field field = cl.getDeclaredField("pds");
				field.setAccessible(true);
				v = field.get(this.m_localPolicy);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (v != null) {
				Set<LocalProtectionDomain> pd = (Set<LocalProtectionDomain>) v;
				for (LocalProtectionDomain lpd : pd) {
					for (Principal principal : lpd.getPrincipals()) {
						System.out.printf("%s \"%s\"\n", principal.getClass().getCanonicalName(), principal.getName());
					}
					PermissionCollection pc = lpd.getPermissions();
					// System.out.println(pc);
					Enumeration<Permission> permissions = pc.elements();
					while (permissions.hasMoreElements()) {
						Permission permission = permissions.nextElement();
						if (permission instanceof UnresolvedPermission) {
							UnresolvedPermission up = (UnresolvedPermission) permission;
							System.out.printf("    %s %s %s\n", up.getUnresolvedType(), up.getUnresolvedName(),
									up.getUnresolvedActions());
						}
					}
				}
			}
		}
		*/
		
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
	
	// -- service handling -----------------------------{end}--
	
	@Override
	public boolean checkPermission(Session session, Permission permission) {
		Function<Permission, Boolean> function = this::test1;

		if (session.isAnonymous()) {
			Boolean status = AnonymousContextActivator.test2(function, permission);
			//return status;
			return true; //:FVK: WORKAROUND 
		}
		if (session.isAsserted()) {
			Boolean status = AssertedContextActivator.test2(function, permission);
			//return status;
			return true; //:FVK: WORKAROUND
		}
		if (session.isAuthenticated()) {
			Boolean status = AuthenticatedContextActivator.test2(function, permission);
			//return status;
			return true; //:FVK: WORKAROUND
		}

		if (1 == 2) {
			PolicyControl.checkPermission();
		} else {
			log.info("--TEST-------------------------------------");

			/* Anonymous Context
			 */
			Bundle bundle = AnonymousContextActivator.getDefault().getBundle();
			@Nullable AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			/*
			if (!testPermission(acc, new FilePermission("test", "write"), false)) {
				System.out.println("01: File should not write.");
			}
			if(!testPermission(acc, new FilePermission("test", "read"), true)){
				System.out.println("02: File should be read.");
			}
			 */
			if (!testPermission(acc, new AllPermission(), false)) {
				System.out.println("03: AllPermission shoul be disabled.");
			}

			System.out.println("::01::" + test1(new AllPermission()));
			;
			System.out.println("::02::" + AnonymousContextActivator.test1(new AllPermission()));

			System.out.println("::03:: " + function.apply(new AllPermission()));
			Boolean res = AnonymousContextActivator.test2(function, new AllPermission());
			System.out.println("::04:: " + //
					AnonymousContextActivator.test2(function, new AllPermission()));

			//testPermission(acc, new PagePermission("page", "edit"), true);
			//testPermission(acc, new PagePermission("_:page", "view"), true);

			if (!testPermission(acc, new PagePermission("wiki:page", "edit"), false)) {
				System.out.println("05: Page should not be edit.");
			}
			if (!testPermission(acc, new PagePermission("wiki:page", "delete"), false)) {
				System.out.println("06: Page should not be deleted.");
			}
			if (!testPermission(acc, new PagePermission("wiki:page", "view"), true)) {
				System.out.println("07: Page should be view.");
			}
			System.out.println("::05:: " + //
					AnonymousContextActivator.test2(function, new PagePermission("wiki:page", "edit")));
			System.out.println("::06:: " + //
					AnonymousContextActivator.test2(function, new PagePermission("wiki:page", "delete")));
			System.out.println("::07:: " + //
					AnonymousContextActivator.test2(function, new PagePermission("wiki:page", "view")));

			/* Asserted Context
			 */
			bundle = AssertedContextActivator.getDefault().getBundle();
			acc = bundle.adapt(AccessControlContext.class);
			if (!testPermission(acc, new PagePermission("wiki:page", "edit"), true)) {
				System.out.println("10: Page should be edit.");
			}
			System.out.println("::10:: " + //
					AssertedContextActivator.test2(function, new PagePermission("wiki:page", "edit")));

			/* Authenticated Context
			 */
			bundle = AuthenticatedContextActivator.getDefault().getBundle();
			acc = bundle.adapt(AccessControlContext.class);
			if (!testPermission(acc, new GroupPermission("vfedorov:group", "view"), true)) {
				System.out.println("20: Group should be viewed.");
			}
			if (!testPermission(acc, new GroupPermission("elwiki:<groupmember>", "edit"), true)) {
				System.out.println("21: Group should be edited.");
			}
			System.out.println("::20:: " + //
					AuthenticatedContextActivator.test2(function, new GroupPermission("vfedorov:group", "view")));
			System.out.println("::21:: " + //
					AuthenticatedContextActivator.test2(function, new GroupPermission("elwiki:<groupmember>", "edit")));
		}

		if (1 == 1) {
			return true;
		}

		// ##############################################################################

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
				this.m_engine.getWikiConfiguration().getApplicationName());
		boolean hasAllPermission = testPermission(session, allPermission);
		if (hasAllPermission) {
			fireEvent(WikiSecurityEvent.ACCESS_ALLOWED, user, permission);
			return true;
		}

		// If the user doesn't have *at least* the permission
		// granted by policy, return false.
		boolean hasPolicyPermission = testPermission(session, permission);
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

	private Boolean test1(Permission permission1) {
		try {
			AccessController.checkPermission(permission1);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	/**
	 * @param acc
	 * @param permission
	 * @param expectedToPass
	 * @return статус проверки: true: условие верно; false: тест провален.
	 */
	private boolean testPermission(AccessControlContext acc, Permission permission, boolean expectedToPass) {
		try {
			SecurityManager sm = System.getSecurityManager();
			sm.checkPermission(permission, acc);
			return expectedToPass;
			//			if (!expectedToPass) {
			//				System.err.println("FAIL: test should not have the permission " + permission); //$NON-NLS-1$
			//			}
		} catch (AccessControlException e) {
			return !expectedToPass;
			//			if (expectedToPass) {
			//				System.err.println("FAIL: test should have the permission " + permission); //$NON-NLS-1$
			//			}
		}
	}

	/*
	AccessControlContext accAuthenticated = FrameworkUtil.getBundle(AuthenticatedContextActivator.class).adapt(AccessControlContext.class);
	AccessControlContext accAsserted = FrameworkUtil.getBundle(AssertedContextActivator.class).adapt(AccessControlContext.class);
	*/
	@Nullable
	AccessControlContext accAnonymous = FrameworkUtil.getBundle(AnonymousContextActivator.class)
			.adapt(AccessControlContext.class);

	private boolean testPermission(Session session, Permission permission) {
		try {
			// Check the Bundle-wide security policy first
			SecurityManager sm = System.getSecurityManager();
			sm.checkPermission(permission, this.accAnonymous);
			return true;
		} catch (AccessControlException e) {
			// Global policy denied the permission
		}
		// Try the local policy - check each Role/Group and User Principal
		if (allowedByLocalPolicy(session.getRoles(), permission)
				|| allowedByLocalPolicy(session.getPrincipals(), permission)) {
			return true;
		}
		return false;
	}

	//@Override
	public boolean checkPermission_(Session session, Permission permission) {
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
				this.m_engine.getWikiConfiguration().getApplicationName());
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
		WikiPage page = null; //:FVK: this.m_engine.getWikiEngine().getPage(pageName);
		Acl acl = null;  //:FVK: (page == null) ? null : this.m_engine.getAclManager().getPermissions(page);
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
		log.debug("Checking for principal: " + Arrays.toString(aclPrincipals));
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
		if (principal instanceof Role && Role.isBuiltInRole((Role) principal)) {
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
	public IAuthorizer getAuthorizer() throws WikiSecurityException {
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
	 * policy file. This method uses standard Java 2 security calls to do its work. Note that the
	 * current access control context's <code>codeBase</code> is effectively <em>this class</em>,
	 * not that of the caller. Therefore, this method will work best when what matters in the policy
	 * is <em>who</em> makes the permission check, not what the caller's code source is. Internally,
	 * this method works by executing <code>Subject.doAsPrivileged</code> with a privileged action
	 * that simply calls {@link java.security.AccessController#checkPermission(Permission)}.
	 * 
	 * @see AccessController#checkPermission(java.security.Permission) . A caught exception (or lack
	 *          thereof) determines whether the privilege is absent (or present).
	 * @param session
	 *                   the WikiSession whose permission status is being queried
	 * @param permission
	 *                   the Permission the Subject must possess
	 * @return <code>true</code> if the Subject possesses the permission, <code>false</code>
	 *             otherwise
	 */
	public boolean checkStaticPermission(final Session session, final Permission permission) {
		Boolean allowed = (Boolean) Session.doPrivileged(session, new PrivilegedAction<Boolean>() {
			@Override
			public Boolean run() {
				try {
					// Check the JVM-wide security policy first
					AccessController.checkPermission(permission);
					return Boolean.TRUE;
				} catch (AccessControlException e) {
					// Global policy denied the permission
				}

				// Try the local policy - check each Role/Group and User Principal
				if (allowedByLocalPolicy(session.getRoles(), permission)
						|| allowedByLocalPolicy(session.getPrincipals(), permission)) {
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
		});
		return allowed.booleanValue();
	}

	/**
	 * Checks to see if the local security policy allows a particular static Permission. Do not use
	 * this method for normal permission checks; use
	 * {@link #checkPermission(WikiSession, Permission)} instead.
	 * 
	 * @param principals
	 *                   the Principals to check
	 * @param permission
	 *                   the Permission
	 * @return the result
	 */
	public boolean allowedByLocalPolicy(Principal[] principals, Permission permission) {
		for (Principal principal : principals) {
			// Get ProtectionDomain for this Principal from cache, or create new one
			ProtectionDomain pd = this.m_cachedPds.get(principal);
			if (pd == null) {
				ClassLoader cl = this.getClass().getClassLoader();
				CodeSource cs = new CodeSource(null, (Certificate[]) null);
				pd = new ProtectionDomain(cs, null, cl, new Principal[] { principal });
				this.m_cachedPds.put(principal, pd);
			}

			// Consult the local policy and get the answer
			if (this.m_localPolicy.implies(pd, permission)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.elwiki.core.auth.IAuthorizationManager#resolvePrincipal(java.lang.String)
	 */
	@Override
	public Principal resolvePrincipal(String name) {
		Role role;
		Principal principal;

		// Check built-in Roles first
		role = new Role(name);
		if (Role.isBuiltInRole(role)) {
			return role;
		}

		// Check Authorizer Roles
		principal = this.m_authorizer.findRole(name);
		if (principal != null) {
			return principal;
		}

		/*:FVK:
		// Check Groups
		principal = ServicesRefs.getGroupManager().findRole(name); // IAuthorizer.class
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

	@Override
	public void actionPerformed(WikiEvent event) {
		if (event instanceof WikiSecurityEvent) {
			WikiSecurityEvent secEvent = (WikiSecurityEvent) event;
			if (secEvent.getTarget() != null) {
				switch (secEvent.getType()) {
				case WikiSecurityEvent.LOGIN_ANONYMOUS: {
					addPermissionsFor(StatusType.ANONYMOUS.name());
					break;
				}
				case WikiSecurityEvent.LOGIN_ASSERTED: {
					addPermissionsFor(StatusType.ASSERTED.name());
					break;
				}
				case WikiSecurityEvent.LOGIN_AUTHENTICATED: {
					addPermissionsFor(StatusType.AUTHENTICATED.name());
					break;
				}
				}
			}
		}
	}

	private static final PermissionInfo[] ANONYMOUS_PERMISSIONS = new PermissionInfo[] {
			new PermissionInfo(PagePermission.class.getName(), "*:*", "edit"),
			new PermissionInfo(WikiPermission.class.getName(), "*", "createPages") };

	private static final PermissionInfo[] ASSERTED_PERMISSIONS = new PermissionInfo[] {
			new PermissionInfo(PagePermission.class.getName(), "*:*", "edit"),
			new PermissionInfo(WikiPermission.class.getName(), "*", "createPages"),
			new PermissionInfo(GroupPermission.class.getName(), "*:*", "view") };

	private static final PermissionInfo[] AUTHENTICATED_PERMISSIONS = new PermissionInfo[] {
			new PermissionInfo(PagePermission.class.getName(), "*:*", "modify,rename"),
			new PermissionInfo(WikiPermission.class.getName(), "*", "createPages,createGroups"),
			new PermissionInfo(GroupPermission.class.getName(), "*:*", "view"),
			new PermissionInfo(GroupPermission.class.getName(), "*:<groupmember>", "edit"), };

	static boolean flag11=false; //:FVK: workaround
	
	private void addPermissionsFor(String cpiName) {
		log.debug("Commit permissions for \"" + cpiName + "\"");
//		ConditionalPermissionAdmin cpaService = AuthorizePluginActivator.getDefault().getCpaService();

		ConditionalPermissionUpdate cpUpdate = cpaService.newConditionalPermissionUpdate();
		List<ConditionalPermissionInfo> listInfo = cpUpdate.getConditionalPermissionInfos();
		// Проверка наличия в таблице требуемого ConditionalPermissionInfo.
		boolean isInfoExists = false;
		for (ConditionalPermissionInfo info : listInfo) {
			if (info.getName().equals(cpiName)) {
				isInfoExists = true;
				break;
			}
		}

		// Создать ConditionalPermissionInfo, добавить в список.
		if (!isInfoExists) {
			String bundleLocation = "*/org.elwiki.authorize.check.*"; 
					// AuthorizeCheckActivator.getContext().getBundle().getLocation();
			IAuthorizer groupManager = this.groupManager;

			if(!flag11) {
			//-- Add info of DENY AllPermission for context --
			listInfo.add(cpaService.newConditionalPermissionInfo("elwiki.context.denyAllPermission",
					new ConditionInfo[] {
							new ConditionInfo(
									BundleLocationCondition.class.getName(),
									new String[] { bundleLocation, "!" })
					},
					new PermissionInfo[] {
							new PermissionInfo("java.security.AllPermission", "<all permissions>", "<all actions>") },
//							new PermissionInfo("java.security.AllPermission", "*", "*") },						
					ConditionalPermissionInfo.ALLOW));
			flag11=true;
			}
			
			//@formatter:off
			listInfo.add(cpaService.newConditionalPermissionInfo(
					cpiName,
					new ConditionInfo[] {
							new ConditionInfo(
									SessionTypeCondition.class.getName(),
									new String[] { cpiName }),
							new ConditionInfo(
									BundleLocationCondition.class.getName(),
									new String[] { bundleLocation }),
					},
					groupManager.getRolePermissionInfo(cpiName),
					ConditionalPermissionInfo.ALLOW));
			//@formatter:on

			if (!cpUpdate.commit()) {
				log.error("Unsuccessful commit of ConditionalPermissionInfo \"" + cpiName + "\"");
			} else {
				log.debug("Commit permissions for \"" + cpiName + "\" - done.");
			}
		}
	}

	// -- service handling -------------------------------------------< start --

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
	private IAuthorizer getAuthorizerImplementation(String defaultAuthorizerId) throws WikiException {
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
						Class<? extends IAuthorizer> cl = clazz.asSubclass(IAuthorizer.class);
						this.authorizerClasses.put(authorizerId, (Class<? extends IAuthorizer>) cl);
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

		Class<? extends IAuthorizer> clazzAuthorizer = this.authorizerClasses.get(defaultAuthorizerId);
		if (clazzAuthorizer == null) {
			// TODO: это сообщение не к месту (логика не адекватна).
			throw new NoRequiredPropertyException("Unable to find an entry in the preferences.", PROP_AUTHORIZER);
		}

		IAuthorizer authorizer;
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

	@Override
	public boolean hasAccess(Context context, HttpServletResponse response, boolean redirect) throws IOException {
        //:FVK: final boolean allowed = checkPermission( context.getWikiSession(), context.requiredPermission() );

        // Stash the wiki context
        if ( context.getHttpRequest() != null && context.getHttpRequest().getAttribute( Context.ATTR_CONTEXT ) == null ) {
            context.getHttpRequest().setAttribute( Context.ATTR_CONTEXT, context );
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

	// -- service handling --------------------------------------------- end >--
	
}