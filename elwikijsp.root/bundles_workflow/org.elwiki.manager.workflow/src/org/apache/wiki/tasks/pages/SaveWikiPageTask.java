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
package org.apache.wiki.tasks.pages;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.workflow0.Outcome;
import org.apache.wiki.workflow0.Task;
import org.apache.wiki.workflow0.WorkflowManager;

/**
 * Handles the actual page save and post-save actions.
 */
public class SaveWikiPageTask extends Task {

	private static final long serialVersionUID = 3190559953484411420L;

	private final WikiContext context;

	private final String author;

	private final String changenote;

	/**
	 * Creates the Task.
	 * 
	 * @param author     TODO
	 * @param changenote TODO
	 */
	public SaveWikiPageTask(WikiContext context, String author, String changenote) {
		super(TasksManager.WIKIPAGE_SAVE_TASK_MESSAGE_KEY);
		this.context = context;
		this.author = author;
		this.changenote = changenote;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Outcome execute() throws WikiException {
		// Retrieve attributes
		String proposedText = (String) getWorkflowContext().get(WorkflowManager.WF_WP_SAVE_FACT_PROPOSED_TEXT);

		Engine engine = context.getEngine();
		WikiPage page = context.getPage();
		PageManager pageManager = engine.getManager(PageManager.class);
		RenderingManager renderingManager = engine.getManager(RenderingManager.class);
		FilterManager filterManager = engine.getManager(FilterManager.class);

		// Let the rest of the engine handle actual saving.
		pageManager.putPageText(page, proposedText, this.author, this.changenote);

		// Refresh the context for post save filtering.
		pageManager.getPage(page.getName());
		renderingManager.textToHTML(context, proposedText);
		filterManager.doPostSaveFiltering(context, proposedText);

		return Outcome.STEP_COMPLETE;
	}

}
