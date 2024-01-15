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
package org.apache.wiki.api.search;

import java.io.IOException;
import java.util.Collection;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.filters.PageFilter;
import org.apache.wiki.api.modules.InternalModule;
import org.elwiki.api.component.WikiManager;
import org.elwiki_data.WikiPage;


/**
 *  Manages searching the Wiki.
 *
 *  @since 2.2.21.
 */
public interface SearchManager extends WikiManager, PageFilter, InternalModule {

    String DEFAULT_SEARCHPROVIDER = "org.apache.wiki.search.lucene.LuceneSearchProvider";

    /** Property name for setting the search provider. Value is <tt>{@value}</tt>. */
    String PROP_SEARCHPROVIDER = "jspwiki.searchProvider";

    /** The name of the JSON object that manages search. */
    String JSON_SEARCH = "search";

    /**
     *  Returns the SearchProvider used.
     *
     *  @return The current SearchProvider.
     */
    SearchProvider getSearchEngine();

    /**
     *  Sends a search to the current search provider. The query is is whatever native format the query engine wants to use.
     *
     * @param query The query.  Null is safe, and is interpreted as an empty query.
     * @param wikiContext the context within which to run the search
     * @return A collection of WikiPages that matched.
     * @throws ProviderException If the provider fails and a search cannot be completed.
     * @throws IOException If something else goes wrong.
     */
    default Collection< SearchResult > findPages( String query, final WikiContext wikiContext ) throws ProviderException, IOException {
        if( query == null ) {
            query = "";
        }
        return getSearchEngine().findPages( query, wikiContext );
    }

    /**
     *  Removes the page from the search cache (if any).
     *
     *  @param page  The page to remove
     */
    default void pageRemoved( final WikiPage page ) {
        getSearchEngine().pageRemoved( page );
    }

    /**
     *   Forces the reindex of the given page.
     *
     *   @param page The page.
     */
    default void reindexPage( final WikiPage page ) {
        getSearchEngine().reindexPage( page );
    }

}
