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
package org.elwiki.authorize.internal.account.registry;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.util.CryptoUtil;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.data.authorize.WikiPrincipal;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

/**
 * Abstract AccountRegistry class that provides convenience methods for finding profiles, building Principal collections and hashing passwords.
 *
 * @since 2.3
 */
public abstract class AbstractAccountRegistry implements AccountRegistry {

	protected static final Logger log = Logger.getLogger(AbstractAccountRegistry.class);

	protected abstract UserAdmin getUserAdmin();

	/** {@inheritDoc} */
	@Override
	public UserProfile find(final String index) throws NoSuchPrincipalException {
		UserProfile profile = null;

        // Try finding by UID
        try {
            profile = findByUid( index );
        } catch( final NoSuchPrincipalException e ) {
        }
        if( profile != null ) {
            return profile;
        }
		
        // Try finding by full name
        try {
            profile = findByFullName( index );
        } catch( final NoSuchPrincipalException e ) {
        }
        if( profile != null ) {
            return profile;
        }

        // Try finding by wiki name
        try {
            profile = findByWikiName( index );
        } catch( final NoSuchPrincipalException e ) {
        }
        if( profile != null ) {
            return profile;
        }

        // Try finding by login name
        try {
            profile = findByLoginName( index );
        } catch( final NoSuchPrincipalException e ) {
        }
        if( profile != null ) {
            return profile;
        }

        /*:FVK: workaround
		profile = newProfile();
		// Retrieve basic attributes
		profile.setUid((String) "52345-513452345-5234652");
		profile.setLoginName((String) "vfedorov");
		profile.setFullname((String) "Victor Fedorov");
		profile.setPassword((String) "123123");
		profile.setEmail((String) "ru@ru.ru");
		if(1==(2-1))
		return profile;
         */
        
        throw new NoSuchPrincipalException( "Not in database: " + index );
    }

    /**
     * {@inheritDoc}
     * @see AccountRegistry#findByEmail(java.lang.String)
     */
    @Override
    public abstract UserProfile findByEmail( String index ) throws NoSuchPrincipalException;

    /**
     * {@inheritDoc}
     * @see AccountRegistry#findByFullName(java.lang.String)
     */
    @Override
    public abstract UserProfile findByFullName( String index ) throws NoSuchPrincipalException;

    /**
     * {@inheritDoc}
     * @see AccountRegistry#findByLoginName(java.lang.String)
     */
    @Override
    public abstract UserProfile findByLoginName( String index ) throws NoSuchPrincipalException;

    /**
     * {@inheritDoc}
     * @see AccountRegistry#findByWikiName(java.lang.String)
     */
    @Override
    public abstract UserProfile findByWikiName( String index ) throws NoSuchPrincipalException;

	/**
	 * <p>
	 * Looks up the Principals representing a user from the user database. These are defined as a set of
	 * WikiPrincipals manufactured from the login name, full name, and wiki name. If the user database
	 * does not contain a user with the supplied identifier, throws a {@link NoSuchPrincipalException}.
	 * </p>
	 * <p>
	 * When this method creates WikiPrincipals, the Principal containing the user's full name is marked
	 * as containing the common name (see
	 * {@link org.elwiki.data.authorize.core.auth.wiki.auth.WikiPrincipal#WikiPrincipal(String, String)}).
	 * 
	 * @param identifier the name of the principal to retrieve; this corresponds to value returned by
	 *                   the user profile's {@link UserProfile#getLoginName()}method.
	 * @return the array of Principals representing the user
	 * @see AccountRegistry#getPrincipals(java.lang.String)
	 * @throws NoSuchPrincipalException {@inheritDoc}
	 */
	@Deprecated //:FVK: - переместить в профиль, или удалить вообще.
	@Override
	public Principal[] getPrincipals(final String identifier) throws NoSuchPrincipalException {
		try {
			UserProfile profile = findByLoginName(identifier);
			ArrayList<Principal> principals = new ArrayList<>();
			if (profile.getLoginName() != null && profile.getLoginName().length() > 0) {
				principals.add(new WikiPrincipal(profile.getLoginName(), WikiPrincipal.LOGIN_UID));
			}
			if (profile.getFullname() != null && profile.getFullname().length() > 0) {
				principals.add(new WikiPrincipal(profile.getFullname(), WikiPrincipal.FULL_NAME));
			}
			if (profile.getWikiName() != null && profile.getWikiName().length() > 0) {
				principals.add(new WikiPrincipal(profile.getWikiName(), WikiPrincipal.WIKI_NAME));
			}
			return principals.toArray(new Principal[principals.size()]);
		} catch (NoSuchPrincipalException e) {
			throw e;
		}
	}

	protected abstract void prepareProfile(UserProfile profile, User user);

    /**
     * {@inheritDoc}
     *
     * @see AccountRegistry#initialize(org.apache.wiki.api.core.Engine, java.util.Properties)
     */
	/*:FVK:
    @Override
    public abstract void initialize( Engine engine, Properties props ) throws NoRequiredPropertyException, WikiSecurityException;
    */

	/**
	 * Factory method that instantiates a new DefaultUserProfile with a new, distinct unique identifier.
	 *
	 * @return A new, empty profile.
	 */
	@Override
	public UserProfile newProfile() {
		UserProfile profile = DefaultUserProfile.newProfile();
		profile.setUid(generateUid());
		return profile;
	}

	@Override
	public UserProfile newProfile(User user) {
		UserProfile profile = newProfile();
		if (user != null) {
			prepareProfile(profile, user);
		}
		return profile;
	}

	/**
	 * Validates the password for a given user. If the user does not exist in the user database, this method
	 * always returns <code>false</code>. If the user exists, the supplied password is compared to the stored
	 * password. Note that if the stored password's value starts with <code>{SHA}</code>, the supplied
	 * password is hashed prior to the comparison.
	 * 
	 * @param loginName
	 *            the user's login name
	 * @param password
	 *            the user's password (obtained from user input, e.g., a web form)
	 * @return <code>true</code> if the supplied user password matches the stored password
	 * @see AccountRegistry#validatePassword(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean validatePassword(UserProfile profile, String password) {
		try {
			String storedPassword = profile.getPassword();
			return CryptoUtil.verifySaltedPassword(password.getBytes("UTF-8"), storedPassword);
		} catch (NoSuchAlgorithmException e) {
			log.error("Unsupported algorithm: " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			log.fatal("You do not have UTF-8!?!");
		}
		return false;
	}

	@Override
    public boolean validatePassword( String loginName, String password ) {
		boolean status = false;
		try {
			final UserProfile profile = findByLoginName( loginName );
			status = validatePassword(profile, password);
		} catch (NoSuchPrincipalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Generates a new random user identifier (uid) that is guaranteed to be unique.
	 * 
	 * @return A random, unique UID.
	 */
	@NonNull
	protected String generateUid() {
		// Keep generating UUIDs until we find one that doesn't collide
		String uid = null;
		boolean collision;

		do {
			uid = UUID.randomUUID().toString();
			collision = true;
			try {
				this.findByUid(uid);
			} catch (NoSuchPrincipalException e) {
				collision = false;
			}
		} while (collision || uid == null);
		return uid;
	}

	/**
	 * Private method that calculates the salted SHA-1 hash of a given <code>String</code>. Note that as of
	 * JSPWiki 2.8, this method calculates a <em>salted</em> hash rather than a plain hash.
	 * 
	 * @param text
	 *            the text to hash
	 * @return the result hash
	 */
	protected String getHash(String text) {
		String hash = null;
		try {
			hash = CryptoUtil.getSaltedPassword(text.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			log.error("Error creating salted SHA password hash:" + e.getMessage());
			hash = text;
		} catch (UnsupportedEncodingException e) {
			log.fatal("You do not have UTF-8!?!");
		} catch (Exception e) {
			log.fatal("Unsupported exception: " + e.getLocalizedMessage());
		}
		return hash;
	}

	/**
	 * Parses a long integer from a supplied string, or returns 0 if not parsable.
	 * 
	 * @param value
	 *            the string to parse
	 * @return the value parsed
	 */
	protected long parseLong(String value) {
		if (value == null || value.length() == 0) {
			return 0;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}
