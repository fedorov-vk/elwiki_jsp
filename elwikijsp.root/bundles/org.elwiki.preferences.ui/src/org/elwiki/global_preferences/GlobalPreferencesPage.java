package org.elwiki.global_preferences;

import org.apache.wiki.api.core.Engine;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.preferences.ui.IWikiPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;

public class GlobalPreferencesPage extends FieldEditorPreferencePage implements IWikiPreferencePage {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the preference page.
	 */
	public GlobalPreferencesPage() {
		super(FLAT);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		// Create the field editors
		addField(new StringFieldEditor(GlobalPreferences.Prefs.APP_NAME, "Application name", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
		addField(new BooleanFieldEditor(GlobalPreferences.Prefs.ALLOW_CREATION_OF_EMPTY_PAGES,
				"Allow create empty pages", BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(Engine engine) {
		GlobalPreferences globalPreferences = engine.getManager(GlobalPreferences.class);
		if (globalPreferences != null) {
			setPreferenceStore(globalPreferences.getPreferenceStore());
		}
	}

}
