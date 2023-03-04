package org.apache.wiki.api.event;

/**
 * <p>
 * Event class for security events: login/logout, wiki group adds/changes, and authorization
 * decisions. When a WikiSecurityEvent is constructed, the security logger {@link #log} is notified.
 * </p>
 * <p>
 * These events are logged with priority <code>ERROR</code>:
 * </p>
 * <ul>
 * <li>login failed - bad credential or password</li>
 * </ul>
 * <p>
 * These events are logged with priority <code>WARN</code>:
 * </p>
 * <ul>
 * <li>access denied</li>
 * <li>login failed - credential expired</li>
 * <li>login failed - account expired</li>
 * </ul>
 * <p>
 * These events are logged with priority <code>INFO</code>:
 * </p>
 * <ul>
 * <li>login succeeded</li>
 * <li>logout</li>
 * <li>user profile name changed</li>
 * </ul>
 * <p>
 * These events are logged with priority <code>DEBUG</code>:
 * </p>
 * <ul>
 * <li>access allowed</li>
 * <li>add group</li>
 * <li>remove group</li>
 * <li>clear all groups</li>
 * <li>add group member</li>
 * <li>remove group member</li>
 * <li>clear all members from group</li>
 * </ul>
 * 
 * @since 2.3.79
 */
public interface WikiSecurityEventTopic extends WikiEventTopic {

	/* == TOPIC_SECUR ==================================================== */

	String TOPIC_SECUR = TOPIC_BASE_ELWIKI + "/secur";

	String TOPIC_SECUR_ALL = TOPIC_SECUR + "/*";

    /** When a session expires. */
	String TOPIC_SECUR_SESSION_EXPIRED = TOPIC_SECUR + "/SESSION_EXPIRED";

    /** When a new wiki group is added. */
	String TOPIC_SECUR_GROUP_ADD = TOPIC_SECUR + "/GROUP_ADD";

    /** When a wiki group is deleted. */
	String TOPIC_SECUR_GROUP_REMOVE = TOPIC_SECUR + "/GROUP_REMOVE";

    //:FVK: пересмотреть, так как рефакторизованы Group-, User- Databases... 
    /** When all wiki groups are removed from GroupDatabase. -- :FVK: AccountRegistry? */
	String TOPIC_SECUR_GROUPS_CLEAR = TOPIC_SECUR + "/GROUPS_CLEAR";

    /** When access to a resource is allowed. */
	String TOPIC_SECUR_ACCESS_ALLOWED = TOPIC_SECUR + "/ACCESS_ALLOWED";

    /** When access to a resource is allowed. */
	String TOPIC_SECUR_ACCESS_DENIED = TOPIC_SECUR + "/ACCESS_DENIED";

    /** When a user profile is saved. */
	String TOPIC_SECUR_PROFILE_SAVE = TOPIC_SECUR + "/PROFILE_SAVE";

    /** When a user profile name changes. */
	String TOPIC_SECUR_PROFILE_NAME_CHANGED = TOPIC_SECUR + "/PROFILE_NAME_CHANGED";

	/* ====================================================================== */

	String PROPERTY_SESSION = "session";
	String PROPERTY_USER = "user";
	String PROPERTY_PERMISSION = "permission";
	String PROPERTY_PROFILE = "profile";
	String PROPERTY_PROFILES = "profiles";

}
