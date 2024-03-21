package org.elwiki.api.part;

public interface Id2NamePage {

	/**
	 * Returns name of page by its Id.
	 * 
	 * @param pageId
	 * @return The page name of correspond page Id. It can return {@code null} for unknown Id of this
	 *         mapper.
	 */
	String getName(String pageId);

	/**
	 * Returns an array of all pages that the Mapper knows about. This should be roughly equivalent to
	 * PageManager.getAllPages(), but without the potential disk access overhead. Note that this method
	 * is not guaranteed to return an array of really all pages (especially during startup), but it is very
	 * fast.
	 *
	 * @return An array of all the page names that mapper knows about.
	 */
	String[] getAllPageNames();

}
