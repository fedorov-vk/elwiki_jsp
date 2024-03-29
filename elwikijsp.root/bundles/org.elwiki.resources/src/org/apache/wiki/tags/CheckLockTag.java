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

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.pages0.PageLock;
import org.apache.wiki.pages0.PageManager;
import org.eclipse.jdt.annotation.NonNull;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;

import java.io.IOException;

/**
 * Checks whether the page is locked for editing. If the mode matches, the tag
 * body is included.
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>The "<b>mode</b>" attribute can be any of the following:
 * <ul>
 * <li><i>locked</i> - The page is currently locked, but the lock is owned by
 * someone else.</li>
 * <li><i>owned</i> - The page is currently locked and the current user is the
 * owner of the lock.</li>
 * <li><i>unlocked</i> - Nobody has locked the page.</li>
 * </ul>
 * </li>
 * <li><b>id</b> - The value of the ID attribute is the identifier for the page
 * scope attribute, and is set if the page is locked.
 * </ul>
 * 
 * @since 2.0
 */
public class CheckLockTag extends BaseWikiTag {

	private static final long serialVersionUID = 2879188449549914568L;

	private enum LockState {
		LOCKED, NOTLOCKED, OWNED
	}

	private LockState m_mode = LockState.NOTLOCKED;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initTag() {
		super.initTag();
		m_mode = LockState.NOTLOCKED;
	}

	/**
	 * Sets the mode to check for.
	 * 
	 * @param arg A String for the mode.
	 */
	public void setMode(final String arg) {
		switch (arg) {
		case "locked": // TODO: :FVK: - вынести константы в интерфейс.
			m_mode = LockState.LOCKED;
			break;
		case "owned":
			m_mode = LockState.OWNED;
			break;
		default:
			m_mode = LockState.NOTLOCKED;
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		final WikiPage page = wikiContext.getPage();

		if (page != null) {
			final Engine engine = wikiContext.getEngine();
			@NonNull
			PageManager pageManager = engine.getManager(PageManager.class);
			final PageLock lock = pageManager.getCurrentLock(page);
			final HttpSession session = pageContext.getSession();
			final PageLock userLock = (PageLock) session.getAttribute("lock-" + page.getName());
			if ((lock != null && m_mode == LockState.LOCKED && lock != userLock)
					|| (lock != null && m_mode == LockState.OWNED && lock == userLock)
					|| (lock == null && m_mode == LockState.NOTLOCKED)) {

				final String tid = getId();
				if (tid != null && lock != null) {
					pageContext.setAttribute(tid, lock);
				}

				return EVAL_BODY_INCLUDE;
			}
		}

		return SKIP_BODY;
	}

}