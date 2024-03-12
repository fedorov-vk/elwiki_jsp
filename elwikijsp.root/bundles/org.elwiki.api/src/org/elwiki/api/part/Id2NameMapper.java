package org.elwiki.api.part;

public interface Id2NameMapper {

	/**
	 * Returns name of page by its Id.
	 * 
	 * @param pageId
	 * @return The page name of correspond page Id.
	 */
	String getName(String pageId);

	/**
	 * Returns a list of all pages that the Mapper knows about. This should be
	 * roughly equivalent to PageManager.getAllPages(), but without the potential
	 * disk access overhead. Note that this method is not guaranteed to return a Set
	 * of really all pages (especially during startup), but it is very fast.
	 *
	 * @return A set of all defined page names that Id2NameMapper knows about.
	 */
	String[] getAllPagesNames();

}
