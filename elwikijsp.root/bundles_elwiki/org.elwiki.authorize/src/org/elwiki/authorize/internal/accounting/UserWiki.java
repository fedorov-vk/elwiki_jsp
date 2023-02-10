package org.elwiki.authorize.internal.accounting;

import org.elwiki.api.authorization.IUserWiki;

final public class UserWiki implements IUserWiki {

	private String name;

	public UserWiki(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
