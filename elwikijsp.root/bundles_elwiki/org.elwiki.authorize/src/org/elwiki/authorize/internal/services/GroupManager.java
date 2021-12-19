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

import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.event.WikiEvent;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiSecurityEvent;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.UserManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user0.UserDatabase;
import org.apache.wiki.auth.user0.UserProfile;
import org.apache.wiki.ui.InputValidator;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.IPreferenceStore;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.IAuthenticationManager;
//import org.elwiki.api.IElWikiSession;
//import org.elwiki.api.IUserManager;
//import org.elwiki.api.IWikiContext;
//import org.elwiki.api.IWikiEngine;

import org.elwiki.api.authorization.IAuthorizer;
import org.elwiki.api.authorization.WrapGroup;
import org.elwiki.api.authorization.authorize.GroupDatabase;
//import org.elwiki.api.authorization.user.UserProfile;
//import org.elwiki.api.event.WikiEvent;
//import org.elwiki.api.event.WikiEventListener;
//import org.elwiki.api.event.WikiEventManager;
//import org.elwiki.api.event.WikiSecurityEvent;
//import org.elwiki.api.exceptions.NoRequiredPropertyException;
//import org.elwiki.api.exceptions.NoSuchPrincipalException;
//import org.elwiki.api.exceptions.WikiException;
//import org.elwiki.api.exceptions.WikiSecurityException;
//import org.elwiki.api.ui.InputValidator;
import org.elwiki.authorize.XMLGroupDatabase;
import org.elwiki.authorize.internal.bundle.InternalClassUtil;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.data.authorize.GroupPrincipal;
import org.elwiki.data.authorize.WikiPrincipal;
import org.elwiki.services.ServicesRefs;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.UserAdmin;

/**
 * <p>
 * Facade class for storing, retrieving and managing wiki groups on behalf of
 * AuthorizationManager, JSPs and other presentation-layer classes. GroupManager works in
 * collaboration with a back-end {@link GroupDatabase}, which persists groups to permanent
 * storage.
 * </p>
 * <p>
 * <em>Note: prior to JSPWiki 2.4.19, GroupManager was an interface; it is now a concrete, final
 * class. The aspects of GroupManager which previously extracted group information from storage
 * (e.g., wiki pages) have been refactored into the GroupDatabase interface.</em>
 * </p>
 */
/* For ElWiki reviewed by Victor Fedorov. */
public class GroupManager implements IAuthorizer {

	private static final Logger log = Logger.getLogger(GroupManager.class);

	protected Engine m_engine;

	//@SuppressWarnings("unused")
	protected WikiEventListener m_groupListener;

	@Deprecated
	private GroupDatabase m_groupDatabase = null;

	/** Map with GroupPrincipals as keys, and Groups as values */
	//:FVK: private final Map<Principal, Group> m_groups = new HashMap<Principal, Group>();

	//:FVK: private IApplicationSession applicationSession;

	private UserAdmin userAdminService;

	// == CODE ================================================================

	@Override
	public List<Group> getRoles() {
		List<Group> groups = new ArrayList<>();
		try {
			for (Role role : this.userAdminService.getRoles(null)) {
				if (role instanceof Group) {
					groups.add((Group) role);
				}
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groups; // :FVK: this.m_groups.keySet().toArray(new Principal[this.m_groups.size()]);
	}

	@Override
	public Principal findRole(String roleName) {
		/*try {
			Group group = getGroup(roleName);
			return group.getPrincipal();
		} catch (NoSuchPrincipalException e) {
			return null;
		}*/
		return null;
	}

	/**
	 * Determines whether the Subject associated with a WikiSession is in a particular role. This
	 * method takes two parameters: the WikiSession containing the subject and the desired role (
	 * which may be a Role or a Group). If either parameter is <code>null</code>, or if the user is
	 * not authenticated, this method returns <code>false</code>.
	 * <p>
	 * With respect to this implementation, the supplied Principal must be a GroupPrincipal. The
	 * Subject posesses the "role" if it the session is authenticated <em>and</em> a Subject's
	 * principal is a member of the corresponding Group. This method simply finds the Group in
	 * question, then delegates to {@link Group#isMember(Principal)} for each of the principals in
	 * the Subject's principal set.
	 * 
	 * @param session
	 *                the current WikiSession.
	 * @param group
	 *                the role to check.
	 * @return <code>true</code> if the user is considered to be in the role, <code>false</code>
	 *             otherwise.
	 */
	//@Override
	public boolean isUserInRole(Session session, Group group) {
		// Always return false if session/role is null,
		//  or if role isn't a GroupPrincipal.
		if (session == null || group == null || !session.isAuthenticated()) {
			return false;
		}

		User user = session.getUser();
		Authorization auth = this.userAdminService.getAuthorization(user);
		return auth.hasRole(group.getName());
	}

	@Override
	public boolean isUserInGroup(String attrValue, Group group) {
		/*:FVK:
		try {
			UserManager um = this.applicationSession.getUserManager();
			UserProfile profile = um.getUserDatabase().find(attrValue);
			User user = profile.getAdapter(User.class);
			Authorization auth = this.userAdminService.getAuthorization(user);
			return auth.hasRole(group.getName());
		} catch (NoSuchPrincipalException e) {
			// ignored.
		}
		 */

		return false;
	}

	/* :FVK:
	@Override
	public Group getGroup(String name) throws NoSuchPrincipalException {
		Group group = this.m_groups.get(new GroupPrincipal(name));
		if (group != null) {
			return group;
		}
		throw new NoSuchPrincipalException("Group " + name + " not found.");
	}
	*/

	@SuppressWarnings("unchecked")
	//:FVK: @Override
	public WrapGroup parseGroup(Session session, String groupName, String memberLine_, boolean create)
			throws WikiSecurityException {
		String name = groupName;
		String memberLine = memberLine_;
		// If null name parameter, it's because someone's creating a new group
		if (name == null) {
			if (create) {
				name = "MyGroup";
			} else {
				throw new WikiSecurityException("Group name cannot be blank.");
			}
		} else if (ArrayUtils.contains(IAuthorizer.RESTRICTED_GROUPNAMES, name)) {
			// Certain names are forbidden
			throw new WikiSecurityException("Illegal group name: " + name);
		}
		name = name.trim();

		// Normalize the member line
		if (InputValidator.isBlank(memberLine)) {
			memberLine = "";
		}
		memberLine = memberLine.trim();
		
		WrapGroup group = null;
		/*TODO: :FVK: - рассмотреть...
		// Create or retrieve the group (may have been previously cached).
		group = (Group) this.userAdminService.createRole(name, Role.GROUP);
		if (group != null) {
			// Creats new group
			String creator = (session.getUserPrincipal() == null) ? "foo" : session.getUserPrincipal().getName(); // :FVK: workaround 'foo'
			group.getProperties().put(CREATOR, creator);
			group.getProperties().put(CREATED, this.m_format.format(new Date(System.currentTimeMillis())));
		} else {
			// Modify existed group.
			group = (Group) this.userAdminService.getRole(name);
			String modifier = (session.getUserPrincipal() == null) ? "foo" : session.getUserPrincipal().getName(); // :FVK: workaround 'foo'
			group.getProperties().put(MODIFIER, modifier);
			group.getProperties().put(LAST_MODIFIED, this.m_format.format(new Date(System.currentTimeMillis())));
			Role[] members = group.getMembers();
			if (members != null) {
				for (Role role : members) { // :FVK: workaround - possibly needed group.getRequiredMembers()
					group.removeMember(role);
				}
			}
		}

		// If passed members not empty, overwrite
		UserManager um = ServicesRefs.getUserManager();
		String[] members = extractMembers(memberLine);
		for (String member : members) {
			UserProfile userProfile = um.getUserDatabase().find(member);

			User user;
			try {
				user = userProfile.getAdapter(User.class);
				group.addMember(user);
			} catch (Exception e) {
				// wrong user profile
				throw new WikiSecurityException("Illegal member name: " + member);
			}
		}
		*/
		return group;
	}

	/**
	 * Extracts group name and members from the HTTP request and populates an existing Group with
	 * them. The Group will either be a copy of an existing Group (if one can be found), or a new,
	 * unregistered Group (if not). Optionally, this method can throw a WikiSecurityException if the
	 * Group does not yet exist in the GroupManager cache.
	 * <p>
	 * The <code>group</code> parameter in the HTTP request contains the Group name to look up and
	 * populate. The <code>members</code> parameter contains the member list. If these differ from
	 * those in the existing group, the passed values override the old values.
	 * <p>
	 * This method does not commit the new Group to the GroupManager cache. To do that, use
	 * {@link #setGroup(WikiSession, Group)}.
	 * 
	 * @param context
	 *                the current wiki context
	 * @param create
	 *                whether this method should create a new, empty Group if one with the requested
	 *                name is not found. If <code>false</code>, groups that do not exist will cause
	 *                a <code>NoSuchPrincipalException</code> to be thrown
	 * @return a new, populated group
	 * @throws WikiSecurityException
	 *                               if the group name isn't allowed, or if <code>create</code> is
	 *                               <code>false</code> and the Group does not exist
	 */
	// :FVK: этот метод используется только в JSP файлах.
	@Override
	public WrapGroup parseGroup(Context context, boolean create) throws WikiSecurityException {
		// Extract parameters
		HttpServletRequest request = context.getHttpRequest();
		String name = request.getParameter("group");
		String memberLine = request.getParameter("members");

		// Create the named group; we pass on any NoSuchPrincipalExceptions
		// that may be thrown if create == false, or WikiSecurityExceptions
		WrapGroup wrapGroup = null;
		/*:FVK:
		WrapGroup group = parseGroup(null, name, memberLine, create);
		*/
		Object role = this.userAdminService.getUser(UserDatabase.GROUP_NAME, name);
		if (role instanceof Group) {
			wrapGroup = new WrapGroup((Group) role);
		} else {
			// TODO: обработать отсутствие группы.
		}
		/*:FVK: // If no members, add the current user by default
		if (group.members().length == 0) {
			group.add(context.getWikiSession().getUserPrincipal());
		}*/

		return wrapGroup;
	}

	@Override
	// :FVK: этот метод нигде не используется (только в тестах) -- см. JSP файлы.
	public void removeGroup(String index) throws WikiSecurityException {
		if (index == null) {
			throw new IllegalArgumentException("Group cannot be null.");
		}

		//:FVK:		Group group = this.m_groups.get(new GroupPrincipal(index));
		//		if (group == null) {
		//			throw new NoSuchPrincipalException("Group " + index + " not found");
		//		}

		// Delete the group
		// TODO: need rollback procedure
		//:FVK:		synchronized (this.m_groups) {
		//			this.m_groups.remove(group.getPrincipal());
		//		}
		//		this.m_groupDatabase.delete(group);
		//		fireEvent(WikiSecurityEvent.GROUP_REMOVE, group);
	}

	@Override
	public void setGroup(Session session, WrapGroup group) throws WikiSecurityException {
		// TODO: check for appropriate permissions

		// If group already exists, delete it; fire GROUP_REMOVE event
		/*Group oldGroup = this.m_groups.get(group.getPrincipal());
		if (oldGroup != null) {
			fireEvent(WikiSecurityEvent.GROUP_REMOVE, oldGroup);
			synchronized (this.m_groups) {
				this.m_groups.remove(oldGroup.getPrincipal());
			}
		}*/

		// Copy existing modifier info & timestamps
		/*if (oldGroup != null) {
			group.setCreator(oldGroup.getCreator());
			group.setCreated(oldGroup.getCreated());
			group.setModifier(oldGroup.getModifier());
			group.setLastModified(oldGroup.getLastModified());
		}*/

		// Add new group to cache; announce GROUP_ADD event
		/*synchronized (this.m_groups) {
			this.m_groups.put(group.getPrincipal(), group);
		}*/
		fireEvent(WikiSecurityEvent.GROUP_ADD, group);

		// Save the group to back-end database; if it fails,
		// roll back to previous state. Note that the back-end
		// MUST timestamp the create/modify fields in the Group.
		/*
		try {
			this.m_groupDatabase.save(group, session.getUserPrincipal());
		}
		// We got an exception! Roll back...
		catch (WikiSecurityException e) {
			if (oldGroup != null) {
				// Restore previous version, re-throw...
				fireEvent(WikiSecurityEvent.GROUP_REMOVE, group);
				fireEvent(WikiSecurityEvent.GROUP_ADD, oldGroup);
				synchronized (this.m_groups) {
					this.m_groups.put(oldGroup.getPrincipal(), oldGroup);
				}
				throw new WikiSecurityException(e.getMessage() + " (rolled back to previous version).", e);
			}
			// Re-throw security exception
			throw new WikiSecurityException(e.getMessage(), e);
		}
		*/
	}

	/**
	 * Validates a Group, and appends any errors to the session errors list. Any validation errors
	 * are added to the wiki session's messages collection (see {@link WikiSession#getMessages()}.
	 * 
	 * @param context
	 *                the current wiki context.
	 * @param group
	 *                the supplied Group.
	 */
	// :FVK: этот метод нигде используется -- в JSP файле.
	@Override
	public void validateGroup(Context context, WrapGroup group) {
		InputValidator validator = new InputValidator(MESSAGES_KEY, context);

		// Name cannot be null or one of the restricted names
		try {
			checkGroupName(context, group.getName());
		} catch (WikiSecurityException e) {
			//TODO: ...
		}

		// Member names must be "safe" strings
		//:FVK: заменил Principal на String.
		String[] members = group.members();
		for (String member : members) {
			validator.validateNotNull(member, "Full name", InputValidator.ID);
		}
	}

	/**
	 * Extracts carriage-return separated members into a Set of String objects.
	 * 
	 * @param memberLine
	 *                   the list of members.
	 * @return the list of members.
	 */
	protected String[] extractMembers(String memberLine) {
		Set<String> members = new HashSet<String>();
		if (memberLine != null) {
			StringTokenizer tok = new StringTokenizer(memberLine, "\n");
			while (tok.hasMoreTokens()) {
				String uid = tok.nextToken().trim();
				if (uid != null && uid.length() > 0) {
					members.add(uid);
				}
			}
		}
		return members.toArray(new String[members.size()]);
	}

	/**
	 * Checks if a String is blank or a restricted Group name, and if it is, appends an error to the
	 * WikiSession's message list.
	 * 
	 * @param context
	 *                the wiki context.
	 * @param name
	 *                the Group name to test.
	 * @throws WikiSecurityException
	 *                               if <code>session</code> is <code>null</code> or the Group name
	 *                               is illegal.
	 * @see Group#RESTRICTED_GROUPNAMES
	 */
	protected void checkGroupName(Context context, String name) throws WikiSecurityException {
		//TODO: groups cannot have the same name as a user

		// Name cannot be null
		InputValidator validator = new InputValidator(MESSAGES_KEY, context);
		validator.validateNotNull(name, "Group name");

		// Name cannot be one of the restricted names either
		//:FVK:		if (ArrayUtils.contains(Group.RESTRICTED_GROUPNAMES, name)) {
		//			throw new WikiSecurityException("The group name '" + name + "' is illegal. Choose another.");
		//		}
	}

	// -- public stuff --------------------------------------------------------

	/**
	 * Returns the current external {@link GroupDatabase} in use. This method is guaranteed to
	 * return a properly-initialized GroupDatabase, unless it could not be initialized. In that
	 * case, this method throws a
	 * {@link org.elwiki.core.api.exceptions.wiki.api.exceptions.WikiException}. The GroupDatabase
	 * is lazily initialized.
	 * 
	 * @throws org.elwiki.core.api.exceptions.wiki.auth.WikiSecurityException
	 *                                                                        if the GroupDatabase
	 *                                                                        could not be
	 *                                                                        initialized
	 * @return the current GroupDatabase
	 */
	// :FVK: включить этот метод в интерфейс, но для этого следует поработать, так как Authorizer объединяет и WebAuth и этот менеджер ролей...
	public GroupDatabase getGroupDatabase() throws WikiSecurityException {
		if (this.m_groupDatabase != null) {
			return this.m_groupDatabase;
		}

		String dbClassName = "<unknown>";
		String dbInstantiationError = null;
		Throwable cause = null;
		IPreferenceStore properties = this.m_engine.getWikiConfiguration().getWikiPreferences();
		try {
			dbClassName = properties.getString(PROP_GROUPDATABASE);
			if (dbClassName == null) {
				dbClassName = XMLGroupDatabase.class.getName();
			}
			log.info("Attempting to load group database class " + dbClassName);
			Class<?> dbClass = InternalClassUtil.findClass(AuthorizePluginActivator.AUTHORIZATION_PACKAGE, dbClassName);
			this.m_groupDatabase = (GroupDatabase) dbClass.newInstance();
//:FVK:			this.m_groupDatabase.initialize(this.applicationSession);
			log.info("Group database initialized.");
		} catch (ClassNotFoundException e) {
			log.error("GroupDatabase class " + dbClassName + " cannot be found.", e);
			dbInstantiationError = "Failed to locate GroupDatabase class " + dbClassName;
			cause = e;
		} catch (InstantiationException e) {
			log.error("GroupDatabase class " + dbClassName + " cannot be created.", e);
			dbInstantiationError = "Failed to create GroupDatabase class " + dbClassName;
			cause = e;
		} catch (IllegalAccessException e) {
			log.error("You are not allowed to access group database class " + dbClassName + ".", e);
			dbInstantiationError = "Access GroupDatabase class " + dbClassName + " denied";
			cause = e;
		}/*:FVK:  catch (NoRequiredPropertyException e) {
			log.error("Missing property: " + e.getMessage() + ".");
			dbInstantiationError = "Missing property: " + e.getMessage();
			cause = e;
		}*/

		if (dbInstantiationError != null) {
			throw new WikiSecurityException(
					dbInstantiationError + " Cause: " + (cause != null ? cause.getMessage() : ""), cause);
		}

		return this.m_groupDatabase;
	}

	// events processing .......................................................

	//:FVK: @Override
	public synchronized void addWikiEventListener(WikiEventListener listener) {
		WikiEventManager.addWikiEventListener(this, listener);
	}

	//:FVK: @Override
	public synchronized void removeWikiEventListener(WikiEventListener listener) {
		WikiEventManager.removeWikiEventListener(this, listener);
	}

	/**
	 * Fires a WikiSecurityEvent of the provided type, Principal and target Object to all registered
	 * listeners.
	 *
	 * @see org.elwiki.api.event.wiki.event.WikiSecurityEvent
	 * @param type
	 *               the event type to be fired
	 * @param target
	 *               the changed Object, which may be <code>null</code>
	 */
	protected void fireEvent(int type, Object target) {
		if (WikiEventManager.isListening(this)) {
			WikiEventManager.fireEvent(this, new WikiSecurityEvent(this, type, target));
		}
	}

	/**
	 * Listens for {@link org.elwiki.api.event.wiki.event.WikiSecurityEvent#PROFILE_NAME_CHANGED}
	 * events. If a user profile's name changes, each group is inspected. If an entry contains a
	 * name that has changed, it is replaced with the new one. No group events are emitted as a
	 * consequence of this method, because the group memberships are still the same; it is only the
	 * representations of the names within that are changing.
	 * 
	 * @param event
	 *              the incoming event
	 */
	//:FVK: @Override
	public void actionPerformed(WikiEvent event) {
		if (!(event instanceof WikiSecurityEvent)) {
			return;
		}

		WikiSecurityEvent se = (WikiSecurityEvent) event;
		if (se.getType() == WikiSecurityEvent.PROFILE_NAME_CHANGED) {
			Session session = se.getSrc();
			UserProfile[] profiles = (UserProfile[]) se.getTarget();
			Principal[] oldPrincipals = new Principal[] { new WikiPrincipal(profiles[0].getLoginName()),
					new WikiPrincipal(profiles[0].getFullname()), new WikiPrincipal(profiles[0].getWikiName()) };
			Principal newPrincipal = new WikiPrincipal(profiles[1].getFullname());

			// Examine each group
			int groupsChanged = 0;
			// здесь сохраняется изменение в группе, при изменении профиля пользователя. 
			//:FVK:			try {
			//				for (Group group : this.m_groupDatabase.groups()) {
			//					boolean groupChanged = false;
			//					for (Principal oldPrincipal : oldPrincipals) {
			//						if (group.isMember(oldPrincipal)) {
			//							group.remove(oldPrincipal);
			//							group.add(newPrincipal);
			//							groupChanged = true;
			//						}
			//					}
			//					if (groupChanged) {
			//						setGroup(session, group);
			//						groupsChanged++;
			//					}
			//				}
			//			} catch (WikiException e) {
			//				// Oooo! This is really bad...
			//				log.error("Could not change user name in Group lists because of GroupDatabase error:" + e.getMessage());
			//			}
			log.info("Profile name change for '" + newPrincipal.toString() + "' caused " + groupsChanged
					+ " groups to change also.");
		}
	}

	// -- service support -----------------------------------------------------

	public synchronized void startup(BundleContext bc) throws WikiException {
		ServiceReference<?> ref = bc.getServiceReference(UserAdmin.class.getName());
		if (ref != null) {
			this.userAdminService = (UserAdmin) bc.getService(ref);
		}
	}

	/**
	 * Initializes the group cache by initializing the group database and obtaining a list of all of
	 * the groups it stores.
	 * 
	 * @see GroupDatabase#initialize()
	 * @see GroupDatabase#groups()
	 * @throws WikiSecurityException
	 *                               if GroupManager cannot be initialized
	 */
	/*:FVK: 
	@Override
	public void initialize(IApplicationSession applicationSession1) throws WikiSecurityException {
		this.applicationSession = applicationSession1;
		this.m_engine = this.applicationSession.getWikiEngine();

		/ *:FVK: - group Database - устарело (XML...)
		try {
			this.m_groupDatabase = getGroupDatabase();
		} catch (WikiException e) {
			throw new WikiSecurityException(e.getMessage(), e);
		}
		* /

		// Load all groups from the database into the cache
		//:FVK:		Group[] groups = this.m_groupDatabase.groups();
		//		synchronized (this.m_groups) {
		//			for (Group group : groups) {
		//				// Add new group to cache; fire GROUP_ADD event
		//				this.m_groups.put(group.getPrincipal(), group);
		//				fireEvent(WikiSecurityEvent.GROUP_ADD, group);
		//			}
		//		}

		// Make the GroupManager listen for WikiEvents (WikiSecurityEvents for changed user profiles)
		//:FVK: this.applicationSession.getUserManager().addWikiEventListener(this);

		// Success!
		//:FVK:		log.info("Authorizer GroupManager initialized successfully; loaded " + groups.length + " group(s).");
	}
	*/

	public synchronized void shutdown() {
		//
	}

	@Override
	public Group getGroup(String groupName) {
		Group group = (Group) this.userAdminService.getRole(groupName);

		return group;
	}

	/* @Deprecated comment
	(org.elwiki.permissions.PagePermission "*:*" "view")
	(org.elwiki.permissions.WikiPermission "*" "createPages")
	
	(org.elwiki.permissions.PagePermission "*:*" "edit")
	(org.elwiki.permissions.WikiPermission "*" "createPages")
	(org.elwiki.permissions.GroupPermission "*:*" "view")
	
	(org.elwiki.permissions.PagePermission "*:*" "modify,rename")
	(org.elwiki.permissions.WikiPermission "*" "createPages,createGroups")
	(org.elwiki.permissions.GroupPermission "*:*" "view")
	String chrLevel1 = "\u2666"; // \u2666 == '♦'
	*/
	@Override
	public PermissionInfo[] getRolePermissionInfo(String roleName) {
		Role role = userAdminService.getUser("groupName", roleName); //:FVK: workaround.
		if (!(role instanceof Group)) {
			throw new IllegalArgumentException("Required role \"" + roleName + "\" is not Group.");
		}
		Group group = (Group) role;

		String permissions = (String) group.getProperties().get("PERMISSIONS");//:FVK: workaround
		String splitChar = "\u2666"; // \u2666 == '♦'
		Pattern pattern = Pattern.compile(splitChar);
		List<PermissionInfo> listPi = new ArrayList<>();
		for (String encodedPermission : pattern.split(permissions)) {
			listPi.add(new PermissionInfo(encodedPermission));
		}
		return listPi.toArray(new PermissionInfo[listPi.size()]);
	}

	@Override
	public boolean isUserInRole(Group rgoup) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Group parseGroup(String name, String memberLine, boolean create) throws WikiSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

}