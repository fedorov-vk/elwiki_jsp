package org.elwiki.internal;

import org.apache.wiki.api.core.Command;
import org.eclipse.core.runtime.IAdapterFactory;

public class CmdCodeAdapterFactory implements IAdapterFactory {

	// use a static final field so that the adapterList is only instanciated once.
	private static final Class<?>[] adapterList = new Class<?>[] { CmdCode.class };

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == CmdCode.class && adaptableObject instanceof Command) {
			return adapterType.cast(getCommandCode((Command) adaptableObject));
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return adapterList;
	}

	private Object getCommandCode(Command command) {
		CmdCode cmdCode = null;
		String commandId = command.getRequestContext();
		switch (commandId) {
		case "view":
			cmdCode = new ViewCmdCode(command);
			break;

		case "edit":
			cmdCode = new EditCmdCode(command);
			break;

		case "login":
			cmdCode = new LoginCmdCode(command);
			break;

		case "logout":
			cmdCode = new LogoutCmdCode(command);
			break;

		case "rename":
			cmdCode = new RenameCmdCode(command);
			break;

		case "diff":
			cmdCode = new DiffCmdCode(command);
			break;

		case "upload":
			cmdCode = new UploadCmdCode(command);
			break;

		case "editGroup":
			cmdCode = new EditGroupCmdCode(command);
			break;

		/*case "viewGroup":
			cmdCode = new EditGroupCmdCode(command);
			break;*/

		case "createGroup":
			cmdCode = new CreateGroupCmdCode(command);
			break;
			
		case "deleteGroup":
			cmdCode = new DeleteGroupCmdCode(command);
			break;

		case "find":
			cmdCode = new FindCmdCode(command);
			break;
			
		default:
			break;
		}

		return cmdCode;
	}

}
