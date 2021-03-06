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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.PageContent;
import org.elwiki_data.WikiPage;

/**
 * Iterates through tags.
 *
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <LI>page - Page name to refer to. Default is the current page.
 * </ul>
 *
 * @since 2.0
 */
// FIXME: Too much in common with IteratorTag - REFACTOR
public class HistoryIteratorTag extends BaseIteratorTag<PageContent> {

	private static final long serialVersionUID = 7176074423129405610L;
	private static final Logger LOG = Logger.getLogger(HistoryIteratorTag.class);

	/** {@inheritDoc} */
	@Override
	public final int doStartTag() {
		m_wikiContext = (Context) pageContext.getAttribute(Context.ATTR_WIKI_CONTEXT, PageContext.REQUEST_SCOPE);
		final Engine engine = m_wikiContext.getEngine();
		final WikiPage page = m_wikiContext.getPage();

		try {
			if (page != null && ServicesRefs.getPageManager().wikiPageExists(page)) {
				//:FVK: ?????????????? - ???????????? ?????????????? ???? ????????????????.  final List< WikiPage > versions = ResourcesRefs.getPageManager().getVersionHistory( page );
				List<PageContent> contents = new ArrayList<>(page.getPagecontents());
				contents.sort((c1, c2) -> {
					return c2.getVersion() - c1.getVersion();
				});
				m_iterator = contents.iterator();
				if (m_iterator.hasNext()) {
					final Context context = m_wikiContext.clone();
					// :FVK:              context.setPage( ( WikiPage )m_iterator.next() );
					pageContext.setAttribute(Context.ATTR_WIKI_CONTEXT, context, PageContext.REQUEST_SCOPE);
					// :FVK:              pageContext.setAttribute( getId(), context.getPage() );
					pageContext.setAttribute(getId(), m_iterator.next());
				} else {
					return SKIP_BODY;
				}
			}

			return EVAL_BODY_BUFFERED;
		} catch (final ProviderException e) {
			LOG.fatal("Provider failed while trying to iterator through history", e);
			// FIXME: THrow something.
		}

		return SKIP_BODY;
	}

	/** {@inheritDoc} */
	@Override
	public final int doAfterBody() {
		if (bodyContent != null) {
			try {
				final JspWriter out = getPreviousOut();
				out.print(bodyContent.getString());
				bodyContent.clearBody();
			} catch (final IOException e) {
				LOG.error("Unable to get inner tag text", e);
				// FIXME: throw something?
			}
		}

		if (m_iterator != null && m_iterator.hasNext()) {
			final Context context = m_wikiContext.clone();
			// :FVK:      context.setPage( ( WikiPage )m_iterator.next() );
			pageContext.setAttribute(Context.ATTR_WIKI_CONTEXT, context, PageContext.REQUEST_SCOPE);
			// :FVK:      pageContext.setAttribute( getId(), context.getPage() );
			pageContext.setAttribute(getId(), m_iterator.next());
			return EVAL_BODY_BUFFERED;
		}

		return SKIP_BODY;
	}

}