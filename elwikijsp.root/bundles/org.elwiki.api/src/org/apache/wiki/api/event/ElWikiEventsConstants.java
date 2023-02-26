package org.apache.wiki.api.event;

public interface ElWikiEventsConstants {

	String TOPIC_BASE_ELWIKI = "org/elwiki/events";

	/** All ElWiki events. (workaround) */
	String TOPIC_ALL = TOPIC_BASE_ELWIKI + "/*";

	/* == INITIALIZATION ==================================================== */

	String TOPIC_INIT = TOPIC_BASE_ELWIKI + "/init";
	String TOPIC_INIT_ALL = TOPIC_INIT + "/*";
	String TOPIC_INIT_STAGE_ONE = TOPIC_INIT + "/STAGE_ONE";
	String TOPIC_INIT_STAGE_TWO = TOPIC_INIT + "/STAGE_TWO";

	String TOPIC_INIT_DONE = TOPIC_INIT + "/DONE";

	/* == LOGGING =========================================================== */

	String TOPIC_LOGGING = TOPIC_BASE_ELWIKI + "/logging";
	String TOPIC_LOGGING_ALL = TOPIC_LOGGING + "/*";

	/** When a user first accesses JSPWiki, but before logging in or setting a cookie. */
	String TOPIC_LOGIN_ANONYMOUS = TOPIC_LOGGING + "/LOGIN_ANONYMOUS";

	/** When a user sets a cookie to assert their identity. */
	String TOPIC_LOGIN_ASSERTED = TOPIC_LOGGING + "/LOGIN_ASSERTED";

	/** When a user authenticates with a username and password, or via container auth. */
	String TOPIC_LOGIN_AUTHENTICATED = TOPIC_LOGGING + "/LOGIN_AUTHENTICATED";

	/** When a user logs out. */
	String TOPIC_LOGOUT = TOPIC_LOGGING + "/LOGOUT";

	/* == WORKFLOW ========================================================== */

	String TOPIC_WORKFLOW = TOPIC_BASE_ELWIKI + "/workflow";
	String TOPIC_WORKFLOW_ALL = TOPIC_WORKFLOW + "/*";

	/** After Workflow instantiation. */
	String TOPIC_WORKFLOW_CREATED = TOPIC_WORKFLOW + "/CREATED";

	/** If a Step has elected to abort the Workflow. */
	String TOPIC_WORKFLOW_ABORTED = TOPIC_WORKFLOW + "/ABORTED";

	/** After the Workflow has finished processing all Steps, without errors. */
	String TOPIC_WORKFLOW_COMPLETED = TOPIC_WORKFLOW + "/COMPLETED";

	/**
	 * After the Workflow has been instantiated, but before it has been started using the
	 * {@link Workflow#start()} method.
	 */
	String TOPIC_WORKFLOW_STARTED = TOPIC_WORKFLOW + "/STARTED";

	/**
	 * After the Workflow has been started (or re-started) using the {@link Workflow#start()} method,
	 * but before it has finished processing all Steps.
	 */
	String TOPIC_WORKFLOW_RUNNING = TOPIC_WORKFLOW + "/RUNNING";

	/** When the Workflow has temporarily paused, for example because of a pending Decision. */
	String TOPIC_WORKFLOW_WAITING = TOPIC_WORKFLOW + "/WAITING";

	/** When the workflow wishes to add a Decision to the DecisionQueue */
	String TOPIC_WORKFLOW_DQ_ADDITION = TOPIC_WORKFLOW + "/DQ_ADDITION";

	/** When the decision queue decides the outcome of a Decision */
	String TOPIC_WORKFLOW_DQ_DECIDE = TOPIC_WORKFLOW + "/DQ_DECIDE";

	/** When the workflow wishes to remove a Decision from the DecisionQueue */
	String TOPIC_WORKFLOW_DQ_REMOVAL = TOPIC_WORKFLOW + "/DQ_REMOVAL";

	/** When the decision queue reassigns a Decision */
	String TOPIC_WORKFLOW_DQ_REASSIGN = TOPIC_WORKFLOW + "/DQ_REASSIGN";

	String PROPERTY_WORKFLOW = "workflow";
	String PROPERTY_STEP = "step";
	String PROPERTY_DECISION = "decision";

	/* ====================================================================== */

	/**
	 * Indicates the HttpSession identifier, for identification of the corresponding wiki-session for
	 * sending events.
	 */
	String PROPERTY_KEY_TARGET = "target";

	String PROPERTY_LOGIN_PRINCIPALS = "principal";

}
