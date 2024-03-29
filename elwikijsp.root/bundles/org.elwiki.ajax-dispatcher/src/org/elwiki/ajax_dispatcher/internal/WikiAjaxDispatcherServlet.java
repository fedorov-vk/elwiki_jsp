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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.ajax.AjaxUtil;
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.ISessionMonitor;
import org.elwiki.configuration.IWikiConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * This provides a simple ajax servlet for handling "/ajax/<ClassName>" requests. HttpServlet
 * classes need to be registered using
 * {@link WikiAjaxDispatcher #registerServlet(WikiAjaxServlet)}.
 */
//@formatter:off
@Component(
	name = "web.WikiAjaxDispatcherServlet",
	service = Servlet.class,
	property = {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/ajax/*",
	},
	scope = ServiceScope.PROTOTYPE)
//@formatter:on
public class WikiAjaxDispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 6765508366678337861L;
	private static final Logger log = Logger.getLogger(WikiAjaxDispatcherServlet.class);

	private String PATH_AJAX = "/ajax/";

	@Reference
	private IWikiConfiguration wikiConfiguration;

	@Reference
	private Engine engine;

	@Activate
	protected void startup() {
		log.debug("«web» start " + WikiAjaxDispatcherServlet.class.getSimpleName());
	}

	/**
	 * {@inheritDoc}
	 *
	 * This sets the AjaxPath to "/ajax/" as configured in "jspwiki.ajax.url.prefix". Note: Do not
	 * change this without also changing the web.xml file.
	 */
	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		PATH_AJAX = "/" + wikiConfiguration.getStringProperty("jspwiki.ajax.url.prefix", "ajax") + "/";
		log.debug("«web» init  " + WikiAjaxDispatcherServlet.class.getSimpleName() + " (AJAX path: " + PATH_AJAX + ")");
	}

	/**
	 * Calls {@link #performAction}
	 */
	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse res)
			throws IOException, ServletException {
		performAction(req, res);
	}

	/**
	 * Calls {@link #performAction}
	 */
	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse res)
			throws IOException, ServletException {
		performAction(req, res);
	}

	/**
	 * The main method which get the requestURI "/ajax/<ServletName>", gets the
	 * {@link #getServletName} and finds the servlet using {@link #findServletByName}. It then calls
	 * {@link WikiAjaxServlet#service} method.
	 *
	 * @param req the inbound request
	 * @param res the outbound response
	 * @throws IOException      if WikiEngine's content encoding is valid
	 * @throws ServletException if no registered servlet can be found
	 */
	private void performAction(final HttpServletRequest req, final HttpServletResponse res)
			throws IOException, ServletException {
		
		final String path = req.getRequestURI();
		final String servletName = getServletName(path);
		if (servletName != null) {
			final AjaxServletContainer container = findServletContainer(servletName);
			if (container != null) {
				final WikiAjaxServlet servlet = container.servlet;
				if (validatePermission(req, container)) {
					req.setCharacterEncoding(engine.getContentEncoding().displayName());
					res.setCharacterEncoding(engine.getContentEncoding().displayName());
					final String actionName = AjaxUtil.getNextPathPart(req.getRequestURI(),
							servlet.getServletMapping());
					log.debug("actionName=" + actionName);
					final String params = req.getParameter("params");
					log.debug("params=" + params);
					List<String> paramValues = new ArrayList<>();
					if (params != null) {
						if (StringUtils.isNotBlank(params)) {
							paramValues = Arrays.asList(params.trim().split(","));
						}
					}
					servlet.service(req, res, actionName, paramValues);
				} else {
					log.warn("Servlet container " + container + " not authorised. Permission required.");
				}
			} else {
				log.error("No registered class for servletName=" + servletName + " in path=" + path);
				throw new ServletException("No registered class for servletName=" + servletName);
			}
		}
	}

	/**
	 * Validate the permission of the {@link WikiAjaxServlet} using the
	 * {@link AuthorizationManager#checkPermission}
	 *
	 * @param request       the servlet request
	 * @param container the container info of the servlet
	 * @return true if permission is valid
	 */
	private boolean validatePermission(final HttpServletRequest request, final AjaxServletContainer container) {
		ISessionMonitor sessionMonitor = this.engine.getManager(ISessionMonitor.class);
		AuthorizationManager authorizationManager = this.engine.getManager(AuthorizationManager.class);

		boolean valid = false;
		if (container != null) {
			WikiSession wikiSession = sessionMonitor.getWikiSession(request);
			valid = authorizationManager.checkPermission(wikiSession, container.permission);
		}
		return valid;
	}

	/**
	 * Get the ServletName from the requestURI "/ajax/<ServletName>", using
	 * {@link AjaxUtil#getNextPathPart}.
	 *
	 * @param path The requestURI, which must contains "/ajax/<ServletName>" in the path
	 * @return The ServletName for the requestURI, or null
	 * @throws ServletException if the path is invalid
	 */
	public String getServletName(final String path) throws ServletException {
		return AjaxUtil.getNextPathPart(path, PATH_AJAX);
	}

	/**
	 * Find the {@link AjaxServletContainer} as registered in {@link #registerServlet}.
	 *
	 * @param servletAlias the name of the servlet from {@link #getServletName}
	 * @return The first servlet found, or null.
	 */
	private AjaxServletContainer findServletContainer(final String servletAlias) {
		return WikiAjaxDispatcherService.getAjaxServlets().get(servletAlias);
	}

	/**
	 * Find the {@link WikiAjaxServlet} given the servletAlias that it was registered with.
	 *
	 * @param servletAlias the value provided to {@link #registerServlet}
	 * @return the {@link WikiAjaxServlet} given the servletAlias that it was registered with.
	 */
	public WikiAjaxServlet findServletByName(final String servletAlias) {
		final AjaxServletContainer container = WikiAjaxDispatcherService.getAjaxServlets().get(servletAlias);
		if (container != null) {
			return container.servlet;
		}
		return null;
	}

}
