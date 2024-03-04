package org.apache.wiki.pages;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.pages0.PageManager;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.elwiki.preferences.ui.IWikiPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;

public class PageManagerPreferencePage extends FieldEditorPreferencePage implements IWikiPreferencePage {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the preference page.
	 */
	public PageManagerPreferencePage() {
		super(FLAT);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		// Create the field editors
		addField(new StringFieldEditor(PageManager.Prefs.PAGE_MANAGER, "Page provider Id", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(Engine engine) {
		PageManager pageManager = engine.getManager(PageManager.class);
		if (pageManager != null) {
			setPreferenceStore(pageManager.getPreferenceStore());
		}
	}

}
