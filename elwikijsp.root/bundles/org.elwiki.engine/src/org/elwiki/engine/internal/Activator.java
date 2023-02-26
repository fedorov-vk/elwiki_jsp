package org.elwiki.engine.internal;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final Logger log = Logger.getLogger(Activator.class);

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		log.debug(" ~~ START.");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
