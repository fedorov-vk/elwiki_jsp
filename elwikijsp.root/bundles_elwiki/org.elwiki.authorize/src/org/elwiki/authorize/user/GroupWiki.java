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
package org.elwiki.authorize.user;

import java.security.Principal;
import java.util.Date;
import java.util.Vector;

import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.data.authorize.GroupPrincipal;

/**
 * <p>
 * Groups are a specialized type of ad-hoc role used by the wiki system. Unlike externally-provided roles
 * (such as those provided by an LDAP server or web container), JSPWiki groups can be created dynamically by
 * wiki users, without requiring special container privileges or administrator intervention. They are designed
 * to provide a lightweight role-based access control system that complements existing role systems.
 * </p>
 * <p>
 * Group names are case-insensitive, and have a few naming restrictions, which are enforced by the
 * {@link GroupManager}:
 * </p>
 * <ul>
 * <li>Groups cannot have the same name as a built-in Role (e.g., "Admin", "Authenticated" etc.)</li>
 * <li>Groups cannot have the same name as an existing user</li>
 * </ul>
 * <p>
 * <em>Note: prior to JSPWiki 2.4.19, Group was an interface; it is now a concrete, final class.</em>
 * </p>
 * <p>
 * Groups are related to {@link GroupPrincipal}s. A GroupPrincipal, when injected into the Principal set of a
 * WikiSession's Subject, means that the user is a member of a Group of the same name -- it is, in essence, an
 * "authorization token." GroupPrincipals, unlike Groups, are thread-safe, lightweight and immutable. That's
 * why we use them in Subjects rather than the Groups themselves.
 * </p>
 */
public class GroupWiki implements IGroupWiki {

	public static final String[] RESTRICTED_GROUPNAMES = new String[] { "Anonymous", "All", "Asserted", "Authenticated" };

	private final Vector<Principal> m_members = new Vector<Principal>();

	private String m_creator = null;

	private Date m_created = null;

	private String m_modifier = null;

	private Date m_modified = null;

	private final String m_name;

	private final Principal m_principal;

	@Deprecated //:FVK:
	private final String m_wiki;

	/**
	 * Protected constructor to prevent direct instantiation except by other package members. Callers should
	 * use {@link GroupManager#parseGroup(IElWikiSession, String, String, boolean)} or
	 * {@link GroupManager#parseGroup(org.apache.wiki.WikiContext, boolean)}. instead.
	 * 
	 * @param name
	 *            the name of the group
	 * @param wiki
	 *            the wiki the group belongs to
	 */
	public GroupWiki(String name, String wiki) {
		this.m_name = name;
		this.m_wiki = wiki;
		this.m_principal = new GroupPrincipal(name);
	}

	/**
	 * Adds a Principal to the group.
	 * 
	 * @param user
	 *            the principal to add
	 * @return <code>true</code> if the operation was successful
	 */
	@Override
	public synchronized boolean add(Principal user) {
		if (isMember(user)) {
			return false;
		}

		this.m_members.add(user);
		return true;
	}

	/**
	 * Clears all Principals from the group list.
	 */
	@Override
	public synchronized void clear() {
		this.m_members.clear();
	}

	/**
	 * Two DefaultGroups are equal if they contain identical member Principals and have the same name.
	 * 
	 * @param o
	 *            the object to compare
	 * @return the comparison
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof GroupWiki))
			return false;

		GroupWiki g = (GroupWiki) o; // Just a shortcut.

		if (g.m_members.size() != this.m_members.size())
			return false;

		if (getName() != null && !getName().equals(g.getName())) {
			return false;
		} else if (getName() == null && g.getName() != null) {
			return false;
		}

		for (Principal principal : this.m_members) {
			if (!g.isMember(principal)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * The hashcode is calculated as a XOR sum over all members of the Group.
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		int hc = 0;
		for (Principal member : this.m_members) {
			hc ^= member.hashCode();
		}
		return hc;
	}

	/**
	 * Returns the creation date.
	 * 
	 * @return the creation date
	 */
	@Override
	public synchronized Date getCreated() {
		return this.m_created;
	}

	/**
	 * Returns the creator of this Group.
	 * 
	 * @return the creator
	 */
	@Override
	public final synchronized String getCreator() {
		return this.m_creator;
	}

	/**
	 * Returns the last-modified date.
	 * 
	 * @return the date and time of last modification
	 */
	@Override
	public synchronized Date getLastModified() {
		return this.m_modified;
	}

	/**
	 * Returns the name of the user who last modified this group.
	 * 
	 * @return the modifier
	 */
	@Override
	public final synchronized String getModifier() {
		return this.m_modifier;
	}

	/**
	 * The name of the group. This is set in the class constructor.
	 * 
	 * @return the name of the Group
	 */
	@Override
	public String getName() {
		return this.m_name;
	}

	/**
	 * Returns the GroupPrincipal that represents this Group.
	 * 
	 * @return the group principal
	 */
	@Override
	public Principal getPrincipal() {
		return this.m_principal;
	}

	/**
	 * Returns the wiki name.
	 * 
	 * @return the wiki name
	 */
	@Override
	public String getWiki() {
		return this.m_wiki;
	}

	/**
	 * Returns <code>true</code> if a Principal is a member of the group. Specifically, the Principal's
	 * <code>getName()</code> method must return the same value as one of the Principals in the group member
	 * list. The Principal's type does <em>not</em> need to match.
	 * 
	 * @param principal
	 *            the principal about whom membeship status is sought
	 * @return the result of the operation
	 */
	@Override
	public boolean isMember(Principal principal) {
		return findMember(principal.getName()) != null;
	}

	/**
	 * Returns the members of the group as an array of Principal objects.
	 * 
	 * @return the members
	 */
	@Override
	public Principal[] members() {
		return this.m_members.toArray(new Principal[this.m_members.size()]);
	}

	/**
	 * Removes a Principal from the group.
	 * 
	 * @param user
	 *            the principal to remove
	 * @return <code>true</code> if the operation was successful
	 */
	@Override
	public synchronized boolean remove(Principal user1) {
		Principal user = user1;
		user = findMember(user.getName());

		if (user == null)
			return false;

		this.m_members.remove(user);

		return true;
	}

	/**
	 * Sets the created date.
	 * 
	 * @param date
	 *            the creation date
	 */
	@Override
	public synchronized void setCreated(Date date) {
		this.m_created = date;
	}

	/**
	 * Sets the creator of this Group.
	 * 
	 * @param creator
	 *            the creator
	 */
	@Override
	public final synchronized void setCreator(String creator) {
		this.m_creator = creator;
	}

	/**
	 * Sets the last-modified date
	 * 
	 * @param date
	 *            the last-modified date
	 */
	@Override
	public synchronized void setLastModified(Date date) {
		this.m_modified = date;
	}

	/**
	 * Sets the name of the user who last modified this group.
	 * 
	 * @param modifier
	 *            the modifier
	 */
	@Override
	public final synchronized void setModifier(String modifier) {
		this.m_modifier = modifier;
	}

	/**
	 * Returns a string representation of the Group.
	 * 
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(Group " + getName() + ")");
		return sb.toString();
	}

	private Principal findMember(String name) {
		for (Principal member : this.m_members) {
			if (member.getName().equals(name)) {
				return member;
			}
		}

		return null;
	}
}
