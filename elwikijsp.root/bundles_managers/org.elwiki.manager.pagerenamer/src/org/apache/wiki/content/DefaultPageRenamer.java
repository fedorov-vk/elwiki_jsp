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

import org.apache.log4j.Logger;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiPageRenameEvent;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.api.search.SearchManager;
import org.apache.wiki.content0.PageRenamer;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.util.TextUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.services.ServicesRefs;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides page renaming functionality. Note that there used to be a similarly named class in
 * 2.6, but due to unclear copyright, the class was completely rewritten from scratch for 2.8.
 *
 * @since 2.8
 */
@Component(name = "elwiki.DefaultPageRenamer", service = PageRenamer.class, //
		factory = "elwiki.PageRenamer.factory")
public class DefaultPageRenamer implements PageRenamer {

	private static final Logger log = Logger.getLogger(DefaultPageRenamer.class);

	private boolean m_camelCase = false;

	// -- service handling -------------------------< start >--

	@Activate
	protected void startup() throws WikiException {
		//
	}

	@Deactivate
	protected void shutdown() {
		//
	}

	// -- service handling ---------------------------< end >--

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
	public String renamePage(final Context context, final String renameFrom, final String renameTo,
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
		final WikiPage fromPage = ServicesRefs.getPageManager().getPage(renameFrom);
		if (fromPage == null) {
			throw new WikiException("No such page " + renameFrom);
		}
		WikiPage toPage = ServicesRefs.getPageManager().getPage(renameToClean);
		if (toPage != null) {
			throw new WikiException("Page already exists " + renameToClean);
		}

		final Set<String> referrers = getReferencesToChange(fromPage, engine);

		// Do the actual rename by changing from the frompage to the topage, including all of the
		// attachments
		// Remove references to attachments under old name
		final List<PageAttachment> attachmentsOldName = ServicesRefs.getAttachmentManager().listAttachments(fromPage);
		for (final PageAttachment att : attachmentsOldName) {
			final WikiPage fromAttPage = ServicesRefs.getPageManager().getPage(att.getName());
			ServicesRefs.getReferenceManager().pageRemoved(fromAttPage);
		}

		ServicesRefs.getPageManager().getProvider().movePage(renameFrom, renameToClean);
		if (ServicesRefs.getAttachmentManager().attachmentsEnabled()) {
			ServicesRefs.getAttachmentManager().getCurrentProvider().moveAttachmentsForPage(renameFrom, renameToClean);
		}

		// Add a comment to the page notifying what changed. This adds a new revision to the repo with
		// no actual change.
		toPage = ServicesRefs.getPageManager().getPage(renameToClean);
		if (toPage == null) {
			throw new ProviderException("Rename seems to have failed for some strange reason - please check logs!");
		}
		PageContent content = toPage.getLastContent();
		content.setChangeNote(fromPage.getName() + " ==> " + toPage.getName()); // :FVK: workaround - previous change note is removed.

		content.setAuthor(context.getCurrentUser().getName());
		ServicesRefs.getPageManager().putPageText(toPage, ServicesRefs.getPageManager().getPureText(toPage), "author",
				"changenote"); // FIXME: ?????????? ???????? ???? ??????????, ?? ???????????? ?????? ??????????????.

		// Update the references
		ServicesRefs.getReferenceManager().pageRemoved(fromPage);
		ServicesRefs.getReferenceManager().updateReferences(toPage);

		// Update referrers
		if (changeReferrers) {
			updateReferrers(context, fromPage, toPage, referrers);
		}

		// re-index the page including its attachments
		ServicesRefs.getSearchManager().reindexPage(toPage);

		final Collection<PageAttachment> attachmentsNewName = ServicesRefs.getAttachmentManager()
				.listAttachments(toPage);
		for (final PageAttachment att : attachmentsNewName) {
			final WikiPage toAttPage = ServicesRefs.getPageManager().getPage(att.getName());
			// add reference to attachment under new page name
			ServicesRefs.getReferenceManager().updateReferences(toAttPage);
			// :FVK: ServicesRefs.getSearchManager().reindexPage( att );
		}

		firePageRenameEvent(renameFrom, renameToClean);

		// Done, return the new name.
		return renameToClean;
	}

	/**
	 * Fires a WikiPageRenameEvent to all registered listeners. Currently not used internally by
	 * JSPWiki itself, but you can use it for something else.
	 *
	 * @param oldName the former page name
	 * @param newName the new page name
	 */
	@Override
	public void firePageRenameEvent(final String oldName, final String newName) {
		if (WikiEventManager.isListening(this)) {
			WikiEventManager.fireEvent(this, new WikiPageRenameEvent(this, oldName, newName));
		}
	}

	/**
	 * This method finds all the pages which have anything to do with the fromPage and change any
	 * referrers it can figure out in that page.
	 * 
	 * @param context  WikiContext in which we operate
	 * @param fromPage The old page
	 * @param toPage   The new page
	 */
	private void updateReferrers(final Context context, final WikiPage fromPage, final WikiPage toPage,
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

			final WikiPage p = ServicesRefs.getPageManager().getPage(pageName);

			final String sourceText = ServicesRefs.getPageManager().getPureText(p);
			String newText = replaceReferrerString(context, sourceText, fromPage.getName(), toPage.getName());

			IPreferenceStore wikiPreferences = engine.getWikiPreferences();
			m_camelCase = TextUtil.getBooleanProperty(wikiPreferences, MarkupParser.PROP_CAMELCASELINKS, m_camelCase);
			if (m_camelCase) {
				newText = replaceCCReferrerString(context, newText, fromPage.getName(), toPage.getName());
			}

			if (!sourceText.equals(newText)) {
				PageContent content = p.getLastContent();
				content.setChangeNote(fromPage.getName() + " ==> " + toPage.getName()); // :FVK: workaround - previous change note is removed.
				content.setAuthor(context.getCurrentUser().getName());
				try {
					ServicesRefs.getPageManager().putPageText(p, newText, "author", "changenote"); // FIXME: ?????????? ???????? ???? ??????????, ?? ??????-???? ????????????.
					ServicesRefs.getReferenceManager().updateReferences(p);
				} catch (final ProviderException e) {
					// We fail with an error, but we will try to continue to rename other referrers as well.
					log.error("Unable to perform rename.", e);
				}
			}
		}
	}

	private Set<String> getReferencesToChange(final WikiPage fromPage, final Engine engine) {
		final Set<String> referrers = new TreeSet<>();
		final Collection<String> r = ServicesRefs.getReferenceManager().findReferrers(fromPage.getName());
		if (r != null) {
			referrers.addAll(r);
		}

		try {
			final List<PageAttachment> attachments = ServicesRefs.getAttachmentManager().listAttachments(fromPage);
			for (final PageAttachment att : attachments) {
				final Collection<String> c = ServicesRefs.getReferenceManager().findReferrers(att.getName());
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

	/**
	 * Replaces camelcase links.
	 */
	private String replaceCCReferrerString(final Context context, final String sourceText, final String from,
			final String to) {
		final StringBuilder sb = new StringBuilder(sourceText.length() + 32);
		final Pattern linkPattern = Pattern.compile("\\p{Lu}+\\p{Ll}+\\p{Lu}+[\\p{L}\\p{Digit}]*");
		final Matcher matcher = linkPattern.matcher(sourceText);
		int start = 0;

		while (matcher.find(start)) {
			final String match = matcher.group();
			sb.append(sourceText.substring(start, matcher.start()));
			final int lastOpenBrace = sourceText.lastIndexOf('[', matcher.start());
			final int lastCloseBrace = sourceText.lastIndexOf(']', matcher.start());

			if (match.equals(from) && lastCloseBrace >= lastOpenBrace) {
				sb.append(to);
			} else {
				sb.append(match);
			}

			start = matcher.end();
		}

		sb.append(sourceText.substring(start));

		return sb.toString();
	}

	private String replaceReferrerString(final Context context, final String sourceText, final String from,
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
	private String replaceSingleLink(final Context context, final String original, final String from,
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

}
