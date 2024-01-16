package org.apache.wiki.api.cfgoptions;

import java.util.ArrayList;
import java.util.List;

import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;
import org.osgi.framework.BundleContext;

public abstract class Options {

	protected final List<Option<?>> options = new ArrayList<>();
	protected final List<ICallbackAction> actions = new ArrayList<>();

	protected WikiAjaxServlet jsonTracker;

	private ButtonRestoreDefault restoreDefaultButton;
	private ButtonApply applyButton;

	public Options(BundleContext bundleContext) {
		jsonTracker = new OptionsJsonTracker(getServletMapping(), actions);
		populateOptions(bundleContext);
	}

	protected abstract String getServletMapping();

	protected abstract String getPreferencesSection();

	protected abstract void populateOptions(BundleContext bundleContext);

	public void initialize(Engine engine) {
		WikiAjaxDispatcher wikiAjaxDispatcher = engine.getManager(WikiAjaxDispatcher.class);
		wikiAjaxDispatcher.registerServlet(jsonTracker);

		restoreDefaultButton = new ButtonRestoreDefault(options, jsonTracker);
		actions.add(restoreDefaultButton);

		applyButton = new ButtonApply(options, jsonTracker);
		actions.add(applyButton);
	}

	public String getConfigurationJspPage() {
		String textOptions = "";
		for (Option<?> option : options) {
			textOptions += "\n" + option.getJsp();
		}

		String textRestoreDefault = restoreDefaultButton.getJsp();
		String textApply = applyButton.getJsp();

//@formatter:off
		String result =
"<h4>" + getPreferencesSection() + "</h4>" +
textOptions + """
  <div class="form-group form-inline">
    <br/><span class="form-col-20 control-label"></span>""" +
textRestoreDefault +
textApply +
  "</div>";
//@formatter:on

		return result;
	}

}
