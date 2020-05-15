package sample.http;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

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

	@Override
	public Object addingService(ServiceReference reference) {
		HttpService httpService = (HttpService) context.getService(reference);
		try {
			httpService.registerResources("/helloworld.html", "/helloworld.html", null); //$NON-NLS-1$
			httpService.registerServlet("/helloworld", new HelloWorldServlet(), null, null); //$NON-NLS-1$
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