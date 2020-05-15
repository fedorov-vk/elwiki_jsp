package sample.http;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private HttpServiceTracker httpServiceTracker;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		httpServiceTracker = new HttpServiceTracker(context);
		httpServiceTracker.open();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		if (httpServiceTracker != null) {
			httpServiceTracker.close();
		}
		httpServiceTracker = null;
	}

}