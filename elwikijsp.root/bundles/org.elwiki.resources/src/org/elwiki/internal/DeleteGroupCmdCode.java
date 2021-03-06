package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.auth.WikiSecurityException;
import org.elwiki.api.authorization.IAuthorizer;
import org.elwiki.api.authorization.WrapGroup;
import org.elwiki.services.ServicesRefs;

public class DeleteGroupCmdCode extends CmdCode {

	protected DeleteGroupCmdCode(Command command) {
		super(command);
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		Engine wiki = ServicesRefs.Instance;

		// Create wiki context and check for authorization
		Context wikiContext = Wiki.context().create(wiki, httpRequest, ContextEnum.GROUP_EDIT.getRequestContext());
		if (!ServicesRefs.getAuthorizationManager().hasAccess(wikiContext, response))
			return;

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

	    // Now, let's delete the group
	    try
	    {
	        groupMgr.removeGroup( group );
	        //response.sendRedirect( "." );
	        response.sendRedirect( "Group.jsp?group=" + group.getName() );
	    }
	    catch ( WikiSecurityException e )
	    {
	        // Send error message
	        wikiSession.addMessage( IAuthorizer.MESSAGES_KEY, e.getMessage() );
	        response.sendRedirect( "Group.jsp" );
	    }
	}

}
