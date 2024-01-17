package org.apache.wiki.pages;

import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.Options;
import org.osgi.framework.BundleContext;

public class PageManagerOptions extends Options {

	private static final String SERVLET_MAPPING = "PageManager";

	private static final String PROP_PAGE_MANAGER = "pageManager";

	private OptionString optPageManager;

	public PageManagerOptions(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	protected String getServletMapping() {
		return SERVLET_MAPPING;
	}

	@Override
	protected String getPreferencesSectionName() {
		return "Page manager";
	}

	@Override
	protected void populateOptions(BundleContext bundleContext) {
		String infoAuthorizer = "Specifying ID of the Page Manager.";
		optPageManager = new OptionString(bundleContext, PROP_PAGE_MANAGER, "Page manager", infoAuthorizer,
				jsonTracker);
		options.add(optPageManager);
		actions.add(optPageManager);
	}

	/**
	 * Returns ID of PageManager.
	 *
	 * @return
	 */
	public String getPageManager() {
		return optPageManager.getInstanceValue();
	}

	public String getPageManagerKey() {
		return PROP_PAGE_MANAGER;
	}

}
