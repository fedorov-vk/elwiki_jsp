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
package org.apache.wiki.api.ui;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.modules.ModuleManager;

/**
 *  Defines an editor manager.  An editor can be added by adding a suitable JSP file under shapes/default/editors
 *  If you want your editor to include any scripts or something, you can simply request it by adding the following in your
 *  {@code ini/elwiki_editors.xml}:
 *
 *  <pre>
 *  &lt;modules>
 *   &lt;editor name="myeditor">
 *       &lt;author>Janne Jalkanen&lt;/author>
 *       &lt;script>foo.js&lt;/script>
 *       &lt;stylesheet>foo.css&lt;/stylesheet>
 *       &lt;path>editors/myeditor.jsp&lt;/path>
 *   &lt;/editor>
 *  &lt;/modules>
 *  </pre>
 *
 *  @since 2.4
 */
public interface EditorManager extends ModuleManager {

    /** The property name for setting the editor. Current value is "jspwiki.editor" - not used anymore: replaced by defaultpref.template.editor */
    String PROP_EDITORTYPE = "jspwiki.editor";

    /** Parameter for changing editors at run-time */
    String PARA_EDITOR = "editor";

    /** Known name for the plain wikimarkup editor. */
    String EDITOR_PLAIN = "plain";

    /** Known name for the preview editor component. */
    String EDITOR_PREVIEW = "preview";

    /** Known attribute name for storing the user edited text inside a HTTP parameter. */
    String REQ_EDITEDTEXT = "_editedtext";

    /** Known attribute name for storing the user edited text inside a session or a page context */
    String ATTR_EDITEDTEXT = REQ_EDITEDTEXT;

    /**
     *  Returns an editor for the current context.  The editor names are matched in a case insensitive manner.  At the moment, the only
     *  place that this method looks in is the property file, but in the future this will also look at user preferences.
     *  <p>
     *  Determines the editor to use by the following order of conditions:
     *  1. Editor set in User Preferences
     *  2. Default Editor set in jspwiki.properties (:FVK: is this true?) 
     *  <p>
     *  For the PREVIEW context, this method returns the "preview" editor.
     *
     * @param context The context that is chosen.
     * @return The name of the chosen editor. If no match could be found, will revert to the default "plain" editor.
     */
    String getEditorName( WikiContext context );

    /**
     *  Returns a list of editors as Strings of editor names.
     *
     *  @return the list of available editors
     */
    String[] getEditorList();

    /**
     *  Convenience method for getting the path to the editor JSP file.
     *
     *  @param context WikiContext from where the editor name is retrieved.
     *  @return e.g. "editors/plain.jsp"
     */
    String getEditorPath( WikiContext context );

}
