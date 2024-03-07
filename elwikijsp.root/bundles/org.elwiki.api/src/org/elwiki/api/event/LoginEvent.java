package org.elwiki.api.event;

public interface LoginEvent extends WikiEvent {

	interface Topic {
		String DOMAIN = WikiEvent.Topic.DOMAIN + "/logging";
		String ALL = DOMAIN + "/*";

		/** When a user's attempts to log in as guest, via cookies, using a password or otherwise. */
		String INITIATED = DOMAIN + "/INITIATED";

		/** When a login fails due to account expiration. */
		String ACCOUNT_EXPIRED = DOMAIN + "/ACCOUNT_EXPIRED";

		/** When a login fails due to credential expiration. */
		String CREDENTIAL_EXPIRED = DOMAIN + "/CREDENTIAL_EXPIRED";

		/** When a login fails due to wrong username or password. */
		String FAILED = DOMAIN + "/FAILED";

		/** When a Principals should be added to the Session */
		String PRINCIPALS_ADD = DOMAIN + "/PRINCIPALS_ADD";

		/** When a user first accesses ElWiki, but before logging in or setting a cookie. */
		String ANONYMOUS = DOMAIN + "/LOGIN_ANONYMOUS";

		/** When a user sets a cookie to assert their identity. */
		String ASSERTED = DOMAIN + "/LOGIN_ASSERTED";

		/** When a user authenticates with a username and password, or via container auth. */
		String AUTHENTICATED = DOMAIN + "/LOGIN_AUTHENTICATED";

		/** When a user logs out. */
		String LOGOUT = DOMAIN + "/LOGOUT";
	}

}
