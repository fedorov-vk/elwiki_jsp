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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.ui.Editor;

/**
 * Iterates through editors.
 *
 * @since 2.4.12
 */

public class EditorIteratorTag extends BaseIteratorTag<Editor> {

	private static final long serialVersionUID = -5067091242204416850L;

	/** {@inheritDoc} */
	@Override
	public final int doStartTag() {
		final Engine engine = getWikiContext().getEngine();
		final EditorManager editorManager = engine.getManager(EditorManager.class);
		final String[] editorList = editorManager.getEditorList();
		final Collection<Editor> editors = new ArrayList<>();

		for (final String editor : editorList) {
			editors.add(new Editor(getWikiContext(), editor));
		}
		setList(editors);

		return super.doStartTag();
	}

}
