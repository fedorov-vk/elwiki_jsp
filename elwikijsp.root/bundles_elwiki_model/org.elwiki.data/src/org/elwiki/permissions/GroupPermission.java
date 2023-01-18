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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;

/**
 * <p>
 * Permission to perform an operation on a group in a given wiki. Permission
 * actions include: <code>view</code>, <code>edit</code>, <code>delete</code>.
 * </p>
 * <p>
 * The target of a permission is a single group or collection in a given wiki.
 * The syntax for the target is the wiki name, followed by a colon (:) and the
 * name of the group. &#8220;All wikis&#8221; can be specified using a wildcard
 * (*). Group collections may also be specified using a wildcard. For groups,
 * the wildcard may be a prefix, suffix, or all by itself. Examples of targets
 * include:
 * </p>
 * <blockquote><code>*:*<br/>
 * *:TestPlanners<br/>
 * *:*Planners<br/>
 * *:Test*<br/>
 * mywiki:TestPlanners<br/>
 * mywiki:*Planners<br/>
 * mywiki:Test*</code> </blockquote>
 * <p>
 * For a given target, certain permissions imply others:
 * </p>
 * <ul>
 * <li><code>edit</code>&nbsp;implies&nbsp;<code>view</code></li>
 * <li><code>delete</code>&nbsp;implies&nbsp;<code>edit</code> and
 * <code>view</code></li>
 * </ul>
 * <P>
 * Targets that do not include a wiki prefix <em>never </em> imply others.
 * </p>
 * <p>
 * GroupPermission accepts a special target called
 * <code>&lt;groupmember&gt;</code> that means &#8220;all groups that a user is
 * a member of.&#8221; When included in a policy file <code>grant</code> block,
 * it functions like a wildcard. Thus, this block:
 *
 * <pre>
 *  grant signedBy &quot;jspwiki&quot;,
 *    principal org.elwiki.auth.authorize.Role &quot;Authenticated&quot; {
 *      permission org.elwiki.auth.permissions.GroupPermission &quot;*:&lt;groupmember&gt;&quot;, &quot;edit&quot;;
 * </pre>
 *
 * means, &#8220;allow Authenticated users to edit any groups they are members
 * of.&#8221; The wildcard target (*) does <em>not</em> imply
 * <code>&lt;groupmember&gt;</code>; it must be granted explicitly.
 */
public final class GroupPermission extends Apermission {

	private static final long serialVersionUID = 347330660713847609L;
	private static final Logger log = Logger.getLogger(GroupPermission.class);

	/**
	 * Special target token that denotes all groups that a Subject's Principals are
	 * members of.
	 */
	public static final String MEMBER_TOKEN = "<groupmember>";

	/** Action for deleting a group or collection of groups. */
	public static final String DELETE_ACTION = "delete";

	/** Action for editing a group or collection of groups. */
	public static final String EDIT_ACTION = "edit";

	/** Action for viewing a group or collection of groups. */
	public static final String VIEW_ACTION = "view";

	protected static final int DELETE_MASK = 0x4;

	protected static final int EDIT_MASK = 0x2;

	protected static final int VIEW_MASK = 0x1;

	/**
	 * Convenience constant that denotes
	 * <code>GroupPermission( "*:*, "delete" )</code>.
	 */
	public static final GroupPermission DELETE = new GroupPermission(DELETE_ACTION);

	/**
	 * Convenience constant that denotes
	 * <code>GroupPermission( "*:*, "edit" )</code>.
	 */
	public static final GroupPermission EDIT = new GroupPermission(EDIT_ACTION);

	/**
	 * Convenience constant that denotes
	 * <code>GroupPermission( "*:*, "view" )</code>.
	 */
	public static final GroupPermission VIEW = new GroupPermission(VIEW_ACTION);

	private final String m_group;

	/** For serialization purposes */
	protected GroupPermission() {
		this("");
	}

	/**
	 * Private convenience constructor that creates a new GroupPermission for all
	 * wikis and groups (*:*) and set of actions.
	 * 
	 * @param actions
	 */
	private GroupPermission(String actions) {
		this(WILDCARD + WIKI_SEPARATOR + WILDCARD, actions);
	}

	/**
	 * Creates a new GroupPermission for a specified group and set of actions. Group
	 * should include a prepended wiki name followed by a colon (:). If the wiki
	 * name is not supplied or starts with a colon, the group refers to all wikis.
	 * 
	 * @param group   the wiki group
	 * @param actions the allowed actions for this group
	 */
	public GroupPermission(String group, String actions) {
		super(group);

		// Parse wiki and group (which may include wiki name and group)
		// Strip out attachment separator; it is irrelevant.
		String[] pathParams = group.split(WIKI_SEPARATOR);
		String groupName;
		if (pathParams.length >= 2) {
			setWikiName(pathParams[0]);
			groupName = pathParams[1];
		} else {
			setWikiName(WILDCARD);
			groupName = pathParams[0];
		}
		this.m_group = groupName;

		parseActions(actions);
	}

	/**
	 * Two PagePermission objects are considered equal if their actions (after
	 * normalization), wiki and target are equal.
	 * 
	 * @param obj the object to compare
	 * @return the result of the comparison
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GroupPermission)) {
			return false;
		}
		GroupPermission p = (GroupPermission) obj;
		return p.getMask() == this.getMask() && p.m_group.equals(this.m_group) && p.getWikiName() != null
				&& p.getWikiName().equals(this.getWikiName());
	}

	/**
	 * Returns the name of the wiki group represented by this permission.
	 * 
	 * @return the page name
	 */
	public String getGroup() {
		return this.m_group;
	}

	/**
	 * <p>
	 * GroupPermissions can only imply other GroupPermissions; no other permission
	 * types are implied. One GroupPermission implies another if its actions if
	 * three conditions are met:
	 * </p>
	 * <ol>
	 * <li>The other GroupPermission&#8217;s wiki is equal to, or a subset of, that
	 * of this permission. This permission&#8217;s wiki is considered a superset of
	 * the other if it contains a matching prefix plus a wildcard, or a wildcard
	 * followed by a matching suffix.</li>
	 * <li>The other GroupPermission&#8217;s target is equal to, or a subset of, the
	 * target specified by this permission. This permission&#8217;s target is
	 * considered a superset of the other if it contains a matching prefix plus a
	 * wildcard, or a wildcard followed by a matching suffix.</li>
	 * <li>All of other GroupPermission&#8217;s actions are equal to, or a subset
	 * of, those of this permission</li>
	 * </ol>
	 * 
	 * @param permission the Permission to examine
	 * @return <code>true</code> if the GroupPermission implies the supplied
	 *         Permission; <code>false</code> otherwise
	 * @see java.security.Permission#implies(java.security.Permission)
	 */
	@Override
	public boolean implies(Permission permission) {
		// Permission must be a GroupPermission
		if (!(permission instanceof GroupPermission)) {
			return false;
		}

		// Build up an "implied mask"
		GroupPermission p = (GroupPermission) permission;
		int impliedMask = impliedMask(this.getMask());

		// If actions aren't a proper subset, return false
		if ((impliedMask & p.getMask()) != p.getMask()) {
			return false;
		}

		// See if the tested permission's wiki is implied
		boolean impliedWiki = isSubset(this.getWikiName(), p.getWikiName());

		// If this group is "*", the tested permission's group is implied,
		// unless implied permission has <groupmember> token
		boolean impliedGroup;
		if (MEMBER_TOKEN.equals(p.m_group)) {
			impliedGroup = MEMBER_TOKEN.equals(this.m_group);
		} else {
			impliedGroup = isSubset(this.m_group, p.m_group);
		}

		// See if this permission is <groupmember> and Subject possesses
		// User matching the implied GroupPermission's group.
		boolean isImpliedMember = MEMBER_TOKEN.equals(this.m_group)
				&& Platform.getAdapterManager().getAdapter(permission, Boolean.class);

		return impliedWiki && (impliedGroup || isImpliedMember);
	}

	/**
	 * Prints a human-readable representation of this permission.
	 * 
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String wiki = (this.getWikiName() == null) ? "" : this.getWikiName();
		// :FVK: original code: return "(\"" + this.getClass().getName() + "\",\"" + wiki + WIKI_SEPARATOR + this.m_group + "\",\"" + getActions() + "\")";
		return this.getClass().getName() + "|" + wiki + WIKI_SEPARATOR + this.m_group + "|" + getActions();
	}

	/**
	 * Creates an &#8220;implied mask&#8221; based on the actions originally
	 * assigned: for example, delete implies edit; edit implies view.
	 * 
	 * @param mask binary mask for actions
	 * @return binary mask for implied actions
	 */
	protected static int impliedMask(int mask) {
		int resultMask = mask;

		if ((resultMask & DELETE_MASK) > 0) {
			resultMask |= EDIT_MASK;
		}
		if ((resultMask & EDIT_MASK) > 0) {
			resultMask |= VIEW_MASK;
		}
		return resultMask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int createMask(String[] actions) {
		if (actions == null || actions.length == 0) {
			throw new IllegalArgumentException("Actions cannot be blank or null");
		}
		int mask = 0;
		for (String action : actions) {
			switch (action.toLowerCase()) {
			case VIEW_ACTION:
				mask |= VIEW_MASK;
				break;
			case EDIT_ACTION:
				mask |= EDIT_MASK;
				break;
			case DELETE_ACTION:
				mask |= DELETE_MASK;
				break;
			default:
				throw new IllegalArgumentException("Unrecognized action: " + action);
			}
		}
		return mask;
	}

}
