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

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;

/**
 * Returns the currently requested page or attachment size.
 *
 * @since 2.0
 */
public class PageSizeTag extends BaseWikiTag {

	private static final long serialVersionUID = 4194343181131621002L;
	private static final Logger log = Logger.getLogger(PageSizeTag.class);

	@Override
	public final int doWikiStartTag() throws IOException {
		final Engine engine = m_wikiContext.getEngine();
		final WikiPage page = m_wikiContext.getPage();

		try {
			if (page != null) {
				long size = 123; //:WORKAROUND. FVK: page.getSize();

				if (size == -1 && ServicesRefs.getPageManager().wikiPageExists(page)) { // should never happen with attachments
					size = ServicesRefs.getPageManager().getPureText(page.getName(), page.getVersion()).length();
					//:FVK: page.setSize( size );
				}

				pageContext.getOut().write(Long.toString(size));
			}
		} catch (final ProviderException e) {
			log.warn("Providers did not work: ", e);
			pageContext.getOut().write("Error determining page size: " + e.getMessage());
		}

		return SKIP_BODY;
	}

}
