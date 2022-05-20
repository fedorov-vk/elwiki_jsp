package org.elwiki.permissions;

import java.io.Serializable;
import java.security.Permission;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class APermission extends Permission implements Serializable {

	private static final long serialVersionUID = -6232332910696496522L;

	protected static final String ACTION_SEPARATOR = ",";

	/** Value for a generic wildcard. */
	protected static final String WILDCARD = "*";

	protected static final String WIKI_SEPARATOR = ":";

	private String wikiName;

	private String m_actionsSequence;

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
	 * Returns the name of the wiki containing the page represented by permission;
	 * may return the wildcard string.
	 * 
	 * @return wiki name.
	 */
	public String getWikiName() {
		return this.wikiName;
	}

	public void setWikiName(String wikiName) {
		this.wikiName = wikiName;
	}

	/**
	 * WikiPermission</br>
	 * Returns the actions for this permission: "createGroups", "createPages",
	 * "editPreferences", "editProfile", or "login". The actions will always be
	 * sorted in alphabetic order, and will always appear in lower case.
	 * 
	 * PagePermission</br>
	 * Returns the actions for this permission: "view", "edit", "comment", "modify",
	 * "upload" or "delete". The actions will always be sorted in alphabetic order,
	 * and will always appear in lower case.
	 * 
	 * GroupPermission</br>
	 * Returns the actions for this permission: &#8220;view&#8221;,
	 * &#8220;edit&#8221;, or &#8220;delete&#8221;. The actions will always be
	 * sorted in alphabetic order, and will always appear in lower case.
	 *
	 * @return the actions
	 * @see java.security.Permission#getActions()
	 */
	public String getActions() {
		return this.m_actionsSequence;
	}

	public void setActions(String actions) {
		this.m_actionsSequence = actions;
	}

	public int getMask() {
		return this.mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	/**
	 * Method creates a binary mask based on the actions specified. This is used by
	 * {@link #implies(Permission)}.
	 * 
	 * @param actions array of actions for this permission. They are represented by
	 *                lowercase characters.
	 * @return the binary actions mask.
	 */
	protected abstract int createMask(String[] actions);

	/**
	 * Parses actions.
	 * 
	 * @param actionsSequence
	 */
	protected void parseActions(String actionsSequence) {
		String[] actions = actionsSequence.toLowerCase().split(ACTION_SEPARATOR);
		Arrays.sort(actions, String.CASE_INSENSITIVE_ORDER);
		setMask(createMask(actions));
		String resultSequence = Arrays.asList(actions).stream().collect(Collectors.joining(ACTION_SEPARATOR));
		setActions(resultSequence);
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
		return this.mask + ((13 * this.m_actionsSequence.hashCode()) * 23 * wiki.hashCode());
	}

}
