package org.elwiki.rss.internal.bundle;

import org.eclipse.core.runtime.preferences.BundleDefaultsScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ActivatorRssGenerator implements BundleActivator {

	private static ActivatorRssGenerator pluginInstance;
	private BundleContext bundleContext;

	/**
	 * Constructor.
	 */
	public ActivatorRssGenerator() {
		super();
		pluginInstance = this;
	}
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		this.bundleContext = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance.
	 */
	public static ActivatorRssGenerator getDefault() {
		return pluginInstance;
	}

	public String getName() {
		return this.bundleContext.getBundle().getSymbolicName();
	}

	public IEclipsePreferences getDefaultPrefs() {
		String bundleName = getName();
		return BundleDefaultsScope.INSTANCE.getNode(bundleName);
	}

	public IEclipsePreferences getInstancePrefs() {
		String bundleName = getName();
		return InstanceScope.INSTANCE.getNode(bundleName);
	}

}
