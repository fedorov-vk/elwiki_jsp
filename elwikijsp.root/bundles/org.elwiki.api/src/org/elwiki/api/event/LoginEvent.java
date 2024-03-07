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
package org.elwiki.api.event;

public interface LoginEvent extends WikiEvent {

	interface Topic {
		String DOMAIN = WikiEvent.Topic.DOMAIN + "/logging";
		String ALL = DOMAIN + "/*";

		/** When a user's attempts to log in as guest, via cookies, using a password or otherwise. */
		String INITIATED = DOMAIN + "/INITIATED";

		/** When a user first accesses ElWiki, but before logging in or setting a cookie. */
		String ANONYMOUS = DOMAIN + "/LOGIN_ANONYMOUS";

		/** When a user sets a cookie to assert their identity. */
		String ASSERTED = DOMAIN + "/LOGIN_ASSERTED";

		/** When a user authenticates with a username and password, or via container auth. */
		String AUTHENTICATED = DOMAIN + "/LOGIN_AUTHENTICATED";

		/** When a login fails due to account expiration. */
		String ACCOUNT_EXPIRED = DOMAIN + "/ACCOUNT_EXPIRED";

		/** When a login fails due to credential expiration. */
		String CREDENTIAL_EXPIRED = DOMAIN + "/CREDENTIAL_EXPIRED";

		/** When a login fails due to wrong username or password. */
		String FAILED = DOMAIN + "/FAILED";

		/** When a user logs out. */
		String LOGOUT = DOMAIN + "/LOGOUT";

		/** When a Principals should be added to the Session */
		String PRINCIPALS_ADD = DOMAIN + "/PRINCIPALS_ADD";
	}

}
