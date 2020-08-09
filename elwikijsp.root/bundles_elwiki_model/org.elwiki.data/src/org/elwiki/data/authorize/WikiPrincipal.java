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

import java.security.Principal;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A lightweight, immutable Principal class. WikiPrincipals can be created with and optional
 * "type" to denote what type of user profile Principal it represents (FULL_NAME, WIKI_NAME,
 * LOGIN_NAME). Types are used to determine suitable user and login Principals in classes like
 * WikiSession. However, the type property of a WikiPrincipal does not affect a WikiPrincipal's
 * logical equality or hash code; two WikiPrincipals with the same name but different types are
 * still considered equal.
 */
public final class WikiPrincipal extends APrincipal implements Comparable<Principal> {

	private static final long serialVersionUID = 1L;

	/**
	 * Represents an anonymous user. WikiPrincipals may be created with an optional type designator:
	 * LOGIN_NAME, WIKI_NAME, FULL_NAME or UNSPECIFIED.
	 */
	public static final Principal GUEST = new WikiPrincipal("Guest");

	/** WikiPrincipal type denoting a user's uid. */
	public static final String USED_ID = "userId";

	/** WikiPrincipal type denoting a user's full name. */
	public static final String FULL_NAME = "fullName";

	/** WikiPrincipal type denoting a user's login name. */
	public static final String LOGIN_NAME = "loginName";

	/** WikiPrincipal type denoting a user's wiki name. */
	public static final String WIKI_NAME = "wikiName";

	/** Generic WikiPrincipal of unspecified type. */
	public static final String UNSPECIFIED = "unspecified";

	/** Static instance of Comparator that allows Principals to be sorted. */
	public static final Comparator<Principal> COMPARATOR = new PrincipalComparator();

	private static final String[] VALID_TYPES;

	static {
		VALID_TYPES = new String[] { USED_ID, FULL_NAME, LOGIN_NAME, WIKI_NAME, UNSPECIFIED };
		Arrays.sort(VALID_TYPES);
	}

	private final String m_type;

	/** For serialization purposes */
	protected WikiPrincipal() {
		this(null);
	}

	/**
	 * Constructs a new WikiPrincipal with a given name and a type of {@link #UNSPECIFIED}.
	 * 
	 * @param name
	 *             the name of the Principal
	 */
	public WikiPrincipal(String name) {
		super(name);
		this.m_type = UNSPECIFIED;
	}

	/**
	 * Constructs a new WikiPrincipal with a given name and optional type designator. If the
	 * supplied <code>type</code> parameter is not {@link #LOGIN_NAME}, {@link #FULL_NAME},
	 * {@link #WIKI_NAME} or {@link #WIKI_NAME}, this method throws an
	 * {@link IllegalArgumentException}.
	 * 
	 * @param name
	 *             the name of the Principal
	 * @param type
	 *             the type for this principal, which may be {@link #LOGIN_NAME},
	 *             {@link #FULL_NAME}, {@link #WIKI_NAME} or {@link #WIKI_NAME}.
	 */
	public WikiPrincipal(String name, String type) {
		super(name);
		if (type == null || Arrays.binarySearch(VALID_TYPES, type) < 0) {
			throw new IllegalArgumentException("Principal type '" + type + "' is invalid.");
		}
		this.m_type = type;
	}

	/**
	 * Two <code>WikiPrincipal</code>s are considered equal if their names are equal
	 * (case-sensitive).
	 * 
	 * @param obj
	 *            the object to compare
	 * @return the result of the equality test
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WikiPrincipal) {
			return this.getName().equals(((WikiPrincipal) obj).getName());
		}
		return false;
	}

	/**
	 * Returns the Principal "type": {@link #LOGIN_NAME}, {@link #FULL_NAME}, {@link #WIKI_NAME} or
	 * {@link #WIKI_NAME}
	 * 
	 * @return the type
	 */
	public String getType() {
		return this.m_type;
	}

	/**
	 * Allows comparisons to any other Principal objects. Primary sorting order is by the principal
	 * name, as returned by getName().
	 * 
	 * @param o
	 *          {@inheritDoc}
	 * @return {@inheritDoc}
	 * @since 2.7.0
	 */
	@Override
	public int compareTo(Principal o) {
		return getName().compareTo(o.getName());
	}

	/**
	 * Returns an additional String for representing of the object.
	 * 
	 * @return the string representation.
	 */
	protected String getParameters() {
		return this.m_type;
	}

}