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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.wiki.api.exceptions.WikiException;
import org.elwiki.api.component.WikiComponent;
import org.elwiki.api.part.Id2NameMapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

//@formatter:off
@Component(
	name = "elwiki.Id2NameMapper",
	service = { Id2NameMapper.class, WikiComponent.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class Id2NameMapperImpl implements Id2NameMapper, WikiComponent, EventHandler {

	private Map<String, String> mapId2name = new HashMap<>();

	// -- OSGi service handling ----------------------(start)--

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// Nothing to do.
	}

	// -- OSGi service handling ------------------------(end)--

	/** {@inheritDoc} */
	@Override
	public String[] getAllPagesNames() {
		return mapId2name.values().toArray(new String[mapId2name.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public String getName(String pageId) {
		return mapId2name.get(pageId);
	}

	@Override
	public void handleEvent(Event event) {
		/*
		String topic = event.getTopic();
		switch (topic) {
		}
		*/
	}

}
