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
package org.apache.wiki.render0;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wiki.LinkCollector;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.engine.RenderApi;
import org.apache.wiki.api.modules.InternalModule;
import org.apache.wiki.api.providers.PageProvider;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.parser0.WikiDocument;
import org.elwiki_data.WikiPage;

/**
 *  This class provides a facade towards the differing rendering routines.  You should use the routines in this manager
 *  instead of the ones in Engine, if you don't want the different side effects to occur - such as WikiFilters.
 *  <p>
 *  This class also manages a rendering cache, i.e. documents are stored between calls. You may control the cache by
 *  tweaking the ehcache.xml file.
 *  <p>
 *
 *  @since  2.4
 */
public interface RenderingManager extends RenderApi, InternalModule {

    /** markup parser property. */
    String PROP_PARSER = "jspwiki.renderingManager.markupParser";

    /** default renderer property. */
    String PROP_RENDERER = "jspwiki.renderingManager.renderer";

    /** default wysiwyg renderer property. */
    String PROP_WYSIWYG_RENDERER = "jspwiki.renderingManager.renderer.wysiwyg";

    /** Name of the regular page cache. */
    String DOCUMENTCACHE_NAME = "jspwiki.renderingCache";

    /**
     *  Returns the wiki Parser
     *  @param pagedata the page data
     *  @return A MarkupParser instance.
     */
    MarkupParser getParser( WikiContext context, String pagedata );

    /**
     *  Returns a cached document, if one is found.
     *
     * @param context the wiki context
     * @param pagedata the page data
     * @return the rendered wiki document
     */
    WikiDocument getRenderedDocument( WikiContext context, String pagedata );

    /**
     * Returns a WikiRenderer instance, initialized with the given context and doc. The object is an XHTMLRenderer,
     * unless overridden in preferences.ini with PROP_RENDERER.
     *
     * @param context The WikiContext
     * @param doc The document to render
     * @return A WikiRenderer for this document, or null, if no such renderer could be instantiated.
     */
    WikiRenderer getRenderer( WikiContext context, WikiDocument doc );

    /**
     * Returns a WikiRenderer instance meant for WYSIWYG editing, initialized with the given
     * context and doc. The object is an WysiwygEditingRenderer, unless overridden
     * in preferences.ini with PROP_WYSIWYG_RENDERER.
     *
     * @param context The WikiContext
     * @param doc The document to render
     * @return A WikiRenderer instance meant for WYSIWYG editing, for this document, or null, if no such renderer could be instantiated.
     */
    WikiRenderer getWysiwygRenderer( WikiContext context, WikiDocument doc );

    /**
     *  Simply renders a WikiDocument to a String.  This version does not get the document from the cache - in fact, it does
     *  not cache the document at all.  This is very useful, if you have something that you want to render outside the caching
     *  routines.  Because the cache is based on full pages, and the cache keys are based on names, use this routine if you're
     *  rendering anything for yourself.
     *
     *  @param context The WikiContext to render in
     *  @param doc A proper WikiDocument
     *  @return Rendered HTML.
     *  @throws IOException If the WikiDocument is poorly formed.
     */
    String getHTML( WikiContext context, WikiDocument doc ) throws IOException;

    /**
     *  Returns the converted HTML of the page using a different context than the default context.<br/>
     *  After the content of the page, the comments of the page are added through separators.  
     *
     *  @param  context A WikiContext in which you wish to render this page in.
     *  @param  page WikiPage reference.
     *  @return HTML-rendered version of the page.
     */
    String getHTML( WikiContext context, WikiPage page );

    /**
     *  Returns the converted HTML of the page's specific version. The version must be a positive integer, otherwise the current
     *  version is returned.
     *
     *  @param pagename WikiName of the page to convert.
     *  @param version Version number to fetch
     *  @return HTML-rendered page text.
     */
    String getHTML( String pagename, int version );

    /**
     *   Convenience method for rendering, using the default parser and renderer.  Note that you can't use this method
     *   to do any arbitrary rendering, as the pagedata MUST be the data from the that the WikiContext refers to - this
     *   method caches the HTML internally, and will return the cached version.  If the pagedata is different from what
     *   was cached, will re-render and store the pagedata into the internal cache.
     *
     *   @param context the wiki context
     *   @param pagedata the page data
     *   @return XHTML data.
     */
    default String getHTML( final WikiContext context, final String pagedata ) {
        try {
            final WikiDocument doc = getRenderedDocument( context, pagedata );
            return getHTML( context, doc );
        } catch( final IOException e ) {
            Logger.getLogger( RenderingManager.class ).error("Unable to parse", e );
        }

        return null;
    }

    /**
     *  Returns the converted HTML of the page.
     *
     *  @param page WikiName of the page to convert.
     *  @return HTML-rendered version of the page.
     */
    default String getHTML( final String page ) {
        return getHTML( page, PageProvider.LATEST_VERSION );
    }

    /**
     *  Helper method for doing the HTML translation.
     *
     *  @param context The WikiContext in which to do the conversion
     *  @param pagedata The data to render
     *  @param localLinkCollector Is called whenever a wiki link is found
     *  @param extLinkCollector   Is called whenever an external link is found
     *  @param attLinkCollector   Is called whenever ...
     *  @param parseAccessRules Parse the access rules if we encounter them
     *  @param justParse Just parses the pagedata, does not actually render. In this case, this methods an empty string.
     *  @return HTML-rendered page text.
     */
    String textToHTML( WikiContext context,
                       String pagedata,
                       LinkCollector localLinkCollector,
                       LinkCollector extLinkCollector,
                       LinkCollector attLinkCollector,
                       LinkCollector unknownPagesLinkCollector,
                       boolean parseAccessRules,
                       boolean justParse );

    /**
     *  Just convert WikiText to HTML.
     *
     *  @param context The WikiContext in which to do the conversion
     *  @param pagedata The data to render
     *  @param localLinkCollector Is called whenever a wiki link is found
     *  @param extLinkCollector   Is called whenever an external link is found
     *
     *  @return HTML-rendered page text.
     */
    default String textToHTML( final WikiContext context,
                               final String pagedata,
                               final LinkCollector localLinkCollector,
                               final LinkCollector extLinkCollector ) {
        return textToHTML( context, pagedata, localLinkCollector, extLinkCollector, null, null, true, false );
    }

    /**
     *  Just convert WikiText to HTML.
     *
     *  @param context The WikiContext in which to do the conversion
     *  @param pagedata The data to render
     *  @param localLinkCollector Is called whenever a wiki link is found
     *  @param extLinkCollector   Is called whenever an external link is found
     *  @param attLinkCollector   Is called whenever an attachment link is found
     *  @return HTML-rendered page text.
     */
    default String textToHTML( final WikiContext context,
                               final String pagedata,
                               final LinkCollector localLinkCollector,
                               final LinkCollector extLinkCollector,
                               final LinkCollector attLinkCollector ) {
        return textToHTML( context, pagedata, localLinkCollector, extLinkCollector, attLinkCollector, null, true, false );
    }

}
