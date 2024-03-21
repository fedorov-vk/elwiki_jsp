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
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiComponent;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.permissions.PagePermission;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * This provides service for registering HttpServlet classes. need to be registered using
 * {@link WikiAjaxDispatcherService#registerServlet(WikiAjaxServlet)}. <br/>
 * Servlet {@link WikiAjaxDispatcherService} handling /ajax/<ClassName> requests as a simple
 * ajax functionality.
 */
//@formatter:off
@Component(
	name = "elwiki.WikiAjaxDispatcher",
	service = { WikiAjaxDispatcher.class, WikiComponent.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class WikiAjaxDispatcherService implements WikiAjaxDispatcher, WikiComponent, EventHandler {

	private static final Logger log = Logger.getLogger(WikiAjaxDispatcherService.class);

	private static final Map<String, AjaxServletContainer> ajaxServlets = new ConcurrentHashMap<>();

	// -- OSGi service handling ----------------------(start)--

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	public Engine m_engine;

	@Activate
	protected void startup() {
		log.debug("«web» start " + WikiAjaxDispatcherService.class.getSimpleName());
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * This sets the AjaxPath to "/ajax/" as configured in "jspwiki.ajax.url.prefix". Note: Do not
	 * change this without also changing the {@link WikiAjaxDispatcherServlet}
	 * 
	 * @throws WikiException
	 */
	@Override
	public  void initialize() throws WikiException {
		log.debug("«initialized» " + WikiAjaxDispatcherService.class.getSimpleName());
	}

	// -- OSGi service handling ------------------------(end)--

	@Override
	public void registerServlet(WikiAjaxServlet servlet) {
		registerServlet(servlet.getServletMapping(), servlet);
	}

	public void registerServlet(String alias, WikiAjaxServlet servlet) {
		registerServlet(alias, servlet, PagePermission.VIEW);
	}

	public void registerServlet(final String alias, final WikiAjaxServlet servlet, final Permission perm) {
		log.info("WikiAjaxDispatcher registering:\n " + alias + " = " + servlet + "\n perm = " + perm);
		ajaxServlets.put(alias, new AjaxServletContainer(alias, servlet, perm));
	}

	static Map<String, AjaxServletContainer> getAjaxServlets() {
		return ajaxServlets;
	}

	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
			break;
		}*/		
	}

}
