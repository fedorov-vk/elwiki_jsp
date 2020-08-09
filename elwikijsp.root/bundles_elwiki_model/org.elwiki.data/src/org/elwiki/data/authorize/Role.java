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
package org.elwiki.data.authorize;

/**
 * Облегченный, неизменяемый Принципал, который представляет встроенную роль вики, такую как
 * Anonymous, Asserted и Authenticated. Он также может представлять динамические роли,
 * используемые внешним авторизатором {@link org.elwiki.api.authorization.IAuthorizer},
 * например, веб контейнером.
 * 
 * A lightweight, immutable Principal that represents a built-in wiki role such as Anonymous,
 * Asserted and Authenticated. It can also represent dynamic roles used by an external
 * {@link org.elwiki.api.authorization.IAuthorizer.auth.wiki.auth.Authorizer}, such as a web
 * container.
 * 
 * @since 2.3
 */
public final class Role extends APrincipal {

	private static final long serialVersionUID = 1L;

	/** All users, regardless of authentication status */
	public static final Role ALL = new Role("All");

	/** If the user hasn't supplied a name */
	public static final Role ANONYMOUS = new Role("Anonymous");

	/** If the user has supplied a cookie with a username */
	public static final Role ASSERTED = new Role("Asserted");

	/** If the user has authenticated with the Container or UserDatabase */
	public static final Role AUTHENTICATED = new Role("Authenticated");

	/**
	 * Create an empty Role.
	 */
	protected Role() {
		this(null);
	}

	/**
	 * Constructs a new Role with a given name.
	 * 
	 * @param name
	 *             the name of the Role
	 */
	public Role(String name) {
		super(name);
	}

	/**
	 * Returns <code>true</code> if a supplied Role is a built-in Role: {@link #ALL},
	 * {@link #ANONYMOUS}, {@link #ASSERTED}, or {@link #AUTHENTICATED}.
	 * 
	 * @param role
	 *             the role to check
	 * @return the result of the check
	 */
	public static boolean isBuiltInRole(Role role) {
		return role.equals(ALL) || role.equals(ANONYMOUS) || role.equals(ASSERTED) || role.equals(AUTHENTICATED);
	}

	/**
	 * Returns <code>true</code> if the supplied name is identical to the name of a built-in Role;
	 * that is, the value returned by <code>getName()</code> for built-in Roles {@link #ALL},
	 * {@link #ANONYMOUS}, {@link #ASSERTED}, or {@link #AUTHENTICATED}.
	 * 
	 * @param name
	 *             the name to be tested
	 * @return <code>true</code> if the name is reserved; <code>false</code> if not
	 */
	public static boolean isReservedName(String name) {
		return name.equals(ALL.getName()) || name.equals(ANONYMOUS.getName()) || name.equals(ASSERTED.getName())
				|| name.equals(AUTHENTICATED.getName());
	}

	/**
	 * Two Role objects are considered equal if their names are identical.
	 * 
	 * @param obj
	 *            the object to test
	 * @return <code>true</code> if both objects are of type Role and have identical names
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Role) {
			return this.getName().equals(((Role) obj).getName());
		}
		return false;
	}

}