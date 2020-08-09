package org.elwiki.resources;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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

}
