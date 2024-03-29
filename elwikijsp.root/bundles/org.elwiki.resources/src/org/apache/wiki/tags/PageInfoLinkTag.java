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

import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.pages0.PageManager;
import org.elwiki_data.WikiPage;

/**
 * Writes a link to the Wiki PageInfo. Body of the link becomes the actual text.
 *
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>pageName - Page name to refer to. Default is the current page.
 * <li>title - Is used in page actions to display hover text (tooltip)
 * <li>format - Either "url" or "anchor".
 * <li>accesskey - Set an accesskey (ALT+[Char])
 * </ul>
 *
 * @since 2.0
 */
// FIXME: Refactor together with LinkToTag and EditLinkTag.
public class PageInfoLinkTag extends BaseWikiLinkTag {

	private static final long serialVersionUID = 8852324545520143793L;

	public String m_title = "";
	public String m_accesskey = "";

	public void setTitle(final String title) {
		m_title = title;
	}

	public void setAccesskey(final String access) {
		m_accesskey = access;
	}

	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		final Engine engine = wikiContext.getEngine();
		PageManager pageManager = engine.getManager(PageManager.class);
		String pageName = m_pageName;

		if (m_pageName == null) {
			final WikiPage p = wikiContext.getPage();
			if (p != null) {
				pageName = p.getName();
			} else {
				return SKIP_BODY;
			}
		}

		if (pageManager.pageExistsByName(pageName)) {
			final JspWriter out = pageContext.getOut();
			final String url = wikiContext.getURL(ContextEnum.PAGE_INFO.getRequestContext(), pageName);

			switch (m_format) {
			case ANCHOR:
				out.print("<a class=\"pageinfo\" href=\"" + url + "\" accesskey=\"" + m_accesskey + "\" title=\""
						+ m_title + "\">");
				break;
			case URL:
				out.print(url);
				break;
			}
			return EVAL_BODY_INCLUDE;
		}
		return SKIP_BODY;
	}

}
