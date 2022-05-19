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
package org.elwiki.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Arrays;

/**
 * <p>
 * Permission to perform an global wiki operation, such as self-registering or creating new pages.
 * Permission actions include: <code>createGroups</code>, <code>createPages</code>,
 * <code>editPreferences</code>, <code>editProfile</code> and <code>login</code>.
 * </p>
 * <p>
 * The target is a given wiki. The syntax for the target is the wiki name. "All wikis" can be
 * specified using a wildcard (*). Page collections may also be specified using a wildcard. For
 * pages, the wildcard may be a prefix, suffix, or all by itself.
 * <p>
 * Certain permissions imply others. Currently, <code>createGroups</code> implies
 * <code>createPages</code>.
 * </p>
 */
public final class WikiPermission extends APermission {

	private static final long serialVersionUID = -7293877464652228726L;

	/** Name of the action for createGroups permission. */
	public static final String CREATE_GROUPS_ACTION = "createGroups";

	/** Name of the action for createPages permission. */
	public static final String CREATE_PAGES_ACTION = "createPages";

	/** Name of the action for login permission. */
	public static final String LOGIN_ACTION = "login";

	/** Name of the action for editPreferences permission. */
	public static final String EDIT_PREFERENCES_ACTION = "editPreferences";

	/** Name of the action for editProfile permission. */
	public static final String EDIT_PROFILE_ACTION = "editProfile";

	/** Value for a generic wildcard. */
	public static final String WILDCARD = "*";

	protected static final int CREATE_GROUPS_MASK = 0x1;

	protected static final int CREATE_PAGES_MASK = 0x2;

	protected static final int EDIT_PREFERENCES_MASK = 0x4;

	protected static final int EDIT_PROFILE_MASK = 0x8;

	protected static final int LOGIN_MASK = 0x10;

	/** A static instance of the createGroups permission. */
	public static final WikiPermission CREATE_GROUPS = new WikiPermission(WILDCARD, CREATE_GROUPS_ACTION);

	/** A static instance of the createPages permission. */
	public static final WikiPermission CREATE_PAGES = new WikiPermission(WILDCARD, CREATE_PAGES_ACTION);

	/** A static instance of the login permission. */
	public static final WikiPermission LOGIN = new WikiPermission(WILDCARD, LOGIN_ACTION);

	/** A static instance of the editPreferences permission. */
	public static final WikiPermission EDIT_PREFERENCES = new WikiPermission(WILDCARD, EDIT_PREFERENCES_ACTION);

	/** A static instance of the editProfile permission. */
	public static final WikiPermission EDIT_PROFILE = new WikiPermission(WILDCARD, EDIT_PROFILE_ACTION);

	/**
	 * Creates a new WikiPermission for a specified set of actions.
	 * 
	 * @param actions
	 *            the actions for this permission
	 * @param wiki
	 *            The name of the wiki the permission belongs to.
	 */
	public WikiPermission(String wiki, String actions) {
		super(wiki);
		String[] pageActions = actions.toLowerCase().split(",");
		Arrays.sort(pageActions, String.CASE_INSENSITIVE_ORDER);
		setMask(createMask(actions));
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < pageActions.length; i++) {
			buffer.append(pageActions[i]);
			if (i < (pageActions.length - 1)) {
				buffer.append(",");
			}
		}
		setActions(buffer.toString());
		setWikiName((wiki == null) ? WILDCARD : wiki);
	}

	/**
	 * Two WikiPermission objects are considered equal if their wikis and actions (after normalization)
	 * are equal.
	 * 
	 * @param obj
	 *            the object to test
	 * @return the result
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WikiPermission)) {
			return false;
		}
		WikiPermission p = (WikiPermission) obj;
		return p.getMask() == this.getMask() && p.getWikiName() != null && p.getWikiName().equals(this.getWikiName());
	}

	/**
	 * WikiPermission can only imply other WikiPermissions; no other permission types are implied. One
	 * WikiPermission implies another if all of the other WikiPermission's actions are equal to, or a
	 * subset of, those for this permission.
	 * 
	 * @param permission
	 *            the permission which may (or may not) be implied by this instance
	 * @return <code>true</code> if the permission is implied, <code>false</code> otherwise
	 * @see java.security.Permission#implies(java.security.Permission)
	 */
	@Override
	public boolean implies(Permission permission) {
		// Permission must be a WikiPermission
		if (!(permission instanceof WikiPermission)) {
			return false;
		}
		WikiPermission p = (WikiPermission) permission;

		// See if the wiki is implied
		boolean impliedWiki = PagePermission.isSubset(this.getWikiName(), p.getWikiName());

		// Build up an "implied mask" for actions
		int impliedMask = impliedMask(this.getMask());

		// If actions aren't a proper subset, return false
		return impliedWiki && (impliedMask & p.getMask()) == p.getMask();
	}

	/**
	 * Returns a new {@link AllPermissionCollection}.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public PermissionCollection newPermissionCollection() {
		return new AllPermissionCollection();
	}

	/**
	 * Prints a human-readable representation of this permission.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public String toString() {
		// :FVK: return "(\"" + this.getClass().getName() + "\",\"" + this.m_wiki + "\",\"" + getActions() + "\")";
		String wiki = (this.getWikiName() == null) ? "" : this.getWikiName();
		return this.getClass().getName() + "|" + wiki + "|" + getActions();
	}

	/**
	 * Creates an "implied mask" based on the actions originally assigned: for example,
	 * <code>createGroups</code> implies <code>createPages</code>.
	 * 
	 * @param resultMask
	 *            the initial mask
	 * @return the implied mask
	 */
	protected static int impliedMask(int mask1) {
		int resultMask = mask1;
		if ((resultMask & CREATE_GROUPS_MASK) > 0) {
			resultMask |= CREATE_PAGES_MASK;
		}
		return resultMask;
	}

	/**
	 * Private method that creates a binary mask based on the actions specified. This is used by
	 * {@link #implies(Permission)}.
	 * 
	 * @param actions
	 *            the permission actions, separated by commas
	 * @return binary mask representing the permissions
	 */
	protected static int createMask(String actions) {
		if (actions == null || actions.length() == 0) {
			throw new IllegalArgumentException("Actions cannot be blank or null");
		}
		int mask = 0;
		String[] actionList = actions.split(",");
		for (int i = 0; i < actionList.length; i++) {
			String action = actionList[i];
			if (action.equalsIgnoreCase(CREATE_GROUPS_ACTION)) {
				mask |= CREATE_GROUPS_MASK;
			} else if (action.equalsIgnoreCase(CREATE_PAGES_ACTION)) {
				mask |= CREATE_PAGES_MASK;
			} else if (action.equalsIgnoreCase(LOGIN_ACTION)) {
				mask |= LOGIN_MASK;
			} else if (action.equalsIgnoreCase(EDIT_PREFERENCES_ACTION)) {
				mask |= EDIT_PREFERENCES_MASK;
			} else if (action.equalsIgnoreCase(EDIT_PROFILE_ACTION)) {
				mask |= EDIT_PROFILE_MASK;
			} else {
				throw new IllegalArgumentException("Unrecognized action: " + action);
			}
		}
		return mask;
	}
}
