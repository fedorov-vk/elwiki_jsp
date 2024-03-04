package org.elwiki.authorize.internal.services;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.auth.AuthenticationManager;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.elwiki.preferences.ui.IWikiPreferencePage;

public class AuthenticationManagerPreferencePage extends FieldEditorPreferencePage implements IWikiPreferencePage {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the preference page.
	 */
	public AuthenticationManagerPreferencePage() {
		super(FieldEditorPreferencePage.FLAT);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		// Create the field editors
		addField(new BooleanFieldEditor(AuthenticationManager.Prefs.ALLOW_COOKIE_ASSERTIONS,
				"Cookie assertions", BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(AuthenticationManager.Prefs.ALLOW_COOKIE_AUTH,
				"Cookie authentication", BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(AuthenticationManager.Prefs.LOGIN_THROTTLING,
				"Whether logins should be throttled to limit bruce-force attempts.", BooleanFieldEditor.DEFAULT,
				getFieldEditorParent()));
		addField(new StringFieldEditor(AuthenticationManager.Prefs.LOGIN_MODULE_ID, "Login module ID", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
	}

	/**
	 * Initialize the preference page.
	 */
	@Override
	public void init(Engine engine) {
		AuthenticationManager authenticationManager = engine.getManager(AuthenticationManager.class);
		if (authenticationManager != null) {
			setPreferenceStore(authenticationManager.getPreferenceStore());
		}
	}

}
