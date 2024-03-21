package org.elwiki.internal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.WikiContext.TimeFormat;
import org.apache.wiki.api.exceptions.RedirectException;
import org.apache.wiki.api.filters.ISpamFilter;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.htmltowiki.HtmlStringToWikiTranslator;
import org.apache.wiki.pages0.PageLock;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki_data.WikiPage;

public class CommentCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(CommentCmdCode.class);

	protected CommentCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);

		// Get wiki context and check for authorization.
		WikiContext wikiContext = getWikiContext();
		AuthorizationManager authorizationManager = getEngine().getManager(AuthorizationManager.class);
		authorizationManager.checkAccess(wikiContext, httpRequest, httpResponse);
		if (wikiContext.getCommand().getTarget() == null) {
			httpResponse.sendRedirect(wikiContext.getURL(wikiContext.getRequestContext(), wikiContext.getName()));
			return;
		}

		HttpSession httpSession = httpRequest.getSession();
		PageManager pageManager = getEngine().getManager(PageManager.class);
		Engine engine = wikiContext.getEngine();
		@NonNull
		FilterManager filterManager = engine.getManager(FilterManager.class);
		ISpamFilter spamFilter = filterManager.getSpamFilter();

		String pagereq = wikiContext.getName();

		WikiSession wikiSession = wikiContext.getWikiSession();
		String storedUser = wikiSession.getUserPrincipal().getName();
		if (wikiSession.isAnonymous()) {
			storedUser = TextUtil.replaceEntities(httpRequest.getParameter("author"));
		}
		String ok = httpRequest.getParameter("ok");
		String preview = httpRequest.getParameter("preview");
		String cancel = httpRequest.getParameter("cancel");
		String author = TextUtil.replaceEntities(httpRequest.getParameter("author"));
		String link = TextUtil.replaceEntities(httpRequest.getParameter("link"));
		String remember = TextUtil.replaceEntities(httpRequest.getParameter("remember"));
		String changenote = TextUtil.replaceEntities(httpRequest.getParameter("changenote"));

		WikiPage wikipage = wikiContext.getPage();
		WikiPage latestversion = pageManager.getPage(pagereq);

		httpSession.removeAttribute(EditorManager.REQ_EDITEDTEXT);

		if (latestversion == null) {
			latestversion = wikiContext.getPage();
		}

		//
		//  Setup everything for the editors and possible preview. We store everything in the session.
		//

		if (remember == null) {
			remember = (String) httpSession.getAttribute("remember");
		}

		if (remember == null) {
			remember = "false";
		} else {
			remember = "true";
		}

		httpSession.setAttribute("remember", remember);

		if (author == null) {
			author = storedUser;
		}
		if (author == null || author.length() == 0) {
			author = "AnonymousCoward";
		}

		httpSession.setAttribute("author", author);

		if (link == null) {
			link = HttpUtil.retrieveCookieValue(httpRequest, "link");
			if (link == null)
				link = "";
			link = TextUtil.urlDecodeUTF8(link);
		}

		httpSession.setAttribute("link", link);

		if (changenote != null) {
			httpSession.setAttribute("changenote", changenote);
		}

		//
		//  Branch
		//
		log.debug("preview=" + preview + ", ok=" + ok);

		if (ok != null) {
			log.info("Saving comment of page " + pagereq + ". User=" + storedUser + ", host="
					+ HttpUtil.getRemoteAddress(httpRequest));

			try {
				//  Modifications are written here before actual saving

				//:FVK: WikiPage modifiedPage = (WikiPage)wikiContext.getPage().clone();
				WikiPage modifiedPage = wikiContext.getPage();

				//  FIXME: I am not entirely sure if the JSP page is the
				//  best place to check for concurrent changes.  It certainly
				//  is the best place to show errors, though.

				String spamhash = httpRequest.getParameter(spamFilter.getHashFieldName(httpRequest));

				/*TODO: check on SpamFilter
				if( !spamFilter.checkHash(wikiContext, pageContext) ) {
				return;
				}*/

				//
				//  We expire ALL locks at this moment, simply because someone has already broken it.
				//
				PageLock lock = pageManager.getCurrentLock(wikipage);
				pageManager.unlockPage(lock);
				httpSession.removeAttribute("lock-" + pagereq);

				//
				//  Build comment part
				//
				String commentText = httpRequest.getParameter(EditorManager.REQ_EDITEDTEXT);
				//:FVK: workaround - commented: ContextUtil.getEditedText(pageContext);
				//log.info("comment text"+commentText);

				//
				//  WYSIWYG editor sends us its greetings
				//
				String htmlText = (String) httpSession.getAttribute("htmlPageText");
				//:FVK: workaround - commented: findParam( pageContext, "htmlPageText" );
				if (htmlText != null && cancel == null) {
					commentText = new HtmlStringToWikiTranslator().translate(htmlText, wikiContext);
				}

				StringBuffer allCommentText = new StringBuffer(commentText);

				log.debug("Author name =" + author);
				if (author != null && author.length() > 0) {
					String signature = author;

					if (link != null && link.length() > 0) {
						link = HttpUtil.guessValidURI(link);
						signature = "[" + author + "|" + link + "]";
					}

					Calendar cal = Calendar.getInstance();
					SimpleDateFormat fmt = Preferences.getDateFormat(wikiContext, TimeFormat.DATETIME);

					allCommentText.append("\n\n%%signature\n" + signature + ", " + fmt.format(cal.getTime()) + "\n/%");
				}

				var isRemember = BooleanUtils.toBooleanObject(remember);
				if (isRemember != null && isRemember) {
					if (link != null) {
						Cookie linkcookie = new Cookie("link", TextUtil.urlEncodeUTF8(link));
						linkcookie.setMaxAge(1001 * 24 * 60 * 60);
						httpResponse.addCookie(linkcookie);
					}

					CookieAssertionLoginModule.setUserCookie(httpResponse, author);
				} else {
					httpSession.removeAttribute("link");
					httpSession.removeAttribute("author");
				}

				try {
					wikiContext.setPage(modifiedPage);
					pageManager.savePageComment(wikiContext, allCommentText.toString());
				} catch (DecisionRequiredException e) {
					String redirect = wikiContext.getURL(ContextEnum.PAGE_VIEW.getRequestContext(),
							"ApprovalRequiredForPageChanges");
					httpResponse.sendRedirect(redirect);
					return;
				} catch (RedirectException e) {
					httpSession.setAttribute(VariableManager.VAR_MSG, e.getMessage());
					httpResponse.sendRedirect(e.getRedirect());
					return;
				}
				httpResponse.sendRedirect(wikiContext.getViewURL(wikiContext.getPage().getId()));
				return;
			} finally {
			}
		} else if (preview != null) {
			log.debug("Previewing " + pagereq);
			String editedText = httpRequest.getParameter(EditorManager.REQ_EDITEDTEXT);
			//session.setAttribute(EditorManager.REQ_EDITEDTEXT, ContextUtil.getEditedText(pageContext));

			httpResponse.sendRedirect(TextUtil.replaceString(
					engine.getURL(ContextEnum.PAGE_PREVIEW.getRequestContext(), pagereq, "action=comment"), "&amp;",
					"&"));
			return;
		} else if (cancel != null) {
			log.debug("Cancelled editing " + pagereq);
			PageLock lock = (PageLock) httpSession.getAttribute("lock-" + pagereq);

			if (lock != null) {
				pageManager.unlockPage(lock);
				httpSession.removeAttribute("lock-" + pagereq);
			}
			httpResponse.sendRedirect(wikiContext.getViewURL(wikiContext.getPage().getId()));
			return;
		}

		log.info("Commenting page " + pagereq + ". User=" + httpRequest.getRemoteUser() + ", host="
				+ HttpUtil.getRemoteAddress(httpRequest));

		//
		//  Determine and store the date the latest version was changed.  Since the newest version is the one that is changed,
		//  we need to track that instead of the edited version.
		//
		long lastchange = 0;

		Date d = latestversion.getLastModifiedDate();
		if (d != null)
			lastchange = d.getTime();

		/*:FVK:
		pageContext.setAttribute( "lastchange", Long.toString( lastchange ), PageContext.REQUEST_SCOPE );
		*/

		//  This is a hack to get the preview to work.
		// pageContext.setAttribute( "comment", Boolean.TRUE, PageContext.REQUEST_SCOPE );

		//
		//  Attempt to lock the page.
		//
		PageLock lock = pageManager.lockPage(wikipage, storedUser);

		if (lock != null) {
			httpSession.setAttribute("lock-" + pagereq, lock);
		}

		// Set the content type and include the response content
		//httpResponse.setContentType("text/html; charset="+engine.getContentEncoding() );
		httpResponse.setHeader("Cache-control", "max-age=0");
		httpResponse.setDateHeader("Expires", new Date().getTime());
		httpResponse.setDateHeader("Last-Modified", new Date().getTime());
	}

}
