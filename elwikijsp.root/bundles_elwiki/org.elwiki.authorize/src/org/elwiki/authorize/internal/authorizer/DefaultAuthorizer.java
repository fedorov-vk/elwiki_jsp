package org.elwiki.authorize.internal.authorizer;

import java.security.Principal;
import java.util.Properties;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.auth.WikiSecurityException;
import org.eclipse.core.runtime.Assert;
import org.elwiki.api.authorization.Authorizer;

public class DefaultAuthorizer implements Authorizer {

	@Override
	public Principal[] getRoles() {
		// TODO Auto-generated method stub
		return new Principal[0];
	}

	@Override
	public Principal findRole(String role) {
		Assert.isTrue(false, "code is not implemented.");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(Engine engine, Properties props) throws WikiSecurityException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isUserInRole(Session session, Principal role) {
		Assert.isTrue(false, "code is not implemented.");
		// TODO Auto-generated method stub
		return false;
	}

}
