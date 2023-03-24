package org.elwiki.plugins.internal;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.wiki.api.exceptions.PluginException;
import org.eclipse.osgi.service.localization.BundleLocalization;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class PluginsActivator implements BundleActivator {

	private static BundleContext bundleContext;

	public static BundleContext getContext() {
		return bundleContext;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		PluginsActivator.bundleContext = bundleContext;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		PluginsActivator.bundleContext = null;
	}

	/**
	 * Returns required service implementation. Can be <code>null</code>.
	 *
	 * @param <T>
	 * @param clazz
	 * @return required service implementation.
	 */
	public static <T> T getService(Class<T> clazz) {
		ServiceReference<T> ref = bundleContext.getServiceReference(clazz);
		if (ref != null) {
			return bundleContext.getService(ref);
		}
		return null;
	}

	/**
	 * Locates the specified i18n ResourceBundle of core plugins.<br/>
	 * This method interprets the request locale, and uses that to figure out which language the user
	 * wants.<br/>
	 * Returns ResourceBundle instance of required locale for core plugins.
	 *
	 * @param locale the required Locale instance.
	 * @return A ResourceBundle with localized strings (or from the default language, if Locale not defined).
	 * @throws PluginException - In case if osgi component or resource bundle is not possible to get.
	 */
	public static ResourceBundle getBundle(Locale locale) throws PluginException {
		BundleLocalization bundleLocalization = getService(BundleLocalization.class);
		if (bundleLocalization == null) {
			throw new PluginException("Can't get BundleLocalization component of osgi.");
		}
		ResourceBundle resourceBundle = bundleLocalization.getLocalization(bundleContext.getBundle(),
				locale.toString());
		if (resourceBundle == null) {
			throw new PluginException(
					"Can't load ResourceBundle object of core plugins, for " + locale.toString() + " locale.");
		}

		return resourceBundle;
	}

	public static String getMessage(String resourceId, Locale locale) throws PluginException {
		try {
			ResourceBundle rb = getBundle(locale);
			return rb.getString(resourceId);
		} catch (Exception ex) {
			throw new PluginException(ex.getMessage());
		}
	}

}
