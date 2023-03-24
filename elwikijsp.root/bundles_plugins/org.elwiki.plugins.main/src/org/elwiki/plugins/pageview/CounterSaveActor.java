package org.elwiki.plugins.pageview;

import org.elwiki.api.BackgroundThreads.Actor;
import org.elwiki.plugins.pageview.PageViewPlugin.PageViewManager;

/**
 * Actor of background thread for storing the page counters.
 */
public class CounterSaveActor extends Actor {

	/** The page view manager. */
	private final PageViewManager m_manager;

	/**
	 * Create an actor of background thread to store the page counters.
	 * 
	 * @param pageViewManager
	 */
	public CounterSaveActor(PageViewManager pageViewManager) {
		if (pageViewManager == null) {
			throw new IllegalArgumentException("Manager cannot be null");
		}

		this.m_manager = pageViewManager;
	}

	/**
	 * Save the page counters to file. <br/>
	 * Overloads the: {@inheritDoc}
	 */
	@Override
	public void backgroundTask() throws Exception {
		if (m_manager.isRunning(this)) {
			m_manager.storeCounters();
		}
	}

}
