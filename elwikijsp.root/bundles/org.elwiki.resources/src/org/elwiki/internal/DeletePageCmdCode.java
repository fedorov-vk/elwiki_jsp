package org.elwiki.internal;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.pages0.PageManager;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki_data.WikiPage;

public class DeletePageCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(RenameCmdCode.class);

	protected DeletePageCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		Enumeration<String> paramsNames = httpRequest.getParameterNames();
		//String targetPageId = httpRequest.getParameter("redirect");
		//String action = httpRequest.getParameter("action");
		String pageId = (String) httpRequest.getParameter("pageId");
	    String delete = httpRequest.getParameter( "delete" );
	    String deleteall = httpRequest.getParameter( "delete-all" );

	    if( deleteall != null ) {
	    	/*
	        log.info("Deleting page "+pagereq+". User="+request.getRemoteUser()+", host="+HttpUtil.getRemoteAddress(request) );

	        wiki.getManager( PageManager.class ).deletePage( pagereq );

	        FixedQueue trail = (FixedQueue) session.getAttribute( BreadcrumbsTag.BREADCRUMBTRAIL_KEY );
	        if( trail != null )
	        {
	            trail.removeItem( pagereq );
	            session.setAttribute( BreadcrumbsTag.BREADCRUMBTRAIL_KEY, trail );
	        }

	        response.sendRedirect( TextUtil.replaceString( wiki.getURL( ContextEnum.PAGE_VIEW.getRequestContext(), redirTo, "tab="+request.getParameter("tab") ),"&amp;","&" ));
	        return;
	        */
	    } else if( delete != null ) {
	    	/*
	        log.info("Deleting a range of pages from "+pagereq);

	        for( Enumeration< String > params = request.getParameterNames(); params.hasMoreElements(); ) {
	            String paramName = params.nextElement();

	            if( paramName.startsWith("delver") ) {
	                int version = Integer.parseInt( paramName.substring(7) );

	                Page p = wiki.getManager( PageManager.class ).getPage( pagereq, version );

	                log.debug("Deleting version "+version);
	                wiki.getManager( PageManager.class ).deleteVersion( p );
	            }
	        }

	        response.sendRedirect(
	            TextUtil.replaceString( wiki.getURL( ContextEnum.PAGE_VIEW.getRequestContext(), redirTo, "tab=" + request.getParameter( "tab" ) ),"&amp;","&" )
	        );

	        return;
	        */
	    }

	    /*{
			Context m_wikiContext = (Context) httpRequest.getAttribute(Context.ATTR_WIKI_CONTEXT);
			final Engine engine = m_wikiContext.getEngine();
			@NonNull
			PageManager pageManager = engine.getManager(PageManager.class);
			pageManager.deletePageById();

			String redirectedUrl;
			redirectedUrl = "cmd.edit?pageId=" + newPage.getId();

			response.sendRedirect(redirectedUrl);
		}*/
	}

	public void applyEpilogue() {
		/*
		w.exitState();
		*/
	}

}
