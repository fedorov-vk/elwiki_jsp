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

import javax.servlet.jsp.JspTagException;

import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki_data.WikiPage;

/**
 * Does a version check on the page.
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>The "<b>mode</b>" attribute can be any of the following:
 * <ul>
 * <li><i>latest</i> - Include body, if the page is the latest version.
 * <li><i>notlatest</i> - Include body, if the page is NOT the latest version.
 * <li><i>first</i> - Include body, if page is the first version (version 1)
 * <li><i>notfirst</i> - Include body, if page is NOT the first version (version
 * 1)
 * </ul>
 * </ul>
 * If the page does not exist, body content is never included.
 *
 * @since 2.0
 */
public class CheckVersionTag extends BaseWikiTag {

	private enum VersionMode {
		LATEST, NOTLATEST, FIRST, NOTFIRST
	}

	private static final long serialVersionUID = 3269431461906269282L;

	private VersionMode m_mode;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initTag() {
		super.initTag();
		m_mode = VersionMode.LATEST;
	}

	/**
	 * Sets the mode.
	 * 
	 * @param arg The mode to set.
	 */
	public void setMode(String arg) {
		this.m_mode = switch (arg) {
		case "first" -> VersionMode.FIRST;
		case "notfirst" -> VersionMode.NOTFIRST;
		case "latest" -> VersionMode.LATEST;
		case "notlatest" -> VersionMode.NOTLATEST;
		default -> VersionMode.NOTLATEST;
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int doWikiStartTag() throws ProviderException, IOException, JspTagException {
		WikiPage page = getWikiContext().getPage();
		if (page != null) {
			int pageVersion = getWikiContext().getPageVersion();
			int latestVersion = page.getLastVersion();
			boolean isIncluding = switch (m_mode) {
			case LATEST -> (pageVersion == 0) || (latestVersion == pageVersion);
			case NOTLATEST -> (pageVersion > 0) && (latestVersion != pageVersion);
			case FIRST -> (pageVersion == 1) || (pageVersion < 0 && latestVersion == 1);
			case NOTFIRST -> pageVersion > 1;
			};
			if (isIncluding) {
				return EVAL_BODY_INCLUDE;
			}
		}
		return SKIP_BODY;
	}

}
