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
package org.apache.wiki;

import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.internal.MainActivator;
import org.apache.wiki.url0.URLConstructor;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.services.ServicesRefs;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Deactivate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * This provides a master servlet for dealing with short urls.  It mostly does redirects to the proper JSP pages. It also intercepts the
 * servlet shutdown events and uses it to signal wiki shutdown.
 *
 * @since 2.2
 */
public class WikiServlet extends HttpServlet {

    private static final long serialVersionUID = 3258410651167633973L;
    private static final Logger log = Logger.getLogger( WikiServlet.class.getName() );

    final private Engine m_engine;
    final private IWikiConfiguration wikiConfiguration;
    
    
    /**
     * Creates a WikiServlet.
     */
    public WikiServlet() {
		super();
		BundleContext context = MainActivator.getContext();
		ServiceReference<?> ref = context.getServiceReference(Engine.class.getName());
		m_engine = (ref != null) ? (Engine) context.getService(ref) : null;
		if (m_engine != null) {
			this.wikiConfiguration = m_engine.getWikiConfiguration();
		} else {
			this.wikiConfiguration = null;
			//TODO: ???????????????????? ???????????? - ?????? ?????????????? Engine.
			throw new NullPointerException("missed Engine service.");
		}
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public void init( final ServletConfig config ) throws ServletException {
        super.init( config );
        log.info( "WikiServlet initialized." );
        // :FVK: workaround (???????????????? ?????????? ?????? ???? ?????????? - ?????? ?????? ???????? WikiServlet - ???? ????????????????????????.?
    }

    /**
     * Destroys the WikiServlet; called by the servlet container when shutting down the webapp. This method calls the
     * protected method {@link WikiEngine#shutdown()}, which sends {@link org.apache.wiki.api.event.WikiEngineEvent#SHUTDOWN}
     * events to registered listeners.
     *
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        log.info( "WikiServlet shutdown." );
        CacheManager.getInstance().shutdown();
        m_engine.shutdown(); //:FVK: workaround. ?????????? ?????????? ?????????????????? ?????? @Deactivate.
        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doPost( final HttpServletRequest req, final HttpServletResponse res ) throws IOException, ServletException {
        doGet( req, res );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doGet( final HttpServletRequest req, final HttpServletResponse res ) throws IOException, ServletException {
        String pageName = URLConstructor.parsePageFromURL( req, wikiConfiguration.getContentEncodingCs() );

        log.info( "Request for page: " + pageName );
        if( pageName == null ) {
            pageName = wikiConfiguration.getFrontPage(); // FIXME: Add special pages as well
        }

        final String jspPage = ServicesRefs.getUrlConstructor().getForwardPage( req );
        final RequestDispatcher dispatcher = req.getRequestDispatcher( "/" + jspPage + "?page=" +
        		wikiConfiguration.encodeName( pageName ) + "&" + req.getQueryString() );

        dispatcher.forward( req, res );
    }

}
