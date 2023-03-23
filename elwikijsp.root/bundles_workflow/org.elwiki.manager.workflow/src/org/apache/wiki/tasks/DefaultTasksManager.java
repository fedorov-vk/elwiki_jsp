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
package org.apache.wiki.tasks;

import java.util.Locale;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.tasks.auth.SaveUserProfileTask;
import org.apache.wiki.tasks.pages.PreSaveWikiPageTask;
import org.apache.wiki.tasks.pages.SaveWikiPageTask;
import org.apache.wiki.workflow0.Step;
import org.elwiki.api.component.WikiManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Default implementation for {@link TasksManager}.
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultTasksManager",
	service = { TasksManager.class, WikiManager.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultTasksManager implements TasksManager, WikiManager {

	// -- OSGi service handling ----------------------(start)--

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
	public Step buildPreSaveWikiPageTask(final WikiContext context, final String proposedText) {
		return new PreSaveWikiPageTask(context, proposedText);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Step buildSaveWikiPageTask(final WikiContext context, String author, String changenote) {
		return new SaveWikiPageTask(context, author, changenote);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Step buildSaveUserProfileTask(final Engine engine, final Locale loc) {
		return new SaveUserProfileTask(engine, loc);
	}

}
