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

package org.apache.wiki.diff;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.diff.DiffProvider;
import org.apache.wiki.api.diff.DifferenceManager;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.providers.PageProvider;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.util.TextUtil;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Load, initialize and delegate to the DiffProvider that will actually do the work.
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultDifferenceManager",
	service = { DifferenceManager.class, WikiManager.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultDifferenceManager implements DifferenceManager, WikiManager, EventHandler {

	private static final Logger log = Logger.getLogger(DefaultDifferenceManager.class);

	private DiffProvider m_provider;

	/**
	 * Creates instance of DefaultDifferenceManager.
	 */
	public DefaultDifferenceManager() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	/** Stores configuration. */
	@Reference //(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private Engine engine;

	@WikiServiceReference
	private PageManager pageManager;

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		loadProvider();
		initializeProvider(engine);

		log.info("Using difference provider: " + m_provider.getProviderInfo());
	}

	// -- OSGi service handling ------------------------(end)--

    private void loadProvider() {
		final String providerClassName = wikiConfiguration.getStringProperty(PROP_DIFF_PROVIDER,
				TraditionalDiffProvider.class.getName());
        /*:FVK:
        try {
            final Class< ? > providerClass = ClassUtil.findClass("org.apache.wiki.diff", providerClassName );
            m_provider = (DiffProvider) providerClass.getDeclaredConstructor().newInstance();
        } catch( final ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e ) {
            log.warn("Failed loading DiffProvider, will use NullDiffProvider.", e);
        }
        */
        m_provider = new TraditionalDiffProvider();

        if( m_provider == null ) {
            m_provider = new DiffProvider.NullDiffProvider();
        }
    }

    private void initializeProvider( final Engine engine ) {
        try {
            m_provider.initialize( engine );
        } catch( final NoRequiredPropertyException | IOException e ) {
            log.warn( "Failed initializing DiffProvider, will use NullDiffProvider.", e );
            m_provider = new DiffProvider.NullDiffProvider(); //doesn't need init'd
        }
    }

    /**
     * Returns valid XHTML string to be used in any way you please.
     *
     * @param context        The Wiki Context
     * @param firstWikiText  The old text
     * @param secondWikiText the new text
     * @return XHTML, or empty string, if no difference detected.
     */
    @Override
    public String makeDiff( final WikiContext context, final String firstWikiText, final String secondWikiText ) {
        String diff;
        try {
            diff = m_provider.makeDiffHtml( context, firstWikiText, secondWikiText );

            if( diff == null ) {
                diff = "";
            }
        } catch( final Exception e ) {
            diff = "Failed to create a diff, check the logs.";
            log.warn( diff, e );
        }
        return diff;
    }

    /**
     *  Returns a diff of two versions of a page.
     *  <p>
     *  Note that the API was changed in 2.6 to provide a WikiContext object!
     *
     *  @param context The WikiContext of the page you wish to get a diff from
     *  @param version1 Version number of the old page.  If WikiPageProvider.LATEST_VERSION (-1), then uses current page.
     *  @param version2 Version number of the new page.  If WikiPageProvider.LATEST_VERSION (-1), then uses current page.
     *
     *  @return A HTML-ized difference between two pages.  If there is no difference, returns an empty string.
     */
    @Override
    public String getDiff( final WikiContext context, final int version1, final int version2 ) {
		WikiPage wikiPage = context.getPage();
		String page1 = pageManager.getPureText(wikiPage, version1);
		String page2 = pageManager.getPureText(wikiPage, version2);

        // Kludge to make diffs for new pages to work this way.
        if( version1 == PageProvider.LATEST_VERSION ) {
            page1 = "";
        }

        return makeDiff( context, page1, page2 );
    }

	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
			break;
		}*/
	}

}
