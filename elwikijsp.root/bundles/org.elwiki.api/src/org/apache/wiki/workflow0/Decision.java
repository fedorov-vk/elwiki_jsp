package org.apache.wiki.workflow0;

import java.security.Principal;
import java.util.List;

import org.apache.wiki.api.exceptions.WikiException;

public interface Decision extends Step {

	/**
	 * Appends a Fact to the list of Facts associated with this Decision.
	 * 
	 * @param fact the new fact to add
	 */
	void addFact(Fact fact);

	/**
	 * <p>
	 * Sets this Decision's outcome, and restarts the parent Workflow if it is in the
	 * {@link Workflow#WAITING} state and this Decision is its currently active Step. Any checked
	 * WikiExceptions thrown by the workflow after re-start will be re-thrown to callers.
	 * </p>
	 * <p>
	 * This method cannot be invoked if the Decision is not the current Workflow step; all other
	 * invocations will throw an IllegalStateException. If the Outcome supplied to this method is one
	 * one of the Outcomes returned by {@link #getAvailableOutcomes()}, an IllegalArgumentException will
	 * be thrown.
	 * </p>
	 * 
	 * @param outcome the Outcome of the Decision
	 * @throws WikiException if the act of restarting the Workflow throws an exception
	 */
	void decide(Outcome outcome) throws WikiException;

	/**
	 * {@inheritDoc}
	 */
	Principal getSubmitter();

	/**
	 * Returns the default or suggested outcome, which must be one of those returned by
	 * {@link #getAvailableOutcomes()}. This method is guaranteed to return a non-<code>null</code>
	 * Outcome.
	 * 
	 * @return the default outcome.
	 */
	Outcome getDefaultOutcome();

	/**
	 * Returns the Facts associated with this Decision, in the order in which they were added.
	 * 
	 * @return the list of Facts
	 */
	List<Fact> getFacts();

	/**
	 * Returns the unique identifier for this Decision. Normally, this ID is programmatically assigned
	 * when the Decision is added to the DecisionQueue.
	 * 
	 * @return the identifier
	 */
	int getId();

	/**
	 * Sets the unique identifier for this Decision.
	 * 
	 * @param id the identifier
	 */
	void setId(int id);

	/**
	 * Returns <code>true</code> if the Decision can be reassigned to another actor.
	 *
	 * @return the result
	 */
	boolean isReassignable();

	/**
	 * Reassigns the Decision to a new actor (that is, provide an outcome). If the Decision is not
	 * reassignable, this method throws an IllegalArgumentException.
	 * 
	 * @param actor the actor to reassign the Decision to
	 */
	void reassign(Principal actor);

}