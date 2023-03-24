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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.TextUtil;
import org.elwiki.plugins.internal.AbstractReferralPlugin;
import org.elwiki.plugins.internal.PluginsActivator;
import org.elwiki_data.WikiPage;

/**
 * <p>
 * Lists all pages containing links to Undefined Pages (pages containing dead links).
 * </p>
 *
 * An original idea from Gregor Hagedorn.
 *
 * @since 2.10.0
 */
public class ReferringUndefinedPagesPlugin extends AbstractReferralPlugin {

	/** Parameter name for setting the maximum items to show. Value is <tt>{@value}</tt>. */
	public static final String PARAM_MAX = "max";

	/**
	 * Parameter name for setting the text to show when the maximum items is overruled. Value is <tt>{@value}</tt>.
	 */
	public static final String PARAM_EXTRAS = "extras";

	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		super.initialize(context, params);

		try {
			Engine engine = context.getEngine();
			PageManager pageManager = engine.getManager(PageManager.class);

			/* Parse parameters.
			 */
			int items = TextUtil.parseIntParameter(params.get(PARAM_MAX), ALL_ITEMS);
			String extras = params.get(PARAM_EXTRAS);
			if (extras == null) {
				extras = PluginsActivator.getMessage("referringundefinedpagesplugin.more",
						Preferences.getLocale(context));
			}

			Collection<WikiPage> referrers = pageManager.getReferrersToUncreatedPages();

			//:FVK: result = super.filterAndSortCollection( result );
			String wikitext = wikitizePageCollection(referrers, m_separator, items);

			StringBuilder resultHTML = new StringBuilder();
			resultHTML.append(makeHTML(context, wikitext));

			// add the more.... text
			if (items < referrers.size() && items > 0) {
				Object[] args = { "" + (referrers.size() - items) };
				extras = MessageFormat.format(extras, args);

				resultHTML.append("<br/>" + extras + "<br/>");
			}
			return resultHTML.toString();
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

}
