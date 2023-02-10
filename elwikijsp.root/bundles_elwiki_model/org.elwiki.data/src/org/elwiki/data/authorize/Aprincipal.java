package org.elwiki.data.authorize;

import java.io.Serializable;
import java.security.Principal;

abstract public class Aprincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;

	private final String principalName;
	private final String principalUid;

	protected Aprincipal(String name) {
		this.principalName = name;
		this.principalUid = "---"; //:FVK: workaround.
	}

	public Aprincipal(String name, String uid) {
		this.principalName = name;
		this.principalUid = uid;
	}

	public String getUid() {
		return this.principalUid;
	}
	
	/**
	 * Returns the name of the ElWiki Principal.
	 * 
	 * @return the name of the ElWiki Principal.
	 * @see java.security.Principal#getName()
	 */
	@Override
	public String getName() {
		return this.principalName;
	}

	/**
	 * The hashCode of this object is equal to the hash code of its name.
	 * 
	 * @return the hashcode a unique hashcode for the Object.
	 */
	@Override
	public int hashCode() {
		return this.principalName.hashCode();
	}

	protected String getParameters() {
		return null;
	}

	/**
	 * Returns a String representation of the principal implementation.
	 * 
	 * @return the string representation.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String suffix = getParameters();
		if (suffix != null) {
			suffix = " (" + suffix + ")";
		} else {
			suffix = "";
		}
		return "«" + this.getClass().getSimpleName() + suffix + ": " + this.principalName + "»";
	}

}