package org.apache.wiki.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class MainActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		MainActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		MainActivator.context = null;
	}

}
