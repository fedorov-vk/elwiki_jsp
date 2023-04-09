package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.wiki.WatchDog;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.util.TextUtil;
import org.eclipse.emf.common.util.EList;
import org.elwiki.api.WikiScopeManager;
import org.elwiki_data.PageAclEntry;
import org.elwiki_data.WikiPage;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AclEntryEditCmdCode extends CmdCode {

	/** The name of the cookie that gets stored to the user`s browser. */
	public static final String SCOPES_COOKIE_NAME = "ElWikiScopes";

	protected AclEntryEditCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		// Get wiki context and check for authorization
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		AuthorizationManager authorizationManager = getEngine().getManager(AuthorizationManager.class);
		authorizationManager.checkAccess(wikiContext, httpRequest, httpResponse);
		/*:FVK: - зачем это?
		if( wikiContext.getCommand().getTarget() == null ) {
		    response.sendRedirect( wikiContext.getURL( wikiContext.getRequestContext(), wikiContext.getName() ) );
		    return;
		}
		 */

		HttpSession session = httpRequest.getSession();		

		Engine wiki = wikiContext.getEngine();

		String pagereq = wikiContext.getName();

		/////////
		/*
		WatchDog w = WatchDog.getCurrentWatchDog(WikiEngine.Instance);
		try {
			w.enterState("Generating INFO response", 60);
		
			// Set the content type and include the response content
			response.setContentType("text/html; charset=" + WikiEngine.Instance.getContentEncoding());
		} finally {
		}
		*/

		// Are we save updated AclEntry?
		if ("save".equals(httpRequest.getParameter("action"))) {
			String function = httpRequest.getParameter("function");
			String permissionAction = httpRequest.getParameter("permission");
			String roles = httpRequest.getParameter("roles");

			WikiPage wikiPage = wikiContext.getPage();
			for( PageAclEntry aclEntry : wikiPage.getPageAcl()) {
				// .... 
			}

			/*
			String targetPageId = httpRequest.getParameter("redirect");
			String redirectedUrl = "cmd.view?pageId=" + targetPageId;
			httpResponse.sendRedirect(redirectedUrl);
			*/
		}
	}

	public void applyEpilogue() {
		/*
		w.exitState();
		*/
	}

}
