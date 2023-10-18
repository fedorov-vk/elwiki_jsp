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

import org.apache.wiki.api.core.WikiContext;

/**
 * Root class for different internal wiki links. Cannot be used directly, but
 * provides basic stuff for other classes.
 * <P>
 * Extend from this class if you need the following attributes.
 *
 * <p>
 * <b>Attributes</b>
 * </p>
 * <u;>
 * <li>page - Page name to refer to. Default is the current page.
 * <li>format - Either "url" or "anchor". If "url", will provide just the URL
 * for the link. If "anchor", will output proper HTML (&lt;a&gt; href="...).
 * </ul>
 */
public abstract class BaseWikiLinkTag extends BaseWikiTag {

	private static final long serialVersionUID = 4130732879352134867L;

	enum LinkFormat {ANCHOR, URL};

	private String m_pageId;
	protected String m_pageName;
	protected LinkFormat m_format = LinkFormat.ANCHOR;
	protected String m_template;

	public void initTag() {
		super.initTag();
	}

	public void setPageName(String page) {
		m_pageName = page;
	}

	public String getPageName() {
		if (m_pageName == null) {
			WikiContext ctx = getWikiContext();
			if (ctx != null)
				return ctx.getPageName();
		}
		return m_pageName;
	}

	public void setPageId(String pageId) {
		m_pageId = pageId;
	}

	public String getPageId() {
		if (m_pageId == null) {
			WikiContext ctx = getWikiContext();
			if (ctx != null)
				return ctx.getPageId();
		}
		return m_pageId;
	}

	public String getTemplate() {
		return m_template;
	}

	public void setTemplate(String arg) {
		m_template = arg;
	}

	public void setFormat(String mode) {
		if ("url".equalsIgnoreCase(mode)) {
			m_format = LinkFormat.URL;
		} else {
			m_format = LinkFormat.ANCHOR;
		}
	}

	public int doEndTag() {
		try {
			if (m_format == LinkFormat.ANCHOR) {
				pageContext.getOut().print("</a>");
			}
		} catch (IOException e) {
			// FIXME: Should do something?
		}

		return EVAL_PAGE;
	}
}
