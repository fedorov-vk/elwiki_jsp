package org.elwiki.authorize.internal.account.registry;

import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.event.ElWikiEventsConstants;
import org.apache.wiki.api.exceptions.DuplicateUserException;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.AccountManager;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.auth.WikiSecurityException;
import org.eclipse.core.runtime.Assert;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.api.component.WikiManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.WikiPrincipal;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

//@formatter:off
@Component(
	name = "elwiki.DefaultAccountRegistry",
	service = { AccountRegistry.class, WikiManager.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public final class DefaultAccountRegistry extends InitialAccountRegistry
		implements AccountRegistry, WikiManager, EventHandler {

	protected static final Logger log = Logger.getLogger(DefaultAccountRegistry.class);

	// == CODE ================================================================

	/**
	 * Constructs a new AccountRegistry instance.
	 */
	public DefaultAccountRegistry() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	@Reference
	private UserAdmin userAdminService;

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private Engine m_engine;

	@WikiServiceReference
	private AccountManager accountManager;

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		try {
			/* Reading initialized custom user profiles, groups of users.
			 * (:FVK: workaround.)
			 */
			loadInitialAccounts();
		} catch (Exception e) {
			log.error("Could not load Users & Groups data from JSON file of bundle.", e);
		}
	}

	// -- OSGi service handling ------------------------(end)--

	@Override
	public UserAdmin getUserAdminService() {
		return this.userAdminService;
	}

	// -- implementation of AccountRegistry ------------------------------------

	@Override
	public void delete(IGroupWiki group) throws WikiSecurityException {
		// TODO Auto-generated method stub
		Assert.isTrue(false, "Code is not implemented.");
	}

	@Override
	public void deleteByLoginName(String loginName) throws NoSuchPrincipalException, WikiSecurityException {
		User user = getUserAdminService().getUser(LOGIN_NAME, loginName);
		if (user == null) {
			throw new NoSuchPrincipalException("Not in database: " + loginName);
		}
		if (!getUserAdminService().removeRole(user.getName())) {
			throw new NoSuchPrincipalException("Could't removed from database: " + loginName);
		}
	}

	@Override
	public UserProfile find(User user) throws NoSuchPrincipalException {
		return findByUid((String) user.getProperties().get(UID));
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
		try {
			// convert to lower case if we have to do a case insensitive compare of email.
			String checkedValue = (matchAttribute.equals(EMAIL)) ? StringUtils.lowerCase(value) : value;
			User user = getUserAdminService().getUser(matchAttribute, checkedValue);
			if (user != null) {
				UserProfile profile = newProfile();
				// Retrieve basic attributes
				profile.setUid((String) user.getProperties().get(UID));
				profile.setLoginName((String) user.getProperties().get(LOGIN_NAME));
				profile.setFullname((String) user.getProperties().get(FULL_NAME));
				profile.setPassword((String) user.getProperties().get(PASSWORD));
				profile.setEmail((String) user.getProperties().get(EMAIL_HUMAN));

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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
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
	public UserProfile findByFullName(String index) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(FULL_NAME, index);
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

	@Deprecated
	@Override
	public UserProfile findByWikiName(String index) throws NoSuchPrincipalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile findByUid(String uid) throws NoSuchPrincipalException {
		UserProfile profile = findByAttribute(UID, uid);
		if (profile != null) {
			return profile;
		}
		throw new NoSuchPrincipalException("Not in database: " + uid);
	}

	@Override
	public void rename(String loginName, String newName)
			throws NoSuchPrincipalException, DuplicateUserException, WikiSecurityException {
		// Get the existing user; if not found, throws NoSuchPrincipalException.
		User user = getUserAdminService().getUser(LOGIN_NAME, loginName);
		if (user == null) {
			throw new NoSuchPrincipalException("Not in database: " + loginName);
		}

		// Get user with the proposed name; if found, it's a collision
		User tryUser = getUserAdminService().getUser(LOGIN_NAME, newName);
		if (tryUser != null) {
			throw new DuplicateUserException("security.error.cannot.rename", newName);
		}

		// Change the user with the old LOGIN_NAME attribute.
		DateFormat c_format = new SimpleDateFormat(DATE_FORMAT);
		Date modDate = new Date(System.currentTimeMillis());

		Dictionary<String, Object> props = user.getProperties();
		props.put(LOGIN_NAME, newName);
		props.put(LAST_MODIFIED, c_format.format(modDate));
	}

	@Override
	public IGroupWiki[] getGroups() throws WikiSecurityException {
		//:FVK: workaround -- avoid to code duplication. Due to using 'upper' service - UserAdmin.
		List<IGroupWiki> groups = accountManager.getGroups();
		return groups.toArray(new IGroupWiki[groups.size()]);
	}

	@Override
	public String getUserIdByFullName(String fullName) {
		User user = getUserAdminService().getUser(FULL_NAME, fullName);
		if (user != null) {
			return user.getName();
		}
		return null;
	}

	@Override
	public String getUserIdByLoginName(String loginName) {
		User user = getUserAdminService().getUser(LOGIN_NAME, loginName);
		if (user != null) {
			return user.getName();
		}
		return null;
	}

	@Override
	public Principal[] getWikiNames() throws WikiSecurityException {
		SortedSet<Principal> principals = new TreeSet<Principal>();
		try {
			Role[] roles = getUserAdminService().getRoles(null); // :FVK: workaround. (возможно лучше использовать фильтр, см. UserAdmin описание)
			for (Role role : roles) {
				if (role instanceof User user && !(role instanceof Group)) {
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
	protected void prepareProfile(UserProfile profile, User user) {
		profile.setLoginName((String) user.getProperties().get(LOGIN_NAME));
		profile.setUid(user.getName());
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

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		/*switch (topic) {
			break;
		}*/
	}

}
