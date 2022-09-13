package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.auth.WikiSecurityException;
import org.elwiki.api.authorization.IAuthorizer;
import org.elwiki.api.authorization.WrapGroup;
import org.elwiki.services.ServicesRefs;

public class EditGroupCmdCode extends CmdCode {

	protected EditGroupCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {

		// Get wiki context and check for authorization
		Context wikiContext = ContextUtil.findContext(httpRequest);
		Engine wiki = wikiContext.getEngine();
		if (!ServicesRefs.getAuthorizationManager().hasAccess(wikiContext, response)) {
			return;
		}

		// Extract the current user, group name, members and action attributes
		Session wikiSession = wikiContext.getWikiSession();
		IAuthorizer groupMgr = ServicesRefs.getGroupManager();
		WrapGroup group = null;
		/*:FVK: TODO:... передача редактируемой группы. */
		try {
			group = groupMgr.parseGroup(wikiContext, false);
			// pageContext.setAttribute( "Group", group, PageContext.REQUEST_SCOPE );

			/* TODO: if group == null (undefined) - make redirect:
	        wikiSession.addMessage( GroupManager.MESSAGES_KEY, "Parameter 'group' cannot be null." );
	        response.sendRedirect( "Group.jsp" );*/        	

			httpRequest.setAttribute("Group", group); //HACK: вместо pageContext.setAttribute() 
		} catch (WikiSecurityException e) {
			wikiSession.addMessage(IAuthorizer.MESSAGES_KEY, e.getMessage());
			response.sendRedirect("Group.jsp");
		}

		// Are we saving the group?
		// :FVK: TODO:... проверить, рефакторизовать (выделить функционал групп).
		if ("save".equals(httpRequest.getParameter("action"))) {
			// Validate the group
			groupMgr.validateGroup(wikiContext, group);

			// If no errors, save the group now
			if (wikiSession.getMessages(IAuthorizer.MESSAGES_KEY).length == 0) {
				try {
					groupMgr.setGroup(wikiSession, group);
				} catch (WikiSecurityException e) {
					// Something went horribly wrong! Maybe it's an I/O error...
					wikiSession.addMessage(IAuthorizer.MESSAGES_KEY, e.getMessage());
				}
			}
			if (wikiSession.getMessages(IAuthorizer.MESSAGES_KEY).length == 0) {
				response.sendRedirect("Group.jsp?group=" + group.getName());
				return;
			}
		}
	}

}
