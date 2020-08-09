package org.elwiki.configuration.bundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author vfedorov
 */
public class ConfigurationActivator implements BundleActivator {

	public static String PLIGIN_ID = "org.elwiki.configuration";

	// The shared instance
	private static ConfigurationActivator instance;

	private static BundleContext context;

	// == CODE ================================================================

	/**
	 * Constructor.
	 */
	public ConfigurationActivator() {
		super();
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		ConfigurationActivator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		ConfigurationActivator.context = null;
	}

	public static BundleContext getContext() {
		return context;
	}

	public static ConfigurationActivator getDefault() {
		return instance;
	}
}
