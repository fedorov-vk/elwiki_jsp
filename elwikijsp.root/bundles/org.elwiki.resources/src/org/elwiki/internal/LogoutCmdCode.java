package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.Command;
import org.apache.wiki.auth.AuthenticationManager;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki.authorize.login.CookieAuthenticationLoginModule;

public class LogoutCmdCode extends CmdCode {

	//private static final Logger log = Logger.getLogger(LogoutCmdCode.class);

	protected LogoutCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		AuthenticationManager mgr = getEngine().getManager(AuthenticationManager.class);
		mgr.logout(httpRequest);

		// Clear the user cookie
		CookieAssertionLoginModule.clearUserCookie(httpResponse);

		// Delete the login cookie
		CookieAuthenticationLoginModule.clearLoginCookie(getEngine(), httpRequest, httpResponse);

		// Redirect to the webroot
		// TODO: Should redirect to a "goodbye" -page?
		httpResponse.sendRedirect("/cmd.view");
	}

}
