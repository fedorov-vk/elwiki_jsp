package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextUtil;

public class CreatePageCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(RenameCmdCode.class);

	protected CreatePageCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		Context wikiContext = ContextUtil.findContext(httpRequest);
		/*
		if( !ServicesRefs.getAuthorizationManager().hasAccess( wikiContext, response ) ) return;
		if( wikiContext.getCommand().getTarget() == null ) {
		    response.sendRedirect( wikiContext.getURL( wikiContext.getRequestContext(), wikiContext.getName() ) );
		    return;
		}
		*/
		String pagereq = wikiContext.getName();

		// Are we set selected scope?
		if ("createpage".equals(httpRequest.getParameter("action"))) {
			log.debug("create page.");
		} else if("createeditpage".equals(httpRequest.getParameter("action"))) {
			log.debug("create & edit page.");
		}
	}

	public void applyEpilogue() {
		/*
		w.exitState();
		*/
	}

}
