package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextUtil;

public class DeleteAttachmentCmdCode extends CmdCode {

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		String attachmentId = (String) httpRequest.getParameter("pageId"); //TODO: rename "pageId" into "id".

		//TODO: release delete attachment resources.

		String url = wikiContext.getURL(WikiContext.ATTACHMENT_UPLOAD, wikiContext.getPageId());
		httpResponse.sendRedirect(url);
	}

}
