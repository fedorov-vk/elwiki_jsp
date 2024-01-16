package org.elwiki.rss.internal;

import org.apache.wiki.api.cfgoptions.OptionBoolean;
import org.apache.wiki.api.cfgoptions.OptionInteger;
import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.OptionText;
import org.apache.wiki.api.cfgoptions.Options;
import org.osgi.framework.BundleContext;

public class RssGeneratorOptions extends Options {

	private static final String SERVLET_MAPPING = "RssGeneratorOptions_";

	private static final String PROP_RSS_GENERATE = "rss.generate";
	private static final String PROP_RSS_FILENAME = "rss.fileName";
	private static final String PROP_RSS_INTERVAL = "rss.interval";
	private static final String PROP_RSS_CHANNEL_DESCRIPTION = "rss.channelDescription";
	private static final String PROP_RSS_CHANNEL_LANGUAGE = "rss.channelLanguage";

	private OptionBoolean optRssGenerate;
	private OptionString optRssFilename;
	private OptionInteger optRssInterval;
	private OptionText optRssChannelDescription;
	private OptionString optRssChannelLanguage;

	public RssGeneratorOptions(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	protected String getServletMapping() {
		return SERVLET_MAPPING;
	}

	@Override
	protected String getPreferencesSection() {
		return "RSS generator";
	}
	
	@Override
	protected void populateOptions(BundleContext bundleContext) {
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
