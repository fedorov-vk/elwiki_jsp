package org.elwiki.rss.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.cfgoptions.ButtonApply;
import org.apache.wiki.api.cfgoptions.ButtonRestoreDefault;
import org.apache.wiki.api.cfgoptions.ICallbackAction;
import org.apache.wiki.api.cfgoptions.Option;
import org.apache.wiki.api.cfgoptions.OptionBoolean;
import org.apache.wiki.api.cfgoptions.OptionInteger;
import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.OptionText;
import org.apache.wiki.api.cfgoptions.Options;
import org.apache.wiki.api.cfgoptions.OptionsJsonTracker;
import org.apache.wiki.api.core.Engine;
import org.osgi.framework.BundleContext;

public class RssGeneratorOptions extends Options {

	private static final String SERVLET_MAPPING = "RssGeneratorOptions_";

	private static final String PROP_RSS_GENERATE = "rss.generate";
	private static final String PROP_RSS_FILENAME = "rss.fileName";
	private static final String PROP_RSS_INTERVAL = "rss.interval";
	private static final String PROP_RSS_CHANNEL_DESCRIPTION = "rss.channelDescription";
	private static final String PROP_RSS_CHANNEL_LANGUAGE = "rss.channelLanguage";

	private final List<Option<?>> options = new ArrayList<>();
	private final List<ICallbackAction> actions = new ArrayList<>();

	private WikiAjaxServlet jsonTracker;

	private OptionBoolean optRssGenerate;
	private OptionString optRssFilename;
	private OptionInteger optRssInterval;
	private OptionText optRssChannelDescription;
	private OptionString optRssChannelLanguage;

	private ButtonRestoreDefault restoreDefaultButton;
	private ButtonApply applyButton;

	@Override
	public void initialize(BundleContext bundleContext, Engine engine) {
		jsonTracker = new OptionsJsonTracker(SERVLET_MAPPING, actions, engine);

		optRssGenerate = new OptionBoolean(bundleContext, PROP_RSS_GENERATE, "RSS generate",
				"Determine if the RSS file should be generated at all.", jsonTracker);
		options.add(optRssGenerate);
		actions.add(optRssGenerate);

		optRssFilename = new OptionString(bundleContext, PROP_RSS_FILENAME, "RSS filename",
				"Determine the name of the RSS file.", jsonTracker);
		options.add(optRssFilename);
		actions.add(optRssFilename);

		optRssInterval = new OptionInteger(bundleContext, PROP_RSS_INTERVAL, "RSS interval",
				"Determine the refresh interval in seconds.", jsonTracker);
		options.add(optRssInterval);
		actions.add(optRssInterval);

		optRssChannelDescription = new OptionText(bundleContext, PROP_RSS_CHANNEL_DESCRIPTION, "RSS channel desription",
				"Text shown for \"channel description\".", jsonTracker);
		options.add(optRssChannelDescription);
		actions.add(optRssChannelDescription);

		optRssChannelLanguage = new OptionString(bundleContext, PROP_RSS_CHANNEL_LANGUAGE, "RSS channel language",
				"The language of your Wiki.", jsonTracker);
		options.add(optRssChannelLanguage);
		actions.add(optRssChannelLanguage);

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
