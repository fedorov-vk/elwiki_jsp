package org.apache.wiki.workflow0;

import java.security.Principal;
import java.util.List;

import org.apache.wiki.api.exceptions.WikiException;

public interface IWorkflowBuilder {

	/**
	 * <p>
	 * Builds an approval workflow that requests approval from a named user,
	 * {@link org.elwiki.api.authorization.IGroupWiki} or
	 * {@link org.elwiki.data.authorize.GroupPrincipal} before running a Task.
	 * </p>
	 * <p>
	 * The Principal who approves the activity is determined by looking up the property
	 * <code>jspwiki.approver.<var>workflowApproverKey</var></code> in <code>jspwiki.properties</code>.
	 * If that Principal resolves to a known user, Group Role, a Decision will be placed in the
	 * respective workflow queue (or multiple queues, if necessary). Only one approver needs to make the
	 * Decision, and if the request is approved, the completion task will be executed. If the request is
	 * denied, a {@link SimpleNotification} with a message corresponding to the
	 * <code>rejectedMessage</code> message key will be placed in the submitter's workflow queue.
	 * </p>
	 * <p>
	 * To help approvers determine how to make the Decision, callers can supply an array of Fact objects
	 * to this method, which will be added to the Decision in the order they appear in the array. These
	 * items will be displayed in the web UI. In addition, the value of the first Fact will also be
	 * added as the third message argument for the workflow (the first two are always the submitter and
	 * the approver). For example, the PageManager code that creates the "save page approval" workflow
	 * adds the name of the page as its first Fact; this results in the page name being substituted
	 * correctly into the resulting message: "Save wiki page &lt;strong&gt;{2}&lt;/strong&gt;".
	 * </p>
	 * 
	 * @param submitter           the user submitting the request
	 * @param workflowApproverKey the key that names the user, Group or Role who must approve the
	 *                            request. The role by this key is looked up in section of wiki
	 *                            configuration, which is presented by <code>getApprovers</code> method.
	 * @param prepTask            the initial task that should run before the Decision step is
	 *                            processed. If this parameter is <code>null</code>, the Decision will
	 *                            run as the first Step instead
	 * @param decisionKey         the message key in <code>default.properties</code> that contains the
	 *                            text that will appear in approvers' workflow queues indicating they
	 *                            need to make a Decision; for example,
	 *                            <code>wf.decision.saveWikiPage</code>. In the i18n message bundle
	 *                            file, this key might return text that reads "Approve page
	 *                            &lt;strong&gt;{2}&lt;/strong&gt;"
	 * @param facts               an array of {@link Fact} objects that will be shown to the approver to
	 *                            aid decision-making. The facts will be displayed in the order supplied
	 *                            in the array
	 * @param completionTask      the Task that will run if the Decision is approved
	 * @param rejectedMessageKey  the message key in <code>default.properties</code> that contains the
	 *                            text that will appear in the submitter's workflow queue if request was
	 *                            not approved; for example,
	 *                            <code>wf.notification.saveWikiPage.reject</code>. In the i18n message
	 *                            bundle file, this key might might return text that reads "Your request
	 *                            to save page &lt;strong&gt;{2}&lt;/strong&gt; was rejected." If this
	 *                            parameter is <code>null</code>, no message will be sent
	 * @return the created workflow
	 * @throws WikiException if the name of the approving user, Role or Group cannot be determined
	 */
	Workflow buildApprovalWorkflow(Principal submitter, String workflowApproverKey, Step prepTask, String decisionKey,
			List<Fact> facts, Step completionTask, String rejectedMessageKey) throws WikiException;

}
