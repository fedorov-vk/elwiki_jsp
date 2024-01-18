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
package org.apache.wiki.content;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.api.search.SearchManager;
import org.apache.wiki.content0.PageRenamer;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.util.TextUtil;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.api.event.WikiPageEventTopic;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

/**
 * Provides page renaming functionality. Note that there used to be a similarly named class in
 * 2.6, but due to unclear copyright, the class was completely rewritten from scratch for 2.8.
 *
 * @since 2.8
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultPageRenamer",
	service = { PageRenamer.class, WikiManager.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultPageRenamer implements PageRenamer, EventHandler {

	private static final Logger log = Logger.getLogger(DefaultPageRenamer.class);

	// -- OSGi service handling --------------------( start )--

    @Reference
    EventAdmin eventAdmin;

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private PageManager pageManager;

	@WikiServiceReference
	private AttachmentManager attachmentManager;

	@WikiServiceReference
	private ReferenceManager referenceManager;

	@WikiServiceReference
	private SearchManager searchManager;

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// nothing to do.
	}

	// -- OSGi service handling ----------------------( end )--

	/**
	 * Renames a page.
	 * 
	 * @param context         The current context.
	 * @param renameFrom      The name from which to rename.
	 * @param renameTo        The new name.
	 * @param changeReferrers If true, also changes all the referrers.
	 * @return The final new name (in case it had to be modified)
	 * @throws WikiException If the page cannot be renamed.
	 */
	@Override
	public String renamePage(final WikiContext context, final String renameFrom, final String renameTo,
			final boolean changeReferrers) throws WikiException {
		// Sanity checks first
		if (renameFrom == null || renameFrom.length() == 0) {
			throw new WikiException("From name may not be null or empty");
		}
		if (renameTo == null || renameTo.length() == 0) {
			throw new WikiException("To name may not be null or empty");
		}

		// Clean up the "to" -name so that it does not contain anything illegal
		final String renameToClean = MarkupParser.cleanLink(renameTo.trim());
		if (renameToClean.equals(renameFrom)) {
			throw new WikiException("You cannot rename the page to itself");
		}

		// Preconditions: "from" page must exist, and "to" page must not yet exist.
		final Engine engine = context.getEngine();
		final WikiPage fromPage = pageManager.getPage(renameFrom);
		if (fromPage == null) {
			throw new WikiException("No such page " + renameFrom);
		}
		WikiPage toPage = pageManager.getPage(renameToClean);
		if (toPage != null) {
			throw new WikiException("Page already exists " + renameToClean);
		}

		final Set<String> referrers = getReferencesToChange(fromPage, engine);

		// Do the actual rename by changing from the frompage to the topage, including all of the
		// attachments
		// Remove references to attachments under old name
		final List<PageAttachment> attachmentsOldName = attachmentManager.listAttachments(fromPage);
		for (final PageAttachment att : attachmentsOldName) {
			final WikiPage fromAttPage = pageManager.getPage(att.getName());
			referenceManager.pageRemoved(fromAttPage);
		}

		pageManager.getProvider().movePage(renameFrom, renameToClean);
		if (attachmentManager.attachmentsEnabled()) {
			attachmentManager.getCurrentProvider().moveAttachmentsForPage(renameFrom, renameToClean);
		}

		// Add a comment to the page notifying what changed. This adds a new revision to the repo with
		// no actual change.
		toPage = pageManager.getPage(renameToClean);
		if (toPage == null) {
			throw new ProviderException("Rename seems to have failed for some strange reason - please check logs!");
		}
		PageContent content = toPage.getLastContent();
		content.setChangeNote(fromPage.getName() + " ==> " + toPage.getName()); // :FVK: workaround - previous change note is removed.

		content.setAuthor(context.getCurrentUser().getName());
		pageManager.putPageText(toPage, pageManager.getPureText(toPage, context.getPageVersion()), "author",
				"changenote"); // FIXME: здесь надо не текст, а просто имя поменть.

		// Update the references
		referenceManager.pageRemoved(fromPage);
		//:FVK:referenceManager.updateReferences(toPage);

		// Update referrers
		if (changeReferrers) {
			updateReferrers(context, fromPage, toPage, referrers);
		}

		// re-index the page including its attachments
		searchManager.reindexPage(toPage);

		final Collection<PageAttachment> attachmentsNewName = attachmentManager
				.listAttachments(toPage);
		for (final PageAttachment att : attachmentsNewName) {
			final WikiPage toAttPage = pageManager.getPage(att.getName());
			// add reference to attachment under new page name
			//:FVK: referenceManager.updateReferences(toAttPage);
			// :FVK: Engine.getSearchManager().reindexPage( att );
		}

		this.eventAdmin.sendEvent(new Event(WikiPageEventTopic.TOPIC_PAGE_RENAMED,
				Map.of(WikiPageEventTopic.PROPERTY_OLD_PAGE_NAME, renameFrom,
						WikiPageEventTopic.PROPERTY_NEW_PAGE_NAME, renameToClean)));
		
		// Done, return the new name.
		return renameToClean;
	}

	/**
	 * This method finds all the pages which have anything to do with the fromPage and change any
	 * referrers it can figure out in that page.
	 * 
	 * @param context  WikiContext in which we operate
	 * @param fromPage The old page
	 * @param toPage   The new page
	 */
	@Deprecated
	private void updateReferrers(final WikiContext context, final WikiPage fromPage, final WikiPage toPage,
			final Set<String> referrers) {
		if (referrers.isEmpty()) { // No referrers
			return;
		}

		final Engine engine = context.getEngine();
		for (String pageName : referrers) {
			// In case the page was just changed from under us, let's do this small kludge.
			if (pageName.equals(fromPage.getName())) {
				pageName = toPage.getName();
			}

			final WikiPage p = pageManager.getPage(pageName);

			final String sourceText = pageManager.getPureText(p, context.getPageVersion());
			String newText = replaceReferrerString(context, sourceText, fromPage.getName(), toPage.getName());

			if (!sourceText.equals(newText)) {
				PageContent content = p.getLastContent();
				content.setChangeNote(fromPage.getName() + " ==> " + toPage.getName()); // :FVK: workaround - previous change note is removed.
				content.setAuthor(context.getCurrentUser().getName());
				try {
					pageManager.putPageText(p, newText, "author", "changenote"); // FIXME: здесь надо не текст, а что-то задать.
					//:FVK:referenceManager.updateReferences(p);
				} catch (final ProviderException e) {
					// We fail with an error, but we will try to continue to rename other referrers as well.
					log.error("Unable to perform rename.", e);
				}
			}
		}
	}

	private Set<String> getReferencesToChange(final WikiPage fromPage, final Engine engine) {
		final Set<String> referrers = new TreeSet<>();
		final Collection<String> r = referenceManager.findReferrers(fromPage.getName());
		if (r != null) {
			referrers.addAll(r);
		}

		try {
			final List<PageAttachment> attachments = attachmentManager.listAttachments(fromPage);
			for (final PageAttachment att : attachments) {
				final Collection<String> c = referenceManager.findReferrers(att.getName());
				if (c != null) {
					referrers.addAll(c);
				}
			}
		} catch (final ProviderException e) {
			// We will continue despite this error
			log.error("Provider error while fetching attachments for rename", e);
		}
		return referrers;
	}

	private String replaceReferrerString(final WikiContext context, final String sourceText, final String from,
			final String to) {
		final StringBuilder sb = new StringBuilder(sourceText.length() + 32);

		// This monstrosity just looks for a JSPWiki link pattern. But it is pretty cool for a regexp,
		// isn't it? If you can
		// understand this in a single reading, you have way too much time in your hands.
		final Pattern linkPattern = Pattern
				.compile("([\\[\\~]?)\\[([^\\|\\]]*)(\\|)?([^\\|\\]]*)(\\|)?([^\\|\\]]*)\\]");
		final Matcher matcher = linkPattern.matcher(sourceText);
		int start = 0;

		while (matcher.find(start)) {
			char charBefore = (char) -1;

			if (matcher.start() > 0) {
				charBefore = sourceText.charAt(matcher.start() - 1);
			}

			if (matcher.group(1).length() > 0 || charBefore == '~' || charBefore == '[') {
				// Found an escape character, so I am escaping.
				sb.append(sourceText.substring(start, matcher.end()));
				start = matcher.end();
				continue;
			}

			String text = matcher.group(2);
			String link = matcher.group(4);
			final String attr = matcher.group(6);

			if (link.length() == 0) {
				text = replaceSingleLink(context, text, from, to);
			} else {
				link = replaceSingleLink(context, link, from, to);

				// A very simple substitution, but should work for quite a few cases.
				text = TextUtil.replaceString(text, from, to);
			}

			//
			// Construct the new string
			//
			sb.append(sourceText.substring(start, matcher.start()));
			sb.append("[").append(text);
			if (link.length() > 0) {
				sb.append("|").append(link);
			}
			if (attr.length() > 0) {
				sb.append("|").append(attr);
			}
			sb.append("]");

			start = matcher.end();
		}

		sb.append(sourceText.substring(start));

		return sb.toString();
	}

	/**
	 * This method does a correct replacement of a single link, taking into account anchors and
	 * attachments.
	 */
	private String replaceSingleLink(final WikiContext context, final String original, final String from,
			final String newlink) {
		final int hash = original.indexOf('#');
		final int slash = original.indexOf('/');
		String realLink = original;

		if (hash != -1) {
			realLink = original.substring(0, hash);
		}
		if (slash != -1) {
			realLink = original.substring(0, slash);
		}

		realLink = MarkupParser.cleanLink(realLink);
		final String oldStyleRealLink = MarkupParser.wikifyLink(realLink);

		// WikiPage realPage = context.getEngine().getPage( reallink );
		// WikiPage p2 = context.getEngine().getPage( from );

		// System.out.println(" "+reallink+" :: "+ from);
		// System.out.println(" "+p+" :: "+p2);

		//
		// Yes, these point to the same page.
		//
		if (realLink.equals(from) || original.equals(from) || oldStyleRealLink.equals(from)) {
			//
			// if the original contains blanks, then we should introduce a link, for example: [My Page] =>
			// [My Page|My Renamed Page]
			final int blank = realLink.indexOf(" ");

			if (blank != -1) {
				return original + "|" + newlink;
			}

			return newlink + ((hash > 0) ? original.substring(hash) : "")
					+ ((slash > 0) ? original.substring(slash) : "");
		}

		return original;
	}

	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
			break;
		}*/
	}

}
