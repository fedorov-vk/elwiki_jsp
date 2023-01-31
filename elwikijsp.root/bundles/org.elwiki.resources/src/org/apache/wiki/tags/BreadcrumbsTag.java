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
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.util.TextUtil;
import org.elwiki.services.ServicesRefs;

/**
 * Implement a "breadcrumb" (most recently visited) trail. This tag can be added
 * to any view jsp page. Separate breadcrumb trails are not tracked across
 * multiple browser windows.<br>
 * The optional attributes are:
 * <p>
 * <b>maxpages</b>, the number of pages to store, 10 by default<br>
 * <b>separator</b>, the separator string to use between pages, " | " by
 * default<br>
 * </p>
 *
 * <p>
 * This class is implemented by storing a breadcrumb trail, which is a fixed
 * size queue, into a session variable "breadCrumbTrail". This queue is
 * displayed as a series of links separated by a separator character.
 * </p>
 */
public class BreadcrumbsTag extends BaseWikiTag {

	public static class PageDescription {

		public String name;
		public String id;

		public PageDescription(String name, String id) {
			this.name = name;
			this.id = id;
		}

		@Override
		public String toString() {
			return id + ": " + name;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, name);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PageDescription other = (PageDescription) obj;
			return Objects.equals(id, other.id) && Objects.equals(name, other.name);
		}
	}

	/**
	 * Extends the LinkedList class to provide a fixed-size queue implementation
	 */
	public static class FixedQueue extends LinkedList<PageDescription> implements Serializable {

		private static final long serialVersionUID = 7399763506436664635L;

		private int m_size;

		FixedQueue(int size) {
			m_size = size;
		}

		PageDescription pushItem(PageDescription o) {
			if (!super.contains(o)) {
				add(o);
			}
			if (size() > m_size) {
				return removeFirst();
			}

			return null;
		}

		/**
		 * @param pageName the page to be deleted from the breadcrumb
		 */
		public void removeItem(String pageName) {
			for (int i = 0; i < size(); i++) {
				final PageDescription page = get(i);
				if (page != null && page.equals(pageName)) {
					remove(page);
				}
			}
		}

	}

	private static final long serialVersionUID = -6878621471743655660L;
	private static final Logger log = Logger.getLogger(BreadcrumbsTag.class);

	/** The name of the session attribute representing the breadcrumbtrail */
	public static final String BREADCRUMBTRAIL_KEY = "breadCrumbTrail";

	public static final int MAX_PAGES_DEFAULT = 10;

	private int m_maxQueueSize = 11;
	private String m_separator = ", ";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initTag() {
		super.initTag();
		m_maxQueueSize = MAX_PAGES_DEFAULT + 1;
		m_separator = ", ";
	}

	/**
	 * Returns the maximum pages. This may differ from what was set by
	 * setMaxpages().
	 *
	 * @return The current size of the pages.
	 */
	public int getMaxpages() {
		return m_maxQueueSize;
	}

	/**
	 * Sets how many pages to show.
	 *
	 * @param maxpages The amount.
	 */
	public void setMaxpages(final int maxpages) {
		m_maxQueueSize = maxpages + 1;
	}

	/**
	 * Get the separator string.
	 *
	 * @return The string set in setSeparator()
	 */
	public String getSeparator() {
		return m_separator;
	}

	/**
	 * Set the separator string.
	 *
	 * @param separator A string which separates the page names.
	 */
	public void setSeparator(final String separator) {
		m_separator = TextUtil.replaceEntities(separator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		final HttpSession session = pageContext.getSession();
		FixedQueue trail = (FixedQueue) session.getAttribute(BREADCRUMBTRAIL_KEY);
		final String pageName = m_wikiContext.getPage().getName();
		final String pageId = m_wikiContext.getPage().getId();

		if (trail == null) {
			trail = new FixedQueue(m_maxQueueSize);
		} else {
			//  check if page still exists (could be deleted/renamed by another user)
			for (int i = 0; i < trail.size(); i++) {
				String pageId1 = trail.get(i).id;
				if (!ServicesRefs.getPageManager().pageExistsById(pageId1)) {
					trail.remove(i);
				}
			}
		}

		if (m_wikiContext.getRequestContext().equals(ContextEnum.PAGE_VIEW.getRequestContext())) {
			if (ServicesRefs.getPageManager().pageExistsById(pageId)) {
				if (trail.isEmpty()) {
					trail.pushItem(new PageDescription(pageName, pageId));
				} else {
					// Don't add the page to the queue if the page was just refreshed
					if (!trail.getLast().equals(pageId)) {
						trail.pushItem(new PageDescription(pageName, pageId));
					}
				}
			} else {
				log.debug("didn't add page because it doesn't exist: " + pageId);
			}
		}

		session.setAttribute(BREADCRUMBTRAIL_KEY, trail);

		//
		//  Print out the breadcrumb trail
		//
		final JspWriter out = pageContext.getOut();
		final int queueSize = trail.size();
		final String linkclass = "wikipage";

		for (int i = queueSize - 2; i >= 0; i--) {
			String pageName1 = trail.get(i).name;
			String pageId1 = trail.get(i).id;

			//FIXME: I can't figure out how to detect the appropriate jsp page to put here, so I hard coded Wiki.jsp
			//This breaks when you view an attachment metadata page
			out.print("<a class=\"" + linkclass + "\" href=\"" + m_wikiContext.getViewURL(pageId1) + "\">"
					+ TextUtil.replaceEntities(pageName1) + "</a>");

			if (i < queueSize - 2) {
				out.print(m_separator);
			}
		}

		return SKIP_BODY;
	}

}
