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

import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.ui.TemplateManager;

/**
 * This tag is used to include any programmatic includes into the output stream.
 * Actually, what it does is that it simply emits a tiny marker into the stream,
 * and then a {@link org.elwiki.web.jsp.JspServletFilter} take care of the actual inclusion.
 *
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>type - Name of the variable. Required.
 * </ul>
 */
public class IncludeResourcesTag extends BaseWikiTag {

	private static final long serialVersionUID = -1551412771964050050L;

	private String m_type;

	public void initTag() {
		super.initTag();
		m_type = null;
	}

	public void setType(String type) {
		m_type = type;
	}

	public int doWikiStartTag() throws ProviderException, IOException, JspTagException {
		String marker = TemplateManager.getMarker(getWikiContext(), m_type);

		pageContext.getOut().println(marker);

		return SKIP_BODY;
	}

}
