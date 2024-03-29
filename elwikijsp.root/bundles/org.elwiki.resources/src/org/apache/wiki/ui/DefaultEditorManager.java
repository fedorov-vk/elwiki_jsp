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
package org.apache.wiki.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.NoSuchVariableException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.modules.BaseModuleManager;
import org.apache.wiki.api.modules.WikiModuleInfo;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.XmlUtil;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiComponent;
import org.elwiki.resources.ResourcesActivator;
import org.jdom2.Element;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Defines an editor manager. An editor can be added by adding a suitable JSP file under
 * shapes/default/editors If you want your editor to include any scripts or something, you can
 * simply request it by adding the following in your {@code ini/elwiki_editors.xml}:
 *
 * <pre>
 *  &lt;modules>
 *   &lt;editor name="myeditor">
 *       &lt;author>Janne Jalkanen&lt;/author>
 *       &lt;script>foo.js&lt;/script>
 *       &lt;stylesheet>foo.css&lt;/stylesheet>
 *       &lt;path>editors/myeditor.jsp&lt;/path>
 *   &lt;/editor>
 *  &lt;/modules>
 * </pre>
 *
 * @since 2.4
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultEditorManager",
	service = { EditorManager.class, WikiComponent.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultEditorManager extends BaseModuleManager implements EditorManager, WikiComponent, EventHandler {

	private static final Logger log = Logger.getLogger(DefaultEditorManager.class);

	static final String CONFIGURATION_FILE = "ini/elwiki_editors.xml";

	private Map<String, WikiEditorInfo> m_editors;

	/**
	 * Constructs the DefaultEditorManager instance.
	 */
	public DefaultEditorManager() {
		super();
	}

	// -- OSGi service handling --------------------( start )--

	@WikiServiceReference
	protected Engine m_engine;

	@WikiServiceReference
	private VariableManager variableManager;

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		registerEditors();
	}

	// -- OSGi service handling ----------------------( end )--

	/**
	 * This method goes through the elwiki_editors.xml file and hunts for editors. Any editors found are
	 * put in the registry.
	 */
	private void registerEditors() {
		log.info("Registering editor modules");
		m_editors = new HashMap<>();

		// Register all editors which have created a resource containing its properties. Get all resources of all modules
		final List<Element> editors = XmlUtil.parse(ResourcesActivator.getContext().getBundle(), CONFIGURATION_FILE,
				"/modules/editor");
		for (final Element pluginEl : editors) {
			final String name = pluginEl.getAttributeValue("name");
			final WikiEditorInfo info = WikiEditorInfo.newInstance(name, pluginEl);

			if (checkCompatibility(info)) {
				m_editors.put(name, info);
				log.debug("Registered editor " + name);
			} else {
				log.info("Editor '" + name + "' not compatible with this version of JSPWiki.");
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getEditorName(final WikiContext context) {
		if (context.getRequestContext().equals(ContextEnum.PAGE_PREVIEW.getRequestContext())) {
			return EDITOR_PREVIEW;
		}

		// User has set an editor in preferences
		String editor = Preferences.getPreference(context, PARA_EDITOR);

		/* FIXME: actual default 'editor' property is read by the Preferences class */
		if (editor == null) {
			// or use the default editor in preferences.ini
			try {
				editor = variableManager.getValue(context, PROP_EDITORTYPE);
			} catch (final NoSuchVariableException e) {
			} // This is fine
		}

		if (editor != null) {
			final String[] editorlist = getEditorList();
			editor = editor.trim();
			for (final String s : editorlist) {
				if (s.equalsIgnoreCase(editor)) {
					return s;
				}
			}
		}

		return EDITOR_PLAIN;
	}

	/** {@inheritDoc} */
	@Override
	public String[] getEditorList() {
		final String[] editors = new String[m_editors.size()];
		final Set<String> keys = m_editors.keySet();

		return keys.toArray(editors);
	}

	/** {@inheritDoc} */
	@Override
	public String getEditorPath(final WikiContext context) {
		final String editor = getEditorName(context);
		final WikiEditorInfo ed = m_editors.get(editor);
		final String path;
		if (ed != null) {
			path = ed.getPath();
		} else {
			path = "editors/" + editor + ".jsp";
		}

		return path;
	}

	/** Contains info about an editor. */
	private static final class WikiEditorInfo extends WikiModuleInfo {
		private String m_path;

		protected static WikiEditorInfo newInstance(final String name, final Element el) {
			if (name == null || name.length() == 0) {
				return null;
			}
			final WikiEditorInfo info = new WikiEditorInfo(name);
			info.initializeFromXML(el);
			return info;
		}

		/** {@inheritDoc} */
		@Override
		protected void initializeFromXML(final Element el) {
			super.initializeFromXML(el);
			m_path = el.getChildText("path");
		}

		private WikiEditorInfo(final String name) {
			super(name);
		}

		public String getPath() {
			return m_path;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Collection<WikiModuleInfo> modules() {
		return modules(m_editors.values().iterator());
	}

	/** {@inheritDoc} */
	@Override
	public WikiEditorInfo getModuleInfo(final String moduleName) {
		return m_editors.get(moduleName);
	}

	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
			break;
		}*/
	}

}
