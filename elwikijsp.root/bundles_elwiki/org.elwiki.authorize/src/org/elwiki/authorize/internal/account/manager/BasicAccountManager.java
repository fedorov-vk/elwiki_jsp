package org.elwiki.authorize.internal.account.manager;

import org.apache.log4j.Logger;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.AuthenticationManager;
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

	protected abstract AuthenticationManager getAuthenticationManager();

	protected abstract TasksManager getTasksManager();

	protected abstract FilterManager getFilterManager();

	protected abstract UserAdmin getUserAdmin();

}
