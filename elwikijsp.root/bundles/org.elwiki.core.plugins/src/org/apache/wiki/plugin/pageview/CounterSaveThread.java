package org.apache.wiki.plugin.pageview;

import org.apache.wiki.WikiBackgroundThread;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.plugin.pageview.PageViewPlugin.PageViewManager;

/**
 * Background thread storing the page counters.
 */
final class CounterSaveThread extends WikiBackgroundThread {

    /** The page view manager. */
    private final PageViewManager m_manager;

    /**
     * Create a wiki background thread to store the page counters.
     * 
     * @param engine The wiki engine.
     * @param interval Delay in seconds between saves.
     * @param pageViewManager page view manager.
     */
    public CounterSaveThread( final Engine engine, final int interval, final PageViewManager pageViewManager ) {
        super( engine, interval );
        if( pageViewManager == null ) {
            throw new IllegalArgumentException( "Manager cannot be null" );
        }

        m_manager = pageViewManager;
    }

    /**
     * Save the page counters to file.
     */
    @Override
    public void backgroundTask() {
        if( m_manager.isRunning( this ) ) {
            m_manager.storeCounters();
        }
    }
}