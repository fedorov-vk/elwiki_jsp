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
package org.apache.wiki.api.providers;

import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.UnknownPage;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.search.QueryItem;
import org.apache.wiki.api.search.SearchResult;
import org.apache.wiki.pages0.PageManager.PageMotionType;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *  Each Wiki page provider should implement this interface.
 *  <P>
 *  You can build whatever page providers based on this, just leave the unused methods do something useful.
 *  <P>
 *  WikiPageProvider uses Strings and ints to refer to pages.  This may be a bit odd, since WikiAttachmentProviders all use Attachment
 *  instead of name/version.  We will perhaps modify these in the future.  In the mean time, name/version is quite sufficient.
 *  <P>
 *  FIXME: In reality we should have an AbstractWikiPageProvider, which would provide intelligent backups for subclasses.
 */
public interface PageProvider extends WikiProvider {

    /**
     *  Attempts to save the page text for page "page".  Note that the provider creates a new version regardless of what the version
     *  parameter of the WikiPage is.
     *  
     *  @param page The WikiPage to save
     *  @param text The text to save.
     *  @throws ProviderException If something goes wrong.
     */
    void putPageText( WikiPage page, String text, String author, String changenote ) throws ProviderException;

	/**
	 * Saves comment text of page.
	 *
	 * @param page    The target WikiPage to save comment.
	 * @param comment Text of the comment.
	 * @throws ProviderException TODO
	 */
	void savePageComment(WikiPage page, String comment) throws ProviderException;

	/**
	 * Return true, if page exists.
	 * 
	 * @param pageId ID of the page.
	 * @return true, if page exists.
	 */
	boolean pageExistsById(String pageId);

	/**
	 * Return true, if page exists.
	 * 
	 * @param page The page name.
	 * @return true, if the page exists; false otherwise.
	 */
	boolean pageExistsByName(String page);

    /**
     * Return true, if page with a particular version exists.
     *
     * @param page    The page name to check for
     * @param version The version to check
     * @return True, if page exists; false otherwise.
     * @throws ProviderException If something goes wrong.
     */
    boolean pageExists( String page, int version ) throws ProviderException;

    /**
     *  Finds pages based on the query.   Only applicable to providers which implement the FastSearch interface.  Otherwise JSPWiki
     *  will use its internal cache.
     *  <p>
     *  This method should really be a part of the FastSearch IF.
     *  
     *  @param query An array of QueryItems to match
     *  @return A Collection of WikiPages.
     */
    Collection< SearchResult > findPages( QueryItem[] query );

    /**
     *  Returns info about the page.
     *  
     *  @return A filled WikiPage.
     *  @param page The page name
     *  @param version The version number
     *  @throws ProviderException If something goes wrong.
     */
    WikiPage getPageInfo( String page, int version ) throws ProviderException;

    /**
     * Returns only of upper level pages.
     * 
     * @return A collection of upper level WikiPages.
     * @throws ProviderException If something goes wrong.
     */
    Collection< WikiPage > getUpperPages() throws ProviderException;

    /**
     *  Returns all pages.  Each element in the returned Collection should be a WikiPage.
     *  
     *  @return A collection of WikiPages
     *  @throws ProviderException If something goes wrong.
     */
    Collection< WikiPage > getAllPages() throws ProviderException;

    /**
     *  Gets a list of recent changes.
     *  
     *  @param date The date to check from
     *  @return A Collection of WikiPages
     *  @since 1.6.4
     */
    Collection< WikiPage > getAllChangedSince( Date date );

    /**
     *  Gets the number of pages.
     *  
     *  @return The number of pages in the repository
     *  @throws ProviderException If something goes wrong
     *  @since 1.6.4
     */
    int getPageCount() throws ProviderException;

    /**
     *  Returns version history.  Each element should be a WikiPage.
     *
     *  @param page The name of the page to get the history from.
     *  @return A collection of WikiPages.
     *  @throws ProviderException If something goes wrong.
     */
	List<PageContent> getVersionHistory(WikiPage page) throws ProviderException;

    /**
     *  Gets a specific version out of the repository.
     *
     *  @param page Name of the page to fetch.
     *  @param version Version of the page to fetch.
     *  @return The content of the page, or null, if the page does not exist.
     *  @throws ProviderException If something goes wrong.
     */
    String getPageText( String page, int version ) throws ProviderException;

    /**
     *  Removes a specific version from the repository.  The implementations should really do no more security checks, since that is the
     *  domain of the PageManager.  Just delete it as efficiently as you can.
     *
     *  @since 2.0.17.
     *  @param pageName Name of the page to be removed.
     *  @param version  Version of the page to be removed.  May be LATEST_VERSION.
     *  @throws ProviderException If the page cannot be removed for some reason.
     */
    void deleteVersion( String pageName, int version ) throws ProviderException;

    /**
     *  Removes an entire page from the repository.  The implementations should really do no more security checks, since that is the domain
     *  of the PageManager.  Just delete it as efficiently as you can.  You should also delete any auxiliary files that belong to this page,
     *  IF they were created by this provider.
     *
     *  <P>The reason why this is named differently from deleteVersion() (logically, this method should be an overloaded version) is that
     *  I want to be absolutely sure I don't accidentally use the wrong method.  With overloading something like that happens sometimes...
     *  <p>
     *  Return operation status: =true - page removed successfully; =false - something went wrong.
     *
     *  @since 2.0.17.
     *  @param pageName Name of the page to be removed completely.
     * @return result of the operation. True/False.
     *  @throws ProviderException If the page could not be removed for some reason.
     */
    boolean deletePage( String pageName ) throws ProviderException;

    /**
     * Move a page
     *
     * @param from  Name of the page to move.
     * @param to    New name of the page.
     * @throws ProviderException If the page could not be moved for some reason.
     */
    void movePage( String from, String to ) throws ProviderException;

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
	 * If the parent page is set to null, then the page is created in the wiki root.
	 * 
	 * @param name The name of page.
	 * @param content The content of page.
	 * @param parentPage Parent page of new page.
	 * @return New wiki page.
	 */
	WikiPage createPage(String name, String content, WikiPage parentPage);

	/**
	 * Add attachment metadata for specisied wiki page.<br/>
	 * If the operation is successful, also increases the attachments ID of data model.
	 * 
	 * @param wikiPage   specified page.
	 * @param attContent new metadata of attachment.
	 * @param attName    name of attachment.
	 */
	void addAttachment(WikiPage wikiPage, AttachmentContent attContent, String attName) throws Exception;

	/**
	 * Returns the PageAttachment corresponding to the page attachment ID.
	 * 
	 * @param pageAttachmentId page attachment ID.
	 * @return
	 * @throws Exception if something goes wrong with the backend.
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
     * @throws ProviderException If something goes wrong.
     */
	List<PageReference> getPageReferrers(String pageId) throws ProviderException;

	List<UnknownPage> getUnknownPages() throws ProviderException;

	/**
	 * Returns a collection of pages that are not linked to in other pages.
	 *
	 * @return Collection of pages that are not linked to in other pages.
	 * @throws ProviderException if something goes wrong with the backend.
	 */
	Collection<PageReference> getPageReferences() throws ProviderException;


	/**
	 * Returns the attachment corresponding to the attachment ID.
	 * 
	 * @param attachmentId ID of the required attachment.
	 * @return PageAttachment or <code>null</code>.
	 * @throws ProviderException if something goes wrong with the backend.
	 */
	PageAttachment getAttachmentById(String attachmentId) throws ProviderException;

	/**
	 * Removes the page attachment from the repository.<br/>
	 * Return operation status: =true - attachment removed successfully; =false - something went wrong.
	 * 
	 * @param attachmentId ID to delete the specified attachment.
	 * @return result of the operation. True/False.
	 * @throws ProviderException if something goes wrong with the backend.
	 */
	boolean deleteAttachment(String attachmentId) throws ProviderException;

	/**
	 * Removes the page attachment from the repository.<br/>
	 * Return operation status: =true - attachment removed successfully; =false - something went wrong.
	 * 
	 * @param attachment The attachment to delete.
	 * @return result of the operation. True/False.
	 * @throws ProviderException if something goes wrong with the backend.
	 */
	boolean deleteAttachment(PageAttachment attachment) throws ProviderException;

	/**
	 * Moves the page. 
	 *
	 * @param motionType
	 * @param targetPageId
	 * @param movedPageId
	 * @throws ProviderException TODO
	 */
	void movePage(PageMotionType motionType, String targetPageId, String movedPageId) throws ProviderException;

	void setPageTimestamp(WikiPage wikiPage, Date time);

}
