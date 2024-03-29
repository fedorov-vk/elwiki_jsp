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
package org.apache.wiki.pages0;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.providers.PageProvider;
import org.apache.wiki.api.providers.WikiProvider;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.api.component.IModulePreferences;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.UnknownPage;
import org.elwiki_data.WikiPage;

public interface PageManager extends IModulePreferences {

	interface Prefs {
		String PAGE_MANAGER = "pageManager";
	}

	/** Defines type of page motion. */
	enum PageMotionType { AFTER, BEFORE, BOTTOM };

    /** The property value for setting the current page provider.  Value is {@value}. */
	@Deprecated
    String PROP_PAGEPROVIDER = "jspwiki.pageProvider";

    /** The property value for setting the amount of time before the page locks expire. Value is {@value}. */
    String PROP_LOCKEXPIRY = "jspwiki.lockExpiryTime";

    /**
     * Returns the page provider currently in use.
     *
     * @return A WikiPageProvider instance.
     */
    PageProvider getProvider();

	/**
	 * Returns only of upper level pages.
	 * 
	 * @return A collection of upper level WikiPages.
	 * @throws ProviderException
	 */
	Collection<WikiPage> getUpperPages() throws ProviderException;

    /**
     * Returns all pages in some random order.  If you need just the page names,
     * please see {@link Id2NamePage#getAllPageNames()}, which is probably a lot
     * faster.  This method may cause repository access.
     *
     * @return A Collection of WikiPage objects.
     * @throws ProviderException If the backend has problems.
     */
    Collection< WikiPage > getAllPages() throws ProviderException;

    /**
     * Fetches the page text from the repository.  This method also does some sanity checks, like checking for the pageName validity, etc.
     * Also, if the page repository has been modified externally, it is smart enough to handle such occurrences.
     *
     * @param pageName The name of the page to fetch.
     * @param version  The version to find
     * @return The page content as a raw string
     * @throws ProviderException If the backend has issues.
     */
    String getPageText( String pageName, int version ) throws ProviderException;

	String getPureText(WikiPage page, int version);

    /**
     *  Returns the pure text of a page, no conversions.  Use this if you are writing something that depends on the parsing
     *  of the page. Note that you should always check for page existence through pageExists() before attempting to fetch
     *  the page contents.
     *
     *  This method is pretty similar to {@link #getPageText(String, int)}, except that it doesn't throw {@link ProviderException},
     *  it logs and swallows them.
     *
     *  @param page The name of the page to fetch.
     *  @param version If WikiPageProvider.LATEST_VERSION, then uses the latest version.
     *  @return The page contents.  If the page does not exist, returns an empty string.
     */
    String getPureText( String page, int version );

    /**
     *  Returns the pure text of a page, no conversions.  Use this if you are writing something that depends on the parsing
     *  the page. Note that you should always check for page existence through pageExists() before attempting to fetch
     *  the page contents.
     *
     *  This method is pretty similar to {@link #getPageText(String, int)}, except that it doesn't throw {@link ProviderException},
     *  it logs and swallows them.
     *
     *  @param page A handle to the WikiPage
     * @param pageVersion TODO
     *  @return String of WikiText.
     *  @since 2.1.13, moved to PageManager on 2.11.0.
     */
	default String getPureText(WikiPage page, int... pageVersion) {
		int pageVersion1 = (pageVersion.length == 0) ? WikiProvider.LATEST_VERSION : pageVersion[0];
		return getPureText(page, pageVersion1);
	}

	/**
	 * Returns the un-HTMLized text of the given version of a page. This method also replaces the &lt;
	 * and &amp; -characters with their respective HTML entities, thus making it suitable for inclusion
	 * on an HTML page. If you want to have the page text without any conversions, use
	 * {@link #getPureText(String, int)}.
	 *
	 * @param page    WikiName of the page to fetch
	 * @param version Version of the page to fetch
	 * @return WikiText.
	 */
//:FVK:	String getText(WikiPage page, int version);

    String getText( String pageName, int version );

    /**
     *  Returns the un-HTMLized text of the latest version of a page. This method also replaces the &lt; and &amp; -characters with
     *  their respective HTML entities, thus making it suitable for inclusion on an HTML page.  If you want to have the page text
     *  without any conversions, use {@link #getPureText(String, int)}.
     *
     *  @param page WikiName of the page to fetch.
     *  @return WikiText.
     */
    default String getText( final String page ) {
        return getText( page, PageProvider.LATEST_VERSION );
    }

    /**
     *  Returns the un-HTMLized text of the given version of a page in the given context.  USE THIS METHOD if you don't know what doing.
     *  <p>
     *  This method also replaces the &lt; and &amp; -characters with their respective HTML entities, thus making it suitable
     *  for inclusion on an HTML page.  If you want to have the page text without any conversions, use {@link #getPureText(WikiPage, int)}.
     *
     *  @since 1.9.15.
     *  @param page A page reference (not an attachment)
     * @param pageVersion TODO
     *  @return The page content as HTMLized String.
     *  @see PageManager#getPureText(WikiPage, int)
     */
    default String getText( final WikiPage page, int pageVersion ) {
        return getText( page, pageVersion );
    }

	/**
	 * Writes the WikiText of a page into the page repository. If the <code>preferences.ini</code>
	 * file contains the property <code>jspwiki.approver.workflow.saveWikiPage</code> and its value
	 * resolves to a valid user, {@link IGroupWiki} or {@link org.elwiki.data.authorize.GroupPrincipal},
	 * this method will place a {@link org.apache.wiki.workflow.IDecision} in the approver's workflow
	 * inbox and throw a {@link org.apache.wiki.workflow.DecisionRequiredException}. If the submitting
	 * user is authenticated and the page save is rejected, a notification will be placed in the user's
	 * decision queue.
	 *
	 * @since 2.1.28, moved to PageManager on 2.11.0
	 * @param context The current WikiContext
	 * @param text    The Wiki markup for the page.
	 * @throws WikiException if the save operation encounters an error during the save operation. If the
	 *                       page-save operation requires approval, the exception will be of type
	 *                       {@link org.apache.wiki.workflow.DecisionRequiredException}. Individual
	 *                       PageFilters, such as the {@link org.apache.wiki.filters.filters.SpamFilter}
	 *                       may also throw a {@link org.apache.wiki.api.exceptions.RedirectException}.
	 */
	void saveText(WikiContext context, String text, String author, String changenote) throws WikiException;

	/**
	 * Writes the comment text of a page into the page repository.
	 * 
	 * @param wikiContext The current WikiContext.
	 * @param comment     The Wiki markup for the comment.
	 * @throws WikiException TODO
	 */
	void savePageComment(WikiContext wikiContext, String comment) throws WikiException;

    /**
     * Puts the page text into the repository.  Note that this method does NOT update
     * JSPWiki internal data structures, and therefore you should always use saveText()
     *
     * @param page    Page to save
     * @param content Wikimarkup to save
     * @param author TODO:
     * @param changenote TODO:
     * @throws ProviderException If something goes wrong in the saving phase
     */
    void putPageText( WikiPage page, String content, String author, String changenote ) throws ProviderException;

    /**
     * Locks page for editing.  Note, however, that the PageManager will in no way prevent you from actually editing this page;
     * the lock is just for information.
     *
     * @param page WikiPage to lock
     * @param user Username to use for locking
     * @return null, if page could not be locked.
     */
    PageLock lockPage( WikiPage page, String user );

    /**
     * Marks a page free to be written again.  If there has not been a lock, will fail quietly.
     *
     * @param lock A lock acquired in lockPage().  Safe to be null.
     */
    void unlockPage( PageLock lock );

    /**
     * Returns the current lock owner of a page.  If the page is not locked, will return null.
     *
     * @param page The page to check the lock for
     * @return Current lock, or null, if there is no lock
     */
    PageLock getCurrentLock( WikiPage page );

    /**
     * Returns a list of currently applicable locks.  Note that by the time you get the list,
     * the locks may have already expired, so use this only for informational purposes.
     *
     * @return List of PageLock objects, detailing the locks.  If no locks exist, returns an empty list.
     * @since 2.0.22.
     */
    List< PageLock > getActiveLocks();

    /**
     *  Finds the corresponding WikiPage object based on the page name.  It always finds
     *  the latest version of a page.
     *
     *  @param pagereq The name of the page to look for.
     *  @return A WikiPage object, or null, if the page by the name could not be found.
     */
    WikiPage getPage( String pagereq );

    /**
     *  Finds the corresponding WikiPage object base on the page name and version.
     *
     *  @param pagereq The name of the page to look for.
     *  @param version The version number to look for.  May be WikiProvider.LATEST_VERSION,
     *  in which case it will look for the latest version (and this method then becomes
     *  the equivalent of getPage(String).
     *
     *  @return A WikiPage object, or null, if the page could not be found; or if there
     *  is no such version of the page.
     *  @since 1.6.7 (moved to PageManager on 2.11.0).
     */
    WikiPage getPage( String pagereq, int version );

    /**
     * Finds a WikiPage object describing a particular page and version.
     *
     * @param pageName The name of the page
     * @param version  A version number
     * @return A WikiPage object, or null, if the page does not exist
     * @throws ProviderException If there is something wrong with the page name or the repository
     */
    WikiPage getPageInfo( String pageName, int version ) throws ProviderException;

    /**
     * Gets a version history of page.  Each element in the returned List is a WikiPage.
     *
     * @param pageName The name of the page or attachment to fetch history for
     * @return If the page does not exist or there's some problem retrieving the version history, returns null,
     *         otherwise a List of WikiPages / Attachments, each corresponding to a different revision of the page / attachment.
     */
	List<PageContent> getVersionHistory(WikiPage page);

    /**
     *  Returns the provider name.
     *
     *  @return The full class name of the current page provider.
     */
    String getCurrentProvider();

    /**
     * Returns a human-readable description of the current provider.
     *
     * @return A human-readable description.
     */
    String getProviderDescription();

    /**
     * Returns the total count of all pages in the repository. This
     * method is equivalent of calling getAllPages().size(), but
     * it swallows the ProviderException and returns -1 instead of
     * any problems.
     *
     * @return The number of pages, or -1, if there is an error.
     */
    int getTotalPageCount();

    /**
     *  Returns a Collection of WikiPages, sorted in time order of last change (i.e. first object is the most recently changed).
     *  This method also includes attachments.
     *
     *  @return Set of WikiPage objects.
     */
    Set< WikiPage > getRecentChanges();

    /**
     * Returns true, if the page exists (any version) on the underlying WikiPageProvider.
     *
     * @param pageName Name of the page.
     * @return A boolean value describing the existence of a page
     * @throws ProviderException If the backend fails or the name is illegal.
     */
    boolean pageExists( String pageName ) throws ProviderException;

    /**
     * Checks for existence of a specific page and version on the underlying WikiPageProvider.
     *
     * @param pageName Name of the page
     * @param version  The version to check
     * @return <code>true</code> if the page exists, <code>false</code> otherwise
     * @throws ProviderException If backend fails or name is illegal
     * @since 2.3.29
     */
    boolean pageExists( String pageName, int version ) throws ProviderException;

    /**
     *  Checks for existence of a specific page and version denoted by a WikiPage on the underlying WikiPageProvider.
     *
     *  @param page A WikiPage object describing the name and version.
     *  @return true, if the page (or alias, or attachment) exists.
     *  @throws ProviderException If something goes badly wrong.
     *  @since 2.0
     */
	default boolean pageExists(final WikiPage page) throws ProviderException {
		if (page != null) {
			return pageExistsById(page.getId());
		}
		return false;
	}

	/**
	 * Returns true, if the requested page exists. Will consider any version as existing.
	 * 
	 * @param pageId ID of the page.
	 * @return true, if page exists.
	 */
	boolean pageExistsById(String pageId);
    
    /**
     *  Returns true, if the requested page (or an alias) exists.  Will consider any version as existing. Will check for all types of
     *  WikiPages: wiki pages themselves, attachments and special pages (non-existant references to other pages).
     *
     *  @param page WikiName of the page.
     *  @return true, if page (or attachment) exists.
     */
    boolean pageExistsByName( String page );

    /**
     *  Returns true, if the requested page (or an alias) exists with the requested version. Will check for all types of
     *  WikiPages: wiki pages themselves, attachments and special pages (non-existant references to other pages).
     *
     *  @param page Page name
     *  @param version Page version
     *  @return True, if page (or alias, or attachment) exists
     *  @throws ProviderException If the provider fails.
     */
    boolean wikiPageExists( String page, int version ) throws ProviderException;

    /**
     *  Returns true, if the requested page (or an alias) exists, with the specified version in the WikiPage. Will check for all types of
     *  WikiPages: wiki pages themselves, attachments and special pages (non-existant references to other pages).
     *
     *  @param page A WikiPage object describing the name and version.
     *  @return true, if the page (or alias, or attachment) exists.
     *  @throws ProviderException If something goes badly wrong.
     *  @since 2.0
     */
    default boolean wikiPageExists( final WikiPage page ) throws ProviderException {
        if( page != null ) {
            return wikiPageExists( page.getName(), page.getLastVersion() );
        }
        return false;
    }

    /**
     * Deletes only a specific version of a WikiPage.
     *
     * @param page The page to delete.
     * @throws ProviderException if the page fails
     */
    void deleteVersion( WikiPage page ) throws ProviderException;

    /**
     *  Deletes a page or an attachment completely, including all versions.  If the page does not exist, does nothing.
     *
     * @param pageName The name of the page.
     * @throws ProviderException If something goes wrong.
     */
    void deletePage( String pageName ) throws ProviderException;

    /**
     * Deletes an entire page, all versions, all traces.
     *
     * @param page The WikiPage to delete
     * @throws ProviderException If the repository operation fails
     */
    void deletePage( WikiPage page ) throws ProviderException;

    /**
     * Returns the configured {@link PageSorter}.
     * 
     * @return the configured {@link PageSorter}.
     */
    PageSorter getPageSorter();

	/**
	 * Returns the page corresponding to the page ID.
	 * 
	 * @param pageId ID of the required page. 
	 * @return Wiki page or <code>null</code>.
	 * @throws ProviderException TODO
	 */
	WikiPage getPageById(String pageId) throws ProviderException;

	String getMainPageId();

	/**
	 * Creates new wiki page. <br/>
	 * If the ID of the parent page is incorrect, then the page is created in the wiki root.
	 *
	 * @param pageName The name of the new page.
	 * @param parentPageId ID of the parent WikiPage.
	 * @return New wiki page.
	 * @throws ProviderException TODO
	 */
	WikiPage createPage( String pageName, String parentPageId ) throws ProviderException;

	/**
	 * Add attachment metadata for specisied wiki page.
	 *
	 * @param wikiPage   specified page.
	 * @param attContent new metadata of attachment.
	 * @param attName    name of attachment.
	 * @throws Exception TODO
	 */
	void addAttachment(WikiPage wikiPage, AttachmentContent attContent, String attName) throws Exception;

	/**
	 * Returns the PageAttachment corresponding to the page attachment ID.
	 * 
	 * @param pageAttachmentId page attachment ID.
	 * @return
	 * @throws Exception TODO
	 */
	PageAttachment getPageAttachmentById(String pageAttachmentId) throws Exception;

	void updateReferences(WikiPage page, Collection<String> pagesIds, Collection<String> unknownPages) throws ProviderException;

    /**
     * Finds all references that refer to this page.
     * Returns a collection containing {@link PageReference} that refer to this one.<br/>
     * The page version is not taken into account.
     * <p>
     * @param pageId The page ID to find referrers for.
     * @return A List of {@link PageReference}. May return empty, if it has no references.
     * @throws WikiException TODO
     */
	List<PageReference> getPageReferrers(String pageId) throws WikiException;

	/**
	 * Returns a collection of pages containing links to Undefined Pages (pages containing dead links).
	 *
	 * @return Collection of pages containing links to Undefined Pages.
	 * @throws ProviderException
	 */
	Collection<WikiPage> getReferrersToUncreatedPages() throws ProviderException;

	/**
	 * Returns a collection of names of pages that do not exist in the wiki.
	 *
	 * @return Collection of names of pages that do not exist in the wiki.
	 * @throws ProviderException
	 */
	Collection<UnknownPage> getUnknownPages() throws ProviderException;

	/**
	 * Returns a collection of pages that are not linked to in other pages.
	 *
	 * @return Collection of pages that are not linked to in other pages.
	 */
	Collection<WikiPage> getUnreferencedPages() throws ProviderException;

	/**
	 * Moves the page. 
	 *
	 * @param motionType
	 * @param targetPageId
	 * @param movedPageId
	 * @throws ProviderException TODO
	 */
	void movePage(PageMotionType motionType, String targetPageId, String movedPageId) throws ProviderException;

}
