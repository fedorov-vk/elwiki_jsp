package org.elwiki.api.component;

public interface IWikiPreferencesConstants {

	/** Define the used encoding. Currently supported are ISO-8859-1 and UTF-8 */
	String PROP_ENCODING = "jspwiki.encoding";

	/** Do not use encoding in WikiJSPFilter, default is false for most servers.<br/>
	    Double negative, cause for most servers you don't need the property */
	String PROP_NO_FILTER_ENCODING = "jspwiki.nofilterencoding";

}
