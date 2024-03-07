package org.elwiki.api.event;

/* NOTE: For identifiers, the "Et" prefix is an acronym of "EVENT TOPIC". */
public interface WikiEvent {

	interface Topic {
		String DOMAIN = "org/elwiki/events";

		/** All ElWiki events. */
		String ALL = DOMAIN + "/*";
	}

	/**
	 * Indicates the HttpSession identifier, for identification of the corresponding wiki-session for
	 * sending events.
	 */
	String PROPERTY_KEY_TARGET = "target";

	String PROPERTY_PRINCIPALS = "principal";

}
