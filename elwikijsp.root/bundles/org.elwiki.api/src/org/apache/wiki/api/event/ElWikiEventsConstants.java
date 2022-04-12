package org.apache.wiki.api.event;

public interface ElWikiEventsConstants {

	String TOPIC_BASE = "org/elwiki/events/";

	String TOPIC_LOGGING = "org/elwiki/events/logging/";
	String TOPIC_LOGGING_ALL = TOPIC_LOGGING + "*";
	String TOPIC_LOGIN_ANONYMOUS = TOPIC_LOGGING + "TOPIC_LOGIN_ANONYMOUS";
	String TOPIC_LOGIN_ASSERTED = TOPIC_LOGGING + "TOPIC_LOGIN_ASSERTED";
	String TOPIC_LOGIN_AUTHENTICATED = TOPIC_LOGGING + "TOPIC_LOGIN_AUTHENTICATED";
	String TOPIC_LOGOUT = TOPIC_LOGGING + "TOPIC_LOGOUT";

	String TOPIC_ALL = TOPIC_BASE + "*";

	/**
	 * Передает идентификатор HttpSession, для тождественной идентификации
	 * соответствующей wiki-сесии.
	 */
	String PROPERTY_KEY_TARGET = "target";
	String PROPERTY_LOGIN_PRINCIPALS = "principal";

}
