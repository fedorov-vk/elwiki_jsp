package org.elwiki.data.persist;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.elwiki_data.PagesStore;

public interface IDataStore {

	void doConnect();
	
	CDOView getView();

	CDOTransaction getTransactionCDO();
	
	PagesStore getPagesStore();

}
