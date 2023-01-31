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

import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki.services.ServicesRefs;
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

	private static final long serialVersionUID = 3269431461906269282L;

	private enum VersionMode {
		LATEST, NOTLATEST, FIRST, NOTFIRST
	}

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
	public void setMode(final String arg) {
		if ("latest".equals(arg)) {
			m_mode = VersionMode.LATEST;
		} else if ("notfirst".equals(arg)) {
			m_mode = VersionMode.NOTFIRST;
		} else if ("first".equals(arg)) {
			m_mode = VersionMode.FIRST;
		} else {
			m_mode = VersionMode.NOTLATEST;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int doWikiStartTag() throws ProviderException, IOException, JspTagException {
		final Engine engine = m_wikiContext.getEngine();
		final WikiPage page = m_wikiContext.getPage();

		if (page != null && ServicesRefs.getPageManager().pageExistsByName(page.getName())) {
			final int version = page.getVersion();
			final boolean include;
			final WikiPage latest = ServicesRefs.getPageManager().getPage(page.getName());

			switch (m_mode) {
			case LATEST:
				include = (version < 0) || (latest.getVersion() == version);
				break;
			case NOTLATEST:
				include = (version > 0) && (latest.getVersion() != version);
				break;
			case FIRST:
				include = (version == 1) || (version < 0 && latest.getVersion() == 1);
				break;
			case NOTFIRST:
				include = version > 1;
				break;
			default:
				throw new InternalWikiException("Mode which is not available!");
			}
			if (include) {
				return EVAL_BODY_INCLUDE;
			}
		}
		return SKIP_BODY;
	}

}
