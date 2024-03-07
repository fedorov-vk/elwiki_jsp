package org.elwiki.api.event;

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
public interface SecurityEvent extends WikiEvent {

	interface Topic {
		String DOMAIN = WikiEvent.Topic.DOMAIN + "/secur";
		String ALL = DOMAIN + "/*";

		/** When a session expires. */
		String SESSION_EXPIRED = DOMAIN + "/SESSION_EXPIRED";

		/** When a new wiki group is added. */
		String GROUP_ADD = DOMAIN + "/GROUP_ADD";

		/** When a wiki group is deleted. */
		String GROUP_REMOVE = DOMAIN + "/GROUP_REMOVE";

		// :FVK: пересмотреть, так как рефакторизованы Group-, User- Databases...
		/** When all wiki groups are removed from GroupDatabase. -- :FVK: AccountRegistry? */
		String GROUPS_CLEAR = DOMAIN + "/GROUPS_CLEAR";

		/** When access to a resource is allowed. */
		String ACCESS_ALLOWED = DOMAIN + "/ACCESS_ALLOWED";

		/** When access to a resource is allowed. */
		String ACCESS_DENIED = DOMAIN + "/ACCESS_DENIED";

		/** When a user profile is saved. */
		String PROFILE_SAVE = DOMAIN + "/PROFILE_SAVE";

		/** When a user profile name changes. */
		String PROFILE_NAME_CHANGED = DOMAIN + "/PROFILE_NAME_CHANGED";
	}

	String PROPERTY_SESSION = "session";
	String PROPERTY_USER = "user";
	String PROPERTY_PERMISSION = "permission";
	String PROPERTY_PROFILE = "profile";
	String PROPERTY_PROFILES = "profiles";

}
