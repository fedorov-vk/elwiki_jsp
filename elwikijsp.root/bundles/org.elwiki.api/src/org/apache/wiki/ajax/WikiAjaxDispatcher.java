package org.apache.wiki.ajax;

import java.security.Permission;

import org.elwiki.permissions.PagePermission;

public interface WikiAjaxDispatcher {

	String WIKI_AJAX_DISPATCHER_FACTORY = "elwiki.WikiAjaxDispatcher.factory";

	/**
	 * Register a {@link WikiAjaxServlet} using the servlet mapping as the alias.
	 * 
	 * @param servlet
	 */
	void registerServlet(WikiAjaxServlet servlet);

	/**
	 * Register a {@link WikiAjaxServlet} with a specific alias, and default permission
	 * {@link PagePermission#VIEW}.
	 * 
	 * @param alias
	 * @param servlet
	 */
	void registerServlet(String alias, WikiAjaxServlet servlet);

	/**
	 * Register a {@link WikiAjaxServlet} given an alias, the servlet, and the permission. This
	 * creates a temporary bundle object called
	 * {@link WikiAjaxDispatcherService.AjaxServletContainer}
	 *
	 * @param alias   the uri link to this servlet
	 * @param servlet the servlet being registered
	 * @param perm    the permission required to execute the servlet.
	 */
	void registerServlet(String alias, WikiAjaxServlet servlet, Permission perm);

}
