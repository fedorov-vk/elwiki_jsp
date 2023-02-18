package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.WikiContext;
import org.eclipse.jdt.annotation.NonNull;

public class DeleteAttachmentCmdCode extends CmdCode {

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		String attachmentId = (String) httpRequest.getParameter("idattach");

		@NonNull
		AttachmentManager attachmentManager = getEngine().getManager(AttachmentManager.class);
		attachmentManager.deleteAttachmentById(attachmentId);

		String url = wikiContext.getURL(WikiContext.ATTACHMENT_UPLOAD, wikiContext.getPageId());
		httpResponse.sendRedirect(url);
	}

}
