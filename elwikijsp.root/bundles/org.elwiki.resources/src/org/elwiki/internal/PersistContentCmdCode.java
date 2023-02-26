package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.IStorageCdo;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;

public class PersistContentCmdCode extends CmdCode {

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		Engine wiki = wikiContext.getEngine();
		IStorageCdo storageCdo = wiki.getManager(IStorageCdo.class);

		String action = httpRequest.getParameter("action");
		switch (action) {
		case "load":
			storageCdo.loadAllContent();
			break;
		case "save":
			storageCdo.saveAllContent();
			break;
		}

		String redirectPageId = httpRequest.getParameter("redirect");
		httpResponse.sendRedirect(wikiContext.getViewURL(redirectPageId));	
	}

}
