package org.elwiki.rss.internal.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.cfgoptions.ButtonApply;
import org.apache.wiki.api.cfgoptions.ButtonRestoreDefault;
import org.apache.wiki.api.cfgoptions.ICallbackAction;
import org.apache.wiki.api.cfgoptions.Option;
import org.apache.wiki.api.cfgoptions.OptionBoolean;
import org.apache.wiki.api.cfgoptions.OptionInteger;
import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.OptionText;
import org.apache.wiki.api.core.Engine;
import org.osgi.framework.BundleContext;

public class RssGeneratorOptionsImpl {

	private static final String PROP_RSS_GENERATE = "rss.generate";
	private static final String PROP_RSS_FILENAME = "rss.fileName";
	private static final String PROP_RSS_INTERVAL = "rss.interval";
	private static final String PROP_RSS_CHANNEL_DESCRIPTION = "rss.channelDescription";
	private static final String PROP_RSS_CHANNEL_LANGUAGE = "rss.channelLanguage";

	private final List<Option<?>> options = new ArrayList<>();
	private final List<ICallbackAction> actions = new ArrayList<>();

	private JSONtracker jsonTracker;

	private ButtonRestoreDefault restoreDefaultButton;
	private ButtonApply applyButton;

	private OptionBoolean optRssGenerate;
	private OptionString optRssFilename;
	private OptionInteger optRssInterval;
	private OptionText optRssChannelDescription;
	private OptionString optRssChannelLanguage;

	public void initialize(BundleContext bundleContext, Engine engine) {
		jsonTracker = new JSONtracker("RssGenerator", actions);
		WikiAjaxDispatcher wikiAjaxDispatcher = engine.getManager(WikiAjaxDispatcher.class);
		wikiAjaxDispatcher.registerServlet(jsonTracker);

		optRssGenerate = new OptionBoolean(bundleContext, PROP_RSS_GENERATE,
				"RSS generate", "Determine if the RSS file should be generated at all.", jsonTracker);
		options.add(optRssGenerate);
		actions.add(optRssGenerate);

		optRssFilename = new OptionString(bundleContext, PROP_RSS_FILENAME, "RSS filename",
				"Determine the name of the RSS file.", jsonTracker);
		options.add(optRssFilename);
		actions.add(optRssFilename);

		optRssInterval = new OptionInteger(bundleContext, PROP_RSS_INTERVAL,
				"RSS interval", "Determine the refresh interval in seconds.", jsonTracker);
		options.add(optRssInterval);
		actions.add(optRssInterval);

		optRssChannelDescription = new OptionText(bundleContext, PROP_RSS_CHANNEL_DESCRIPTION,
				"RSS channel desription", "Text shown for \"channel description\".", jsonTracker);
		options.add(optRssChannelDescription);
		actions.add(optRssChannelDescription);

		optRssChannelLanguage = new OptionString(bundleContext, PROP_RSS_CHANNEL_LANGUAGE,
				"RSS channel language", "The language of your Wiki.", jsonTracker);
		options.add(optRssChannelLanguage);
		actions.add(optRssChannelLanguage);

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

	/**
	 * Returns value of option 'rss.generate'.
	 *
	 * @return
	 */
	public boolean isGenerateRss() {
		return optRssGenerate.getInstanceValue();
	}

	/**
	 * Returns value of option 'rss.fileName'.
	 *
	 * @return
	 */
	public String rssFilename() {
		return optRssFilename.getInstanceValue();
	}

	/**
	 * Returns value of option 'rss.interval'.
	 *
	 * @return
	 */
	public Integer rssInterval() {
		return optRssInterval.getInstanceValue();
	}

	/**
	 * Returns value of option 'rss.channelDescription'.
	 *
	 * @return
	 */
	public String rssChannelDescription() {
		return optRssChannelDescription.getInstanceValue();
	}

	/**
	 * Returns value of option 'rss.channelLanguage'.
	 *
	 * @return
	 */
	public String rssChannelLanguage() {
		return optRssChannelLanguage.getInstanceValue();
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
		String parameter = String.join(", ", params); // :FVK: workaround, due to
													  // WikiAjaxDispatcherServlet.performAction split by ','
		for (ICallbackAction actions : actions) {
			if (actionName.equals(actions.getId())) {
				actions.callBack(parameter);
				return;
			}
		}
	}

}
