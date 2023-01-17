package org.elwiki.internal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.util.TextUtil;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.elwiki_data.*;
import org.apache.wiki.api.core.*;
import org.apache.wiki.api.exceptions.RedirectException;
import org.apache.wiki.Wiki;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.filters0.SpamFilter;
import org.apache.wiki.htmltowiki.HtmlStringToWikiTranslator;
import org.apache.wiki.pages0.PageLock;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.preferences.Preferences.TimeFormat;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki.services.ServicesRefs;

public class CommentCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(CommentCmdCode.class);

	protected CommentCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();

		// Create wiki context and check for authorization
		Context wikiContext = ContextUtil.findContext(request);
		Engine wiki = wikiContext.getEngine();
	    if( !ServicesRefs.getAuthorizationManager().hasAccess( wikiContext, response ) ) return;
	    if( wikiContext.getCommand().getTarget() == null ) {
	        response.sendRedirect( wikiContext.getURL( wikiContext.getRequestContext(), wikiContext.getName() ) );
	        return;
	    }
	    String pagereq = wikiContext.getName();

	    ResourceBundle rb = Preferences.getBundle( wikiContext, "CoreResources" );
	    Session wikiSession = wikiContext.getWikiSession();
	    String storedUser = wikiSession.getUserPrincipal().getName();

	    if( wikiSession.isAnonymous() ) {
	        storedUser  = TextUtil.replaceEntities( request.getParameter( "author" ) );
	    }

	    String ok      = request.getParameter("ok");
	    String preview = request.getParameter("preview");
	    String cancel  = request.getParameter("cancel");
	    String author  = TextUtil.replaceEntities( request.getParameter("author") );
	    String link    = TextUtil.replaceEntities( request.getParameter("link") );
	    String remember = TextUtil.replaceEntities( request.getParameter("remember") );
	    String changenote = TextUtil.replaceEntities( request.getParameter( "changenote" ) );

	    WikiPage wikipage = wikiContext.getPage();
	    WikiPage latestversion = ServicesRefs.getPageManager().getPage( pagereq );

	    session.removeAttribute( EditorManager.REQ_EDITEDTEXT );

	    if( latestversion == null ) {
	        latestversion = wikiContext.getPage();
	    }

	    //
	    //  Setup everything for the editors and possible preview.  We store everything in the session.
	    //

	    if( remember == null ) {
	        remember = (String)session.getAttribute("remember");
	    }

	    if( remember == null ) {
	        remember = "false";
	    } else {
	        remember = "true";
	    }

	    session.setAttribute("remember",remember);

	    if( author == null ) {
	        author = storedUser;
	    }
	    if( author == null || author.length() == 0 ) {
	        author = "AnonymousCoward";
	    }

	    session.setAttribute("author",author);

	    if( link == null ) {
	        link = HttpUtil.retrieveCookieValue( request, "link" );
	        if( link == null ) link = "";
	        link = TextUtil.urlDecodeUTF8(link);
	    }

	    session.setAttribute( "link", link );

	    if( changenote != null ) {
	       session.setAttribute( "changenote", changenote );
	    }

	    //
	    //  Branch
	    //
	    log.debug("preview="+preview+", ok="+ok);

	    if( ok != null ) {
	        log.info("Saving page "+pagereq+". User="+storedUser+", host="+HttpUtil.getRemoteAddress(request) );

	        //  Modifications are written here before actual saving

	        //:FVK: WikiPage modifiedPage = (WikiPage)wikiContext.getPage().clone();
	        WikiPage modifiedPage = wikiContext.getPage();

	        //  FIXME: I am not entirely sure if the JSP page is the
	        //  best place to check for concurrent changes.  It certainly
	        //  is the best place to show errors, though.

	        String spamhash = request.getParameter( SpamFilter.getHashFieldName(request) );

	        /*:FVK: workaround - commented
	        if( !SpamFilter.checkHash(wikiContext,pageContext) ) {
	            return;
	        }
	        */

	        //
	        //  We expire ALL locks at this moment, simply because someone has already broken it.
	        //
	        PageLock lock = ServicesRefs.getPageManager().getCurrentLock( wikipage );
	        ServicesRefs.getPageManager().unlockPage( lock );
	        session.removeAttribute( "lock-"+pagereq );

	        //
	        //  Set author and changenote information
	        //
	        //:FVK: modifiedPage.setAuthor( storedUser );

	        /* :FVK:
	        if( changenote != null ) {
	            modifiedPage.setAttribute( WikiPage.CHANGENOTE, changenote );
	        } else {
	            modifiedPage.removeAttribute( WikiPage.CHANGENOTE );
	        }
	        */

	        //
	        //  Build comment part
	        //
	        StringBuffer pageText = new StringBuffer( ServicesRefs.getPageManager().getPureText( wikipage ));

	        log.debug("Page initial contents are "+pageText.length()+" chars");

	        //
	        //  Add a line on top only if we need to separate it from the content.
	        //
	        if( pageText.length() > 0 ) {
	            pageText.append( "\n\n----\n\n" );
	        }

	        String commentText = request.getParameter(EditorManager.REQ_EDITEDTEXT); 
	        		//:FVK: workaround - commented: ContextUtil.getEditedText(pageContext);
	        //log.info("comment text"+commentText);

	        //
	        //  WYSIWYG editor sends us its greetings
	        //
	        String htmlText = (String) session.getAttribute("htmlPageText"); 
	        		//:FVK: workaround - commented: findParam( pageContext, "htmlPageText" );
	        if( htmlText != null && cancel == null ) {
	        	commentText = new HtmlStringToWikiTranslator().translate(htmlText,wikiContext);
	        }

	        pageText.append( commentText );

	        log.debug("Author name ="+author);
	        if( author != null && author.length() > 0 ) {
	            String signature = author;

	            if( link != null && link.length() > 0 ) {
	                link = HttpUtil.guessValidURI( link );
	                signature = "["+author+"|"+link+"]";
	            }

	            Calendar cal = Calendar.getInstance();
	            SimpleDateFormat fmt = Preferences.getDateFormat( wikiContext , TimeFormat.DATETIME);

	            pageText.append("\n\n%%signature\n"+signature+", "+fmt.format(cal.getTime())+"\n/%");

	        }

	        if( TextUtil.isPositive(remember) ) {
	            if( link != null ) {
	                Cookie linkcookie = new Cookie("link", TextUtil.urlEncodeUTF8(link) );
	                linkcookie.setMaxAge(1001*24*60*60);
	                response.addCookie( linkcookie );
	            }

	            CookieAssertionLoginModule.setUserCookie( response, author );
	        } else {
	            session.removeAttribute("link");
	            session.removeAttribute("author");
	        }

	        PageManager pageManager = ServicesRefs.getPageManager();
	        try {
	            wikiContext.setPage( modifiedPage );
	            pageManager.saveText( wikiContext, pageText.toString(), storedUser, "" ); //:FVK: workaround: changenote == ""
	        } catch( DecisionRequiredException e ) {
	        	String redirect = wikiContext.getURL( ContextEnum.PAGE_VIEW.getRequestContext(), "ApprovalRequiredForPageChanges" );
	            response.sendRedirect( redirect );
	            return;
	        } catch( RedirectException e ) {
	            session.setAttribute( VariableManager.VAR_MSG, e.getMessage() );
	            response.sendRedirect( e.getRedirect() );
	            return;
	        }
	        response.sendRedirect(wikiContext.getViewURL(pagereq));
	        return;
	    } else if( preview != null ) {
	        log.debug("Previewing "+pagereq);
	        String editedText = request.getParameter(EditorManager.REQ_EDITEDTEXT);
	        //session.setAttribute(EditorManager.REQ_EDITEDTEXT, ContextUtil.getEditedText(pageContext));
	        
	        response.sendRedirect( TextUtil.replaceString( wiki.getURL( ContextEnum.PAGE_PREVIEW.getRequestContext(), pagereq, "action=comment"),"&amp;","&") );
	        return;
	    } else if( cancel != null ) {
	        log.debug("Cancelled editing "+pagereq);
	        PageLock lock = (PageLock) session.getAttribute( "lock-"+pagereq );

	        if( lock != null ) {
	            ServicesRefs.getPageManager().unlockPage( lock );
	            session.removeAttribute( "lock-"+pagereq );
	        }
	        response.sendRedirect( wikiContext.getViewURL(pagereq) );
	        return;
	    }

	    log.info("Commenting page "+pagereq+". User="+request.getRemoteUser()+", host="+HttpUtil.getRemoteAddress(request) );

	    //
	    //  Determine and store the date the latest version was changed.  Since the newest version is the one that is changed,
	    //  we need to track that instead of the edited version.
	    //
	    long lastchange = 0;

	    Date d = latestversion.getLastModifiedDate();
	    if( d != null ) lastchange = d.getTime();

	    /*:FVK:
	    pageContext.setAttribute( "lastchange", Long.toString( lastchange ), PageContext.REQUEST_SCOPE );
	    */

	    //  This is a hack to get the preview to work.
	    // pageContext.setAttribute( "comment", Boolean.TRUE, PageContext.REQUEST_SCOPE );

	    //
	    //  Attempt to lock the page.
	    //
	    PageLock lock = ServicesRefs.getPageManager().lockPage( wikipage, storedUser );

	    if( lock != null ) {
	        session.setAttribute( "lock-"+pagereq, lock );
	    }

	    // Set the content type and include the response content
	    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
	    response.setHeader( "Cache-control", "max-age=0" );
	    response.setDateHeader( "Expires", new Date().getTime() );
	    response.setDateHeader( "Last-Modified", new Date().getTime() );
	}

}
