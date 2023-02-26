package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.api.authorization.IGroupWiki;

public class EditGroupCmdCode extends CmdCode {

	protected EditGroupCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		Engine wiki = wikiContext.getEngine();
		@NonNull
		AuthorizationManager authorizationManager = wiki.getManager(AuthorizationManager.class);
		@NonNull
		AccountManager accountManager = wiki.getManager(AccountManager.class);

		// Check for authorization
		if (!authorizationManager.hasAccess(wikiContext, httpResponse)) {
			return;
		}

		// Extract the current user, group name, members and action attributes
		Session wikiSession = wikiContext.getWikiSession();
		IGroupWiki groupWiki = null;
		/*:FVK: TODO:... передача редактируемой группы. */
		try {
			groupWiki = accountManager.parseGroup(wikiContext, false);
			// pageContext.setAttribute( "Group", group, PageContext.REQUEST_SCOPE );

			/* TODO: if group == null (undefined) - make redirect:
	        wikiSession.addMessage( AccountManager.MESSAGES_KEY, "Parameter 'group' cannot be null." );
	        response.sendRedirect( "Group.jsp" );*/        	

			httpRequest.setAttribute("Group", groupWiki); //HACK: вместо pageContext.setAttribute() 
		} catch (WikiSecurityException e) {
			wikiSession.addMessage(AccountManager.MESSAGES_KEY, e.getMessage());
			httpResponse.sendRedirect("Group.jsp");
		}

		// Are we saving the group?
		// :FVK: TODO:... проверить, рефакторизовать (выделить функционал групп).
		if ("save".equals(httpRequest.getParameter("action"))) {
			// Validate the group
			accountManager.validateGroup(wikiContext, groupWiki);

			// If no errors, save the group now
			if (wikiSession.getMessages(AccountManager.MESSAGES_KEY).length == 0) {
				try {
					accountManager.setGroup(wikiSession, groupWiki);
				} catch (WikiSecurityException e) {
					// Something went horribly wrong! Maybe it's an I/O error...
					wikiSession.addMessage(AccountManager.MESSAGES_KEY, e.getMessage());
				}
			}
			if (wikiSession.getMessages(AccountManager.MESSAGES_KEY).length == 0) {
				httpResponse.sendRedirect("Group.jsp?group=" + groupWiki.getName());
				return;
			}
		}
	}

}
