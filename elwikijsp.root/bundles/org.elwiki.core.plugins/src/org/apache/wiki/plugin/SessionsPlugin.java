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

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.auth.SessionMonitor;
import org.apache.wiki.util.TextUtil;
import org.elwiki.api.plugin.WikiPlugin;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Displays information about active wiki sessions. The parameter <code>property</code> specifies what
 * information is displayed. If omitted, the number of sessions is returned.
 *
 * <p>
 * Parameters :
 * </p>
 * <ul>
 * <li><b>property</b> - specify what output to display, valid values are:</li>
 * <ul>
 * <li><code>users</code> - returns a comma-separated list of users</li>
 * <li><code>distinctUsers</code> - will only show distinct users.</li>
 * </ul>
 * </ul>
 */
public class SessionsPlugin implements WikiPlugin {

	/** The parameter name for setting the property value. */
	public static final String PARAM_PROP = "property";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		Engine engine = context.getEngine();
		ISessionMonitor sessionMonitor = engine.getManager(ISessionMonitor.class);
		String prop = params.get(PARAM_PROP);

		if ("users".equals(prop)) {
			Principal[] principals = sessionMonitor.userPrincipals();
			StringBuilder s = new StringBuilder();
			for (Principal principal : principals) {
				s.append(principal.getName()).append(", ");
			}
			// remove the last comma and blank :
			return TextUtil.replaceEntities(s.substring(0, s.length() - (s.length() > 2 ? 2 : 0)));
		}

		// show each user session only once (with a counter that indicates the number of sessions for each user)
		if ("distinctUsers".equals(prop)) {
			Principal[] principals = sessionMonitor.userPrincipals();
			// we do not assume that the principals are sorted, so first count them :
			HashMap<String, Integer> distinctPrincipals = new HashMap<>();
			for (Principal principal : principals) {
				String principalName = principal.getName();

				if (distinctPrincipals.containsKey(principalName)) {
					// we already have an entry, increase the counter:
					int numSessions = distinctPrincipals.get(principalName);
					// store the new value:
					distinctPrincipals.put(principalName, ++numSessions);
				} else {
					// first time we see this entry, add entry to HashMap with value 1
					distinctPrincipals.put(principalName, 1);
				}
			}

			StringBuilder s = new StringBuilder();
			for (Map.Entry<String, Integer> entry : distinctPrincipals.entrySet()) {
				s.append(entry.getKey()).append("(").append(entry.getValue().toString()).append("), ");
			}
			// remove the last comma and blank :
			return TextUtil.replaceEntities(s.substring(0, s.length() - (s.length() > 2 ? 2 : 0)));

		}

		return String.valueOf(sessionMonitor.sessions());
	}
}
