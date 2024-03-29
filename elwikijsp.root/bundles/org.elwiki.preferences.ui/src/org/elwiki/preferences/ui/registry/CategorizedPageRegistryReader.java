/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Oakland Software (Francis Upton) <francisu@ieee.org> - bug 219273 
 *******************************************************************************/
package org.elwiki.preferences.ui.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceNode;

/**
 * The CategorizedPageRegistryReader is the abstract super class of registry readers for page that
 * have categorization.
 */
public abstract class CategorizedPageRegistryReader extends RegistryReader {

	public static final String ATT_CATEGORY = "category"; //$NON-NLS-1$

	static final String PREFERENCE_SEPARATOR = "/"; //$NON-NLS-1$

	List<IPreferenceNode> topLevelNodes;

	/**
	 * Internal class used to sort all the preference page nodes based on the category.
	 */
	abstract class CategoryNode {
		/**
		 * Comment for <code>reader</code>
		 */
		private final CategorizedPageRegistryReader reader;

		// private WorkbenchPreferenceNode node;

		private String flatCategory;

		/**
		 * Default constructor
		 */
		public CategoryNode(CategorizedPageRegistryReader reader) {
			this.reader = reader;
		}

		/**
		 * Return the flatten category
		 */
		public String getFlatCategory() {
			if (flatCategory == null) {
				initialize();
				if (flatCategory == null) {
					flatCategory = getLabelText();
				}
			}
			return flatCategory;
		}

		/**
		 * Get the label text for this node.
		 * 
		 * @return String
		 */
		abstract String getLabelText();

		/*
		 * Initialize the flat category to include the parents'
		 * category names and the current node's label
		 */
		private void initialize() {
			String category = reader.getCategory(getNode());
			if (category == null) {
				return;
			}

			StringBuffer sb = new StringBuffer();
			StringTokenizer stok = new StringTokenizer(category, PREFERENCE_SEPARATOR);
			IPreferenceNode immediateParent = null;
			while (stok.hasMoreTokens()) {
				String pathID = stok.nextToken();
				immediateParent = this.reader.findNode(pathID);
				if (immediateParent == null) {
					return;
				}
				if (sb.length() > 0) {
					sb.append(PREFERENCE_SEPARATOR);
				}
				sb.append(getLabelText(immediateParent));
			}

			if (sb.length() > 0) {
				sb.append(PREFERENCE_SEPARATOR);
			}
			sb.append(getLabelText());
			flatCategory = sb.toString();
		}

		/**
		 * Return the label text for the passed element.
		 * 
		 * @param element
		 * @return String
		 */
		abstract String getLabelText(IPreferenceNode element);

		/**
		 * Get the node the receiver represents.
		 * 
		 * @return IPreferenceNode
		 */
		abstract IPreferenceNode getNode();
	}

	/**
	 * Create a new instance of the receiver.
	 */
	public CategorizedPageRegistryReader() {
		super();
	}

	/**
	 * Process the preference page nodes.
	 */
	void processNodes() {
		topLevelNodes = new ArrayList<>();
		// root nodes (which contain subnodes)

		// Add root nodes to the contributions vector
		StringTokenizer tokenizer;
		String currentToken;

		CategoryNode[] nodes = createCategoryNodes(getNodes());
		// flag to indicate that some work was done in the inner loop over the nodes
		boolean workDone;
		do {
			// reset the flag
			workDone = false;
			List<CategoryNode> deferred = new ArrayList<>();
			for (int i = 0; i < nodes.length; i++) {
				// Iterate through all the nodes
				CategoryNode categoryNode = nodes[i];
				IPreferenceNode node = categoryNode.getNode();

				String category = getCategory(node);
				if (category == null) {
					topLevelNodes.add(node);
					continue;
				}
				// has category
				tokenizer = new StringTokenizer(category, PREFERENCE_SEPARATOR);
				IPreferenceNode parent = null;
				while (tokenizer.hasMoreElements()) {
					currentToken = tokenizer.nextToken();
					IPreferenceNode child = null;
					if (parent == null) {
						child = findNode(currentToken);
					} else {
						child = findNode(parent, currentToken);
					}

					if (child == null) {
						parent = null;
						break;
					}
					parent = child;
				}
				if (parent != null) {
					// we've done some work - the number of nodes to process has decreased
					workDone = true;
					add(parent, node);
				} else {
					// we haven't done any work - the parent for this node has not been found.
					deferred.add(categoryNode);
				}
			}
			// reset the nodes to all that have yet to find their proper parent
			nodes = (CategoryNode[]) deferred.toArray(new CategoryNode[deferred.size()]);
		} while (nodes.length > 0 && workDone); // loop while we still have nodes to work on and the list is shrinking

		// log anything left over.
		for (int i = 0; i < nodes.length; i++) {
			CategoryNode categoryNode = nodes[i];
			// Could not find the parent - log
			// TODO: :FVK: WorkbenchPlugin.log("Invalid preference page path: " +
			// categoryNode.getFlatCategory()); //$NON-NLS-1$
			topLevelNodes.add(categoryNode.getNode());
		}
	}

	/**
	 * Get the category for the node if there is one. If there isn't return <code>null</code>.
	 * 
	 * @param node
	 * @return String or <code>null</code>.
	 */
	abstract String getCategory(IPreferenceNode node);

	/**
	 * Add the node to the parent.
	 * 
	 * @param parent
	 * @param node
	 */
	abstract void add(IPreferenceNode parent, IPreferenceNode node);

	/**
	 * Get the nodes for the receiver.
	 * 
	 * @return Collection of IPreferenceNode
	 */
	abstract Collection<IPreferenceNode> getNodes();

	/**
	 * Sort the nodes based on full category + name. Category used for sorting is created by
	 * substituting node IDs with labels of the referenced nodes. workbench node is excluded from
	 * sorting because it always appears first in the dialog.
	 */
	CategoryNode[] createCategoryNodes(Collection<IPreferenceNode> nodesToCategorize) {
		// sort by categories
		List<CategoryNode> nodes = new ArrayList<>();
		for(IPreferenceNode node : nodesToCategorize) {
			nodes.add(createCategoryNode(this, node));
		}

		return (CategoryNode[]) nodes.toArray(new CategoryNode[nodes.size()]);
	}

	/**
	 * Create a node for categorization from the reader and the supplied object.
	 * 
	 * @param reader
	 * @param object
	 * @return CategoryNode
	 */
	abstract CategoryNode createCategoryNode(CategorizedPageRegistryReader reader, IPreferenceNode object);

	/**
	 * Searches for the top-level node with the given id.
	 * 
	 * @param id
	 * @return Categorized IPreferenceNode or <code>null</code>
	 */
	abstract IPreferenceNode findNode(String id);

	/**
	 * Find the node with the given parent with the id of currentToken.
	 * 
	 * @param parent
	 * @param currentToken
	 * @return
	 */
	abstract IPreferenceNode findNode(IPreferenceNode parent, String currentToken);

}
