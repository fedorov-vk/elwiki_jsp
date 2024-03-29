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
package org.elwiki.plugins;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.util.TextUtil;
import org.elwiki.plugins.internal.AbstractReferralPlugin;
import org.elwiki_data.UnknownPage;
import org.elwiki_data.WikiPage;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Plugin for displaying pages that are not linked to in other pages. Uses the Id2NamePage.
 * <p>
 * Parameters (from AbstractReferralPlugin):
 * <ul>
 * <li><b>separator</b> - how to separate generated links; default is a wikitext line break,
 * producing a vertical list</li>
 * <li><b> maxwidth</b> - maximum width, in chars, of generated links.</li>
 * </ul>
 */
public class UnusedPagesPlugin extends AbstractReferralPlugin {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		super.initialize(context, params);

		try {
			Collection<WikiPage> unreferencedPages = super.pageManager.getUnreferencedPages();
			//:FVK: Collection<String> links = unreferencedPages.stream().map(WikiPage::getName).collect(Collectors.toList());
			//:FVK: links = filterAndSortCollection(links);

			unreferencedPages.removeIf(page -> page.getId().startsWith("w")); // :FVK: workaround.

			String wikitext;
			if (m_show.equals(PARAM_SHOW_VALUE_COUNT)) {
				wikitext = "" + unreferencedPages.size();
				if (m_lastModified && unreferencedPages.size() != 0) {
					wikitext = unreferencedPages.size() + " (" + m_dateFormat.format(m_dateLastModified) + ")";
				}
			} else {
				wikitext = wikitizePageCollection(unreferencedPages, m_separator, ALL_ITEMS);
			}
			return makeHTML(context, wikitext);
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

}
