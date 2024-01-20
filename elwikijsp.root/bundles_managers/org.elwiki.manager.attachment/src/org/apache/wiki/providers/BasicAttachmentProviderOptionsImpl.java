package org.apache.wiki.providers;

import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.Options;
import org.osgi.framework.BundleContext;

public class BasicAttachmentProviderOptionsImpl extends Options {

	private static final String SERVLET_MAPPING = "BasicAttachmentProvider";

	private static final String PROP_DISABLE_CACHE = "disableCache";

	private OptionString optDisableCache;

	public BasicAttachmentProviderOptionsImpl(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	protected String getServletMapping() {
		return SERVLET_MAPPING;
	}

	@Override
	protected String getPreferencesSectionName() {
		return "Basic Attachment Provider";
	}

	@Override
	protected void populateOptions(BundleContext bundleContext) {
		optDisableCache = new OptionString(bundleContext, PROP_DISABLE_CACHE, "Disable cache",
				"", jsonTracker);
		options.add(optDisableCache);
		actions.add(optDisableCache);
	}
	
	public String getDisableCache() {
		return optDisableCache.getInstanceValue();
	}

}
