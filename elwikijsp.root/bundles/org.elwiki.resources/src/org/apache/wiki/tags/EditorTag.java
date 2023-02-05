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

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.ui.EditorManager;
import org.elwiki.services.ServicesRefs;

/**
 * Creates an editor component with all the necessary parts to get it working.
 * <p>
 * In the future, this component should be expanded to provide a customized
 * version of the editor according to user preferences.
 *
 * @since 2.2
 */
public class EditorTag extends BaseWikiBodyTag {

	private static final long serialVersionUID = -8740301007871109716L;
	private static final Logger log = Logger.getLogger(EditorTag.class);

	@Override
	public final int doWikiStartTag() throws IOException {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		final Engine engine = m_wikiContext.getEngine();
		final EditorManager mgr = ServicesRefs.getEditorManager();
		final String editorPath = mgr.getEditorPath(m_wikiContext);

		try {
			String page; 
			page = ServicesRefs.getTemplateManager().findJSP( pageContext, m_wikiContext.getShape(), editorPath );
			if (page == null) {
				//FIXME: should be I18N ...
				pageContext.getOut().println("Unable to find editor '" + editorPath + "'");
			} else {
				pageContext.include(page);
			}
		} catch (final ServletException e) {
			log.error("Failed to include editor", e);
			throw new JspException("Failed to include editor: " + e.getMessage());
		} catch (final IOException e) {
			throw new JspException("Could not print Editor tag: " + e.getMessage());
		}

		return EVAL_PAGE;
	}

}
