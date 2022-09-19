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
package org.elwiki.ajax_dispatcher.internal;

import java.security.Permission;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.util.TextUtil;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.permissions.PagePermission;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * This provides service for registering HttpServlet classes. need to be registered using
 * {@link WikiAjaxDispatcherService#registerServlet(WikiAjaxServlet)}. <br/>
 * Servlet {@link WikiAjaxDispatcherServlet} handling /ajax/<ClassName> requests as a simple
 * ajax functionality.
 */
//@formatter:off
@Component(
	name = "elwiki.WikiAjaxDispatcher",
	service = WikiAjaxDispatcher.class,
	factory = WikiAjaxDispatcher.WIKI_AJAX_DISPATCHER_FACTORY)
//@formatter:on
public class WikiAjaxDispatcherService implements WikiAjaxDispatcher {

	private static final Logger log = Logger.getLogger(WikiAjaxDispatcherService.class.getName());

	private IWikiConfiguration config;
	private Engine m_engine;

	private static final Map<String, AjaxServletContainer> ajaxServlets = new ConcurrentHashMap<>();
	private String PATH_AJAX = "/ajax/";

	// -- service handling ---------------------------(start)--

	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		try {
			Object engine = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
			if (engine instanceof Engine) {
				initialize((Engine) engine);
			}
		} catch (WikiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Deactivate
	protected void shutdown() {
		//
	}

	// -- service handling -----------------------------(end)--

	/**
	 * This sets the AjaxPath to "/ajax/" as configured in "jspwiki.ajax.url.prefix". Note: Do not
	 * change this without also changing the {@link WikiAjaxDispatcherServlet}
	 * 
	 * @param engine
	 * @throws WikiException
	 */
	private void initialize(Engine engine) throws WikiException {
		this.m_engine = engine;
		this.config = m_engine.getWikiConfiguration();
		PATH_AJAX = "/" + TextUtil.getStringProperty(m_engine.getWikiPreferences(), "jspwiki.ajax.url.prefix", "ajax")
				+ "/";
		log.info("WikiAjaxDispatcher initialized.");
	}

	@Override
	public void registerServlet(WikiAjaxServlet servlet) {
		registerServlet(servlet.getServletMapping(), servlet);
	}

	public void registerServlet(String alias, WikiAjaxServlet servlet) {
		registerServlet(alias, servlet, PagePermission.VIEW);
	}

	public void registerServlet(final String alias, final WikiAjaxServlet servlet, final Permission perm) {
		log.info("WikiAjaxDispatcherServlet registering:\n " + alias + " = " + servlet + "\n perm = " + perm);
		ajaxServlets.put(alias, new AjaxServletContainer(alias, servlet, perm));
	}

	static Map<String, AjaxServletContainer> getAjaxServlets() {
		return ajaxServlets;
	}

}
