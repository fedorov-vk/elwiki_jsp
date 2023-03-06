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
 * Immutable Principal that represents a Group. GroupPrincipals are injected into a Subject's
 * principal list at the time of authentication (login), and serve as proxies for Group objects for
 * the purposes of making Java 2 security policy decisions. We add GroupPrincipals instead of the
 * actual Groups because calling classes should never be able to obtain a mutable object (Group
 * memberships can be changed by callers). Administrators who wish to grant privileges to specific
 * wiki groups via the security policy file should always specify principals of type GroupPrincipal.
 * 
 * @see org.elwiki.api.authorization.Group
 * @since 2.3.79
 */
public final class GroupPrincipal extends Aprincipal implements Comparable<GroupPrincipal> {

	private static final long serialVersionUID = 1L;

	public static final String ALL_GROUP_UID = "00E38123-3FF8-4C9E-BC30-19190A2D07DC";
	public static final String ANONYMOUS_GROUP_UID = "322DC8D2-422F-488B-BCA3-F3117581D9FE";
	public static final String ASSERTED_GROUP_UID = "5ACBB1BB-393C-4DB1-B326-4CD299B92227";
	public static final String AUTHENTICATED_GROUP_UID = "78FCF6AD-9A08-42F2-ADE8-8A895569B470";
	public static final String ADMIN_GROUP_UID = "80618E94-99F8-4BDA-9646-290EE539F874";

	/** All users, regardless of authentication status */
	public static final GroupPrincipal ALL = new GroupPrincipal("All", ALL_GROUP_UID);

	/** If the user hasn't supplied a name */
	public static final GroupPrincipal ANONYMOUS = new GroupPrincipal("Anonymous", ANONYMOUS_GROUP_UID);

	/** If the user has supplied a cookie with a user name */
	public static final GroupPrincipal ASSERTED = new GroupPrincipal("Asserted", ASSERTED_GROUP_UID);

	/** If the user has authenticated with the Container or UserDatabase */
	public static final GroupPrincipal AUTHENTICATED = new GroupPrincipal("Authenticated", AUTHENTICATED_GROUP_UID);

	/**
	 * Constructs a new GroupPrincipal object with a supplied name.
	 *
	 * @param groupName the wiki group; cannot be <code>null</code>
	 * @param groupUid  the UID of group; cannot be <code>null</code>
	 */
	public GroupPrincipal(String groupName, String groupUid) throws IllegalArgumentException {
		super(groupName, groupUid);
		if (groupName == null) {
			throw new IllegalArgumentException("Group name cannot be null.");
		}
		if (groupUid == null) {
			throw new IllegalArgumentException("Group UID cannot be null.");
		}
	}

	/**
	 * Returns <code>true</code> if a supplied Group is a built-in Group: {@link #ALL},
	 * {@link #ANONYMOUS}, {@link #ASSERTED}, or {@link #AUTHENTICATED}.
	 * 
	 * @param group the role to check
	 * @return the result of the check
	 */
	public static boolean isBuiltInGroup(GroupPrincipal group) {
		String uid = group.getUid();
		return GroupPrincipal.ALL_GROUP_UID.equals(uid) //
				|| GroupPrincipal.ANONYMOUS_GROUP_UID.equals(uid) //
				|| GroupPrincipal.ASSERTED_GROUP_UID.equals(uid) //
				|| GroupPrincipal.AUTHENTICATED_GROUP_UID.equals(uid);
	}

	/**
	 * Two GroupPrincipals are equal if their UIDs are equal.
	 * 
	 * @param obj the object to compare
	 * @return <code>true</code> if both objects are of type GroupPrincipal and have identical UIDs
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GroupPrincipal groupPrincipal) {
			String thisUid = this.getUid();
			String objUid = groupPrincipal.getUid();
			return thisUid.equals(objUid);
		}
		return false;
	}

	@Override
	public int compareTo(GroupPrincipal o) {
		return this.getName().compareTo(o.getName());
	}

}
