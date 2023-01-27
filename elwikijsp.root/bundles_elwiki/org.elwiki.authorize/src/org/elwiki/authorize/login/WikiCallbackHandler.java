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
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.http.HttpServletRequest;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.auth.UserManager;
import org.elwiki.authorize.internal.services.DefaultUserManager;
import org.elwiki.services.ServicesRefs;


/**
 * Handles logins made from inside the wiki application, rather than via the web container. This
 * handler is instantiated in
 * {@link org.apache.wiki.auth.IIAuthenticationManager#login(HttpServletRequest, Session)}.
 * If container-managed authentication is used, the {@link WebContainerCallbackHandler}is used
 * instead. This callback handler is designed to be used with {@link UserDatabaseLoginModule}.
 * 
 * @since 2.3
 */
public class WikiCallbackHandler implements CallbackHandler {

	private final HttpServletRequest m_request;
	private final Engine m_engine;
	private final String m_password;
	private final String m_username;

	/**
	 * Create a new callback handler.
	 * 
	 * @param engine the Engine
	 * @param request
	 *            the user's HTTP request. If passed as <code>null</code>, later requests for
	 *            {@link HttpRequestCallback} will return an UnsupportedCallbackException
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public WikiCallbackHandler(Engine engine, HttpServletRequest request, String username, String password) {
		this.m_request = request;
		this.m_engine = engine;
		this.m_username = username;
		this.m_password = password;
	}

	/**
	 * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
	 * 
	 *      {@inheritDoc}
	 */
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof HttpRequestCallback httpRequestCallback) {
				httpRequestCallback.setRequest(this.m_request);
			} else if (callback instanceof WikiEngineCallback wikiEngineCallback) {
				wikiEngineCallback.setEngine(this.m_engine);
			} else if (callback instanceof UserDatabaseCallback userDatabaseCallback) {
				userDatabaseCallback.setUserDatabase(ServicesRefs.getUserManager().getUserDatabase() );
			} else if (callback instanceof NameCallback nameCallback) {
				nameCallback.setName(this.m_username);
			} else if (callback instanceof PasswordCallback passwordCallback) {
				passwordCallback.setPassword(this.m_password.toCharArray());
			} else {
				throw new UnsupportedCallbackException(callback);
			}
		}
	}
}
