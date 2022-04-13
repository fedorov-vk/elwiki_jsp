package org.elwiki.internal;

import java.security.Principal;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.WikiContext;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.apache.wiki.auth.UserManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user0.UserProfile;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki.authorize.login.CookieAuthenticationLoginModule;
import org.elwiki.services.ServicesRefs;

public class LoginCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(LoginCmdCode.class);

	public LoginCmdCode(Command command) {
		super(command);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		Context wikiContext = ServicesRefs.getCurrentContext();
		Wiki.context().create(ServicesRefs.Instance, httpRequest, ContextEnum.WIKI_LOGIN.getRequestContext());
		// :FVK: ?? надо ли указывать PageContext.REQUEST_SCOPE
		// httpRequest.setAttribute( Context.ATTR_CONTEXT, wikiContext, PageContext.REQUEST_SCOPE );
		Session wikiSession = wikiContext.getWikiSession();
		ResourceBundle rb = Preferences.getBundle(wikiContext, "CoreResources"); // :FVK: это ресурсы в заданном
																					// каталоге...

		// Set the redirect-page variable if one was passed as a parameter
		String requestRedirect = (httpRequest.getParameter("redirect") != null) ? //
				httpRequest.getParameter("redirect") : wikiContext.getConfiguration().getFrontPage();
		wikiContext.setVariable("redirect", requestRedirect);

		// Are we saving the profile?
		if ("saveProfile".equals(httpRequest.getParameter("action"))) {
			UserManager userMgr = ServicesRefs.getUserManager();
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
					wikiSession.addMessage("profile",
							ServicesRefs.getInternationalizationManager().get(InternationalizationManager.CORE_BUNDLE,
									Preferences.getLocale(wikiContext), due.getMessage(), due.getArgs()));
				} catch (DecisionRequiredException e) {
					String redirect = ServicesRefs.Instance.getURL(ContextEnum.PAGE_VIEW.getRequestContext(),
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
				response.sendRedirect(wikiContext.getViewURL(redirectPage));
				return;
			}
		}

		IIAuthenticationManager mgr = ServicesRefs.getAuthenticationManager();
		// If NOT using container auth, perform all of the access control logic here...
		// (Note: if using the container for auth, it will handle all of this for us.)
		if (!mgr.isContainerAuthenticated()) {
			// If user got here and is already authenticated, it means they just aren't allowed
			// access to what they asked for. Weepy tears and hankies all 'round.
			if (wikiSession.isAuthenticated()) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, rb.getString("login.error.noaccess"));
				return;
			}

			// If using custom auth, we need to do the login now
			@SuppressWarnings("unused")
			String action = httpRequest.getParameter("action");
			if (httpRequest.getParameter("submitlogin") != null) {
				String uid = httpRequest.getParameter("j_username");
				String passwd = httpRequest.getParameter("j_password");
				log.debug("Attempting to authenticate user " + uid);

				// Log the user in!
				if (mgr.loginAsserted(wikiSession, httpRequest, uid, passwd)) {
					log.info("Successfully authenticated user " + uid + " (custom auth)");
				} else {
					log.info("Failed to authenticate user " + uid);
					wikiSession.addMessage("login", rb.getString("login.error.password"));
				}
			}
		} else {
			HttpSession session = httpRequest.getSession();
			//
			// Have we already been submitted? If yes, then we can assume that we have been logged in
			// before.
			//
			Object seen = session.getAttribute("_redirect");
			if (seen != null) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, rb.getString("login.error.noaccess"));
				session.removeAttribute("_redirect");
				return;
			}
			session.setAttribute("_redirect", "I love Outi"); // Just any marker will do

			// If using container auth, the container will have automatically
			// attempted to log in the user before Login.jsp was loaded.
			// Thus, if we got here, the container must have authenticated
			// the user already. All we do is simply record that fact.
			// Nice and easy.

			Principal user = wikiSession.getLoginPrincipal();
			log.info("Successfully authenticated user " + user.getName() + " (container auth)");
		}

		// If user logged in, set the user cookie with the wiki principal's name.
		// redirect to wherever we're supposed to go. If login.jsp
		// was called without parameters, this will be the front page. Otherwise,
		// there's probably a 'redirect' parameter telling us where to go.

		if (wikiSession.isAuthenticated()) {
			String rember = httpRequest.getParameter("j_remember");

			// Set user cookie
			Principal principal = wikiSession.getUserPrincipal();
			CookieAssertionLoginModule.setUserCookie(response, principal.getName());

			if (rember != null) {
				CookieAuthenticationLoginModule.setLoginCookie(ServicesRefs.Instance, response, principal.getName());
			}

			// If wiki page was "Login", redirect to main, otherwise use the page supplied
			String redirectPage = httpRequest.getParameter("redirect");
			if (!ServicesRefs.getPageManager().wikiPageExists(redirectPage)) {
				redirectPage = wikiContext.getConfiguration().getFrontPage();
			}
			String viewUrl = ("Login".equals(redirectPage)) ? "Wiki.jsp" : wikiContext.getViewURL(redirectPage);

			// Redirect!
			log.info("Redirecting user to " + viewUrl);
			response.sendRedirect(viewUrl);
			return;
		}
	}

}
