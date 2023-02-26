package org.apache.wiki.api.rss;

import org.elwiki_data.WikiPage;

public interface IEntry {

	/**
	 * Set the author of this entry.
	 *
	 * @param author Name of the author.
	 */
	void setAuthor(String author);

	/**
	 * Return the author set by setAuthor().
	 *
	 * @return A String representing the author.
	 */
	String getAuthor();

	/**
	 * Returns the page set by {@link #setPage(WikiPage)}.
	 *
	 * @return The WikiPage to which this Entry refers to.
	 */
	WikiPage getPage();

	/**
	 * Sets the WikiPage to which this Entry refers to.
	 *
	 * @param p A valid WikiPage.
	 */
	void setPage(WikiPage p);

	/**
	 * Sets a title for the change.  For example, a WebLog entry might use the
	 * post title, or a Wiki change could use something like "XXX changed page YYY".
	 *
	 * @param title A String description of the change.
	 */
	void setTitle(String title);

	/**
	 * Returns the title.
	 *
	 * @return The title set in setTitle.
	 */
	String getTitle();

	/**
	 * Set the URL - the permalink - of the Entry.
	 *
	 * @param url An absolute URL to the entry.
	 */
	void setURL(String url);

	/**
	 * Return the URL set by setURL().
	 *
	 * @return The URL.
	 */
	String getURL();

	/**
	 * Set the content of this entry.
	 *
	 * @param content A String of the content.
	 */
	void setContent(String content);

	/**
	 * Return the content set by {@link #setContent(String)}.
	 *
	 * @return Whatever was set by setContent().
	 */
	String getContent();

}