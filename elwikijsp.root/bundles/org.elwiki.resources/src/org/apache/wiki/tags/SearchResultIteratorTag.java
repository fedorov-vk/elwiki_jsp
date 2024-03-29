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
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.search.SearchResult;
import org.apache.wiki.api.ui.PageCommand;

/**
 * Iterates through results of Search operation.
 *
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>id
 * <li>maxItems = how many search results should be shown.
 * <li>list
 * <li>start
 * </ul>
 *
 * @since 2.0
 */
// FIXME: Shares MUCH too much in common with IteratorTag.  Must refactor.
public class SearchResultIteratorTag extends BaseIteratorTag<SearchResult> {

	private static final long serialVersionUID = 2710478241806938763L;

	private int m_maxItems;
	private int m_count = 0;
	private int m_start = 0;

	private static final Logger log = Logger.getLogger(SearchResultIteratorTag.class);

	/** {@inheritDoc} */
	@Override
	public void release() {
		super.release();
		m_maxItems = m_count = 0;
	}

	public void setMaxItems(final int arg) {
		m_maxItems = arg;
	}

	public void setStart(final int arg) {
		m_start = arg;
	}

	/** {@inheritDoc} */
	@Override
	public final int doStartTag() {
		//  Do lazy eval if the search results have not been set.
		if (m_iterator == null) {
			@SuppressWarnings("unchecked")
			final Collection<SearchResult> searchresults = (Collection<SearchResult>) pageContext
					.getAttribute("searchresults", PageContext.REQUEST_SCOPE);
			setList(searchresults);

			int skip = 0;

			//  Skip the first few ones...
			m_iterator = searchresults.iterator();
			while (m_iterator.hasNext() && (skip++ < m_start)) {
				m_iterator.next();
			}
		}

		m_count = 0;

		return nextResult();
	}

	private int nextResult() {
		if (m_iterator != null && m_iterator.hasNext() && m_count++ < m_maxItems) {
			final SearchResult r = (SearchResult) m_iterator.next();
			if (r == null)
				return SKIP_BODY;

			// Create a wiki context for the result (context represents the found page).
			final Engine engine = getWikiContext().getEngine();
			final HttpServletRequest request = getWikiContext().getHttpRequest();
			final Command command = PageCommand.VIEW.targetedCommand(r.getPage());
			final WikiContext context = Wiki.context().create(engine, request, command);

			// Stash it in the page context
			pageContext.setAttribute(WikiContext.ATTR_WIKI_CONTEXT, context, PageContext.REQUEST_SCOPE);
			pageContext.setAttribute(getId(), r);

			return EVAL_BODY_BUFFERED;
		}

		return SKIP_BODY;
	}

	/** {@inheritDoc} */
	@Override
	public int doAfterBody() {
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

		return nextResult();
	}

	/** {@inheritDoc} */
	@Override
	public int doEndTag() {
		m_iterator = null;
		return super.doEndTag();
	}

}
