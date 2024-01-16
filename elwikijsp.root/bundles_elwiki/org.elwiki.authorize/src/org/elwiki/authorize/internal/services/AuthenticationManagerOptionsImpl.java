package org.elwiki.authorize.internal.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.cfgoptions.ButtonApply;
import org.apache.wiki.api.cfgoptions.ButtonRestoreDefault;
import org.apache.wiki.api.cfgoptions.ICallbackAction;
import org.apache.wiki.api.cfgoptions.Option;
import org.apache.wiki.api.cfgoptions.OptionBoolean;
import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.Options;
import org.apache.wiki.api.cfgoptions.OptionsJsonTracker;
import org.apache.wiki.api.core.Engine;
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

	private final List<Option<?>> options = new ArrayList<>();
	private final List<ICallbackAction> actions = new ArrayList<>();

	private WikiAjaxServlet jsonTracker;

	private OptionBoolean optCookieAssertions;
	private OptionBoolean optCookieAuthentication;
	private OptionBoolean optLoginThrottling;
	private OptionString optLoginModuleClass;

	private ButtonRestoreDefault restoreDefaultButton;
	private ButtonApply applyButton;


	@Override
	public void initialize(BundleContext bundleContext, Engine engine) {
		jsonTracker = new OptionsJsonTracker(SERVLET_MAPPING, actions, engine);

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

		restoreDefaultButton = new ButtonRestoreDefault(options, jsonTracker);
		actions.add(restoreDefaultButton);

		applyButton = new ButtonApply(options, jsonTracker);
		actions.add(applyButton);
	}

	@Override
	public String getConfigurationJspPage() {
		String textOptions = "";
		for (Option<?> option : options) {
			textOptions += "\n" + option.getJsp();
		}

		String textRestoreDefault = restoreDefaultButton.getJsp();
		String textApply = applyButton.getJsp();

//@formatter:off
		String result =
"<h4>Authentication manager</h4>" +
textOptions + """
  <div class="form-group form-inline">
    <br/><span class="form-col-20 control-label"></span>""" +
textRestoreDefault +
textApply +
  "</div>";
//@formatter:on

		return result;
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
