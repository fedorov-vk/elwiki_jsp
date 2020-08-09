package org.apache.wiki.ui.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class EditorActivator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		EditorActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		EditorActivator.context = null;
	}

}
