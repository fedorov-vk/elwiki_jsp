package org.elwiki.authorize.internal.bundle;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AuthorizePluginActivator extends Plugin {

	private static final Logger log = Logger.getLogger(AuthorizePluginActivator.class);

	// The shared instance
	private static AuthorizePluginActivator pluginInstance;

	/** :FVK: Old JSPwiki = "org.apache.wiki.auth.authorize" */
	public static String AUTHORIZATION_PACKAGE = "org.elwiki.authorize";

	// == CODE ================================================================

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		AuthorizePluginActivator.pluginInstance = this;

//		Job job = Job.create("Update permissions", (ICoreRunnable) monitor -> {
//			initContextPermissions();
//		});
//		job.schedule();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		pluginInstance = null;
		super.stop(bundleContext);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static AuthorizePluginActivator getDefault() {
		return AuthorizePluginActivator.pluginInstance;
	}

	public String getName() {
		return getBundle().getSymbolicName();
	}

}
