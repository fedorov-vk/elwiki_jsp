package org.elwiki.internal;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiSecurityException;
import org.elwiki.api.authorization.IGroupManager;
import org.elwiki.api.authorization.WrapGroup;
import org.elwiki.services.ServicesRefs;
import org.osgi.service.useradmin.Group;

public class CreateGroupCmdCode extends CmdCode {

	protected CreateGroupCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		Engine wiki = ServicesRefs.Instance;

		// Get wiki context and check for authorization
		WikiContext wikiContext = Wiki.context().create(wiki, httpRequest, WikiContext.GROUP_EDIT);
		if (!ServicesRefs.getAuthorizationManager().hasAccess(wikiContext, httpResponse)) {
			return;
		}

		// Extract the current user, group name, members and action attributes
		Session wikiSession = wikiContext.getWikiSession();
		IGroupManager groupMgr = ServicesRefs.getGroupManager();
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
			wikiSession.addMessage(IGroupManager.MESSAGES_KEY, e.getMessage());
			httpResponse.sendRedirect("Group.jsp");
		}

		// Are we saving the group?
		// :FVK: TODO:... проверить, рефакторизовать (выделить функционал групп).
		if ("save".equals(httpRequest.getParameter("action"))) {
			// Validate the group
			groupMgr.validateGroup(wikiContext, group);

			Group grp2 = groupMgr.getGroup(group.getName());
			if (grp2 != null) {

				// Oops! The group already exists. This is mischief!
				ResourceBundle rb = null; // Preferences.getBundle( wikiContext, "CoreResources");
				wikiSession.addMessage(IGroupManager.MESSAGES_KEY,
						MessageFormat.format(rb.getString("newgroup.exists"), group.getName()));
			}
			// Group not found; this is good!
			
			// If no errors, save the group now
			if (wikiSession.getMessages(IGroupManager.MESSAGES_KEY).length == 0) {
				try {
					groupMgr.setGroup( wikiSession, group );
				} catch (WikiSecurityException e) {
					// Something went horribly wrong! Maybe it's an I/O error...
	                wikiSession.addMessage(IGroupManager.MESSAGES_KEY, e.getMessage());
				}
			}
			if (wikiSession.getMessages(IGroupManager.MESSAGES_KEY).length == 0) {
				httpResponse.sendRedirect("Group.jsp?group=" + group.getName());
				return;
			}
		}
	}

}
