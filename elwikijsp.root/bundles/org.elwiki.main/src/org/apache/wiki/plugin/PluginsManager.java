package org.apache.wiki.plugin;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.spi.LoginModule;

import org.apache.log4j.Logger;
import org.apache.wiki.WikiEngine;
import org.apache.wiki.api.plugin.Plugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class PluginsManager {

    private static final Logger log = Logger.getLogger( WikiEngine.class );
	
	private static PluginsManager instancePluginsManager;

	// org.apache.wiki.plugin.UndefinedPagesPlugin
	private static String PLIGIN_ID = "org.elwiki.api"; 

	private static String ID_EXTENSION_WIKI_PLUGIN = "WikiPlugin";

	Map<String, Class<? extends Plugin>> pluginClasses = new HashMap<>();

	// == CODE ================================================================

	public static PluginsManager getInstance() {
		if (instancePluginsManager == null) {
			instancePluginsManager = new PluginsManager();
			instancePluginsManager.initialize();
		}
		return instancePluginsManager;
	}

	private void initialize() {
		String namespace = PLIGIN_ID;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep;

		/* -- Загрузка классов плагинов. -- */
		ep = registry.getExtensionPoint(namespace, ID_EXTENSION_WIKI_PLUGIN);
		if (ep != null) {
			for (IConfigurationElement el : ep.getConfigurationElements()) {
				String contributorName = el.getContributor().getName();
				String className = el.getAttribute("class");
				String wikiPluginId = el.getAttribute("id");
				try {
					final Bundle bundle = Platform.getBundle(contributorName);
					Class<?> clazz = bundle.loadClass(className);
					try {
						Class<? extends Plugin> cl = clazz.asSubclass(Plugin.class);
						this.pluginClasses.put(wikiPluginId, (Class<? extends Plugin>) clazz);
					} catch (ClassCastException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public Class<? extends Plugin> getPluginClass(String classname) {
		String pluginId = classname;
		Class<? extends Plugin> result;
		
		String PREFIX = "org.apache.wiki.plugin.";
		if (pluginId.startsWith(PREFIX)) {
			pluginId = pluginId.replace(PREFIX, "");
		}
		result = this.pluginClasses.get(pluginId);
		if (result == null) {
			log.debug(":FVK: ~ ~ ~ ~ ERROR: Не найден плагин для '" + pluginId + "' (" + classname + ")");
		}
		return result;
	}

}
