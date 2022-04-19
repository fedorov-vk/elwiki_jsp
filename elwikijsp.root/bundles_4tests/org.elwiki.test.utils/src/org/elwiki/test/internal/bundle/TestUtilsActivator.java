package org.elwiki.test.internal.bundle;

import org.apache.log4j.Logger;
import org.elwiki.test.internal.users.UsersLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The activator class controls the plug-in life cycle.
 */
public class TestUtilsActivator implements BundleActivator {

	private static final Logger log = Logger.getLogger(TestUtilsActivator.class);

	// The plug-in ID
	static String PLUGIN_ID = "org.elwiki.test.utils";

	private static TestUtilsActivator pluginInstance;
	private static BundleContext bundleContext;

	// == CODE ================================================================

	/**
	 * Return this activator's singleton instance or null if it has not been started.
	 */
	public static TestUtilsActivator getDefault() {
		return TestUtilsActivator.pluginInstance;
	}

	public static BundleContext getContext() {
		return bundleContext;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		TestUtilsActivator.bundleContext = bundleContext;
		TestUtilsActivator.pluginInstance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

	@Deprecated
	private static String[] bundleState = new String[] { "X-state", //
			"UNINSTALLED", "INSTALLED", "RESOLVED", "STARTING", "STOPPING", "ACTIVE" };

	@Deprecated
	private void listBundles(BundleContext context) {
		for (Bundle bundle : context.getBundles()) {
			long id = bundle.getBundleId();
			String name = bundle.getSymbolicName();
			int state = bundle.getState();
			int idx = (int) (Math.log(state) / Math.log(2));
			idx = (idx > 5) ? 0 : idx + 1;
			System.out.printf("%-3d  %-10s  %s\n", id, bundleState[idx], name);
		}
	}

}