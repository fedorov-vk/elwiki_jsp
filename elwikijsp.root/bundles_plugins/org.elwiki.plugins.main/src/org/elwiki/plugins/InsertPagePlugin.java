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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.util.TextUtil;
import org.elwiki.api.plugin.InitializablePlugin;
import org.elwiki.api.plugin.WikiPlugin;
import org.elwiki.permissions.PermissionFactory;
import org.elwiki.plugins.internal.PluginsActivator;
import org.elwiki_data.WikiPage;

/**
 * Inserts page contents. Muchos thanks to Scott Hurlbert for the initial code.
 *
 * <p>
 * Parameters :
 * </p>
 * <ul>
 * <li><b>page</b> - the name of the page to be inserted</li>
 * <li><b>style</b> - the style to use</li>
 * <li><b>maxlength</b> - the maximum length of the page to be inserted (page contents)</li>
 * <li><b>class</b> - the class to use</li>
 * <li><b>section</b> - the section of the page that has to be inserted (separated by "----"</li>
 * <li><b>default</b> - the text to insert if the requested page does not exist</li>
 * </ul>
 */
public class InsertPagePlugin implements WikiPlugin, InitializablePlugin {

	/** Parameter name for setting the page ID. Value is <tt>{@value}</tt>. */
	public static final String PARAM_PAGEID = "id";

	/** Parameter name for setting the style. Value is <tt>{@value}</tt>. */
	public static final String PARAM_STYLE = "style";

	/** Parameter name for setting the maxlength. Value is <tt>{@value}</tt>. */
	public static final String PARAM_MAXLENGTH = "maxlength";

	/** Parameter name for setting the class. Value is <tt>{@value}</tt>. */
	public static final String PARAM_CLASS = "class";

	/** Parameter name for setting the show option. Value is <tt>{@value}</tt>. */
	public static final String PARAM_SHOW = "show";

	/** Parameter name for setting the section. Value is <tt>{@value}</tt>. */
	public static final String PARAM_SECTION = "section";

	/** Parameter name for setting the default. Value is <tt>{@value}</tt>. */
	public static final String PARAM_DEFAULT = "default";

	private static final String DEFAULT_STYLE = "";

	private static final String ONCE_COOKIE = "JSPWiki.Once.";

	/**
	 * This attribute is stashed in the WikiContext to make sure that we don't have circular references.
	 */
	public static final String ATTR_RECURSE = "org.apache.wiki.plugin.InsertPage.recurseCheck";

	private AuthorizationManager authorizationManager;

	private PageManager pageManager;

	private RenderingManager renderingManager;

	@Override
	public void initialize(Engine engine) throws PluginException {
		this.pageManager = engine.getManager(PageManager.class);
		this.authorizationManager = engine.getManager(AuthorizationManager.class);
		this.renderingManager = engine.getManager(RenderingManager.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		StringBuilder res = new StringBuilder();

		/* Parse parameters.
		 */
		String clazz = params.get(PARAM_CLASS);
		String includedPageId = params.get(PARAM_PAGEID);
		String style = params.get(PARAM_STYLE);
		boolean showOnce = "once".equals(params.get(PARAM_SHOW));
		String defaultstr = params.get(PARAM_DEFAULT);
		int section = TextUtil.parseIntParameter(params.get(PARAM_SECTION), -1);
		int maxlen = TextUtil.parseIntParameter(params.get(PARAM_MAXLENGTH), -1);

		ResourceBundle rb = PluginsActivator.getBundle(Preferences.getLocale(context));

		if (style == null) {
			style = DEFAULT_STYLE;
		}

		if (maxlen == -1) {
			maxlen = Integer.MAX_VALUE;
		}

		if (includedPageId != null) {
			WikiPage page;
			try {
				page = pageManager.getPageById(includedPageId);
			} catch (ProviderException e) {
				res.append("<span class=\"error\">Page could not be found by the page provider.</span>");
				return res.toString();
			}

			if (page != null) {
				//  Check for recursivity
				List<String> previousIncludes = context.getVariable(ATTR_RECURSE);

				if (previousIncludes != null) {
					if (previousIncludes.contains(page.getName())) {
						return "<span class=\"error\">Error: Circular reference - you can't include a page in itself!</span>";
					}
				} else {
					previousIncludes = new ArrayList<>();
				}

				// Check for permissions
				if (!authorizationManager.checkPermission(context.getWikiSession(),
						PermissionFactory.getPagePermission(page, "view"))) {
					res.append("<span class=\"error\">You do not have permission to view this included page.</span>");
					return res.toString();
				}

				// Show Once
				// Check for page-cookie, only include page if cookie is not yet set
				String cookieName = "";

				if (showOnce) {
					cookieName = ONCE_COOKIE + TextUtil.urlEncodeUTF8(page.getName()).replaceAll("\\+", "%20");

					if (HttpUtil.retrieveCookieValue(context.getHttpRequest(), cookieName) != null) {
						return ""; //silent exit
					}

				}

				// move here, after premature exit points (permissions, page-cookie)
				previousIncludes.add(page.getName());
				context.setVariable(ATTR_RECURSE, previousIncludes);

				/**
				 * We want inclusion to occur within the context of its own page, because we need the links to be
				 * correct.
				 */

				WikiContext includedContext = context.clone();
				includedContext.setPage(page);

				String pageData = pageManager.getPureText(page, context.getPageVersion());
				String moreLink = "";

				if (section != -1) {
					try {
						pageData = TextUtil.getSection(pageData, section);
					} catch (IllegalArgumentException e) {
						throw new PluginException(e.getMessage());
					}
				}

				if (pageData.length() > maxlen) {
					pageData = pageData.substring(0, maxlen) + " ...";
					moreLink = "<p><a href=\"" + context.getURL(WikiContext.PAGE_VIEW, includedPageId) + "\">"
							+ rb.getString("insertpage.more") + "</a></p>";
				}

				res.append("<div class=\"inserted-page ");
				if (clazz != null)
					res.append(clazz);
				if (!style.equals(DEFAULT_STYLE))
					res.append("\" style=\"" + style);
				if (showOnce)
					res.append("\" data-once=\"" + cookieName);
				res.append("\" >");

				res.append(renderingManager.textToHTML(includedContext, pageData));
				res.append(moreLink);

				res.append("</div>");

				//
				//  Remove the name from the stack; we're now done with this.
				//
				previousIncludes.remove(page.getName());
				context.setVariable(ATTR_RECURSE, previousIncludes);
			} else {
				if (defaultstr != null) {
					res.append(defaultstr);
				} else {
					res.append("There is no page called '" + includedPageId + "'.  Would you like to ");
					res.append("<a href=\"" //
							+ context.getURL(WikiContext.PAGE_EDIT, includedPageId) + "\">create it?</a>");
				}
			}
		} else {
			res.append("<span class=\"error\">");
			res.append("You have to define a page!");
			res.append("</span>");
		}
		return res.toString();
	}

}
