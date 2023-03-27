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
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.pages0.PageManager;
import org.elwiki_data.WikiPage;

/**
 * Writes a diff link. Body of the link becomes the link text.
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>pageName - Page name to refer to. Default is the current page.</li>
 * <li>version - The older of these versions. May be an integer to signify a version number, or the
 * text "latest" to signify the latest version. If not specified, will default to "latest". May also
 * be "previous" to signify a version prior to this particular version.</li>
 * <li>newVersion - The newer of these versions. Can also be "latest", or "previous". Defaults to
 * "latest".</li>
 * </ul>
 *
 * If the page does not exist, this tag will fail silently, and not evaluate its body contents.
 *
 * @since 2.0
 */
public class DiffLinkTag extends BaseWikiLinkTag {

	private static final long serialVersionUID = 4900799162029349581L;

	public static final String VER_LATEST = "latest";
	public static final String VER_PREVIOUS = "previous";
	public static final String VER_CURRENT = "current";

	private String m_version = VER_LATEST;
	private String m_newVersion = VER_LATEST;

	@Override
	public void initTag() {
		super.initTag();
		m_version = m_newVersion = VER_LATEST;
	}

	public String getVersion() {
		return m_version;
	}

	public void setVersion(String arg) {
		m_version = arg;
	}

	public String getNewVersion() {
		return m_newVersion;
	}

	public void setNewVersion(String arg) {
		m_newVersion = arg;
	}

	@Override
	public int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		PageManager pageManager = wikiContext.getEngine().getManager(PageManager.class);
		String pageId = getPageId();
		WikiPage wikiPage = null;

		if (pageId == null) {
			wikiPage = wikiContext.getPage();
			if (wikiPage != null) {
				pageId = wikiPage.getId();
			} else {
				return SKIP_BODY;
			}
		}

		JspWriter out = pageContext.getOut();

		int r1;
		int r2;

		//  In case the page does not exist, we fail silently.
		if (pageManager.pageExistsById(pageId) == false) {
			return SKIP_BODY;
		}
		if(wikiPage == null ) {
			wikiPage = pageManager.getPageById(pageId);
		}

		if (VER_LATEST.equals(getVersion())) {
			r1 = wikiPage.getLastVersion();
			if (r1 < 1 ) {
				// This may occur if page has not content, no version.
				return SKIP_BODY;
			}
		} else if (VER_PREVIOUS.equals(getVersion())) {
			r1 = wikiPage.getLastVersion() - 1;
			r1 = Math.max(r1, 1);
		} else if (VER_CURRENT.equals(getVersion())) {
			r1 = wikiPage.getLastVersion();
		} else {
			r1 = Integer.parseInt(getVersion());
		}

		if (VER_LATEST.equals(getNewVersion())) {
			r2 = wikiPage.getLastVersion();
		} else if (VER_PREVIOUS.equals(getNewVersion())) {
			r2 = wikiPage.getLastVersion() - 1;
			r2 = Math.max(r2, 1);
		} else if (VER_CURRENT.equals(getNewVersion())) {
			r2 = wikiPage.getLastVersion();
		} else {
			r2 = Integer.parseInt(getNewVersion());
		}

		String url = wikiContext.getURL(ContextEnum.PAGE_DIFF.getRequestContext(), getPageId(),
				"r1=" + r1 + "&amp;r2=" + r2);
		switch (m_format) {
		case ANCHOR:
			out.print("<a href=\"" + url + "\">");
			break;
		case URL:
			out.print(url);
			break;
		}

		return EVAL_BODY_INCLUDE;
	}

}
