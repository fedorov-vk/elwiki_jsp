/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Original file: IWorkbenchPreferencePage.java
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.elwiki.preferences.ui;

import org.apache.wiki.api.core.Engine;
import org.eclipse.jface.preference.IPreferencePage;

/**
 * Interface for ElWiki preference pages.
 * <p>
 * Clients should implement this interface and include the name of their class
 * in an extension contributed to the wiki's preference extension point 
 * (named <code>"org.eclipse.ui.preferencePages"</code>).
 * For example, the plug-in's XML markup might contain:
 * <pre>
 * &LT;extension point="org.eclipse.ui.preferencePages"&GT;
 *      &LT;page id="com.example.myplugin.prefs"
 *         name="Knobs"
 *         class="com.example.myplugin.MyPreferencePage" /&GT;
 * &LT;/extension&GT;
 * </pre>
 * </p>
 */
public interface IWikiPreferencePage extends IPreferencePage {
	/**
	 * Initializes this preference page of wiki for the given Engine.
	 * <p>
	 * This method is called automatically as the preference page is being created and initialized.
	 * Clients must not call this method.
	 * </p>
	 *
	 * @param engine
	 */
	void init(Engine engine);
}
