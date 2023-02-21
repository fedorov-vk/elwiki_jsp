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

import javax.security.auth.callback.Callback;

import org.apache.wiki.auth.AccountRegistry;

/**
 * Callback for requesting and supplying a wiki AccountRegistry. This callback is used by LoginModules that need
 * access to a user database for looking up users by id.
 * 
 * @since 2.3
 */
public class AccountRegistryCallback implements Callback {

	private AccountRegistry accountRegistry;

	/**
	 * Returns the user database object. LoginModules call this method after a CallbackHandler sets the user
	 * database.
	 * 
	 * @return the user database
	 */
	public AccountRegistry getAccountRegistry() {
		return this.accountRegistry;
	}

	/**
	 * Sets the user database. CallbackHandler objects call this method..
	 * 
	 * @param accountRegistry
	 *            the user database
	 */
	public void setAccountRegistry(AccountRegistry accountRegistry) {
		this.accountRegistry = accountRegistry;
	}

}
