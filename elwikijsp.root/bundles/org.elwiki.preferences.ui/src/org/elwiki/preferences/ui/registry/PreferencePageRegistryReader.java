/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jan-Hendrik Diederich, Bredex GmbH - bug 201052
 *******************************************************************************/
package org.elwiki.preferences.ui.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.jface.preference.IPreferenceNode;
import org.elwiki.preferences.ui.internal.WikiPreferenceManager;
import org.elwiki.preferences.ui.node.WikiPreferenceNode;

/**
 * Instances access the registry that is provided at creation time in order to determine the
 * contributed preference pages
 */
public class PreferencePageRegistryReader extends CategorizedPageRegistryReader {

	private static final String TAG_PAGE = "page"; //$NON-NLS-1$

	private List<IPreferenceNode> nodes;

	class PreferencesCategoryNode extends CategoryNode {

		WikiPreferenceNode node;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param reader
		 * @param nodeToCategorize
		 */
		public PreferencesCategoryNode(CategorizedPageRegistryReader reader, WikiPreferenceNode nodeToCategorize) {
			super(reader);
			this.node = nodeToCategorize;
		}

		@Override
		String getLabelText() {
			return node.getLabelText();
		}

		@Override
		String getLabelText(IPreferenceNode element) {
			return ((WikiPreferenceNode) element).getLabelText();
		}

		@Override
		IPreferenceNode getNode() {
			return node;
		}
	}

	/**
	 * Create a new instance.
	 */
	public PreferencePageRegistryReader() {
		//
	}

	@Override
	IPreferenceNode findNode(String id) {
		for (int i = 0; i < nodes.size(); i++) {
			WikiPreferenceNode node = (WikiPreferenceNode) nodes.get(i);
			if (node.getId().equals(id)) {
				return node;
			}
		}
		return null;
	}

	@Override
	IPreferenceNode findNode(IPreferenceNode parent, String currentToken) {
		IPreferenceNode[] subNodes = ((WikiPreferenceNode) parent).getSubNodes();
		for (int i = 0; i < subNodes.length; i++) {
			WikiPreferenceNode node = (WikiPreferenceNode) subNodes[i];
			if (node.getId().equals(currentToken)) {
				return node;
			}
		}
		return null;
	}

	@Override
	void add(IPreferenceNode parent, IPreferenceNode node) {
		((IPreferenceNode) parent).add((IPreferenceNode) node);
	}

	@Override
	CategoryNode createCategoryNode(CategorizedPageRegistryReader reader, IPreferenceNode object) {
		return new PreferencesCategoryNode(reader, (WikiPreferenceNode) object);
	}

	@Override
	String getCategory(IPreferenceNode node) {
		return ((WikiPreferenceNode) node).getCategory();
	}

	@Override
	Collection<IPreferenceNode> getNodes() {
		return nodes;
	}

	/**
	 * Load the preference page contirbutions from the registry and organize preference node
	 * contributions by category into hierarchies If there is no page for a given node in the hierarchy
	 * then a blank page will be created. If no category has been specified or category information is
	 * incorrect, page will appear at the root level. workbench log entry will be created for incorrect
	 * category information.
	 * 
	 * @param registry the extension registry
	 */
	public void loadFromRegistry(IExtensionRegistry registry) {
		nodes = new ArrayList<>();

		readRegistry(registry, IWikiRegistryConstants.PLUGIN_EXTENSION_NAME_SPACE,
				IWikiRegistryConstants.PL_PREFERENCES);

		processNodes();
	}

	/**
	 * Read preference page element.
	 */
	protected boolean readElement(IConfigurationElement element) {
		if (element.getName().equals(TAG_PAGE) == false) {
			return false;
		}
		WikiPreferenceNode node = createNode(element);
		if (node != null) {
			if (node.getId().equals(WikiPreferenceManager.MAIN_PREFERENCE_PAGE_ID))
				node.setPriority(-1);
			nodes.add(node);
		}
		return true;
	}

	/**
	 * Create a workbench preference node.
	 * 
	 * @param element
	 * @return WorkbenchPreferenceNode
	 */
	public static WikiPreferenceNode createNode(IConfigurationElement element) {
		boolean nameMissing = element.getAttribute(IWikiRegistryConstants.ATT_NAME) == null;
		String id = element.getAttribute(IWikiRegistryConstants.ATT_ID);
		boolean classMissing = getClassValue(element, IWikiRegistryConstants.ATT_CLASS) == null;

		if (nameMissing) {
			logMissingAttribute(element, IWikiRegistryConstants.ATT_NAME);
		}
		if (id == null) {
			logMissingAttribute(element, IWikiRegistryConstants.ATT_ID);
		}
		if (classMissing) {
			logMissingAttribute(element, IWikiRegistryConstants.ATT_CLASS);
		}

		if (nameMissing || id == null || classMissing) {
			return null;
		}

		WikiPreferenceNode node = new WikiPreferenceNode(id, element);
		return node;
	}

	/**
	 * Return the top level IPreferenceNodes, minus the one which fail the Expression check.
	 * 
	 * @return Collection of IPreferenceNode.
	 */
	public Collection<IPreferenceNode> getTopLevelNodes() {
		return topLevelNodes;
		// TODO: :FVK: return WorkbenchActivityHelper.restrictCollection(topLevelNodes, new ArrayList());
	}

}
