package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.elwiki.resources.ResourcesActivator;

public abstract class CmdCode {

	private Command command;
	private final Engine engine;

	protected CmdCode(Command command) {
		this.command = command;
		if( (engine = ResourcesActivator.getService(Engine.class)) == null ) {
			//TODO: we fail.
		}
	}

	public Engine getEngine() {
		return this.engine;
	}

	public Command getCommand() {
		return command;
	}

	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void applyEpilogue() {
	}

}
