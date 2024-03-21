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
package org.elwiki.api.event;

/**
 * <p>
 * WorkflowEvent indicates that a state change to a Workflow: started, running, waiting, completed,
 * aborted. These correspond exactly to the states described in the
 * {@link org.apache.wiki.workflow.Workflow}. All events are logged with priority INFO.
 * </p>
 * 
 * @since 2.3.79
 */
public interface WorkflowEvent extends WikiEvent {

	interface Topic {
		String DOMAIN = WikiEvent.Topic.DOMAIN + "/workflow";
		String ALL = DOMAIN + "/*";

		/** After Workflow instantiation. */
		String CREATED = DOMAIN + "/CREATED";

		/** If a Step has elected to abort the Workflow. */
		String ABORTED = DOMAIN + "/ABORTED";

		/** After the Workflow has finished processing all Steps, without errors. */
		String COMPLETED = DOMAIN + "/COMPLETED";

		/**
		 * After the Workflow has been instantiated, but before it has been started using the
		 * {@link Workflow#start()} method.
		 */
		String STARTED = DOMAIN + "/STARTED";

		/**
		 * After the Workflow has been started (or re-started) using the {@link Workflow#start()} method,
		 * but before it has finished processing all Steps.
		 */
		String RUNNING = DOMAIN + "/RUNNING";

		/** When the Workflow has temporarily paused, for example because of a pending Decision. */
		String WAITING = DOMAIN + "/WAITING";

		/** When the workflow wishes to add a Decision to the DecisionQueue */
		String DQ_ADDITION = DOMAIN + "/DQ_ADDITION";

		/** When the decision queue decides the outcome of a Decision */
		String DQ_DECIDE = DOMAIN + "/DQ_DECIDE";

		/** When the workflow wishes to remove a Decision from the DecisionQueue */
		String DQ_REMOVAL = DOMAIN + "/DQ_REMOVAL";

		/** When the decision queue reassigns a Decision */
		String DQ_REASSIGN = DOMAIN + "/DQ_REASSIGN";
	}

	String PROPERTY_WORKFLOW = "workflow";
	String PROPERTY_STEP = "step";
	String PROPERTY_DECISION = "decision";

}
