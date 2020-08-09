package org.elwiki.authorize.context.asserted;

import java.security.Permission;
import java.util.function.Function;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AssertedContextActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.elwiki.authorize.context.asserted"; //$NON-NLS-1$

	// The shared instance
	private static AssertedContextActivator pluginInstance;

	// == CODE ================================================================

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static AssertedContextActivator getDefault() {
		return pluginInstance;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		pluginInstance = this;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		pluginInstance = null;
		super.stop(bundleContext);
	}

	public static Boolean test2(Function<Permission, Boolean> function, Permission permission) {
		return function.apply(permission);
	}

}