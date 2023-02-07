package org.elwiki.authorize.internal.accounting;

import java.util.HashMap;
import java.util.Map;

import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.user0.UserDatabase;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.osgi.framework.Bundle;

/**
 * Reads the extension data for the Account Manager instance.
 * 
 * @author vfedorov
 */
public class ExtensionHandler {

	/** The extension ID for access to the implementation set of {@link IUserDatabase}. */
	private static final String ID_EXTENSION_USER_DATABASE = "userProfileDatabase";

	static public Map<String, Class<? extends UserDatabase>> getUserDatabaseImplementations() throws WikiException {
		String namespace = AuthorizePluginActivator.getDefault().getBundle().getSymbolicName();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep;
		Map<String, Class<? extends UserDatabase>> userDatabaseClasses = new HashMap<>();

		//
		// Load the UserDatabase definitions from Equinox extensions.
		//
		ep = registry.getExtensionPoint(namespace, ID_EXTENSION_USER_DATABASE);
		if (ep != null) {
			for (IConfigurationElement el : ep.getConfigurationElements()) {
				String contributorName = el.getContributor().getName();
				String className = el.getAttribute("class");
				String userDatabaseId = el.getAttribute("id");
				try {
					final Bundle bundle = Platform.getBundle(contributorName);
					Class<?> clazz = bundle.loadClass(className);
					try {
						Class<? extends UserDatabase> cl = clazz.asSubclass(UserDatabase.class);
						userDatabaseClasses.put(userDatabaseId, (Class<? extends UserDatabase>) cl);
					} catch (ClassCastException e) {
						throw new WikiException(
								"UserDatabase " + className + " is not extends IUserDatabase interface.", e);
					}
				} catch (ClassNotFoundException e) {
					throw new WikiException("UserDatabase " + className + " cannot be found.", e);
				}
			}
		}

		return userDatabaseClasses;
	}
	
}
