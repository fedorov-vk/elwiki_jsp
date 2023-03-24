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
package org.elwiki.core.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.util.TextUtil;
import org.elwiki.api.plugin.WikiPlugin;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;

/**
 * Displays the pages referred from the current page.
 *
 * <p>
 * Parameters of plugin:
 * </p>
 * <ul>
 * <li><b>id</b> - Identifier of the root page. Default page is calling page of context.
 * <li><b>depth</b> - How many levels of pages to be parsed. (allowed from 1 to 8)
 * <li><b>include</b> - Include only these pages. (eg. include='UC.*|BP.*')
 * <li><b>exclude</b> - Exclude with this pattern. (eg. exclude='LeftMenu')
 * <li><b>format</b> - The 'sort' format sorts the page's target links in alphabetical order.
 * </ul>
 */
public class ReferredPagesPlugin implements WikiPlugin {

	private static final Logger log = Logger.getLogger(ReferredPagesPlugin.class);

	private Engine m_engine;
	private PageManager pageManager;

	private int m_depth;
	private Pattern m_includePattern;
	private Pattern m_excludePattern;
	private boolean m_formatSort = false;

	private PatternMatcher m_matcher = new Perl5Matcher();
	private StringBuffer m_result = new StringBuffer(1024);

	/** The parameter name for the root page ID to start from. Value is <tt>{@value}</tt>. */
	public static final String PARAM_PAGE_ID = "id";

	/** The parameter name for the depth. Value is <tt>{@value}</tt>. */
	public static final String PARAM_DEPTH = "depth";

	/** The parameter name for the included pages. Value is <tt>{@value}</tt>. */
	public static final String PARAM_INCLUDE = "include";

	/** The parameter name for the excluded pages. Value is <tt>{@value}</tt>. */
	public static final String PARAM_EXCLUDE = "exclude";

	/** The parameter name for the format. Value is <tt>{@value}</tt>. */
	public static final String PARAM_FORMAT = "format";

	/** The minimum depth. Value is <tt>{@value}</tt>. */
	public static final int MIN_DEPTH = 1;

	/** The maximum depth. Value is <tt>{@value}</tt>. */
	public static final int MAX_DEPTH = 8;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		try {
			m_engine = context.getEngine();
			pageManager = m_engine.getManager(PageManager.class);

			WikiPage rootPage;
			String rootPageName;

			/* Parse parameters.
			 */
			String rootPageId = params.get(PARAM_PAGE_ID);
			rootPage = (rootPageId == null) ? context.getPage() : pageManager.getPageById(rootPageId);
			if (rootPage == null) {
				return ""; //TODO: there should be an information string.
			}
			rootPageId = rootPage.getId();
			rootPageName = rootPage.getName();

			String format = params.get(PARAM_FORMAT);
			if (format == null) {
				format = "";
			} else {
				if (format.contains("sort")) {
					m_formatSort = true;
				}
			}

			m_depth = TextUtil.parseIntParameter(params.get(PARAM_DEPTH), MIN_DEPTH);
			m_depth = Math.max(m_depth, MIN_DEPTH);
			m_depth = Math.min(m_depth, MAX_DEPTH);

			String includePattern = params.get(PARAM_INCLUDE);
			if (includePattern == null)
				includePattern = ".*";

			String excludePattern = params.get(PARAM_EXCLUDE);
			if (excludePattern == null)
				excludePattern = "^$";

			//@formatter:off
			log.debug("Fetching referred pages for " + rootPageId +
        			" with a depth of " + m_depth +
        			" with include pattern of " + includePattern +
        			" with exclude pattern of " + excludePattern );
			//@formatter:on


			/* Do the actual work.
			 */
			String href = context.getViewURL(rootPageId);
			String tooltip = "ReferredPagesPlugin: depth[" + m_depth + "] include[" + includePattern + "] exclude["
					+ excludePattern + "] format[" + (m_formatSort ? " sort" : "") + "]";

			m_result.append("<div class=\"ReferredPagesPlugin\">\n");
			m_result.append("<a class=\"wikipage\" href=\"" + href + "\" title=\"" + TextUtil.replaceEntities(tooltip)
					+ "\">" + TextUtil.replaceEntities(rootPageName) + "</a>\n");

			// pre compile all needed patterns
			// glob compiler :  * is 0..n instance of any char  -- more convenient as input
			// perl5 compiler : .* is 0..n instances of any char -- more powerful
			//PatternCompiler g_compiler = new GlobCompiler();
			PatternCompiler compiler = new Perl5Compiler();

			try {
				m_includePattern = compiler.compile(includePattern);
				m_excludePattern = compiler.compile(excludePattern);
			} catch (MalformedPatternException e) {
				if (m_includePattern == null) {
					throw new PluginException("Illegal include pattern detected.");
				} else if (m_excludePattern == null) {
					throw new PluginException("Illegal exclude pattern detected.");
				} else {
					throw new PluginException("Illegal internal pattern detected.");
				}
			}

			// go get all referred links
			getReferredPages(context, rootPage, 0);

			// close and finish
			m_result.append("</div>\n");

			return m_result.toString();
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

	/**
	 * Retrieves a list of all referred pages. Is called recursively depending on the depth parameter.
	 * 
	 * @throws ProviderException TODO
	 */
	private void getReferredPages(WikiContext context, WikiPage page, int depth) throws ProviderException {
		if (depth >= m_depth) {
			return; // end of recursion
		}

		handleLinks(context, ++depth, page);
	}

	private void handleLinks(WikiContext context, int depth, WikiPage page) throws ProviderException {
		boolean isUL = false;
		List<WikiPage> allLinks = new ArrayList<>();

		for (PageReference pageReference : page.getPageReferences()) {
			String pageId = pageReference.getPageId();
			WikiPage refPage = pageManager.getPageById(pageId);
			if (refPage != null) {
				allLinks.add(refPage);
			}
		}

		if (m_formatSort) {
			Collections.sort(allLinks, WikiPage::compareTo);
		}

		for (WikiPage refPage : allLinks) {
			String pageName = refPage.getName();
			String pageId = refPage.getId();
			if (m_matcher.matches(pageName, m_excludePattern)) {
				continue;
			}
			if (!m_matcher.matches(pageName, m_includePattern)) {
				continue;
			}

			if (!isUL) {
				isUL = true;
				m_result.append("<ul>\n");
			}

			String href = context.getViewURL(pageId);
			m_result.append("<li><a class=\"wikipage\" href=\"" + href + "\">" + pageName + "</a>\n");
			getReferredPages(context, refPage, depth);
			m_result.append("\n</li>\n");
		}

		if (isUL) {
			m_result.append("</ul>\n");
		}
	}

}