package org.apache.wiki.search.lucene;

import org.apache.wiki.InternalWikiException;
import org.apache.wiki.WatchDog;
import org.apache.wiki.api.core.Engine;
import org.elwiki.api.BackgroundThreads.Actor;
import org.elwiki_data.WikiPage;

/**
 * Updater thread that updates Lucene indexes.
 */
public class LuceneUpdater extends Actor {

	protected static final int INDEX_DELAY = 5;
	protected static final int INITIAL_DELAY = 60;

	private final LuceneSearchProvider m_provider;
	private int m_initialDelay;
	private WatchDog m_watchdog;
	private Engine engine;

	/**
	 * @param engine
	 * @param provider
	 * @param initialDelay
	 * 
	 */
	public LuceneUpdater(Engine engine, LuceneSearchProvider provider, int initialDelay) {
		this.engine = engine;
		m_provider = provider;
		m_initialDelay = initialDelay;
	}

	private Engine getEngine() {
		return this.engine;
	}

	public void startupTask() throws Exception {
		m_watchdog = WatchDog.getCurrentWatchDog(getEngine());

		// Sleep initially...
		try {
			Thread.sleep(m_initialDelay * 1000L);
		} catch (final InterruptedException e) {
			throw new InternalWikiException("Interrupted while waiting to start.", e);
		}

		m_watchdog.enterState("Full reindex");
		// Reindex everything
		m_provider.doFullLuceneReindex();
		m_watchdog.exitState();
	}

	public void backgroundTask() throws Exception {
		//:FVK: m_watchdog.enterState("Emptying index queue", 60); //:FVK: TODO: remove constant '60'.

		synchronized (m_provider.m_updates) {
			while (m_provider.m_updates.size() > 0) {
				final Object[] pair = m_provider.m_updates.remove(0);
				final WikiPage page = (WikiPage) pair[0];
				final String text = (String) pair[1];
				m_provider.updateLuceneIndex(page, text);
			}
		}

		//:FVK: m_watchdog.exitState();
	}

}
