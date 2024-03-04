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
package org.apache.wiki.tags;

import java.io.IOException;
import java.security.Principal;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.TextUtil;

/**
 * Returns the current user name, or empty, if the user has not been validated.
 *
 * @since 2.0
 */
public class UserUidTag extends BaseWikiTag {

	private static final long serialVersionUID = -8915224033310536804L;

	private static final String notStartWithBlankOrColon = "^[^( |:)]";

	private static final String noColons = "[^:]*";

	private static final Pattern VALID_USER_NAME_PATTERN = Pattern.compile(notStartWithBlankOrColon + noColons);

	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		final Engine engine = wikiContext.getEngine();
		ISessionMonitor sessionMonitor = engine.getManager(ISessionMonitor.class);
		
		final WikiSession wikiSession = sessionMonitor
				.getWikiSession((HttpServletRequest) pageContext.getRequest());
		final Principal user = wikiSession.getUserPrincipal();

		if (user != null) {
			if (VALID_USER_NAME_PATTERN.matcher(user.getName()).matches()) {
				pageContext.getOut().print(TextUtil.replaceEntities(user.getName()));
			} else {
				pageContext.getOut().print(Preferences.getBundle(wikiContext)
						.getString("security.user.fullname.invalid"));
			}
		}

		return SKIP_BODY;
	}

}
