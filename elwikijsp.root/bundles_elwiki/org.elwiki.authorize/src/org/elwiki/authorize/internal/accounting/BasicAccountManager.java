package org.elwiki.authorize.internal.accounting;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiSecurityEvent;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.apache.wiki.filters0.FilterManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.osgi.service.useradmin.UserAdmin;

public abstract class BasicAccountManager {

	protected static final Logger log = Logger.getLogger(BasicAccountManager.class);

	public BasicAccountManager() {
		super();
	}

	protected abstract IWikiConfiguration getWikiConfiguration();

	protected abstract AuthorizationManager getAuthorizationManager();

	protected abstract IIAuthenticationManager getAuthenticationManager();

	protected abstract TasksManager getTasksManager();

	protected abstract FilterManager getFilterManager();

	protected abstract UserAdmin getUserAdmin();
	
	// -- events processing ------------------------------------------(start)--

	/**
	 * Registers a WikiEventListener with this instance. This is a convenience method.
	 * 
	 * @param listener
	 *            the event listener
	 */
	//@Override
	public synchronized void addWikiEventListener(WikiEventListener listener) {
		WikiEventManager.addWikiEventListener(this, listener);
	}

	/**
	 * Un-registers a WikiEventListener with this instance. This is a convenience method.
	 * 
	 * @param listener
	 *            the event listener
	 */
	//@Override
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
	protected void fireEvent(int type, Session session, Object profile) {
		if (WikiEventManager.isListening(this)) {
			WikiEventManager.fireEvent(this, new WikiSecurityEvent(session, type, profile));
		}
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
	
	// -- events processing --------------------------------------------(end)--
}