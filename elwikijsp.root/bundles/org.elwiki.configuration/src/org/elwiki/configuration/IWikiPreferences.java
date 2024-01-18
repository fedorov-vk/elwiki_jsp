package org.elwiki.configuration;

public interface IWikiPreferences {

	String TRUE = "true";

	/** System property for define custom configuration file. Current value - {@value} */
	String SYSPROP_CUSTOM_CONFIG = "elwiki.custom.configuration";

	/** Property node for set of inline image patterns. Current value - {@value} */
	String NODE_INLINE_PATTERNS = "node.translatorReader.inlinePatterns";

	/** If true, then the user name will be stored with the page data. */
	String PROP_STOREUSERNAME = "jspwiki.storeUserName";

	/** Define the used encoding. Currently supported are ISO-8859-1 and UTF-8 */
	String PROP_ENCODING = "jspwiki.encoding";

	/** The default encoding. */
	String DEFAULT_ENCODING = "ISO-8859-1";

	/** The name for the base URL to use in all references. */
	String PROP_BASEURL = "jspwiki.baseURL";
	@Deprecated
	String DEFAULT_BASEURL = "/?servicehandler=org.elwiki.core.serviceHandler01"; // WORKAROUND.

	/** Property name for the template that is used. If not specified, sets to "default". */
	String PROP_TEMPLATEDIR = "jspwiki.templateDir";

	/** Property name for the default front page. */
	String PROP_FRONTPAGE = "jspwiki.frontPage";

}
