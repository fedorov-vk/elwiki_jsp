package org.elwiki.permissions;

import java.io.Serializable;
import java.security.Permission;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Apermission extends Permission implements Serializable {

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
	protected Apermission(String name) {
		super(name);
	}

	/**
	 * Returns the name of the wiki, represented by this permission;
	 * may return the wildcard string.
	 * 
	 * @return wiki name.
	 */
	public String getWikiName() {
		return this.wikiName;
	}

	/**
	 * Sets the wiki name to be used for this permission.<br/>
	 * If the wiki name is empty or null, the wildcard is accepted.
	 * 
	 * @param wikiName
	 */
	public void setWikiName(String wikiName) {
		String wikiName1 = (wikiName == null || wikiName.isEmpty() || wikiName.isBlank()) ? WILDCARD : wikiName;
		this.wikiName = wikiName1;
	}

	/**
	 * Returns the actions for this permission. Actions depends on permission and can be follows:
	 * <ul>
	 * <li> "createGroups", "createPages", "editPreferences", "editProfile", "login"</li>
	 * <li> "view", "edit", "comment", "modify", "upload", "delete"</li>
	 * </ul
	 * The actions will always be sorted in alphabetic order, and will always appear in lower case.
	 *
	 * @return the actions
	 * @see java.security.Permission#getActions()
	 */
	public String getActions() {
		return this.m_actionsSequence;
	}

	/**
	 * Sets the actions to be used for this permission.
	 * 
	 * @param actions
	 */
	public void setActions(String actions) {
		this.m_actionsSequence = actions;
	}

	public int getMask() {
		return this.mask;
	}

	/**
	 * Sets the mask to be used for this permission.
	 * 
	 * @param mask
	 */
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

}
