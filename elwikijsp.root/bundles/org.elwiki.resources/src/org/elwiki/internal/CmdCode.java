package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.Engine;
import org.elwiki.services.ServicesRefs;

public abstract class CmdCode {

	private final Engine engine;

	protected CmdCode() {
		engine = ServicesRefs.Instance; // :FVK: workaround.
	}

	public Engine getEngine() {
		return this.engine;
	}

	/**
	 * Code for context`s command: execute prolog.
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @throws Exception
	 */
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Code for context`s command: execute epilogue.
	 */
	public void applyEpilogue() {
	}

}
