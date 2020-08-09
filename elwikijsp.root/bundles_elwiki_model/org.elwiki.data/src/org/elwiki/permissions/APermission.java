package org.elwiki.permissions;

import java.io.Serializable;
import java.security.Permission;

public abstract class APermission extends Permission implements Serializable {

	private static final long serialVersionUID = 1L;

	private String wikiName;

	private String actions;

	private int mask;

	/**
	 * Protected Constructor.
	 * 
	 * @param name
	 */
	protected APermission(String name) {
		super(name);
	}

	/**
	 * Returns the name of the wiki containing the page represented by permission; may return the
	 * wildcard string.
	 * 
	 * @return wiki name.
	 */
	public String getWikiName() {
		return this.wikiName;
	}

	public void setWikiName(String wikiName) {
		getName();
		this.wikiName = wikiName;
	}

	/**
	 * WikiPermission</br>
	 * Returns the actions for this permission: "createGroups", "createPages", "editPreferences",
	 * "editProfile", or "login". The actions will always be sorted in alphabetic order, and will always
	 * appear in lower case.
	 * 
	 * PagePermission</br>
	 * Returns the actions for this permission: "view", "edit", "comment", "modify", "upload" or
	 * "delete". The actions will always be sorted in alphabetic order, and will always appear in lower
	 * case.
	 * 
	 * GroupPermission</br>
	 * Returns the actions for this permission: &#8220;view&#8221;, &#8220;edit&#8221;, or
	 * &#8220;delete&#8221;. The actions will always be sorted in alphabetic order, and will always
	 * appear in lower case.
	 *
	 * @return the actions
	 * @see java.security.Permission#getActions()
	 */
	public String getActions() {
		return this.actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public int getMask() {
		return this.mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	/**
	 * Returns the hash code for this ElWiki Permission.
	 * 
	 * @see java.security.Permission#hashCode()
	 */
	@Override
	public int hashCode() {
		//  If the wiki has not been set, uses a dummy value for the hashcode
		//  calculation.  This may occur if the page given does not refer
		//  to any particular wiki
		String wiki = this.wikiName != null ? this.wikiName : "dummy_value";
		return this.mask + ((13 * this.actions.hashCode()) * 23 * wiki.hashCode());
	}
}
