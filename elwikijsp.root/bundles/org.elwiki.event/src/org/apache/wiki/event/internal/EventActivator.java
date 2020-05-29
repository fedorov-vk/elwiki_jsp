package org.apache.wiki.event.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class EventActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		EventActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		EventActivator.context = null;
	}

}
