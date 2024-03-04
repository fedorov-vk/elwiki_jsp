/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.elwiki.preferences.ui.page;

import org.apache.wiki.api.core.Engine;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.elwiki.preferences.ui.IWikiPreferencePage;

/*
 * A page used as a filler for nodes in the preference tree
 * for which no page is supplied.
 */
public class EmptyPreferencePage extends PreferencePage implements IWikiPreferencePage {
	private static final long serialVersionUID = 1L;

	protected Control createContents(Composite parent) {
		return new Composite(parent, SWT.NULL);
	}

	/**
	 * Hook method to get a page specific preference store.<br/>
	 * Reimplement this method if a page don't want to use its parent's preference store.
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		//TODO: was returns the instance of ScopedPreferenceStore:
		//result = PluginActivator.getDefault().getPreferenceStore();
		IPreferenceStore result = null;
		return result;
	}

	/**
	 * @see IWikiPreferencePage
	 */
	public void init(Engine engine) {
	}
}
