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
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.parser0.WikiDocument;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.util.TextUtil;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;

/**
 * Writes the author name of the current page, including a link to that page, if
 * that page exists.
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>format - Format, either "plain" or empty.
 * </ul>
 *
 * @since 2.0
 */
public class AuthorTag extends BaseWikiTag {

	private static final long serialVersionUID = -5347299857056429136L;

	public String m_format = "";

	public void setFormat(final String format) {
		m_format = format;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		final Engine engine = m_wikiContext.getEngine();
		final WikiPage page = m_wikiContext.getPage();
		String author = page.getAuthor();

		if (author != null && !author.isBlank()) {
			author = TextUtil.replaceEntities(author);

			if (ServicesRefs.getPageManager().pageExistsByName(author) && !("plain".equalsIgnoreCase(m_format))) {
				// FIXME: It's very boring to have to do this.  Slow, too.
				final RenderingManager mgr = ServicesRefs.getRenderingManager();
				final MarkupParser p = mgr.getParser(m_wikiContext, "[" + author + "|" + author + "]");
				final WikiDocument d = p.parse();
				author = mgr.getHTML(m_wikiContext, d);
			}

			pageContext.getOut().print(author);
		} else {
			pageContext.getOut().print(Preferences.getBundle(m_wikiContext, InternationalizationManager.CORE_BUNDLE)
					.getString("common.unknownauthor"));
		}

		return SKIP_BODY;
	}

}
