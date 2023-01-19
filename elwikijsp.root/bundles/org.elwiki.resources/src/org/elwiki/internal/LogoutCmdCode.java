package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.Command;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.elwiki.authorize.login.CookieAssertionLoginModule;
import org.elwiki.authorize.login.CookieAuthenticationLoginModule;
import org.elwiki.services.ServicesRefs;

public class LogoutCmdCode extends CmdCode {

	//private static final Logger log = Logger.getLogger(LogoutCmdCode.class);

	protected LogoutCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		IIAuthenticationManager mgr = ServicesRefs.getAuthenticationManager();
		mgr.logout(httpRequest);

		// Clear the user cookie
		CookieAssertionLoginModule.clearUserCookie(httpResponse);

		// Delete the login cookie
		CookieAuthenticationLoginModule.clearLoginCookie(ServicesRefs.Instance, httpRequest, httpResponse);

		// Redirect to the webroot
		// TODO: Should redirect to a "goodbye" -page?
		httpResponse.sendRedirect("/cmd.view");
	}

}
