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

/**
 * <p>
 * Permission to perform all operations on a given wiki.
 * </p>
 * 
 * @since 2.3.80
 */
public final class AllPermission extends APermission {

	private static final long serialVersionUID = 11L;

	private static final String WILDCARD = "*";

	/** For serialization purposes. */
	protected AllPermission() {
		this(null);
	}

	/**
	 * Creates a new AllPermission for the given wikis.
	 * 
	 * @param wiki
	 *            the wiki to which the permission should apply. If null, will apply to all wikis.
	 */
	public AllPermission(String wiki) {
		super(wiki);
		setWikiName((wiki == null) ? WILDCARD : wiki);
	}

	/**
	 * Two AllPermission objects are considered equal if their wikis are equal.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * @return {@inheritDoc}
	 * @param obj
	 *            {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AllPermission)) {
			return false;
		}
		AllPermission p = (AllPermission) obj;
		return p.getWikiName() != null && p.getWikiName().equals(this.getWikiName());
	}

	/**
	 * No-op; always returns <code>null</code>
	 * 
	 * @see java.security.Permission#getActions()
	 *
	 * @return Always null.
	 */
	@Override
	public String getActions() {
		return null;
	}

	/**
	 * Returns the hash code for this WikiPermission.
	 * 
	 * @see java.lang.Object#hashCode()
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return getWikiName().hashCode();
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
		// Permission must be of ElWiki permissions.
		if (permission instanceof APermission) {
			String wiki = ((APermission) permission).getWikiName();

			// If the wiki is implied, it's allowed
			return PagePermission.isSubset(this.getWikiName(), wiki);
		}

		return false;
	}

	/**
	 * Returns a new {@link AllPermissionCollection}.
	 * 
	 * @see java.security.Permission#newPermissionCollection()
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
	 * @see java.lang.Object#toString()
	 * @return {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + "|" + this.getWikiName();
	}
}
