package org.elwiki.plugins.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PluginsActivator implements BundleActivator {

	private static BundleContext bundleContext;

	public static BundleContext getContext() {
		return bundleContext;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		PluginsActivator.bundleContext = bundleContext;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		PluginsActivator.bundleContext = null;
	}

}
