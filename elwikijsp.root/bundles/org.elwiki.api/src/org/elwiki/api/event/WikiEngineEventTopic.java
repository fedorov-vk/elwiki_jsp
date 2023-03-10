package org.elwiki.api.event;

/**
 * WikiEngineEventTopic indicates a change in the state of the Engine.
 *
 */
public interface WikiEngineEventTopic extends WikiEventTopic {

	/* == INITIALIZATION ==================================================== */

	String TOPIC_ENGINE = TOPIC_BASE_ELWIKI + "/engine";
	String TOPIC_ENGINE_ALL = TOPIC_ENGINE + "/*";

	/**
	 * Indicates of an Stage One of WikiEngine initialization, fired as the Engine component is being
	 * activated (in progress).
	 */
	String TOPIC_ENGINE_INIT_STAGE_ONE = TOPIC_ENGINE + "/INIT_STAGE_ONE";

	/**
	 * Indicates of an Stage Two of WikiEngine initialization, fired after basic initialization of all
	 * wiki components.<br/>
	 * Fired after the wiki services is avilabled for full initialization.
	 */
	String TOPIC_ENGINE_INIT_STAGE_TWO = TOPIC_ENGINE + "/INIT_STAGE_TWO";

	/** Indicates a Engine initialized event, fired after the wiki components are fully available. */
	String TOPIC_ENGINE_INIT_DONE = TOPIC_ENGINE + "/INIT_DONE";

	/** Indicates a Engine closing event, fired as a signal that the wiki is shutting down. */
	String TOPIC_ENGINE_SHUTDOWN = TOPIC_ENGINE + "/ENGINE_SHUTDOWN";

	/**
	 * Indicates a Engine stopped event, fired after halting the wiki service. A Engine in this state is
	 * not expected to provide further services.
	 */
	@Deprecated
	String TOPIC_ENGINE_STOPPED = TOPIC_ENGINE + "/ENGINE_SHUTDOWN";

}