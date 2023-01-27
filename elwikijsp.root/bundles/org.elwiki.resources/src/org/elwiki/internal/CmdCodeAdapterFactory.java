package org.elwiki.internal;

import java.util.Map;

import org.apache.wiki.api.core.ContextEnum;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factory converting ContextEnum to CmdCode.
 *
 * @author v.fedorov
 */
public class CmdCodeAdapterFactory implements IAdapterFactory {

	// use a static final field so that the adapterList is only instantiated once.
	private static final Class<?>[] adapterList = new Class<?>[] { CmdCode.class };

	/** Private map with request CmdContexts as keys, Commands as values */
	//@formatter:off
	private final Map<ContextEnum, CmdCode> context2cmdCode = Map.ofEntries(
			Map.entry(ContextEnum.WIKI_LOGIN, new LoginCmdCode()),
			Map.entry(ContextEnum.WIKI_LOGOUT, new LogoutCmdCode()),
			Map.entry(ContextEnum.PAGE_CREATE, new CreatePageCmdCode()),
			Map.entry(ContextEnum.PAGE_DELETE, new DeletePageCmdCode()),
			Map.entry(ContextEnum.PAGE_VIEW, new ViewCmdCode()),
			Map.entry(ContextEnum.PAGE_EDIT, new EditCmdCode()),
			Map.entry(ContextEnum.PAGE_DIFF, new DiffCmdCode()),
			Map.entry(ContextEnum.PAGE_INFO, new InfoCmdCode()),
			Map.entry(ContextEnum.PAGE_RENAME, new RenameCmdCode()),
			Map.entry(ContextEnum.PAGE_UPLOAD, new UploadCmdCode()),
			Map.entry(ContextEnum.GROUP_EDIT, new EditGroupCmdCode()),
			//Map.entry(ContextEnum.GROUP_VIEW, )
			//Map.entry(ContextEnum.GROUP_CREATE, )
			Map.entry(ContextEnum.GROUP_DELETE, new DeleteGroupCmdCode()),
			Map.entry(ContextEnum.WIKI_PREFS, new PrefsCmdCode()),
			Map.entry(ContextEnum.WIKI_FIND, new FindCmdCode()),
			Map.entry(ContextEnum.WIKI_SCOPE, new ScopeCmdCode()),
			Map.entry(ContextEnum.PAGE_COMMENT, new CommentCmdCode()),
			Map.entry(ContextEnum.WIKI_PERSIST_CONTENT, new PersistContentCmdCode()),
			Map.entry(ContextEnum.ATTACHMENT_DELETE, new DeleteAttachmentCmdCode())
	); //@formatter:on

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == CmdCode.class && adaptableObject instanceof ContextEnum contextEnum) {
			return adapterType.cast(getCommandCode(contextEnum));
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return adapterList;
	}

	private Object getCommandCode(ContextEnum adaptableObject) {
		return this.context2cmdCode.get(adaptableObject);
	}

}
