package org.elwiki.preferences.ui.internal.bundle;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PluginActivator implements BundleActivator {

	// The shared instance
	private static PluginActivator instance;

	private static BundleContext context;

	private ExtensionTracker extensionTracker;

	// == CODE ================================================================

	public static BundleContext getContext() {
		return context;
	}

	public static PluginActivator getDefault() {
		return instance;
	}

	/**
	 * Constructor.
	 */
	public PluginActivator() {
		super();
		instance = this;
	}

	public void start(BundleContext bundleContext) throws Exception {
		PluginActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		PluginActivator.context = null;
	}

	public ExtensionTracker getExtensionTracker() {
		if (extensionTracker == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			extensionTracker = new ExtensionTracker(registry);
		}
		return extensionTracker;
	}

}
