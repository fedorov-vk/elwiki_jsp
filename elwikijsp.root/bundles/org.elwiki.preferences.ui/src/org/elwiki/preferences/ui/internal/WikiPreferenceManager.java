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
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jan-Hendrik Diederich, Bredex GmbH - bug 201052
 * Original file: WorkbenchPreferenceManager.java
 *******************************************************************************/
package org.elwiki.preferences.ui.internal;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.elwiki.preferences.ui.internal.bundle.PluginActivator;
import org.elwiki.preferences.ui.node.WikiPreferenceNode;
import org.elwiki.preferences.ui.registry.IWikiRegistryConstants;
import org.elwiki.preferences.ui.registry.PreferencePageRegistryReader;

public class WikiPreferenceManager extends PreferenceManager implements IExtensionChangeHandler {
	private static final long serialVersionUID = 1L;

	/** The character used to separate preference page category ids. */
	public static final char PREFERENCE_PAGE_CATEGORY_SEPARATOR = '/';

	public static final String MAIN_PREFERENCE_PAGE_ID = "root";

	private static WikiPreferenceManager preferenceManager;

	public static WikiPreferenceManager Instance() {
		if (preferenceManager == null) {
			preferenceManager = new WikiPreferenceManager(PREFERENCE_PAGE_CATEGORY_SEPARATOR);
			// Get the pages from the registry
			PreferencePageRegistryReader registryReader = new PreferencePageRegistryReader();
			registryReader.loadFromRegistry(Platform.getExtensionRegistry());
			preferenceManager.addPages(registryReader.getTopLevelNodes());
		}
		return preferenceManager;
	}

	/**
	 * Create a new instance of the receiver with the specified seperatorChar.
	 *
	 * @param separatorChar
	 */
	private WikiPreferenceManager(char separatorChar) {
		super(separatorChar, new PreferenceNode(MAIN_PREFERENCE_PAGE_ID));

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extPoint = registry.getExtensionPoint( //
				IWikiRegistryConstants.PLUGIN_EXTENSION_NAME_SPACE, IWikiRegistryConstants.PL_PREFERENCES);
		IExtensionTracker tracker = PluginActivator.getDefault().getExtensionTracker();
		tracker.registerHandler(this, ExtensionTracker.createExtensionPointFilter(extPoint));
	}

	@Override
	protected IPreferenceNode getRoot() {
		return super.getRoot();
	}

	/**
	 * Add the pages and the groups to the receiver.
	 * 
	 * @param pageContributions
	 */
	private void addPages(Collection<IPreferenceNode> pageContributions) {
		// Add the contributions to the manager
		for (IPreferenceNode page : pageContributions) {
			if (page instanceof WikiPreferenceNode wikiPreferenceNode) {
				addToRoot(wikiPreferenceNode);
				registerNode(wikiPreferenceNode);
			}
		}
	}

	/**
	 * Register a node with the extension tracker.
	 * 
	 * @param node register the given node and its subnodes with the extension tracker
	 */
	private void registerNode(WikiPreferenceNode node) {
		PluginActivator.getDefault().getExtensionTracker().registerObject(
				node.getConfigurationElement().getDeclaringExtension(), node, IExtensionTracker.REF_WEAK);
		IPreferenceNode[] subNodes = node.getSubNodes();
		for (int i = 0; i < subNodes.length; i++) {
			registerNode((WikiPreferenceNode) subNodes[i]);
		}
	}

	@Override
	public void addExtension(IExtensionTracker tracker, IExtension extension) {
		// TODO Auto-generated method stub
		System.err.println("addExtension()");

		IConfigurationElement[] elements = extension.getConfigurationElements();
		for (IConfigurationElement cfgElement : elements) {
			WikiPreferenceNode node = PreferencePageRegistryReader.createNode(cfgElement);
			if (node != null) {
				registerNode(node);
				String category = node.getCategory();
				if (category == null) {
					addToRoot(node);
				} else {
					IPreferenceNode parent = null;
					for (Iterator<?> j = super.getElements(PreferenceManager.POST_ORDER).iterator(); j.hasNext();) {
						IPreferenceNode element = (IPreferenceNode) j.next();
						if (category.equals(element.getId())) {
							parent = element;
							break;
						}
					}
					if (parent == null) {
						// Could not find the parent - log
						// TODO: :FVK: WorkbenchPlugin.log("Invalid preference page path: " + category); //$NON-NLS-1$
						addToRoot(node);
					} else {
						parent.add(node);
					}
				}
			}
		}
	}

	@Override
	public void removeExtension(IExtension extension, Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof IPreferenceNode) {
				IPreferenceNode wNode = (IPreferenceNode) objects[i];
				wNode.disposeResources();
				deepRemove(getRoot(), wNode);
			}
		}
	}

	/**
	 * Removes the node from the manager, searching through all subnodes.
	 * 
	 * @param parent       the node to search
	 * @param nodeToRemove the node to remove
	 * @return whether the node was removed
	 */
	private boolean deepRemove(IPreferenceNode parent, IPreferenceNode nodeToRemove) {
		if (parent == nodeToRemove) {
			if (parent == getRoot()) {
				removeAll(); // we're removing the root
				return true;
			}
		}

		if (parent.remove(nodeToRemove)) {
			return true;
		}

		IPreferenceNode[] subNodes = parent.getSubNodes();
		for (int i = 0; i < subNodes.length; i++) {
			if (deepRemove(subNodes[i], nodeToRemove)) {
				return true;
			}
		}
		return false;
	}

}
