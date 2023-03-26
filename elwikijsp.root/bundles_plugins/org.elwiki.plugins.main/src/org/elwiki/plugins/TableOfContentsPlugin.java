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

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.parser0.Heading;
import org.apache.wiki.parser0.HeadingListener;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.util.TextUtil;
import org.elwiki.api.plugin.InitializablePlugin;
import org.elwiki.api.plugin.WikiPlugin;
import org.elwiki.plugins.internal.PluginsActivator;
import org.elwiki_data.WikiPage;

/**
 * Provides a table of contents.
 * <p>
 * Parameters :
 * </p>
 * <ul>
 * <li><b>title</b> - The title of the table of contents.</li>
 * <li><b>numbered</b> - if true, generates automatically numbers for the headings.</li>
 * <li><b>start</b> - If using a numbered list, sets the start number.</li>
 * <li><b>prefix</b> - If using a numbered list, sets the prefix used for the list.</li>
 * </ul>
 */
public class TableOfContentsPlugin implements WikiPlugin, HeadingListener, InitializablePlugin {

	private static final Logger log = Logger.getLogger(TableOfContentsPlugin.class);

	/** Parameter name for setting the title. */
	public static final String PARAM_TITLE = "title";

	/** Parameter name for setting whether the headings should be numbered. */
	public static final String PARAM_NUMBERED = "numbered";

	/** Parameter name for setting where the numbering should start. */
	public static final String PARAM_START = "start";

	/** Parameter name for setting what the prefix for the heading is. */
	public static final String PARAM_PREFIX = "prefix";

	/** Varible name of WikiContext. */
	private static final String VAR_ALREADY_PROCESSING = "__TableOfContents.processing";

	StringBuffer m_buf = new StringBuffer();
	private boolean m_usingNumberedList = false;
	private String m_prefix = "";
	private int m_starting = 0;
	private int m_level1Index = 0;
	private int m_level2Index = 0;
	private int m_level3Index = 0;
	private int m_lastLevel = 0;
	
	FilterManager filterManager;
	
	PageManager pageManager;

	RenderingManager renderingManager;
	
	VariableManager variableManager;

	@Override
	public void initialize(Engine engine) throws PluginException {
		filterManager = engine.getManager(FilterManager.class);
		pageManager = engine.getManager(PageManager.class);
		renderingManager = engine.getManager(RenderingManager.class);
		variableManager = engine.getManager(VariableManager.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void headingAdded(WikiContext context, Heading hd) {
		//log.debug("HD: " + hd.m_level + ", " + hd.m_titleText + ", " + hd.m_titleAnchor);

		switch (hd.m_level) {
		case Heading.HEADING_SMALL:
			m_buf.append("<li class=\"toclevel-3\">");
			m_level3Index++;
			break;
		case Heading.HEADING_MEDIUM:
			m_buf.append("<li class=\"toclevel-2\">");
			m_level2Index++;
			break;
		case Heading.HEADING_LARGE:
			m_buf.append("<li class=\"toclevel-1\">");
			m_level1Index++;
			break;
		default:
			throw new InternalWikiException("Unknown depth in toc! (Please submit a bug report.)");
		}

		if (m_level1Index < m_starting) {
			// in case we never had a large heading ...
			m_level1Index++;
		}
		if ((m_lastLevel == Heading.HEADING_SMALL) && (hd.m_level != Heading.HEADING_SMALL)) {
			m_level3Index = 0;
		}
		if (((m_lastLevel == Heading.HEADING_SMALL) || (m_lastLevel == Heading.HEADING_MEDIUM))
				&& (hd.m_level == Heading.HEADING_LARGE)) {
			m_level3Index = 0;
			m_level2Index = 0;
		}

		String titleSection = hd.m_titleSection.replace('%', '_');
		String pageName = null;
		try {
			pageName = context.getConfiguration().encodeName(context.getPage().getName()).replace('%', '_');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sectref = "#section-" + pageName + "-" + titleSection;

		m_buf.append("<a class=\"wikipage\" href=\"" + sectref + "\">");
		if (m_usingNumberedList) {
			switch (hd.m_level) {
			case Heading.HEADING_SMALL:
				m_buf.append(m_prefix + m_level1Index + "." + m_level2Index + "." + m_level3Index + " ");
				break;
			case Heading.HEADING_MEDIUM:
				m_buf.append(m_prefix + m_level1Index + "." + m_level2Index + " ");
				break;
			case Heading.HEADING_LARGE:
				m_buf.append(m_prefix + m_level1Index + " ");
				break;
			default:
				throw new InternalWikiException("Unknown depth in toc! (Please submit a bug report.)");
			}
		}
		m_buf.append(TextUtil.replaceEntities(hd.m_titleText) + "</a></li>\n");

		m_lastLevel = hd.m_level;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		WikiPage page = context.getPage();
		ResourceBundle rb = PluginsActivator.getBundle(Preferences.getLocale(context));

		if (context.getVariable(VAR_ALREADY_PROCESSING) != null) {
			//return rb.getString("tableofcontents.title");
			return "<a href=\"#section-TOC\" class=\"toc\">" + rb.getString("tableofcontents.title") + "</a>";
		}

		/* Initialise variables. */
		m_buf.setLength(0);
		m_usingNumberedList = false;
		m_prefix = "";
		m_starting = 0;
		m_level1Index = 0;
		m_level2Index = 0;
		m_level3Index = 0;
		m_lastLevel = 0;
		StringBuilder sb = new StringBuilder();

		sb.append("<div class=\"toc\">\n");
		sb.append("<div class=\"collapsebox\">\n");

		sb.append("<h4 id=\"section-TOC\">");
		String title = params.get(PARAM_TITLE);
		sb.append((title != null) ? TextUtil.replaceEntities(title) : rb.getString("tableofcontents.title"));
		sb.append("</h4>\n");

		// should we use an ordered list?
		if (params.containsKey(PARAM_NUMBERED)) {
			String numbered = params.get(PARAM_NUMBERED);
			m_usingNumberedList = switch (numbered.toLowerCase()) {
			case "true", "yes" -> true;
			default -> false;
			};
		}

		// if we are using a numbered list, get the rest of the parameters (if any) ...
		if (m_usingNumberedList) {
			int start = 0;
			String startStr = params.get(PARAM_START);
			if ((startStr != null) && (startStr.matches("^\\d+$"))) {
				start = Integer.parseInt(startStr);
			}
			if (start < 0)
				start = 0;

			m_starting = start;
			m_level1Index = start - 1;
			if (m_level1Index < 0)
				m_level1Index = 0;
			m_level2Index = 0;
			m_level3Index = 0;
			m_prefix = TextUtil.replaceEntities(params.get(PARAM_PREFIX));
			if (m_prefix == null)
				m_prefix = "";
			m_lastLevel = Heading.HEADING_LARGE;
		}

		try {
			String wikiText = pageManager.getPureText(page);
			boolean runFilters = "true"
					.equals(variableManager.getValue(context, VariableManager.VAR_RUNFILTERS, "true"));

			if (runFilters) {
				try {
					wikiText = filterManager.doPreTranslateFiltering(context, wikiText);

				} catch (Exception e) {
					log.error("Could not construct table of contents: Filter Error", e);
					throw new PluginException("Unable to construct table of contents (see logs)");
				}
			}

			context.setVariable(VAR_ALREADY_PROCESSING, "x");

			MarkupParser parser = renderingManager.getParser(context, wikiText);
			parser.addHeadingListener(this);
			parser.parse();

			sb.append("<ul>\n").append(m_buf.toString()).append("</ul>\n");
		} catch (IOException e) {
			log.error("Could not construct table of contents", e);
			throw new PluginException("Unable to construct table of contents (see logs)");
		}

		sb.append("</div>\n</div>\n");

		return sb.toString();
	}

}
