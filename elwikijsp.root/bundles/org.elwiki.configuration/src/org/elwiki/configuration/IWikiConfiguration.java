package org.elwiki.configuration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

public interface IWikiConfiguration {

	@Deprecated
	String PROP_URL_HANDLER01 = "elwiki.url_handler01";

	@Deprecated
	String PROP_URL_ATTACHMENT_HANDLER = "elwiki.url_attachment_handler";

	// == CODE ================================================================

	IPreferenceStore getWikiPreferences();

	/**
	 * Returns the attachment path. The attachment path is where the attachment files is located in the
	 * file system.
	 *
	 * @return A path to where the attachment files is placed in the local filesystem.
	 */
	IPath getAttachmentPath();

	/**
	 * Returns the ElWiki working directory set with "elwiki.workDir".
	 *
	 * @return The working directory.
	 */
	IPath getWorkDir();

	/**
	 * Returns the IANA name of the character set encoding we're supposed to be using right now.
	 *
	 * @since 1.5.3
	 * @return The content encoding (either UTF-8 or ISO-8859-1).
	 */
	String getContentEncoding();

	boolean isUseUtf8();

	/**
	 * Returns the base URL, telling where this Wiki actually lives.
	 *
	 * @since 1.6.1
	 * @return The Base URL.
	 */
	// :FVK: String getBaseURL();

	boolean isBeautifyTitle();

	/**
	 * Возвращает текущий каталог шаблонов.
	 * 
	 * @return Каталог шаблонов, инициализированный движком.
	 */
	String getTemplateDir();

	/**
	 * Returns the name of default wiki's Front Page.
	 * Used if no page is used.
	 * 
	 * @return The front page name.
	 */
	String getFrontPage();

	/**
	 * Gets an attribute from the configuration.
	 * 
	 * @param key
	 * @return
	 */
	public Object getAttribute(String key);

	/**
	 * Adds an attribute to the configuration for the duration of this wiki. The value is not persisted.
	 * 
	 * @param key
	 *            the attribute name
	 * @param the
	 *            value
	 */
	void setAttribute(String key, Object value);

	/**
	 * Returns a collection of all image types that get inlined.
	 *
	 * @return A Collection of Strings with a regexp pattern.
	 */
	public Collection<String> getAllInlinedImagePatterns();

	public String getInterWikiURL(String extWiki);

	/**
	 * Returns a collection of all supported InterWiki links.
	 *
	 * @return A Collection of Strings.
	 */
	public Collection<String> getAllInterWikiLinks();

	/**
	 * Returns the workspace path. The workspace path is where the workspace is located in the file
	 * system.
	 * 
	 * @return the workspace path.
	 */
	IPath getWorkspacePath();

    /**
     *  Returns the name of the application.
     *
     *  @return A string describing the name of this application.
     */
    String getApplicationName();

    /**
     *  Turns a WikiName into something that can be called through using an URL.
     *
     *  @since 1.4.1
     *  @param pagename A name. Can be actually any string.
     *  @return A properly encoded name.
     *  @throws Exception;
     *  @see #decodeName(String)
     */
    String encodeName( String pagename ) throws IOException; 

    /**
     *  Decodes a URL-encoded request back to regular life.  This properly heeds the encoding as defined in the settings file.
     *
     *  @param pagerequest The URL-encoded string to decode
     *  @return A decoded string.
     *  @see #encodeName(String)
     */
    String decodeName( String pagerequest ) throws IOException;

    /**
     *  Returns the IANA name of the character set encoding we're supposed to be using right now.
     *
     *  @since 1.5.3
     *  @return The content encoding (either UTF-8 or ISO-8859-1).
     */
    Charset getContentEncodingCs();

    /**
     *  Returns the base URL, telling where this Wiki actually lives.
     *
     *  @since 1.6.1
     *  @return The Base URL.
     */
    String getBaseURL();
    
    void setBaseURL(String baseURL);
}