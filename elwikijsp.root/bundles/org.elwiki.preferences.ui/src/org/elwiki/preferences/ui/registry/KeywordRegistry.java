/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.elwiki.preferences.ui.registry;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.elwiki.preferences.ui.internal.bundle.PluginActivator;

/**
 * Contains extensions defined on the <code>keywords</code> extension point.
 */
public final class KeywordRegistry implements IExtensionChangeHandler {

	private static final String ATT_ID = "id"; //$NON-NLS-1$

	private static final String ATT_LABEL = "label"; //$NON-NLS-1$

	private static KeywordRegistry instance;

	private static final String TAG_KEYWORD = "keyword"; //$NON-NLS-1$

	/**
	 * Return the singleton instance of the <code>KeywordRegistry</code>.
	 * 
	 * @return the singleton registry
	 */
	public static KeywordRegistry getInstance() {
		if (instance == null) {
			instance = new KeywordRegistry();
		}

		return instance;
	}

	/** Map of id->labels. */
	private Map<String, String> internalKeywordMap = new HashMap<>();

	/**
	 * Private constructor.
	 */
	private KeywordRegistry() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extPoint = registry.getExtensionPoint( //
				IWikiRegistryConstants.PLUGIN_EXTENSION_NAME_SPACE, IWikiRegistryConstants.PL_KEYWORDS);

		IExtensionTracker tracker = PluginActivator.getDefault().getExtensionTracker();
		tracker.registerHandler(this, ExtensionTracker.createExtensionPointFilter(extPoint));

		for (IExtension extension : extPoint.getExtensions()) {
			addExtension(tracker, extension);
		}
	}

	@Override
	public void addExtension(IExtensionTracker tracker, IExtension extension) {
		IConfigurationElement[] elements = extension.getConfigurationElements();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].getName().equals(TAG_KEYWORD)) {
				String name = elements[i].getAttribute(ATT_LABEL);
				String id = elements[i].getAttribute(ATT_ID);
				internalKeywordMap.put(id, name);
				tracker.registerObject(extension, id, IExtensionTracker.REF_WEAK);
			}
		}
	}

	/**
	 * Return the label associated with the given keyword.
	 * 
	 * @param id the keyword id
	 * @return the label or <code>null</code>
	 */
	public String getKeywordLabel(String id) {
		return (String) internalKeywordMap.get(id);
	}

	@Override
	public void removeExtension(IExtension extension, Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof String) {
				internalKeywordMap.remove(objects[i]);
			}
		}
	}
}
