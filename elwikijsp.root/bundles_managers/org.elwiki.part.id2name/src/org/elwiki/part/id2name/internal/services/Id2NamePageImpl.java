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
package org.elwiki.part.id2name.internal.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.pages0.PageManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobFunction;
import org.eclipse.core.runtime.jobs.Job;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiComponent;
import org.elwiki.api.event.EngineEvent;
import org.elwiki.api.event.PageEvent;
import org.elwiki.api.part.Id2NamePage;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

//@formatter:off
@Component(
	name = "elwiki.Id2Name",
	service = { Id2NamePage.class, WikiComponent.class, EventHandler.class },
	property = {
		EventConstants.EVENT_TOPIC + "=" + EngineEvent.Topic.ALL,
		EventConstants.EVENT_TOPIC + "=" + PageEvent.Topic.DELETED,
	},
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class Id2NamePageImpl implements Id2NamePage, WikiComponent, EventHandler {

	private static final Logger log = Logger.getLogger(Id2NamePageImpl.class);

	private Map<String, String> mapId2name = new HashMap<>();

	// -- OSGi service handling ----------------------(start)--

	@WikiServiceReference
	PageManager pageManager;

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// Nothing to do.
	}

	// -- OSGi service handling ------------------------(end)--

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		switch (topic) {
		// Initialize.
		case EngineEvent.Topic.INIT_STAGE_TWO:
			try {
				initializeStageTwo();
			} catch (WikiException e) {
				log.error("Failed initialization of references for DefaultReferenceManager.", e);
			}
			break;
		case PageEvent.Topic.DELETED:
			String pageId = (String) event.getProperty(PageEvent.PROPERTY_PAGE_ID);
			if (pageId != null) {
				// TODO: pageRemoved(pageId);
			}
			break;
		}
	}

	/**
	 * Initializes the id2name part. Scans all existing WikiPages for these attributes (name, id) and
	 * adds them to the dictionary.
	 *
	 * @throws WikiException If the id2name part initialization fails.
	 */
	private void initializeStageTwo() throws WikiException {
		log.debug("Initializing id2name part.");

		Job job = Job.create("null", new IJobFunction() {
			@Override
			public IStatus run(IProgressMonitor monitor) {
				StopWatch sw = new StopWatch();
				sw.start();
				log.info("Start reading attributes of all wiki pages.");

				ArrayList<WikiPage> pages = new ArrayList<>();
				try {
					pages.addAll(pageManager.getAllPages());
					for (WikiPage page : pages) {
						mapId2name.put(page.getId(), page.getName());
					}
				} catch (Exception e) {
					// TODO: когда база пустая - не выдавать ошибку. (возможно это нормально? в менеджере страниц -
					// сделать: нет страниц - возврат пустой список)
					log.error("Problem with get all pages:", e);
				}

				sw.stop();
				log.info("Reading attributes done in " + sw);

				return Status.OK_STATUS;
			}
		});

		job.setSystem(false);
		job.setUser(false);
		job.schedule();
	}

	/** {@inheritDoc} */
	@Override
	public String[] getAllPageNames() {
		return mapId2name.values().toArray(new String[mapId2name.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public String getName(String pageId) {
		return mapId2name.get(pageId);
	}

}
