package org.elwiki.authorize.internal.services;

import org.apache.wiki.api.cfgoptions.OptionBoolean;
import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.Options;
import org.apache.wiki.auth.AuthenticationManagerOptions;
import org.osgi.framework.BundleContext;

public class AuthenticationManagerOptionsImpl extends Options implements AuthenticationManagerOptions {

	private static final String SERVLET_MAPPING = "AuthenticationManager";

	/** If this property is <code>true</code>, allow cookies to be used to assert identities. */
	private static final String PROP_ALLOW_COOKIE_ASSERTIONS = "cookieAssertions";

	/** If this property is <code>true</code>, allow cookies to be used for authentication. */
	private static final String PROP_ALLOW_COOKIE_AUTH = "cookieAuthentication";

	/** Whether logins should be throttled to limit brute-forcing attempts. Defaults to true. */
	private static final String PROP_LOGIN_THROTTLING = "login.throttling";

	private OptionBoolean optCookieAssertions;
	private OptionBoolean optCookieAuthentication;
	private OptionBoolean optLoginThrottling;
	private OptionString optLoginModuleClass;

	public AuthenticationManagerOptionsImpl(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	protected String getServletMapping() {
		return SERVLET_MAPPING;
	}

	@Override
	protected String getPreferencesSection() {
		return "Authentication manager";
	}

	@Override
	protected void populateOptions(BundleContext bundleContext) {
		String infoCookieAssertions = """
				If this value is set to "true", then JSPWiki <br/>
				 will allow you to "assert" an identity using a cookie. <br/>
				It's still considered to be unsafe, <br/>
				 just like no login at all, but it is useful <br/>
				 when you have no need to force everyone to login. """;
		optCookieAssertions = new OptionBoolean(bundleContext, PROP_ALLOW_COOKIE_ASSERTIONS, "Cookie assertions",
				infoCookieAssertions, jsonTracker);
		options.add(optCookieAssertions);
		actions.add(optCookieAssertions);

		String infoCookieAuthentication = ":FVK:foo";
		optCookieAuthentication = new OptionBoolean(bundleContext, PROP_ALLOW_COOKIE_AUTH, "Cookie authentication",
				infoCookieAuthentication, jsonTracker);
		options.add(optCookieAuthentication);
		actions.add(optCookieAuthentication);

		String infoLoginThrottling = "Whether logins should be throttled to limit bruce-force attempts.";
		optLoginThrottling = new OptionBoolean(bundleContext, PROP_LOGIN_THROTTLING, "Login throttling",
				infoLoginThrottling, jsonTracker);
		options.add(optLoginThrottling);
		actions.add(optLoginThrottling);

		String infoLoginModuleClass = "Supply the JAAS LoginModule class used for custom authentication here.";
		optLoginModuleClass = new OptionString(bundleContext, PROP_LOGIN_MODULE_CLASS, "JAAS login class",
				infoLoginModuleClass, jsonTracker);
		options.add(optLoginModuleClass);
		actions.add(optLoginModuleClass);
	}

	@Override
	public boolean isCookieAssertions() {
		return optCookieAssertions.getInstanceValue();
	}

	@Override
	public boolean isCookieAuthentication() {
		return optCookieAuthentication.getInstanceValue();
	}

	@Override
	public boolean isLoginThrottling() {
		return optLoginThrottling.getInstanceValue();
	}

	@Override
	public String getLoginModuleClass() {
		return optLoginModuleClass.getInstanceValue();
	}

}
