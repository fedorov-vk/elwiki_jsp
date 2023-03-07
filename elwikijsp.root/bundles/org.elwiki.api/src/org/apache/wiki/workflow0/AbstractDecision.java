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

import org.apache.wiki.api.event.WikiWorkflowEventTopic;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.internal.ApiActivator;
import org.osgi.service.event.Event;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * AbstractStep subclass that asks an actor Principal to choose an Outcome on behalf of an owner (also a Principal). The actor
 * "makes the decision" by calling the {@link #decide(Outcome)} method. When this method is called, it will set the Decision's Outcome to
 * the one supplied. If the parent Workflow is in the {@link Workflow#WAITING} state, it will be re-started. Any checked WikiExceptions
 * thrown by the workflow after re-start will be re-thrown to callers.
 * </p>
 * <p>
 * When a Decision completes, its {@link #isCompleted()} method returns <code>true</code>. It also tells its parent WorkflowManager to
 * remove it from the list of pending tasks by calling {@link DecisionQueue#remove(Decision)}.
 * </p>
 * <p>
 * To enable actors to choose an appropriate Outcome, Decisions can store arbitrary key-value pairs called "facts." These facts can be
 * presented by the user interface to show details the actor needs to know about. Facts are added by calling classes to the Decision,
 * in order of expected presentation, by the {@link #addFact(Fact)} method. They can be retrieved, in order, via {@link #getFacts()}.
 * </p>
 *
 * @since 2.5
 */
abstract class AbstractDecision extends AbstractStep implements Decision {

    private static final long serialVersionUID = -6835601038263238062L;

    private Principal m_actor;

    private Principal m_submitter;

    private int m_id;

    private final Outcome m_defaultOutcome;

    private final List<Fact> m_facts;

    /**
     * Constructs a new Decision for a required "actor" Principal, having a default Outcome.
     *
     * @param workflowId the parent workflow id to set
     * @param workflowContext the parent workflow context to set
     * @param messageKey the i18n message key that represents the message the actor will see
     * @param actor the Principal (<em>e.g.</em>, a WikiPrincipal, Role, GroupPrincipal) who is required to select an appropriate Outcome
     * @param submitter TODO
     * @param defaultOutcome the Outcome that the user interface will recommend as the default choice
     */
    AbstractDecision( final int workflowId, final Map< String, Serializable > workflowContext, final String messageKey, final Principal actor, Principal submitter, final Outcome defaultOutcome ) {
        super( workflowId, workflowContext, messageKey );
        m_actor = actor;
        m_submitter = submitter;
        m_defaultOutcome = defaultOutcome;
        m_facts = new ArrayList<>();
        addSuccessor( defaultOutcome, null );
    }

    /**
     * Appends a Fact to the list of Facts associated with this Decision.
     * 
     * @param fact the new fact to add
     */
    @Override
	public final void addFact( final Fact fact )
    {
        m_facts.add( fact );
    }

    /**
     * <p>
     * Sets this Decision's outcome, and restarts the parent Workflow if it is
     * in the {@link Workflow#WAITING} state and this Decision is its currently
     * active Step. Any checked WikiExceptions thrown by the workflow after
     * re-start will be re-thrown to callers.
     * </p>
     * <p>
     * This method cannot be invoked if the Decision is not the current Workflow
     * step; all other invocations will throw an IllegalStateException. If the
     * Outcome supplied to this method is one one of the Outcomes returned by
     * {@link #getAvailableOutcomes()}, an IllegalArgumentException will be
     * thrown.
     * </p>
     * 
     * @param outcome the Outcome of the Decision
     * @throws WikiException if the act of restarting the Workflow throws an exception
     */
    @Override
	public void decide( final Outcome outcome ) throws WikiException {
        super.setOutcome( outcome );
		ApiActivator.getEventAdmin().sendEvent(new Event(WikiWorkflowEventTopic.TOPIC_WORKFLOW_DQ_REMOVAL,
				Map.of(WikiWorkflowEventTopic.PROPERTY_DECISION, this)));
    }

    /**
     * Default implementation that always returns {@link Outcome#STEP_CONTINUE} if the current Outcome isn't a completion (which will be
     * true if the {@link #decide(Outcome)} method hasn't been executed yet. This method will also add the Decision to the associated
     * DecisionQueue.
     * 
     * @return the Outcome of the execution
     * @throws WikiException never
     */
    @Override
    public Outcome execute() throws WikiException {
        if( getOutcome().isCompletion() ) {
            return getOutcome();
        }

        // Put decision in the DecisionQueue
		ApiActivator.getEventAdmin().sendEvent(new Event(WikiWorkflowEventTopic.TOPIC_WORKFLOW_DQ_ADDITION,
				Map.of(WikiWorkflowEventTopic.PROPERTY_DECISION, this)));

        // Indicate we are waiting for user input
        return Outcome.STEP_CONTINUE;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Principal getActor() {
		return m_actor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Principal getSubmitter() {
		return m_submitter;
	}

    /**
     * Returns the default or suggested outcome, which must be one of those returned by {@link #getAvailableOutcomes()}. This method is
     * guaranteed to return a non-<code>null</code> Outcome.
     * 
     * @return the default outcome.
     */
    @Override
	public Outcome getDefaultOutcome() {
        return m_defaultOutcome;
    }

    /**
     * Returns the Facts associated with this Decision, in the order in which they were added.
     * 
     * @return the list of Facts
     */
    @Override
	public final List< Fact > getFacts() {
        return Collections.unmodifiableList( m_facts );
    }

    /**
     * Returns the unique identifier for this Decision. Normally, this ID is programmatically assigned when the Decision is added to the
     * DecisionQueue.
     * 
     * @return the identifier
     */
    @Override
	public final int getId() {
        return m_id;
    }

    /**
     * Sets the unique identifier for this Decision.
     * 
     * @param id the identifier
     */
    @Override
	public final void setId( final int id ) {
        m_id = id;
    }

    /**
     * Returns <code>true</code> if the Decision can be reassigned to another actor. This implementation always returns <code>true</code>.
     *
     * @return the result
     */
    @Override
	public boolean isReassignable() {
        return true;
    }

    /**
     * Reassigns the Decision to a new actor (that is, provide an outcome). If the Decision is not reassignable, this method throws an
     * IllegalArgumentException.
     * 
     * @param actor the actor to reassign the Decision to
     */
    @Override
	public final synchronized void reassign( final Principal actor ) {
        if( isReassignable() ) {
            m_actor = actor;
        } else {
            throw new IllegalArgumentException( "Decision cannot be reassigned." );
        }
    }

}
