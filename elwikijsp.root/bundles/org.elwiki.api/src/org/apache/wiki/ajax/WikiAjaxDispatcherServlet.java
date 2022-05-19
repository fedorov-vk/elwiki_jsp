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
package org.apache.wiki.ajax;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.internal.ApiActivator;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.util.TextUtil;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.permissions.PagePermission;
import org.elwiki.services.ServicesRefs;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This provides a simple ajax servlet for handling /ajax/<ClassName> requests. HttpServlet classes need to be registered using
 * {@link WikiAjaxDispatcherServlet#registerServlet(WikiAjaxServlet)}
 *
 * @since 2.10.2-svn12
 */
public class WikiAjaxDispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 2371163144047926990L;
    private static final Map< String, AjaxServletContainer > ajaxServlets = new ConcurrentHashMap<>();
    private static final Logger log = Logger.getLogger( WikiAjaxDispatcherServlet.class.getName() );
    final private Engine m_engine;
    private String PATH_AJAX = "/ajax/";
	private IWikiConfiguration config;

    /**
     * Creates a WikiAjaxDispatcherServlet.
     */
    public WikiAjaxDispatcherServlet() {
		super();
		BundleContext context = ApiActivator.getContext();
		ServiceReference<?> ref = context.getServiceReference(Engine.class.getName());
		m_engine = (ref != null) ? (Engine) context.getService(ref) : null;
		if (m_engine == null) {
			//TODO: обработать аварию - нет сервиса Engine.
			throw new NullPointerException("missed Engine service.");
		}
	}

	/**
     * {@inheritDoc}
     *
     * This sets the AjaxPath to "/ajax/" as configured in "jspwiki.ajax.url.prefix".
     * Note: Do not change this without also changing the web.xml file.
     */
    @Override
    public void init( final ServletConfig config ) throws ServletException {
        super.init( config );
        this.config = m_engine.getWikiConfiguration();
        PATH_AJAX = "/" + TextUtil.getStringProperty( m_engine.getWikiPreferences(), "jspwiki.ajax.url.prefix", "ajax" ) + "/";
        log.info( "WikiAjaxDispatcherServlet initialized." );
    }

    /**
     * Register a {@link WikiAjaxServlet} using the servlet mapping as the alias
     */
    public static void registerServlet( final WikiAjaxServlet servlet ) {
        registerServlet( servlet.getServletMapping(), servlet );
    }

    /**
     * Register a {@link WikiAjaxServlet} with a specific alias, and default permission {@link PagePermission#VIEW}.
     */
    public static void registerServlet( final String alias, final WikiAjaxServlet servlet ) {
        registerServlet( alias, servlet, PagePermission.VIEW );
    }

    /**
     * Regster a {@link WikiAjaxServlet} given an alias, the servlet, and the permission.
     * This creates a temporary bundle object called {@link WikiAjaxDispatcherServlet.AjaxServletContainer}
     *
     * @param alias the uri link to this servlet
     * @param servlet the servlet being registered
     * @param perm the permission required to execute the servlet.
     */
    public static void registerServlet( final String alias, final WikiAjaxServlet servlet, final Permission perm ) {
        log.info( "WikiAjaxDispatcherServlet registering " + alias + "=" + servlet + " perm=" + perm );
        ajaxServlets.put( alias, new AjaxServletContainer( alias, servlet, perm ) );
    }

    /**
     * Calls {@link #performAction}
     */
    @Override
    public void doPost( final HttpServletRequest req, final HttpServletResponse res ) throws IOException, ServletException {
        performAction( req, res );
    }

    /**
     * Calls {@link #performAction}
     */
    @Override
    public void doGet( final HttpServletRequest req, final HttpServletResponse res ) throws IOException, ServletException {
        performAction( req, res );
    }

    /**
     * The main method which get the requestURI "/ajax/<ServletName>", gets the {@link #getServletName} and finds the servlet using
     * {@link #findServletByName}. It then calls {@link WikiAjaxServlet#service} method.
     *
     * @param req the inbound request
     * @param res the outbound response
     * @throws IOException if WikiEngine's content encoding is valid
     * @throws ServletException if no registered servlet can be found
     */
    private void performAction( final HttpServletRequest req, final HttpServletResponse res ) throws IOException, ServletException {
        final String path = req.getRequestURI();
        final String servletName = getServletName( path );
        if( servletName != null) {
            final AjaxServletContainer container = findServletContainer( servletName );
            if( container != null ) {
                final WikiAjaxServlet servlet = container.servlet;
                if ( validatePermission( req, container ) ) {
                    req.setCharacterEncoding( config.getContentEncodingCs().displayName() );
                    res.setCharacterEncoding( config.getContentEncodingCs().displayName() );
                    final String actionName = AjaxUtil.getNextPathPart( req.getRequestURI(), servlet.getServletMapping() );
                    log.debug( "actionName=" + actionName );
                    final String params = req.getParameter( "params" );
                    log.debug( "params=" + params );
                    List< String > paramValues = new ArrayList<>();
                    if( params != null ) {
                        if( StringUtils.isNotBlank( params ) ) {
                            paramValues = Arrays.asList( params.trim().split( "," ) );
                        }
                    }
                    servlet.service( req, res, actionName, paramValues );
                } else {
                    log.warn( "Servlet container " + container + " not authorised. Permission required." );
                }
            } else {
                log.error( "No registered class for servletName=" + servletName + " in path=" + path );
                throw new ServletException( "No registered class for servletName=" + servletName );
            }
        }
    }

    /**
     * Validate the permission of the {@link WikiAjaxServlet} using the {@link AuthorizationManager#checkPermission}
     *
     * @param req the servlet request
     * @param container the container info of the servlet
     * @return true if permission is valid
     */
    private boolean validatePermission( final HttpServletRequest req, final AjaxServletContainer container ) {
        boolean valid = false;
        if( container != null ) {
        	Session wikiSession = m_engine.getSessionMonitor().getWikiSession(req);
            valid = ServicesRefs.getAuthorizationManager().checkPermission(wikiSession, container.permission );
        }
        return valid;
    }

    /**
     * Get the ServletName from the requestURI "/ajax/<ServletName>", using {@link AjaxUtil#getNextPathPart}.
     *
     * @param path The requestURI, which must contains "/ajax/<ServletName>" in the path
     * @return The ServletName for the requestURI, or null
     * @throws ServletException if the path is invalid
     */
    public String getServletName( final String path ) throws ServletException {
        return AjaxUtil.getNextPathPart( path, PATH_AJAX );
    }

    /**
     * Find the {@link AjaxServletContainer} as registered in {@link #registerServlet}.
     *
     * @param servletAlias the name of the servlet from {@link #getServletName}
     * @return The first servlet found, or null.
     */
    private AjaxServletContainer findServletContainer( final String servletAlias ) {
        return ajaxServlets.get( servletAlias );
    }

    /**
     * Find the {@link WikiAjaxServlet} given the servletAlias that it was registered with.
     *
     * @param servletAlias the value provided to {@link #registerServlet}
     * @return the {@link WikiAjaxServlet} given the servletAlias that it was registered with.
     */
    public WikiAjaxServlet findServletByName( final String servletAlias ) {
        final AjaxServletContainer container = ajaxServlets.get( servletAlias );
        if( container != null ) {
            return container.servlet;
        }
        return null;
    }

    private static class AjaxServletContainer {

        final String alias;
        final WikiAjaxServlet servlet;
        final Permission permission;

        public AjaxServletContainer( final String alias, final WikiAjaxServlet servlet, final Permission permission ) {
            this.alias = alias;
            this.servlet = servlet;
            this.permission = permission;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " " + alias + "=" + servlet.getClass().getSimpleName() + " permission=" + permission;
        }

    }

}
