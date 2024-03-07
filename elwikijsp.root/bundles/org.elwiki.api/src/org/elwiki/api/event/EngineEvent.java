package org.elwiki.api.event;

/**
 * EngineEvent topics indicates a change in the state of the Engine.
 */
public interface EngineEvent extends WikiEvent {

	interface Topic {
		String DOMAIN = WikiEvent.Topic.DOMAIN + "/engine";
		String ALL = DOMAIN + "/*";

		/**
		 * Indicates of an Stage One of WikiEngine initialization.<br/>
		 * Fired as the Engine component is being activated (in progress).
		 */
		String INIT_STAGE_ONE = DOMAIN + "/INIT_STAGE_ONE";

		/**
		 * Indicates of an Stage Two of WikiEngine initialization, fired after basic initialization of all
		 * wiki components.<br/>
		 * Fired after the wiki services become available for full initialization.
		 */
		String INIT_STAGE_TWO = DOMAIN + "/INIT_STAGE_TWO";

		/**
		 * Indicates the completion of Engine initialization.<br/>
		 * Fired after the wiki components are fully available.
		 */
		String INIT_DONE = DOMAIN + "/INIT_DONE";

		/**
		 * Indicates a Engine closing event.<br/>
		 * Fired as a signal that the wiki is shutting down.
		 */
		String SHUTDOWN = DOMAIN + "/SHUTDOWN";

		/**
		 * Indicates a Engine stopped event. A Engine in this state is not expected to provide further
		 * services.<br/>
		 * Fired after halting the wiki service.
		 */
		@Deprecated
		String STOPPED = DOMAIN + "/STOPPED";
	}

}
