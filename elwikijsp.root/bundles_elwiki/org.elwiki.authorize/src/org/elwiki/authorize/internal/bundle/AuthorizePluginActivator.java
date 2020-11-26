package org.elwiki.authorize.internal.bundle;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.annotation.Nullable;
import org.elwiki.authorize.authenticated.AuthenticatedContextActivator;
import org.elwiki.authorize.check.AuthorizeCheckActivator;
//:FVK: import org.elwiki.authorize.condition.SessionTypeCondition;
import org.elwiki.authorize.context.anonymous.AnonymousContextActivator;
import org.elwiki.authorize.context.asserted.AssertedContextActivator;
import org.elwiki.permissions.GroupPermission;
import org.elwiki.permissions.PagePermission;
import org.elwiki.permissions.WikiPermission;
import org.osgi.framework.BundleContext;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AuthorizePluginActivator extends Plugin {

	private static final Logger log = Logger.getLogger(AuthorizePluginActivator.class);

	// The plug-in ID
	static final String PLIGIN_ID = "org.elwiki.authorize";

	// The shared instance
	private static AuthorizePluginActivator pluginInstance;
	private ServiceTracker<ConditionalPermissionAdmin, Object> cpaTracker;

	/** :FVK: Old JSPwiki = "org.apache.wiki.auth.authorize" */
	public static String AUTHORIZATION_PACKAGE = "org.elwiki.authorize";

	// == CODE ================================================================

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static AuthorizePluginActivator getDefault() {
		return AuthorizePluginActivator.pluginInstance;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		AuthorizePluginActivator.pluginInstance = this;

//		Job job = Job.create("Update permissions", (ICoreRunnable) monitor -> {
//			initContextPermissions();
//		});
//		job.schedule();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		pluginInstance = null;
		super.stop(bundleContext);
	}

	public ConditionalPermissionAdmin getCpaService() {
		if (cpaTracker == null) {
			cpaTracker = new ServiceTracker<>(getBundle().getBundleContext(), ConditionalPermissionAdmin.class, null);
			cpaTracker.open();
		}
		ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) cpaTracker.getService();
		return cpa;
	}

	// -------------------------------------------------------------------------
	
	//:FVK: - политика установлена согласно тестам JSPwiki (файл "jspwiki.policy") 

	private static final PermissionInfo[] ANONYMOUS_PERMISSIONS = new PermissionInfo[] {
			new PermissionInfo(PagePermission.class.getName(), "*:*", "edit"),
			new PermissionInfo(WikiPermission.class.getName(), "*", "createPages") };

	private static final PermissionInfo[] ASSERTED_PERMISSIONS = new PermissionInfo[] {
			new PermissionInfo(PagePermission.class.getName(), "*:*", "edit"),
			new PermissionInfo(WikiPermission.class.getName(), "*", "createPages"),
			new PermissionInfo(GroupPermission.class.getName(), "*:*", "view") };

	private static final PermissionInfo[] AUTHENTICATED_PERMISSIONS = new PermissionInfo[] {
			new PermissionInfo(PagePermission.class.getName(), "*:*", "modify,rename"),
			new PermissionInfo(WikiPermission.class.getName(), "*", "createPages,createGroups"),
			new PermissionInfo(GroupPermission.class.getName(), "*:*", "view"),
			new PermissionInfo(GroupPermission.class.getName(), "*:<groupmember>", "edit"), };

	private static final String ANONYMOUS_MASK = "*/" + AnonymousContextActivator.PLUGIN_ID + "/*";
	private static final String ASSERTED_MASK = "*/" + AssertedContextActivator.PLUGIN_ID + "/*";
	private static final String AUTHENTICATED_MASK = "*/" + AuthenticatedContextActivator.PLUGIN_ID + "/*";

	private static final String INFONAME_DENYALL = "elwiki.context.denyAllPermission";
	private static final String INFONAME_ANONYMOUS = "elwiki.anonymous.permissions";
	private static final String INFONAME_ASSERTED = "elwiki.asserted.permissions";
	private static final String INFONAME_AUTHENTICATED = "elwiki.authenticated.permissions";

	private void initContextPermissions() {
		BundleContext context = getBundle().getBundleContext();
		@Nullable
		ConditionalPermissionAdmin cpa = context
				.getService(context.getServiceReference(ConditionalPermissionAdmin.class));

		ConditionalPermissionUpdate u = cpa.newConditionalPermissionUpdate();
		List<ConditionalPermissionInfo> infos = u.getConditionalPermissionInfos();
		if (infos.size() > 0) {
			log.error("Unexpected list of ConditionalPermissionInfo.");
		}

		String bundleLocation = AuthorizeCheckActivator.getContext().getBundle().getLocation();

		//@formatter:off
		//-- Add info of !AllPermission for context --
		infos.add(cpa.newConditionalPermissionInfo(INFONAME_DENYALL,
				new ConditionInfo[] {
						new ConditionInfo(BundleLocationCondition.class.getName(),
						new String[] { bundleLocation, "!" }) },
				new PermissionInfo[] {
						new PermissionInfo("java.security.AllPermission", "<all permissions>", "<all actions>") },
				ConditionalPermissionInfo.ALLOW));

		// -- Add info of Anonymous context --
		infos.add(cpa.newConditionalPermissionInfo(
				INFONAME_ANONYMOUS,
				new ConditionInfo[] {
//						new ConditionInfo(
//								SessionTypeCondition.class.getName(),
//								new String[] { SESSION_STATE.ASSERTED.toString() }),
						new ConditionInfo(
								BundleLocationCondition.class.getName(),
								new String[] { bundleLocation }),
				},
				ANONYMOUS_PERMISSIONS,
				ConditionalPermissionInfo.ALLOW));

		// -- Add info of Asserted context --
		infos.add(cpa.newConditionalPermissionInfo(
				INFONAME_ASSERTED,
				new ConditionInfo[] { new ConditionInfo(
						BundleLocationCondition.class.getName(),
						new String[] { ASSERTED_MASK })
				},
				ASSERTED_PERMISSIONS,
				ConditionalPermissionInfo.ALLOW));
		
		// -- Add info of Authenticated context --
		infos.add(cpa.newConditionalPermissionInfo(
				INFONAME_AUTHENTICATED,
				new ConditionInfo[] {
						new ConditionInfo(BundleLocationCondition.class.getName(),
						new String[] { AUTHENTICATED_MASK })
				},
				AUTHENTICATED_PERMISSIONS,
				ConditionalPermissionInfo.ALLOW));
		//@formatter:on

		if (!u.commit()) {
			log.error("Unsuccessful commit of ConditionalPermissionInfo.");
		}

	}

}