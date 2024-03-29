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

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.auth.WikiSecurityException;
//import org.elwiki.api.event.WikiEventListener;
//import org.elwiki.api.event.WikiEventProvider;
//import org.elwiki.api.exceptions.WikiSecurityException;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.useradmin.Group;

/**
 * <p>
 * The interface partially serves groups. Together with {@link AccountManager} - provides a code
 * base of {@link DefaultAccountManager}.
 */
public interface IGroupManager {

	String CREATED = "created";

	String CREATOR = "creator";

	String GROUP_TAG = "group";

	//:FVK: - определение не отсюда... String GROUP_NAME = "name";

	String LAST_MODIFIED = "lastModified";

	String MODIFIER = "modifier";

	String MEMBER_TAG = "member";

	String PRINCIPAL = "principal";

	String DATE_FORMAT = "yyyy.MM.dd 'at' HH:mm:ss:SSS z";
	DateFormat m_format = new SimpleDateFormat(DATE_FORMAT);

	/** Key used for adding UI messages to a user's WikiSession. */
	String MESSAGES_KEY = "group";

	//@formatter:off
	public static String[] RESTRICTED_GROUPNAMES = new String[] {
			"Anonymous",
			"All",
			"Asserted",
			"Authenticated" };
	//@formatter:on

	/**
	 * Returns an array of role Principals this Authorizer knows about. This method will always return
	 * an array; an implementing class may choose to return an zero-length array if it has no ability to
	 * identify the roles under its control.
	 * <p>
	 * This method actually returns a defensive copy of an internally stored data (ie. hashmap/array).
	 * 
	 * @return an array of Principals representing the roles.
	 * @throws WikiSecurityException
	 */
	//TODO: :FVK: remove full class name of Group.; RENAME getRoles -> getGroups.
	List<IGroupWiki> getGroups() throws WikiSecurityException;

	/**
	 * Looks up and returns a role Principal (GroupPrincipal) matching a given name. If a matching role
	 * cannot be found, this method returns <code>null</code>. Note that it may not always be feasible
	 * for an Authorizer implementation to return a role Principal.
	 * 
	 * @param roleName the name of the role to retrieve.
	 * @return the role Principal
	 */
	Principal findRole(String roleName);

	/**
	 * Determines whether the Subject associated with a WikiSession is in a particular role. This method
	 * takes two parameters: the WikiSession containing the subject and the desired role ( which may be
	 * a Role or a Group). If either parameter is <code>null</code>, this method must return
	 * <code>false</code>.
	 * 
	 * @param session the current WikiSession
	 * @param rgoup   the role to check
	 * @return <code>true</code> if the user is considered to be in the role, <code>false</code>
	 *         otherwise
	 */
	//TODO: :FVK: remove full class name of Group.
	boolean isUserInRole(/*IElWikiSession session,*/ org.osgi.service.useradmin.Group rgoup);

	/**
	 * Returns the Group matching a given name. If the group cannot be found, this method throws a
	 * <code>NoSuchPrincipalException</code>.
	 * 
	 * @param name the name of the group to find.
	 * @return the group.
	 * @throws NoSuchPrincipalException if the group cannot be found.
	 */
	//:FVK:org.osgi.service.useradmin.Group getGroup(String name) throws NoSuchPrincipalException;

	public IGroupWiki parseGroup(WikiContext context, boolean create) throws WikiSecurityException;

	/**
	 * Extracts group name and members from passed parameters and populates an existing Group with them.
	 * The Group will either be a copy of an existing Group (if one can be found), or a new,
	 * unregistered Group (if not). Optionally, this method can throw a WikiSecurityException if the
	 * Group does not yet exist in the GroupManager cache.
	 * <p>
	 * The <code>group</code> parameter in the HTTP request contains the Group name to look up and
	 * populate. The <code>members</code> parameter contains the member list. If these differ from those
	 * in the existing group, the passed values override the old values.
	 * <p>
	 * This method does not commit the new Group to the GroupManager cache. To do that, use
	 * {@link #setGroup(WikiSession, IGroupWiki)}.
	 *
	 * @param session    TODO
	 * @param name       the name of the group to construct.
	 * @param memberLine the line of text containing the group membership list.
	 * 
	 * @param create     -- @Deprecated whether this method should create a new, empty Group if one with
	 *                   the requested name is not found. If <code>false</code>, groups that do not
	 *                   exist will cause a <code>NoSuchPrincipalException</code> to be thrown.
	 * 
	 * @return a new, populated group.
	 * @see GroupWiki#RESTRICTED_GROUPNAMES
	 * @throws WikiSecurityException if the group name isn't allowed, or if <code>create</code> is
	 *                               <code>false</code> and the Group named <code>name</code> does not
	 *                               exist.
	 */
	//TODO: :FVK: remove full class name of Group.
	org.osgi.service.useradmin.Group parseGroup(/*IElWikiSession session,*/ String name, String memberLine,
			boolean create) throws WikiSecurityException;

	/**
	 * Removes a named Group from the group database. If not found, throws a
	 * <code>NoSuchPrincipalException</code>. After removal, this method will commit the delete to the
	 * back-end group database. It will also fire a
	 * {@link SecurityEvent.Topic.GROUP_REMOVE} event with the
	 * GroupManager instance as the source and the Group as target. If <code>index</code> is
	 * <code>null</code>, this method throws an {@link IllegalArgumentException}.
	 * 
	 * @param index the group to remove.
	 * @throws WikiSecurityException if the Group cannot be removed by the back-end.
	 * @see GroupDatabase#delete(GroupWiki)
	 */
	void removeGroup(IGroupWiki index) throws WikiSecurityException;

	/**
	 * Saves the {@link GroupWiki} created by a user in a wiki session. This method registers the Group
	 * with the GroupManager and saves it to the back-end database. If an existing Group with the same
	 * name already exists, the new group will overwrite it. After saving the Group, the group database
	 * changes are committed.
	 * <p>
	 * This method fires the following events:
	 * <ul>
	 * <li><strong>When creating a new Group</strong>, this method fires a
	 * {@link SecurityEvent.Topic.GROUP_ADD} with the GroupManager
	 * instance as its source and the new Group as the target.</li>
	 * <li><strong>When overwriting an existing Group</strong>, this method fires a new
	 * {@link SecurityEvent.Topic.GROUP_REMOVE} with this GroupManager
	 * instance as the source, and the new Group as the target. It then fires a
	 * {@link SecurityEvent.Topic.GROUP_ADD} event with the same source
	 * and target.</li>
	 * </ul>
	 * <p>
	 * In addition, if the save or commit actions fail, this method will attempt to restore the older
	 * version of the wiki group if it exists. This will result in a <code>GROUP_REMOVE</code> event
	 * (for the new version of the Group) followed by a <code>GROUP_ADD</code> event (to indicate
	 * restoration of the old version).
	 * <p>
	 * This method will register the new Group with the GroupManager. For example,
	 * {@link org.elwiki.AuthenticationManager} attaches each
	 * WikiSession as a GroupManager listener. Thus, the act of registering a Group with
	 * <code>setGroup</code> means that all WikiSessions will automatically receive group
	 * add/change/delete events immediately.
	 * 
	 * @param session the wiki session, which may not be <code>null</code>.
	 * @param group   the Group, which may not be <code>null</code>.
	 * @throws WikiSecurityException if the Group cannot be saved by the back-end.
	 */
	void setGroup(WikiSession session, IGroupWiki group) throws WikiSecurityException;

	/**
	 * Returns useradmin OSGi group by specified name.
	 * 
	 * @param groupName
	 * @return
	 */
	org.osgi.service.useradmin.Group getGroup(String groupName);

	/**
	 * Возвращает массив PermissionInfo для заданной группы.<br/>
	 *
	 * @param roleName
	 * @return
	 */
	PermissionInfo[] getRolePermissionInfo(String roleName);

	void validateGroup(WikiContext context, IGroupWiki group);
}
