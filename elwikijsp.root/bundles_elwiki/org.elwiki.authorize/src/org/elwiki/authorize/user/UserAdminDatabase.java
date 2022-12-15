package org.elwiki.authorize.user;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user0.UserProfile;
import org.eclipse.jdt.annotation.NonNull;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.authorization.user.UserProfile;
//import org.elwiki.api.exceptions.DuplicateUserException;
//import org.elwiki.api.exceptions.InternalWikiException;
//import org.elwiki.api.exceptions.NoSuchPrincipalException;
//import org.elwiki.api.exceptions.WikiSecurityException;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.data.authorize.WikiPrincipal;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class UserAdminDatabase extends AbstractUserDatabase {

	private UserAdmin userAdmin;

	@Override
	public void initialize(Engine engine, Properties props) throws NoRequiredPropertyException, WikiSecurityException {
		BundleContext context = AuthorizePluginActivator.getDefault().getBundle().getBundleContext();
		ServiceReference<?> ref = context.getServiceReference(UserAdmin.class.getName());
		if (ref != null) {
			this.userAdmin = (UserAdmin) context.getService(ref);
		}

		// Read customized user profiles. (:FVK: workaround.)
		try {
			loadUsersDataBase();
		} catch (Exception e) {
			log.error("Could not load users JSON file from bundle.", e);
		}

		// Read customized user's groups. (:FVK: workaround.)
		try {
			loadGroupsDataBase();
		} catch (Exception e) {
			log.error("Could not load groups JSON file from bundle.", e);
		}
	}

	//:FVK: workaround. (JSON loading from bundle's file)
	private void loadUsersDataBase() throws Exception {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		Type collectionType = new TypeToken<List<DefaultUserProfile>>() {
		}.getType();
		List<DefaultUserProfile> data = null;

		URL url = new URL("platform:/plugin/" + AuthorizePluginActivator.PLIGIN_ID + "/users.json");
		InputStream inputStream = url.openConnection().getInputStream();
		try (InputStreamReader isr = new InputStreamReader(inputStream)) {
			JsonReader reader = new JsonReader(isr);
			data = gson.fromJson(reader, collectionType);
		}

		for (DefaultUserProfile profile : data) {
			String login = profile.getLoginName();
			if (this.userAdmin.getUser(LOGIN_NAME, login) == null) {
				try {
					this.save(profile);
				} catch (Exception e) {
					log.error("Profile of [" + login + "] can't be saved.", e);
				}
			}
		}
	}

	//:FVK: workaround. (JSON loading from bundle's file)
	private void loadGroupsDataBase()  throws Exception {
		Type collectionType = new TypeToken<List<GroupContent>>() {
		}.getType();
		List<GroupContent> data = null;

		URL url = new URL("platform:/plugin/" + AuthorizePluginActivator.PLIGIN_ID + "/groups.json");
		InputStream inputStream = url.openConnection().getInputStream();
		Gson gson = new GsonBuilder().create();
		try (InputStreamReader isr = new InputStreamReader(inputStream)) {
			JsonReader reader = new JsonReader(isr);
			data = gson.fromJson(reader, collectionType);
		}

		for (GroupContent groupData : data) {
			String groupUid = groupData.uid;
			Group group;
			if ((group = (Group) this.userAdmin.getRole(groupUid)) == null) {
				try {
					group = (Group) this.userAdmin.createRole(groupUid, Role.GROUP);
					Dictionary<String, Object> groupProps = group.getProperties();
					groupProps.put(GROUP_NAME, groupData.name);
					groupProps.put(GROUP_PERMISSIONS, gson.toJson(groupData.permissions));
					groupProps.put(GROUP_CREATOR, groupData.creator);
					groupProps.put(GROUP_MODIFIER, groupData.modifier);
					
					DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
					Date modifyDate = new Date(System.currentTimeMillis());
					groupProps.put(CREATED, c_format.format(modifyDate));
					groupProps.put(LAST_MODIFIED, c_format.format(modifyDate));
				} catch (Exception e) {
					// TODO: :FVK: workaround.
					System.out.println("Could not make group [" + groupData.name + "].\n" + e.getMessage());
					return;
				}
			}
			if (groupData.users != null) {
				for (String userGUID : groupData.users) {
					User user = this.userAdmin.getUser(UID, userGUID);
					if (user != null) {
						group.addMember(user);
					}
				}
			}
		}
	}

	@Override
	public void deleteByLoginName(String loginName) throws NoSuchPrincipalException, WikiSecurityException {
		User user = this.userAdmin.getUser(LOGIN_NAME, loginName);
		if (user == null) {
			throw new NoSuchPrincipalException("Not in database: " + loginName);
		}
		if (!this.userAdmin.removeRole(user.getName())) {
			throw new NoSuchPrincipalException("Could't removed from database: " + loginName);
		}
	}

	@Override
	public Principal[] getWikiNames() throws WikiSecurityException {
		SortedSet<Principal> principals = new TreeSet<Principal>();
		try {
			Role[] roles = this.userAdmin.getRoles(null); // :FVK: workaround. (возможно лучше использовать фильтр)
			for (Role role : roles) {
				if (role instanceof User && !(role instanceof Group)) {
					User user = (User) role;
					String login = (String) user.getProperties().get(LOGIN_NAME);
					if (login != null) {
						Principal principal = new WikiPrincipal(login, WikiPrincipal.WIKI_NAME);
						principals.add(principal);
					}
				}

			}
		} catch (InvalidSyntaxException e) {
			// ignored (workaround).
		}

		return principals.toArray(new Principal[principals.size()]);
	}

	@Override
	public UserProfile find(User user) throws NoSuchPrincipalException {
		return findByUid((String) user.getProperties().get(UID));
	}

	@Override
	public UserProfile findByEmail(String index) throws NoSuchPrincipalException {
		UserProfile profile = null;
		profile = findByAttribute(EMAIL, index);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + index);
	}

	@Override
	public UserProfile findByLoginName(String loginName) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(LOGIN_NAME, loginName);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + loginName);
	}

	@Override
	public UserProfile findByUid(String uid) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(UID, uid);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + uid);
	}

	@Deprecated
	@Override
	public UserProfile findByWikiName(String index) throws NoSuchPrincipalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile findByFullName(String index) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(FULL_NAME, index);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + index);
	}

	@Override
	public void rename(String loginName, String newName)
			throws NoSuchPrincipalException, DuplicateUserException, WikiSecurityException {
		// Get the existing user; if not found, throws NoSuchPrincipalException.
		User user = this.userAdmin.getUser(LOGIN_NAME, loginName);
		if (user == null) {
			throw new NoSuchPrincipalException("Not in database: " + loginName);
		}

		// Get user with the proposed name; if found, it's a collision
		User tryUser = this.userAdmin.getUser(LOGIN_NAME, newName);
		if (tryUser != null) {
			throw new DuplicateUserException("security.error.cannot.rename", newName);
		}

		// Change the user with the old LOGIN_NAME attribute.
		DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
		Date modDate = new Date(System.currentTimeMillis());
		@SuppressWarnings("unchecked")
		Dictionary<String, Object> props = user.getProperties();
		props.put(LOGIN_NAME, newName);
		props.put(LAST_MODIFIED, c_format.format(modDate));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void save(UserProfile profile) throws WikiSecurityException {
		DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
		String uid = profile.getUid();
		String loginName = profile.getLoginName();
		User user = this.userAdmin.getUser(LOGIN_NAME, loginName);

		Date modDate = new Date(System.currentTimeMillis());
		if (user == null) {
			profile.setCreated(modDate);

			// Create new user node
			log.info("Creating new user: " + loginName);
			user = (User) this.userAdmin.createRole(uid, Role.USER);
			if (user == null) {
				throw new WikiSecurityException("Wrong (alredy exists) the UID: " + uid);
			}
			Dictionary properties = user.getProperties();
			properties.put(CREATED, c_format.format(modDate));
			properties.put(UID, uid);
		} else {
			// Update existing user's object...
		}

		profile.setLastModified(modDate);

		Dictionary userProps = user.getProperties();
		userProps.put(LOGIN_NAME, profile.getLoginName());
		userProps.put(FULL_NAME, profile.getFullname());
		userProps.put(EMAIL, profile.getEmail());
		userProps.put(LAST_MODIFIED, c_format.format(modDate));
		Date lockExpiry = profile.getLockExpiry();
		userProps.put(LOCK_EXPIRY, lockExpiry == null ? "" : c_format.format(lockExpiry));

		// Hash and save the new password if it's different from old one. (:FVK: при повторном сохранении - пароль портится?)
		String newPassword = profile.getPassword();
		if (newPassword.length() != 0) {
			newPassword = getHash(newPassword);
			String oldPassword = (String) userProps.get(PASSWORD);
			if (!newPassword.equals(oldPassword)) { //TODO: сравнить захэшированные пароли...
				userProps.put(PASSWORD, newPassword);
			}
		}
	}

	/**
	 * Private method that returns the first {@link UserProfile}matching a &lt;user&gt; element's
	 * supplied attribute. This method will also set the UID if it has not yet been set.
	 * 
	 * @param matchAttribute
	 * @param value
	 * @return the profile, or <code>null</code> if not found
	 * @throws InvalidSyntaxException
	 * @throws NumberFormatException
	 */
	private UserProfile findByAttribute(String matchAttribute, String value) {
		String expectedValue = value;

		// check if we have to do a case insensitive compare
		boolean caseSensitiveCompare = true;
		if (matchAttribute.equals(EMAIL)) {
			caseSensitiveCompare = false;
		}

		try {
			for (Role role : this.userAdmin.getRoles(null)) {
				if (role instanceof User && !(role instanceof Group)) {
					User user = (User) role;
					String userAttribute = (String) user.getProperties().get(matchAttribute);
					if (userAttribute == null) {
						break;
					}
					if (!caseSensitiveCompare) {
						userAttribute = StringUtils.lowerCase(userAttribute);
						expectedValue = StringUtils.lowerCase(expectedValue);
					}
					if (userAttribute.equals(expectedValue)) {
						UserProfile profile = newProfile();
						// Retrieve basic attributes
						profile.setUid((String) user.getProperties().get(UID));
						profile.setLoginName((String) user.getProperties().get(LOGIN_NAME));
						profile.setFullname((String) user.getProperties().get(FULL_NAME));
						profile.setPassword((String) user.getProperties().get(PASSWORD));
						profile.setEmail((String) user.getProperties().get(EMAIL));

						// Get created/modified timestamps
						String created = (String) user.getProperties().get(CREATED);
						String modified = (String) user.getProperties().get(LAST_MODIFIED);
						profile.setCreated(parseDate(profile, created));
						profile.setLastModified(parseDate(profile, modified));

						// Is the profile locked?
						String lockExpiry = (String) user.getProperties().get(LOCK_EXPIRY);
						if (lockExpiry == null || lockExpiry.length() == 0) {
							profile.setLockExpiry(null);
						} else {
							profile.setLockExpiry(new Date(Long.parseLong(lockExpiry)));
						}

						return profile;
					}
				}
			}
		} catch (NumberFormatException | InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Tries to parse a date using the default format - then, for backwards compatibility reasons,
	 * tries the platform default.
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

	@Override
	protected void prepareProfile(UserProfile profile, User user) {
		profile.setLoginName((String) user.getProperties().get(LOGIN_NAME));
		profile.setUid(user.getName());
	}

	@Override
	public String getUserIdByLoginName(String loginName) {
		User user = this.userAdmin.getUser(LOGIN_NAME, loginName);
		if (user != null) {
			return user.getName();
		}
		return null;
	}

	@Override
	public String getUserIdByFullName(String fullName) {
		User user = this.userAdmin.getUser(FULL_NAME, fullName);
		if (user != null) {
			return user.getName();
		}
		return null;
	}

}
