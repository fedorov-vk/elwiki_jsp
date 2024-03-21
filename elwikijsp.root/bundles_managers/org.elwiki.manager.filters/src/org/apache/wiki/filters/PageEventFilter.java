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

package org.apache.wiki.filters;

import java.util.Map;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.FilterException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.filters.BasePageFilter;
import org.apache.wiki.filters.internal.FiltersActivator;
import org.elwiki.api.event.PageEvent;
import org.osgi.service.event.Event;

/**
 * Fires WikiPageEvents for page events. Events are triggered synchronously by
 * {@link org.osgi.service.event.EventAdmin#sendEvent()}.
 */
public class PageEventFilter extends BasePageFilter {

	/**
	 * Called whenever a new PageFilter is instantiated and reset.
	 */
	@Override
	public void initialize(final Engine engine) throws FilterException {
		super.initialize(engine);
	}

	/**
	 * This method is called whenever a page has been loaded from the provider, but not yet been sent
	 * through the TranslatorReader. Note that you cannot do HTML translation here, because
	 * TranslatorReader is likely to escape it.
	 *
	 * @param wikiContext The current wikicontext.
	 * @param content     WikiMarkup.
	 */
	@Override
	public String preTranslate(final WikiContext wikiContext, final String content) {
		FiltersActivator.getEventAdmin().sendEvent( //
				new Event(PageEvent.Topic.PRE_TRANSLATE,
						Map.of(PageEvent.PROPERTY_WIKI_CONTEXT, wikiContext)));

		return content;
	}

	/**
	 * This method is called after a page has been fed through the TranslatorReader, so anything you are
	 * seeing here is translated content. If you want to do any of your own WikiMarkup2HTML translation,
	 * do it here.
	 */
	@Override
	public String postTranslate(final WikiContext wikiContext, final String htmlContent) {
		FiltersActivator.getEventAdmin().sendEvent( //
				new Event(PageEvent.Topic.POST_TRANSLATE,
						Map.of(PageEvent.PROPERTY_WIKI_CONTEXT, wikiContext)));

		return htmlContent;
	}

	/**
	 * This method is called before the page has been saved to the PageProvider.
	 */
	@Override
	public String preSave(final WikiContext wikiContext, final String content) {
		FiltersActivator.getEventAdmin().sendEvent( //
				new Event(PageEvent.Topic.PRE_SAVE,
						Map.of(PageEvent.PROPERTY_WIKI_CONTEXT, wikiContext)));

		return content;
	}

	/**
	 * This method is called after the page has been successfully saved. If the saving fails for any
	 * reason, then this method will not be called.
	 * <p>
	 * Since the result is discarded from this method, this is only useful for things like counters,
	 * etc.
	 */
	@Override
	public void postSave(final WikiContext wikiContext, final String content) throws WikiException {
		FiltersActivator.getEventAdmin().sendEvent( //
				new Event(PageEvent.Topic.POST_SAVE,
						Map.of(PageEvent.PROPERTY_WIKI_CONTEXT, wikiContext)));
	}

}
