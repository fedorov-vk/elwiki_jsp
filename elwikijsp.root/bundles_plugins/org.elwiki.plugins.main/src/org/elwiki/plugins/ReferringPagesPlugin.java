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

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextEnum;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.TextUtil;
import org.elwiki.plugins.internal.AbstractReferralPlugin;
import org.elwiki.plugins.internal.PluginsActivator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Displays the pages referring to the current page.
 *
 * Parameters:
 * <ul>
 * <li><b>id</b> - Which page to get the table of contents from.</li>
 * <li><b>max</b> - How many items to show.</li>
 * <li><b>extras</b> - How to announce extras.</li>
 * </ul>
 *
 * From AbstractReferralPlugin:
 * <ul>
 * <li><b>separator</b> - How to separate generated links; default is a wikitext line break, producing a
 * vertical list.</li>
 * <li><b>maxwidth</b> - maximum width, in chars, of generated links.</li>
 * </ul>
 */
public class ReferringPagesPlugin extends AbstractReferralPlugin {

	private static final Logger log = Logger.getLogger(ReferringPagesPlugin.class);

	/** Parameter name for choosing the page by ID. Value is <tt>{@value}</tt>. */
	public static final String PARAM_PAGE_ID = "id";

	/** Parameter name for setting the maximum items to show. Value is <tt>{@value}</tt>. */
	public static final String PARAM_MAX = "max";

	/**
	 * The name of the resource for the text output when the maximum number of elements is overruled. Value is
	 * <tt>{@value}</tt>.
	 */
	public static final String PARAM_EXTRAS = "extras";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		super.initialize(context, params);

		try {
			//:FVK: или не я? ReferenceManager refmgr = Engine.getReferenceManager();
			ResourceBundle rb = PluginsActivator.getBundle(Preferences.getLocale(context));
			StringBuilder result = new StringBuilder(256);

			WikiPage page;
			String pageName;

			/* Parse parameters.
			 */
			String pageId = params.get(PARAM_PAGE_ID);
			page = (pageId == null) ? context.getPage() : pageManager.getPageById(pageId);
			if (page == null) {
				return "";
			}
			pageId = page.getId();
			pageName = page.getName();

			int items = TextUtil.parseIntParameter(params.get(PARAM_MAX), ALL_ITEMS);

			String extras = TextUtil.replaceEntities(params.get(PARAM_EXTRAS));
			if (extras == null) {
				extras = rb.getString("referringpagesplugin.more");
			}

			log.debug("Fetching referring pages for " + page.getName() + " with a max of " + items);

			/* Do the actual work.
			 */
			List<PageReference> inReferences;
			inReferences = pageManager.getPageReferrers(pageId);
			List<WikiPage> referrers = new ArrayList<>();
			for (PageReference pageReference : inReferences) {
				String pageId1 = pageReference.getWikipage().getId();
				WikiPage refPage = pageManager.getPageById(pageId1);
				if (refPage != null) {
					referrers.add(refPage);
				}
			}

			String wikitext;

			if (referrers != null && referrers.size() > 0) {
				//TODO: :FVK: rewrite code filterAndSortCollection() for WikiPages, PageReference... 
				// old call:: links = filterAndSortCollection(links);
				wikitext = wikitizePageCollection(referrers, m_separator, items);

				result.append(makeHTML(context, wikitext));

				if (items < referrers.size() && items > 0) {
					Object[] args = { "" + (referrers.size() - items) };
					extras = MessageFormat.format(extras, args);

					result.append("<br />").append("<a class='morelink' href='")
							.append(context.getURL(ContextEnum.PAGE_INFO.getRequestContext(), page.getName()))
							.append("' ").append(">").append(extras).append("</a><br />");
				}
			}

			//
			// If nothing was left after filtering or during search
			//
			if (referrers == null || referrers.size() == 0) {
				wikitext = rb.getString("referringpagesplugin.nobody");

				result.append(makeHTML(context, wikitext));
			} else {
				if (m_show.equals(PARAM_SHOW_VALUE_COUNT)) {
					result = new StringBuilder();
					result.append(referrers.size());
					if (m_lastModified) {
						result.append(" (").append(m_dateFormat.format(m_dateLastModified)).append(")");
					}
				}
			}

			return result.toString();
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

}
