package org.apache.wiki.tasks.auth;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.util0.MailUtil;
import org.apache.wiki.workflow0.Outcome;
import org.apache.wiki.workflow0.Task;
import org.apache.wiki.workflow0.WorkflowManager;
import org.elwiki.api.GlobalPreferences;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.util.Locale;


/**
 * Handles the actual profile save action. 
 */
public class SaveUserProfileTask extends Task {

    private static final long serialVersionUID = 6994297086560480285L;
    private static final Logger LOG = Logger.getLogger( SaveUserProfileTask.class );
    private final Engine m_engine;
    private final Locale m_loc;

    /**
     * Constructs a new Task for saving a user profile.
     *
     * @param engine the wiki engine
     */
    public SaveUserProfileTask( final Engine engine, final Locale loc ) {
        super( TasksManager.USER_PROFILE_SAVE_TASK_MESSAGE_KEY );
        m_engine = engine;
        m_loc = loc;
    }

    /**
     * Saves the user profile to the user database.
     *
     * @return {@link org.apache.wiki.workflow0.Outcome#STEP_COMPLETE} if the task completed successfully
     * @throws WikiException if the save did not complete for some reason
     */
    @Override
    public Outcome execute() throws WikiException {
        // Retrieve user profile
        final UserProfile profile = ( UserProfile )getWorkflowContext().get( WorkflowManager.WF_UP_CREATE_SAVE_ATTR_SAVED_PROFILE );
        if ( profile != null) {
        	// Save the profile (userdatabase will take care of timestamps for us)
        	m_engine.getManager(AccountRegistry.class).save( profile );
        	
            // Send e-mail if user supplied an e-mail address
        	final String to = profile.getEmail();
            if ( to != null && to.length() > 0) { // :FVK: TODO: реализовать проверку правильности email address. возможно не здесь.
                try {
                    final InternationalizationManager i18n = m_engine.getManager(InternationalizationManager.class);
                    final String app = m_engine.getManager(GlobalPreferences.class).getApplicationName();
                    final String subject = i18n.get(m_loc, "wf.notification.createUserProfile.accept.subject", app );

                    final String content = i18n.get(m_loc,
                                                     "wf.notification.createUserProfile.accept.content", app,
                                                     profile.getLoginName(),
                                                     profile.getFullname(),
                                                     profile.getEmail(),
                                                     m_engine.getURL( ContextEnum.WIKI_LOGIN.getRequestContext(), null, null ) );
					MailUtil.sendMessage(m_engine.getWikiConfiguration(), to, subject, content);
                } catch ( final AddressException e) {
                    LOG.debug( e.getMessage(), e );
                } catch ( final MessagingException me ) {
                    LOG.error( "Could not send registration confirmation e-mail. Is the e-mail server running?", me );
                }
            }
        }

        return Outcome.STEP_COMPLETE;
    }

}
