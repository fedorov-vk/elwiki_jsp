package org.elwiki.internal;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.RedirectException;
import org.apache.wiki.api.filters.ISpamFilter;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.htmltowiki.HtmlStringToWikiTranslator;
import org.apache.wiki.pages0.PageLock;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki_data.WikiPage;

public class EditCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(EditCmdCode.class);

	/**
	 * Creates an instance of EditCmdCode.
	 * 
	 * @param command
	 */
	protected EditCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);

		// Get wiki context and check for authorization.
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
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
		String user = wikiSession.getUserPrincipal().getName();
		String action = httpRequest.getParameter("action");
		String ok = httpRequest.getParameter("ok");
		String preview = httpRequest.getParameter("preview");
		String cancel = httpRequest.getParameter("cancel");
		String append = httpRequest.getParameter("append");
		String edit = httpRequest.getParameter("edit");

		String author = (String) httpSession.getAttribute("author");
		author = httpRequest.getParameter("author");
		//TextUtil.replaceEntities( findParam( pageContext, "author" ) );

		String changenote = (String) httpSession.getAttribute("changenote");
		changenote = httpRequest.getParameter("changenote");
		//findParam( pageContext, "changenote" );

		String text = httpRequest.getParameter(EditorManager.REQ_EDITEDTEXT);
		//ContextUtil.getEditedText( pageContext );
		String link = (String) httpSession.getAttribute("link");
		link = httpRequest.getParameter("link");
		//TextUtil.replaceEntities( findParam( pageContext, "link") );

		String spamhash = (String) httpSession.getAttribute(spamFilter.getHashFieldName(httpRequest));
		//findParam( pageContext, SpamFilter.getHashFieldName(httpRequest) );
		String captcha = (String) httpSession.getAttribute("captcha");
		captcha = httpRequest.getParameter("captcha");

		if (!wikiSession.isAuthenticated() && wikiSession.isAnonymous() && author != null) {
			/*:FVK: user  = TextUtil.replaceEntities( findParam( pageContext, "author" ) ); */
			user = TextUtil.replaceEntities(author);
		}

		//
		//  WYSIWYG editor sends us its greetings
		//
		String htmlText = (String) httpSession.getAttribute("\"htmlPageText\"");
		//findParam( pageContext, "htmlPageText" );
		if (htmlText != null && cancel == null) {
			text = new HtmlStringToWikiTranslator().translate(htmlText, wikiContext);
		}

		WikiPage wikipage = wikiContext.getPage();
		WikiPage latestversion = pageManager.getPage(pagereq);

		if (latestversion == null) {
			latestversion = wikiContext.getPage();
		}

		//
		//  Set the response type before we branch.
		//

		httpResponse.setContentType("text/html; charset=" + engine.getContentEncoding());
		httpResponse.setHeader("Cache-control", "max-age=0");
		httpResponse.setDateHeader("Expires", new Date().getTime());
		httpResponse.setDateHeader("Last-Modified", new Date().getTime());

		//log.debug("Request character encoding="+request.getCharacterEncoding());
		//log.debug("Request content type+"+request.getContentType());
		log.debug("preview=" + preview + ", ok=" + ok);

		if (ok != null || captcha != null) {
			log.info("Saving page " + pagereq + ". User=" + user + ", host=" + HttpUtil.getRemoteAddress(httpRequest));

			//
			//  Check for session expiry
			//
			/*TODO: check on SpamFilter
			 if( !SpamFilter.checkHash(wikiContext, pageContext) ) {
			    return;
			}
			*/

			try {
				// FIXME: I am not entirely sure if the JSP page is the
				//  best place to check for concurrent changes.
				// It certainly is the best place to show errors, though.

				String h = spamFilter.getSpamHash(latestversion, httpRequest);

				/*:FVK:
				if (!h.equals(spamhash)) {
					//
					// Someone changed the page while we were editing it!
					//
				
					log.info("Page changed, warning user.");
				
					session.setAttribute(EditorManager.REQ_EDITEDTEXT, ContextUtil.getEditedText(pageContext));
					response.sendRedirect(wiki.getURL(ContextEnum.PAGE_CONFLICT.getRequestContext(), pagereq, null));
					return;
				}
				*/

				//
				// We expire ALL locks at this moment, simply because someone has already broken it.
				//
				PageLock lock = pageManager.getCurrentLock(wikipage);
				pageManager.unlockPage(lock);
				httpSession.removeAttribute("lock-" + pagereq);

				if (changenote == null) {
					changenote = (String) httpSession.getAttribute("changenote"); // TODO: :FVK: replace string.
				}
				httpSession.removeAttribute("changenote");

				//
				// Figure out the actual page text.
				//
				if (text == null) {
					throw new ServletException("No parameter text set!");
				}

				//
				// If this is an append, then we just append it to the page.
				// If it is a full edit, then we will replace the previous contents.
				//
				try {
					if (captcha != null) {
						wikiContext.setVariable("captcha", Boolean.TRUE); // TODO: :FVK: replace string.
						httpSession.removeAttribute("captcha");
					}

					if (append != null) {
						StringBuffer pageText = new StringBuffer(pageManager.getText(pagereq));
						pageText.append(text);
						pageManager.saveText(wikiContext, pageText.toString(), user, changenote);
					} else {
						pageManager.saveText(wikiContext, text, user, changenote);
					}
				} catch (DecisionRequiredException ex) {
					String redirect = wikiContext.getURL(ContextEnum.PAGE_VIEW.getRequestContext(),
							"ApprovalRequiredForPageChanges");
					httpResponse.sendRedirect(redirect);
					return;
				} catch (RedirectException ex) {
					// FIXME: Cut-n-paste code.
					wikiContext.getWikiSession().addMessage(ex.getMessage()); // FIXME: should work, but doesn't
					httpSession.setAttribute("message", ex.getMessage());
					/*:FVK:
					session.setAttribute(EditorManager.REQ_EDITEDTEXT, ContextUtil.getEditedText(pageContext));
					*/
					httpSession.setAttribute(EditorManager.REQ_EDITEDTEXT, "ContextUtil.getEditedText(pageContext)");

					httpSession.setAttribute("author", user);
					httpSession.setAttribute("link", link != null ? link : "");
					if (htmlText != null)
						httpSession.setAttribute(EditorManager.REQ_EDITEDTEXT, text);

					httpSession.setAttribute("changenote", changenote != null ? changenote : "");
					httpSession.setAttribute(spamFilter.getHashFieldName(httpRequest), spamhash);
					httpResponse.sendRedirect(ex.getRedirect());
					return;
				}

				httpResponse.sendRedirect(wikiContext.getViewURL(wikiContext.getPage().getId()));
				return;
			} finally {
			}
		} else if (preview != null) {
			log.debug("Previewing " + pagereq);
			/*:FVK:
			session.setAttribute(EditorManager.REQ_EDITEDTEXT, ContextUtil.getEditedText(pageContext));
			*/
			httpSession.setAttribute(EditorManager.REQ_EDITEDTEXT, "ContextUtil.getEditedText(pageContext)");

			httpSession.setAttribute("author", user);
			httpSession.setAttribute("link", link != null ? link : "");

			if (htmlText != null) {
				httpSession.setAttribute(EditorManager.REQ_EDITEDTEXT, text);
			}

			httpSession.setAttribute("changenote", changenote != null ? changenote : "");
			httpResponse.sendRedirect(engine.getURL(ContextEnum.PAGE_PREVIEW.getRequestContext(), pagereq, null));
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

		httpSession.removeAttribute(EditorManager.REQ_EDITEDTEXT);

		log.info("Editing page " + pagereq + ". User=" + user + ", host=" + HttpUtil.getRemoteAddress(httpRequest));

		//
		//  Determine and store the date the latest version was changed.  Since
		//  the newest version is the one that is changed, we need to track
		//  that instead of the edited version.
		//
		String lastchange = spamFilter.getSpamHash(latestversion, httpRequest);

		/*:FVK:
		pageContext.setAttribute("lastchange", lastchange, PageContext.REQUEST_SCOPE);
		*/

		//
		//  Attempt to lock the page.
		//
		PageLock lock = pageManager.lockPage(wikipage, user);
		if (lock != null) {
			httpSession.setAttribute("lock-" + pagereq, lock);
		}
	}

	String findParam(PageContext ctx, String key) {
		ServletRequest req = ctx.getRequest();
		String val = req.getParameter(key);
		if (val == null) {
			val = (String) ctx.findAttribute(key);
		}

		return val;
	}

}
