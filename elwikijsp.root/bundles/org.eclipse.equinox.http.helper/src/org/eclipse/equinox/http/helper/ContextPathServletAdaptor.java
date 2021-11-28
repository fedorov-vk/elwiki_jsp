/*******************************************************************************
 * Copyright (c) 2007 Cognos Incorporated, IBM Corporation and others
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ContextPathServletAdaptor implements Servlet {

	private Servlet delegate;
	String contextPath;

	public ContextPathServletAdaptor(Servlet delegate, String contextPath) {
		this.delegate = delegate;
		this.contextPath = (contextPath == null || contextPath.equals("/")) ? "" : contextPath; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void init(ServletConfig config) throws ServletException {
		delegate.init(new ServletConfigAdaptor(config));
	}

	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		delegate.service(new HttpServletRequestAdaptor((HttpServletRequest) request), response);
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

	@SuppressWarnings({"unchecked", "rawtypes"})
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	private class ServletContextAdaptor implements ServletContext {

		private ServletContext delegate;

		public ServletContextAdaptor(ServletContext delegate) {
			this.delegate = delegate;
		}

		public RequestDispatcher getRequestDispatcher(String path) {
			if (contextPath.equals("/")) //$NON-NLS-1$
				return new RequestDispatcherAdaptor(delegate.getRequestDispatcher(path));
			return new RequestDispatcherAdaptor(delegate.getRequestDispatcher(contextPath + path));
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
			return delegate.getInitParameter(arg0);
		}

		public Enumeration getInitParameterNames() {
			return delegate.getInitParameterNames();
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
			return new RequestDispatcherAdaptor(delegate.getNamedDispatcher(arg0));
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
			return delegate.getEffectiveMajorVersion();
		}

		public int getEffectiveMinorVersion() {
			return delegate.getEffectiveMinorVersion();
		}

		public boolean setInitParameter(String name, String value) {
			return delegate.setInitParameter(name, value);
		}

		public Dynamic addServlet(String servletName, String className) {
			return delegate.addServlet(servletName, className);
		}

		public Dynamic addServlet(String servletName, Servlet servlet) {
			return addServlet(servletName, servlet);
		}

		public Dynamic addServlet(String servletName, Class servletClass) {
			return delegate.addServlet(servletName, servletClass);
		}

		public Servlet createServlet(Class clazz) throws ServletException {
			return delegate.createServlet(clazz);
		}

		public ServletRegistration getServletRegistration(String servletName) {
			return delegate.getServletRegistration(servletName);
		}

		public Map getServletRegistrations() {
			return delegate.getServletRegistrations();
		}

		public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
			return delegate.addFilter(filterName, className);
		}

		public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
			return delegate.addFilter(filterName, filter);
		}

		public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class filterClass) {
			return delegate.addFilter(filterName, filterClass);
		}

		public Filter createFilter(Class clazz) throws ServletException {
			return delegate.createFilter(clazz);
		}

		public FilterRegistration getFilterRegistration(String filterName) {
			return delegate.getFilterRegistration(filterName);
		}

		public Map getFilterRegistrations() {
			return delegate.getFilterRegistrations();
		}

		public SessionCookieConfig getSessionCookieConfig() {
			return delegate.getSessionCookieConfig();
		}

		public void setSessionTrackingModes(Set sessionTrackingModes) {
			// TODO Auto-generated method stub
			delegate.setSessionTrackingModes(sessionTrackingModes);
		}

		public Set getDefaultSessionTrackingModes() {
			return delegate.getDefaultSessionTrackingModes();
		}

		public Set getEffectiveSessionTrackingModes() {
			return delegate.getEffectiveSessionTrackingModes();
		}

		public void addListener(String className) {
			// TODO Auto-generated method stub
			delegate.addListener(className);
		}

		public void addListener(EventListener t) {
			// TODO Auto-generated method stub
			delegate.addListener(t);
		}

		public void addListener(Class listenerClass) {
			// TODO Auto-generated method stub
			delegate.addListener(listenerClass);
		}

		public EventListener createListener(Class clazz) throws ServletException {
			return delegate.createListener(clazz);
		}

		public JspConfigDescriptor getJspConfigDescriptor() {
			return delegate.getJspConfigDescriptor();
		}

		public ClassLoader getClassLoader() {
			return delegate.getClassLoader();
		}

		public void declareRoles(String[] roleNames) {
			delegate.declareRoles(roleNames);			
		}

		public String getVirtualServerName() {
			return delegate.getVirtualServerName();
		}

		@Override
		public Dynamic addJspFile(String servletName, String jspFile) {
			return delegate.addJspFile(servletName, jspFile);
		}

		@Override
		public int getSessionTimeout() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setSessionTimeout(int sessionTimeout) {
			delegate.setSessionTimeout(sessionTimeout);
		}

		@Override
		public String getRequestCharacterEncoding() {
			return delegate.getRequestCharacterEncoding();
		}

		@Override
		public void setRequestCharacterEncoding(String encoding) {
			delegate.setRequestCharacterEncoding(encoding);			
		}

		@Override
		public String getResponseCharacterEncoding() {
			return delegate.getResponseCharacterEncoding();
		}

		@Override
		public void setResponseCharacterEncoding(String encoding) {
			delegate.setResponseCharacterEncoding(encoding);			
		}
	}

	private class HttpServletRequestAdaptor extends HttpServletRequestWrapper {
		static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri"; //$NON-NLS-1$
		static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path"; //$NON-NLS-1$
		static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path"; //$NON-NLS-1$
		static final String INCLUDE_PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info"; //$NON-NLS-1$
		private boolean isRequestDispatcherInclude;

		public HttpServletRequestAdaptor(HttpServletRequest req) {
			super(req);
			isRequestDispatcherInclude = req.getAttribute(HttpServletRequestAdaptor.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
		}

		public String getServletPath() {
			if (isRequestDispatcherInclude)
				return super.getServletPath();

			String fullPath = super.getServletPath();
			return fullPath.substring(contextPath.length());
		}

		public String getContextPath() {
			if (isRequestDispatcherInclude)
				return super.getContextPath();

			return super.getContextPath() + contextPath;
		}

		public Object getAttribute(String attributeName) {
			if (isRequestDispatcherInclude) {
				if (attributeName.equals(HttpServletRequestAdaptor.INCLUDE_CONTEXT_PATH_ATTRIBUTE)) {
					String contextPathAttribute = (String) super.getAttribute(HttpServletRequestAdaptor.INCLUDE_CONTEXT_PATH_ATTRIBUTE);
					if (contextPathAttribute == null || contextPathAttribute.equals("/")) //$NON-NLS-1$
						return contextPath;

					return contextPathAttribute + contextPath;
				} else if (attributeName.equals(HttpServletRequestAdaptor.INCLUDE_SERVLET_PATH_ATTRIBUTE)) {
					String servletPath = (String) super.getAttribute(HttpServletRequestAdaptor.INCLUDE_SERVLET_PATH_ATTRIBUTE);
					return servletPath.substring(contextPath.length());
				}
			}
			return super.getAttribute(attributeName);
		}

		public RequestDispatcher getRequestDispatcher(String arg0) {
			return new RequestDispatcherAdaptor(super.getRequestDispatcher(contextPath + arg0));
		}
	}
	
	private class RequestDispatcherAdaptor implements RequestDispatcher {

		private RequestDispatcher requestDispatcher;
		public RequestDispatcherAdaptor(RequestDispatcher requestDispatcher) {
			this.requestDispatcher = requestDispatcher;
		}

		public void forward(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
			if (req instanceof HttpServletRequestAdaptor)
				req = ((HttpServletRequestAdaptor) req).getRequest();
			
			requestDispatcher.forward(req, resp);
		}

		public void include(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
			if (req instanceof HttpServletRequestAdaptor)
				req = ((HttpServletRequestAdaptor) req).getRequest();
			
			requestDispatcher.include(req, resp);
		}
	}
}
