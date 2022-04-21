package org.elwiki.internal;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Engine;

import org.apache.log4j.*;
import java.util.*;
import org.elwiki_data.*;
import org.apache.wiki.api.core.*;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.RedirectException;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.Wiki;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.UserManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user0.UserProfile;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.filters0.SpamFilter;
import org.apache.wiki.htmltowiki.HtmlStringToWikiTranslator;
import org.apache.wiki.pages0.PageLock;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki.services.ServicesRefs;


public class PrefsCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(PrefsCmdCode.class);

	public PrefsCmdCode(Command command) {
		super(command);
	}

	@Override
	public void applyPrologue(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Engine wiki = ServicesRefs.Instance;

	    // Create wiki context and check for authorization
	    Context wikiContext;
	    wikiContext = ServicesRefs.getCurrentContext();
	    /*:FVK:
	    // = Wiki.context().create( wiki, request, ContextEnum.WIKI_PREFS.getRequestContext() );
	    if(false == ServicesRefs.getAuthorizationManager().hasAccess( wikiContext, response ) ) return;
	    */

	    // Extract the user profile and action attributes
	    UserManager userMgr = ServicesRefs.getUserManager();
	    Session wikiSession = wikiContext.getWikiSession();

	/* FIXME: Obsolete
	    if( request.getParameter(EditorManager.PARA_EDITOR) != null )
	    {
	    	String editor = request.getParameter(EditorManager.PARA_EDITOR);
	    	session.setAttribute(EditorManager.PARA_EDITOR,editor);
	    }
	*/

	    // Are we saving the profile?
	    if( "saveProfile".equals(request.getParameter("action")) )
	    {
	        UserProfile profile = userMgr.parseProfile( wikiContext );
	         
	        // Validate the profile
	        userMgr.validateProfile( wikiContext, profile );

	        // If no errors, save the profile now & refresh the principal set!
	        if ( wikiSession.getMessages( "profile" ).length == 0 )
	        {
	            try
	            {
	                userMgr.setUserProfile( wikiSession, profile );
	                CookieAssertionLoginModule.setUserCookie( response, profile.getFullname() );
	            }
	            catch( DuplicateUserException due )
	            {
	                // User collision! (full name or wiki name already taken)
	                wikiSession.addMessage( "profile", ServicesRefs.getInternationalizationManager()
	                                                       .get( InternationalizationManager.CORE_BUNDLE,
	                                                    		 Preferences.getLocale( wikiContext ), 
	                                                             due.getMessage(), due.getArgs() ) );
	            }
	            catch( DecisionRequiredException e )
	            {
	                String redirect = wiki.getURL( ContextEnum.PAGE_VIEW.getRequestContext(), "ApprovalRequiredForUserProfiles", null );
	                response.sendRedirect( redirect );
	                return;
	            }
	            catch( WikiSecurityException e )
	            {
	                // Something went horribly wrong! Maybe it's an I/O error...
	                wikiSession.addMessage( "profile", e.getMessage() );
	            }
	        }
	        if ( wikiSession.getMessages( "profile" ).length == 0 )
	        {
	            String redirectPage = request.getParameter( "redirect" );

	            if( !ServicesRefs.getPageManager().wikiPageExists( redirectPage ) )
	            {
	               redirectPage = wiki.getWikiConfiguration().getFrontPage();
	            }
	            
	            String viewUrl = ( "UserPreferences".equals( redirectPage ) ) ? "Wiki.jsp" : wikiContext.getViewURL( redirectPage );
	            log.info( "Redirecting user to " + viewUrl );
	            response.sendRedirect( viewUrl );
	            return;
	        }
	    }
	    if( "setAssertedName".equals(request.getParameter("action")) )
	    {
	        //TODO: :FVK: Preferences.reloadPreferences(pageContext);
	        
	        String assertedName = request.getParameter("assertedName");
	        CookieAssertionLoginModule.setUserCookie( response, assertedName );

	        String redirectPage = request.getParameter( "redirect" );
	        if( !ServicesRefs.getPageManager().wikiPageExists( redirectPage ) )
	        {
	          redirectPage = wiki.getWikiConfiguration().getFrontPage();
	        }
	        String viewUrl = ( "UserPreferences".equals( redirectPage ) ) ? "Wiki.jsp" : wikiContext.getViewURL( redirectPage );

	        log.info( "Redirecting user to " + viewUrl );
	        response.sendRedirect( viewUrl );
	        return;
	    }
	    if( "clearAssertedName".equals(request.getParameter("action")) )
	    {
	        CookieAssertionLoginModule.clearUserCookie( response );
	        response.sendRedirect( wikiContext.getURL(ContextEnum.PAGE_NONE.getRequestContext(),"Logout.jsp") );
	        return;
	    }
	    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
	    //:FVK: String contentPage = ServicesRefs.getTemplateManager().findJSP( pageContext, wikiContext.getTemplate(), "ViewTemplate.jsp" );
	}

}
