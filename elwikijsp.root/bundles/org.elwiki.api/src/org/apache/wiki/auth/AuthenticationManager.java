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
package org.apache.wiki.auth;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;

import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.exceptions.WikiException;
import org.elwiki.api.component.IModulePreferences;
import org.elwiki.api.event.WikiLoginEventTopic;
import org.elwiki.data.authorize.GroupPrincipal;

/**
 * Manages authentication activities for a Engine: user login, logout, and credential refreshes. This class uses JAAS to determine how
 * users log in.
 * <p>
 * The login procedure is protected in addition by a mechanism which prevents a hacker to try and force-guess passwords by slowing down
 * attempts to log in into the same account.  Every login attempt is recorded, and stored for a while (currently ten minutes), and each
 * login attempt during that time incurs a penalty of 2^login attempts milliseconds - that is, 10 login attempts incur a login penalty
 * of 1.024 seconds. The delay is currently capped to 20 seconds.
 */
public interface AuthenticationManager extends IModulePreferences {

	interface Prefs {
		/// Preferences names of Authentication Manager.
		/** If this property is <code>true</code>, allow cookies to be used to assert identities. */
		String ALLOW_COOKIE_ASSERTIONS = "cookieAssertions";

		/** If this property is <code>true</code>, allow cookies to be used for authentication. */
		String ALLOW_COOKIE_AUTH = "cookieAuthentication";

		/** Whether logins should be throttled to limit brute-forcing attempts. Defaults to true. */
		String LOGIN_THROTTLING = "login.throttling";

		/** The {@link LoginModule} to use for custom authentication. */
		String LOGIN_MODULE_ID = "loginModule.id";
	}

	/** Prefix for LoginModule options key/value pairs. */
	@Deprecated
	String PREFIX_LOGIN_MODULE_OPTIONS = "jspwiki.loginModule.options.";

	/**
	 * Looks up the LoginModule class, via extension point "org.elwiki.authorize.loginModule".
	 *
	 * @param loginModuleId
	 * @return
	 * @throws WikiException
	 */
	Class<? extends LoginModule> getLoginModule(String loginModuleId) throws WikiException;

    /**
     * Returns true if this Engine uses container-managed authentication. This method is used primarily for cosmetic purposes in the
     * JSP tier, and performs no meaningful security function per se. Delegates to
     * {@link WebContainerAuthorizer#isContainerAuthorized()},
     * if used as the external authorizer; otherwise, returns <code>false</code>.
     *
     * @return <code>true</code> if the wiki's authentication is managed by the container, <code>false</code> otherwise
     */
    boolean isContainerAuthenticated();

	/**
	 * <p>
	 * Logs in the user by attempting to populate a Session Subject from a web servlet request by
	 * examining the request for the presence of container credentials and user cookies. The processing
	 * logic is as follows:
	 * </p>
	 * <ul>
	 * <li>If the Session had previously been unauthenticated, check to see if user has subsequently
	 * authenticated. To be considered "authenticated," the request must supply one of the following (in
	 * order of preference): the container <code>userPrincipal</code>, container
	 * <code>remoteUser</code>, or authentication cookie. If the user is authenticated, this method
	 * fires event {@link WikiLoginEventTopic#TOPIC_LOGIN_AUTHENTICATED} with two parameters: a
	 * Principals representing the login principal, and the ID of current Session. In addition, if the
	 * authorizer is of type WebContainerAuthorizer, this method iterates through the container roles
	 * returned by {@link WebContainerAuthorizer#getGroups()}, tests for membership in each one, and
	 * adds those that pass to the Subject's principal set.</li>
	 * <li>If, after checking for authentication, the Session is still Anonymous, this method next
	 * checks to see if the user has "asserted" an identity by supplying an assertion cookie. If the
	 * user is found to be asserted, this method fires event
	 * {@link WikiLoginEventTopic#TOPIC_LOGIN_ASSERTED} with two parameters:
	 * <code>WikiPrincipal(<em>cookievalue</em>)</code>, and the ID of current Session.</li>
	 * <li>If, after checking for authenticated and asserted status, the Session is <em>still</em>
	 * anonymous, this method fires event {@link WikiLoginEventTopic#TOPIC_LOGIN_ANONYMOUS} with two
	 * parameters: <code>WikiPrincipal(<em>remoteAddress</em>)</code>, and the ID of current
	 * Session</li>
	 * </ul>
	 *
	 * @param request servlet request for this user
	 * @return always returns <code>true</code> (because anonymous login, at least, will always succeed)
	 * @throws org.apache.wiki.auth.WikiSecurityException if the user cannot be logged in for any reason
	 */
	boolean login(HttpServletRequest request) throws WikiSecurityException;

	/**
	 * The same as {@link AuthenticationManager#login(HttpServletRequest)} with session specified directly for authenticate. 
     * 
	 * @param request servlet request for this user
	 * @param session WikiSession which should be authenticated
	 * @return always returns <code>true</code> (because anonymous login, at least, will always succeed)
	 * @throws WikiSecurityException if the user cannot be logged in for any reason
	 */
	boolean login(HttpServletRequest request, WikiSession session) throws WikiSecurityException;
    
    /**
     * Attempts to perform a Session login for the given username/password combination using JSPWiki's custom authentication mode. In
     * order to log in, the JAAS LoginModule supplied by the Engine property {@link #PROP_LOGIN_MODULE} will be instantiated, and its
     * {@link javax.security.auth.spi.LoginModule#initialize(Subject, CallbackHandler, Map, Map)} method will be invoked. By default,
     * the {@link AccountRegistryLoginModule} class will be used. When the LoginModule's <code>initialize</code>
     * method is invoked, an options Map populated by properties keys prefixed by {@link #PREFIX_LOGIN_MODULE_OPTIONS} will be passed as a
     * parameter.
     *
     * @param session the current wiki session; may not be <code>null</code>.
     * @param request the user's HTTP request. This parameter may be <code>null</code>, but the configured LoginModule will not have access
     *                to the HTTP request in this case.
     * @param username The user name. This is a login name, not a WikiName. In most cases they are the same, but in some cases, they might not be.
     * @param password the password
     * @return true, if the username/password is valid
     * @throws org.apache.wiki.auth.WikiSecurityException if the Authorizer or AccountManager cannot be obtained
     */
    boolean loginAsserted( WikiSession session, HttpServletRequest request, String username, String password ) throws WikiSecurityException;

    /**
     * Logs the user out by retrieving the Session associated with the HttpServletRequest and unbinding all of the Subject's Principals,
     * except for {@link Role#ALL}, {@link Role#ANONYMOUS}. is a cheap-and-cheerful way to do it without invoking JAAS LoginModules.
     * The logout operation will also flush the JSESSIONID cookie from the user's browser session, if it was set.
     *
     * @param request the current HTTP request
     */
    void logout( HttpServletRequest request );

    /**
     * Determines whether this Engine allows users to assert identities using cookies instead of passwords. This is determined by inspecting
     * the property {@link #ALLOW_COOKIE_ASSERTIONS}.
     *
     * @return <code>true</code> if cookies are allowed
     */
    boolean isAllowsCookieAssertions();

    /**
     * Determines whether this Engine allows users to authenticate using cookies instead of passwords. This is determined by inspecting
     * the property {@link #ALLOW_COOKIE_AUTH}.
     *
     *  @return <code>true</code> if cookies are allowed for authentication
     *  @since 2.5.62
     */
    boolean isAllowsCookieAuthentication();

    /**
     * Determines whether the supplied Principal is a "role principal".
     *
     * @param principal the principal to test
     * @return {@code true} if the Principal is of type {@link GroupPrincipal}, {@code false} otherwise.
     */
    static boolean isRolePrincipal( final Principal principal ) {
        return principal instanceof GroupPrincipal;
    }

    /**
     * Determines whether the supplied Principal is a "user principal".
     *
     * @param principal the principal to test
     * @return {@code false} if the Principal is of type {@link GroupPrincipal}, {@code true} otherwise.
     */
    static boolean isUserPrincipal( final Principal principal ) {
        return !isRolePrincipal( principal );
    }

    /**
     * Returns the first Principal in a set that isn't a {@link Role} or {@link GroupPrincipal}.
     *
     * @param principals the principal set
     * @return the login principal
     */
    @Deprecated
    static Principal getLoginPrincipal(Collection< Principal > principals ) {
		for (final Principal principal : principals) {
			if (isUserPrincipal(principal)) {
				return principal;
			}
		}
		return null;
    }

}
