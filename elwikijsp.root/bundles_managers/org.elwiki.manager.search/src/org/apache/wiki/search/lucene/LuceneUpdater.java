package org.apache.wiki.search.lucene;

import org.apache.wiki.InternalWikiException;
import org.apache.wiki.WatchDog;
import org.apache.wiki.WikiBackgroundThread;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;

/**
 * Updater thread that updates Lucene indexes.
 */
final class LuceneUpdater extends WikiBackgroundThread {
    protected static final int INDEX_DELAY    = 5;
    protected static final int INITIAL_DELAY = 60;
    private final LuceneSearchProvider m_provider;

    private int m_initialDelay;

    private WatchDog m_watchdog;

    protected LuceneUpdater( final Engine engine, final LuceneSearchProvider provider, final int initialDelay, final int indexDelay ) {
        super( engine, indexDelay );
        m_provider = provider;
        m_initialDelay = initialDelay;
        setName("JSPWiki Lucene Indexer");
    }

    @Override
    public void startupTask() throws Exception {
        m_watchdog = WatchDog.getCurrentWatchDog( getEngine() );

        // Sleep initially...
        try {
            Thread.sleep( m_initialDelay * 1000L );
        } catch( final InterruptedException e ) {
            throw new InternalWikiException("Interrupted while waiting to start.", e);
        }

        m_watchdog.enterState( "Full reindex" );
        // Reindex everything
        m_provider.doFullLuceneReindex();
        m_watchdog.exitState();
    }

    @Override
    public void backgroundTask() {
        m_watchdog.enterState("Emptying index queue", 60);

        synchronized ( m_provider.m_updates ) {
            while( m_provider.m_updates.size() > 0 ) {
                final Object[] pair = m_provider.m_updates.remove(0);
                final WikiPage page = ( WikiPage ) pair[0];
                final String text = ( String ) pair[1];
                m_provider.updateLuceneIndex(page, text);
            }
        }

        m_watchdog.exitState();
    }

}
