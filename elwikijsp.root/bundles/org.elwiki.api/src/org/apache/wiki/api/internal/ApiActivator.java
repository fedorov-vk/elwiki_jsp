package org.apache.wiki.api.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ApiActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		ApiActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		ApiActivator.context = null;
	}

	public static <T> T getService(Class<T> clazz) {
		Bundle bundle = context.getBundle();
		if (bundle != null) {
			ServiceTracker<T, T> st = new ServiceTracker<T, T>(bundle.getBundleContext(), clazz, null);
			st.open();
			if (st != null) {
				try {
					return st.waitForService(1500L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}