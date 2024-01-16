package org.elwiki.authorize.internal.services;

import org.apache.wiki.api.cfgoptions.ButtonApply;
import org.apache.wiki.api.cfgoptions.ButtonRestoreDefault;
import org.apache.wiki.api.cfgoptions.Option;
import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.Options;
import org.apache.wiki.api.cfgoptions.OptionsJsonTracker;
import org.apache.wiki.api.core.Engine;
import org.osgi.framework.BundleContext;

public class AuthorizationManagerOptions extends Options {

	private static final String SERVLET_MAPPING = "AuthorizationManager";

	private static final String PROP_AUTHORIZER = "authorizer";

	private OptionString optAuthorizer;

	private ButtonRestoreDefault restoreDefaultButton;
	private ButtonApply applyButton;

	@Override
	public void initialize(BundleContext bundleContext, Engine engine) {
		jsonTracker = new OptionsJsonTracker(SERVLET_MAPPING, actions, engine);

		String infoAuthorizer = "Specifying the Authorizer.";
		optAuthorizer = new OptionString(bundleContext, PROP_AUTHORIZER, "Authorizer", infoAuthorizer, jsonTracker);
		options.add(optAuthorizer);
		actions.add(optAuthorizer);

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
"<h4>Authorization manager</h4>" +
textOptions + """
  <div class="form-group form-inline">
    <br/><span class="form-col-20 control-label"></span>""" +
textRestoreDefault +
textApply +
  "</div>";
//@formatter:on

		return result;
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
