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
package org.apache.wiki.i18n;

import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.elwiki.api.component.WikiManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Manages all internationalization in JSPWiki.
 *
 * @since 2.6
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultInternationalizationManager",
	service = { InternationalizationManager.class, WikiManager.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultInternationalizationManager implements InternationalizationManager, WikiManager {

	/**
	 * Constructs a new InternationalizationManager.
	 */
	public DefaultInternationalizationManager() {
	}

	// -- OSGi service handling ----------------------(start)--

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// doesn't used.
	}

	// -- OSGi service handling ------------------------(end)--

}
