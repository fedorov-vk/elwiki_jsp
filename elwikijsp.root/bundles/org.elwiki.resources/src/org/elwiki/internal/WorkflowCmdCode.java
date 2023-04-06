package org.elwiki.internal;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.workflow0.DecisionQueue;
import org.apache.wiki.workflow0.Decision;
import org.apache.wiki.workflow0.NoSuchOutcomeException;
import org.apache.wiki.workflow0.Outcome;
import org.apache.wiki.workflow0.Workflow;
import org.apache.wiki.workflow0.WorkflowManager;

public class WorkflowCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(WorkflowCmdCode.class);

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);

		AuthorizationManager authorizationManager = getEngine().getManager(AuthorizationManager.class);
		WorkflowManager workflowManager = getEngine().getManager(WorkflowManager.class);
		
		authorizationManager.checkAccess(wikiContext, httpRequest, httpResponse);
		// Extract the wiki session
		Session wikiSession = wikiContext.getWikiSession();

		// Get the current decisions
		DecisionQueue dq = workflowManager.getDecisionQueue();

		if ("decide".equals(httpRequest.getParameter("action"))) {
			try {
				// Extract parameters for decision ID & decision outcome
				int id = Integer.parseInt(httpRequest.getParameter("id"));
				String outcomeKey = httpRequest.getParameter("outcome");
				Outcome outcome = Outcome.forName(outcomeKey);
				// Iterate through our actor decisions and see if we can find an ID match
				Collection<Decision> decisions = dq.getActorDecisions(wikiSession);
				for (Iterator<Decision> it = decisions.iterator(); it.hasNext();) {
					Decision d = it.next();
					if (d.getId() == id) {
						// Cool, we found it. Now make the decision.
						dq.decide(d, outcome);
					}
				}
			} catch (NumberFormatException e) {
				log.warn("Could not parse integer from parameter 'decision'. Somebody is being naughty.");
			} catch (NoSuchOutcomeException e) {
				log.warn("Could not look up Outcome from parameter 'outcome'. Somebody is being naughty.");
			}
		}
		if ("abort".equals(httpRequest.getParameter("action"))) {
			try {
				// Extract parameters for decision ID & decision outcome
				int id = Integer.parseInt(httpRequest.getParameter("id"));
				// Iterate through our owner decisions and see if we can find an ID match
				Collection<Workflow> workflows = workflowManager.getOwnerWorkflows(wikiSession);
				for (Iterator<Workflow> it = workflows.iterator(); it.hasNext();) {
					Workflow w = it.next();
					if (w.getId() == id) {
						// Cool, we found it. Now kill the workflow.
						w.abort();
					}
				}
			} catch (NumberFormatException e) {
				log.warn("Could not parse integer from parameter 'decision'. Somebody is being naughty.");
			}
		}

		// Stash the current decisions/workflows
		Collection<Decision> workflows = dq.getActorDecisions(wikiSession);
		httpRequest.setAttribute("decisions", workflows);
		httpRequest.setAttribute("workflows", workflowManager.getOwnerWorkflows(wikiSession));
		httpRequest.setAttribute("wikiSession", wikiSession);

		/*:FVK:
		httpRequest.setContentType("text/html; charset="+wiki.getContentEncoding() );
		*/
	}

}
