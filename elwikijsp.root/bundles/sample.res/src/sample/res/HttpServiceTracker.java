package sample.res;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import org.osgi.service.http.HttpContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.eclipse.equinox.http.helper.BundleEntryHttpContext;
import org.eclipse.equinox.http.helper.ContextPathServletAdaptor;
import org.eclipse.equinox.jsp.jasper.JspServlet;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class HttpServiceTracker extends ServiceTracker {

	/**
	 * Created this.
	 * 
	 * @param context
	 */
	public HttpServiceTracker(BundleContext context) {
		super(context, HttpService.class.getName(), null);
	}

	static protected String webappContextRoot = "/";

	@Override
	public Object addingService(ServiceReference reference) {

		HttpService httpService = (HttpService) context.getService(reference);

		try {
			//	httpService.registerResources("/helloworld.html", "/helloworld.html", null); //$NON-NLS-1$
			//	httpService.registerServlet("/helloworld", new HelloWorldServlet(), null, null); //$NON-NLS-1$

			//HttpContext commonContext = new BundleEntryHttpContext(context.getBundle(), "/");

			/*
			//for JSPs under /WebContent
			Servlet jspServlet = (Servlet) new JspServlet(context.getBundle(), "/");
			//apply namespace for bundle
			jspServlet = new ContextPathServletAdaptor(jspServlet, webappContextRoot);
			
			httpService.registerServlet("/*.jsp", jspServlet, null, null);
			*/
			

			///////////////////////////
			
			HttpContext httpContext = new BundleEntryHttpContext(context.getBundle(), "/");
			httpService.registerServlet("/*.jsp", new JspServlet(context.getBundle(), "/"), null, httpContext);
//			HttpContext httpContext = new BundleEntryHttpContext(context.getBundle(), "/wc");
//			httpService.registerServlet("/*.jsp", new JspServlet(context.getBundle(), "/wc"), null, httpContext);

			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpService;
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		HttpService httpService = (HttpService) service;
		httpService.unregister("/helloworld.html"); //$NON-NLS-1$
		httpService.unregister("/helloworld"); //$NON-NLS-1$
		super.removedService(reference, service);
	}

}