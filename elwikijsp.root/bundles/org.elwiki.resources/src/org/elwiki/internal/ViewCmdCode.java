package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.wiki.WatchDog;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextUtil;
import org.elwiki.services.ServicesRefs;

public class ViewCmdCode extends CmdCode {

	protected ViewCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws Exception {
		Context wikiContext = ContextUtil.findContext(httpRequest);
		String pagereq = wikiContext.getName();

	    // Redirect if request was for a special page
	    String redirect = wikiContext.getRedirectURL();
	    if( redirect != null )
	    {
	        httpResponse.sendRedirect( redirect );
	        return;
	    }
/*
	    StopWatch sw = new StopWatch();
	    sw.start();
	    WatchDog w = WatchDog.getCurrentWatchDog( ServicesRefs.Instance );
	    try {
	        w.enterState("Generating VIEW response for "+wikiContext.getPage(), 600); //:FVK: =60

	        // Set the content type and include the response content
	        response.setContentType("text/html; charset="+ServicesRefs.Instance.getContentEncoding() );
	        String contentPage = ServicesRefs.getTemplateManager()
	        		.findJSP( pageContext, wikiContext.getShape(), "ViewTemplate.jsp" );
	    }
	    finally {
		}
*/
	}

	public void applyEpilogue() {
		/*
        sw.stop();
        if( log.isDebugEnabled() ) {
        	log.debug("Total response time from server on page "+pagereq+": "+sw);
        }
        w.exitState();
        */
	}

}
