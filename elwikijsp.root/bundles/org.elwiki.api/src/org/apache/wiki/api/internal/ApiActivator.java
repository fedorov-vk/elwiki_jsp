package org.apache.wiki.api.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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

}