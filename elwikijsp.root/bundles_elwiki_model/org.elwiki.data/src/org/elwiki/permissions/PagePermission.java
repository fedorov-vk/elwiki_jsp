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
package org.elwiki.permissions;

import java.security.Permission;
import java.security.PermissionCollection;

import org.elwiki_data.WikiPage;

/**
 * <p>
 * Permission to perform an operation on a single page or collection of pages in
 * a given wiki. Permission actions include: <code>view</code>,&nbsp;
 * <code>edit</code> (edit the text of a wiki
 * page),&nbsp;<code>comment</code>,&nbsp;
 * <code>upload</code>,&nbsp;<code>modify</code>&nbsp;(edit text and upload
 * attachments),&nbsp;<code>delete</code>&nbsp; and&nbsp;<code>rename</code>.
 * </p>
 * <p>
 * The target of a permission is a single page or collection in a given wiki.
 * The syntax for the target is the wiki name, followed by a colon (:) and the
 * name of the page. "All wikis" can be specified using a wildcard (*). Page
 * collections may also be specified using a wildcard. For pages, the wildcard
 * may be a prefix, suffix, or all by itself. Examples of targets include:
 * </p>
 * <blockquote><code>*:*<br/>
 * *:JanneJalkanen<br/>
 * *:Jalkanen<br/>
 * *:Janne*<br/>
 * mywiki:JanneJalkanen<br/>
 * mywiki:*Jalkanen<br/>
 * mywiki:Janne*</code> </blockquote>
 * <p>
 * For a given target, certain permissions imply others:
 * </p>
 * <ul>
 * <li><code>delete</code>&nbsp;and&nbsp;<code>rename</code>&nbsp;imply&nbsp;<code>edit</code></li>
 * <li><code>modify</code>&nbsp;implies&nbsp;<code>edit</code>&nbsp;and&nbsp;<code>upload</code></li>
 * <li><code>edit</code>&nbsp;implies&nbsp;<code>comment</code>&nbsp;and&nbsp;<code>view</code></li>
 * <li><code>comment</code>&nbsp;and&nbsp;<code>upload</code>&nbsp;imply&nbsp;<code>view</code></li>
 * Targets that do not include a wiki prefix <i>never </i> imply others.
 * </ul>
 */
public final class PagePermission extends APermission {

	private static final long serialVersionUID = 5211426500968895383L;

	/** Action name for the view permission. */
	public static final String VIEW_ACTION = "view";

	/** Action name for the edit permission. */
	public static final String EDIT_ACTION = "edit";

	/** Action name for the comment permission. */
	public static final String COMMENT_ACTION = "comment";

	/** Action name for the upload permission. */
	public static final String UPLOAD_ACTION = "upload";

	/** Action name for the rename permission. */
	public static final String RENAME_ACTION = "rename";

	/** Action name for the modify permission. */
	public static final String MODIFY_ACTION = "modify";

	/** Action name for the delete permission. */
	public static final String DELETE_ACTION = "delete";

	protected static final int VIEW_MASK = 0x01;
	protected static final int EDIT_MASK = 0x02;
	protected static final int COMMENT_MASK = 0x04;
	protected static final int UPLOAD_MASK = 0x08;
	protected static final int RENAME_MASK = 0x20;
	protected static final int MODIFY_MASK = 0x40;
	protected static final int DELETE_MASK = 0x10;

	/** A static instance of the comment permission. */
	public static final PagePermission COMMENT = new PagePermission(COMMENT_ACTION);

	/** A static instance of the delete permission. */
	public static final PagePermission DELETE = new PagePermission(DELETE_ACTION);

	/** A static instance of the edit permission. */
	public static final PagePermission EDIT = new PagePermission(EDIT_ACTION);

	/** A static instance of the rename permission. */
	public static final PagePermission RENAME = new PagePermission(RENAME_ACTION);

	/** A static instance of the modify permission. */
	public static final PagePermission MODIFY = new PagePermission(MODIFY_ACTION);

	/** A static instance of the upload permission. */
	public static final PagePermission UPLOAD = new PagePermission(UPLOAD_ACTION);

	/** A static instance of the view permission. */
	public static final PagePermission VIEW = new PagePermission(VIEW_ACTION);

	private static final String ATTACHMENT_SEPARATOR = "/";

	private final String m_page;

	/** For serialization purposes. */
	protected PagePermission() {
		this("");
	}

	/**
	 * Private convenience constructor that creates a new PagePermission for all
	 * wikis and pages (*:*) and set of actions.
	 * 
	 * @param actions
	 */
	private PagePermission(String actions) {
		this(WILDCARD + WIKI_SEPARATOR + WILDCARD, actions);
	}

	/**
	 * Creates a new PagePermission for a specified page name and set of actions.
	 * Page should include a prepended wiki name followed by a colon (:). If the
	 * wiki name is not supplied or starts with a colon, the page refers to no wiki
	 * in particular, and will never imply any other PagePermission.
	 * 
	 * @param page    the wiki page
	 * @param actions the allowed actions for this page
	 */
	public PagePermission(String page, String actions) {
		super(page);

		// Parse wiki and page (which may include wiki name and page)
		// Strip out attachment separator; it is irrelevant.

		// FIXME3.0: Assumes attachment separator is "/".
		String[] pathParams = page.split(WIKI_SEPARATOR);
		String pageName;
		if (pathParams.length >= 2) {
			setWikiName(pathParams[0].length() > 0 ? pathParams[0] : null);
			pageName = pathParams[1];
		} else {
			setWikiName(null);
			pageName = pathParams[0];
		}
		int pos = pageName.indexOf(ATTACHMENT_SEPARATOR);
		this.m_page = (pos == -1) ? pageName : pageName.substring(0, pos);

		parseActions(actions);
	}

	/**
	 * Creates a new PagePermission for a specified page and set of actions.
	 * 
	 * @param page    The wikipage.
	 * @param actions A set of actions; a comma-separated list of actions.
	 */
	public PagePermission(WikiPage page, String actions) {
		this(page.getWiki() + WIKI_SEPARATOR + page.getName(), actions);
	}

	/**
	 * Two PagePermission objects are considered equal if their actions (after
	 * normalization), wiki and target are equal.
	 * 
	 * @param obj {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PagePermission)) {
			return false;
		}
		PagePermission p = (PagePermission) obj;
		return p.getMask() == this.getMask() && p.m_page.equals(this.m_page) && p.getWikiName() != null
				&& p.getWikiName().equals(this.getWikiName());
	}

	/**
	 * Returns the name of the wiki page represented by this permission.
	 * 
	 * @return the page name
	 */
	public String getPage() {
		return this.m_page;
	}

	/**
	 * <p>
	 * PagePermission can only imply other PagePermissions; no other permission
	 * types are implied. One PagePermission implies another if its actions if three
	 * conditions are met:
	 * </p>
	 * <ol>
	 * <li>The other PagePermission's wiki is equal to, or a subset of, that of this
	 * permission. This permission's wiki is considered a superset of the other if
	 * it contains a matching prefix plus a wildcard, or a wildcard followed by a
	 * matching suffix.</li>
	 * <li>The other PagePermission's target is equal to, or a subset of, the target
	 * specified by this permission. This permission's target is considered a
	 * superset of the other if it contains a matching prefix plus a wildcard, or a
	 * wildcard followed by a matching suffix.</li>
	 * <li>All of other PagePermission's actions are equal to, or a subset of, those
	 * of this permission</li>
	 * </ol>
	 * 
	 * @see java.security.Permission#implies(java.security.Permission)
	 * 
	 * @param permission {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean implies(Permission permission) {
		// Permission must be a PagePermission
		if (!(permission instanceof PagePermission)) {
			return false;
		}

		// Build up an "implied mask"
		PagePermission p = (PagePermission) permission;
		int impliedMask = impliedMask(this.getMask());

		// If actions aren't a proper subset, return false
		if ((impliedMask & p.getMask()) != p.getMask()) {
			return false;
		}

		// See if the tested permission's wiki is implied
		boolean impliedWiki = isSubset(getWikiName(), p.getWikiName());

		// If this page is "*", the tested permission's
		// page is implied
		boolean impliedPage = isSubset(this.m_page, p.m_page);

		return impliedWiki && impliedPage;
	}

	/**
	 * Returns a new {@link AllPermissionCollection}.
	 * 
	 * @see java.security.Permission#newPermissionCollection()
	 * @return {@inheritDoc}
	 */
	@Override
	public PermissionCollection newPermissionCollection() {
		return new AllPermissionCollection();
	}

	/**
	 * Prints a human-readable representation of this permission.
	 * 
	 * @see java.lang.Object#toString()
	 * 
	 * @return Something human-readable
	 */
	@Override
	public String toString() {
		String wiki = (this.getWikiName() == null) ? "" : this.getWikiName();
		// :FVK: return "(\"" + this.getClass().getName() + "\",\"" + wiki + WIKI_SEPARATOR + this.m_page + "\",\"" + getActions() + "\")";

		return this.getClass().getName() + "|" + wiki + WIKI_SEPARATOR + this.m_page + "|" + getActions();
	}

	/**
	 * Creates an "implied mask" based on the actions originally assigned: for
	 * example, delete implies modify, comment, upload and view.
	 * 
	 * @param resultMask binary mask for actions
	 * @return binary mask for implied actions
	 */
	protected static int impliedMask(int mask) {
		int resultMask = mask;
		if ((resultMask & DELETE_MASK) > 0) {
			resultMask |= MODIFY_MASK;
		}
		if ((resultMask & RENAME_MASK) > 0) {
			resultMask |= EDIT_MASK;
		}
		if ((resultMask & MODIFY_MASK) > 0) {
			resultMask |= EDIT_MASK | UPLOAD_MASK;
		}
		if ((resultMask & EDIT_MASK) > 0) {
			resultMask |= COMMENT_MASK;
		}
		if ((resultMask & COMMENT_MASK) > 0) {
			resultMask |= VIEW_MASK;
		}
		if ((resultMask & UPLOAD_MASK) > 0) {
			resultMask |= VIEW_MASK;
		}
		return resultMask;
	}

	/**
	 * Determines whether one target string is a logical subset of the other.
	 * 
	 * @param superSet the prospective superset
	 * @param subSet   the prospective subset
	 * @return the results of the test, where <code>true</code> indicates that
	 *         <code>subSet</code> is a subset of <code>superSet</code>
	 */
	protected static boolean isSubset(String superSet, String subSet) {
		// If either is null, return false
		if (superSet == null || subSet == null) {
			return false;
		}

		// If targets are identical, it's a subset
		if (superSet.equals(subSet)) {
			return true;
		}

		// If super is "*", it's a subset
		if (superSet.equals(WILDCARD)) {
			return true;
		}

		// If super starts with "*", sub must end with everything after the *
		if (superSet.startsWith(WILDCARD)) {
			String suffix = superSet.substring(1);
			return subSet.endsWith(suffix);
		}

		// If super ends with "*", sub must start with everything before *
		if (superSet.endsWith(WILDCARD)) {
			String prefix = superSet.substring(0, superSet.length() - 1);
			return subSet.startsWith(prefix);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int createMask(String[] actions) {
		if (actions == null || actions.length == 0) {
			throw new IllegalArgumentException("Actions cannot be blank or null");
		}
		int mask = 0;
		for (String action : actions) {
			if (VIEW_ACTION.equalsIgnoreCase(action)) {
				mask |= VIEW_MASK;
			} else if (EDIT_ACTION.equalsIgnoreCase(action)) {
				mask |= EDIT_MASK;
			} else if (COMMENT_ACTION.equalsIgnoreCase(action)) {
				mask |= COMMENT_MASK;
			} else if (MODIFY_ACTION.equalsIgnoreCase(action)) {
				mask |= MODIFY_MASK;
			} else if (UPLOAD_ACTION.equalsIgnoreCase(action)) {
				mask |= UPLOAD_MASK;
			} else if (DELETE_ACTION.equalsIgnoreCase(action)) {
				mask |= DELETE_MASK;
			} else if (RENAME_ACTION.equalsIgnoreCase(action)) {
				mask |= RENAME_MASK;
			} else {
				throw new IllegalArgumentException("Unrecognized action: " + action);
			}
		}
		return mask;
	}

}
