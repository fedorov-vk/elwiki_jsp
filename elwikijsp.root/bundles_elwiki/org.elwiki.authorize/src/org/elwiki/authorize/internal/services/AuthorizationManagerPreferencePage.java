package org.elwiki.authorize.internal.services;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.auth.AuthorizationManager;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.elwiki.preferences.ui.IWikiPreferencePage;

public class AuthorizationManagerPreferencePage extends FieldEditorPreferencePage implements IWikiPreferencePage {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the preference page.
	 */
	public AuthorizationManagerPreferencePage() {
		super(FLAT);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		// Create the field editors
		addField(new StringFieldEditor(AuthorizationManager.Prefs.AUTHORIZER_ID, "Authorizer ID", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(Engine engine) {
		AuthorizationManager authorizationManager = engine.getManager(AuthorizationManager.class);
		if (authorizationManager != null) {
			setPreferenceStore(authorizationManager.getPreferenceStore());
		}
	}

}
