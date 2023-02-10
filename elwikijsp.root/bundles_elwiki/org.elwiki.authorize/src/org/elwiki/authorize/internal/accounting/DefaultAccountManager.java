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
package org.elwiki.authorize.internal.accounting;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.ajax.AjaxUtil;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiSecurityEvent;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.filters.PageFilter;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user0.UserDatabase;
import org.apache.wiki.auth.user0.UserProfile;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.filters0.SpamFilter;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.ui.InputValidator;
import org.apache.wiki.util.MailUtil;
import org.apache.wiki.workflow0.Decision;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.apache.wiki.workflow0.Fact;
import org.apache.wiki.workflow0.Outcome;
import org.apache.wiki.workflow0.Step;
import org.apache.wiki.workflow0.Task;
import org.apache.wiki.workflow0.Workflow;
import org.apache.wiki.workflow0.WorkflowBuilder;
import org.apache.wiki.workflow0.WorkflowManager;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.GroupDatabase;
import org.elwiki.api.authorization.IGroupManager;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.authorize.Messages;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.authorize.user.DummyUserDatabase;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki.permissions.WikiPermission;
//import org.elwiki.utils.MailUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
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
@Component(name = "elwiki.DefaultAccountManager", service = AccountManager.class, //
		factory = "elwiki.AccountManager.factory")
public final class DefaultAccountManager extends GroupSupport implements AccountManager {

	// -- workflow task inner classes -----------------------------------------

	/**
	 * Inner class that handles the actual profile save action. Instances of this class are assumed to
	 * have been added to an approval workflow via
	 * {@link org.apache.wiki.workflow.WorkflowBuilder#buildApprovalWorkflow(Principal, String, Task, String, org.apache.wiki.workflow.Fact[], Task, String)};
	 * they will not function correctly otherwise.
	 *
	 */
	/*:FVK:
	public static class SaveUserProfileTask extends Task {
		private static final long serialVersionUID = 4297004510105358843L;

		private final UserDatabase m_db;
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
			this.m_db = engine.getApplicationSession().getAccountManager().getUserDatabase();
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

	
	/** Property-идентификатор пользовательской БД. */
	protected static final String PROP_DATABASE = "userdatabase";
	
	/** Extension's specific ID of default user database implementation. Current value - {@value} */
	private static final String DEFAULT_DATABASE_ID = "UserAdminDatabase";
	protected static final String NODE_ACCOUNTMANAGER = "node.accountManager";
	
	private ScopedPreferenceStore prefsAauth;
	
	private Engine m_engine;
	
	/** Presents the available implementations of UserDatabase. */
	private Map<String, Class<? extends UserDatabase>> userDataBases;

	private Class<? extends UserDatabase> classUserDatabase;
	
	/** The user database loads, manages and persists user identities */
	private UserDatabase m_database;
	
	/** Associates wiki sessions with profiles. */
	private final Map<Session, UserProfile> m_profiles = new WeakHashMap<>();

	private final List<IGroupWiki> m_groups = new ArrayList<>();

	// == CODE ================================================================

	/**
	 * Constructs a new AccountManager instance.
	 */
	public DefaultAccountManager() {
		super();
	}

	// -- service handling ---------------------------(start)--

	@Reference
	private UserAdmin userAdminService;

	/** Stores configuration. */
	@Reference //(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private AuthorizationManager authorizationManager;

	@WikiServiceReference
	private IIAuthenticationManager authenticationManager;

	@WikiServiceReference
	private TasksManager tasksManager;

	@WikiServiceReference
	private FilterManager filterManager;

	/**
	 * This component activate routine. Does all the real initialization.
	 *
	 * @param componentContext
	 * @throws WikiException
	 */
	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		BundleContext bc = componentContext.getBundleContext();
		this.prefsAauth = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				bc.getBundle().getSymbolicName() + "/" + NODE_ACCOUNTMANAGER);

		Object obj = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
		if (obj instanceof Engine engine) {
			initialize(engine);
		}
	}

	public void initialize(final Engine engine) throws WikiException {
		this.m_engine = engine;

		//:FVK: this.m_engine = this.applicationSession.getWikiEngine();

		/*TODO: Replace with custom annotations. See JSPWIKI-566
		WikiAjaxDispatcherServlet.registerServlet(JSON_USERS, new JSONUserModule(this), new AllPermission(null));
		*/

		this.userDataBases = ExtensionHandler.getUserDatabaseImplementations();
		String dbId = this.prefsAauth.getString(PROP_DATABASE);
		if (dbId.length() == 0) {
			// get default UserDatabase.
			dbId = DEFAULT_DATABASE_ID; // WORKARUND. -- такого идентификатора может не быть.
		}
		this.classUserDatabase = this.userDataBases.get(dbId);

		Assert.isNotNull(this.classUserDatabase, "Undefined UserDatabase with ID = " + dbId);
		
		/* Attaching users database.
		 */
		try {
			this.m_database = this.classUserDatabase.getDeclaredConstructor().newInstance();
			log.info("Attempting to load user database class " + this.m_database.getClass().getName());
			this.m_database.initialize(this.m_engine, null); // :FVK: workaround - here load Groups, Users from JSON.
			log.info("UserDatabase initialized.");
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
			log.error("UserDatabase class " + this.classUserDatabase.getClass().getName() + " cannot be created.", e);
			// throw new WikiException("UserDatabase class " + this.classUserDatabase + " cannot be created.", e);
		} catch (IllegalAccessException e) {
			log.error("You are not allowed to user database class " + this.classUserDatabase.getClass().getName(), e);
			// throw new WikiException("You are not allowed to user database class " + this.classUserDatabase, e);
		} catch (WikiSecurityException e) {
			log.error("Exception initializing user database: " + e.getMessage());
		} catch (NoRequiredPropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (this.m_database == null) {
				log.info(
						"I could not create a database object you specified (or didn't specify), so I am falling back to a default.");
				this.m_database = new DummyUserDatabase();
			}
		}

        /* Put all groups from the persistent store into the cache.
         * :FVK: with UserAdmin service - groups are loaded when OSGi services started? 
         */
		for (final Group group : getNativeGroups()) {
			// Add new group; fire GROUP_ADD event
			m_groups.add(new GroupWiki(group, this));
			//:FVK: fireEvent(WikiSecurityEvent.GROUP_ADD, group);
		}

		// Attach the PageManager as a listener
		// TODO: it would be better if we did this in PageManager directly
		// :FVK: addWikiEventListener( engine.getPageManager() );

		//TODO: Replace with custom annotations. See JSPWIKI-566
		// :FVK: WikiAjaxDispatcherServlet.registerServlet( JSON_USERS, new JSONUserModule(this), new AllPermission(null));
	}
	
	@Deactivate
	protected void shutdown() {
		//
	}
	
	// -- service handling -----------------------------(end)--

	@Override
	protected IWikiConfiguration getWikiConfiguration() {
		return this.wikiConfiguration;
	}

	@Override
	protected AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	@Override
	protected IIAuthenticationManager getAuthenticationManager() {
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
	public UserDatabase getUserDatabase() {
		return this.m_database;
	}

	@Override
	public UserProfile getUserProfile(Session session) {
		// Look up cached user profile
		UserProfile profile = this.m_profiles.get(session);
		boolean newProfile = (profile == null);
		User user = session.getUser();

		// If user is authenticated, figure out if this is an existing profile.
		if (session.isAuthenticated()) {
			try {
				profile = getUserDatabase().find(user);
				newProfile = false;
			} catch (NoSuchPrincipalException e) {
			}
		}

		if (newProfile) {
			profile = getUserDatabase().newProfile(user);
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
            otherProfile = getUserDatabase().findByLoginName( profile.getLoginName() );
            if( otherProfile != null && !otherProfile.equals( oldProfile ) ) {
                throw new DuplicateUserException( "security.error.login.taken", profile.getLoginName() );
            }
        } catch( final NoSuchPrincipalException e ) {
        }
        try {
            otherProfile = getUserDatabase().findByFullName( profile.getFullname() );
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
                final IIAuthenticationManager mgr = getAuthenticationManager();
                if( !mgr.isContainerAuthenticated() ) {
                    mgr.loginAsserted( session, null, profile.getLoginName(), profile.getPassword() );
                }
            } catch( final WikiException e ) {
                throw new WikiSecurityException( e.getMessage(), e );
            }

            // Alert all listeners that the profile changed...
            // ...this will cause credentials to be reloaded in the wiki session
            fireEvent( WikiSecurityEvent.PROFILE_SAVE, session, profile );
        } else { // For existing accounts, just save the profile
            // If login name changed, rename it first
			if (nameChanged && oldProfile != null && !oldProfile.getLoginName().equals(profile.getLoginName())) {
				getUserDatabase().rename(oldProfile.getLoginName(), profile.getLoginName());
			}

            // Now, save the profile (userdatabase will take care of timestamps for us)
            getUserDatabase().save( profile );

            if( nameChanged ) {
                // Fire an event if the login name or full name changed
                final UserProfile[] profiles = new UserProfile[] { oldProfile, profile };
                fireEvent( WikiSecurityEvent.PROFILE_NAME_CHANGED, session, profiles );
            } else {
                // Fire an event that says we have new a new profile (new principals)
                fireEvent( WikiSecurityEvent.PROFILE_SAVE, session, profile );
            }
        }
	}

    /** {@inheritDoc} */
	@Override
	public void startUserProfileCreationWorkflow(Session session, UserProfile profile) throws WikiException {
        final WorkflowBuilder builder = WorkflowBuilder.getBuilder( m_engine );
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
	        final FilterManager fm = getFilterManager();
	        final List< PageFilter > ls = fm.getFilterList();
	        for( final PageFilter pf : ls ) {
	            if( pf instanceof SpamFilter spamFilter) {
	                if( !spamFilter.isValidUserProfile( context, profile ) ) {
	                    session.addMessage( SESSION_MESSAGES, "Invalid userprofile" );
	                    return;
	                }
	                break;
	            }
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
	            otherProfile = getUserDatabase().find( fullName );
	            if( otherProfile != null && !profile.equals( otherProfile ) && !fullName.equals( otherProfile.getFullname() ) ) {
	                final Object[] args = { fullName };
	                session.addMessage( SESSION_MESSAGES, MessageFormat.format( rb.getString( "security.error.illegalfullname" ), args ) );
	            }
	        } catch( final NoSuchPrincipalException e ) { /* It's clean */ }

	        // It's illegal to use as a login name someone else's full name
	        try {
	            otherProfile = getUserDatabase().find( loginName );
	            if( otherProfile != null && !profile.equals( otherProfile ) && !loginName.equals( otherProfile.getLoginName() ) ) {
	                final Object[] args = { loginName };
	                session.addMessage( SESSION_MESSAGES, MessageFormat.format( rb.getString( "security.error.illegalloginname" ), args ) );
	            }
	        } catch( final NoSuchPrincipalException e ) { /* It's clean */ }

	        // It's illegal to use multiple accounts with the same email
	        try {
	            otherProfile = getUserDatabase().findByEmail( email );
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
		return getUserDatabase().getWikiNames();
	}

	@Override
	public String getUserName(String uid) {
		if( getUserAdmin().getRole(uid) instanceof User user) {
			String name = (String)user.getProperties().get(UserDatabase.LOGIN_NAME);
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
	public List<IGroupWiki> getGroups() {
		return this.m_groups;
	}

	protected List<Group> getNativeGroups() {
		List<Group> groups = new ArrayList<>();
		try {
			for (Role role : getUserAdmin().getRoles(null)) {
				if (role instanceof Group group) {
					groups.add(group);
				}
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groups; // :FVK: this.m_groups.keySet().toArray(new Principal[this.m_groups.size()]);
	}

	@Override
	public PermissionInfo[] getRolePermissionInfo(String roleName) {
		Role role = getUserAdmin().getRole(roleName);
		if (!(role instanceof Group group)) {
			throw new IllegalArgumentException("Required role \"" + roleName + "\" is not founded as group of UserAdmin service.");
		}
		String allPermissions = (String) group.getProperties().get(UserDatabase.GROUP_PERMISSIONS);
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
		Object role = this.getUserAdmin().getUser(UserDatabase.GROUP_NAME, name);
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
		Object role = userAdminService.getUser(UserDatabase.GROUP_NAME, name);
		if (role instanceof Group group) {
			uid = group.getName();
		}
		return uid;
	}

	// -- implementation GroupManager ----------------------------------(end)--
	
}
