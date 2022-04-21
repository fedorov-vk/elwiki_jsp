package org.elwiki.web.jsp;
//:FVK: класс - отладка при разработке. - так же файл "MyInsert.jsp" 

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.eclipse.equinox.jsp.jasper.JspServlet;
import org.elwiki.resources.ResourcesActivator;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

//@formatter:off
@Component(
    service=Servlet.class,
    property= {
    	"osgi.http.whiteboard.servlet.pattern=/*",
    	"osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=eclipse)"
    },
    scope=ServiceScope.PROTOTYPE,
    name="partJspServlet")
//@formatter:on
public class JspServletPart implements Servlet {

	private static String ALIAS = "/";

	private Servlet delegate;
	private ServletConfig config;
	private boolean loadOnStartup = false;

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
		delegate.service(request, response);
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
				JspServlet newDelegate = new JspServlet(b, bundleResourcePath, ALIAS);
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
