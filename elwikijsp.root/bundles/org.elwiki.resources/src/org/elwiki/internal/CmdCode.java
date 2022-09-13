package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.elwiki.services.ServicesRefs;

public abstract class CmdCode {

	private final Engine engine;

	protected CmdCode() {
		engine = ServicesRefs.Instance; // :FVK: workaround.
	}

	public Engine getEngine() {
		return this.engine;
	}

	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void applyEpilogue() {
	}

}
