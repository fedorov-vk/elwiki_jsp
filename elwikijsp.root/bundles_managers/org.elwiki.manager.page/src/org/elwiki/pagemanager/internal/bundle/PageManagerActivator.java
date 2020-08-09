package org.elwiki.pagemanager.internal.bundle;

import org.apache.wiki.pages0.PageManager;
//import org.elwiki.api.pagemanager.IPageManager;
//import org.elwiki.api.pageprovider.IWikiPageProvider;
//import org.elwiki.configuration.IWikiConfiguration;
//import org.elwiki.pagemanager.internal.service.PageManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Активатор требуется для обеспечения доступа к ...
 * 
 * @author vfedorov
 */
public class PageManagerActivator implements BundleActivator {

	public static String PLIGIN_ID = "org.elwiki.manager.page";

	// The shared instance
	private static PageManagerActivator instance;

	private static BundleContext context;

	// == CODE ================================================================

	/**
	 * Constructor.
	 */
	public PageManagerActivator() {
		super();
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		PageManagerActivator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		PageManagerActivator.context = null;
	}

	public static BundleContext getContext() {
		return context;
	}

	public static PageManagerActivator getDefault() {
		return PageManagerActivator.instance;
	}

}