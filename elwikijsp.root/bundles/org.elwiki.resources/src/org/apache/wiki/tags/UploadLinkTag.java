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

import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * Writes a link to the upload page. Body of the link becomes the actual text.
 * The link is written regardless to whether the page exists or not.
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>pageName - Page name to refer to. Default is the current page.
 * <li>format - either "anchor" or "url" to output either an &lt;A&gt;... or
 * just the HREF part of one.
 * </ul>
 *
 * @since 2.0
 */
public class UploadLinkTag extends BaseWikiLinkTag {

	private static final long serialVersionUID = 593568457874198342L;

	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		String pageName = m_pageName;
		if (m_pageName == null) {
			if (wikiContext.getPage() != null) {
				pageName = wikiContext.getPage().getName();
			} else {
				return SKIP_BODY;
			}
		}

		final JspWriter out = pageContext.getOut();
		final String url = wikiContext.getURL(ContextEnum.ATTACHMENT_UPLOAD.getRequestContext(), pageName);
		switch (m_format) {
		case ANCHOR:
			out.print("<a target=\"_new\" class=\"uploadlink\" href=\"" + url + "\">");
			break;
		case URL:
			out.print(url);
			break;
		}

		return EVAL_BODY_INCLUDE;
	}

}
