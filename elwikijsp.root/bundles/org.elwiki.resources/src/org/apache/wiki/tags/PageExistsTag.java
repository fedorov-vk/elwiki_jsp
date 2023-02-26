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

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.elwiki_data.WikiPage;

/**
 * Includes the body in case the set page does exist.
 *
 * @since 2.0
 */
public class PageExistsTag extends BaseWikiTag {

	private static final long serialVersionUID = -4687069201552542591L;

	private String m_pageName;
	private String m_pageId;

	@Override
	public void initTag() {
		super.initTag();
		m_pageName = null;
		m_pageId = null;
	}

	public void setPageName(String name) {
		m_pageName = name;
	}

	public String getPageName() {
		return m_pageName;
	}

	public void setPageId(String pageId) {
		m_pageId = pageId;
	}

	public String getPageId() {
		return m_pageId;
	}

	public int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
        Engine engine = wikiContext.getEngine();
        PageManager pageManager = engine.getManager(PageManager.class);

		WikiPage page;

		if (m_pageId != null) {
			page = pageManager.getPageById(m_pageId);
		} else if (m_pageName != null) {
			page = pageManager.getPage(m_pageName);
		} else {
			page = wikiContext.getPage();
		}

		if (page != null && pageManager.pageExistsById(page.getId())) {
			return EVAL_BODY_INCLUDE;
		}

		return SKIP_BODY;
	}

}