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
package org.elwiki.authorize.internal.account.manager;

import java.security.Permission;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.event.WikiEventTopic;
import org.apache.wiki.api.event.WikiSecurityEventTopic;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.filters.ISpamFilter;
import org.apache.wiki.api.filters.PageFilter;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.AuthenticationManager;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.ui.InputValidator;
import org.apache.wiki.workflow0.Decision;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.apache.wiki.workflow0.Fact;
import org.apache.wiki.workflow0.IWorkflowBuilder;
import org.apache.wiki.workflow0.Step;
import org.apache.wiki.workflow0.Task;
import org.apache.wiki.workflow0.Workflow;
import org.apache.wiki.workflow0.WorkflowManager;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.IGroupManager;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.api.component.WikiManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki.data.authorize.WikiPrincipal;
import org.elwiki.permissions.WikiPermission;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.ComponentContext;
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
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import com.google.gson.Gson;

/**
 * Provides a facade for obtaining user information.
 * <p>
 * NOTE: The PageManager is attached as a listener.
 * <p>
 * One purpose:<br/>
 * Facade class for storing, retrieving and managing wiki groups on behalf of AuthorizationManager,
 * JSPs and other presentation-layer classes. GroupManager works in collaboration with a back-end
 * {@link GroupDatabase}, which persists groups to permanent storage.
 * </p>
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultAccountManager",
	service = { AccountManager.class, WikiManager.class, EventHandler.class},
	scope = ServiceScope.SINGLETON)
//@formatter:on
public final class DefaultAccountManager extends UserSupport implements AccountManager, WikiManager, EventHandler {

	// -- workflow task inner classes -----------------------------------------

	/**
	 * Inner class that handles the actual profile save action. Instances of this class are assumed to
	 * have been added to an approval workflow via
	 * {@link org.apache.wiki.workflow.workflow.WorkflowBuilder#buildApprovalWorkflow(Principal, String, Task, String, org.apache.wiki.workflow.Fact[], Task, String)};
	 * they will not function correctly otherwise.
	 *
	 */
	/*:FVK:
	public static class SaveUserProfileTask extends Task {
		private static final long serialVersionUID = 4297004510105358843L;

		private final AccountRegistry m_db;
		private final Engine m_engine;
		private final Locale m_loc;

		/ **
		 * Constructs a new Task for saving a user profile.
		 * 
		 * @param engine
		 *            the wiki engine
		 * @param locale
		 *            the Locale.
		 * /
		public SaveUserProfileTask(Engine engine, Locale loc) {
			super(SAVE_TASK_MESSAGE_KEY);
			this.m_engine = engine;
			this.m_db = engine.getApplicationSession().getAccountManager().get User Database();
			this.m_loc = loc;
		}

		/ **
		 * Saves the user profile to the user database.
		 * 
		 * @return {@link org.apache.wiki.workflow.Outcome#STEP_COMPLETE} if the task completed successfully
		 * @throws WikiException
		 *             if the save did not complete for some reason
		 * /
		public Outcome execute() throws WikiException {
			// Retrieve user profile
			UserProfile profile = (UserProfile) getWorkflow().getAttribute(SAVED_PROFILE);

			// Save the profile (userdatabase will take care of timestamps for us)
			this.m_db.saveProfile(profile);

			// Send e-mail if user supplied an e-mail address
			if (profile.getEmail().length() > 0) {
				/ * TODO: :FVK: * /
				try {
					String app = "fooApplication"; // :FVK: this.m_engine.getApplicationName();
					String to = profile.getEmail();
					String subject = Messages.fmt(Messages.get().notification_createUserProfile_accept_subject, app);
					//@formatter:off
					String content = Messages.fmt(Messages.get().notification_createUserProfile_accept_content, app, 
							profile.getLoginName(), 
                            profile.getFullname(),
                            profile.getEmail());
					//@formatter:on
					MailUtil.sendMessage(this.m_engine.getWikiPreferences(), to, subject, content);
				} catch (AddressException e) {
					// ignore.
				} catch (MessagingException me) {
					log.error("Could not send registration confirmation e-mail. Is the e-mail server running?", me);
				}
			}

			return Outcome.STEP_COMPLETE;
		}
	}*/

	private static final String SESSION_MESSAGES = "profile";
	/* Profile attributes names. */
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_FULLNAME = "fullname";
	private static final String PARAM_PASSWORD = "password";
	private static final String PARAM_LOGINNAME = "loginname";

	protected static final String NODE_ACCOUNTMANAGER = "node.accountManager";
	
	private ScopedPreferenceStore prefsAauth;
	
	/** Associates wiki sessions with profiles. */
	private final Map<Session, UserProfile> m_profiles = new WeakHashMap<>();

	// == CODE ================================================================

	/**
	 * Constructs a new AccountManager instance.
	 */
	public DefaultAccountManager() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	@Reference
	private UserAdmin userAdminService;

	@Reference
	private EventAdmin eventAdmin;
	
	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private Engine m_engine;

	@WikiServiceReference
	private ISessionMonitor sessionMonitor;
	
	@WikiServiceReference
	private AccountRegistry accountRegistry;

	@WikiServiceReference
	private AuthorizationManager authorizationManager;

	@WikiServiceReference
	private AuthenticationManager authenticationManager;

	@WikiServiceReference
	private TasksManager tasksManager;

	@WikiServiceReference
	private FilterManager filterManager;

	@WikiServiceReference
	private WorkflowManager workflowManager;

	/**
	 * This component activate routine. Initializes basic stuff.
	 *
	 * @param componentContext
	 * @throws WikiException
	 */
	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		BundleContext bc = componentContext.getBundleContext();
		this.prefsAauth = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				bc.getBundle().getSymbolicName() + "/" + NODE_ACCOUNTMANAGER);
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		//:FVK: this.m_engine = this.applicationSession.getWikiEngine();

		/*TODO: Replace with custom annotations. See JSPWIKI-566
		WikiAjaxDispatcherServlet.registerServlet(JSON_USERS, new JSONUserModule(this), new AllPermission(null));
		*/

		// Attach the PageManager as a listener
		// TODO: it would be better if we did this in PageManager directly
		// :FVK: addWikiEventListener( engine.getPageManager() );

		//TODO: Replace with custom annotations. See JSPWIKI-566
		// :FVK: WikiAjaxDispatcherServlet.registerServlet( JSON_USERS, new JSONUserModule(this), new AllPermission(null));
	}

	// -- OSGi service handling ------------------------(end)--

	static List<Group> getNativeGroups(UserAdmin userAdmin) throws InvalidSyntaxException {
		List<Group> groups = new ArrayList<>();

		for (Role role : userAdmin.getRoles(null)) {
			if (role instanceof Group group) {
				groups.add(group);
			}
		}

		return groups;
	}
	
	@Override
	protected IWikiConfiguration getWikiConfiguration() {
		return this.wikiConfiguration;
	}

	@Override
	protected AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	@Override
	protected AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	@Override
	protected TasksManager getTasksManager() {
		return tasksManager;
	}

	@Override
	protected FilterManager getFilterManager() {
		return filterManager;
	}

	@Override
	protected UserAdmin getUserAdmin() {
		return userAdminService;
	}
	
	// -- implementation AccountManager ------------------------------(start)--

	@Override
	public UserProfile getUserProfile(Session session) {
		// Look up cached user profile
		UserProfile profile = this.m_profiles.get(session);
		boolean newProfile = (profile == null);
		User user = session.getUser();

		// If user is authenticated, figure out if this is an existing profile.
		if (session.isAuthenticated()) {
			try {
				profile = accountRegistry.find(user);
				newProfile = false;
			} catch (NoSuchPrincipalException e) {
			}
		}

		if (newProfile) {
			profile = accountRegistry.newProfile(user);
			if (!profile.isNew()) {
				throw new IllegalStateException(
						"New profile should be marked 'new'. Check your UserProfile implementation.");
			}
		}

		// Stash the profile for next time
		this.m_profiles.put(session, profile);
		return profile;
	}

	@Override
	public void setUserProfile(Session session, UserProfile profile) throws DuplicateUserException, WikiException {
		// Verify user is allowed to save profile!
		Permission p = new WikiPermission(this.m_engine.getWikiConfiguration().getApplicationName(),
				WikiPermission.EDIT_PROFILE_ACTION);
		if (!getAuthorizationManager().checkPermission(session, p)) {
			throw new WikiSecurityException("You are not allowed to save wiki profiles.");
		}

        // Check if profile is new, and see if container allows creation
        final boolean newProfile = profile.isNew();

        // Check if another user profile already has the fullname or loginname
        final UserProfile oldProfile = getUserProfile( session );
        final boolean nameChanged = ( oldProfile != null && oldProfile.getFullname() != null ) &&
                                    !( oldProfile.getFullname().equals( profile.getFullname() ) &&
                                    oldProfile.getLoginName().equals( profile.getLoginName() ) );
        UserProfile otherProfile;
        try {
            otherProfile = accountRegistry.findByLoginName( profile.getLoginName() );
            if( otherProfile != null && !otherProfile.equals( oldProfile ) ) {
                throw new DuplicateUserException( "security.error.login.taken", profile.getLoginName() );
            }
        } catch( final NoSuchPrincipalException e ) {
        }
        try {
            otherProfile = accountRegistry.findByFullName( profile.getFullname() );
            if( otherProfile != null && !otherProfile.equals( oldProfile ) ) {
                throw new DuplicateUserException( "security.error.fullname.taken", profile.getFullname() );
            }
        } catch( final NoSuchPrincipalException e ) {
        }

        // For new accounts, create approval workflow for user profile save.
        if( newProfile && oldProfile != null && oldProfile.isNew() ) {
            startUserProfileCreationWorkflow( session, profile );

            // If the profile doesn't need approval, then just log the user in

            try {
                final AuthenticationManager mgr = getAuthenticationManager();
                if( !mgr.isContainerAuthenticated() ) {
                    mgr.loginAsserted( session, null, profile.getLoginName(), profile.getPassword() );
                }
            } catch( final WikiException e ) {
                throw new WikiSecurityException( e.getMessage(), e );
            }

            // Alert all listeners that the profile changed...
            // ...this will cause credentials to be reloaded in the wiki session
            String sesionId = sessionMonitor.getSessionId(session);
			eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_PROFILE_SAVE, Map.of( //
					WikiEventTopic.PROPERTY_KEY_TARGET, sesionId, //
					WikiSecurityEventTopic.PROPERTY_PROFILE, profile)));
        } else { // For existing accounts, just save the profile
            // If login name changed, rename it first
			if (nameChanged && oldProfile != null && !oldProfile.getLoginName().equals(profile.getLoginName())) {
				accountRegistry.rename(oldProfile.getLoginName(), profile.getLoginName());
			}

            // Now, save the profile (userdatabase will take care of timestamps for us)
            accountRegistry.save( profile );

            if( nameChanged ) {
                // Fire an event if the login name or full name changed
                final UserProfile[] profiles = new UserProfile[] { oldProfile, profile };
                String sesionId = sessionMonitor.getSessionId(session);
				eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_PROFILE_NAME_CHANGED, Map.of( //
						WikiEventTopic.PROPERTY_KEY_TARGET, sesionId, //
						WikiSecurityEventTopic.PROPERTY_PROFILES, profiles)));
            } else {
                // Fire an event that says we have new a new profile (new principals)
                String sesionId = sessionMonitor.getSessionId(session);
    			eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_PROFILE_SAVE, Map.of( //
    					WikiEventTopic.PROPERTY_KEY_TARGET, sesionId, //
    					WikiSecurityEventTopic.PROPERTY_PROFILE, profile)));
            }
        }
	}

    /** {@inheritDoc} */
	@Override
	public void startUserProfileCreationWorkflow(Session session, UserProfile profile) throws WikiException {
		final IWorkflowBuilder builder = workflowManager.getWorkflowBuilder();
        final Principal submitter = session.getUserPrincipal();
        final Step completionTask = getTasksManager().buildSaveUserProfileTask( m_engine, session.getLocale() );

        // Add user profile attribute as Facts for the approver (if required)
        final boolean hasEmail = profile.getEmail() != null;
        final Fact[] facts = new Fact[ hasEmail ? 4 : 3 ];
        facts[ 0 ] = new Fact( WorkflowManager.WF_UP_CREATE_SAVE_FACT_PREFS_FULL_NAME, profile.getFullname() );
        facts[ 1 ] = new Fact( WorkflowManager.WF_UP_CREATE_SAVE_FACT_PREFS_LOGIN_NAME, profile.getLoginName() );
        facts[ 2 ] = new Fact( WorkflowManager.WF_UP_CREATE_SAVE_FACT_SUBMITTER, submitter.getName() );
        if ( hasEmail ) {
            facts[ 3 ] = new Fact( WorkflowManager.WF_UP_CREATE_SAVE_FACT_PREFS_EMAIL, profile.getEmail() );
        }
        final Workflow workflow = builder.buildApprovalWorkflow( submitter,
                                                                 WorkflowManager.WF_UP_CREATE_SAVE_APPROVER,
                                                                 null,
                                                                 WorkflowManager.WF_UP_CREATE_SAVE_DECISION_MESSAGE_KEY,
                                                                 facts,
                                                                 completionTask,
                                                                 null );

        workflow.setAttribute( WorkflowManager.WF_UP_CREATE_SAVE_ATTR_SAVED_PROFILE, profile );
        workflow.start();

        final boolean approvalRequired = workflow.getCurrentStep() instanceof Decision;

        // If the profile requires approval, redirect user to message page
        if ( approvalRequired ) {
            throw new DecisionRequiredException( "This profile must be approved before it becomes active" );
        }
	}

	/**
	 * <p>
	 * Extracts user profile parameters from the HTTP request and populates a UserProfile with them. The
	 * UserProfile will either be a copy of the user's existing profile (if one can be found), or a new
	 * profile (if not). The rules for populating the profile as as follows:
	 * <ul>
	 * <li>If the <code>email</code> or <code>password</code> parameter values differ from those in the
	 * existing profile, the passed parameters override the old values.</li>
	 * <li>For new profiles, the user-supplied <code>fullname</code> parameter is always used; for
	 * existing profiles the existing value is used, and whatever value the user supplied is discarded.
	 * The wiki name is automatically computed by taking the full name and extracting all
	 * whitespace.</li>
	 * <li>In all cases, the created/last modified timestamps of the user's existing or new profile
	 * always override whatever values the user supplied.</li>
	 * <li>If container authentication is used, the login name property of the profile is set to the
	 * name of {@link org.apache.wiki.IElWikiSession#getLoginPrincipal()}. Otherwise, the value of the
	 * <code>loginname</code> parameter is used.</li>
	 * </ul>
	 *
	 * @param context
	 *            the current wiki context.
	 * @return a new, populated user profile.
	 */
	// :FVK: Не используется в Java. См. JSP файлы.
	@Override
	public UserProfile parseProfile(WikiContext context) {
		// Retrieve the user's profile (may have been previously cached)
		UserProfile profile = getUserProfile(context.getWikiSession());
		HttpServletRequest request = context.getHttpRequest();

        // Extract values from request stream (cleanse whitespace as needed)
        String loginName = request.getParameter( PARAM_LOGINNAME );
        String password = request.getParameter( PARAM_PASSWORD );
        String fullname = request.getParameter( PARAM_FULLNAME );
        String email = request.getParameter( PARAM_EMAIL );
        loginName = InputValidator.isBlank( loginName ) ? null : loginName;
        password = InputValidator.isBlank( password ) ? null : password;
        fullname = InputValidator.isBlank( fullname ) ? null : fullname;
        email = InputValidator.isBlank( email ) ? null : email;

        // A special case if we have container authentication: if authenticated, login name is always taken from container
        if ( getAuthenticationManager().isContainerAuthenticated() && context.getWikiSession().isAuthenticated() ) {
            loginName = context.getWikiSession().getLoginPrincipal().getName();
        }

        // Set the profile fields!
		if (loginName != null)
			profile.setLoginName(loginName);
		if (email != null)
			profile.setEmail(email);
		if (fullname != null)
			profile.setFullname(fullname);
		if (password != null)
			profile.setPassword(password);
        return profile;
	}

	/**
	 * Validates a user profile, and appends any errors to the session errors list. If the profile is
	 * new, the password will be checked to make sure it isn't null. Otherwise, the password is checked
	 * for length and that it matches the value of the 'password2' HTTP parameter. Note that we have a
	 * special case when container-managed authentication is used and the user is not authenticated;
	 * this will always cause validation to fail. Any validation errors are added to the wiki session's
	 * messages collection (see {@link IElWikiSession#getMessages()}.
	 * 
	 * @param context
	 *            the current wiki context.
	 * @param profile
	 *            the supplied UserProfile.
	 */
	//:FVK: - где используется?
	@Override
	public void validateProfile(WikiContext context, UserProfile profile) {
		 final boolean isNew = profile.isNew();
	        final Session session = context.getWikiSession();
	        final InputValidator validator = new InputValidator( SESSION_MESSAGES, context );
	        final ResourceBundle rb = Preferences.getBundle( context, InternationalizationManager.CORE_BUNDLE );

	        //  Query the SpamFilter first
			ISpamFilter spamFilter = getFilterManager().getSpamFilter();
			if (!spamFilter.isValidUserProfile(context, profile)) {
				session.addMessage(SESSION_MESSAGES, "Invalid userprofile");
				return;
			}

	        // If container-managed auth and user not logged in, throw an error
	        if ( getAuthenticationManager().isContainerAuthenticated()
	             && !context.getWikiSession().isAuthenticated() ) {
	            session.addMessage( SESSION_MESSAGES, rb.getString("security.error.createprofilebeforelogin") );
	        }

	        validator.validateNotNull( profile.getLoginName(), rb.getString("security.user.loginname") );
	        validator.validateNotNull( profile.getFullname(), rb.getString("security.user.fullname") );
	        validator.validate( profile.getEmail(), rb.getString("security.user.email"), InputValidator.EMAIL );

	        // If new profile, passwords must match and can't be null
	        if( !getAuthenticationManager().isContainerAuthenticated() ) {
	            final String password = profile.getPassword();
	            if( password == null ) {
	                if( isNew ) {
	                    session.addMessage( SESSION_MESSAGES, rb.getString( "security.error.blankpassword" ) );
	                }
	            } else {
	                final HttpServletRequest request = context.getHttpRequest();
	                final String password2 = ( request == null ) ? null : request.getParameter( "password2" );
	                if( !password.equals( password2 ) ) {
	                    session.addMessage( SESSION_MESSAGES, rb.getString( "security.error.passwordnomatch" ) );
	                }
	            }
	        }

	        UserProfile otherProfile;
	        final String fullName = profile.getFullname();
	        final String loginName = profile.getLoginName();
	        final String email = profile.getEmail();

	        // It's illegal to use as a full name someone else's login name
	        try {
	            otherProfile = accountRegistry.find( fullName );
	            if( otherProfile != null && !profile.equals( otherProfile ) && !fullName.equals( otherProfile.getFullname() ) ) {
	                final Object[] args = { fullName };
	                session.addMessage( SESSION_MESSAGES, MessageFormat.format( rb.getString( "security.error.illegalfullname" ), args ) );
	            }
	        } catch( final NoSuchPrincipalException e ) { /* It's clean */ }

	        // It's illegal to use as a login name someone else's full name
	        try {
	            otherProfile = accountRegistry.find( loginName );
	            if( otherProfile != null && !profile.equals( otherProfile ) && !loginName.equals( otherProfile.getLoginName() ) ) {
	                final Object[] args = { loginName };
	                session.addMessage( SESSION_MESSAGES, MessageFormat.format( rb.getString( "security.error.illegalloginname" ), args ) );
	            }
	        } catch( final NoSuchPrincipalException e ) { /* It's clean */ }

	        // It's illegal to use multiple accounts with the same email
	        try {
	            otherProfile = accountRegistry.findByEmail( email );
	            if( otherProfile != null && !profile.getUid().equals( otherProfile.getUid() ) // Issue JSPWIKI-1042
	                    && !profile.equals( otherProfile ) && StringUtils.lowerCase( email )
	                    .equals( StringUtils.lowerCase( otherProfile.getEmail() ) ) ) {
	                final Object[] args = { email };
	                session.addMessage( SESSION_MESSAGES, MessageFormat.format( rb.getString( "security.error.email.taken" ), args ) );
	            }
	        } catch( final NoSuchPrincipalException e ) { /* It's clean */ }
	}

	/**
	 * A helper method for returning all of the known WikiNames in this system.
	 * 
	 * @return An Array of Principals
	 * @throws WikiSecurityException
	 *             If for reason the names cannot be fetched
	 */
	@Override
	// :FVK: Не используется в Java.
	public Principal[] listWikiNames() throws WikiSecurityException {
		return accountRegistry.getWikiNames();
	}

	@Override
	public String getUserName(String uid) {
		if( getUserAdmin().getRole(uid) instanceof User user) {
			String name = (String)user.getProperties().get(AccountRegistry.LOGIN_NAME);
			return name;
		}

		return "";
	}

	// -- implementation AccountManager --------------------------------(end)--

	// -- implementation GroupManager --------------------------------(start)--
	
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

	@Override
	public Group getGroup(String groupName) {
		Group group = (Group) getUserAdmin().getRole(groupName);

		return group;
	}

	@Override
	public List<IGroupWiki> getGroups() throws WikiSecurityException {
		List<IGroupWiki> groups = new ArrayList<>();
		try {
			for (Group group : getNativeGroups(getUserAdmin())) {
				groups.add(new GroupWiki(group, this));
			}
		} catch (InvalidSyntaxException e) {
			throw new WikiSecurityException("Fail getting groups of UserAdmin service.", e);
		}

		return groups;
	}

	@Override
	public PermissionInfo[] getRolePermissionInfo(String roleName) {
		Role role = getUserAdmin().getRole(roleName);
		if (!(role instanceof Group group)) {
			throw new IllegalArgumentException("Required role \"" + roleName + "\" is not founded as group of UserAdmin service.");
		}
		String allPermissions = (String) group.getProperties().get(AccountRegistry.GROUP_PERMISSIONS);
		List<PermissionInfo> listPi = new ArrayList<>();
		String[] permissions = new Gson().fromJson(allPermissions, String[].class);
		for(String encodedPermission : permissions) {
			listPi.add(new PermissionInfo(encodedPermission));
		}
		return listPi.toArray(new PermissionInfo[listPi.size()]);
	}
	
	@Override
	public boolean isUserInRole(Group rgoup) {
		return false;
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
	 * {@link #setGroup(WikiSession, IGroupWiki)}.
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
	// :FVK: этот метод используется только для/(в) JSP файлах.
	@Override
	public IGroupWiki parseGroup(WikiContext context, boolean create) throws WikiSecurityException {
		// Extract parameters
		HttpServletRequest request = context.getHttpRequest();
		String name = request.getParameter("group");
		String memberLine = request.getParameter("members");

		// Create the named group; we pass on any NoSuchPrincipalExceptions
		// that may be thrown if create == false, or WikiSecurityExceptions
		IGroupWiki groupWiki = null;
		/*:FVK:
		IGroupWiki group = parseGroup(null, name, memberLine, create);
		*/
		Object role = this.getUserAdmin().getUser(AccountRegistry.GROUP_NAME, name);
		if (role instanceof Group group) {
			groupWiki = new GroupWiki(group, this);
		} else {
			// TODO: обработать отсутствие группы.
		}
		/*:FVK: // If no members, add the current user by default
		if (group.members().length == 0) {
			group.add(context.getWikiSession().getUserPrincipal());
		}*/

		return groupWiki;
	}

	@Override
	public Group parseGroup(String name, String memberLine, boolean create) throws WikiSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//:FVK: этот метод используется в коде DeleteGroup.jsp.
	public void removeGroup(IGroupWiki index) throws WikiSecurityException {
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
	public void setGroup(Session session, IGroupWiki group) throws WikiSecurityException {
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
			group.setLastModified(oldGroup.getLastModifiedDate());
		}*/

		// Add new group to cache; announce GROUP_ADD event
		/*synchronized (this.m_groups) {
			this.m_groups.put(group.getPrincipal(), group);
		}*/
		
//		fireEvent(WikiSecurityEvent.GROUP_ADD, group);

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
	//? :FVK: этот метод используется -- в коде EditGroupCmdCode (из JSP файла).
	//? :FVK: этот метод нигде используется -- в JSP файле.
	@Override
	public void validateGroup(WikiContext context, IGroupWiki group) {
		InputValidator validator = new InputValidator(IGroupManager.MESSAGES_KEY, context);

		// Name cannot be null or one of the restricted names
		try {
			checkGroupName(context, group.getName());
		} catch (WikiSecurityException e) {
			//TODO: ...
		}

		// Member names must be "safe" strings
		//:FVK: заменил Principal на String.
		String[] members = group.getMemberNames();
		for (String member : members) {
			validator.validateNotNull(member, "Full name", InputValidator.ID);
		}
	}

	@Override
	public String getGroupUid(String name) {
		String uid = null;
		Object role = userAdminService.getUser(AccountRegistry.GROUP_NAME, name);
		if (role instanceof Group group) {
			uid = group.getName();
		}
		return uid;
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
	protected void checkGroupName(WikiContext context, String name) throws WikiSecurityException {
		//TODO: groups cannot have the same name as a user

		// Name cannot be null
		InputValidator validator = new InputValidator(IGroupManager.MESSAGES_KEY, context);
		validator.validateNotNull(name, "Group name");

		// Name cannot be one of the restricted names either
		//:FVK:		if (ArrayUtils.contains(Group.RESTRICTED_GROUPNAMES, name)) {
		//			throw new WikiSecurityException("The group name '" + name + "' is illegal. Choose another.");
		//		}
	}

	// -- implementation GroupManager ----------------------------------(end)--

	/**
	 * Listens for {@link WikiSecurityEventTopic#TOPIC_SECUR_PROFILE_NAME_CHANGED}
	 * events. If a user profile's name changes, each group is inspected. If an entry contains a
	 * name that has changed, it is replaced with the new one. No group events are emitted as a
	 * consequence of this method, because the group memberships are still the same; it is only the
	 * representations of the names within that are changing.
	 * 
	 * @param event
	 *              the incoming event
	 */
	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		switch (topic) {
		case WikiSecurityEventTopic.TOPIC_SECUR_PROFILE_NAME_CHANGED: {
			UserProfile[] profiles = (UserProfile[])event.getProperty(WikiSecurityEventTopic.PROPERTY_PROFILES);
			Principal[] oldPrincipals = new Principal[] { //
					new WikiPrincipal(profiles[0].getLoginName()), //
					new WikiPrincipal(profiles[0].getFullname()), //
					new WikiPrincipal(profiles[0].getWikiName()) };
			final Principal newPrincipal = new WikiPrincipal(profiles[1].getFullname());

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
			break;
		}
	}

}
