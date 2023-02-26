package org.apache.wiki.api.rss;

public interface IFeed {

    /**
     * Adds a new Entry to the Feed, at the end of the list.
     *
     * @param e The Entry to add.
     */
    public void addEntry( final IEntry e);

    /**
     * Returns the XML for the feed contents in a String format.  All subclasses must implement.
     *
     * @return valid XML, ready to be shoved out.
     */
    String getString();
	
	/**
	 * @return Returns the m_channelDescription.
	 */
	String getChannelDescription();

	/**
	 * @param description The m_channelDescription to set.
	 */
	void setChannelDescription(String description);

	/**
	 * @return Returns the m_channelLanguage.
	 */
	String getChannelLanguage();

	/**
	 * @param language The m_channelLanguage to set.
	 */
	void setChannelLanguage(String language);

	/**
	 * @return Returns the m_channelTitle.
	 */
	String getChannelTitle();

	/**
	 * @param title The m_channelTitle to set.
	 */
	void setChannelTitle(String title);

	/**
	 * @return Returns the m_feedURL.
	 */
	String getFeedURL();

	/**
	 * @param feedurl The m_feedURL to set.
	 */
	void setFeedURL(String feedurl);

}