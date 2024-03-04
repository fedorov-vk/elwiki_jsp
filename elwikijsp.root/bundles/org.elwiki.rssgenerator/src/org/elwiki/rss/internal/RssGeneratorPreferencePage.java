package org.elwiki.rss.internal;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.rss.RssGenerator;
import org.apache.wiki.pages0.PageManager;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.elwiki.preferences.ui.IWikiPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;

public class RssGeneratorPreferencePage extends FieldEditorPreferencePage implements IWikiPreferencePage {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the preference page.
	 */
	public RssGeneratorPreferencePage() {
		super(FLAT);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		// Create the field editors
		addField(new BooleanFieldEditor(RssGenerator.Prefs.RSS_GENERATE, "RSS generate", BooleanFieldEditor.DEFAULT,
				getFieldEditorParent()));
		addField(new StringFieldEditor(RssGenerator.Prefs.RSS_FILENAME, "RSS filename", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
		addField(new IntegerFieldEditor(RssGenerator.Prefs.RSS_INTERVAL, "RSS interval", getFieldEditorParent()));
		addField(new StringFieldEditor(RssGenerator.Prefs.RSS_CHANNEL_DESCRIPTION, "RSS channel desription", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
		addField(new StringFieldEditor(RssGenerator.Prefs.RSS_CHANNEL_LANGUAGE, "RSS channel language", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(Engine engine) {
		RssGenerator rssGenerator = engine.getManager(RssGenerator.class);
		if (rssGenerator != null) {
			setPreferenceStore(rssGenerator.getPreferenceStore());
		}
	}

}
