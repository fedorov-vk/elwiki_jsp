package org.elwiki.authorize.internal.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.event.ElWikiEventsConstants;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.IPermissionManager;
import org.elwiki.IWikiConstants.AuthenticationStatus;
import org.elwiki.authorize.authenticated.AuthenticatedContextActivator;
import org.elwiki.authorize.condition.SessionTypeCondition;
import org.elwiki.authorize.context.anonymous.AnonymousContextActivator;
import org.elwiki.authorize.context.asserted.AssertedContextActivator;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.permissions.GroupPermission;
import org.elwiki.permissions.PagePermission;
import org.elwiki.permissions.WikiPermission;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.permissionadmin.PermissionInfo;

//@formatter:off
@Component(
	name = "elwiki.PermissionManager",
	service = {IPermissionManager.class, EventHandler.class},
	factory = IPermissionManager.COMPONENT_FACTORY,
	property = EventConstants.EVENT_TOPIC + "=" + ElWikiEventsConstants.TOPIC_LOGGING_ALL)
//@formatter:on
public class PermissionManager implements IPermissionManager, EventHandler {

	private static final Logger log = Logger.getLogger(PermissionManager.class);

	private Engine engine;

	@Reference
	private ConditionalPermissionAdmin cpaService;

	/** Stores configuration. */
	@Reference //(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	// -- service handling ---------------------------(start)--

	/**
	 * This component activate routine. Does all the real initialization.
	 * Initializes security policy of AuthorizationManager.
	 * 
	 * @param componentContext
	 * @throws WikiException if the AuthorizationManager failed on startup.
	 */
	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		try {
			Object engine = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
			if (engine instanceof Engine) {
				initialize((Engine) engine);
				initContextPermissions();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initialize(Engine engine1) throws WikiException {
		this.engine = engine1;
	}

	private static final PermissionInfo[] ANONYMOUS_PERMISSIONS = new PermissionInfo[] {
			//new PermissionInfo(AdminPermission.class.getName(), "*", "context"),
			new PermissionInfo(PagePermission.class.getName(), "*:*", "view"),
			new PermissionInfo(WikiPermission.class.getName(), "*", "createPages,login") };

	private static final PermissionInfo[] ASSERTED_PERMISSIONS = new PermissionInfo[] {
			//new PermissionInfo(AdminPermission.class.getName(), "*", "context"),
			new PermissionInfo(PagePermission.class.getName(), "*:*", "edit"),
			new PermissionInfo(WikiPermission.class.getName(), "*", "createPages,login"),
			new PermissionInfo(GroupPermission.class.getName(), "*:*", "view") };

	private static final PermissionInfo[] AUTHENTICATED_PERMISSIONS = new PermissionInfo[] {
			//new PermissionInfo(AdminPermission.class.getName(), "*", "context"),
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

	private void initContextPermissions() throws Exception {
		ConditionalPermissionUpdate u = cpaService.newConditionalPermissionUpdate();
		List<ConditionalPermissionInfo> infos = u.getConditionalPermissionInfos();
		if (infos.size() > 0) {
			log.error("Unexpected list of ConditionalPermissionInfo.");
		}

		//@formatter:off
		//-- Add info of DENY AllPermission for context --
		infos.add(cpaService.newConditionalPermissionInfo(INFONAME_DENYALL,
				new ConditionInfo[] {
						new ConditionInfo(BundleLocationCondition.class.getName(),
						new String[] { "*/org.elwiki.authorize.context.*", "!" }) },
				new PermissionInfo[] {
						new PermissionInfo("java.security.AllPermission", "<all permissions>", "<all actions>") },
				//new PermissionInfo("java.security.AllPermission", "*", "*") },						
				ConditionalPermissionInfo.ALLOW));
		//@formatter:on

		// -- Add info of Anonymous context --
		infos.add(cpaService.newConditionalPermissionInfo(INFONAME_ANONYMOUS,
				new ConditionInfo[] {
						new ConditionInfo(BundleLocationCondition.class.getName(), new String[] { ANONYMOUS_MASK }), },
				ANONYMOUS_PERMISSIONS, ConditionalPermissionInfo.ALLOW));

		// -- Add info of Asserted context --
		infos.add(cpaService.newConditionalPermissionInfo(INFONAME_ASSERTED,
				new ConditionInfo[] {
						new ConditionInfo(BundleLocationCondition.class.getName(), new String[] { ASSERTED_MASK }), },
				ASSERTED_PERMISSIONS, ConditionalPermissionInfo.ALLOW));

		// -- Add info of Authenticated context --
		infos.add(cpaService.newConditionalPermissionInfo(INFONAME_AUTHENTICATED,
				new ConditionInfo[] { new ConditionInfo(BundleLocationCondition.class.getName(),
						new String[] { AUTHENTICATED_MASK }), },
				AUTHENTICATED_PERMISSIONS, ConditionalPermissionInfo.ALLOW));

		if (!u.commit()) {
			log.error("Unsuccessful commit of ConditionalPermissionInfo.");
		}
	}

	// -- service handling -----------------------------(end)--

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
	}
}
