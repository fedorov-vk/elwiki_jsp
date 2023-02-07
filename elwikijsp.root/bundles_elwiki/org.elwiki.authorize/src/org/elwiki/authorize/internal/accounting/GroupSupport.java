package org.elwiki.authorize.internal.accounting;

import java.security.Principal;

import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.event.WikiEvent;
import org.apache.wiki.api.event.WikiSecurityEvent;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user0.UserProfile;
import org.apache.wiki.ui.InputValidator;
import org.elwiki.api.authorization.IGroupManager;
import org.elwiki.data.authorize.WikiPrincipal;
import org.osgi.service.useradmin.Group;

public abstract class GroupSupport extends UserSupport {

	/**
	 * Checks if a String is blank or a restricted Group name, and if it is, appends an error to the
	 * WikiSession's message list.
	 * 
	 * @param context
	 *                the wiki context.
	 * @param name
	 *                the Group name to test.
	 * @throws WikiSecurityException
	 *                               if <code>session</code> is <code>null</code> or the Group name
	 *                               is illegal.
	 * @see Group#RESTRICTED_GROUPNAMES
	 */
	protected void checkGroupName(WikiContext context, String name) throws WikiSecurityException {
		//TODO: groups cannot have the same name as a user

		// Name cannot be null
		InputValidator validator = new InputValidator(IGroupManager.MESSAGES_KEY, context);
		validator.validateNotNull(name, "Group name");

		// Name cannot be one of the restricted names either
		//:FVK:		if (ArrayUtils.contains(Group.RESTRICTED_GROUPNAMES, name)) {
		//			throw new WikiSecurityException("The group name '" + name + "' is illegal. Choose another.");
		//		}
	}


	/**
	 * Listens for {@link org.elwiki.api.event.wiki.event.WikiSecurityEvent#PROFILE_NAME_CHANGED}
	 * events. If a user profile's name changes, each group is inspected. If an entry contains a
	 * name that has changed, it is replaced with the new one. No group events are emitted as a
	 * consequence of this method, because the group memberships are still the same; it is only the
	 * representations of the names within that are changing.
	 * 
	 * @param event
	 *              the incoming event
	 */
	//:FVK: @Override
	public void actionPerformed(WikiEvent event) {
		if (!(event instanceof WikiSecurityEvent se)) {
			return;
		}

		if (se.getType() == WikiSecurityEvent.PROFILE_NAME_CHANGED) {
			Session session = se.getSrc();
			UserProfile[] profiles = (UserProfile[]) se.getTarget();
			Principal[] oldPrincipals = new Principal[] { new WikiPrincipal(profiles[0].getLoginName()),
					new WikiPrincipal(profiles[0].getFullname()), new WikiPrincipal(profiles[0].getWikiName()) };
			Principal newPrincipal = new WikiPrincipal(profiles[1].getFullname());

			// Examine each group
			int groupsChanged = 0;
			// здесь сохраняется изменение в группе, при изменении профиля пользователя. 
			//:FVK:			try {
			//				for (Group group : this.m_groupDatabase.groups()) {
			//					boolean groupChanged = false;
			//					for (Principal oldPrincipal : oldPrincipals) {
			//						if (group.isMember(oldPrincipal)) {
			//							group.remove(oldPrincipal);
			//							group.add(newPrincipal);
			//							groupChanged = true;
			//						}
			//					}
			//					if (groupChanged) {
			//						setGroup(session, group);
			//						groupsChanged++;
			//					}
			//				}
			//			} catch (WikiException e) {
			//				// Oooo! This is really bad...
			//				log.error("Could not change user name in Group lists because of GroupDatabase error:" + e.getMessage());
			//			}
			log.info("Profile name change for '" + newPrincipal.toString() + "' caused " + groupsChanged
					+ " groups to change also.");
		}
	}
	
}
