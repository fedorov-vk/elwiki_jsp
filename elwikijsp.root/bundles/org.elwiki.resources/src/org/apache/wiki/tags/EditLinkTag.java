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

import javax.servlet.jsp.JspWriter;

import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;

/**
 * Writes an edit link. Body of the link becomes the link text.
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>pageName - Page name to refer to. Default is the current page.
 * <li>pageId - Page Id to refer to. Default is the current page.
 * <li>format - Format, either "anchor" or "url".
 * <li>version - Version number of the page to refer to. Possible values are
 * "this", meaning the version of the current page; or a version number. Default
 * is always to point at the latest version of the page.
 * <li>title - Is used in page actions to display hover text (tooltip)
 * <li>accesskey - Set an accesskey (ALT+[Char])
 * </ul>
 *
 * @since 2.0
 */
public class EditLinkTag extends WikiLinkTag {

	private static final long serialVersionUID = 0L;

	public String m_version = null;
	public String m_title = "";
	public String m_accesskey = "";

	@Override
	public void initTag() {
		super.initTag();
		m_version = null;
	}

	public void setVersion(final String vers) {
		m_version = vers;
	}

	public void setTitle(final String title) {
		m_title = title;
	}

	public void setAccesskey(final String access) {
		m_accesskey = access;
	}

	@Override
	public final int doWikiStartTag() throws IOException {
		WikiPage page = null;
		String versionString = "";
		final Engine engine = m_wikiContext.getEngine();

		//  Determine the page and the link.
		if (m_pageId != null) {
			page = ServicesRefs.getPageManager().getPageById(m_pageId);
		}
		if (m_pageName != null) {
			page = ServicesRefs.getPageManager().getPage(m_pageName);
		} else {
			page = m_wikiContext.getPage();
		}

		if (page == null) {
			// You can't call this on the page itself anyways.
			return SKIP_BODY;
		}

		//
		//  Determine the latest version, if the version attribute is "this".
		//
		if (m_version != null) {
			if ("this".equalsIgnoreCase(m_version)) {
				versionString = "version=" + page.getVersion();
			} else {
				versionString = "version=" + m_version;
			}
		}

		//
		//  Finally, print out the correct link, according to what user commanded.
		//
		JspWriter out = pageContext.getOut();
		String pageId = page.getId();
		switch (m_format) {
		case ANCHOR:
			out.print("<a href=\""
					+ m_wikiContext.getURL(ContextEnum.PAGE_EDIT.getRequestContext(), pageId, versionString)
					+ "\" accesskey=\"" + m_accesskey + "\" title=\"" + m_title + "\">");
			break;
		case URL:
			out.print(m_wikiContext.getURL(ContextEnum.PAGE_EDIT.getRequestContext(), pageId, versionString));
			break;
		}

		return EVAL_BODY_INCLUDE;
	}

}
