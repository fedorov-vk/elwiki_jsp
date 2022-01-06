package org.elwiki.resources;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ResourcesActivator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		ResourcesActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		ResourcesActivator.context = null;
	}

	public static <T> T getService(Class<T> clazz) {
		BundleContext bundleContext = ResourcesActivator.getContext();
		ServiceReference<T> ref = context.getServiceReference(clazz);
		if (ref != null) {
			return bundleContext.getService(ref);
		}
		return null;
	}

}