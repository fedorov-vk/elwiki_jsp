package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.wiki.WatchDog;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextUtil;
import org.elwiki.services.ServicesRefs;

public class InfoCmdCode extends CmdCode {

	protected InfoCmdCode() {
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

/////////
		/*
		WatchDog w = WatchDog.getCurrentWatchDog(ServicesRefs.Instance);
		try {
			w.enterState("Generating INFO response", 60);

			// Set the content type and include the response content
			response.setContentType("text/html; charset=" + ServicesRefs.Instance.getContentEncoding());
		} finally {
		}
		*/
	}

	public void applyEpilogue() {
		/*
        w.exitState();
        */
	}
	
}
