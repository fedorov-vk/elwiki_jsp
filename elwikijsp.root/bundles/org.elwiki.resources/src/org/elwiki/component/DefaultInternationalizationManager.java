/*
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.  
 */
package org.elwiki.component;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.eclipse.osgi.service.localization.BundleLocalization;
import org.elwiki.api.component.WikiManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Manages all internationalization in ElWiki (accessed from Java code).
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultInternationalizationManager",
	service = { InternationalizationManager.class, WikiManager.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultInternationalizationManager implements InternationalizationManager, WikiManager {

	@Reference
	private BundleLocalization bundleLocalization;

	private Bundle bundle;

	/**
	 * Constructs a new InternationalizationManager.
	 */
	public DefaultInternationalizationManager() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	/**
	 * This component activate routine. Initializes basic stuff.
	 *
	 * @param componentContext
	 * @throws WikiException
	 */
	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		BundleContext bc = componentContext.getBundleContext();
		bundle = bc.getBundle();
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// nothing to do.
	}

	// -- OSGi service handling ------------------------(end)--

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResourceBundle getBundle(Locale locale) throws MissingResourceException {
		if (locale == null) {
			locale = Locale.getDefault();
			//:FVK: Locale locale1 = new Locale("en", "US");
		}

		ResourceBundle result = null;

		result = bundleLocalization.getLocalization(this.bundle, locale.toString());

		return result;
	}

}
