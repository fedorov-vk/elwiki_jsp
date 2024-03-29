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
package org.apache.wiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.auth.AuthenticationManager;

/**
 * Includes the content if an user check validates. 
 * <br/>The possibilities for the "status" attribute are:
 * <ul>
 * <li>"anonymous" - the body of the tag is included if the user is completely
 * unknown (no cookie, no password)</li>
 * <li>"asserted" - the body of the tag is included if the user has either been
 * named by a cookie, but not been authenticated.</li>
 * <li>"authenticated" - the body of the tag is included if the user is
 * validated either through the container, or by our own authentication.</li>
 * <li>"assertionsAllowed" - the body of the tag is included if wiki allows
 * identities to be asserted using cookies.</li>
 * <li>"assertionsNotAllowed" - the body of the tag is included if wiki does
 * <i>not</i> allow identities to be asserted using cookies.</li>
 * <li>"containerAuth" - the body of the tag is included if the user is
 * validated through the container.</li>
 * <li>"customAuth" - the body of the tag is included if the user is validated
 * through our own authentication.</li>
 * <li>"known" - if the user is not anonymous</li>
 * <li>"notAuthenticated" - the body of the tag is included if the user is not
 * yet authenticated.</li>
 * </ul>
 */
public class UserCheckTag extends BaseWikiTag {

	private static final long serialVersionUID = 3256438110127863858L;

	private static final String ASSERTED = "asserted";
	private static final String AUTHENTICATED = "authenticated";
	private static final String ANONYMOUS = "anonymous";
	private static final String ASSERTIONS_ALLOWED = "assertionsallowed";
	private static final String ASSERTIONS_NOT_ALLOWED = "assertionsnotallowed";
	private static final String CONTAINER_AUTH = "containerauth";
	private static final String CUSTOM_AUTH = "customauth";
	private static final String KNOWN = "known";
	private static final String NOT_AUTHENTICATED = "notauthenticated";

	private String m_status;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initTag() {
		super.initTag();
		m_status = null;
	}

	/**
	 * Get the status as defined above.
	 * 
	 * @return The status to be checked.
	 */
	public String getStatus() {
		return m_status;
	}

	/**
	 * Sets the status as defined above.
	 * 
	 * @param status The status to be checked.
	 */
	public void setStatus(String status) {
		m_status = status.toLowerCase();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.wiki.tags.BaseWikiTag#doWikiStartTag()
	 */
	@Override
	public int doWikiStartTag() throws ProviderException, IOException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		WikiSession session = wikiContext.getWikiSession();
		AuthenticationManager authMgr = wikiContext.getEngine().getManager(AuthenticationManager.class);
		
		boolean containerAuth = authMgr.isContainerAuthenticated();
		boolean cookieAssertions = authMgr.isAllowsCookieAssertions();

		int result = SKIP_BODY;
		if (m_status != null) {
			result = switch (m_status) {
			case ANONYMOUS -> (session.isAnonymous()) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			case AUTHENTICATED -> (session.isAuthenticated()) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			case ASSERTED -> (session.isAsserted()) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			case ASSERTIONS_ALLOWED -> (cookieAssertions) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			case ASSERTIONS_NOT_ALLOWED -> (!cookieAssertions) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			case CONTAINER_AUTH -> (containerAuth) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			case CUSTOM_AUTH -> (!containerAuth) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			case KNOWN -> (!session.isAnonymous()) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			case NOT_AUTHENTICATED -> (!session.isAuthenticated()) ? EVAL_BODY_INCLUDE : SKIP_BODY;
			default -> SKIP_BODY;
			};
		}

		return result;
	}

}
