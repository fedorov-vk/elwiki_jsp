package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;

public abstract class CmdCode {

	private Engine engine;
	private WikiContext wikiContext;

	protected CmdCode() {
		//
	}

	public WikiContext getWikiContext() {
		return wikiContext;
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
		this.wikiContext = ContextUtil.findContext(httpRequest);
		this.engine = wikiContext.getEngine();
	}

	/**
	 * Code for context`s command: execute epilogue.
	 */
	public void applyEpilogue() {
	}

}
