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

import org.apache.log4j.Logger;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;

/**
 * Includes body if page has attachments.
 *
 * @since 2.0
 */
public class HasAttachmentsTag extends BaseWikiTag {

	private static final long serialVersionUID = 7907668402706865398L;
	private static final Logger log = Logger.getLogger(HasAttachmentsTag.class);

	@Override
	public final int doWikiStartTag() {
		final Engine engine = m_wikiContext.getEngine();
		final WikiPage page = m_wikiContext.getPage();
		final AttachmentManager mgr = ServicesRefs.getAttachmentManager();

		try {
			if (page != null && ServicesRefs.getPageManager().wikiPageExists(page) && mgr.attachmentsEnabled()) {
				if (mgr.hasAttachments(page)) {
					return EVAL_BODY_INCLUDE;
				}
			}
		} catch (final ProviderException e) {
			log.fatal("Provider failed while trying to check for attachements", e);
			// FIXME: THrow something.
		}

		return SKIP_BODY;
	}

}
