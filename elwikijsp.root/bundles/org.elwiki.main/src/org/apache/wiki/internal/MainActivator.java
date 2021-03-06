package org.apache.wiki.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class MainActivator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		MainActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		MainActivator.context = null;
	}

	@Deprecated
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