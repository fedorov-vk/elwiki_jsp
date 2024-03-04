package org.elwiki.configuration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.eclipse.jface.preference.IPreferenceStore;

public interface IWikiConfiguration {

	@Deprecated
	String PROP_URL_HANDLER01 = "elwiki.url_handler01";

	@Deprecated
	String PROP_URL_ATTACHMENT_HANDLER = "elwiki.url_attachment_handler";

	// == CODE ================================================================

	IPreferenceStore getWikiPreferences();

	/**
	 * Returns the base URL, telling where this Wiki actually lives.
	 *
	 * @since 1.6.1
	 * @return The Base URL.
	 */
	// :FVK: String getBaseURL();

	/**
	 * Returns the name of default wiki's Front Page. Used if no page is used.
	 * 
	 * @return The front page name.
	 */
	@Deprecated
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
	 * @param key the attribute name
	 * @param the value
	 */
	void setAttribute(String key, Object value);

	/**
	 * Returns an InterWiki link of required wiki name.
	 * 
	 * @param wikiName required wiki name.
	 * @return InterWiki link.
	 */
	public String getInterWikiURL(String wikiName);

	/**
	 * Returns a collection of all supported InterWiki links.
	 *
	 * @return A Collection of Strings.
	 */
	public Collection<String> getAllInterWikiLinks();

	/**
	 * Returns the base URL, telling where this Wiki actually lives.
	 *
	 * @since 1.6.1
	 * @return The Base URL.
	 */
	String getBaseURL();

	void setBaseURL(String baseURL);

	/**
	 * Fetches a String property from the set of Properties. This differs from Properties.getProperty()
	 * in a couple of key respects: First, property value is trim()med (so no extra whitespace back and
	 * front).
	 *
	 * Before inspecting the props, we first check if there is a Java System Property with the same
	 * name, if it exists we use that value, if not we check an environment variable with that (almost)
	 * same name, almost meaning we replace dots with underscores.
	 * 
	 * @param key    The property key
	 * @param defval A default value to return, if the property does not exist.
	 *
	 * @return The property value.
	 */
	String getStringProperty(String key, String defval);

	/**
	 * Throws an exception if a property is not found.
	 *
	 * @param key The key to look for.
	 * @return The required property
	 *
	 * @throws NoSuchElementException If the search key is not in the property set.
	 */
	String getRequiredProperty(String key) throws NoSuchElementException;

	/**
	 * Gets a boolean property from a standard Properties list. Returns the default value, in case the
	 * key has not been set. Before inspecting the props, we first check if there is a Java System
	 * Property with the same name, if it exists we use that value, if not we check an environment
	 * variable with that (almost) same name, almost meaning we replace dots with underscores.
	 * <P>
	 * The possible values for the property are "true"/"false", "yes"/"no", or "on"/"off". Any value not
	 * recognized is always defined as "false".
	 * 
	 * @param key    The property key.
	 * @param defval The default value to return.
	 *
	 * @return True, if the property "key" was set to "true", "on", or "yes".
	 */
	boolean getBooleanProperty(String key, boolean defval);

	/**
	 * Gets an integer-valued property from a standard Properties list.
	 *
	 * Before inspecting the props, we first check if there is a Java System Property with the same
	 * name, if it exists we use that value, if not we check an environment variable with that (almost)
	 * same name, almost meaning we replace dots with underscores.
	 *
	 * If the value does not exist, or is a non-integer, returns defVal.
	 * @param key    The key to look for
	 * @param defVal If the property is not found or is a non-integer, returns this value.
	 *
	 * @return The property value as an integer (or defVal).
	 */
	int getIntegerProperty(String key, int defVal);

}
