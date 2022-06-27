package org.elwiki.ajax_dispatcher.internal;

import java.security.Permission;

import org.apache.wiki.ajax.WikiAjaxServlet;

class AjaxServletContainer {

	final String alias;
	final WikiAjaxServlet servlet;
	final Permission permission;

	public AjaxServletContainer(final String alias, final WikiAjaxServlet servlet, final Permission permission) {
		this.alias = alias;
		this.servlet = servlet;
		this.permission = permission;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + alias + "=" + servlet.getClass().getSimpleName() + " permission="
				+ permission;
	}

}
