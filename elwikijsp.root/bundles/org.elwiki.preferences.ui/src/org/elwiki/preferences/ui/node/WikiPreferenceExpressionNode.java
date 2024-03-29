/*******************************************************************************
 * Copyright (c) 2008 Bredex GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan-Hendrik Diederich, Bredex GmbH - initial API and implementation, bug 201052
 *     IBM Corporation - bug 201052
 ******************************************************************************/
package org.elwiki.preferences.ui.node;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceNode;

/**
 * @author Jan Diederich
 */
public class WikiPreferenceExpressionNode extends PreferenceNode
/*TODO: :FVK: implements IPluginContribution*/ {
	private static final long serialVersionUID = 1L;

	/**
	 * @param id The id.
	 * @see PreferenceNode#PreferenceNode(String)
	 */
	public WikiPreferenceExpressionNode(String id) {
		super(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferenceNode#findSubNode(java.lang.String)
	 */
	public IPreferenceNode findSubNode(String id) {
		return getNodeExpression(super.findSubNode(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferenceNode#getSubNodes()
	 */
	public IPreferenceNode[] getSubNodes() {
		IPreferenceNode[] prefNodes = super.getSubNodes();
		int size = prefNodes.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			IPreferenceNode prefNode = getNodeExpression(prefNodes[i]);
			if (prefNode != null) {
				list.add(prefNode);
			}
		}
		return (IPreferenceNode[]) list.toArray(new IPreferenceNode[list.size()]);
	}

	/**
	 * Returns the given <code>prefNode</code>, but only if it's no WorkbenchPreferenceExtensionNode
	 * which fails the Expression check.
	 * 
	 * @param prefNode The preference node which will be checked. Can be <code>null
	 *            </code>.
	 * @return The given <code>prefNode</code>, or <code>null</code> if it fails the Expressions check.
	 */
	public static IPreferenceNode getNodeExpression(IPreferenceNode prefNode) {
		if (prefNode == null)
			return null;
		if (prefNode instanceof WikiPreferenceExtensionNode) {
			WikiPreferenceExpressionNode node = (WikiPreferenceExtensionNode) prefNode;
			/*TODO: :FVK:
			if (WorkbenchActivityHelper.restrictUseOf(node)) {
				return null;
			}
			*/
		}
		return prefNode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPluginContribution#getLocalId()
	 */
	public String getLocalId() {
		return getId();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPluginContribution#getPluginId()
	 */
	public String getPluginId() {
		return ""; //$NON-NLS-1$
	}
}
