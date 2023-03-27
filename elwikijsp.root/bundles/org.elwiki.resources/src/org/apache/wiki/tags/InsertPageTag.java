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

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.elwiki_data.WikiPage;

/**
 * Renders WikiPage content. For InsertPage tag and the InsertPage plugin the
 * difference is that the tag will always render in the context of the page
 * which is referenced (i.e. a LeftMenu inserted on a JSP page with the
 * InsertPage tag will always render in the context of the actual URL, e.g.
 * Main.), whereas the InsertPage plugin always renders in local context. This
 * allows this like ReferringPagesPlugin to really refer to the Main page
 * instead of having to resort to any trickery.
 * <p>
 * This tag sets the "realPage" field of the WikiContext to point at the
 * inserted page, while the "page" will contain the actual page in which the
 * rendering is being made.
 * 
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>pageName - Page name to refer to. Default is the current page.
 * <li>pageId - Page Id to refer to. Default is the current page.
 * <li>mode - In which format to insert the page. Can be either "plain" or
 * "html".
 * </ul>
 *
 * @since 2.0
 */
public class InsertPageTag extends BaseWikiTag {

	private enum InsertMode {
		HTML, PLAIN
	}

	private static final long serialVersionUID = 1615720737028000289L;

	private static final Logger log = Logger.getLogger(InsertPageTag.class);

	protected String m_pageName = null;
	protected String m_pageId = null;
	private InsertMode m_mode = InsertMode.HTML;

	@Override
	public void initTag() {
		super.initTag();
		m_pageName = null;
		m_pageId = null;
		m_mode = InsertMode.HTML;
	}

	public void setPageName(String page) {
		m_pageName = page;
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

	public void setMode(String arg) {
		if ("plain".equalsIgnoreCase(arg)) {
			m_mode = InsertMode.PLAIN;
		} else {
			m_mode = InsertMode.HTML;
		}
	}

	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		Engine engine = wikiContext.getEngine();
		PageManager pageManager = engine.getManager(PageManager.class);
		RenderingManager renderingManager = engine.getManager(RenderingManager.class);
		WikiPage insertedPage;

		//
		//  NB: The page might not really exist if the user is currently
		//      creating it (i.e. it is not yet in the cache or providers), 
		//      AND we got the page from the wikiContext.
		//

		boolean isPushRequired = true;
		if (m_pageId != null) {
			insertedPage = pageManager.getPageById(m_pageId);
		} else if (m_pageName != null) {
			insertedPage = pageManager.getPage(m_pageName);
		} else {
			isPushRequired = false;
			insertedPage = wikiContext.getPage();
		}

		if (insertedPage != null) {
			log.debug("Inserting page " + insertedPage + ": \"" + insertedPage.getName() + "\" -- for "
					+ this.pageContext.getPage());
			try {
				JspWriter out = pageContext.getOut();
				if (isPushRequired)
					wikiContext.pushRealPage(insertedPage);

				String text = switch (m_mode) {
				case HTML -> renderingManager.getHTML(wikiContext, insertedPage);
				case PLAIN -> pageManager.getText(insertedPage, wikiContext.getPageVersion());
				};
				out.print(text);
			} finally {
				if (isPushRequired)
					wikiContext.popRealPage();
			}
		}

		return SKIP_BODY;
	}

}
