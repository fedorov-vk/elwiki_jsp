package org.elwiki.authorize.internal.authorizer;

import java.security.Principal;
import java.util.Properties;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.api.authorization.Authorizer;

public class DefaultAuthorizer implements Authorizer {

	private @NonNull AccountManager accountManager;

	/**
	 * Creates instance of DefaultAuthorizer
	 * 
	 * @param engine
	 */
	public DefaultAuthorizer(Engine engine) {
		this.accountManager = engine.getManager(AccountManager.class);
	}

	@Override
	public Principal[] getRoles() {
		// TODO Auto-generated method stub
		return new Principal[0];
	}

	@Override
	public Principal findRole(String role) {
		Principal principal = this.accountManager.findRole(role);
		// --OLDCODE-- gp = new GroupPrincipal("Admin", uid);

		return principal;
	}

	@Override
	public void initialize(Engine engine, Properties props) throws WikiSecurityException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isUserInRole(WikiSession session, Principal role) {
		Assert.isTrue(false, "code is not implemented.");
		// TODO Auto-generated method stub
		return false;
	}

}
