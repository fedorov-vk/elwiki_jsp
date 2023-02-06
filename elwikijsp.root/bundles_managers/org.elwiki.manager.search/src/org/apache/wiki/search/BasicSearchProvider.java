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
package org.apache.wiki.search;

import org.apache.log4j.Logger;
import org.elwiki_data.PageAttachment;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.providers.PageProvider;
import org.apache.wiki.api.search.QueryItem;
import org.apache.wiki.api.search.SearchMatcher;
import org.apache.wiki.api.search.SearchProvider;
import org.apache.wiki.api.search.SearchResult;
import org.apache.wiki.api.search.SearchResultComparator;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.pages0.PageManager;
import org.elwiki.permissions.PagePermission;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 *  Interface for the search providers that handle searching the Wiki
 *
 *  @since 2.2.21.
 */
public class BasicSearchProvider implements SearchProvider {

    private static final Logger log = Logger.getLogger( BasicSearchProvider.class );
    private Engine m_engine;

    /**
     *  {@inheritDoc}
     */
    @Override
    public void initialize( final Engine engine ) throws NoRequiredPropertyException, IOException {
        m_engine = engine;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void pageRemoved( final WikiPage page ) {}

    /**
     *  {@inheritDoc}
     */
    @Override
    public void reindexPage( final WikiPage page ) {}

    /**
     *  Parses a query into something that we can use.
     *  
     *  @param query A query string.
     *  @return A parsed array.
     */
    public QueryItem[] parseQuery( final String query) {
        final StringTokenizer st = new StringTokenizer( query, " \t," );
        final QueryItem[] items = new QueryItem[st.countTokens()];
        int word = 0;

        log.debug("Expecting "+items.length+" items");

        //  Parse incoming search string
        while( st.hasMoreTokens() ) {
            log.debug( "Item " + word );
            String token = st.nextToken().toLowerCase();

            items[ word ] = new QueryItem();

            switch( token.charAt( 0 ) ) {
            case '+':
                items[ word ].type = QueryItem.REQUIRED;
                token = token.substring( 1 );
                log.debug( "Required word: " + token );
                break;

            case '-':
                items[ word ].type = QueryItem.FORBIDDEN;
                token = token.substring( 1 );
                log.debug( "Forbidden word: " + token );
                break;

            default:
                items[ word ].type = QueryItem.REQUESTED;
                log.debug( "Requested word: " + token );
                break;
            }

            items[ word++ ].word = token;
        }

        return items;
    }

    private String attachmentNames( final WikiPage page ) {
        if( ServicesRefs.getAttachmentManager().hasAttachments( page ) ) {
            final List< PageAttachment > attachments;
            try {
                attachments = ServicesRefs.getAttachmentManager().listAttachments( page );
            } catch( final ProviderException e ) {
                log.error( "Unable to get attachments for page", e );
                return "";
            }

            final StringBuilder attachmentNames = new StringBuilder();
            for( final Iterator< PageAttachment > it = attachments.iterator(); it.hasNext(); ) {
                final PageAttachment att = it.next();
                attachmentNames.append( att.getName() );
                if( it.hasNext() ) {
                    attachmentNames.append( " " );
                }
            }
            return attachmentNames.toString();
        }

        return "";
    }

    private Collection< SearchResult > findPages( final QueryItem[] query, final WikiContext wikiContext ) {
        final TreeSet< SearchResult > res = new TreeSet<>( new SearchResultComparator() );
        final SearchMatcher matcher = new SearchMatcher( m_engine, query );
        final Collection< WikiPage > allPages;
        try {
            allPages = ServicesRefs.getPageManager().getAllPages();
        } catch( final ProviderException pe ) {
            log.error( "Unable to retrieve page list", pe );
            return null;
        }

        final AuthorizationManager mgr = ServicesRefs.getAuthorizationManager();

        for( final WikiPage page : allPages ) {
            try {
                if( page != null ) {
                    final PagePermission pp = new PagePermission( page, PagePermission.VIEW_ACTION );
                    if( wikiContext == null || mgr.checkPermission( wikiContext.getWikiSession(), pp ) ) {
                        final String pageName = page.getName();
                        final String pageContent =
                        		ServicesRefs.getPageManager().getPageText( pageName, PageProvider.LATEST_VERSION ) + attachmentNames( page );
                        final SearchResult comparison = matcher.matchPageContent( pageName, pageContent );
                        if( comparison != null ) {
                            res.add( comparison );
                        }
                    }
                }
            } catch( final ProviderException pe ) {
                log.error( "Unable to retrieve page from cache", pe );
            } catch( final IOException ioe ) {
                log.error( "Failed to search page", ioe );
            }
        }

        return res;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Collection< SearchResult > findPages( final String query, final WikiContext wikiContext ) {
        return findPages( parseQuery( query ), wikiContext );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getProviderInfo() {
        return "BasicSearchProvider";
    }

}
