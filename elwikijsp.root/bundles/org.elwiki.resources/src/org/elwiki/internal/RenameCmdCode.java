package org.elwiki.internal;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.content0.PageRenamer;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.tags.BreadcrumbsTag;
import org.apache.wiki.tags.BreadcrumbsTag.FixedQueue;
import org.apache.wiki.util.HttpUtil;

public class RenameCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(RenameCmdCode.class);

	protected RenameCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		
		AuthorizationManager authorizationManager = getEngine().getManager(AuthorizationManager.class);

		// Get wiki context and check for authorization
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		authorizationManager.checkAccess(wikiContext, httpRequest, httpResponse);
		if (wikiContext.getCommand().getTarget() == null) {
			httpResponse.sendRedirect(wikiContext.getURL(wikiContext.getRequestContext(), wikiContext.getName()));
			return;
		}

		String renameFrom = wikiContext.getName();
		String renameTo = httpRequest.getParameter("renameto");

		boolean changeReferences = false;

		ResourceBundle rb = Preferences.getBundle(wikiContext);

		if (httpRequest.getParameter("references") != null) {
			changeReferences = true;
		}

		log.info("Page rename request for page '" + renameFrom + "' to new name '" + renameTo + "' from "
				+ HttpUtil.getRemoteAddress(httpRequest) + " by " + httpRequest.getRemoteUser());

		WikiSession wikiSession = wikiContext.getWikiSession();
		try {
			HttpSession session = httpRequest.getSession();
			if (renameTo.length() > 0) {
				PageRenamer pageRenamer = getEngine().getManager(PageRenamer.class);
				String renamedTo = pageRenamer.renamePage(wikiContext, renameFrom, renameTo,
						changeReferences);

				FixedQueue trail = (FixedQueue) session.getAttribute(BreadcrumbsTag.BREADCRUMBTRAIL_KEY);
				if (trail != null) {
					trail.removeItem(renameFrom);
					session.setAttribute(BreadcrumbsTag.BREADCRUMBTRAIL_KEY, trail);
				}

				log.info("Page successfully renamed to '" + renamedTo + "'");

				httpResponse.sendRedirect(wikiContext.getURL(ContextEnum.PAGE_VIEW.getRequestContext(), renamedTo));
				return;
			}
			wikiSession.addMessage("rename", rb.getString("rename.empty"));

			log.info("Page rename request failed because new page name was left blank");
		} catch (WikiException e) {
			if (e.getMessage().equals("You cannot rename the page to itself")) {
				log.info("Page rename request failed because page names are identical");
				wikiSession.addMessage("rename", rb.getString("rename.identical"));
			} else if (e.getMessage().startsWith("Page already exists ")) {
				log.info("Page rename request failed because new page name is already in use");
				wikiSession.addMessage("rename", MessageFormat.format(rb.getString("rename.exists"), renameTo));
			} else {
				wikiSession.addMessage("rename",
						MessageFormat.format(rb.getString("rename.unknownerror"), e.toString()));
			}
		}
	}

}
