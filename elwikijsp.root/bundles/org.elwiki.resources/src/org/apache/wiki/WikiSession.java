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
package org.apache.wiki;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.AuthenticationManager;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.util.HttpUtil;
import org.elwiki.IWikiConstants.AuthenticationStatus;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.api.event.WikiEventTopic;
import org.elwiki.api.event.WikiLoginEventTopic;
import org.elwiki.api.event.WikiSecurityEventTopic;
import org.elwiki.data.authorize.GroupPrincipal;
import org.elwiki.data.authorize.WikiPrincipal;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

/**
 * <p>
 * Default implementation for {@link Session}.
 * </p>
 */
//@formatter:off
@Component(
	name = "elwiki.WikiSession",
	service = { org.apache.wiki.api.core.Session.class, EventHandler.class },
	property = {
		EventConstants.EVENT_TOPIC + "=" + WikiLoginEventTopic.TOPIC_LOGGING_ALL,
		EventConstants.EVENT_TOPIC + "=" + WikiSecurityEventTopic.TOPIC_SECUR_ALL
	},
	factory = "elwiki.WikiSession.factory")
//@formatter:on
public final class WikiSession implements Session, EventHandler {

    private static final Logger log                   = Logger.getLogger( WikiSession.class );

    private static final String ALL                   = "*";

    private final Subject       m_subject             = new Subject();

    private final Map< String, Set< String > > m_messages  = new ConcurrentHashMap<>();

    private Principal           m_userPrincipal       = WikiPrincipal.GUEST;

    private Principal           m_loginPrincipal      = WikiPrincipal.GUEST;

    private Locale              m_cachedLocale        = Locale.getDefault();

	private User user;

    /**
     * Returns <code>true</code> if one of this WikiSession's user Principals can be shown to belong to a particular wiki group. If
     * the user is not authenticated, this method will always return <code>false</code>.
     *
     * @param group the group to test
     * @return the result
     */
    protected boolean isInGroup( final IGroupWiki group ) {
        for( final Principal principal : getPrincipals() ) {
            if( isAuthenticated() && group.isMember( principal ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * TODO: hide this class into *.internal package --
     * to prevent WikiSession from being instantiated directly. 
     */
    public WikiSession() {
    	//
    }

	@Override
	public void setCachedLocale(Locale locale) {
		this.m_cachedLocale = locale;		
	}
    
    // -- OSGi service handling ----------------------(start)--

	@Reference
	private UserAdmin userAdminService;

	@WikiServiceReference
	private ISessionMonitor sessionMonitor;

	@WikiServiceReference
	private AccountRegistry accountRegistry;

    /**
     * This component activate routine. Does all the basic initialization.
     */
    @Activate
	protected void startup() {
		this.invalidate();
	}

	@Deactivate
	protected void shutdown() {
		//
	}

	// -- OSGi service handling ------------------------(end)--
    
    /** {@inheritDoc} */
    @Override
    public boolean isAsserted() {
        return m_subject.getPrincipals().contains( GroupPrincipal.ASSERTED );
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAuthenticated() {
    	Set<Principal> principals = m_subject.getPrincipals();
        // If Role.AUTHENTICATED is in principals set, always return true.
        if ( principals.contains( GroupPrincipal.AUTHENTICATED ) ) {
            return true;
        }

        // With non-JSPWiki LoginModules, the role may not be there, so we need to add it if the user really is authenticated.
        if ( !isAnonymous() && !isAsserted() ) { //:FVK: workaround? (from JSPwiki code)
        	principals.add( GroupPrincipal.AUTHENTICATED );
            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAnonymous() {
        final Set< Principal > principals = m_subject.getPrincipals();
        boolean result = principals.contains( GroupPrincipal.ANONYMOUS ) ||
               principals.contains( WikiPrincipal.GUEST ) ||
               HttpUtil.isIPV4Address( getUserPrincipal().getName() );

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Principal getLoginPrincipal() {
        return m_loginPrincipal;
    }

    /** {@inheritDoc} */
    @Override
    public Principal getUserPrincipal() {
        return m_userPrincipal;
    }

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        return m_cachedLocale;
    }

    /** {@inheritDoc} */
    @Override
    public void addMessage( final String message ) {
        addMessage( ALL, message );
    }

    /** {@inheritDoc} */
    @Override
	public void addMessage(final String topic, final String message) {
		if (topic == null) {
			throw new IllegalArgumentException("addMessage: topic cannot be null.");
		}
		final Set<String> messages = m_messages.computeIfAbsent(topic, k -> new LinkedHashSet<>());
		messages.add((message != null) ? message : "");
	}

    /** {@inheritDoc} */
    @Override
    public void clearMessages() {
        m_messages.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void clearMessages( final String topic ) {
        final Set< String > messages = m_messages.get( topic );
        if ( messages != null ) {
            m_messages.clear();
        }
    }

    /** {@inheritDoc} */
    @Override
    public String[] getMessages() {
        return getMessages( ALL );
    }

    /** {@inheritDoc} */
    @Override
    public String[] getMessages( final String topic ) {
        final Set< String > messages = m_messages.get( topic );
        if( messages == null || messages.size() == 0 ) {
            return new String[ 0 ];
        }
        return messages.toArray( new String[ messages.size() ] );
    }

    /** {@inheritDoc} */
    @Override
    public Principal[] getPrincipals() {
        final ArrayList< Principal > principals = new ArrayList<>();

        // Take the first non Role as the main Principal
        for( final Principal principal : m_subject.getPrincipals() ) {
            if ( AuthenticationManager.isUserPrincipal( principal ) ) {
                principals.add( principal );
            }
        }

        return principals.toArray( new Principal[ principals.size() ] );
    }

    /** {@inheritDoc} */
    @Override
    public Principal[] getRoles() {
        final Set< Principal > roles = new HashSet<>();

        // Add all of the GroupPrincipals possessed by the Subject directly
        roles.addAll( m_subject.getPrincipals( GroupPrincipal.class ) );

        // Return a defensive copy
        final Principal[] roleArray = roles.toArray( new Principal[ roles.size() ] );
        Arrays.sort( roleArray, WikiPrincipal.COMPARATOR );
        return roleArray;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPrincipal( final Principal principal ) {
        return m_subject.getPrincipals().contains( principal );
    }

	/**
	 * Listens for WikiEvents generated by source objects such as the GroupManager, AccountManager or
	 * AuthenticationManager. This method adds Principals to the private Subject managed by the
	 * WikiSession.
	 */
	@Override
	public void handleEvent(Event event) {
		log.debug("Recevied event with topic: " + event.getTopic());

		String topic = event.getTopic();
		switch (topic) {
		case WikiLoginEventTopic.TOPIC_LOGIN_INITIATED: {
			// Do nothing
		}
			break;
		case WikiLoginEventTopic.TOPIC_LOGOUT: {
			this.invalidate();
		}
			break;
		case WikiLoginEventTopic.TOPIC_PRINCIPALS_ADD: {
			@SuppressWarnings("unchecked")
			Collection<Principal> eventPrincipals = (Collection<Principal>) event
					.getProperty(WikiEventTopic.PROPERTY_PRINCIPALS);
			Set<Principal> subjectPrincipals = m_subject.getPrincipals();
			// bypass wrong logging status - don't add ANONYMOUS, ASSERTED roles.
			if (isAuthenticated() && !eventPrincipals.contains(GroupPrincipal.ASSERTED)
					&& !eventPrincipals.contains(GroupPrincipal.ANONYMOUS)) {
				subjectPrincipals.addAll(eventPrincipals);
			}
		}
			break;
		case WikiLoginEventTopic.TOPIC_LOGIN_ANONYMOUS: {
			@SuppressWarnings("unchecked")
			Collection<Principal> eventPrincipals = (Collection<Principal>) event
					.getProperty(WikiEventTopic.PROPERTY_PRINCIPALS);

			// Set the login/user principals and login status
			m_loginPrincipal = m_userPrincipal = AuthenticationManager.getLoginPrincipal(eventPrincipals);
			setUser(this.m_userPrincipal);

			// Puts the login principal to the Subject, and set the built-in roles.
			Set<Principal> subjectPrincipals = m_subject.getPrincipals();
			subjectPrincipals.clear();
			subjectPrincipals.add(m_loginPrincipal);
			subjectPrincipals.add(GroupPrincipal.ALL);
			subjectPrincipals.add(GroupPrincipal.ANONYMOUS);
		}
			break;
		case WikiLoginEventTopic.TOPIC_LOGIN_ASSERTED: {
			@SuppressWarnings("unchecked")
			Collection<Principal> eventPrincipals = (Collection<Principal>) event
					.getProperty(WikiEventTopic.PROPERTY_PRINCIPALS);

			// Set the login/user principals and login status
			m_loginPrincipal = m_userPrincipal = AuthenticationManager.getLoginPrincipal(eventPrincipals);
			setUser(this.m_userPrincipal);

			// Puts the login principal to the Subject, and set the built-in roles.
			Set<Principal> subjectPrincipals = m_subject.getPrincipals();
			subjectPrincipals.clear();
			subjectPrincipals.add(m_loginPrincipal);
			subjectPrincipals.add(GroupPrincipal.ALL);
			subjectPrincipals.add(GroupPrincipal.ASSERTED);
		}
			break;
		case WikiLoginEventTopic.TOPIC_LOGIN_AUTHENTICATED: {
			@SuppressWarnings("unchecked")
			Collection<Principal> eventPrincipals = (Collection<Principal>) event
					.getProperty(WikiEventTopic.PROPERTY_PRINCIPALS);

			// Set the login/user principals and login status
			m_loginPrincipal = m_userPrincipal = AuthenticationManager.getLoginPrincipal(eventPrincipals);
			setUser(this.m_userPrincipal);

			// Puts the login principal to the Subject, and set the built-in roles.
			Set<Principal> subjectPrincipals = m_subject.getPrincipals();
			subjectPrincipals.clear();
			subjectPrincipals.addAll(eventPrincipals);
			subjectPrincipals.add(GroupPrincipal.ALL);
			subjectPrincipals.add(GroupPrincipal.AUTHENTICATED);

			// Add the user and group principals
			injectUserProfilePrincipals(); // Add principals for the user profile
			injectGroupPrincipals(); // Inject group principals
		}
			break;
		case WikiSecurityEventTopic.TOPIC_SECUR_PROFILE_SAVE: {
			injectUserProfilePrincipals(); // Add principals for the user profile
			injectGroupPrincipals(); // Inject group principals
		}
			break;
		case WikiSecurityEventTopic.TOPIC_SECUR_PROFILE_NAME_CHANGED: {
            // Refresh user principals based on new user profile
            if( this.isAuthenticated()) {
                // To prepare for refresh, set the new full name as the primary principal
				UserProfile[] profiles = (UserProfile[]) event
						.getProperty(WikiSecurityEventTopic.PROPERTY_PROFILES);
                UserProfile newProfile = profiles[ 1 ];
                if( newProfile.getFullname() == null ) {
                    throw new IllegalStateException( "User profile FullName cannot be null." );
                }

                Set< Principal > principals = m_subject.getPrincipals();
                m_loginPrincipal = new WikiPrincipal( newProfile.getLoginName() );

                // Add the login principal to the Subject, and set the built-in roles
                principals.clear();
                principals.add( GroupPrincipal.ALL );
                principals.add( GroupPrincipal.AUTHENTICATED );
                principals.add( m_loginPrincipal );

                // Add the user and group principals
                injectUserProfilePrincipals();  // Add principals for the user profile
                injectGroupPrincipals();  // Inject group principals
            }
		}
			break;
		}
	}

    /**
     * Invalidates the Session and resets its Subject's Principals to the equivalent of a "guest session".
     */
    public void invalidate() {
		if (log.isDebugEnabled()) {
			log.debug("Invalidating Session for wiki session (" + this + ")");
		}

		Set<Principal> principals = m_subject.getPrincipals();
		principals.clear();
		principals.add(GroupPrincipal.ALL);
		principals.add(GroupPrincipal.ANONYMOUS);
		principals.add(WikiPrincipal.GUEST);
		m_loginPrincipal = m_userPrincipal = WikiPrincipal.GUEST;
    }

    /**
     * Injects GroupPrincipal objects into the user's Principal set based on the groups the user belongs to. For Groups, the algorithm
     * first calls the {@link GroupManager#getGroups()} to obtain the array of GroupPrincipals the authorizer knows about. Then, the
     * method {@link GroupManager#isUserInRole(Session, Principal)} is called for each Principal. If the user is a member of the
     * group, an equivalent GroupPrincipal is injected into the user's principal set. Existing GroupPrincipals are flushed and replaced.
     * This method should generally be called after a user's {@link org.apache.wiki.auth.UserProfile} is saved. If the wiki session
     * is null, or there is no matching user profile, the method returns silently.
     */
	protected void injectGroupPrincipals() {
		// Flush the existing GroupPrincipals
		m_subject.getPrincipals().removeAll(m_subject.getPrincipals(GroupPrincipal.class));

		// Get the groups of which the current user is a member.
		for (String roleItem : userAdminService.getAuthorization(this.getUser()).getRoles()) {
			org.osgi.service.useradmin.Role role = userAdminService.getRole(roleItem);
			if (role != null && role.getType() == org.osgi.service.useradmin.Role.GROUP) {
				String name = (String) role.getProperties().get(AccountRegistry.GROUP_NAME);
				GroupPrincipal group = new GroupPrincipal(name, role.getName());
				m_subject.getPrincipals().add(group);
			}
		}
	}

    /**
     * Adds Principal objects to the Subject that correspond to the logged-in user's profile attributes for the wiki name, full name
     * and login name. These Principals will be WikiPrincipals, and they will replace all other WikiPrincipals in the Subject. <em>Note:
     * this method is never called during anonymous or asserted sessions.</em>
     */
    protected void injectUserProfilePrincipals() {
        // Search for the user profile
        final String searchUid = m_loginPrincipal.getName();
        if ( searchUid == null ) {
            // Oh dear, this wasn't an authenticated user after all
            log.info("Refresh principals failed because WikiSession had no user Principal; maybe not logged in?");
            return;
        }

        // Look up the user and go get the new Principals
        //:FVK: final AccountRegistry accountRegistry = WikiEngine.Instance.getManager(AccountRegistry.class);
        if( accountRegistry == null ) {
            throw new IllegalStateException( "User database cannot be null." );
        }
        try {
            final UserProfile profile = accountRegistry.find( searchUid );
            final Principal[] principals = accountRegistry.getPrincipals( profile.getLoginName() );
            for( final Principal principal : principals ) {
                // Add the Principal to the Subject
                m_subject.getPrincipals().add( principal );

                // Set the user principal if needed; we prefer FullName, but the WikiName will also work
                final boolean isFullNamePrincipal = ( principal instanceof WikiPrincipal &&
                                                      ( ( WikiPrincipal )principal ).getType().equals( WikiPrincipal.FULL_NAME ) );
                if ( isFullNamePrincipal ) {
                   m_userPrincipal = principal;
                } else if ( !( m_userPrincipal instanceof WikiPrincipal ) ) {
                    m_userPrincipal = principal;
                }
            }
        } catch ( final NoSuchPrincipalException e ) {
            // We will get here if the user has a principal but not a profile
            // For example, it's a container-managed user who hasn't set up a profile yet
            log.warn("User profile '" + searchUid + "' not found. This is normal for container-auth users who haven't set up a profile yet.");
        }
    }

    /** {@inheritDoc} */
	@Override
	public AuthenticationStatus getLoginStatus() {
		if (isAnonymous()) {
			return AuthenticationStatus.ANONYMOUS;
		}
		if (isAsserted()) {
			return AuthenticationStatus.ASSERTED;
		}
		if (isAuthenticated()) {
			return AuthenticationStatus.AUTHENTICATED;
		}

		return AuthenticationStatus.ANONYMOUS; //:FVK: workaround.
	}

    /** {@inheritDoc} */
    @Override
    public Subject getSubject() {
        return m_subject;
    }

	@Override
	public User getUser() {
		return this.user;
	}

	private void setUser(Principal userPrincipal) {
		if (userPrincipal == null) {
			throw new IllegalArgumentException("setUser: user principal cannot be null.");
		}

		String userUid = userPrincipal.getName();
		org.osgi.service.useradmin.Role userRole = userAdminService.getRole(userUid);
		if (userRole != null && userRole.getType() == org.osgi.service.useradmin.Role.USER) {
			this.user = (User) userRole;
		}
	}

}
