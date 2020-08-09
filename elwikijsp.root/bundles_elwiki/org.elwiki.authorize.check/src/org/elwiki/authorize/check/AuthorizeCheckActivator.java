package org.elwiki.authorize.check;

import java.security.Permission;
import java.util.function.Function;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AuthorizeCheckActivator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public static Boolean test(Function<Permission, Boolean> function, Permission permission) {
		return function.apply(permission);
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		AuthorizeCheckActivator.context = bundleContext;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		AuthorizeCheckActivator.context = null;
	}

}