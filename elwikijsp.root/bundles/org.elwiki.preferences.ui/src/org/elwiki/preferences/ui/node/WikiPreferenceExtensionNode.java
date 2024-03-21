/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jan-Hendrik Diederich, Bredex GmbH - bug 201052
 *     Oakland Software (Francis Upton) <francisu@ieee.org> - bug 219273 
 *     
 *******************************************************************************/
package org.elwiki.preferences.ui.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.graphics.Image;
import org.elwiki.preferences.ui.registry.IWikiRegistryConstants;
import org.elwiki.preferences.ui.registry.KeywordRegistry;

/**
 * The WorkbenchPreferenceExtensionNode is the abstract class for all property and page nodes in the
 * workbench.
 * 
 */
public abstract class WikiPreferenceExtensionNode extends WikiPreferenceExpressionNode implements IAdaptable
/*TODO: :FVK: implements IComparableContribution*/ {
	private static final long serialVersionUID = 1L;

// RAP [fappel]: key to save page on the session to allow multiple sessions
	private static final String ID_PAGE = WikiPreferenceExtensionNode.class.getName() + "#Page"; //$NON-NLS-1$

	private Collection<String> keywordReferences;

	private IConfigurationElement configurationElement;

	private ImageDescriptor imageDescriptor;

	private Image image;

	private Collection<String> keywordLabelCache;

	private int priority;

	private String pluginId;

	/**
	 * Create a new instance of the reciever.
	 * 
	 * @param id
	 * @param configurationElement
	 */
	public WikiPreferenceExtensionNode(String id, IConfigurationElement configurationElement) {
		super(id);
		this.configurationElement = configurationElement;
		this.pluginId = configurationElement.getNamespaceIdentifier();
	}

	/**
	 * Get the ids of the keywords the receiver is bound to.
	 * 
	 * @return Collection of <code>String</code>. Never <code>null</code>.
	 */
	public Collection<String> getKeywordReferences() {
		if (keywordReferences == null) {
			IConfigurationElement[] references = getConfigurationElement()
					.getChildren(IWikiRegistryConstants.TAG_KEYWORD_REFERENCE);
			HashSet<String> list = new HashSet<>(references.length);
			for (int i = 0; i < references.length; i++) {
				IConfigurationElement page = references[i];
				String id = page.getAttribute(IWikiRegistryConstants.ATT_ID);
				if (id != null) {
					list.add(id);
				}
			}

			if (!list.isEmpty()) {
				keywordReferences = list;
			} else {
				keywordReferences = Collections.emptySet();
			}

		}
		return keywordReferences;
	}

	/**
	 * Get the labels of all of the keywords of the receiver.
	 * 
	 * @return Collection of <code>String</code>. Never <code>null</code>.
	 */
	public Collection<String> getKeywordLabels() {
		if (keywordLabelCache != null) {
			return keywordLabelCache;
		}

		Collection<String> refs = getKeywordReferences();

		if (refs == Collections.EMPTY_SET) {
			keywordLabelCache = Collections.emptySet();
			return keywordLabelCache;
		}

		keywordLabelCache = new ArrayList<>(refs.size());
		Iterator<String> referenceIterator = refs.iterator();
		while (referenceIterator.hasNext()) {
			String label = KeywordRegistry.getInstance().getKeywordLabel((String) referenceIterator.next());
			if (label != null) {
				keywordLabelCache.add(label);
			}
		}

		return keywordLabelCache;
	}

	/**
	 * Clear the keyword cache, if any.
	 */
	public void clearKeywords() {
		keywordLabelCache = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceNode#disposeResources()
	 */
	public void disposeResources() {
		if (image != null) {
// RAP [fappel]: Resource disposal not available in RAP
//            image.dispose();
			image = null;
		}
		if (getPage() != null) {
			getPage().dispose();
			removePage();
		}
		super.disposeResources();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceNode#getLabelImage()
	 */
	public Image getLabelImage() {
		if (image == null) {
			ImageDescriptor desc = getImageDescriptor();
			if (desc != null) {
				image = imageDescriptor.createImage();
			}
		}
		return image;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceNode#getLabelText()
	 */
	public String getLabelText() {
		return getConfigurationElement().getAttribute(IWikiRegistryConstants.ATT_NAME);
	}

	/**
	 * Returns the image descriptor for this node.
	 * 
	 * @return the image descriptor
	 */
	public ImageDescriptor getImageDescriptor() {
		if (imageDescriptor != null) {
			return imageDescriptor;
		}

		String imageName = getConfigurationElement().getAttribute(IWikiRegistryConstants.ATT_ICON);
		if (imageName != null) {
			String contributingPluginId = pluginId;
			imageDescriptor = null; // TODO: :FVK: AbstractUIPlugin.imageDescriptorFromPlugin(contributingPluginId,
									// imageName);
		}
		return imageDescriptor;
	}

	/**
	 * Return the configuration element.
	 * 
	 * @return the configuration element
	 */
	public IConfigurationElement getConfigurationElement() {
		return configurationElement;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.activities.support.IPluginContribution#getLocalId()
	 */
	public String getLocalId() {
		return getId();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.activities.support.IPluginContribution#getPluginId()
	 */
	public String getPluginId() {
		return pluginId;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IConfigurationElement.class)
			return getConfigurationElement();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IComparableContribution#getLabel()
	 */
	public String getLabel() {
		return getLabelText();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IComparableContribution#getPriority()
	 */
	public int getPriority() {
		return priority;
	}

	public void setPriority(int pri) {
		priority = pri;
	}

	// RAP [fappel]: override to allow multiple sessions
	public void setPage(final IPreferencePage newPage) {
		RWT.getUISession().setAttribute(ID_PAGE + getId(), newPage);
	}

	// RAP [fappel]: override to allow multiple sessions
	public IPreferencePage getPage() {
		return (IPreferencePage) RWT.getUISession().getAttribute(ID_PAGE + getId());
	}

	// RAP [fappel]: allow to remove page from session store after disposal
	private void removePage() {
		RWT.getUISession().removeAttribute(ID_PAGE + getId());
	}

}
