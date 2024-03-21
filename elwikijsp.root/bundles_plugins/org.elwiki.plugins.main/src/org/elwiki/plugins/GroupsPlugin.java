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
package org.elwiki.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.url0.URLConstructor;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.api.plugin.WikiPlugin;

/**
 * <p>
 * Prints the groups managed by this wiki, separated by commas. <br/>
 * The groups are sorted in ascending order, and are hyperlinked to the page that displays the
 * group's members.
 * </p>
 * <p>
 * Parameters : NONE
 * </p>
 */
public class GroupsPlugin implements WikiPlugin {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {

		// Retrieve groups, and sort by name
		Engine engine = context.getEngine();
		AccountManager accountManager = engine.getManager(AccountManager.class);
		URLConstructor urlConstructor = engine.getManager(URLConstructor.class);
		List<IGroupWiki> groups;
		try {
			groups = accountManager.getGroups();
		} catch (WikiSecurityException e) {
			throw new PluginException(e);
		}
		//:FVK: TODO: Arrays.sort( groups, COMPARATOR );
		List<String> listGroups = new ArrayList<>();
		for (IGroupWiki group : groups) {
			StringBuilder str = new StringBuilder();
			String name = group.getName();

			// Make URL
			String url = urlConstructor.makeURL(WikiContext.GROUP_VIEW, name, null);

			// Create hyperlink
			str.append("<a href=\"").append(url).append("\">").append(name).append("</a>");

			listGroups.add(str.toString());
		}

		// Adding a comma and a space as separators.
		return listGroups.stream().collect(Collectors.joining(", "));

	}

}
