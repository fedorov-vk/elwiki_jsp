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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.security.Principal;
import java.text.MessageFormat;
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
import org.apache.wiki.api.core.Context;
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
import org.apache.wiki.auth.UserManager;
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
//import com.opcoach.e4.preferences.ScopedPreferenceStore;
//import org.elwiki.api.FilterManager;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.IAuthenticationManager;
//import org.elwiki.api.IElWikiSession;
//import org.elwiki.api.IUserManager;
//import org.elwiki.api.IWikiContext;
//import org.elwiki.api.IWikiEngine;
//import org.elwiki.api.authorization.user.IUserDatabase;
//import org.elwiki.api.authorization.user.UserProfile;
//import org.elwiki.api.event.WikiEventListener;
//import org.elwiki.api.event.WikiEventManager;
//import org.elwiki.api.event.WikiSecurityEvent;
//import org.elwiki.api.exceptions.DuplicateUserException;
//import org.elwiki.api.exceptions.NoSuchPrincipalException;
//import org.elwiki.api.exceptions.WikiException;
//import org.elwiki.api.exceptions.WikiSecurityException;
//import org.elwiki.api.filters.ISpamFilter;
//import org.elwiki.api.filters.PageFilter;
//import org.elwiki.api.ui.InputValidator;
//import org.elwiki.api.workflow.Decision;
//import org.elwiki.api.workflow.DecisionRequiredException;
//import org.elwiki.api.workflow.Fact;
//import org.elwiki.api.workflow.Outcome;
//import org.elwiki.api.workflow.Task;
//import org.elwiki.api.workflow.Workflow;
//import org.elwiki.api.workflow.WorkflowBuilder;
import org.elwiki.authorize.Messages;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.authorize.user.DummyUserDatabase;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki.permissions.WikiPermission;
//import org.elwiki.utils.MailUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.User;

/**
 * Provides a facade for obtaining user information.
 * <p>
 * NOTE: The PageManager is attached as a listener.
 */
@Component(name = "elwiki.DefaultUserManager", service = UserManager.class, //
		factory = "elwiki.UserManager.factory")
public class DefaultUserManager implements UserManager {

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
			this.m_db = engine.getApplicationSession().getUserManager().getUserDatabase();
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

	private static final Logger log = Logger.getLogger(DefaultUserManager.class);

	/** The extension ID for access to the implementation set of {@link IUserDatabase}. */
	private static final String ID_EXTENSION_USER_DATABASE = "userProfileDatabase";

	/** Extension's specific ID of default user database implementation. Current value - {@value} */
	private static final String DEFAULT_DATABASE_ID = "UserAdminDatabase";

	private static final String SESSION_MESSAGES = "profile";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_FULLNAME = "fullname";
	private static final String PARAM_PASSWORD = "password";
	private static final String PARAM_LOGINNAME = "loginname";

	private Engine m_engine;

	/** Message key for the "save profile" message. */
	public static final String SAVE_APPROVER = "workflow.createUserProfile";

	/** Property-идентификатор пользовательской БД. */
	protected static final String PROP_DATABASE = "userdatabase";
	protected static final String NODE_USERMANAGER = "node.userManager";

	protected static final String SAVE_TASK_MESSAGE_KEY = "task.createUserProfile";
	protected static final String SAVED_PROFILE = "userProfile";
	protected static final String SAVE_DECISION_MESSAGE_KEY = "decision.createUserProfile";
	protected static final String FACT_SUBMITTER = "fact.submitter";
	protected static final String PREFS_LOGIN_NAME = "prefs.loginname";
	protected static final String PREFS_FULL_NAME = "prefs.fullname";
	protected static final String PREFS_EMAIL = "prefs.email";

	public static final String JSON_USERS = "users";

	// :FVK: - in future? -- private static final String  PROP_ACLMANAGER = "jspwiki.aclManager";

	/** Associates wiki sessions with profiles */
	private final Map<Session, UserProfile> m_profiles = new WeakHashMap<>();

	/** The user database loads, manages and persists user identities */
	private UserDatabase m_database;

	//:FVK: private IApplicationSession applicationSession;

	/** Presents the available implementations of UserDatabase. */
	private Map<String, Class<? extends UserDatabase>> userDataBases;

	private ScopedPreferenceStore prefsAauth;

	private Class<? extends UserDatabase> classUserDatabase;

	// == CODE ================================================================

	/**
	 * Constructs a new UserManager instance.
	 */
	public DefaultUserManager() {
		super();
	}

	// -- service handling ---------------------------(start)--

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
	 */
	@Activate
	protected void startup(ComponentContext componentContext) {
		BundleContext bc = componentContext.getBundleContext();
		this.prefsAauth = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				bc.getBundle().getSymbolicName() + "/" + NODE_USERMANAGER);

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

	public void initialize(final Engine engine) throws WikiException {
		this.m_engine = engine;

		//:FVK: this.m_engine = this.applicationSession.getWikiEngine();

		/*TODO: Replace with custom annotations. See JSPWIKI-566
		WikiAjaxDispatcherServlet.registerServlet(JSON_USERS, new JSONUserModule(this), new AllPermission(null));
		*/

		this.userDataBases = getUserDatabaseImplementations();
		String dbId = this.prefsAauth.getString(PROP_DATABASE);
		if (dbId.length() == 0) {
			// get default UserDatabase.
			dbId = DEFAULT_DATABASE_ID; // WORKARUND. -- такого идентификатора может не быть.
		}
		this.classUserDatabase = this.userDataBases.get(dbId);

		Assert.isNotNull(this.classUserDatabase, "Undefined UserDatabase with ID = " + dbId);

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

	protected IWikiConfiguration getWikiConfiguration() {
		return this.wikiConfiguration;
	}

	AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	IIAuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	TasksManager getTasksManager() {
		return tasksManager;
	}

	FilterManager getFilterManager() {
		return filterManager;
	}
	
	private Map<String, Class<? extends UserDatabase>> getUserDatabaseImplementations() throws WikiException {
		String namespace = AuthorizePluginActivator.getDefault().getBundle().getSymbolicName();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep;
		Map<String, Class<? extends UserDatabase>> userDatabaseClasses = new HashMap<>();

		//
		// Load the UserDatabase definitions from Equinox extensions.
		//
		ep = registry.getExtensionPoint(namespace, ID_EXTENSION_USER_DATABASE);
		if (ep != null) {
			for (IConfigurationElement el : ep.getConfigurationElements()) {
				String contributorName = el.getContributor().getName();
				String className = el.getAttribute("class");
				String userDatabaseId = el.getAttribute("id");
				try {
					final Bundle bundle = Platform.getBundle(contributorName);
					Class<?> clazz = bundle.loadClass(className);
					try {
						Class<? extends UserDatabase> cl = clazz.asSubclass(UserDatabase.class);
						userDatabaseClasses.put(userDatabaseId, (Class<? extends UserDatabase>) cl);
					} catch (ClassCastException e) {
						log.fatal("UserDatabase " + className + " is not extends IUserDatabase interface.", e);
						throw new WikiException(
								"UserDatabase " + className + " is not extends IUserDatabase interface.", e);
					}
				} catch (ClassNotFoundException e) {
					log.fatal("UserDatabase " + className + " cannot be found.", e);
					throw new WikiException("UserDatabase " + className + " cannot be found.", e);
				}
			}
		}

		return userDatabaseClasses;
	}
	
	@Override
	public UserDatabase getUserDatabase() {
		// FIXME: Must not throw RuntimeException, but something else.
		if (this.m_database != null) {
			return this.m_database;
		}

		try {
			this.m_database = this.classUserDatabase.getDeclaredConstructor().newInstance();
			log.info("Attempting to load user database class " + this.m_database.getClass().getName());
			this.m_database.initialize(this.m_engine, null);
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
	public void setUserProfile(Session session, UserProfile profile)
			throws DuplicateUserException, WikiException {
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
    public void startUserProfileCreationWorkflow( final Session session, final UserProfile profile ) throws WikiException {
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
	public UserProfile parseProfile(Context context) {
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
	public void validateProfile(Context context, UserProfile profile) {
		 final boolean isNew = profile.isNew();
        final Session session = context.getWikiSession();
        final InputValidator validator = new InputValidator( SESSION_MESSAGES, context );
        final ResourceBundle rb = Preferences.getBundle( context, InternationalizationManager.CORE_BUNDLE );

        //  Query the SpamFilter first
        final FilterManager fm = getFilterManager();
        final List< PageFilter > ls = fm.getFilterList();
        for( final PageFilter pf : ls ) {
            if( pf instanceof SpamFilter ) {
                if( !( ( SpamFilter )pf ).isValidUserProfile( context, profile ) ) {
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
	// :FVK: Не используется в Java.
	public Principal[] listWikiNames() throws WikiSecurityException {
		return getUserDatabase().getWikiNames();
	}

	// -- events processing ---------------------------------------------------

	/**
	 * Registers a WikiEventListener with this instance. This is a convenience method.
	 * 
	 * @param listener
	 *            the event listener
	 */
	@Override
	public synchronized void addWikiEventListener(WikiEventListener listener) {
		WikiEventManager.addWikiEventListener(this, listener);
	}

	/**
	 * Un-registers a WikiEventListener with this instance. This is a convenience method.
	 * 
	 * @param listener
	 *            the event listener
	 */
	@Override
	public synchronized void removeWikiEventListener(WikiEventListener listener) {
		WikiEventManager.removeWikiEventListener(this, listener);
	}

	/**
	 * Fires a WikiSecurityEvent of the provided type, Principal and target Object to all registered
	 * listeners.
	 *
	 * @see org.apache.wiki.event.WikiSecurityEvent
	 * @param type
	 *            the event type to be fired
	 * @param session
	 *            the wiki session supporting the event
	 * @param profile
	 *            the user profile (or array of user profiles), which may be <code>null</code>
	 */
	/*:FVK: - реализация в интерфейсе.
	protected void fireEvent(int type, IElWikiSession session, Object profile) {
		if (WikiEventManager.isListening(this)) {
			WikiEventManager.fireEvent(this, new WikiSecurityEvent(session, type, profile));
		}
	}
	*/

    /**
     *  Implements the JSON API for usermanager.
     *  <p>
     *  Even though this gets serialized whenever container shuts down/restarts, this gets reinstalled to the session when JSPWiki starts.
     *  This means that it's not actually necessary to save anything.
     */
    public static final class JSONUserModule implements WikiAjaxServlet {

		private volatile DefaultUserManager m_manager;

        /**
         *  Create a new JSONUserModule.
         *  @param mgr Manager
         */
        public JSONUserModule( final DefaultUserManager mgr )
        {
            m_manager = mgr;
        }

        @Override
        public String getServletMapping() {
        	return JSON_USERS;
        }

        @Override
        public void service( final HttpServletRequest req, final HttpServletResponse resp, final String actionName, final List<String> params) throws ServletException, IOException {
        	try {
            	if( params.size() < 1 ) {
            		return;
            	}
        		final String uid = params.get(0);
	        	log.debug("uid="+uid);
	        	if (StringUtils.isNotBlank(uid)) {
		            final UserProfile prof = getUserInfo(uid);
		            resp.getWriter().write(AjaxUtil.toJson(prof));
	        	}
        	} catch (final NoSuchPrincipalException e) {
        		throw new ServletException(e);
        	}
        }

        /**
         *  Directly returns the UserProfile object attached to an uid.
         *
         *  @param uid The user id (e.g. WikiName)
         *  @return A UserProfile object
         *  @throws NoSuchPrincipalException If such a name does not exist.
         */
        public UserProfile getUserInfo( final String uid ) throws NoSuchPrincipalException {
            if( m_manager != null ) {
                return m_manager.getUserDatabase().find( uid );
            }

            throw new IllegalStateException( "The manager is offline." );
        }
    }

}
