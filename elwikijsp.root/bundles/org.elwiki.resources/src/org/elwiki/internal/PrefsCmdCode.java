package org.elwiki.internal;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

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
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki.services.ServicesRefs;

/**
 * @author v.fedorov
 * Code from webapp/UserPreferences.jsp (for ElWiki's PreferencesContent.jsp)
 */
public class PrefsCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(PrefsCmdCode.class);

	protected PrefsCmdCode() {
		super();
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		/*
		Enumeration<String> attrs = request.getAttributeNames();
		Enumeration<String> params = request.getParameterNames();
		Iterator<String> iter = attrs.asIterator();
		while(iter.hasNext()) {
			String param = iter.next();
			String value = request.getParameter(param);
			System.out.println("ATTR: " + param + " = " + value);
		}
		iter = params.asIterator();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = request.getParameter(param);
			System.out.println("PARAM: " + param + " = " + value);
		}
		*/

		// Get wiki context and check for authorization
		Context wikiContext = ContextUtil.findContext(httpRequest);
		Engine wiki = wikiContext.getEngine();
		/*:FVK:
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
		if ("saveProfile".equals(httpRequest.getParameter("action"))) {
			UserProfile profile = userMgr.parseProfile(wikiContext);

			// Validate the profile
			userMgr.validateProfile(wikiContext, profile);

			// If no errors, save the profile now & refresh the principal set!
			if (wikiSession.getMessages("profile").length == 0) {
				try {
					userMgr.setUserProfile(wikiSession, profile);
					CookieAssertionLoginModule.setUserCookie(response, profile.getFullname());
				} catch (DuplicateUserException due) {
					// User collision! (full name or wiki name already taken)
					@NonNull
					InternationalizationManager im = wiki.getManager(InternationalizationManager.class);
					String message = im.get(InternationalizationManager.CORE_BUNDLE, Preferences.getLocale(wikiContext),
							due.getMessage(), due.getArgs());
					wikiSession.addMessage("profile", message);
				} catch (DecisionRequiredException e) {
					String redirect = wiki.getURL(ContextEnum.PAGE_VIEW.getRequestContext(),
							"ApprovalRequiredForUserProfiles", null);
					response.sendRedirect(redirect);
					return;
				} catch (WikiSecurityException e) {
					// Something went horribly wrong! Maybe it's an I/O error...
					wikiSession.addMessage("profile", e.getMessage());
				}
			}
			if (wikiSession.getMessages("profile").length == 0) {
				String redirectPage = httpRequest.getParameter("redirect");
				PageManager pm = wiki.getManager(PageManager.class);
				if (!pm.pageExistsByName(redirectPage)) {
					redirectPage = wiki.getWikiConfiguration().getFrontPage();
				}

				String viewUrl = ("UserPreferences".equals(redirectPage)) ? "Wiki.jsp"
						: wikiContext.getViewURL(redirectPage);
				log.info("Redirecting user to " + viewUrl);
				response.sendRedirect(viewUrl);

				return;
			}
		}

		// Are we saving the preferences?
		if ("setAssertedName".equals(httpRequest.getParameter("action"))) {
			Preferences.reloadPreferences(httpRequest);

			String assertedName = httpRequest.getParameter("assertedName");
			CookieAssertionLoginModule.setUserCookie(response, assertedName);

			String redirectPage = httpRequest.getParameter("redirect");
			if (!ServicesRefs.getPageManager().pageExistsByName(redirectPage)) {
				redirectPage = wiki.getWikiConfiguration().getFrontPage();
			}
			String viewUrl = ("cmd.prefs".equals(redirectPage)) ? "cmd.view" : wikiContext.getViewURL(redirectPage);

			log.info("Redirecting user to " + viewUrl);
			response.sendRedirect(viewUrl);

			return;
		}

		// Are we remove profile information?
		if ("clearAssertedName".equals(httpRequest.getParameter("action"))) {
			CookieAssertionLoginModule.clearUserCookie(response);
			response.sendRedirect(wikiContext.getURL(ContextEnum.PAGE_NONE.getRequestContext(), "Logout.jsp"));

			return;
		}

		response.setContentType("text/html; charset=" + wiki.getContentEncoding());
		// :FVK: String contentPage = ServicesRefs.getTemplateManager().findJSP( pageContext,
		// wikiContext.getTemplate(), "ViewTemplate.jsp" );
	}

}
