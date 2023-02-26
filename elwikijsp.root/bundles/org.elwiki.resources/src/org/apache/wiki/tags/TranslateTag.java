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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.render0.RenderingManager;

/**
 * Converts the body text into HTML content.
 *
 * @since 2.0
 */
public class TranslateTag extends BodyTagSupport {

	private static final long serialVersionUID = 5558995764665411022L;
	private static final Logger log = Logger.getLogger(TranslateTag.class);

	@Override
	public final int doAfterBody() throws JspException {
		try {
			WikiContext context = (WikiContext) pageContext.getAttribute(WikiContext.ATTR_WIKI_CONTEXT, PageContext.REQUEST_SCOPE);

			//  Because the TranslateTag should not affect any of the real page attributes we have to make a clone here.
			context = context.deepClone();

			//  Get the page data.
			final BodyContent bc = getBodyContent();
			String wikiText = bc.getString();
			bc.clearBody();

			if (wikiText != null) {
				wikiText = wikiText.trim();
				RenderingManager renderingManager = context.getEngine().getManager(RenderingManager.class);
				final String result = renderingManager.textToHTML(context, wikiText);
				getPreviousOut().write(result);
			}
		} catch (final Exception e) {
			log.error("Tag failed", e);
			throw new JspException("Tag failed, check logs: " + e.getMessage());
		}

		return SKIP_BODY;
	}

}
