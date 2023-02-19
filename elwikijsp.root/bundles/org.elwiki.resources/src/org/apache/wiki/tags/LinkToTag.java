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
import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 * Writes a link to a Wiki page. Body of the link becomes the actual text. The link is written
 * regardless to whether the page exists or not.
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>pageName - Page name to refer to. Default is the current page.
 * <li>pageId - Page Id to refer to. Default is the current page.
 * <li>format - either "anchor" or "url" to output either an &lt;A&gt... or just the HREF part
 * of one.
 * <li>template - Which template should we link to.
 * <li>title - Is used in page actions to display hover text (tooltip)
 * <li>accesskey - Set an accesskey (ALT+[Char])
 * </ul>
 *
 * @since 2.0
 */
public class LinkToTag extends BaseWikiLinkTag {

	private static final long serialVersionUID = 4569694714427032140L;

	private String m_version = null;
	public String m_title = "";
	public String m_accesskey = "";

	@Override
	public void initTag() {
		super.initTag();
		m_version = null;
	}

	public String getVersion() {
		return m_version;
	}

	public void setVersion(final String arg) {
		m_version = arg;
	}

	public void setTitle(final String title) {
		m_title = title;
	}

	public void setAccesskey(final String access) {
		m_accesskey = access;
	}

	@Override
	public int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		String pageName = m_pageName;
		String pageId = m_pageId;
		boolean isattachment = false;

		if (m_pageId != null) {
			WikiPage page = ServicesRefs.getPageManager().getPageById(m_pageId);
			if (page != null) {
				pageName = m_pageName = page.getName();
			}
		} else if (m_pageName != null) {
			WikiPage page = ServicesRefs.getPageManager().getPage(m_pageName);
			if (page != null) {
				pageId = m_pageId = page.getId();
			}
		}

		/*TODO: :FVK:. разобраться, (рассмотреть, модифицировать весь код ниже...), 1я проблема: (p instanceof PageAttachment).
		 * так же из-за закомментированого фрагмента - проблема для создания ссылки по pageId - не создается, для несуществующей страницы wiki.
		if (m_pageName == null) {
			final WikiPage p = m_wikiContext.getPage();

			if (p != null) {
				pageName = p.getName();
				pageId = p.getId();

				isattachment = p instanceof PageAttachment;
			} else {
				return SKIP_BODY;
			}
		}
		*/

		final JspWriter out = pageContext.getOut();
		final String url;
		final String linkclass;
		String forceDownload = "";

		if (isattachment) {//TODO: разобраться с типом "присоединение"...
			url = m_wikiContext.getURL(ContextEnum.ATTACHMENT_DOGET.getRequestContext(), pageName,
					(getVersion() != null) ? "version=" + getVersion() : null);
			linkclass = "attachment";

			if (ServicesRefs.getAttachmentManager().forceDownload(pageName)) {
				forceDownload = "download ";
			}

		} else {
			final StringBuilder params = new StringBuilder();
			if (getVersion() != null) {
				params.append("version=").append(getVersion());
			}
			if (getTemplate() != null) {
				params.append(params.length() > 0 ? "&amp;" : "").append("shape=").append(getTemplate());
			}

			url = m_wikiContext.getURL(ContextEnum.PAGE_VIEW.getRequestContext(), pageId, params.toString());
			linkclass = "wikipage";
		}

		switch (m_format) {
		case ANCHOR:
			out.print("<a class=\"" + linkclass + "\" href=\"" + url + "\" accesskey=\"" + m_accesskey + "\" title=\""
					+ m_title + "\" " + forceDownload + ">");
			break;
		case URL:
			out.print(url);
			break;
		}

		return EVAL_BODY_INCLUDE;
	}

}
