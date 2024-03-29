/*
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.    
 */
package org.elwiki.authorize.login;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.UserProfile;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.data.authorize.WikiPrincipal;

/**
 * <p>
 * Logs in a user based on a username, password, and static password file location. This module must be used
 * with a CallbackHandler (such as {@link WikiCallbackHandler}) that supports the following Callback types:
 * </p>
 * <ol>
 * <li>{@link javax.security.auth.callback.NameCallback}- supplies the username</li>
 * <li>{@link javax.security.auth.callback.PasswordCallback}- supplies the password</li>
 * <li>{@link org.elwiki.authorize.login.AccountRegistryCallback.auth.login.wiki.auth.login.UserDatabaseCallback}- supplies the
 * {@link AccountRegistry}</li>
 * </ol>
 * <p>
 * After authentication, a Principals based on the login name will be created and associated with the Subject.
 * </p>
 * 
 * @since 2.3
 */
public class AccountRegistryLoginModule extends AbstractLoginModule {

	private static final Logger log = Logger.getLogger(AccountRegistryLoginModule.class);

	/**
	 * @see javax.security.auth.spi.LoginModule#login()
	 * 
	 *      {@inheritDoc}
	 */
	@Override
	public boolean login() throws LoginException {
		AccountRegistryCallback ucb = new AccountRegistryCallback();
		NameCallback ncb = new NameCallback("User name");
		PasswordCallback pcb = new PasswordCallback("Password", false);
		Callback[] callbacks = new Callback[] { ucb, ncb, pcb };
		try {
			this.m_handler.handle(callbacks);
			AccountRegistry accountRegistry = ucb.getAccountRegistry();
			String username = ncb.getName();
			String password = new String(pcb.getPassword());

			// Look up the user and compare the password hash
			if (accountRegistry == null) {
				throw new FailedLoginException("No user database: check the callback handler code!");
			}
			UserProfile profile = accountRegistry.findByLoginName(username);
			String storedPassword = profile.getPassword();
			if (storedPassword.length() != 0 && accountRegistry.validatePassword(profile, password)) {
				if (log.isDebugEnabled()) {
					log.debug("Logged in user database user " + username);
				}

				// If login succeeds, commit these principals/roles
				this.m_principals.add(new WikiPrincipal(profile.getUid(), WikiPrincipal.LOGIN_UID));

				return true;
			}
			throw new FailedLoginException("The username or password is incorrect.");
		} catch (IOException e) {
			String message = "IO exception; disallowing login.";
			log.error(message, e);
			throw new LoginException(message);
		} catch (UnsupportedCallbackException e) {
			String message = "Unable to handle callback; disallowing login.";
			log.error(message, e);
			throw new LoginException(message);
		} catch (NoSuchPrincipalException e) {
			throw new FailedLoginException("The username or password is incorrect.");
		}
	}
}
