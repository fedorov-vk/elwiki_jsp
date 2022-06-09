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

	public LogoutCmdCode(Command command) {
		super(command);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		IIAuthenticationManager mgr = ServicesRefs.getAuthenticationManager();
		mgr.logout(httpRequest);

		// Clear the user cookie
		CookieAssertionLoginModule.clearUserCookie(response);

		// Delete the login cookie
		CookieAuthenticationLoginModule.clearLoginCookie(ServicesRefs.Instance, httpRequest, response);

		// Redirect to the webroot
		// TODO: Should redirect to a "goodbye" -page?
		response.sendRedirect("/cmd.view");
	}

}
