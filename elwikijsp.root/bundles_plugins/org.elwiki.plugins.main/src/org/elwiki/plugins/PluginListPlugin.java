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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.modules.WikiModuleInfo;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.ui.TemplateManager;
import org.elwiki.api.plugin.InitializablePlugin;
import org.elwiki.api.plugin.PluginManager;
import org.elwiki.api.plugin.WikiPlugin;

/**
 * PluginListPlugin gives you a list of plugins, filters and editors installed in your ElWiki
 * instance.
 *
 * @author David Vittor
 */
public class PluginListPlugin implements WikiPlugin, InitializablePlugin {

	public static enum ModuleType {
		ALL, PLUGIN, FILTER, EDITOR;
	}
	
	public class WikiPluginInfoComparator implements Comparator<WikiModuleInfo> {
		public int compare(WikiModuleInfo m1, WikiModuleInfo m2) {
			return m1.getName().compareTo(m2.getName());
		}
	}

	public class PageFilterComparator implements Comparator<WikiModuleInfo> {
		public int compare(WikiModuleInfo pf1, WikiModuleInfo pf2) {
			return pf1.getClass().getSimpleName().compareTo(pf2.getClass().getSimpleName());
		}
	}

	public class WikiModuleInfoComparator implements Comparator<WikiModuleInfo> {
		public int compare(WikiModuleInfo m1, WikiModuleInfo m2) {
			return m1.getClass().getName().compareTo(m2.getClass().getName());
		}
	}

	private final Logger log = Logger.getLogger(PluginListPlugin.class);

	public static final String DEFAULT_CLASS = "plugin-list";
	public static final ModuleType DEFAULT_TYPE = ModuleType.ALL;
	public static final Boolean DEFAULT_SHOWSTYLE = false;

	private static final String PARAM_CLASS = "class";
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_SHOWSTYLE = "showstyle";

	private String className = DEFAULT_CLASS;
	private ModuleType typeFilter = DEFAULT_TYPE;
	private Boolean showStyle = DEFAULT_SHOWSTYLE;
	private static final String DELIM = " | ";

	Engine engine;

	private RenderingManager renderingManager;

	private PageManager pageManager;

	private PluginManager pluginManager;

	private FilterManager filterManager;

	private TemplateManager templateManager;

	private EditorManager editorManager;

	@Override
	public void initialize(Engine engine) throws PluginException {
		this.engine = engine;
		this.renderingManager = engine.getManager(RenderingManager.class);
		this.pageManager = engine.getManager(PageManager.class);
		this.pluginManager = engine.getManager(PluginManager.class);
		this.filterManager = engine.getManager(FilterManager.class);
		this.templateManager = engine.getManager(TemplateManager.class);
		this.editorManager = engine.getManager(EditorManager.class);
	}

	@Override
	public String execute(WikiContext wikiContext, Map<String, String> params) throws PluginException {
		this.setLogForDebug((String) params.get("debug"));
		this.log.info("STARTED");
		String result = "";
		StringBuffer buffer = new StringBuffer();

		// Validate all parameters
		this.validateParams(params);

		Collection<WikiModuleInfo> pluginModules = pluginManager.modules();
		Collection<WikiModuleInfo> filterModules = filterManager.modules();
		Collection<WikiModuleInfo> templateModules = templateManager.modules(); // empty list
		Collection<WikiModuleInfo> editorModules = editorManager.modules();

		try {
			buffer.append("|| Module Type || Name (Alias) || Class || Author || Min-Max");
			if (this.showStyle) {
				buffer.append(" || Script/Stylesheet");
			}

			buffer.append("\n");
			String baseUrl = engine.getWikiConfiguration().getBaseURL();

			String name;
			String author;
			if (this.typeFilter == ModuleType.ALL || this.typeFilter == ModuleType.PLUGIN) {
				ArrayList<WikiModuleInfo> pluginModuleList = new ArrayList<>(pluginModules);
				Collections.sort(pluginModuleList, new PluginListPlugin.WikiPluginInfoComparator());
				for (WikiModuleInfo info : pluginModuleList) {
					name = info.getName();
					author = info.getAuthor();
					//@formatter:off
					buffer.append("| Plugin " +
							DELIM + this.getNameLinked(pageManager, name) + this.getAlias(info.getAlias()) +
							DELIM + this.getClassNameLinked(info.getClass().getName()) +
							DELIM + this.getNameLinked(pageManager, author) +
							DELIM + info.getMinVersion() + "-" + info.getMaxVersion());
					//@formatter:on
					if (this.showStyle) {
						buffer.append(DELIM + this.getResourceLinked(baseUrl, info.getScriptLocation())
								+ this.getResourceLinked(baseUrl, info.getStylesheetLocation()));
					}
					buffer.append("\n");
				}
			}

			if (this.typeFilter == ModuleType.ALL || this.typeFilter == ModuleType.FILTER) {
				ArrayList<WikiModuleInfo> filterModuleList = new ArrayList<>(filterModules);
				Collections.sort(filterModuleList, new PluginListPlugin.PageFilterComparator());
				for (WikiModuleInfo filter : filterModuleList) {
					name = filter.getClass().getSimpleName();
					//@formatter:off
					buffer.append("| Filter" +
							DELIM + this.getNameLinked(pageManager, name) +
							DELIM + this.getClassNameLinked(filter.getClass().getName()) +
							DELIM + "" +
							DELIM + "" + "-" + "");
					//@formatter:on
					if (this.showStyle) {
						buffer.append(DELIM);
					}
					buffer.append("\n");
				}
			}

			if (this.typeFilter == ModuleType.ALL || this.typeFilter == ModuleType.EDITOR) {
				ArrayList<WikiModuleInfo> editorModuleList = new ArrayList<>(editorModules);
				Collections.sort(editorModuleList, new PluginListPlugin.WikiModuleInfoComparator());
				for (WikiModuleInfo info : editorModuleList) {
					name = info.getName();
					author = info.getAuthor();
					//@formatter:off
					buffer.append("| Editor" +
							DELIM + this.getNameLinked(pageManager, name) +
							DELIM + this.getClassNameLinked(info.getClass().getName()) + 
							DELIM + this.getNameLinked(pageManager, author) +
							DELIM + info.getMinVersion() + "-" + info.getMaxVersion());
					//@formatter:on
					if (this.showStyle) {
						buffer.append(DELIM + this.getResourceLinked(baseUrl, info.getScriptLocation())
								+ this.getResourceLinked(baseUrl, info.getStylesheetLocation()));
					}
					buffer.append("\n");
				}
			}

			this.log.info("result=" + buffer.toString());
			result = renderingManager.textToHTML(wikiContext, buffer.toString());
			result = "<div class='" + this.className + "'>" + result + "</div>";
		} catch (Exception e) {
			this.log.error(e, e);
			throw new PluginException(e.getMessage());
		}

		return result;
	}

	protected void validateParams(Map<String, String> params) throws PluginException {
		String paramName;
		String param;

		this.log.info("validateParams() START");

		// Check PARAM_CLASS
		paramName = PARAM_CLASS;
        param = params.get(paramName);
        if (StringUtils.isNotBlank(param)) {
            log.info(paramName + "=" + param);
            if (!StringUtils.isAsciiPrintable(param)) {
                throw new PluginException(paramName + " parameter is not a valid value");
            }
            className = param;
        }

		// Check PARAM_TYPE
        paramName = PARAM_TYPE;
        param = params.get(paramName);
        if (StringUtils.isNotBlank(param)) {
            log.info(paramName + "=" + param);
            if (!StringUtils.isAsciiPrintable(param)) {
                throw new PluginException(paramName + " parameter is not a valid value");
            }
            if (param.equalsIgnoreCase(ModuleType.PLUGIN.name())) {
                typeFilter = ModuleType.PLUGIN;
            }
            else if (param.equalsIgnoreCase(ModuleType.FILTER.name())) {
                typeFilter = ModuleType.FILTER;
            }
            else if (param.equalsIgnoreCase(ModuleType.EDITOR.name())) {
                typeFilter = ModuleType.EDITOR;
            }
            else if (param.equalsIgnoreCase(ModuleType.ALL.name())) {
                typeFilter = ModuleType.ALL;
            } else {
                throw new PluginException(paramName + " parameter is not a valid type. " +
                        "Should be all,plugin,filter, or editor. ");
            }
        }

		// Check PARAM_SHOWSTYLE
        paramName = PARAM_SHOWSTYLE;
        param = params.get(paramName);
        if (StringUtils.isNotBlank(param)) {
            log.info(paramName + "=" + param);
            if (!param.equalsIgnoreCase("true") && !param.equalsIgnoreCase("false")
                    && !param.equals("0") && !param.equals("1")) {
                throw new PluginException(paramName + " parameter is not a valid boolean");
            }
            showStyle = Boolean.parseBoolean(param);
        }
	}

	private String getAlias(String alias) {
		String result = "";
		if (StringUtils.isNotBlank(alias)) {
			result = " (" + alias + ")";
		}

		return result;
	}

	private String getClassNameLinked(String className) {
		String result = className;
		//TODO: :FVK: change "org.apache.wiki"
		if (StringUtils.isNotBlank(className) && className.startsWith("org.apache.wiki")) {
			String pathName = className.replace(".", "/");
			if (pathName.contains("$")) {
				int index = pathName.indexOf("$");
				pathName = pathName.substring(0, index);
			}

			//TODO: :FVK: change "http://jspwiki.apache.org/apidocs/2.10.1/"
			result = "[" + className + "|http://jspwiki.apache.org/apidocs/2.10.1/" + pathName + ".html]";
		}

		return result;
	}

	private String getResourceLinked(String baseUrl, String resourcePath) {
		String result = "";
		if (StringUtils.isNotBlank(resourcePath)) {
			result = "[" + resourcePath + "|" + baseUrl + "/" + resourcePath + "]";
		}

		return result;
	}

	private String getNameLinked(PageManager pageManager, String name) {
		String result = name;
		if (StringUtils.isNotBlank(name)) {
			try {
				if (pageManager.pageExists(name)) {
					result = "[" + name + "]";
				}
			} catch (Exception var5) {
				this.log.error(var5, var5);
			}
		}

		return result;
	}

	private void setLogForDebug(String value) {
		if (StringUtils.isNotBlank(value) && (value.equalsIgnoreCase("true") || value.equals("1"))) {
			this.log.setLevel(Level.INFO);
		}
	}

}
