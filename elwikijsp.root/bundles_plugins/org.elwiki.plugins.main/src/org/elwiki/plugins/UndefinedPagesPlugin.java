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

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.elwiki.plugins.internal.AbstractReferralPlugin;
import org.elwiki_data.UnknownPage;

/**
 * Plugin that enumerates the pages in the wiki that have not yet been defined.
 * 
 * Parameters (from AbstractReferralPlugin):
 * <ul>
 * <li><b>separator</b> - how to separate generated links; default is a wikitext line break,
 * producing a vertical list</li>
 * <li><b> maxwidth</b> - maximum width, in chars, of generated links.</li>
 * </ul>
 */
public class UndefinedPagesPlugin extends AbstractReferralPlugin {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		super.initialize(context, params);
		try {
			Collection<UnknownPage> unknownPages = super.pageManager.getUnknownPages();
			Collection<String> links = unknownPages.stream().map(UnknownPage::getPageName).collect(Collectors.toList());

			links = filterAndSortCollection(links);

			if (m_lastModified) {
				throw new PluginException(
						"parameter " + PARAM_LASTMODIFIED + " is not valid for the UndefinedPagesPlugin");
			}

			String wikitext;
			if (m_show.equals(PARAM_SHOW_VALUE_COUNT)) {
				wikitext = "" + links.size();
			} else {
				wikitext = wikitizeStringCollection(links, m_separator, ALL_ITEMS);
			}

			return makeHTML(context, wikitext);
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

}
