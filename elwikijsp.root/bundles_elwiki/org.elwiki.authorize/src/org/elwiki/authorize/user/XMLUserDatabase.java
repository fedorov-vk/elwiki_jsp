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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user0.UserProfile;
import org.apache.wiki.util.Serializer;
import org.apache.wiki.util.TextUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.WikiPrincipal;
import org.osgi.service.useradmin.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
//import org.xml.sax.SAXException;

/**
 * <p>
 * Manages {@link DefaultUserProfile} objects using XML files for persistence. Passwords are hashed
 * using SHA1. User entries are simple <code>&lt;user&gt;</code> elements under the root. User
 * profile properties are attributes of the element. For example:
 * </p>
 * <blockquote><code>
 * &lt;users&gt;<br/>
 * &nbsp;&nbsp;&lt;user loginName="janne" fullName="Janne Jalkanen"<br/> 
 * &nbsp;&nbsp;&nbsp;&nbsp;wikiName="JanneJalkanen" email="janne@ecyrd.com"<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;password="{SHA}457b08e825da547c3b77fbc1ff906a1d00a7daee"/&gt;<br/>
 * &lt;/users&gt;
 * </code></blockquote>
 * <p>
 * In this example, the un-hashed password is <code>myP@5sw0rd</code>. Passwords are hashed without
 * salt.
 * </p>
 */

// FIXME: If the DB is shared across multiple systems, it's possible to lose accounts
//        if two people add new accounts right after each other from different wikis.
public class XMLUserDatabase extends AbstractUserDatabase {

    /** The jspwiki.properties property specifying the file system location of the user database. */
    public static final String  PROP_USERDATABASE = "jspwiki.xmlUserDatabaseFile";

	private static final String DEFAULT_USERDATABASE = "userdatabase.xml";

	private static final String ATTRIBUTES_TAG = "attributes";

	private static final String CREATED = "created";

	private static final String EMAIL = "email";

	private static final String FULL_NAME = "fullName";

	private static final String LOGIN_NAME = "loginName";

	private static final String LAST_MODIFIED = "lastModified";

	private static final String LOCK_EXPIRY = "lockExpiry";

	private static final String PASSWORD = "password";

	private static final String UID = "uid";

	private static final String USER_TAG = "user";

	private static final String WIKI_NAME = "wikiName";

	private static final String DATE_FORMAT = "yyyy.MM.dd 'at' HH:mm:ss:SSS z";

	private Document c_dom = null;

	private File c_file = null;

	/**
	 * Looks up and deletes the first {@link UserProfile} in the user database that matches a profile
	 * having a given login name. If the user database does not contain a user with a matching
	 * attribute, throws a {@link NoSuchPrincipalException}.
	 * 
	 * @param loginName
	 *            the login name of the user profile that shall be deleted.
	 */
	@Override
	public synchronized void deleteByLoginName(String loginName)
			throws NoSuchPrincipalException, WikiSecurityException {
		if (this.c_dom == null) {
			throw new WikiSecurityException("FATAL: database does not exist");
		}

		NodeList users = this.c_dom.getDocumentElement().getElementsByTagName(USER_TAG);
		for (int i = 0; i < users.getLength(); i++) {
			Element user = (Element) users.item(i);
			if (user.getAttribute(LOGIN_NAME).equals(loginName)) {
				this.c_dom.getDocumentElement().removeChild(user);

				// Commit to disk
				saveDOM();
				return;
			}
		}
		throw new NoSuchPrincipalException("Not in database: " + loginName);
	}

	/**
	 * Looks up and returns the first {@link UserProfile}in the user database that matches a profile
	 * having a given e-mail address. If the user database does not contain a user with a matching
	 * attribute, throws a {@link NoSuchPrincipalException}.
	 * 
	 * @param index
	 *            the e-mail address of the desired user profile
	 * @return the user profile
	 * @see org.elwiki.api.authorization.user.IUserDatabase.auth.user.wiki.auth.user.UserDatabase#findByEmail(String)
	 */
	@Override
	public UserProfile findByEmail(String index) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(EMAIL, index);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + index);
	}

	/**
	 * Looks up and returns the first {@link UserProfile}in the user database that matches a profile
	 * having a given full name. If the user database does not contain a user with a matching attribute,
	 * throws a {@link NoSuchPrincipalException}.
	 * 
	 * @param index
	 *            the fill name of the desired user profile
	 * @return the user profile
	 * @see org.elwiki.api.authorization.user.IUserDatabase.auth.user.wiki.auth.user.UserDatabase#findByFullName(java.lang.String)
	 */
	@Override
	public UserProfile findByFullName(String index) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(FULL_NAME, index);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + index);
	}

	/**
	 * Looks up and returns the first {@link UserProfile}in the user database that matches a profile
	 * having a given login name. If the user database does not contain a user with a matching
	 * attribute, throws a {@link NoSuchPrincipalException}.
	 * 
	 * @param index
	 *            the login name of the desired user profile
	 * @return the user profile
	 * @see org.elwiki.api.authorization.user.IUserDatabase.auth.user.wiki.auth.user.UserDatabase#findByLoginName(java.lang.String)
	 */
	@Override
	public UserProfile findByLoginName(String index) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(LOGIN_NAME, index);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserProfile findByUid(String uid) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(UID, uid);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + uid);
	}

	/**
	 * Looks up and returns the first {@link UserProfile}in the user database that matches a profile
	 * having a given wiki name. If the user database does not contain a user with a matching attribute,
	 * throws a {@link NoSuchPrincipalException}.
	 * 
	 * @param index
	 *            the wiki name of the desired user profile
	 * @return the user profile
	 * @see org.elwiki.api.authorization.user.IUserDatabase.auth.user.wiki.auth.user.UserDatabase#findByWikiName(java.lang.String)
	 */
	@Override
	public UserProfile findByWikiName(String index) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(WIKI_NAME, index);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + index);
	}

/* :FVK:
*/

	/**
	 * Returns all WikiNames that are stored in the UserDatabase as an array of WikiPrincipal objects.
	 * If the database does not contain any profiles, this method will return a zero-length array.
	 * 
	 * @return the WikiNames
	 * @throws WikiSecurityException
	 *             In case things fail.
	 */
	@Override
	public Principal[] getWikiNames() throws WikiSecurityException {
		if (this.c_dom == null) {
			throw new IllegalStateException("FATAL: database does not exist");
		}
		SortedSet<Principal> principals = new TreeSet<Principal>();
		NodeList users = this.c_dom.getElementsByTagName(USER_TAG);
		for (int i = 0; i < users.getLength(); i++) {
			Element user = (Element) users.item(i);
			String wikiName = user.getAttribute(WIKI_NAME);
			if (wikiName == null) {
				log.warn("Detected null wiki name in XMLUserDataBase. Check your user database.");
			} else {
				Principal principal = new WikiPrincipal(wikiName, WikiPrincipal.WIKI_NAME);
				principals.add(principal);
			}
		}
		return principals.toArray(new Principal[principals.size()]);
	}

	/**
	 * Initializes the user database location on values from configuration.
	 * The database locates in the work area of wiki.
	 *
	 * @see NoRequiredPropertyException
	 * @see IUserDatabase#initialize(IApplicationSesion)
	 * @throws NoRequiredPropertyException
	 *             if the user database cannot be located, parsed, or opened
	 */
/*	@Override
	public void initialize(IApplicationSession applicationSesion1) throws InternalWikiException {
		IWikiConfiguration wikiConfiguration = applicationSesion1.getWikiConfiguration();

		// Place database file into ElWiki working location.
		IPath rootPath = wikiConfiguration.getWorkDir();
		this.c_file = rootPath.append(DEFAULT_USERDATABASE).toFile();

		log.info("XML user database at " + this.c_file.getAbsolutePath());

		buildDOM();
		sanitizeDOM();
	}
*/
 @Override
    public void initialize( final Engine engine, final Properties props ) throws NoRequiredPropertyException {
        final File defaultFile;
        if( engine.getRootPath() == null ) {
            log.warn( "Cannot identify JSPWiki root path" );
            defaultFile = new File( "WEB-INF/" + DEFAULT_USERDATABASE ).getAbsoluteFile();
        } else {
            defaultFile = new File( engine.getRootPath() + "/WEB-INF/" + DEFAULT_USERDATABASE );
        }

        // Get database file location
        final String file = TextUtil.getStringProperty( engine.getWikiPreferences(), PROP_USERDATABASE, defaultFile.getAbsolutePath() );
        if( file == null ) {
            log.warn( "XML user database property " + PROP_USERDATABASE + " not found; trying " + defaultFile );
            c_file = defaultFile;
        } else {
            c_file = new File( file );
        }

        log.info( "XML user database at " + c_file.getAbsolutePath() );

        buildDOM();
        sanitizeDOM();
    }

	private void buildDOM() {
		// Read DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setExpandEntityReferences(false);
		factory.setIgnoringComments(true);
		factory.setNamespaceAware(false);
		try {
			this.c_dom = factory.newDocumentBuilder().parse(this.c_file);
			log.debug("Database successfully initialized");
			this.c_lastModified = this.c_file.lastModified();
			this.c_lastCheck = System.currentTimeMillis();
		} catch (ParserConfigurationException e) {
			log.error("Configuration error: " + e.getMessage());
		} catch (FileNotFoundException e) {
			log.info("User database not found; creating from scratch...");
		} catch (IOException e) {
			log.error("IO error: " + e.getMessage());
		} catch (Exception e) { //SAXException
			log.error("SAX error: " + e.getMessage());
		}
		if (this.c_dom == null) {
			try {
				//
				//  Create the DOM from scratch
				//
				this.c_dom = factory.newDocumentBuilder().newDocument();
				this.c_dom.appendChild(this.c_dom.createElement("users"));
			} catch (ParserConfigurationException e) {
				log.fatal("Could not create in-memory DOM");
			}
		}
	}

	private void saveDOM() throws WikiSecurityException {
		if (this.c_dom == null) {
			log.fatal("User database doesn't exist in memory.");
		}

		File newFile = new File(this.c_file.getAbsolutePath() + ".new");
		try (BufferedWriter io = new BufferedWriter( //
				new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8));) {

			// Write the file header and document root
			io.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			io.write("<users>\n");

			// Write each profile as a <user> node
			Element root = this.c_dom.getDocumentElement();
			NodeList nodes = root.getElementsByTagName(USER_TAG);
			for (int i = 0; i < nodes.getLength(); i++) {
				Element user = (Element) nodes.item(i);
				io.write("    <" + USER_TAG + " ");
				io.write(UID);
				io.write("=\"" + user.getAttribute(UID) + "\" ");
				io.write(LOGIN_NAME);
				io.write("=\"" + user.getAttribute(LOGIN_NAME) + "\" ");
				io.write(WIKI_NAME);
				io.write("=\"" + user.getAttribute(WIKI_NAME) + "\" ");
				io.write(FULL_NAME);
				io.write("=\"" + user.getAttribute(FULL_NAME) + "\" ");
				io.write(EMAIL);
				io.write("=\"" + user.getAttribute(EMAIL) + "\" ");
				io.write(PASSWORD);
				io.write("=\"" + user.getAttribute(PASSWORD) + "\" ");
				io.write(CREATED);
				io.write("=\"" + user.getAttribute(CREATED) + "\" ");
				io.write(LAST_MODIFIED);
				io.write("=\"" + user.getAttribute(LAST_MODIFIED) + "\" ");
				io.write(LOCK_EXPIRY);
				io.write("=\"" + user.getAttribute(LOCK_EXPIRY) + "\" ");
				io.write(">");
				NodeList attributes = user.getElementsByTagName(ATTRIBUTES_TAG);
				for (int j = 0; j < attributes.getLength(); j++) {
					Element attribute = (Element) attributes.item(j);
					String value = extractText(attribute);
					io.write("\n        <" + ATTRIBUTES_TAG + ">");
					io.write(value);
					io.write("</" + ATTRIBUTES_TAG + ">");
				}
				io.write("\n    </" + USER_TAG + ">\n");
			}
			io.write("</users>");
			io.close();
		} catch (IOException e) {
			throw new WikiSecurityException(e.getLocalizedMessage(), e);
		}

		// Copy new file over old version
		File backup = new File(this.c_file.getAbsolutePath() + ".old");
		if (backup.exists()) {
			if (!backup.delete()) {
				log.error("Could not delete old user database backup: " + backup);
			}
		}
		if (!this.c_file.renameTo(backup)) {
			log.error("Could not create user database backup: " + backup);
		}
		if (!newFile.renameTo(this.c_file)) {
			log.error("Could not save database: " + backup + " restoring backup.");
			if (!backup.renameTo(this.c_file)) {
				log.error("Restore failed. Check the file permissions.");
			}
			log.error("Could not save database: " + this.c_file + ". Check the file permissions");
		}
	}

	private long c_lastCheck = 0;
	private long c_lastModified = 0;

	private void checkForRefresh() {
		final long time = System.currentTimeMillis();
		if (time - this.c_lastCheck > 60 * 1000L) {
			long lastModified = this.c_file.lastModified();

			if (lastModified > this.c_lastModified) {
				buildDOM();
			}
		}
	}

	/**
	 * @see org.elwiki.api.authorization.user.IUserDatabase.auth.user.wiki.auth.user.UserDatabase#rename(String,
	 *      String)
	 */
	@Override
	public synchronized void rename(String loginName, String newName)
			throws NoSuchPrincipalException, DuplicateUserException, WikiSecurityException {
		if (this.c_dom == null) {
			log.fatal("Could not rename profile '" + loginName + "'; database does not exist");
			throw new IllegalStateException("FATAL: database does not exist");
		}
		checkForRefresh();

		// Get the existing user; if not found, throws NoSuchPrincipalException
		UserProfile profile = findByLoginName(loginName);

		// Get user with the proposed name; if found, it's a collision
		try {
			UserProfile otherProfile = findByLoginName(newName);
			if (otherProfile != null) {
				throw new DuplicateUserException("security.error.cannot.rename", newName);
			}
		} catch (NoSuchPrincipalException e) {
			// Good! That means it's safe to save using the new name
		}

		// Find the user with the old login id attribute, and change it
		NodeList users = this.c_dom.getElementsByTagName(USER_TAG);
		for (int i = 0; i < users.getLength(); i++) {
			Element user = (Element) users.item(i);
			if (user.getAttribute(LOGIN_NAME).equals(loginName)) {
				DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
				Date modDate = new Date(System.currentTimeMillis());
				setAttribute(user, LOGIN_NAME, newName);
				setAttribute(user, LAST_MODIFIED, c_format.format(modDate));
				profile.setLoginName(newName);
				profile.setLastModified(modDate);
				break;
			}
		}

		// Commit to disk
		saveDOM();
	}

	/**
	 * Saves a {@link UserProfile}to the user database, overwriting the existing profile if it exists.
	 * The user name under which the profile should be saved is returned by the supplied profile's
	 * {@link UserProfile#getLoginName()}method.
	 * 
	 * @param profile
	 *            the user profile to save
	 * @throws WikiSecurityException
	 *             if the profile cannot be saved
	 */
	@Override
	public synchronized void save(UserProfile profile) throws WikiSecurityException {
		if (this.c_dom == null) {
			log.fatal("Could not save profile " + profile + " database does not exist");
			throw new IllegalStateException("FATAL: database does not exist");
		}

		checkForRefresh();

		DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
		String index = profile.getLoginName();
		NodeList users = this.c_dom.getElementsByTagName(USER_TAG);
		Element user = null;
		for (int i = 0; i < users.getLength(); i++) {
			Element currentUser = (Element) users.item(i);
			if (currentUser.getAttribute(LOGIN_NAME).equals(index)) {
				user = currentUser;
				break;
			}
		}

		boolean isNew = false;

		Date modDate = new Date(System.currentTimeMillis());
		if (user == null) {
			// Create new user node
			profile.setCreated(modDate);
			log.info("Creating new user: " + index);
			user = this.c_dom.createElement(USER_TAG);
			this.c_dom.getDocumentElement().appendChild(user);
			setAttribute(user, CREATED, c_format.format(profile.getCreated()));
			isNew = true;
		} else {
			// To update existing user node, delete old attributes first...
			NodeList attributes = user.getElementsByTagName(ATTRIBUTES_TAG);
			for (int i = 0; i < attributes.getLength(); i++) {
				user.removeChild(attributes.item(i));
			}
		}

		setAttribute(user, UID, profile.getUid());
		setAttribute(user, LAST_MODIFIED, c_format.format(modDate));
		setAttribute(user, LOGIN_NAME, profile.getLoginName());
		setAttribute(user, FULL_NAME, profile.getFullname());
		setAttribute(user, WIKI_NAME, profile.getWikiName());
		setAttribute(user, EMAIL, profile.getEmail());
		Date lockExpiry = profile.getLockExpiry();
		setAttribute(user, LOCK_EXPIRY, lockExpiry == null ? "" : c_format.format(lockExpiry));

		// Hash and save the new password if it's different from old one
		String newPassword = profile.getPassword();
		if (newPassword.length() != 0) {
			String oldPassword = user.getAttribute(PASSWORD);
			if (!oldPassword.equals(newPassword)) {
				setAttribute(user, PASSWORD, getHash(newPassword));
			}
		}

		// Save the attributes as as Base64 string
		if (profile.getAttributes().size() > 0) {
			try {
				String encodedAttributes = Serializer.serializeToBase64(profile.getAttributes());
				Element attributes = this.c_dom.createElement(ATTRIBUTES_TAG);
				user.appendChild(attributes);
				Text value = this.c_dom.createTextNode(encodedAttributes);
				attributes.appendChild(value);
			} catch (IOException e) {
				throw new WikiSecurityException("Could not save user profile attribute. Reason: " + e.getMessage(), e);
			}
		}

		// Set the profile timestamps
		if (isNew) {
			profile.setCreated(modDate);
		}
		profile.setLastModified(modDate);

		// Commit to disk
		saveDOM();
	}

	/**
	 * Private method that returns the first {@link UserProfile}matching a &lt;user&gt; element's
	 * supplied attribute. This method will also set the UID if it has not yet been set.
	 * 
	 * @param matchAttribute
	 * @param index
	 * @return the profile, or <code>null</code> if not found
	 */
	private UserProfile findByAttribute(String matchAttribute, String index1) {
		String index = index1;
		if (this.c_dom == null) {
			throw new IllegalStateException("FATAL: database does not exist");
		}

		checkForRefresh();

		NodeList users = this.c_dom.getElementsByTagName(USER_TAG);

		if (users == null)
			return null;

		// check if we have to do a case insensitive compare
		boolean caseSensitiveCompare = true;
		if (matchAttribute.equals(EMAIL)) {
			caseSensitiveCompare = false;
		}

		for (int i = 0; i < users.getLength(); i++) {
			Element user = (Element) users.item(i);
			String userAttribute = user.getAttribute(matchAttribute);
			if (!caseSensitiveCompare) {
				userAttribute = StringUtils.lowerCase(userAttribute);
				index = StringUtils.lowerCase(index);
			}
			if (userAttribute.equals(index)) {
				UserProfile profile = newProfile();

				// Parse basic attributes
				profile.setUid(user.getAttribute(UID));
				if (profile.getUid().length() == 0) {
					profile.setUid(generateUid());
				}
				profile.setLoginName(user.getAttribute(LOGIN_NAME));
				profile.setFullname(user.getAttribute(FULL_NAME));
				profile.setPassword(user.getAttribute(PASSWORD));
				profile.setEmail(user.getAttribute(EMAIL));

				// Get created/modified timestamps
				String created = user.getAttribute(CREATED);
				String modified = user.getAttribute(LAST_MODIFIED);
				profile.setCreated(parseDate(profile, created));
				profile.setLastModified(parseDate(profile, modified));

				// Is the profile locked?
				String lockExpiry = user.getAttribute(LOCK_EXPIRY);
				if (lockExpiry == null || lockExpiry.length() == 0) {
					profile.setLockExpiry(null);
				} else {
					profile.setLockExpiry(new Date(Long.parseLong(lockExpiry)));
				}

				// Extract all of the user's attributes (should only be one attributes tag, but you never know!)
				NodeList attributes = user.getElementsByTagName(ATTRIBUTES_TAG);
				for (int j = 0; j < attributes.getLength(); j++) {
					Element attribute = (Element) attributes.item(j);
					String serializedMap = extractText(attribute);
					try {
						Map<String, ? extends Serializable> map = Serializer.deserializeFromBase64(serializedMap);
						profile.getAttributes().putAll(map);
					} catch (IOException e) {
						log.error("Could not parse user profile attributes!", e);
					}
				}

				return profile;
			}
		}
		return null;
	}

	/**
	 * Extracts all of the text nodes that are immediate children of an Element.
	 * 
	 * @param element
	 *            the base element
	 * @return the text nodes that are immediate children of the base element, concatenated together
	 */
	private String extractText(Element element) {
		String text = "";
		if (element.getChildNodes().getLength() > 0) {
			NodeList children = element.getChildNodes();
			for (int k = 0; k < children.getLength(); k++) {
				Node child = children.item(k);
				if (child.getNodeType() == Node.TEXT_NODE) {
					text = text + ((Text) child).getData();
				}
			}
		}
		return text;
	}

	/**
	 * Tries to parse a date using the default format - then, for backwards compatibility reasons, tries
	 * the platform default.
	 * 
	 * @param profile
	 * @param date
	 * @return A parsed date, or null, if both parse attempts fail.
	 */
	private Date parseDate(UserProfile profile, String date) {
		try {
			DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
			return c_format.parse(date);
		} catch (ParseException e) {
			try {
				return DateFormat.getDateTimeInstance().parse(date);
			} catch (ParseException e2) {
				log.warn("Could not parse 'created' or 'lastModified' " + "attribute for " + " profile '"
						+ profile.getLoginName() + "'." + " It may have been tampered with.");
			}
		}
		return null;
	}

	/**
	 * After loading the DOM, this method sanity-checks the dates in the DOM and makes sure they are
	 * formatted properly. This is sort-of hacky, but it should work.
	 */
	private void sanitizeDOM() {
		if (this.c_dom == null) {
			throw new IllegalStateException("FATAL: database does not exist");
		}

		NodeList users = this.c_dom.getElementsByTagName(USER_TAG);
		for (int i = 0; i < users.getLength(); i++) {
			Element user = (Element) users.item(i);

			// Sanitize UID (and generate a new one if one does not exist)
			String uid = user.getAttribute(UID).trim();
			if (uid == null || uid.length() == 0 || "-1".equals(uid)) {
				uid = String.valueOf(generateUid());
				user.setAttribute(UID, uid);
			}

			// Sanitize dates
			String loginName = user.getAttribute(LOGIN_NAME);
			String created = user.getAttribute(CREATED);
			String modified = user.getAttribute(LAST_MODIFIED);
			DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
			try {
				created = c_format.format(c_format.parse(created));
				modified = c_format.format(c_format.parse(modified));
				user.setAttribute(CREATED, created);
				user.setAttribute(LAST_MODIFIED, modified);
			} catch (ParseException e) {
				try {
					created = c_format.format(DateFormat.getDateTimeInstance().parse(created));
					modified = c_format.format(DateFormat.getDateTimeInstance().parse(modified));
					user.setAttribute(CREATED, created);
					user.setAttribute(LAST_MODIFIED, modified);
				} catch (ParseException e2) {
					log.warn("Could not parse 'created' or 'lastModified' attribute for profile '" + loginName + "'."
							+ " It may have been tampered with.");
				}
			}
		}
	}

	/**
	 * Private method that sets an attribute value for a supplied DOM element.
	 * 
	 * @param element
	 *            the element whose attribute is to be set
	 * @param attribute
	 *            the name of the attribute to set
	 * @param value
	 *            the desired attribute value
	 */
	private void setAttribute(Element element, String attribute, String value) {
		if (value != null) {
			element.setAttribute(attribute, value);
		}
	}

	@Override
	public UserProfile find(User user) throws NoSuchPrincipalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void prepareProfile(UserProfile profile, User user) {
		// No operation
	}

	@Override
	public String getUserIdByLoginName(String loginName) {
		// No operation
		return null;
	}

	@Override
	public String getUserIdByFullName(String fullName) {
		// No operation
		return null;
	}
	
}
