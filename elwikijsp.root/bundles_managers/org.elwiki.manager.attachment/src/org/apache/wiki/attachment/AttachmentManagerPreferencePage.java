package org.apache.wiki.attachment;

import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Engine;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.elwiki.preferences.ui.IWikiPreferencePage;

public class AttachmentManagerPreferencePage extends FieldEditorPreferencePage implements IWikiPreferencePage {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the preference page.
	 */
	public AttachmentManagerPreferencePage() {
		super(FieldEditorPreferencePage.FLAT);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(AttachmentManager.Prefs.DISABLE_CACHE, "Disable cache", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(Engine engine) {
		AttachmentManager attachmentManager = engine.getManager(AttachmentManager.class);
		if (attachmentManager != null) {
			setPreferenceStore(attachmentManager.getPreferenceStore());
		}
	}

}
