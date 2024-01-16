package org.elwiki.authorize.internal.services;

import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.Options;
import org.osgi.framework.BundleContext;

public class AuthorizationManagerOptions extends Options {

	private static final String SERVLET_MAPPING = "AuthorizationManager";

	private static final String PROP_AUTHORIZER = "authorizer";

	private OptionString optAuthorizer;

	public AuthorizationManagerOptions(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	protected String getServletMapping() {
		return SERVLET_MAPPING;
	}

	@Override
	protected String getPreferencesSection() {
		return "Authorization manager";
	}

	@Override
	protected void populateOptions(BundleContext bundleContext) {
		String infoAuthorizer = "Specifying the Authorizer.";
		optAuthorizer = new OptionString(bundleContext, PROP_AUTHORIZER, "Authorizer", infoAuthorizer, jsonTracker);
		options.add(optAuthorizer);
		actions.add(optAuthorizer);
	}

	/**
	 * Returns ID of Authorizer.
	 *
	 * @return
	 */
	public String getAuthorizer() {
		return optAuthorizer.getInstanceValue();
	}

	public String getAuthorizerKey() {
		return PROP_AUTHORIZER;
	}

}
