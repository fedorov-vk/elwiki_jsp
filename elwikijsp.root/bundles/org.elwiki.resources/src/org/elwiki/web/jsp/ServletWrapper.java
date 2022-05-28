package org.elwiki.web.jsp;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.equinox.jsp.jasper.JspServlet;
import org.elwiki.resources.ResourcesActivator;
import org.osgi.framework.Bundle;

/**
 * The idea was taken from code of Equinox: <a href=
 * "http://kickjava.com/src/org/eclipse/equinox/http/registry/internal/ServletManager.java.htm">ServletManager</a>
 * and <a href=
 * "http://kickjava.com/src/org/eclipse/equinox/jsp/jasper/registry/JSPFactory.java.htm">JSPFactory</a>.
 * 
 * @author v.fedorov
 */
public abstract class ServletWrapper implements Servlet {

	private static final Logger log = Logger.getLogger(ServletWrapper.class);

	private boolean loadOnStartup = false;
	private ServletConfig config;
	private Servlet delegate;

	abstract protected String getAlias();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		if (loadOnStartup) {
			initializeDelegate();
		}
	}

	@Override
	public ServletConfig getServletConfig() {
		return config;
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		initializeDelegate();

		try {
			delegate.service(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Ошибка delegate.service()", e);
		}
	}

	@Override
	public String getServletInfo() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public void destroy() {
		destroyDelegate();
	}

	private synchronized void initializeDelegate() throws ServletException {
		if (delegate == null) {
			Bundle b = ResourcesActivator.getContext().getBundle();
			String bundleResourcePath = null;
			try {
				JspServlet newDelegate = new JspServlet(b, bundleResourcePath, getAlias());
				newDelegate.init(config);
				delegate = newDelegate;
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}

	private synchronized void destroyDelegate() {
		if (delegate != null) {
			Servlet doomedDelegate = delegate;
			delegate = null;
			doomedDelegate.destroy();
		}
	}

}
