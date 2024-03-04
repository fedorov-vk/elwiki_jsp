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

import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.rss.IFeed;
import org.apache.wiki.util.TextUtil;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki_data.WikiPage;

/**
 * Outputs links to all the site feeds and APIs this Wiki/blog supports.
 *
 * @since 2.2
 */
public class FeedDiscoveryTag extends BaseWikiTag {

	private static final long serialVersionUID = 6827322800489528947L;

	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		final Engine engine = wikiContext.getEngine();
		final IWikiConfiguration config = wikiContext.getConfiguration();
		final WikiPage page = wikiContext.getPage();

		final String encodedName = engine.encodeName(page.getName());
		final String rssURL = engine.getGlobalRSSURL();
		final String rssFeedURL = engine.getURL(ContextEnum.PAGE_NONE.getRequestContext(), "rss.jsp",
				"page=" + encodedName + "&amp;mode=wiki");

		if (rssURL != null) {
			String siteName = IFeed.getSiteName(wikiContext);
			siteName = TextUtil.replaceEntities(siteName);

			pageContext.getOut().print(
					"<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS wiki feed for the entire site.\" href=\""
							+ rssURL + "\" />\n");
			pageContext.getOut()
					.print("<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS wiki feed for page "
							+ siteName + ".\" href=\"" + rssFeedURL + "\" />\n");

			// TODO: Enable this
			/*
			pageContext.getOut().print("<link rel=\"service.post\" type=\"application/atom+xml\" title=\""+
			                           siteName+"\" href=\""+atomPostURL+"\" />\n");
			*/
			// FIXME: This does not work always, as plugins are not initialized until the first fetch
			if (page.isWebLog()) {
				final String blogFeedURL = engine.getURL(ContextEnum.PAGE_NONE.getRequestContext(), "rss.jsp",
						"page=" + encodedName);
				final String atomFeedURL = engine.getURL(ContextEnum.PAGE_NONE.getRequestContext(), "rss.jsp",
						"page=" + encodedName + "&amp;type=atom");

				pageContext.getOut()
						.print("<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS feed for weblog "
								+ siteName + ".\" href=\"" + blogFeedURL + "\" />\n");

				pageContext.getOut().print(
						"<link rel=\"service.feed\" type=\"application/atom+xml\" title=\"Atom 1.0 weblog feed for "
								+ siteName + "\" href=\"" + atomFeedURL + "\" />\n");
			}
		}

		return SKIP_BODY;
	}

}
