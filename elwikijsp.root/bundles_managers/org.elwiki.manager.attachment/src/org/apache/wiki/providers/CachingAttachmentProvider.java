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
package org.apache.wiki.providers;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAttachment;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.providers.AttachmentProvider;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.search.QueryItem;
import org.apache.wiki.util.ClassUtil;
import org.apache.wiki.util.TextUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;


/**
 * Provides a caching attachment provider.  This class rests on top of a real provider class and provides a cache to speed things up.
 * Only the Attachment objects are cached; the actual attachment contents are fetched always from the provider.
 *
 *  @since 2.1.64.
 */
//        EntryRefreshPolicy for that.
public class CachingAttachmentProvider implements AttachmentProvider {

    private static final Logger log = Logger.getLogger(CachingAttachmentProvider.class);

    private AttachmentProvider m_provider;

    private CacheManager m_cacheManager = CacheManager.getInstance();

    /** Default cache capacity for now. */
    public static final int m_capacity = 1000;

    /** The cache contains Collection objects which contain Attachment objects. The key is the parent wiki page name (String). */
    private Cache m_cache;

    /** Name of the attachment cache. */
    public static final String ATTCACHE_NAME = "jspwiki.attachmentsCache";
    /** Name of the attachment cache. */
    public static final String ATTCOLLCACHE_NAME = "jspwiki.attachmentCollectionsCache";

    /**
     * This cache contains Attachment objects and is keyed by attachment name.
     * This provides for quickly giving recently changed attachments (for the RecentChanges plugin)
     */
    private Cache m_attCache;

    private long m_cacheMisses = 0;
    private long m_cacheHits = 0;

    /** The extension to append to directory names to denote an attachment directory. */
    public static final String DIR_EXTENSION   = "-att";


    private boolean m_gotall = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize( final Engine engine ) throws NoRequiredPropertyException, IOException {
        log.info( "Initing CachingAttachmentProvider" );
        final String attCollCacheName = engine.getWikiConfiguration().getApplicationName() + "." + ATTCOLLCACHE_NAME;
        if( m_cacheManager.cacheExists( attCollCacheName ) ) {
            m_cache = m_cacheManager.getCache( attCollCacheName );
        } else {
            m_cache = new Cache( attCollCacheName, m_capacity, false, false, 0, 0 );
            m_cacheManager.addCache( m_cache );
        }

        //
        // cache for the individual Attachment objects, attachment name is key, the Attachment object is the cached object
        //
        final String attCacheName = engine.getWikiConfiguration().getApplicationName() + "." + ATTCACHE_NAME;
        if( m_cacheManager.cacheExists( attCacheName ) ) {
            m_attCache = m_cacheManager.getCache( attCacheName );
        } else {
            m_attCache = new Cache( attCacheName, m_capacity, false, false, 0, 0 );
            m_cacheManager.addCache( m_attCache );
        }
        //
        //  Find and initialize real provider.
        //
        final String classname;
        try {
            classname = TextUtil.getRequiredProperty( engine.getWikiPreferences(), AttachmentManager.PROP_PROVIDER );
        } catch( final NoSuchElementException e ) {
            throw new NoRequiredPropertyException( e.getMessage(), AttachmentManager.PROP_PROVIDER );
        }

//        try {
        	/*:FVK:
            final Class< ? > providerclass = ClassUtil.findClass( "org.apache.wiki.providers", classname );
            m_provider = ( AttachmentProvider )providerclass.newInstance();
            */
        	m_provider = ( AttachmentProvider )new BasicAttachmentProvider();

            log.debug( "Initializing real provider class " + m_provider );
            m_provider.initialize( engine );
//        } catch( final ClassNotFoundException e ) {
//            log.error( "Unable to locate provider class " + classname, e );
//            throw new IllegalArgumentException( "no provider class", e );
//        } catch( final InstantiationException e ) {
//            log.error( "Unable to create provider class " + classname, e );
//            throw new IllegalArgumentException( "faulty provider class", e );
//        } catch( final IllegalAccessException e ) {
//            log.error( "Illegal access to provider class " + classname, e );
//            throw new IllegalArgumentException( "illegal provider class", e );
//        }
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public PageAttachment putAttachmentData(WikiPage wikiPage, final PageAttachment att, final InputStream data)
			throws ProviderException, IOException {
		PageAttachment addedAttachment = m_provider.putAttachmentData(wikiPage, att, data);

		/*:FVK:
		m_cache.remove(att.getParentName());
		att.setLastModified(new Date());
		*/
		m_attCache.put(new Element(addedAttachment.getName(), addedAttachment));

		return addedAttachment;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getAttachmentData( final PageAttachment att )
        throws ProviderException, IOException {
        return m_provider.getAttachmentData( att );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List< PageAttachment > listAttachments( final WikiPage page) throws ProviderException {
        log.debug("Listing attachments for " + page + ": \"" + page.getName() + "\"");
        final Element element = m_cache.get(page.getName());

        if (element != null) {
            @SuppressWarnings("unchecked") final List< PageAttachment > c = ( List< PageAttachment > )element.getObjectValue();
            log.debug("LIST from cache, " + page.getName() + ", size=" + c.size());
            return cloneCollection(c);
        }

        log.debug("list NOT in cache, " + page.getName());

        return refresh(page);
    }

    private < T > List< T > cloneCollection( final Collection< T > c ) {
        return new ArrayList<>( c );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection< PageAttachment > findAttachments( final QueryItem[] query )
    {
        return m_provider.findAttachments( query );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PageAttachment> listAllChanged( final Date timestamp ) throws ProviderException {
        final List< PageAttachment > all;
        // we do a one-time build up of the cache, after this the cache is updated for every attachment add/delete
        if ( !m_gotall ) {
            all = m_provider.listAllChanged(timestamp);

            // Put all pages in the cache :
            synchronized (this) {
                for( final PageAttachment att : all ) {
                    m_attCache.put( new Element( att.getName(), att ) );
                }
                m_gotall = true;
            }
        } else {
            @SuppressWarnings("unchecked") final List< String > keys = m_attCache.getKeysWithExpiryCheck();
            all = new ArrayList<>();
            for ( final String key : keys) {
                final Element element = m_attCache.get(key);
                final PageAttachment cachedAttachment = ( PageAttachment )element.getObjectValue();
                if (cachedAttachment != null) {
                    all.add(cachedAttachment);
                }
            }
        }

        return all;
    }

    /**
     *  Simply goes through the collection and attempts to locate the
     *  given attachment of that name.
     *
     *  @return null, if no such attachment was in this collection.
     */
    private PageAttachment findAttachmentFromCollection( final Collection< PageAttachment > c, final String name ) {
        for( final PageAttachment att : new ArrayList< >( c ) ) {
        	/*:FVK:
            if( name.equals( att.getFileName() ) ) {
                return att;
            }
            */
        }

        return null;
    }

    /**
     *  Refreshes the cache content and updates counters.
     *
     *  @return The newly fetched object from the provider.
     */
    private List< PageAttachment > refresh( final WikiPage page ) throws ProviderException {
        final List< PageAttachment > c = m_provider.listAttachments( page );
        m_cache.put( new Element( page.getName(), c ) );
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public PageAttachment getAttachmentInfo( final WikiPage page, final String name, final short version) throws ProviderException {
        if( log.isDebugEnabled() ) {
            log.debug( "Getting attachments for " + page + ", name=" + name + ", version=" + version );
        }

        //  We don't cache previous versions
        if( version != WikiProvider.LATEST_VERSION ) {
            log.debug( "...we don't cache old versions" );
            return m_provider.getAttachmentInfo( page, name, version );
        }

        final Collection< PageAttachment > c;
        final Element element = m_cache.get( page.getName() );

        if( element == null ) {
            log.debug( page.getName() + " wasn't in the cache" );
            c = refresh( page );

            if( c == null ) {
                return null; // No such attachment
            }
        } else {
            log.debug( page.getName() + " FOUND in the cache" );
            c = ( Collection< PageAttachment > )element.getObjectValue();
        }

        return findAttachmentFromCollection( c, name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AttachmentContent> getVersionHistory( final PageAttachment att ) {
        return m_provider.getVersionHistory( att );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteVersion( final PageAttachment att ) throws ProviderException {
        // This isn't strictly speaking correct, but it does not really matter
        //:FVK: m_cache.remove( att.getParentName() );
        m_provider.deleteVersion( att );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAttachment( final PageAttachment att ) throws ProviderException {
    	//:FVK: m_cache.remove( att.getParentName() );
        m_attCache.remove( att.getName() );
        m_provider.deleteAttachment( att );
    }

    /**
     * Gets the provider class name, and cache statistics (misscount and,hitcount of the attachment cache).
     *
     * @return A plain string with all the above mentioned values.
     */
    @Override
    public synchronized String getProviderInfo() {
        return "Real provider: " + m_provider.getClass().getName() +
                ".  Cache misses: " + m_cacheMisses +
                ".  Cache hits: " + m_cacheHits;
    }

    /**
     *  Returns the WikiAttachmentProvider that this caching provider delegates to.
     *
     *  @return The real provider underneath this one.
     */
    public AttachmentProvider getRealProvider() {
        return m_provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveAttachmentsForPage( final String oldParent, final String newParent ) throws ProviderException {
        m_provider.moveAttachmentsForPage( oldParent, newParent );
        m_cache.remove( newParent );
        m_cache.remove( oldParent );

        // This is a kludge to make sure that the pages are removed from the other cache as well.
        final String checkName = oldParent + "/";

        @SuppressWarnings("unchecked") final List< String > names = m_cache.getKeysWithExpiryCheck();
        for( final String name : names ) {
            if( name.startsWith( checkName ) ) {
                m_attCache.remove( name );
            }
        }
    }

}