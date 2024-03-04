/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.elwiki.preferences.ui.node;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.elwiki.preferences.ui.IWikiPreferencePage;
import org.elwiki.preferences.ui.internal.bundle.PluginActivator;
import org.elwiki.preferences.ui.page.ErrorPreferencePage;
import org.elwiki.preferences.ui.registry.CategorizedPageRegistryReader;
import org.elwiki.preferences.ui.registry.IWikiRegistryConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A proxy for a preference page to avoid creation of preference page just to show a node in the
 * preference dialog tree.
 */
public class WikiPreferenceNode extends WikiPreferenceExtensionNode {
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(WikiPreferenceNode.class);
	
	/**
	 * Create a new instance of the receiver.
	 * 
	 * @param nodeId
	 * @param element
	 */
	public WikiPreferenceNode(String nodeId, IConfigurationElement element) {
		super(nodeId, element);
	}

	/**
	 * Creates the preference page this node stands for.
	 */
	public void createPage() {
		IWikiPreferencePage page;
		try {
			page = (IWikiPreferencePage) getConfigurationElement()
					.createExecutableExtension(IWikiRegistryConstants.ATT_CLASS);
		} catch (CoreException e) {
			// Just inform the user about the error. The details are written to the log by now.
			/*TODO: :FVK:
			IStatus errStatus = StatusUtil.newStatus(e.getStatus(),
					WorkbenchMessages.get().PreferenceNode_errorMessage);
			StatusManager.getManager().handle(errStatus, StatusManager.SHOW | StatusManager.LOG);
			*/
			log.error("Unable to create the selected preference page.", e);
			page = new ErrorPreferencePage();
		}

		BundleContext context = PluginActivator.getContext();
		ServiceReference<?> ref = context.getServiceReference(Engine.class.getName());
		Engine engine = (ref != null) ? (Engine) context.getService(ref) : null;
		if (engine == null) {
			// TODO: обработать аварию - нет сервиса Engine.
			throw new NullPointerException("missed Engine service.");
		}

		page.init(engine);
		if (getLabelImage() != null) {
			page.setImageDescriptor(getImageDescriptor());
		}
		page.setTitle(getLabelText());
		setPage(page);
	}

	/**
	 * Return the category name for the node.
	 * 
	 * @return java.lang.String
	 */
	public String getCategory() {
		return getConfigurationElement().getAttribute(CategorizedPageRegistryReader.ATT_CATEGORY);
	}
}
