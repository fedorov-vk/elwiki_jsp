/* 
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.  
 */
package org.elwiki.rss.internal;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.elwiki.api.event.WikiEngineEventTopic;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.rss.IFeed;
import org.apache.wiki.api.rss.RssGenerator;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.pages0.PageTimeComparator;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.util.TextUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.BackgroundThreads;
import org.elwiki.api.BackgroundThreads.Actor;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.api.component.WikiPrefs;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.permissions.PagePermission;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * Default implementation for {@link RssGenerator}.
 *
 * {@inheritDoc}
 */
// FIXME: Limit diff and page content size.
//@formatter:off
@Component(
	name = "elwiki.DefaultRssGenerator",
	service = { RssGenerator.class, WikiManager.class, EventHandler.class  },
	property = {
		EventConstants.EVENT_TOPIC + "=" + WikiEngineEventTopic.TOPIC_ENGINE_ALL,
	},
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultRssGenerator implements RssGenerator, WikiPrefs, EventHandler {

	private static final Logger log = Logger.getLogger(DefaultRssGenerator.class);

	/** The RSS file to generate. */
	private String m_rssFile;
	private String m_channelDescription = "";
	private String m_channelLanguage = "en-us";
	private boolean m_enabled = true;

	private static final int MAX_CHARACTERS = Integer.MAX_VALUE - 1;

    RssGeneratorOptions options;

	/**
	 * Constructs the RSS generator.
	 */
	public DefaultRssGenerator() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private Engine m_engine;

	/** Indicate enabling/disabling of RSSGenerator manager. */
	private boolean isRequiredRssGenerator = false;

	@Activate
	protected void startup(BundleContext bundleContext) {
		options = new RssGeneratorOptions(bundleContext);
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		options.initialize(m_engine);
		isRequiredRssGenerator = options.isGenerateRss();

		if (isRequiredRssGenerator) {
			m_rssFile = options.rssFilename();
			m_channelDescription = options.rssChannelDescription();
			m_channelLanguage = options.rssChannelLanguage();
		}
	}

	/**
	 * Starts the RSS generator & generator thread.
	 *
	 * @throws WikiException
	 */
	private void initializeStageTwo() throws WikiException {
		if (isRequiredRssGenerator) {
			final File rssFile;
			if (m_rssFile.startsWith(File.separator)) { // honor absolute pathnames
				rssFile = new File(m_rssFile);
			} else { // relative path names are anchored from the webapp root path
				rssFile = new File(m_engine.getRootPath(), m_rssFile);
			}
			int rssInterval = options.rssInterval();

			BackgroundThreads backgroundThreads = (BackgroundThreads) m_engine.getManager(BackgroundThreads.class);
			Actor rssActor = new RssActor(m_engine, rssFile);
			Thread rssThread = backgroundThreads.createThread("ElWiki RSS Generator", rssInterval, rssActor);
			rssThread.start();

			log.debug("RSS file will be at " + rssFile.getAbsolutePath());
			log.debug("RSS refresh interval (seconds): " + rssInterval);
		}
	}

	// -- OSGi service handling ------------------------(end)--

	private String getAuthor(final WikiPage page) {
		String author = page.getAuthor();
		if (author == null) {
			author = "An unknown author";
		}

		return author;
	}

	private String getAttachmentDescription(final PageAttachment att) {
		final String author = ":FVK:"; //:FVK: getAuthor( att );
		final StringBuilder sb = new StringBuilder();

		/*:FVK:
		if( att.getVersion() != 1 ) {
			//:FVK: sb.append( author ).append( " uploaded a new version of this attachment on " ).append( att.getLastModifiedDate() );
		} else {
			//:FVK: sb.append( author ).append( " created this attachment on " ).append( att.getLastModifiedDate() );
		}
		*/

		sb.append("<br /><hr /><br />").append("Parent page: <a href=\"")
				//:FVK: .append( m_engine.getURL( ContextEnum.PAGE_VIEW.getRequestContext(), att.getParentName(), null ) )
				//:FVK: .append( "\">" ).append( att.getParentName() ).append( "</a><br />" )
				.append("Info page: <a href=\"")
				.append(m_engine.getURL(ContextEnum.PAGE_INFO.getRequestContext(), att.getName(), null)).append("\">")
				.append(att.getName()).append("</a>");

		return sb.toString();
	}

	private String getPageDescription(final WikiPage page) {
		RenderingManager renderingManager = this.m_engine.getManager(RenderingManager.class);
		final StringBuilder buf = new StringBuilder();
		final String author = getAuthor(page);
		final WikiContext ctx = Wiki.context().create(m_engine, page);
		/*:FVK:
		if( page.getVersion() > 1 ) {
		    final String diff = WikiEngine.getDifferenceManager().getDiff( ctx,
		                                                        page.getVersion() - 1, // FIXME: Will fail when non-contiguous versions
		                                                                 page.getVersion() );
		
		    buf.append( author ).append( " changed this page on " ).append( page.getLastModifiedDate() ).append( ":<br /><hr /><br />" );
		    buf.append( diff );
		} else*/ {
			buf.append(author).append(" created this page on ").append(page.getLastModifiedDate())
					.append(":<br /><hr /><br />");
			buf.append(renderingManager.getHTML(page.getName()));
		}

		return buf.toString();
	}

	private String getEntryDescription(final WikiPage page) {
		final String res;
		if (page instanceof PageAttachment) {
			res = getAttachmentDescription((PageAttachment) page);
		} else {
			res = getPageDescription(page);
		}

		return res;
	}

	// FIXME: This should probably return something more intelligent
	private String getEntryTitle(final WikiPage page) {
		return ":FVK:"; //:FVK: page.getName() + ", version " + page.getVersion();
	}

	/** {@inheritDoc} */
	@Override
	public String generate() {
		final WikiContext context = Wiki.context().create(m_engine, Wiki.contents().page("__DUMMY"));
		context.setRequestContext(ContextEnum.PAGE_RSS.getRequestContext());
		final Feed feed = new RSS10Feed(context);
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + generateFullWikiRSS(context, feed);
	}

	/** {@inheritDoc} */
	@Override
	public String generateFeed(final WikiContext wikiContext, final List<WikiPage> changed, final String mode,
			final String type) throws IllegalArgumentException {
		final Feed feed;
		final String res;

		if (ATOM.equals(type)) {
			feed = new AtomFeed(wikiContext);
		} else if (RSS20.equals(type)) {
			feed = new RSS20Feed(wikiContext);
		} else {
			feed = new RSS10Feed(wikiContext);
		}

		feed.setMode(mode);

		if (MODE_BLOG.equals(mode)) {
			res = generateBlogRSS(wikiContext, changed, feed);
		} else if (MODE_FULL.equals(mode)) {
			res = generateFullWikiRSS(wikiContext, feed);
		} else if (MODE_WIKI.equals(mode)) {
			res = generateWikiPageRSS(wikiContext, changed, feed);
		} else {
			throw new IllegalArgumentException("Invalid value for feed mode: " + mode);
		}

		return res;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized boolean isEnabled() {
		return m_enabled;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void setEnabled(final boolean enabled) {
		m_enabled = enabled;
	}

	/** {@inheritDoc} */
	@Override
	public String getRssFile() {
		return m_rssFile;
	}

	/** {@inheritDoc} */
	@Override
	public String generateFullWikiRSS(final WikiContext wikiContext, final IFeed feed) {
		PageManager pageManager = this.m_engine.getManager(PageManager.class);
		ISessionMonitor sessionMonitor = this.m_engine.getManager(ISessionMonitor.class);
		AuthorizationManager authorizationManager = this.m_engine.getManager(AuthorizationManager.class);
		feed.setChannelTitle(m_engine.getManager(GlobalPreferences.class).getApplicationName());
		feed.setFeedURL(m_engine.getWikiConfiguration().getBaseURL());
		feed.setChannelLanguage(m_channelLanguage);
		feed.setChannelDescription(m_channelDescription);

		final Set<WikiPage> changed = pageManager.getRecentChanges();

		final Session session = sessionMonitor.createGuestSession(null);
		int items = 0;
		for (final Iterator<WikiPage> i = changed.iterator(); i.hasNext() && items < 15; items++) {
			final WikiPage page = i.next();

			//  Check if the anonymous user has view access to this page.
			if (!authorizationManager.checkPermission(session, new PagePermission(page, PagePermission.VIEW_ACTION))) {
				// No permission, skip to the next one.
				continue;
			}

			final String url;
			if (page instanceof PageAttachment) {
				url = m_engine.getURL(ContextEnum.ATTACHMENT_DOGET.getRequestContext(), page.getName(), null);
			} else {
				url = m_engine.getURL(ContextEnum.PAGE_VIEW.getRequestContext(), page.getName(), null);
			}

			final Entry e = new Entry();
			e.setPage(page);
			e.setURL(url);
			e.setTitle(page.getName());
			e.setContent(getEntryDescription(page));
			e.setAuthor(getAuthor(page));

			feed.addEntry(e);
		}

		return feed.getString();
	}

	/** {@inheritDoc} */
	@Override
	public String generateWikiPageRSS(final WikiContext wikiContext, final List<WikiPage> changed, final IFeed feed) {
		VariableManager variableManager = this.m_engine.getManager(VariableManager.class);
		feed.setChannelTitle(
				m_engine.getManager(GlobalPreferences.class).getApplicationName() + ": " + wikiContext.getPage().getName());
		feed.setFeedURL(wikiContext.getViewURL(wikiContext.getPage().getName()));
		final String language = variableManager.getVariable(wikiContext, PROP_CHANNEL_LANGUAGE);

		if (language != null) {
			feed.setChannelLanguage(language);
		} else {
			feed.setChannelLanguage(m_channelLanguage);
		}
		final String channelDescription = variableManager.getVariable(wikiContext, PROP_CHANNEL_DESCRIPTION);

		if (channelDescription != null) {
			feed.setChannelDescription(channelDescription);
		}

		changed.sort(new PageTimeComparator());

		int items = 0;
		for (final Iterator<WikiPage> i = changed.iterator(); i.hasNext() && items < 15; items++) {
			final WikiPage page = i.next();
			final Entry e = new Entry();
			e.setPage(page);
			String url;

			if (page instanceof PageAttachment) {
				url = ":FVK:"; //:FVK: m_engine.getURL( ContextEnum.PAGE_ATTACH.getRequestContext(), page.getName(), "version=" + page.getVersion() );
			} else {
				url = ":FVK:"; //:FVK: m_engine.getURL( ContextEnum.PAGE_VIEW.getRequestContext(), page.getName(), "version=" + page.getVersion() );
			}

			// Unfortunately, this is needed because the code will again go through replacement conversion
			url = TextUtil.replaceString(url, "&amp;", "&");
			e.setURL(url);
			e.setTitle(getEntryTitle(page));
			e.setContent(getEntryDescription(page));
			e.setAuthor(getAuthor(page));

			feed.addEntry(e);
		}

		return feed.getString();
	}

	/** {@inheritDoc} */
	@Override
	public String generateBlogRSS(final WikiContext wikiContext, final List<WikiPage> changed, final IFeed feed) {
		VariableManager variableManager = this.m_engine.getManager(VariableManager.class);
		PageManager pageManager = this.m_engine.getManager(PageManager.class);
		RenderingManager renderingManager = this.m_engine.getManager(RenderingManager.class);
		if (log.isDebugEnabled()) {
			log.debug("Generating RSS for blog, size=" + changed.size());
		}

		final String ctitle = variableManager.getVariable(wikiContext, PROP_CHANNEL_TITLE);
		if (ctitle != null) {
			feed.setChannelTitle(ctitle);
		} else {
			feed.setChannelTitle(
					m_engine.getManager(GlobalPreferences.class).getApplicationName() + ":" + wikiContext.getPage().getName());
		}

		feed.setFeedURL(wikiContext.getViewURL(wikiContext.getPage().getName()));

		final String language = variableManager.getVariable(wikiContext, PROP_CHANNEL_LANGUAGE);
		if (language != null) {
			feed.setChannelLanguage(language);
		} else {
			feed.setChannelLanguage(m_channelLanguage);
		}

		final String channelDescription = variableManager.getVariable(wikiContext, PROP_CHANNEL_DESCRIPTION);
		if (channelDescription != null) {
			feed.setChannelDescription(channelDescription);
		}

		changed.sort(new PageTimeComparator());

		int items = 0;
		for (final Iterator<WikiPage> i = changed.iterator(); i.hasNext() && items < 15; items++) {
			final WikiPage page = i.next();
			final Entry e = new Entry();
			e.setPage(page);
			final String url;

			if (page instanceof PageAttachment) {
				url = m_engine.getURL(ContextEnum.ATTACHMENT_DOGET.getRequestContext(), page.getName(), null);
			} else {
				url = m_engine.getURL(ContextEnum.PAGE_VIEW.getRequestContext(), page.getName(), null);
			}

			e.setURL(url);

			//  Title
			String pageText = pageManager.getPureText(page, WikiProvider.LATEST_VERSION);

			String title = "";
			final int firstLine = pageText.indexOf('\n');

			if (firstLine > 0) {
				title = pageText.substring(0, firstLine).trim();
			}

			if (title.length() == 0) {
				title = page.getName();
			}

			// Remove wiki formatting
			while (title.startsWith("!")) {
				title = title.substring(1);
			}

			e.setTitle(title);

			//  Description
			if (firstLine > 0) {
				int maxlen = pageText.length();
				if (maxlen > MAX_CHARACTERS) {
					maxlen = MAX_CHARACTERS;
				}
				pageText = renderingManager.textToHTML(wikiContext, pageText.substring(firstLine + 1, maxlen).trim());
				if (maxlen == MAX_CHARACTERS) {
					pageText += "...";
				}
				e.setContent(pageText);
			} else {
				e.setContent(title);
			}
			e.setAuthor(getAuthor(page));
			feed.addEntry(e);
		}

		return feed.getString();
	}

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		switch (topic) {
		// Initialize.
		case WikiEngineEventTopic.TOPIC_ENGINE_INIT_STAGE_TWO:
			try {
				initializeStageTwo();
			} catch (WikiException e) {
				log.error("Failed initialization of DefaultRSSGeneratorManager.", e);
			}
			break;
		}
	}

	@Override
	public String getConfigurationEntry() {
		String jspItems = options.getConfigurationJspPage();
		return jspItems;
	}

}
