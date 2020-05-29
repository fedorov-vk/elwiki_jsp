package org.apache.wiki.util.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class UtilActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		UtilActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		UtilActivator.context = null;
	}

}
