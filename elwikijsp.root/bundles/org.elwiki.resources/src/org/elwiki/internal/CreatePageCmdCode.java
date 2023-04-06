package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.pages0.PageManager;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki_data.WikiPage;

public class CreatePageCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(RenameCmdCode.class);

	protected CreatePageCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		String pageName = (String) httpRequest.getParameter("pageName");
		String targetPageId = httpRequest.getParameter("redirect");
		String action = httpRequest.getParameter("action");

		// Are we create page?
		if (action != null && (action.equals("create") || action.equals("createedit"))) {
			WikiContext m_wikiContext = (WikiContext) httpRequest.getAttribute(WikiContext.ATTR_WIKI_CONTEXT);
			final Engine engine = m_wikiContext.getEngine();
			@NonNull
			PageManager pageManager = engine.getManager(PageManager.class);
			WikiPage newPage = pageManager.createPage(pageName, targetPageId);

			String redirectedUrl;
			switch (action) {
			case "create":
				redirectedUrl = "cmd.view?pageId=" + newPage.getId(); //:FVK: workaround - here need refer to ContextEnum.
				break;
			case "createedit":
				redirectedUrl = "cmd.edit?pageId=" + newPage.getId(); //:FVK: workaround - here need refer to ContextEnum.
				break;
			default: // :FVK: wrong case.
				throw new Exception("Incorrect form action.");
			}

			httpResponse.sendRedirect(redirectedUrl);
		}
	}

	public void applyEpilogue() {
		/*
		w.exitState();
		*/
	}

}
