package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.wiki.WatchDog;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.util.TextUtil;
import org.elwiki.api.WikiScopeManager;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AclPageCmdCode extends CmdCode {

	/** The name of the cookie that gets stored to the user`s browser. */
	public static final String SCOPES_COOKIE_NAME = "ElWikiScopes";

	protected AclPageCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		// Get wiki context and check for authorization
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		Engine wiki = wikiContext.getEngine();
		/*:FVK:
		if(false == WikiEngine.getAuthorizationManager().hasAccess( wikiContext, response ) ) {
			return;
		}
		*/

		/*
		if( !WikiEngine.getAuthorizationManager().hasAccess( wikiContext, response ) ) {
			return;
		}
		if( wikiContext.getCommand().getTarget() == null ) {
		    response.sendRedirect( wikiContext.getURL( wikiContext.getRequestContext(), wikiContext.getName() ) );
		    return;
		}
		*/
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

		// Are we set selected scope?
		if ("submitscope".equals(httpRequest.getParameter("action"))) {
			WikiScopeManager scopeManager = wiki.getManager(WikiScopeManager.class);

			String scopeArea = httpRequest.getParameter("scopearea");
			String scopeName = httpRequest.getParameter("ScopeList");
			String scopes = TextUtil.urlDecodeUTF8(HttpUtil.retrieveCookieValue(httpRequest, SCOPES_COOKIE_NAME));
			scopeManager.ReinitScope(wikiContext, scopeArea, scopeName, scopes);

			String targetPageId = httpRequest.getParameter("redirect");
			String redirectedUrl = "cmd.view?pageId=" + targetPageId;
			httpResponse.sendRedirect(redirectedUrl);
		}
	}

	public void applyEpilogue() {
		/*
		w.exitState();
		*/
	}

}
