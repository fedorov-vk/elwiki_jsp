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

import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 * Writes a link to a parent of a Wiki page.
 *
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
public class LinkToParentTag extends LinkToTag {

	private static final long serialVersionUID = -3221811690118879748L;

	public int doWikiStartTag() throws IOException {
		final WikiPage p = m_wikiContext.getPage();

		//TODO: разобраться, (заменить код?) - //p instanceof PageAttachment// :FVK:.
		//  We just simply set the page to be our parent page and call the superclass.
		if (p instanceof PageAttachment) {
			// :FVK: было -- page = ((PageAttachment)p).getParentName()
			PageAttachment pa = (PageAttachment) p;
			WikiPage page = pa.getWikipage();
			setPageName(page.getName());
		} else {
			final String name = p.getName();
			final int entrystart = name.indexOf("_blogentry_");
			if (entrystart != -1) {
				setPageName(name.substring(0, entrystart));
			}

			final int commentstart = name.indexOf("_comments_");
			if (commentstart != -1) {
				setPageName(name.substring(0, commentstart));
			}
		}

		return super.doWikiStartTag();
	}

}
