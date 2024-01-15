package org.elwiki.rss.internal.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;

public class RssGeneratorOptionsImpl {

	private static final String PROP_RSS_GENERATE = "rss.generate";

	private final List<Option<?>> options = new ArrayList<>();
	private final List<ICallbackAction> actions = new ArrayList<>();

	private JSONtracker jsonTracker;

	private ButtonRestoreDefault restoreDefaultButton;
	private ButtonApply applyButton;

	private BooleanOption optRssGenerate;
	// private ButtonRestoreDefault actRestoreDefault;
	// private ButtonApply actCancel;

	public void initialize(Engine engine) {
		jsonTracker = new JSONtracker("RssGenerator", actions);
		WikiAjaxDispatcher wikiAjaxDispatcher = engine.getManager(WikiAjaxDispatcher.class);
		wikiAjaxDispatcher.registerServlet(jsonTracker);

		optRssGenerate = new BooleanOption(PROP_RSS_GENERATE, "RSS generate",
				"Determine if the RSS file should be generated at all.", jsonTracker);
		options.add(optRssGenerate);
		actions.add(optRssGenerate);

		restoreDefaultButton = new ButtonRestoreDefault(options, jsonTracker);
		applyButton = new ButtonApply(options, jsonTracker);
		actions.add(restoreDefaultButton);
		actions.add(applyButton);
	}

	public String getConfigurationJspPage() {
		String textOptions = "";
		for (Option option : options) {
			textOptions += option.getJsp();
		}

		String textRestoreDefault = restoreDefaultButton.getJsp();
		String textApply = applyButton.getJsp();

//@formatter:off
		String result =
"<h4>RSS generator</h4>" + 
textOptions + """
  <div class="form-group form-inline">
    <br/><span class="form-col-20 control-label"></span>""" +
textRestoreDefault +
textApply +
  "</div>";
//@formatter:on		

		return result;
	}

	public boolean isGenerateRss() {
		return optRssGenerate.getInstanceValue();
	}

}

class JSONtracker implements WikiAjaxServlet {

	private final String servletMapping;
	private final List<ICallbackAction> actions;

	public JSONtracker(String id, List<ICallbackAction> actions) {
		servletMapping = id + "_" + this.hashCode();
		this.actions = actions;
	}

	@Override
	public String getServletMapping() {
		return servletMapping;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response, String actionName,
			List<String> params) throws ServletException, IOException {
		for (ICallbackAction actions : actions) {
			if (actionName.equals(actions.getId()) && params.size() > 0) {
				actions.callBack(params.get(0));
				return;
			}
		}
	}

}
