/* 
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.  
 */
package org.apache.wiki.workflow;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.workflow0.Fact;
import org.apache.wiki.workflow0.Decision;
import org.apache.wiki.workflow0.IWorkflowBuilder;
import org.apache.wiki.workflow0.Outcome;
import org.apache.wiki.workflow0.SimpleDecision;
import org.apache.wiki.workflow0.SimpleNotification;
import org.apache.wiki.workflow0.Step;
import org.apache.wiki.workflow0.Workflow;
import org.apache.wiki.workflow0.WorkflowManager;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory class that creates common Workflow instances such as a standard approval workflow.
 */
public final class WorkflowBuilder implements IWorkflowBuilder {

    private static final Map< Engine, WorkflowBuilder > BUILDERS = new ConcurrentHashMap<>();
    private final Engine m_engine;

    /**
     * Private constructor that creates a new WorkflowBuilder for the supplied Engine.
     * @param engine the wiki engine
     */
    private WorkflowBuilder( final Engine engine )
    {
        m_engine = engine;
    }

    /**
     * Returns the WorkflowBuilder instance for a Engine. Only one WorkflowBuilder
     * exists for a given engine.
     * @param engine the wiki engine
     * @return the workflow builder
     */
    public static IWorkflowBuilder getBuilder( final Engine engine ) {
        WorkflowBuilder builder = BUILDERS.get( engine );
        if ( builder == null ) {
            builder = new WorkflowBuilder( engine );
            BUILDERS.put( engine, builder );
        }
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    //@formatter:off
    @Override
	public Workflow buildApprovalWorkflow(Principal submitter,
                                          String workflowApproverKey,
                                          Step prepTask,
                                          String decisionKey,
                                          List<Fact> facts,
                                          Step completionTask,
                                          String rejectedMessageKey) throws WikiException {
        //@formatter:on
		WorkflowManager mgr = m_engine.getManager(WorkflowManager.class);
		Workflow workflow = new Workflow(workflowApproverKey, submitter);

		// Is a Decision required to run the approve task?
		boolean isDecisionRequired = mgr.requiresApproval(workflowApproverKey);

		// If Decision required, create a simple approval workflow
		if (isDecisionRequired) {
			// Look up the name of the approver (user or group) listed in preferences.ini; approvals go to the approver's decision queue
			Principal approver = mgr.getApprover(workflowApproverKey);
			Decision decision = new SimpleDecision(workflow.getId(), workflow.getAttributes(), decisionKey, approver,
					submitter);

			// Add facts to the Decision, if any were supplied
			if (facts != null) {
				decision.addFacts(facts);
				// Add the first one as a message key
				if (facts.size() > 0) {
					workflow.addMessageArgument(facts.get(0).getValue());
				}
			}

			// If rejected, sent a notification
			if (rejectedMessageKey != null) {
				SimpleNotification rejectNotification = new SimpleNotification(workflow.getId(),
						workflow.getAttributes(), rejectedMessageKey, submitter, approver);
				decision.addSuccessor(Outcome.DECISION_DENY, rejectNotification);
			}

			// If approved, run the 'approved' task
			decision.addSuccessor(Outcome.DECISION_APPROVE, completionTask);

			// Set the first step
			if (prepTask == null) {
				workflow.setFirstStep(decision);
			} else {
				workflow.setFirstStep(prepTask);
				prepTask.addSuccessor(Outcome.STEP_COMPLETE, decision);
			}
		} else { // If Decision not required, just run the prep + approved tasks in succession
			// Set the first step
			if (prepTask == null) {
				workflow.setFirstStep(completionTask);
			} else {
				workflow.setFirstStep(prepTask);
				prepTask.addSuccessor(Outcome.STEP_COMPLETE, completionTask);
			}
		}

		// Make sure our tasks have this workflow as the parent, then return
		if (prepTask != null) {
			prepTask.setWorkflow(workflow.getId(), workflow.getAttributes());
		}
		completionTask.setWorkflow(workflow.getId(), workflow.getAttributes());

		return workflow;
	}

}
