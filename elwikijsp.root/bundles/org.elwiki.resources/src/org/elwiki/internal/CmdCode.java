package org.elwiki.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.exceptions.WikiException;

public abstract class CmdCode {

	private Command command;

	protected CmdCode(Command command) {
		this.command = command;
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
