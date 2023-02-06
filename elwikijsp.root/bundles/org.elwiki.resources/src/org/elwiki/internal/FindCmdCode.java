package org.elwiki.internal;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.api.search.SearchResult;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.util.TextUtil;
import org.elwiki.api.authorization.IGroupManager;
import org.elwiki.api.authorization.WrapGroup;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;
import org.osgi.service.useradmin.Group;

/**
 * 
 * @author vfedorov
 * Code from Search.jsp (for ElWiki's FindContent.jsp)
 */
public class FindCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(FindCmdCode.class);
	
	protected FindCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		Engine wiki = ServicesRefs.Instance;

		// Get wiki context and check for authorization
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		if (!ServicesRefs.getAuthorizationManager().hasAccess(wikiContext, httpResponse)) {
			return;
		}

	    // Get the search results
	    Collection< SearchResult > list = null;
	    String query = httpRequest.getParameter( "query");
	    String go    = httpRequest.getParameter("go");

	    if( query != null ) {
	        log.info("Searching for string "+query);

	        try {
	            list = ServicesRefs.getSearchManager().findPages( query, wikiContext );
	            httpRequest.setAttribute( "searchresults", list); //, PageContext.REQUEST_SCOPE );
	        } catch( Exception e ) {
	            wikiContext.getWikiSession().addMessage( e.getMessage() );
	        }

	        query = TextUtil.replaceEntities( query );

	        httpRequest.setAttribute( "query", query); //, PageContext.REQUEST_SCOPE );

	        //
	        //  Did the user click on "go"?
	        //
	        if( go != null ) {
	            if( list != null && list.size() > 0 ) {
	                SearchResult sr = list.iterator().next();
	                WikiPage wikiPage = sr.getPage();
	                String url = wikiContext.getViewURL( wikiPage.getName() );
	                httpResponse.sendRedirect( url );
	                return;
	            }
	        }
	    }
	}

}
