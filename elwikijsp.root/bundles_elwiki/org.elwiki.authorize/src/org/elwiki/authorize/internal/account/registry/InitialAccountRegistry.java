package org.elwiki.authorize.internal.account.registry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.auth.WikiSecurityException;
import org.eclipse.core.runtime.Assert;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public abstract class InitialAccountRegistry extends AbstractAccountRegistry {

	/** For using with JSON. */
	class GroupContent {
		String name;
		String[] users;
		String[] roles;
		String[] permissions;
		String uid;
		String creator;
		String modifier;
	}

	/** For using with JSON. */
	class JsonContent {
		List<GroupContent> groups;
		List<DefaultUserProfile> users;
	}

	// == CODE ================================================================

	// -- implementation of AccountRegistry ------------------------------------

	/** {@inheritDoc} */
	@Override
	public void save(IGroupWiki group, Principal modifier) throws WikiSecurityException {
		// TODO Auto-generated method stub
		Assert.isTrue(false, "Code is not implemented.");
	}

	/** {@inheritDoc} */
	@Override
	public void save(UserProfile profile) throws WikiSecurityException {
		DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
		String uid = profile.getUid();
		String loginName = profile.getLoginName();
		User user = getUserAdmin().getUser(LOGIN_NAME, loginName);

		Date modDate = new Date(System.currentTimeMillis());
		if (user == null) {
			profile.setCreated(modDate);

			// Create new user node
			log.info("Creating new user: " + loginName);
			user = (User) getUserAdmin().createRole(uid, Role.USER);
			if (user == null) {
				throw new WikiSecurityException("Wrong (alredy exists) the UID: " + uid);
			}
			Dictionary<String, Object> properties = user.getProperties();
			properties.put(CREATED, c_format.format(modDate));
			properties.put(UID, uid);
		} else {
			// Update existing user's object...
		}

		profile.setLastModified(modDate);

		Dictionary<String, Object> userProps = user.getProperties();
		userProps.put(LOGIN_NAME, profile.getLoginName());
		userProps.put(FULL_NAME, profile.getFullname());
		String email = profile.getEmail();
		userProps.put(EMAIL, StringUtils.lowerCase(email));
		userProps.put(EMAIL_HUMAN, email);
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

	// -- code of initialization from JSON file -------------------------------

	//:FVK: workaround. (loading Users and Groups information from bundle's JSON file)
	protected void loadInitialAccounts() throws Exception {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		JsonContent data = null;

		URL url = new URL("platform:/plugin/" + AuthorizePluginActivator.PLIGIN_ID + "/users_groups.json");

		try (InputStream inputStream = url.openConnection().getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream)) {
			JsonReader reader = new JsonReader(isr);
			data = gson.fromJson(reader, JsonContent.class);
			//String tmp = builder.setPrettyPrinting().create().toJson(data); // back to JSON.
		}

		/* Placing information of Users in the User Admin service.
		 */
		for (DefaultUserProfile profile : data.users) {
			String login = profile.getLoginName();
			if (getUserAdmin().getUser(LOGIN_NAME, login) == null) {
				try {
					this.save(profile);
				} catch (Exception e) {
					log.error("Profile of [" + login + "] can't be saved.", e);
				}
			}
		}

		/* Placing information of Groups in the User Admin service.
		 */
		for (GroupContent groupData : data.groups) {
			String groupUid = groupData.uid;
			Group group;
			if ((group = (Group) getUserAdmin().getRole(groupUid)) == null) {
				try {
					group = (Group) getUserAdmin().createRole(groupUid, Role.GROUP);
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
					User user = getUserAdmin().getUser(UID, userGUID);
					if (user != null) {
						group.addMember(user);
					}
				}
			}
			if (groupData.roles != null) {
				for (String roleGUID : groupData.roles) {
					Role role = getUserAdmin().getRole(roleGUID);
					if (role != null) {
						group.addMember(role);
					}
				}
			}
		}
	}

}
