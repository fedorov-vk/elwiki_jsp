package org.apache.wiki.api.event;

/**
 * <p>
 * WorkflowEvent indicates that a state change to a Workflow: started, running, waiting, completed,
 * aborted. These correspond exactly to the states described in the
 * {@link org.apache.wiki.workflow.Workflow}. All events are logged with priority INFO.
 * </p>
 * 
 * @since 2.3.79
 */
public interface WikiWorkflowEventTopic extends WikiEventTopic {

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

	/* ====================================================================== */
	
	String PROPERTY_WORKFLOW = "workflow";
	String PROPERTY_STEP = "step";
	String PROPERTY_DECISION = "decision";

}
