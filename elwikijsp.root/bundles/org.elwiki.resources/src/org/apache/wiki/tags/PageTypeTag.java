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
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 * Includes the body, if the current page is of proper type.
 * <p>
 * <b>Attributes</b>
 * <ul>
 * <li>type - either "page", "attachment" or "weblogentry"
 * </ul>
 *
 * @since 2.0
 */
@Deprecated // :FVK: this tag is no longer used. (the use of "weblogentry" is not noticed.)
public class PageTypeTag extends BaseWikiTag {

	private static final long serialVersionUID = 1594079364766711229L;

	private String m_type;

	public void initTag() {
		super.initTag();
		m_type = null;
	}

	public void setType(final String arg) {
		m_type = arg.toLowerCase();
	}

	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		final WikiPage page = getWikiContext().getPage();
		if (page != null) {
			if (m_type.equals("attachment") && page instanceof PageAttachment) {
				return EVAL_BODY_INCLUDE;
			}
			if (m_type.equals("page") && (page instanceof WikiPage)) {
				return EVAL_BODY_INCLUDE;
			}
			if (m_type.equals("weblogentry") && !(page instanceof PageAttachment)
					&& page.getName().contains("_blogentry_")) {
				return EVAL_BODY_INCLUDE;
			}
		}

		return SKIP_BODY;
	}

}