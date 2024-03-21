package org.apache.wiki.api.rss;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.NoSuchVariableException;
import org.apache.wiki.api.variables.VariableManager;
import org.elwiki.api.GlobalPreferences;

public interface IFeed {

	/** Wiki variable storing the blog's name. */
    public static final String VAR_BLOGNAME = "blogname";

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

    /**
     * Figure out a site name for a feed.
     *
     * @param context the wiki context
     * @return the site name
     */
    public static String getSiteName( final WikiContext context ) {
        final Engine engine = context.getEngine();
        VariableManager variableManager = engine.getManager(VariableManager.class);

        String blogname = null;

        try {
            blogname = variableManager.getValue(context, VAR_BLOGNAME);
        } catch( final NoSuchVariableException e ) {
        }

        if (blogname == null) {
            blogname = engine.getManager(GlobalPreferences.class).getApplicationName() + ": " + context.getPage().getName();
        }

        return blogname;
    }

}