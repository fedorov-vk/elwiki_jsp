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
package org.elwiki.api.authorization.authorize;

import java.security.Principal;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.auth.WikiSecurityException;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.IElWikiSession;
import org.elwiki.api.authorization.IGroupWiki;
//import org.elwiki.api.exceptions.NoRequiredPropertyException;
//import org.elwiki.api.exceptions.WikiSecurityException;

/**
 * Defines an interface for loading, persisting and storing wiki groups.
 */
public interface GroupDatabase {
	/**
	 * No-op method that in previous versions of JSPWiki was intended to atomically commit
	 * changes to the user database. Now, the {@link #save(GroupWiki, Principal)} and
	 * {@link #delete(GroupWiki)} methods are atomic themselves.
	 * 
	 * @throws WikiSecurityException
	 *             never...
	 * @deprecated there is no need to call this method because the save and delete
	 *             methods contain their own commit logic
	 */
	void commit() throws WikiSecurityException;

	/**
	 * Looks up and deletes a {@link GroupWiki} from the group database. If the group database
	 * does not contain the supplied Group. this method throws a
	 * {@link org.elwiki.core.api.exceptions.wiki.auth.NoSuchPrincipalException}. The method commits the
	 * results of the delete to persistent storage.
	 * 
	 * @param group
	 *            the group to remove
	 * @throws WikiSecurityException
	 *             if the database does not contain the supplied group (thrown as
	 *             {@link org.elwiki.core.api.exceptions.wiki.auth.NoSuchPrincipalException}) or if the commit
	 *             did not succeed
	 */
	void delete(IGroupWiki group) throws WikiSecurityException;

	/**
	 * Initializes the group database based on values from a Properties object.
	 * 
	 * @param applicationSession
	 *            the application sesion.
	 * @throws WikiSecurityException
	 *             if the database could not be initialized successfully
	 * @throws NoRequiredPropertyException
	 *             if a required property is not present
	 */
	void initialize(Engine applicationSession) throws NoRequiredPropertyException, WikiSecurityException;

	/**
	 * Saves a Group to the group database. Note that this method <em>must</em> fail, and
	 * throw an <code>IllegalArgumentException</code>, if the proposed group is the same
	 * name as one of the built-in Roles: e.g., Admin, Authenticated, etc. The database is
	 * responsible for setting create/modify timestamps, upon a successful save, to the
	 * Group. The method commits the results of the delete to persistent storage.
	 * 
	 * @param group
	 *            the Group to save
	 * @param modifier
	 *            the user who saved the Group
	 * @throws WikiSecurityException
	 *             if the Group could not be saved successfully
	 */
	void save(IGroupWiki group, Principal modifier) throws WikiSecurityException;

	/**
	 * Returns all wiki groups that are stored in the GroupDatabase as an array of Group
	 * objects. If the database does not contain any groups, this method will return a
	 * zero-length array. This method causes back-end storage to load the entire set of
	 * group; thus, it should be called infrequently (e.g., at initialization time). Note
	 * that this method should use the protected constructor
	 * {@link GroupWiki#Group(String, String)} rather than the various "parse" methods
	 * ({@link GroupManager#parseGroup(IElWikiSession, String, String, boolean)}) to construct the group.
	 * This is so as not to flood GroupManager's event queue with spurious events.
	 * 
	 * @return the wiki groups
	 * @throws WikiSecurityException
	 *             if the groups cannot be returned by the back-end
	 */
	IGroupWiki[] groups() throws WikiSecurityException;
}
