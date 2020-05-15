/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.equinox.http.helper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;

public class ContextInitParametersServletAdaptor implements Servlet {

	private Servlet delegate;
	Properties initParameters;

	public ContextInitParametersServletAdaptor(Servlet delegate, Properties initParameters) {
		this.delegate = delegate;
		this.initParameters = initParameters;
	}

	public void init(ServletConfig config) throws ServletException {
		delegate.init(new ServletConfigAdaptor(config));
	}

	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		delegate.service(request, response);
	}

	public void destroy() {
		delegate.destroy();
	}

	public ServletConfig getServletConfig() {
		return delegate.getServletConfig();
	}

	public String getServletInfo() {
		return delegate.getServletInfo();
	}

	private class ServletConfigAdaptor implements ServletConfig {
		private ServletConfig config;
		private ServletContext context;

		public ServletConfigAdaptor(ServletConfig config) {
			this.config = config;
			this.context = new ServletContextAdaptor(config.getServletContext());
		}

		public String getInitParameter(String arg0) {
			return config.getInitParameter(arg0);
		}

		public Enumeration getInitParameterNames() {
			return config.getInitParameterNames();
		}

		public ServletContext getServletContext() {
			return context;
		}

		public String getServletName() {
			return config.getServletName();
		}
	}

	private class ServletContextAdaptor implements ServletContext {

		private ServletContext delegate;

		public ServletContextAdaptor(ServletContext delegate) {
			this.delegate = delegate;
		}

		public RequestDispatcher getRequestDispatcher(String path) {
			return delegate.getRequestDispatcher(path);
		}

		public URL getResource(String name) throws MalformedURLException {
			return delegate.getResource(name);
		}

		public InputStream getResourceAsStream(String name) {
			return delegate.getResourceAsStream(name);
		}

		public Set getResourcePaths(String name) {
			return delegate.getResourcePaths(name);
		}

		public Object getAttribute(String arg0) {
			return delegate.getAttribute(arg0);
		}

		public Enumeration getAttributeNames() {
			return delegate.getAttributeNames();
		}

		public ServletContext getContext(String arg0) {
			return delegate.getContext(arg0);
		}

		public String getInitParameter(String arg0) {
			return initParameters.getProperty(arg0);
		}

		public Enumeration getInitParameterNames() {
			return initParameters.propertyNames();
		}

		public int getMajorVersion() {
			return delegate.getMajorVersion();
		}

		public String getMimeType(String arg0) {
			return delegate.getMimeType(arg0);
		}

		public int getMinorVersion() {
			return delegate.getMinorVersion();
		}

		public RequestDispatcher getNamedDispatcher(String arg0) {
			return delegate.getNamedDispatcher(arg0);
		}

		public String getRealPath(String arg0) {
			return delegate.getRealPath(arg0);
		}

		public String getServerInfo() {
			return delegate.getServerInfo();
		}

		/** @deprecated **/
		public Servlet getServlet(String arg0) throws ServletException {
			return delegate.getServlet(arg0);
		}

		public String getServletContextName() {
			return delegate.getServletContextName();
		}

		/** @deprecated **/
		public Enumeration getServletNames() {
			return delegate.getServletNames();
		}

		/** @deprecated **/
		public Enumeration getServlets() {
			return delegate.getServlets();
		}

		/** @deprecated **/
		public void log(Exception arg0, String arg1) {
			delegate.log(arg0, arg1);
		}

		public void log(String arg0, Throwable arg1) {
			delegate.log(arg0, arg1);
		}

		public void log(String arg0) {
			delegate.log(arg0);
		}

		public void removeAttribute(String arg0) {
			delegate.removeAttribute(arg0);
		}

		public void setAttribute(String arg0, Object arg1) {
			delegate.setAttribute(arg0, arg1);
		}
		
		// Added in Servlet 2.5
		public String getContextPath() {
			try {
				Method getContextPathMethod = delegate.getClass().getMethod("getContextPath", null); //$NON-NLS-1$
				return (String) getContextPathMethod.invoke(delegate, null);
			} catch (Exception e) {
				// ignore
			}
			return null;
		}

		public int getEffectiveMajorVersion() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getEffectiveMinorVersion() {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean setInitParameter(String name, String value) {
			// TODO Auto-generated method stub
			return false;
		}

		public Dynamic addServlet(String servletName, String className) {
			// TODO Auto-generated method stub
			return null;
		}

		public Dynamic addServlet(String servletName, Servlet servlet) {
			// TODO Auto-generated method stub
			return null;
		}

		public Dynamic addServlet(String servletName, Class servletClass) {
			// TODO Auto-generated method stub
			return null;
		}

		public Servlet createServlet(Class clazz) throws ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		public ServletRegistration getServletRegistration(String servletName) {
			// TODO Auto-generated method stub
			return null;
		}

		public Map getServletRegistrations() {
			// TODO Auto-generated method stub
			return null;
		}

		public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
			// TODO Auto-generated method stub
			return null;
		}

		public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
			// TODO Auto-generated method stub
			return null;
		}

		public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class filterClass) {
			// TODO Auto-generated method stub
			return null;
		}

		public Filter createFilter(Class clazz) throws ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		public FilterRegistration getFilterRegistration(String filterName) {
			// TODO Auto-generated method stub
			return null;
		}

		public Map getFilterRegistrations() {
			// TODO Auto-generated method stub
			return null;
		}

		public SessionCookieConfig getSessionCookieConfig() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setSessionTrackingModes(Set sessionTrackingModes) {
			// TODO Auto-generated method stub
			
		}

		public Set getDefaultSessionTrackingModes() {
			// TODO Auto-generated method stub
			return null;
		}

		public Set getEffectiveSessionTrackingModes() {
			// TODO Auto-generated method stub
			return null;
		}

		public void addListener(String className) {
			// TODO Auto-generated method stub
			
		}

		public void addListener(EventListener t) {
			// TODO Auto-generated method stub
			
		}

		public void addListener(Class listenerClass) {
			// TODO Auto-generated method stub
			
		}

		public EventListener createListener(Class clazz) throws ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		public JspConfigDescriptor getJspConfigDescriptor() {
			// TODO Auto-generated method stub
			return null;
		}

		public ClassLoader getClassLoader() {
			// TODO Auto-generated method stub
			return null;
		}

		public void declareRoles(String[] roleNames) {
			// TODO Auto-generated method stub
			
		}

		public String getVirtualServerName() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
