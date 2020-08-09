package org.elwiki.authorize.user;

import java.security.Principal;
import java.util.Properties;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user0.UserProfile;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.authorization.user.UserProfile;
//import org.elwiki.api.exceptions.DuplicateUserException;
//import org.elwiki.api.exceptions.InternalWikiException;
//import org.elwiki.api.exceptions.NoRequiredPropertyException;
//import org.elwiki.api.exceptions.NoSuchPrincipalException;
//import org.elwiki.api.exceptions.WikiSecurityException;
import org.osgi.service.useradmin.User;

/**
 * This is a database that gets used if nothing else is available. It does nothing of note - it just
 * mostly throws NoSuchPrincipalExceptions if someone tries to log in.
 */
public class DummyUserDatabase extends AbstractUserDatabase {

	/**
	 * No-op.
	 * 
	 * @throws WikiSecurityException
	 *             never...
	 */
	public void commit() throws WikiSecurityException {
		// No operation
	}

	/**
	 * No-op.
	 * 
	 * @param loginName
	 *            the login name to delete
	 * @throws WikiSecurityException
	 *             never...
	 */
	public void deleteByLoginName(final String loginName) throws WikiSecurityException {
		// No operation
	}

	/**
	 * No-op; always throws <code>NoSuchPrincipalException</code>.
	 * 
	 * @param index
	 *            the name to search for
	 * @return the user profile
	 * @throws NoSuchPrincipalException
	 *             never...
	 */
	public UserProfile findByEmail(final String index) throws NoSuchPrincipalException {
		throw new NoSuchPrincipalException("No user profiles available");
	}

	/**
	 * No-op; always throws <code>NoSuchPrincipalException</code>.
	 * 
	 * @param index
	 *            the name to search for
	 * @return the user profile
	 * @throws NoSuchPrincipalException
	 *             always...
	 */
	public UserProfile findByFullName(String index) throws NoSuchPrincipalException {
		throw new NoSuchPrincipalException("No user profiles available");
	}

	/**
	 * No-op; always throws <code>NoSuchPrincipalException</code>.
	 * 
	 * @param index
	 *            the name to search for
	 * @return the user profile
	 * @throws NoSuchPrincipalException
	 *             always...
	 */
	public UserProfile findByLoginName(final String index) throws NoSuchPrincipalException {
		throw new NoSuchPrincipalException("No user profiles available");
	}

	/**
	 * No-op; always throws <code>NoSuchPrincipalException</code>.
	 * 
	 * @param uid
	 *            the unique identifier to search for
	 * @return the user profile
	 * @throws NoSuchPrincipalException
	 *             always...
	 */
	public UserProfile findByUid(String uid) throws NoSuchPrincipalException {
		throw new NoSuchPrincipalException("No user profiles available");
	}

	/**
	 * No-op; always throws <code>NoSuchPrincipalException</code>.
	 * 
	 * @param index
	 *            the name to search for
	 * @return the user profile
	 * @throws NoSuchPrincipalException
	 *             always...
	 */
	public UserProfile findByWikiName(String index) throws NoSuchPrincipalException {
		throw new NoSuchPrincipalException("No user profiles available");
	}

	/**
	 * No-op.
	 * 
	 * @return a zero-length array
	 * @throws WikiSecurityException
	 *             never...
	 */
	public Principal[] getWikiNames() throws WikiSecurityException {
		return new Principal[0];
	}

	/**
	 * No-op.
	 *
	 * @param engine
	 *            the wiki engine
	 * @param props
	 *            the properties used to initialize the wiki engine
	 * @throws NoRequiredPropertyException
	 *             never...
	 */
	public void initialize( final Engine engine, final Properties props ) {
		// No operation
	}

	/**
	 * No-op; always throws <code>NoSuchPrincipalException</code>.
	 * 
	 * @param loginName
	 *            the login name
	 * @param newName
	 *            the proposed new login name
	 * @throws DuplicateUserException
	 *             never...
	 * @throws WikiSecurityException
	 *             always...
	 */
	public void rename(final String loginName, final String newName) throws NoSuchPrincipalException {
		throw new NoSuchPrincipalException("No user profiles available");
	}

	/**
	 * No-op.
	 * 
	 * @param profile
	 *            the user profile
	 * @throws WikiSecurityException
	 *             never...
	 */
	@Override
	public void save(UserProfile profile) throws WikiSecurityException {
		// No operation
	}

	@Override
	protected void prepareProfile(UserProfile profile, User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserProfile find(User user) throws NoSuchPrincipalException {
		throw new NoSuchPrincipalException("Not in database: " + user);
	}

	/*:FVK:
	@Override
	protected void prepareProfile(UserProfile profile, User user) {
		// No operation
	}
	*/

	@Override
	public String getUserIdByLoginName(String loginName) {
		return null;
	}

	@Override
	public String getUserIdByFullName(String fullName) {
		// No operation
		return null;
	}
	
}