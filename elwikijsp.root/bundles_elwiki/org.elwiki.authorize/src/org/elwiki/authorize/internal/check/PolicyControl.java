package org.elwiki.authorize.internal.check;

import java.io.FilePermission;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AllPermission;
import java.security.Permission;

import org.apache.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.permissions.PagePermission;
import org.osgi.framework.Bundle;

@Deprecated
public class PolicyControl {

	private static final Logger log = Logger.getLogger(PolicyControl.class);
	
	public static boolean checkPermission() {
		Bundle bundle = AuthorizePluginActivator.getDefault().getBundle();
		@Nullable
		AccessControlContext acc = bundle.adapt(AccessControlContext.class);

		log.info("--TEST-------------------------------------");

		testPermission(acc, new FilePermission("test", "write"), false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(acc, new FilePermission("test", "read"), true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(acc, new AllPermission(), false);

//		testPermission(acc, new PagePermission("page", "edit"), true);

		//testPermission(acc, new PagePermission("_:page", "view"), true);
		testPermission(acc, new PagePermission("wiki:page", "edit"), false);
		testPermission(acc, new PagePermission("wiki:page", "delete"), false);
		testPermission(acc, new PagePermission("wiki:page", "view"), true);
		
		return true;
	}

	private static void testPermission(AccessControlContext acc, Permission permission, boolean expectedToPass) {
		try {
			SecurityManager sm = System.getSecurityManager();
			sm.checkPermission(permission, acc);
			if (!expectedToPass) {
				System.err.println("FAIL: test should not have the permission " + permission); //$NON-NLS-1$
			}
		} catch (AccessControlException e) {
			if (expectedToPass) {
				System.err.println("FAIL: test should have the permission " + permission); //$NON-NLS-1$
			}
		}
	}

}
