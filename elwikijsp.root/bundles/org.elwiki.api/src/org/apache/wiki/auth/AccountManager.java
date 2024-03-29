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
package org.apache.wiki.auth;

import java.security.Principal;

import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.WikiException;
import org.elwiki.api.authorization.IGroupManager;

/**
 * Provides a facade for obtaining user information.
 *
 * @since 2.3
 */
public interface AccountManager extends IGroupManager {

    /**
     * <p>Retrieves the {@link org.apache.wiki.auth.UserProfile} for the user in a session. If the user is authenticated, the
     * UserProfile returned will be the one stored in the user database; if one does not exist, a new one will be initialized and returned.
     * If the user is anonymous or asserted, the UserProfile will <i>always</i> be newly initialized to prevent spoofing of identities.
     * If a UserProfile needs to be initialized, its {@link org.apache.wiki.auth.UserProfile#isNew()} method will return
     * <code>true</code>, and its login name will will be set automatically if the user is authenticated. Note that this method does
     * not modify the retrieved (or newly created) profile otherwise; other fields in the user profile may be <code>null</code>.</p>
     * <p>If a new UserProfile was created, but its {@link org.apache.wiki.auth.UserProfile#isNew()} method returns
     * <code>false</code>, this method throws an {@link IllegalStateException}. This is meant as a quality check for AccountRegistry providers;
     * it should only be thrown if the implementation is faulty.</p>
     *
     * @param session the session, which may not be <code>null</code>
     * @return the user's profile, which will be newly initialized if the user is anonymous or asserted, or if the user cannot be found in
     *         the user database
     */
    UserProfile getUserProfile( WikiSession session );

	/**
	 * <p>
	 * Saves the {@link org.apache.wiki.auth.UserProfile} for the user in a wiki session. This method
	 * verifies that a user profile to be saved doesn't collide with existing profiles; that is, the
	 * login name or full name is already used by another profile. If the profile collides, a
	 * <code>DuplicateUserException</code> is thrown. After saving the profile, the user database
	 * changes are committed, and the user's credential set is refreshed; if custom authentication is
	 * used, this means the user will be automatically be logged in.
	 * </p>
	 * <p>
	 * When the user's profile is saved successfully, this method fires a
	 * {@link SecurityEvent.Topic.PROFILE_SAVE} event with the Session as the source and
	 * the UserProfile as target. For existing profiles, if the user's full name changes, this method
	 * also fires a "name changed" event
	 * ({@link SecurityEvent.Topic.PROFILE_NAME_CHANGED}) with the Session as the source
	 * and an array containing the old and new UserProfiles, respectively. The <code>NAME_CHANGED</code>
	 * event allows the GroupManager and PageManager can change group memberships and ACLs if needed.
	 * </p>
	 * <p>
	 * Note that Sessions normally attach event listeners to the AccountManager, so changes to the
	 * profile will automatically cause the correct Principals to be reloaded into the current Session's
	 * Subject.
	 * </p>
	 *
	 * @param session the wiki session, which may not be <code>null</code>
	 * @param profile the user profile, which may not be <code>null</code>
	 * @throws DuplicateUserException if the proposed profile's login name or full name collides with
	 *                                another
	 * @throws WikiException          if the save fails for some reason. If the current user does not
	 *                                have permission to save the profile, this will be a
	 *                                {@link org.apache.wiki.auth.WikiSecurityException}; if if the user
	 *                                profile must be approved before it can be saved, it will be a
	 *                                {@link org.apache.wiki.workflow0.DecisionRequiredException}. All
	 *                                other WikiException indicate a condition that is not normal is
	 *                                probably due to mis-configuration
	 */
	   void setUserProfile( WikiSession session, UserProfile profile ) throws DuplicateUserException, WikiException;

    void startUserProfileCreationWorkflow( WikiSession session, UserProfile profile ) throws WikiException;

    /**
     * <p> Extracts user profile parameters from the HTTP request and populates a UserProfile with them. The UserProfile will either be a
     * copy of the user's existing profile (if one can be found), or a new profile (if not). The rules for populating the profile as as
     * follows: </p>
     * <ul>
     * <li>If the <code>email</code> or <code>password</code> parameter values differ from those in the existing profile, the passed
     * parameters override the old values.</li>
     * <li>For new profiles, the user-supplied <code>fullname</code> parameter is always used; for existing profiles the existing value is
     * used, and whatever value the user supplied is discarded. The wiki name is automatically computed by taking the full name and
     * extracting all whitespace.</li>
     * <li>In all cases, the created/last modified timestamps of the user's existing or new profile always override whatever values the user
     * supplied.</li>
     * <li>If container authentication is used, the login name property of the profile is set to the name of
     * {@link org.apache.wiki.api.core.WikiSession#getLoginPrincipal()}. Otherwise, the value of the <code>loginname</code> parameter is used.</li>
     * </ul>
     *
     * @param context the current wiki context
     * @return a new, populated user profile
     */
    UserProfile parseProfile( WikiContext context );

    /**
     * Validates a user profile, and appends any errors to the session errors list. If the profile is new, the password will be checked to
     * make sure it isn't null. Otherwise, the password is checked for length and that it matches the value of the 'password2' HTTP
     * parameter. Note that we have a special case when container-managed authentication is used and the user is not authenticated;
     * this will always cause validation to fail. Any validation errors are added to the wiki session's messages collection
     * (see {@link org.apache.wiki.api.core.WikiSession#getMessages()}.
     *
     * @param context the current wiki context
     * @param profile the supplied UserProfile
     */
    void validateProfile( WikiContext context, UserProfile profile );

    /**
     *  A helper method for returning all of the known WikiNames in this system.
     *
     *  @return An Array of Principals
     *  @throws WikiSecurityException If for reason the names cannot be fetched
     */
    Principal[] listWikiNames() throws WikiSecurityException;

	String getGroupUid(String name);

	/**
	 * Returns user name according to specified UID.
	 * 
	 * @param uid
	 * @return User name.
	 */
	String getUserName(String uid);

}
