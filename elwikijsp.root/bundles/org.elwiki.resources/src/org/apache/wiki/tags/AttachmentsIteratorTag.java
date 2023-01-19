/* 
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.  
 */
package org.apache.wiki.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 * Iterates through the list of attachments one has.
 *
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>page - Page name to refer to. Default is the current page.
 * </ul>
 *
 * @since 2.0
 */
// FIXME: Too much in common with IteratorTag - REFACTOR
public class AttachmentsIteratorTag extends BaseIteratorTag<PageAttachment> {

	private static final long serialVersionUID = -6348093527562836028L;
	private static final Logger log = Logger.getLogger(AttachmentsIteratorTag.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int doStartTag() {
		m_wikiContext = (Context) pageContext.getAttribute(Context.ATTR_WIKI_CONTEXT, PageContext.REQUEST_SCOPE);
		final Engine engine = m_wikiContext.getEngine();
		final AttachmentManager mgr = ServicesRefs.getAttachmentManager();
		final WikiPage page;

		page = m_wikiContext.getPage();

		if (!mgr.attachmentsEnabled()) {
			return SKIP_BODY;
		}

		try {
			if (page != null && ServicesRefs.getPageManager().wikiPageExists(page)) {
				final List<PageAttachment> atts = mgr.listAttachments(page);

				if (atts == null) {
					log.debug("No attachments to display.");
					// There are no attachments included
					return SKIP_BODY;
				}

				m_iterator = atts.iterator();

				if (m_iterator.hasNext()) {
					final PageAttachment att = (PageAttachment) m_iterator.next();
					att.forLastContent(); // workaround.
					final Context context = m_wikiContext.clone();
					//:FVK: context.setPage( att );
					pageContext.setAttribute(Context.ATTR_WIKI_CONTEXT, context, PageContext.REQUEST_SCOPE);
					String attrId = getId();
					pageContext.setAttribute(attrId, att);
				} else {
					return SKIP_BODY;
				}
			} else {
				return SKIP_BODY;
			}

			return EVAL_BODY_BUFFERED;
		} catch (final ProviderException e) {
			log.fatal("Provider failed while trying to iterator through history", e);
			// FIXME: THrow something.
		}

		return SKIP_BODY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int doAfterBody() {
		if (bodyContent != null) {
			try {
				final JspWriter out = getPreviousOut();
				out.print(bodyContent.getString());
				bodyContent.clearBody();
			} catch (final IOException e) {
				log.error("Unable to get inner tag text", e);
				// FIXME: throw something?
			}
		}

		if (m_iterator != null && m_iterator.hasNext()) {
			final PageAttachment att = (PageAttachment) m_iterator.next();
			final Context context = m_wikiContext.clone();
			//:FVK: context.setPage( att );
			pageContext.setAttribute(Context.ATTR_WIKI_CONTEXT, context, PageContext.REQUEST_SCOPE);
			pageContext.setAttribute(getId(), att);

			return EVAL_BODY_BUFFERED;
		}

		return SKIP_BODY;
	}

}
