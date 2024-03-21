package org.elwiki.api.component;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.BundleDefaultsScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public interface IModulePreferences {

	@SuppressWarnings("unchecked")
	default <T> T getPreference(String preferenceName, Class<T> resultType) {
		Object result = null;
		if (resultType.isAssignableFrom(Boolean.class)) {
			result = getInstancePreferences().getBoolean(preferenceName,
					getDefaultPreferences().getBoolean(preferenceName, false));
		} else if (resultType.isAssignableFrom(Integer.class)) {
			result = getInstancePreferences().getInt(preferenceName, getDefaultPreferences().getInt(preferenceName, 0));
		} else if (resultType.isAssignableFrom(String.class)) {
			result = getInstancePreferences().get(preferenceName, getDefaultPreferences().get(preferenceName, ""));
		} else {
			Assert.isTrue(false, "This preference type is not supported.");
		}

		return (T) result;
	}

	default IEclipsePreferences getDefaultPreferences() {
		String bundleName = getBundleContext().getBundle().getSymbolicName();
		return BundleDefaultsScope.INSTANCE.getNode(bundleName);
	}

	default IEclipsePreferences getInstancePreferences() {
		String bundleName = getBundleContext().getBundle().getSymbolicName();
		return InstanceScope.INSTANCE.getNode(bundleName);
	}

	default IPreferenceStore getPreferenceStore() {
		String bundleName = getBundleContext().getBundle().getSymbolicName();
		return PreferenceStoreManager.INSTANCE.getPreferenceStore(bundleName);
	}

	BundleContext getBundleContext();

}
