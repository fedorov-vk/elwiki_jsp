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

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki.services.ServicesRefs;

/**
 * Writes difference between two pages using a HTML table. If there is no
 * difference, includes the body.
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
public class InsertDiffTag extends BaseWikiTag {

	private static final long serialVersionUID = 3396488560010158010L;
	private static final Logger log = Logger.getLogger(InsertDiffTag.class);

	/** Attribute which is used to store the old page content to the Page Context */
	public static final String ATTR_OLDVERSION = "olddiff";

	/** Attribute which is used to store the new page content to the Page Context */
	public static final String ATTR_NEWVERSION = "newdiff";

	private String m_pageName;
	private String m_pageId;

	/** {@inheritDoc} */
	@Override
	public void initTag() {
		super.initTag();
		m_pageName = m_pageId = null;
	}

	/**
	 * Sets the page name.
	 * 
	 * @param page Page to get diff from.
	 */
	public void setPageName(String pageName) {
		m_pageName = pageName;
	}

	/**
	 * Sets the page ID.
	 * 
	 * @param pageId
	 */
	public void setPageId(String pageId) {
		m_pageId = pageId;
	}

	/**
	 * Gets the page name.
	 * 
	 * @return The page name.
	 */
	public String getPage() {
		return m_pageName;
	}

	/** {@inheritDoc} */
	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		final Engine engine = m_wikiContext.getEngine();
		final WikiContext ctx;

		if (m_pageId != null) {
			ctx = m_wikiContext.clone();
			ctx.setPage(ServicesRefs.getPageManager().getPageById(m_pageId));
		} else if (m_pageName != null) {
			ctx = m_wikiContext.clone();
			ctx.setPage(ServicesRefs.getPageManager().getPage(m_pageName));
		} else {
			ctx = m_wikiContext;
		}

		final Integer vernew = (Integer) pageContext.getAttribute(ATTR_NEWVERSION, PageContext.REQUEST_SCOPE);
		final Integer verold = (Integer) pageContext.getAttribute(ATTR_OLDVERSION, PageContext.REQUEST_SCOPE);

		log.debug("Request diff between version " + verold + " and " + vernew);

		if (ctx.getPage() != null) {
			final JspWriter out = pageContext.getOut();
			final String diff = ServicesRefs.getDifferenceManager().getDiff(ctx, vernew.intValue(), verold.intValue());

			if (diff.length() == 0) {
				return EVAL_BODY_INCLUDE;
			}

			out.write(diff);
		}

		return SKIP_BODY;
	}

}
