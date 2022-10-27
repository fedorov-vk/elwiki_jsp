package org.elwiki.manager.wikiscope.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ScopeActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		ScopeActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		ScopeActivator.context = null;
	}

}
