package org.elwiki.manager.importer.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ImporterActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		ImporterActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		ImporterActivator.context = null;
	}

}
