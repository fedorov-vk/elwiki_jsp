package org.elwiki.api.authorization;

import java.security.Principal;
import java.util.Date;

public interface IGroupWiki {

	/**
	 * Adds a Principal to the group.
	 * 
	 * @param user
	 *            the principal to add
	 * @return <code>true</code> if the operation was successful
	 */
	boolean add(Principal user);

	/**
	 * Clears all Principals from the group list.
	 */
	void clear();

	/**
	 * Two DefaultGroups are equal if they contain identical member Principals and have the same name.
	 * 
	 * @param o
	 *            the object to compare
	 * @return the comparison
	 */
	boolean equals(Object o);

	/**
	 * The hashcode is calculated as a XOR sum over all members of the Group.
	 * 
	 * @return the hash code
	 */
	int hashCode();

	/**
	 * Returns the creation date.
	 * 
	 * @return the creation date
	 */
	Date getCreated();

	/**
	 * Returns the creator of this Group.
	 * 
	 * @return the creator
	 */
	String getCreator();

	/**
	 * Returns the last-modified date.
	 * 
	 * @return the date and time of last modification
	 */
	Date getLastModified();

	/**
	 * Returns the name of the user who last modified this group.
	 * 
	 * @return the modifier
	 */
	String getModifier();

	/**
	 * The name of the group. This is set in the class constructor.
	 * 
	 * @return the name of the Group
	 */
	String getName();

	/**
	 * Returns the GroupPrincipal that represents this Group.
	 * 
	 * @return the group principal
	 */
	Principal getPrincipal();

	/**
	 * Returns the wiki name.
	 * 
	 * @return the wiki name
	 */
	String getWiki();

	/**
	 * Returns <code>true</code> if a Principal is a member of the group. Specifically, the Principal's
	 * <code>getName()</code> method must return the same value as one of the Principals in the group member
	 * list. The Principal's type does <em>not</em> need to match.
	 * 
	 * @param principal
	 *            the principal about whom membeship status is sought
	 * @return the result of the operation
	 */
	boolean isMember(Principal principal);

	/**
	 * Returns the members of the group as an array of Principal objects.
	 * 
	 * @return the members
	 */
	Principal[] members();

	/**
	 * Removes a Principal from the group.
	 * 
	 * @param user
	 *            the principal to remove
	 * @return <code>true</code> if the operation was successful
	 */
	boolean remove(Principal user1);

	/**
	 * Sets the created date.
	 * 
	 * @param date
	 *            the creation date
	 */
	void setCreated(Date date);

	/**
	 * Sets the creator of this Group.
	 * 
	 * @param creator
	 *            the creator
	 */
	void setCreator(String creator);

	/**
	 * Sets the last-modified date
	 * 
	 * @param date
	 *            the last-modified date
	 */
	void setLastModified(Date date);

	/**
	 * Sets the name of the user who last modified this group.
	 * 
	 * @param modifier
	 *            the modifier
	 */
	void setModifier(String modifier);

	/**
	 * Returns a string representation of the Group.
	 * 
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	String toString();

}