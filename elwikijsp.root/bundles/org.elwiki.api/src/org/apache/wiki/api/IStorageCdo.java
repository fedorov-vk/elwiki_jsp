package org.apache.wiki.api;

import java.io.IOException;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.elwiki.api.component.WikiManager;
import org.elwiki_data.PagesStore;

/**
 * This interface provides access to the repository's CDO service from anywhere in ElWiki.
 *
 */
public interface IStorageCdo extends WikiManager {

	public interface ITransactionalOperation<T extends CDOObject> {
		public Object execute(T object, CDOTransaction transaction);
	}

	boolean isStorageActive();

	@Deprecated
	void activateStorage() throws Exception;

	@Deprecated
	Exception deactivateStorage();

	@Deprecated
	AdapterFactory getAdapterFactory();

	<T extends CDOObject> Object modify(T object, ITransactionalOperation<T> operation);

	CDOView getView();

	CDOTransaction getTransactionCDO();

	PagesStore getPagesStore();

	void loadAllContent() throws IOException;

	void saveAllContent() throws IOException;
}
