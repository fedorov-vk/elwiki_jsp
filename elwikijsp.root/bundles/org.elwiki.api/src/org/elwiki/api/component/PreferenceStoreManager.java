package org.elwiki.api.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

final class PreferenceStoreManager {

	public static PreferenceStoreManager INSTANCE = new PreferenceStoreManager();

	private Map<String, IPreferenceStore> preferencesStore = new ConcurrentHashMap<>();

	private Lock getStoreLock = new ReentrantLock();

	public IPreferenceStore getPreferenceStore(String bundleName) {
		IPreferenceStore result = null;
		getStoreLock.lock();
		try {
			if (preferencesStore.containsKey(bundleName)) {
				result = preferencesStore.get(bundleName);
			} else {
				result = new ScopedPreferenceStore(InstanceScope.INSTANCE, bundleName);
				preferencesStore.put(bundleName, result);
			}
		} finally {
			getStoreLock.unlock();
		}

		return result;
	}

}
