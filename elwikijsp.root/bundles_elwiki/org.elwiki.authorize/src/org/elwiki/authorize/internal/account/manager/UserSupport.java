package org.elwiki.authorize.internal.account.manager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.wiki.ajax.AjaxUtil;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiSecurityEvent;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.filters.PageFilter;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.ui.InputValidator;
import org.apache.wiki.workflow0.Decision;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.apache.wiki.workflow0.Fact;
import org.apache.wiki.workflow0.Step;
import org.apache.wiki.workflow0.Workflow;
import org.apache.wiki.workflow0.WorkflowManager;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki.permissions.WikiPermission;
import org.osgi.framework.Bundle;
import org.osgi.service.useradmin.User;

public abstract class UserSupport extends BasicAccountManager {

	/** Message key for the "save profile" message. */
	public static final String SAVE_APPROVER = "workflow.createUserProfile";

	protected static final String SAVE_TASK_MESSAGE_KEY = "task.createUserProfile";
	protected static final String SAVED_PROFILE = "userProfile";
	protected static final String SAVE_DECISION_MESSAGE_KEY = "wf.decision.createUserProfile";
	protected static final String FACT_SUBMITTER = "wf.fact.submitter";
	protected static final String PREFS_LOGIN_NAME = "prefs.loginname";
	protected static final String PREFS_FULL_NAME = "prefs.fullname";
	protected static final String PREFS_EMAIL = "prefs.email";

	public static final String JSON_USERS = "users";

	// :FVK: - in future? -- private static final String  PROP_ACLMANAGER = "jspwiki.aclManager";

	//:FVK: private IApplicationSession applicationSession;

	/**
     *  Implements the JSON API for AccountManager.
     *  <p>
     *  Even though this gets serialized whenever container shuts down/restarts, this gets reinstalled to the session when JSPWiki starts.
     *  This means that it's not actually necessary to save anything.
     */
	//:FVK: - зачем это? @Deprecated?
    public static final class JSONUserModule implements WikiAjaxServlet {

		private volatile DefaultAccountManager m_manager;

        /**
         *  Create a new JSONUserModule.
         *  @param mgr Manager
         */
        public JSONUserModule( final DefaultAccountManager mgr )
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
        	Assert.isTrue(false, "Code is not implemented.");
        	return null;
			/*:FVK: - commented code from JSPwiki.
			AccountRegistry accountRegistry = Engine.Instance.getManager(AccountRegistry.class);
			if( accountRegistry != null ) {
			    return accountRegistry.find( uid );
			}
			
			throw new IllegalStateException( "The manager is offline." );
			*/
        }
    }


}
