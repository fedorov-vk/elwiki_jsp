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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.wiki.auth.user0.UserProfile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.annotation.NonNull;
//:FVK:import org.elwiki.core.common.WikiSession;

/**
 * Default implementation for representing wiki user information, such as the login name, full
 * name, wiki name, and e-mail address.
 * 
 * @since 2.3
 */
public final class DefaultUserProfile implements UserProfile {

	private static final long serialVersionUID = -5600466893735300647L;

	private static final String EMPTY_STRING = "";

	private static final String WHITESPACE = "\\s";

	private Map<String, Serializable> attributes = new HashMap<String, Serializable>();

	private Date created = null;

	private @NonNull String email = "";

	private @NonNull String fullname = "";

	private Date lockExpiry = null;

	private @NonNull String loginName = "";

	private Date modified = null;

	private @NonNull String password = "";

	private @NonNull String uid = "";

	// :FVK: Это fullname, без пробелов. ??может объявить его @Deprecated 
	private @NonNull String wikiname = "";

	/**
	 * Private constructor to prevent direct instantiation.
	 */
	private DefaultUserProfile() {
		// no-op.
	}

	/**
	 * Static factory method that creates a new DefaultUserProfile.
	 *
	 * @return the new profile
	 */
	protected static UserProfile newProfile() {
		UserProfile profile = new DefaultUserProfile();
		return profile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof UserProfile)) {
			DefaultUserProfile u = (DefaultUserProfile) o;
			return same(this.fullname, u.fullname) && same(this.password, u.password)
					&& same(this.loginName, u.loginName)
					&& same(StringUtils.lowerCase(this.email), StringUtils.lowerCase(u.email))
					&& same(this.wikiname, u.wikiname);
		}

		return false;
	}

	@Override
	public int hashCode() {
		//@formatter:off
		return this.fullname.hashCode()
				^ this.password.hashCode()
				^ this.loginName.hashCode()
				^ this.wikiname.hashCode()
				^ StringUtils.lowerCase(this.email).hashCode();
		//@formatter:on
	}

	/**
	 * Returns the creation date
	 * 
	 * @return the creation date
	 * @see org.elwiki.api.authorization.user.core.auth.user.wiki.auth.user.UserProfile#getCreated()
	 */
	@Override
	public Date getCreated() {
		return this.created;
	}

	/**
	 * Returns the user's e-mail address.
	 * 
	 * @return the e-mail address
	 */
	@NonNull
	@Override
	public String getEmail() {
		return this.email;
	}

	/**
	 * Returns the user's full name.
	 * 
	 * @return the full name
	 */
	@NonNull
	@Override
	public String getFullname() {
		return this.fullname;
	}

	/**
	 * Returns the last-modified date.
	 * 
	 * @return the last-modified date
	 * @see org.elwiki.api.authorization.user.core.auth.user.wiki.auth.user.UserProfile#getLastModified()
	 */
	@Override
	public Date getLastModified() {
		return this.modified;
	}

	/**
	 * Returns the user's login name.
	 * 
	 * @return the login name
	 */
	@NonNull
	@Override
	public String getLoginName() {
		return this.loginName;
	}

	/**
	 * Returns the user password for use with custom authentication. Note that the password field is
	 * not meaningful for container authentication; the user's private credentials are generally
	 * stored elsewhere. While it depends on the {@link IUserDatabase}implementation, in most cases
	 * the value returned by this method will be a password hash, not the password itself.
	 * 
	 * @return the password
	 */
	@NonNull
	@Override
	public String getPassword() {
		return this.password;
	}

	/**
	 * Returns the user's wiki name.
	 * 
	 * @return the wiki name.
	 */
	@NonNull
	@Override
	public String getWikiName() {
		return this.wikiname;
	}

	/**
	 * Returns <code>true</code> if the user profile is new. This implementation checks whether
	 * {@link #getLastModified()} returns <code>null</code> to determine the status.
	 * 
	 * @see org.elwiki.api.authorization.user.core.auth.user.wiki.auth.user.UserProfile#isNew()
	 */
	@Override
	public boolean isNew() {
		return this.modified == null;
	}

	/**
	 * @param date the creation date
	 * @see org.elwiki.api.authorization.user.core.auth.user.wiki.auth.user.UserProfile#setCreated(java.util.Date)
	 */
	@Override
	public void setCreated(Date date) {
		this.created = date;
	}

	/**
	 * Sets the user's e-mail address.
	 * 
	 * @param email the e-mail address
	 */
	@Override
	public void setEmail(@NonNull String email) {
		this.email = email;
	}

	/**
	 * Sets the user's full name. For example, "Janne Jalkanen."
	 * 
	 * @param arg the full name
	 */
	@SuppressWarnings("null")
	@Override
	public void setFullname(@NonNull String arg) {
		this.fullname = arg;

		// Compute wiki name
		this.wikiname = this.fullname.replaceAll(WHITESPACE, EMPTY_STRING);
	}

	/**
	 * Sets the last-modified date.
	 * 
	 * @param date the last-modified date
	 * @see org.elwiki.api.authorization.user.core.auth.user.wiki.auth.user.UserProfile#setLastModified(java.util.Date)
	 */
	@Override
	public void setLastModified(Date date) {
		this.modified = date;
	}

	/**
	 * Sets the name by which the user logs in. The login name is used as the username for custom
	 * authentication (see
	 * {@link org.elwiki.authorize.internal.services.IIAuthenticationManager.auth.wiki.auth.AuthenticationManager#login(WikiSession,HttpServletRequest, String, String)}).
	 * The login name is typically a short name ("jannej"). In contrast, the wiki name is typically
	 * of type FirstnameLastName ("JanneJalkanen").
	 * 
	 * @param name the login name
	 */
	@Override
	public void setLoginName(@NonNull String name) {
		this.loginName = name;
	}

	/**
	 * Sets the user's password for use with custom authentication. It is <em>not</em> the
	 * responsibility of implementing classes to hash the password; that responsibility is borne by
	 * the UserDatabase implementation during save operations (see
	 * {@link IUserDatabase#saveProfile(UserProfile)}). Note that the password field is not meaningful for
	 * container authentication; the user's private credentials are generally stored elsewhere.
	 * 
	 * @param arg the password
	 */
	@Override
	public void setPassword(@NonNull String arg) {
		this.password = arg;
	}

	/**
	 * Returns a string representation of this user profile.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return "[DefaultUserProfile: '" + getFullname() + "']";
	}

	/**
	 * Private method that compares two objects and determines whether they are equal. Two nulls are
	 * considered equal.
	 * 
	 * @param arg1 the first object
	 * @param arg2 the second object
	 * @return the result of the comparison
	 */
	private boolean same(Object arg1, Object arg2) {
		if (arg1 == null && arg2 == null) {
			return true;
		}
		if (arg1 == null || arg2 == null) {
			return false;
		}
		return arg1.equals(arg2);
	}

	//--------------------------- Attribute and lock interface implementations ---------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Serializable> getAttributes() {
		return this.attributes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLockExpiry() {
		return isLocked() ? this.lockExpiry : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLockExpiry(Date expiry) {
		this.lockExpiry = expiry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLocked() {
		boolean locked = this.lockExpiry != null && System.currentTimeMillis() < this.lockExpiry.getTime();
		// Clear the lock if it's expired already
		if (!locked && this.lockExpiry != null) {
			this.lockExpiry = null;
		}
		return locked;
	}

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getUid() {
		return this.uid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUid(@NonNull String uid) {
		this.uid = uid;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	
}