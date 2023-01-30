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
package org.apache.wiki.plugin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.plugin.Plugin;
import org.apache.wiki.auth.user0.UserDatabase;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.comparators.PrincipalComparator;
import org.elwiki.api.authorization.IGroupManager;
import org.osgi.service.useradmin.Group;

/**
 * <p>
 * Prints the groups managed by this wiki, separated by commas. <br>
 * The groups are sorted in ascending order, and are hyperlinked to the page that displays the group's members.
 * </p>
 * <p>
 * Parameters :
 * </p>
 * NONE
 *
 * @since 2.4.19
 */
public class Groups implements Plugin {

	private static final Comparator<Principal> COMPARATOR = new PrincipalComparator();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(Context context, Map<String, String> params) throws PluginException {
		// Retrieve groups, and sort by name
		Engine engine = context.getEngine();
		IGroupManager groupMgr = engine.getManager(IGroupManager.class); // IGroupManager.class
		URLConstructor urlConstructor = engine.getManager(URLConstructor.class);
		List<Group> groups = groupMgr.getGroups();
		//:FVK: Arrays.sort( groups, COMPARATOR );

		List<String> listGroups = new ArrayList<>();
		for (Group group : groups) {
			StringBuilder str = new StringBuilder();
			Dictionary<String, Object> groupProps = group.getProperties();
			String name = (String) groupProps.get(UserDatabase.GROUP_NAME);

			// Make URL
			String url = urlConstructor.makeURL(Context.VIEW_GROUP, name, null);

			// Create hyperlink
			str.append("<a href=\"").append(url).append("\">").append(name).append("</a>");

			listGroups.add(str.toString());
		}

		// Adding a comma and a space as separators.
		return listGroups.stream().collect(Collectors.joining(", "));
	}

}
