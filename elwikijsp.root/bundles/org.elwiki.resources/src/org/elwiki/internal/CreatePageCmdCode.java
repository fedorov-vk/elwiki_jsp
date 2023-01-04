package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Context;
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
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		String pageName = (String) httpRequest.getParameter("pageName");
		String targetPageId = httpRequest.getParameter("redirect");
		String action = httpRequest.getParameter("action");

		// Are we create page?
		if (action != null && (action.equals("create") || action.equals("createedit"))) {
			Context m_wikiContext = (Context) httpRequest.getAttribute(Context.ATTR_WIKI_CONTEXT);
			final Engine engine = m_wikiContext.getEngine();
			@NonNull
			PageManager pageManager = engine.getManager(PageManager.class);
			WikiPage newPage = pageManager.createPage(pageName, targetPageId);

			String redirectedUrl;
			switch (action) {
			case "create":
				redirectedUrl = "cmd.view?pageId=" + newPage.getId();
				break;
			case "createedit":
				redirectedUrl = "cmd.edit?pageId=" + newPage.getId();
				break;
			default: // :FVK: wrong case.
				throw new Exception("Incorrect form action.");
			}

			response.sendRedirect(redirectedUrl);
		}
	}

	public void applyEpilogue() {
		/*
		w.exitState();
		*/
	}

}
