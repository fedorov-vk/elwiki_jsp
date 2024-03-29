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
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A collection of AllPermission objects.
 */
public class AllPermissionCollection extends PermissionCollection {

	private static final long serialVersionUID = -8627167711908878722L;

	private boolean m_notEmpty = false;

	private boolean m_readOnly = false;

	protected final Hashtable<Permission, Permission> m_permissions = new Hashtable<Permission, Permission>();

	/**
	 * Adds an AllPermission object to this AllPermissionCollection. If this
	 * collection was previously marked read-only, or if the permission supplied is
	 * not of type {@link AllPermission}, a {@link SecurityException} is thrown.
	 * 
	 * @see java.security.PermissionCollection#add(java.security.Permission)
	 * 
	 * @param permission {@inheritDoc}
	 */
	@Override
	public void add(Permission permission) {
		if (!(permission instanceof Apermission)) {
			throw new IllegalArgumentException("Permission must be of type org.elwiki.auth.permissions.*Permission.");
		}

		if (this.m_readOnly) {
			throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
		}

		this.m_notEmpty = true;

		// This is a filthy hack, but it keeps us from having to write our own
		// Enumeration implementation
		this.m_permissions.put(permission, permission);
	}

	/**
	 * Returns an enumeration of all AllPermission objects stored in this
	 * collection.
	 * 
	 * @see java.security.PermissionCollection#elements()
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public Enumeration<Permission> elements() {
		return this.m_permissions.elements();
	}

	/**
	 * Iterates through the AllPermission objects stored by this
	 * AllPermissionCollection and determines if any of them imply a supplied
	 * Permission. If the Permission is not of type {@link AllPermission},
	 * {@link PagePermission} or {@link WikiPermission}, this method will return
	 * <code>false</code>. If none of the AllPermissions stored in this collection
	 * imply the permission, the method returns <code>false</code>; conversely, if
	 * one of the AllPermission objects implies the permission, the method returns
	 * <code>true</code>.
	 * 
	 * @param permission the Permission to test. It may be any Permission type, but
	 *                   only the AllPermission, PagePermission or WikiPermission
	 *                   types are actually evaluated.
	 * @see java.security.PermissionCollection#implies(java.security.Permission)
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean implies(Permission permission) {
		// If nothing in the collection yet, fail fast
		if (!this.m_notEmpty) {
			return false;
		}

		// If not one of our permission types, it's not implied
		if (!(permission instanceof Apermission)) {
			return false;
		}

		// Step through each AllPermission
		Enumeration<Permission> permEnum = this.m_permissions.elements();
		while (permEnum.hasMoreElements()) {
			Permission storedPermission = permEnum.nextElement();
			if (storedPermission.implies(permission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnly() {
		return this.m_readOnly;
	}

	/**
	 * @see java.security.PermissionCollection#setReadOnly()
	 */
	@Override
	public void setReadOnly() {
		this.m_readOnly = true;
	}

}
