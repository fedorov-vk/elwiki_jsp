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
import org.elwiki.web.support.ErrorHandlingServlet;
import org.osgi.framework.Bundle;

/**
 * Prototype of this class is code of Equinox: <a href=
 * "http://kickjava.com/src/org/eclipse/equinox/http/registry/internal/ServletManager.java.htm">ServletManager</a>
 * and <a href=
 * "http://kickjava.com/src/org/eclipse/equinox/jsp/jasper/registry/JSPFactory.java.htm">JSPFactory</a>.
 *
 * @author v.fedorov
 */
public abstract class JspServletWrapper implements Servlet {

	private static final Logger log = Logger.getLogger(JspServletWrapper.class);

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
			log.error("Error of delegate.service()", e);

			// :FVK: TODO: review workaround.
			Object obj = request.getAttribute(ErrorHandlingServlet.ATTR_ELWIKI_ERROR_EXCEPTION);
			if (obj == null) {
				request.setAttribute(ErrorHandlingServlet.ATTR_ELWIKI_ERROR_EXCEPTION, e);
			}
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
			Bundle bundle = ResourcesActivator.getContext().getBundle();
			String bundleResourcePath = null;
			try {
				JspServlet newDelegate = new JspServlet(bundle, bundleResourcePath, getAlias());
				newDelegate.init(this.config);
				newDelegate.init(this.config);
				delegate = newDelegate;
			} catch (Exception e) { //TODO: add support exception logging.
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
