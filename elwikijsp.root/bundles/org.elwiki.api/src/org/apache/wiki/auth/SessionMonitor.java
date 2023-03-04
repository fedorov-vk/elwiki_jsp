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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.event.WikiEventTopic;
import org.apache.wiki.api.event.WikiSecurityEventTopic;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.util.comparators.PrincipalComparator;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 *  <p>Manages Sessions for different Engines.</p>
 *  <p>The Sessions are stored both in the remote user HttpSession and in the SessionMonitor for the Engine.
 *  This class must be configured as a session listener in the web.xml for the wiki web application.</p>
 */
//@formatter:off
@Component(
	name = "elwiki.SessionMonitor",
	service = { org.apache.wiki.auth.ISessionMonitor.class, WikiManager.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public final class SessionMonitor implements ISessionMonitor, WikiManager, HttpSessionListener, EventHandler {

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

	// -- OSGi service handling ----------------------(start)--
	
	@Reference
	volatile protected EventAdmin eventAdmin;

    @Reference(target = "(component.factory=elwiki.WikiSession.factory)")
    private ComponentFactory<Session> factoryWikiSession;

	@WikiServiceReference
	private AuthenticationManager authenticationManager;

	@WikiServiceReference
	private Engine m_engine;

	@Deactivate
	public void shutdown() {
		// TODO:
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// nothong to do.
	}
	
	// -- OSGi service handling ------------------------(end)--

    /**
     * Just looks for a WikiSession; does not create a new one.
     * This method may return <code>null</code>, <em>and
     * callers should check for this value</em>.
     *
     *  @param session the user's HTTP session
     *  @return the WikiSession, if found. Can be null.
     */
	@Override
    public Session findSession( final HttpSession session ) {
        Session wikiSession = null;
        final String sessionId = ( session == null ) ? "(null)" : session.getId();
        final Session storedSession = m_sessions.get( sessionId );

        // If the weak reference returns a wiki session, return it
        if( storedSession != null ) {
            if( log.isDebugEnabled() ) {
                log.debug( "Looking up WikiSession for session ID=" + sessionId + "... found it" );
            }
            wikiSession = storedSession;
        }

        return wikiSession;
    }

    /**
     * {@inheritDoc}
     */
    //@Override //TODO: убрать @Override, сделать protected - так как этот метод заменен методом getWikiSession
    public Session find(HttpServletRequest request) {
    	HttpSession session = request.getSession();
        Session wikiSession = findSession( session );
        final String sid = ( session == null ) ? "(null)" : session.getId();

        // Otherwise, create a new guest session and stash it.
        if( wikiSession == null ) {
            if( log.isDebugEnabled() ) {
                log.debug( "Looking up WikiSession for session ID=" + sid + "... not found. Creating guestSession()" );
            }
            wikiSession = this.createGuestSession(sid);
            try {
				this.authenticationManager.login(request, wikiSession);
			} catch (WikiSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}            
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
     * Fires when the web container creates a new HTTP session.
     * 
     * @param se the HTTP session event
     */ //:FVK: TODO: обеспечить вызов метода.
    @Override
    public void sessionCreated( final HttpSessionEvent se ) {
        final HttpSession session = se.getSession();
        log.debug( "Created session: " + session.getId() + "." );
    }

    /**
     * Removes the user's WikiSession from the internal session cache when the web
     * container destroys an HTTP session.
     * @param se the HTTP session event
     */ //:FVK: TODO: обеспечить вызов метода.
    @Override
	public void sessionDestroyed(final HttpSessionEvent se) {
		final HttpSession session = se.getSession();
		final Session storedSession = this.findSession(session);
		this.remove(session);
		log.debug("Removed session " + session.getId() + ".");
		if (storedSession != null) {
			eventAdmin.sendEvent(new Event(WikiSecurityEventTopic.TOPIC_SECUR_SESSION_EXPIRED,
					Map.of(WikiSecurityEventTopic.PROPERTY_SESSION, storedSession)));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Session getWikiSession(HttpServletRequest request) {
		if (request == null) {
			if (log.isDebugEnabled()) {
				log.debug("Looking up WikiSession for NULL HttpRequest: returning createGuestSession()");
			}
			return staticGuestSession();
		}

		// Look for a WikiSession associated with the user's Http Session and create one if it isn't there yet.
		Session wikiSession = this.find(request);
		wikiSession.setCachedLocale(request.getLocale()); //:FVK: workaround?

		return wikiSession;
	}

	/**
	 * {@inheritDoc}
	 * @param sessionId 
	 */
	@Override
	public Session createGuestSession(String sessionId) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		if (sessionId != null) {
			properties.put(EventConstants.EVENT_FILTER,
					"(" + WikiEventTopic.PROPERTY_KEY_TARGET + "=" + sessionId + ")");
		}

		Session wikiSession = (Session) this.factoryWikiSession.newInstance(properties).getInstance();

		// Add the session as listener for GroupManager, AuthManager, AccountManager events
		//TODO: add listeners...
		/*
		//:FVK: final GroupManager groupMgr = Engine.getGroupManager();
		final IIAuthenticationManager authMgr = Engine.getAuthenticationManager();
		final AccountManager userMgr = Engine.getAccountManager();
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
            session = createGuestSession(null);
            c_guestSession.set( session );
        }

        return session;
    }

	@Override
	public String getSessionId(Session session) {
		String sessionId = null;
		synchronized (m_sessions) {
			sessionId = m_sessions.entrySet().stream().filter(entry -> session.equals(entry.getValue()))
					.map(Map.Entry::getKey).findFirst().get();
		}
		return sessionId;
	}

	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
			break;
		}*/
	}

}
