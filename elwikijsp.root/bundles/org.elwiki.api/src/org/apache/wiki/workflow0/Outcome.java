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
package org.apache.wiki.workflow0;

public enum Outcome {
	//@formatter:off
	
	/** Complete workflow step (without errors) */
	STEP_COMPLETE("outcome.step.complete", true),
	
	/** Terminate workflow step (without errors) */
	STEP_ABORT("outcome.step.abort", true),

	/** Continue workflow step (without errors) */
	STEP_CONTINUE("outcome.step.continue", false),

	/** Acknowledge the Decision. */
	DECISION_ACKNOWLEDGE("outcome.decision.acknowledge", true),

	/** Approve the Decision (and complete the step). */
	DECISION_APPROVE("outcome.decision.approve", true),

	/** Deny the Decision (and complete the step). */
	DECISION_DENY("outcome.decision.deny", true),

	/** Put the Decision on hold (and pause the step). */
	DECISION_HOLD("outcome.decision.hold", false),

	/** Reassign the Decision to another actor (and pause the step). */
	DECISION_REASSIGN("outcome.decision.reassign", false);
	//@formatter:on

	private final String m_key;
	private final boolean m_completion;

	Outcome(String key, boolean completion) {
		m_key = key;
		m_completion = completion;
	}

	/**
	 * Returns <code>true</code> if this Outcome represents a completion condition for a Step.
	 *
	 * @return the result
	 */
	public boolean isCompletion() {
		return m_completion;
	}

	/**
	 * The i18n key for this outcome, which is prefixed by <code>outcome.</code>. If calling classes
	 * wish to return a locale-specific name for this task (such as "approve this request"), they can
	 * use this method to obtain the correct key suffix.
	 *
	 * @return the i18n key for this outcome
	 */
	public String getMessageKey() {
		return m_key;
	}

	/**
	 * Returns an Outcome. If an Outcome matching the supplied name is not found, this method throws
	 * a {@link NoSuchOutcomeException}.
	 *
	 * @param key the name of the outcome
	 * @return the Outcome
	 * @throws NoSuchOutcomeException if an Outcome matching the name isn't found.
	 */
	public static Outcome forName(String key) throws NoSuchOutcomeException {
		try {
			return Outcome.valueOf(key);
		} catch (Exception ex) {
			throw new NoSuchOutcomeException("Outcome " + key + " not found.");
		}
	}

}
