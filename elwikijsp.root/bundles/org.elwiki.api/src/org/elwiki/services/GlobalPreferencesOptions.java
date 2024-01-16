package org.elwiki.services;

import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.Options;
import org.osgi.framework.BundleContext;

public class GlobalPreferencesOptions extends Options {

	private static final String SERVLET_MAPPING = "PreferencesGlobal";

	private static final String PROP_APP_NAME = "applicationName";

	private OptionString optAppName;

	public GlobalPreferencesOptions(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	protected String getServletMapping() {
		return SERVLET_MAPPING;
	}

	@Override
	protected String getPreferencesSection() {
		return "Global preferences";
	}

	@Override
	protected void populateOptions(BundleContext bundleContext) {
		String infoAppName = "Specifying the Application name.";
		optAppName = new OptionString(bundleContext, PROP_APP_NAME, "Application name", infoAppName, jsonTracker);
		options.add(optAppName);
		actions.add(optAppName);
	}

	public String getApplicationName() {
		return optAppName.getInstanceValue();
	}

}
