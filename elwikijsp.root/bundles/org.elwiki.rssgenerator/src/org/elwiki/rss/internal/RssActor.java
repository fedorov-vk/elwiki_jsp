package org.elwiki.rss.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.apache.wiki.WatchDog;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.rss.RssGenerator;
import org.apache.wiki.util.FileUtil;
import org.elwiki.api.BackgroundThreads.Actor;

/**
 * Executed the RSS generation thread. FIXME: MUST be somewhere else, this is not a good place.
 */
public class RssActor extends Actor {

	private static final Logger log = Logger.getLogger(RssActor.class);

	private final Engine engine;
	private final File m_rssFile;
	private final RssGenerator m_generator;
	private WatchDog m_watchdog;

	/**
	 * Create a new RSS thread.
	 * 
	 * @param engine      A Engine to own this thread.
	 * @param rssFile     A File to write the RSS data to.
	 * @param rssInterval How often the RSS should be generated.
	 */
	public RssActor(Engine engine, File rssFile) {
		this.engine = engine;
		this.m_generator = engine.getManager(RssGenerator.class);
		this.m_rssFile = rssFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startupTask() {
		m_watchdog = WatchDog.getCurrentWatchDog(engine);
	}

	/**
	 * Runs the RSS generator thread.<br/>
	 * If a previous RSS generation operation encountered a file I/O or other error, this method will
	 * turn off generation. <code>false</code>.
	 * 
	 * @see java.lang.Thread#run()
	 * @throws Exception All exceptions are thrown upwards.
	 */
	@Override
	public void backgroundTask() throws Exception {
		if (m_generator.isEnabled()) {
			m_watchdog.enterState("Generating RSS feed", 60);
			final String feed = m_generator.generate();
			log.debug("Regenerating RSS feed to " + m_rssFile);

			// Generate RSS file, output it to default "rss.rdf".
			try (final Reader in = new StringReader(feed);
					final Writer out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(m_rssFile), StandardCharsets.UTF_8))) {
				FileUtil.copyContents(in, out);
			} catch (final IOException e) {
				log.error("Cannot generate RSS feed to " + m_rssFile.getAbsolutePath(), e);
				m_generator.setEnabled(false);
			} finally {
				m_watchdog.exitState();
			}
		}
	}

}
