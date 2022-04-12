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

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.event.ElWikiEventsConstants;
import org.apache.wiki.api.event.WikiEngineEvent;
import org.apache.wiki.api.event.WikiEventListener;
import org.apache.wiki.api.event.WikiEventManager;
import org.apache.wiki.api.event.WikiSecurityEvent;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.acl.AclManager;
import org.apache.wiki.auth.authorize.Role;
import org.apache.wiki.util.comparators.PrincipalComparator;
import org.elwiki.services.ServicesRefs;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  <p>Manages Sessions for different Engines.</p>
 *  <p>The Sessions are stored both in the remote user HttpSession and in the SessionMonitor for the Engine.
 *  This class must be configured as a session listener in the web.xml for the wiki web application.</p>
 */
@Component (name = "elwiki.SessionMonitor", service = org.apache.wiki.auth.ISessionMonitor.class)
public final class SessionMonitor implements ISessionMonitor, HttpSessionListener {

    private static final Logger log = Logger.getLogger( SessionMonitor.class );

    private static ThreadLocal< Session > c_guestSession = new ThreadLocal<>();
    
    /** Weak hashmap with HttpSessions as keys, and WikiSessions as values. */
    private final Map< String, Session > m_sessions = new WeakHashMap<>();

    private final PrincipalComparator m_comparator = new PrincipalComparator();

    /**
     * Construct the SessionListener
     */
    public SessionMonitor() {
    	// empty.
    }

	// -- service handling ---------------------------{start}--

	@Reference(target = "(component.factory=elwiki.WikiSession.factory)")
	private ComponentFactory<AclManager> factoryWikiSession;

	@Activate
	protected void startup() {
		// TODO:
	}

	@Deactivate
	public void shutdown() {
		// TODO:
	}

	// -- service handling -----------------------------{end}--

    /**
     * Just looks for a WikiSession; does not create a new one.
     * This method may return <code>null</code>, <em>and
     * callers should check for this value</em>.
     *
     *  @param session the user's HTTP session
     *  @return the WikiSession, if found
     */
    private Session findSession( final HttpSession session ) {
        Session wikiSession = null;
        final String sid = ( session == null ) ? "(null)" : session.getId();
        final Session storedSession = m_sessions.get( sid );

        // If the weak reference returns a wiki session, return it
        if( storedSession != null ) {
            if( log.isDebugEnabled() ) {
                log.debug( "Looking up WikiSession for session ID=" + sid + "... found it" );
            }
            wikiSession = storedSession;
        }

        return wikiSession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session find( final HttpSession session ) {
        Session wikiSession = findSession( session );
        final String sid = ( session == null ) ? "(null)" : session.getId();

        // Otherwise, create a new guest session and stash it.
        if( wikiSession == null ) {
            if( log.isDebugEnabled() ) {
                log.debug( "Looking up WikiSession for session ID=" + sid + "... not found. Creating guestSession()" );
            }
            wikiSession = this.guestSession(sid);
            synchronized( m_sessions ) {
                m_sessions.put( sid, wikiSession );
            }
        }

        return wikiSession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void remove( final HttpServletRequest request ) {
        if( request == null ) {
            throw new IllegalArgumentException( "Request cannot be null." );
        }
        remove( request.getSession() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( HttpSession session ) {
        if( session == null ) {
            throw new IllegalArgumentException( "Session cannot be null." );
        }
        synchronized( m_sessions ) {
            Session wikiSession = m_sessions.remove( session.getId() );
            c_guestSession.remove();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int sessions()
    {
        return userPrincipals().length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Principal[] userPrincipals() {
        final Collection<Principal> principals = new ArrayList<>();
        synchronized ( m_sessions ) {
            for ( final Session session : m_sessions.values()) {
                principals.add( session.getUserPrincipal() );
            }
        }
        final Principal[] p = principals.toArray( new Principal[ principals.size() ] );
        Arrays.sort( p, m_comparator );
        return p;
    }

    /**
     * Registers a WikiEventListener with this instance.
     *
     * @param listener the event listener
     * @since 2.4.75
     */
    public final synchronized void addWikiEventListener( final WikiEventListener listener ) {
        WikiEventManager.addWikiEventListener( this, listener );
    }

    /**
     * Un-registers a WikiEventListener with this instance.
     *
     * @param listener the event listener
     * @since 2.4.75
     */
    public final synchronized void removeWikiEventListener( final WikiEventListener listener ) {
        WikiEventManager.removeWikiEventListener( this, listener );
    }

    /**
     * Fires a WikiSecurityEvent to all registered listeners.
     *
     * @param type  the event type
     * @param principal the user principal associated with this session
     * @param session the wiki session
     * @since 2.4.75
     */
    protected final void fireEvent( final int type, final Principal principal, final Session session ) {
        if( WikiEventManager.isListening( this ) ) {
            WikiEventManager.fireEvent( this, new WikiSecurityEvent( this, type, principal, session ) );
        }
    }

    /**
     * Fires when the web container creates a new HTTP session.
     * 
     * @param se the HTTP session event
     */
    @Override
    public void sessionCreated( final HttpSessionEvent se ) {
        final HttpSession session = se.getSession();
        log.debug( "Created session: " + session.getId() + "." );
    }

    /**
     * Removes the user's WikiSession from the internal session cache when the web
     * container destroys an HTTP session.
     * @param se the HTTP session event
     */
    @Override
	public void sessionDestroyed(final HttpSessionEvent se) {
		final HttpSession session = se.getSession();
		final Session storedSession = this.findSession(session);
		this.remove(session);
		log.debug("Removed session " + session.getId() + ".");
		if (storedSession != null) {
			fireEvent(WikiSecurityEvent.SESSION_EXPIRED, storedSession.getLoginPrincipal(), storedSession);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Session getWikiSession(HttpServletRequest request) {
		if (request == null) {
			if (log.isDebugEnabled()) {
				log.debug("Looking up WikiSession for NULL HttpRequest: returning guestSession()");
			}
			return staticGuestSession();
		}

		// Look for a WikiSession associated with the user's Http Session and create one if it isn't there yet.
		Session wikiSession = this.find(request.getSession());
		wikiSession.setCachedLocale(request.getLocale()); //:FVK: workaround?

		return wikiSession;
	}

	/**
	 * {@inheritDoc}
	 * @param sid 
	 */
	@Override
	public Session guestSession(String sid) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(SESSION_MONITOR, this);
		properties.put(EventConstants.EVENT_TOPIC, ElWikiEventsConstants.TOPIC_LOGGING_ALL); //!!!:FVK:
		if (sid != null) {
			properties.put(EventConstants.EVENT_FILTER,
					"(" + ElWikiEventsConstants.PROPERTY_KEY_TARGET + "=" + sid + ")");
		}

		Session wikiSession = (Session) this.factoryWikiSession.newInstance(properties).getInstance();

		// Add the session as listener for GroupManager, AuthManager, UserManager events
		//TODO: add listeners...
		/*
		//:FVK: final GroupManager groupMgr = ServicesRefs.getGroupManager();
		final IIAuthenticationManager authMgr = ServicesRefs.getAuthenticationManager();
		final UserManager userMgr = ServicesRefs.getUserManager();
		//:FVK: groupMgr.addWikiEventListener( session );
		authMgr.addWikiEventListener( session );
		userMgr.addWikiEventListener( session );
		 */

		return wikiSession;
	}

    /**
     *  Returns a static guest session, which is available for this thread only.  This guest session is used internally whenever
     *  there is no HttpServletRequest involved, but the request is done e.g. when embedding JSPWiki code.
     *
     *  @param engine Engine for this session
     *  @return A static WikiSession which is shared by all in this same Thread.
     */
    // FIXME: Should really use WeakReferences to clean away unused sessions.
	// :FVK: Deprecated? не использовать?
    private Session staticGuestSession() {
        Session session = c_guestSession.get();
        if( session == null ) {
            session = guestSession(null);
            c_guestSession.set( session );
        }

        return session;
    }}
