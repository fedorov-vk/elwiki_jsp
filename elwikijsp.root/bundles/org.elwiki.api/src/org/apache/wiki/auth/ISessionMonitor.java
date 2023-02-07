package org.apache.wiki.auth;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;

public interface ISessionMonitor {

	/** Reference identifier for Engine in the service activator parameters. */
	String SESSION_MONITOR = "SESSION_MONITOR";

	/**
	 * Just looks for a WikiSession; does not create a new one.
	 * This method may return <code>null</code>, <em>and
	 * callers should check for this value</em>.
	 *
	 *  @param session the user's HTTP session
	 *  @return the WikiSession, if found
	 */
	Session findSession(HttpSession session);
	
	/**
	 * <p>
	 * Looks up the wiki session associated with a user's Http session and adds it
	 * to the session cache. This method will return the "guest session" as
	 * constructed by {@link org.apache.wiki.api.spi.SessionSPI#guest()} if the
	 * HttpSession is not currently associated with a WikiSession. This method is
	 * guaranteed to return a non-<code>null</code> WikiSession.
	 * </p>
	 * <p>
	 * Internally, the session is stored in a HashMap; keys are the HttpSession
	 * objects, while the values are {@link java.lang.ref.WeakReference}-wrapped
	 * WikiSessions.
	 * </p>
	 *
	 * @param session the HTTP Servlet request
	 * @return the wiki session
	 */
	//:FVK: Session find(HttpServletRequest request);

	/**
	 * Removes the wiki session associated with the user's HttpRequest from the
	 * session cache.
	 *
	 * @param request the user's HTTP request
	 */
	void remove(HttpServletRequest request);

	/**
	 * Removes the wiki session associated with the user's HTTP request from the
	 * cache of wiki sessions, typically as part of a logout process.
	 *
	 * @param session the user's HTTP session
	 */
	void remove(HttpSession session);

	/**
	 * Returns the current number of active wiki sessions.
	 * 
	 * @return the number of sessions
	 */
	int sessions();

	/**
	 * <p>
	 * Returns the current wiki users as a sorted array of Principal objects. The
	 * principals are those returned by each WikiSession's
	 * {@link Session#getUserPrincipal()}'s method.
	 * </p>
	 * <p>
	 * To obtain the list of current WikiSessions, we iterate through our session
	 * Map and obtain the list of values, which are WikiSessions wrapped in
	 * {@link java.lang.ref.WeakReference} objects. Those
	 * <code>WeakReference</code>s whose <code>get()</code> method returns
	 * non-<code>null</code> values are valid sessions.
	 * </p>
	 *
	 * @return the array of user principals
	 */
	Principal[] userPrincipals();

	/**
	 * <p>
	 * Factory method that returns the Session object associated with the current
	 * HTTP request. This method looks up the associated HttpSession in an internal
	 * WeakHashMap and attempts to retrieve the WikiSession. If not found, one is
	 * created. This method is guaranteed to always return a Session, although the
	 * authentication status is unpredictable until the user attempts to log in. If
	 * the servlet request parameter is <code>null</code>, a synthetic
	 * {@link #guestSession(Engine)} is returned.
	 * </p>
	 * <p>
	 * When a session is created, this method attaches a WikiEventListener to the
	 * GroupManager, AccountManager and AuthenticationManager, so that changes to
	 * users, groups, logins, etc. are detected automatically.
	 * </p>
	 * 
	 * @param request
	 * @return
	 */
	Session getWikiSession(HttpServletRequest request);

	/**
	 * Factory method that creates a new "guest" session containing a single
	 * user Principal {@link org.apache.wiki.auth.WikiPrincipal#GUEST}, plus the
	 * role principals {@link Role#ALL} and {@link Role#ANONYMOUS}. This method also
	 * adds the session as a listener for GroupManager, AuthenticationManager and
	 * AccountManager events.
	 *
	 * @return the guest wiki session
	 */
	Session createGuestSession(String sid);

}
