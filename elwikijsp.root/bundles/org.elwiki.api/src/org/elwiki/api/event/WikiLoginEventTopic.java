package org.elwiki.api.event;

public interface WikiLoginEventTopic extends WikiEventTopic {

	/* == LOGGING =========================================================== */

	String TOPIC_LOGGING = TOPIC_BASE_ELWIKI + "/logging";

	String TOPIC_LOGGING_ALL = TOPIC_LOGGING + "/*";

	/** When a user's attempts to log in as guest, via cookies, using a password or otherwise. */
	String TOPIC_LOGIN_INITIATED = TOPIC_LOGGING + "/INITIATED";

	/** When a login fails due to account expiration. */
	String TOPIC_LOGIN_ACCOUNT_EXPIRED = TOPIC_LOGGING + "/ACCOUNT_EXPIRED";

	/** When a login fails due to credential expiration. */
	String TOPIC_LOGIN_CREDENTIAL_EXPIRED = TOPIC_LOGGING + "/CREDENTIAL_EXPIRED";

	/** When a login fails due to wrong username or password. */
	String TOPIC_LOGIN_FAILED = TOPIC_LOGGING + "/FAILED";

	/** When a Principals should be added to the Session */
	String TOPIC_PRINCIPALS_ADD = TOPIC_LOGGING + "/PRINCIPALS_ADD";

	/** When a user first accesses ElWiki, but before logging in or setting a cookie. */
	String TOPIC_LOGIN_ANONYMOUS = TOPIC_LOGGING + "/LOGIN_ANONYMOUS";
	
	/** When a user sets a cookie to assert their identity. */
	String TOPIC_LOGIN_ASSERTED = TOPIC_LOGGING + "/LOGIN_ASSERTED";
	
	/** When a user authenticates with a username and password, or via container auth. */
	String TOPIC_LOGIN_AUTHENTICATED = TOPIC_LOGGING + "/LOGIN_AUTHENTICATED";

	/** When a user logs out. */
	String TOPIC_LOGOUT = TOPIC_LOGGING + "/LOGOUT";

}
