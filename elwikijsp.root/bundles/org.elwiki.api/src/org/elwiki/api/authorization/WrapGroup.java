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
package org.elwiki.api.authorization;

import org.apache.wiki.api.internal.ApiActivator;
import org.apache.wiki.auth.user0.UserDatabase;
import org.elwiki.data.authorize.GroupPrincipal;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Vector;

/**
 * <p>
 * Groups are a specialized type of ad-hoc role used by the wiki system. Unlike
 * externally-provided roles (such as those provided by an LDAP server or web container),
 * JSPWiki groups can be created dynamically by wiki users, without requiring special container
 * privileges or administrator intervention. They are designed to provide a lightweight
 * role-based access control system that complements existing role systems.
 * </p>
 * <p>
 * Group names are case-insensitive, and have a few naming restrictions, which are enforced by
 * the {@link GroupManager}:
 * </p>
 * <ul>
 * <li>Groups cannot have the same name as a built-in Role (e.g., "Admin", "Authenticated"
 * etc.)</li>
 * <li>Groups cannot have the same name as an existing user</li>
 * </ul>
 * <p>
 * <em>Note: prior to JSPWiki 2.4.19, Group was an interface; it is now a concrete, final
 * class.</em>
 * </p>
 * <p>
 * Groups are related to {@link GroupPrincipal}s. A GroupPrincipal, when injected into the
 * Principal set of a Session's Subject, means that the user is a member of a Group of the same
 * name -- it is, in essence, an "authorization token." GroupPrincipals, unlike Groups, are
 * thread-safe, lightweight and immutable. That's why we use them in Subjects rather than the
 * Groups themselves.
 * </p>
 *
 * @since 2.3
 */
public class WrapGroup {

	static final String[] RESTRICTED_GROUPNAMES = new String[] { "Anonymous", "All", "Asserted", "Authenticated" };

	private final Vector<Principal> m_members = new Vector<>();

	private String m_creator = null;

	private Date m_created = null;

	private String m_modifier = null;

	private Date m_modified = null;

	private final String m_name;

	private final Principal m_principal;

	private final String m_wiki;

	private Group group;

	private UserAdmin userAdmin;

	/**
	 * Protected constructor to prevent direct instantiation except by other package members.
	 * Callers should use {@link GroupManager#parseGroup(String, String, boolean)} or
	 * {@link GroupManager#parseGroup(org.apache.wiki.WikiContext, boolean)}. instead.
	 * 
	 * @param name the name of the group
	 * @param wiki the wiki the group belongs to
	 */
	public WrapGroup(final Group group) {
		this.group = group;
		Dictionary<String, Object> properties = group.getProperties();
		m_name = (String) properties.get(UserDatabase.GROUP_NAME);
		m_wiki = "foo_wiki"; // :FVK:
		m_principal = new GroupPrincipal(m_name); // :FVK: удалить... (что это?)

		this.userAdmin = ApiActivator.getService(UserAdmin.class);
	}

	/**
	 * Adds a Principal to the group.
	 * 
	 * @param user the principal to add
	 * @return <code>true</code> if the operation was successful
	 */
	public synchronized boolean add(final Principal user) {
		if (isMember(user)) {
			return false;
		}

		m_members.add(user);
		return true;
	}

	/**
	 * Clears all Principals from the group list.
	 */
	public synchronized void clear() {
		m_members.clear();
	}

	/**
	 * Two DefaultGroups are equal if they contain identical member Principals and have the same
	 * name.
	 * 
	 * @param o the object to compare
	 * @return the comparison
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof WrapGroup g)) {
			return false;
		}

		if (g.m_members.size() != m_members.size()) {
			return false;
		}

		if (getName() != null && !getName().equals(g.getName())) {
			return false;
		} else if (getName() == null && g.getName() != null) {
			return false;
		}

		for (final Principal principal : m_members) {
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
		for (final Principal member : m_members) {
			hc ^= member.hashCode();
		}
		return hc;
	}

	/**
	 * Returns the creation date.
	 *
	 * @return the creation date
	 */
	public synchronized Date getCreated() {
		Dictionary<String, Object> properties = this.group.getProperties();
		String created = (String) properties.get(UserDatabase.CREATED);

		return convert2date(created);
	}

	protected Date convert2date(String parsedDate) {
		DateFormat c_format = new SimpleDateFormat(UserDatabase.DATE_FORMAT);
		Date date;
		try {
			date = c_format.parse(parsedDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			date = new Date(System.currentTimeMillis()); // :FVK: workaround.
		}

		return date;
	}

	/**
	 * Returns the name of creator of this Group.
	 *
	 * @return the creator
	 */
	public final synchronized String getCreator() {
		Dictionary<String, Object> properties = this.group.getProperties();
		String creator = (String) properties.get(UserDatabase.GROUP_CREATOR);

		Role tryUser = this.userAdmin.getRole(creator);
		if (tryUser instanceof User user) {
			creator = (String)user.getProperties().get(UserDatabase.LOGIN_NAME);
		} else {
			creator = "unknown"; // :FVK: workaround.
		}

		return creator;
	}

	/**
	 * Returns the last-modified date.
	 *
	 * @return the date and time of last modification
	 */
	public synchronized Date getLastModifiedDate() {
		Dictionary<String, Object> properties = this.group.getProperties();
		String modified = (String) properties.get(UserDatabase.LAST_MODIFIED);

		return convert2date(modified);
	}

	/**
	 * Returns the name of the user who last modified this group.
	 *
	 * @return the modifier
	 */
	public final synchronized String getModifier() {
		Dictionary<String, Object> properties = this.group.getProperties();
		String modifierUID = (String) properties.get(UserDatabase.GROUP_MODIFIER);
		String modifierName;

		Role tryUser = this.userAdmin.getRole(modifierUID);
		if (tryUser instanceof User user) {
			modifierName = (String)user.getProperties().get(UserDatabase.LOGIN_NAME);
		} else {
			modifierName = "unknown"; // :FVK: workaround.
		}

		return modifierName;
	}

	/**
	 * The name of the group. This is set in the class constructor.
	 *
	 * @return the name of the Group
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Returns the GroupPrincipal that represents this Group.
	 *
	 * @return the group principal
	 */
	public Principal getPrincipal() {
		return m_principal;
	}

	/**
	 * Returns the wiki name.
	 *
	 * @return the wiki name
	 */
	public String getWiki() {
		return m_wiki;
	}

	/**
	 * Returns <code>true</code> if a Principal is a member of the group. Specifically, the
	 * Principal's <code>getName()</code> method must return the same value as one of the Principals
	 * in the group member list. The Principal's type does <em>not</em> need to match.
	 *
	 * @param principal the principal about whom membeship status is sought
	 * @return the result of the operation
	 */
	public boolean isMember(final Principal principal) {
		return findMember(principal.getName()) != null;
	}

	/**
	 * Returns the members of the group as an array of String.
	 *
	 * @return the members
	 */
	public String[] members() {
		List<String> result = new ArrayList<>();
		org.osgi.service.useradmin.Role[] members = this.group.getMembers();
		if (members != null) {
			for (org.osgi.service.useradmin.Role role : members) {
				String member;
				if (role instanceof Group group) {
					member = (String) group.getProperties().get(UserDatabase.GROUP_NAME);
				} else if (role instanceof User user) {
					member = (String)user.getProperties().get(UserDatabase.LOGIN_NAME);
				} else if (role instanceof Role) {
					member = role.getName();
				} else {
					member = ":FVK:"; // :FVK: workaround.
				}
				result.add(member);
			}
		}
		
		String[] resultArray = result.toArray(new String[result.size()]);
		return resultArray;
	}

	/**
	 * Removes a Principal from the group.
	 *
	 * @param user the principal to remove
	 * @return <code>true</code> if the operation was successful
	 */
	public synchronized boolean remove(Principal user) {
		user = findMember(user.getName());
		if (user == null)
			return false;

		m_members.remove(user);

		return true;
	}

	/**
	 * Sets the created date.
	 *
	 * @param date the creation date
	 */
	public synchronized void setCreated(final Date date) {
		m_created = date;
	}

	/**
	 * Sets the creator of this Group.
	 * 
	 * @param creator the creator
	 */
	public final synchronized void setCreator(final String creator) {
		this.m_creator = creator;
	}

	/**
	 * Sets the last-modified date
	 *
	 * @param date the last-modified date
	 */
	public synchronized void setLastModified(final Date date) {
		m_modified = date;
	}

	/**
	 * Sets the name of the user who last modified this group.
	 *
	 * @param modifier the modifier
	 */
	public final synchronized void setModifier(final String modifier) {
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
		return "(Group " + getName() + ")";
	}

	private Principal findMember(final String name) {
		for (final Principal member : m_members) {
			if (member.getName().equals(name)) {
				return member;
			}
		}

		return null;
	}

}
