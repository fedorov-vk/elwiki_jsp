package org.elwiki.services;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.event.ElWikiEventsConstants;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.workflow0.WorkflowManager;
import org.elwiki.api.BackgroundThreads;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

//@formatter:off
@Component(
	name = "elwiki.InitTrigger",
	service = { InitTrigger.class, EventHandler.class },
	//reference = @Reference(name = "elwiki.Engine", service = Engine.class),
	property = EventConstants.EVENT_TOPIC + "=" + ElWikiEventsConstants.TOPIC_ALL)
//@formatter:on
public class InitTriggerImpl implements InitTrigger, EventHandler {

    private static final Logger log = Logger.getLogger( InitTriggerImpl.class );
	
	// -- OSGi service handling --------------------( start )--

    @Activate
	protected void startup(ComponentContext componentContext) {
    	log.debug(">> Activate InitTrigger");
    }

	// -- OSGi service handling ----------------------( end )--

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
	}

}
