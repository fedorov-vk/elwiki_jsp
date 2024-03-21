package org.elwiki.preferences.ui.registry;

public interface IWikiRegistryConstants {

	/** Identifies the namespace for extension points. */
	String PLUGIN_EXTENSION_NAME_SPACE = "org.elwiki.preferences.ui"; //$NON-NLS-1$
	
	/** The extension point for keyword definitions. */
	String PL_KEYWORDS = "keywords"; //$NON-NLS-1$

	/** The extension point for preference page definitions. */
	String PL_PREFERENCES = "preferencePages"; //$NON-NLS-1$
	
	/** Class attribute. Value <code>class</code>. */
	String ATT_CLASS = "class"; //$NON-NLS-1$

	/** Icon attribute. Value <code>icon</code>. */
	String ATT_ICON = "icon"; //$NON-NLS-1$

	/** Id attribute. Value <code>id</code>. */
	String ATT_ID = "id"; //$NON-NLS-1$
	
	/** Name attribute. Value <code>name</code>. */
	String ATT_NAME = "name"; //$NON-NLS-1$
	
	/** Description element. Value <code>description</code>. */
	String TAG_DESCRIPTION = "description"; //$NON-NLS-1$

	String TAG_KEYWORD_REFERENCE = "keywordReference"; //$NON-NLS-1$
	
}
