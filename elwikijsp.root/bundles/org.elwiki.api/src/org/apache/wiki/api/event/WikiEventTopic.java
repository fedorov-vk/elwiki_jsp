package org.apache.wiki.api.event;

public interface WikiEventTopic {

	String TOPIC_BASE_ELWIKI = "org/elwiki/events";

	/** All ElWiki events. */
	String TOPIC_ALL = TOPIC_BASE_ELWIKI + "/*";
	
	/* ====================================================================== */

	/**
	 * Indicates the HttpSession identifier, for identification of the corresponding wiki-session for
	 * sending events.
	 */
	String PROPERTY_KEY_TARGET = "target";

	String PROPERTY_PRINCIPALS = "principal";

}
