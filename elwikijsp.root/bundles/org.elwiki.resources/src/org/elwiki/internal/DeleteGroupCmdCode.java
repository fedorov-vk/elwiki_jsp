package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.services.ServicesRefs;

public class DeleteGroupCmdCode extends CmdCode {

	protected DeleteGroupCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		Engine wiki = ServicesRefs.Instance;

		// Create wiki context and check for authorization
		WikiContext wikiContext = Wiki.context().create(wiki, httpRequest, WikiContext.GROUP_EDIT);
		if (!ServicesRefs.getAuthorizationManager().hasAccess(wikiContext, httpResponse))
			return;

		// Extract the current user, group name, members and action attributes
		Session wikiSession = wikiContext.getWikiSession();
		AccountManager accountManager = ServicesRefs.getAccountManager();
		IGroupWiki group = null;
		/*:FVK: TODO:... передача редактируемой группы. */
		try {
			group = accountManager.parseGroup(wikiContext, false);
			// pageContext.setAttribute( "Group", group, PageContext.REQUEST_SCOPE );

			/* TODO: if group == null (undefined) - make redirect:
	        wikiSession.addMessage( AccountManager.MESSAGES_KEY, "Parameter 'group' cannot be null." );
	        response.sendRedirect( "Group.jsp" );*/        	
			
			httpRequest.setAttribute("Group", group); //HACK: вместо pageContext.setAttribute() 
		} catch (WikiSecurityException e) {
			wikiSession.addMessage(AccountManager.MESSAGES_KEY, e.getMessage());
			httpResponse.sendRedirect("Group.jsp");
		}

	    // Now, let's delete the group
	    try
	    {
	        accountManager.removeGroup( group );
	        //response.sendRedirect( "." );
	        httpResponse.sendRedirect( "Group.jsp?group=" + group.getName() );
	    }
	    catch ( WikiSecurityException e )
	    {
	        // Send error message
	        wikiSession.addMessage( AccountManager.MESSAGES_KEY, e.getMessage() );
	        httpResponse.sendRedirect( "Group.jsp" );
	    }
	}

}
