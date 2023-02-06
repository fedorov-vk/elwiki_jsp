package org.elwiki.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.wiki.api.core.WikiContext;

public interface WikiScopeManager {
	String getData();

	String[] getScopeList(HttpServletRequest request);

	/**
	 * The method sets scope parameters for the specified wiki session. The session is contained in
	 * the wikiContext parameter.
	 * 
	 * @param wikiContext
	 * @param scopeArea   defines the scope category: "all": all pages; "selected": set by the
	 *                    specified scope name.
	 * @param scopeName   defines the name of the specified scope area or =null in the case of all
	 *                    pages.
	 * @param scopes      contains JSON data defining the names and pages of named scopes.
	 */
	void ReinitScope(WikiContext wikiContext, String scopeArea, String scopeName, String scopes);
}
