package org.elwiki.pageprovider.cdo.internal.bundle;

import org.apache.wiki.api.IStorageCdo;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class PageProviderCdoActivator implements BundleActivator {

	public static String PLIGIN_ID = "org.elwiki.provider.page.cdo";

	// The shared instance
	private static PageProviderCdoActivator instance;

	private static BundleContext context;

	private static IStorageCdo storageCdo;

	// == CODE ================================================================

	/**
	 * Constructor.
	 */
	public PageProviderCdoActivator() {
		super();
		instance = this;
	}

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		PageProviderCdoActivator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		PageProviderCdoActivator.context = null;
	}

	public static PageProviderCdoActivator getDefault() {
		return PageProviderCdoActivator.instance;
	}


	public static <T> T getService(Class<T> clazz) {
		Bundle bundle = context.getBundle();
		if (bundle != null) {
			ServiceTracker<T, T> st = new ServiceTracker<T, T>(bundle.getBundleContext(), clazz, null);
			st.open();
			if (st != null) {
				try {
					return st.waitForService(1500L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static IStorageCdo getStorageCdo() {
		if(storageCdo == null) {
			storageCdo = getService(IStorageCdo.class);

			if (!storageCdo.isStorageActive()) { //:FVK:? WORKAROUND.
				try {
					storageCdo.activateStorage();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return storageCdo;
	}

}