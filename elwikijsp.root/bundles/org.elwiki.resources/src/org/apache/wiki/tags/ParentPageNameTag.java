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
import org.apache.wiki.render0.RenderingManager;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 * Returns the parent of the currently requested page. Weblog entries are
 * recognized as subpages of the weblog page.
 *
 * @since 2.0
 */
@Deprecated // :FVK: this tag is no longer used.
public class ParentPageNameTag extends BaseWikiTag {

	private static final long serialVersionUID = -5237880586223850672L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		final WikiPage page = wikiContext.getPage();

		if (page != null) {
			if (page instanceof PageAttachment) {
				//:FVK: pageContext.getOut().print( WikiEngine.getRenderingManager().beautifyTitle( ((PageAttachment)page).getParentName()) );
				// TODO: release ...
			} else {
				String name = page.getName();
				final int entrystart = name.indexOf("_blogentry_");
				if (entrystart != -1) {
					name = name.substring(0, entrystart);
				}

				final int commentstart = name.indexOf("_comments_");
				if (commentstart != -1) {
					name = name.substring(0, commentstart);
				}

				pageContext.getOut().print(name);
			}
		}

		return SKIP_BODY;
	}

}
