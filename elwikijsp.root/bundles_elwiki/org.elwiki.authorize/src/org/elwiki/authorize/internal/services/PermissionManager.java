package org.elwiki.authorize.internal.services;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.event.ElWikiEventsConstants;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.IPermissionManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

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
				//:FVK: -- here -- initContextPermissions();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initialize(Engine engine1) throws WikiException {
		this.engine = engine1;
	}
/*
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
*/

	// -- service handling -----------------------------(end)--

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
	}
}
