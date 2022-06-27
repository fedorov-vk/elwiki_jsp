package org.elwiki.ajax_dispatcher.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class WikiAjaxActivator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		WikiAjaxActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		WikiAjaxActivator.context = null;
	}

}
