package org.elwiki.data.persist.internal;

import org.apache.log4j.Logger;
import org.apache.wiki.api.IStorageCdo;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.net4j.util.WrappedException;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.persist.IDataStore;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PagesStore;

/**
 * Предоставляет доступ к CDO репозиторию.
 *  
 * @author vfedorov
 */
public class DataStore extends Repository implements IDataStore, IStorageCdo {

	protected static final Logger log = Logger.getLogger(DataStore.class);
	
	private static final String PAGES_STORE_PATH = "Pages/";

	private PagesStore pagesStore;

	private IWikiConfiguration wikiConfiguration;

	/**
	 * Instantiate DataStore.
	 */
	public DataStore() {
		super();
	}

	private void createResource(String path, EObject eObject) {
		CDOTransaction transaction = this.session.openTransaction();
		try {
			CDOResource resource = transaction.createResource(path);
			resource.getContents().add(eObject);
			transaction.commit();
		} catch (CommitException ex) {
			throw WrappedException.wrap(ex);
		} finally {
			transaction.close();
		}
	}

	@Override
	public CDOView getView() {
		return this.view;
	}

	@Override
	public CDOTransaction getTransactionCDO() {
		CDOTransaction transaction = this.session.openTransaction();
		return transaction;
	}

	@Override
	public PagesStore getPagesStore() {
		if (this.pagesStore == null) {
			if (this.view.hasResource(PAGES_STORE_PATH) == false) {
				PagesStore ps = Elwiki_dataFactory.eINSTANCE.createPagesStore();
				createResource(PAGES_STORE_PATH, ps);
			}
			CDOResource resource = this.view.getResource(PAGES_STORE_PATH);
			this.pagesStore = (PagesStore) resource.getContents().get(0);
		}
		return this.pagesStore;
	}

	/* == IStorageCdo ====================================================== */
	
	private boolean isActive = false;
	
	@Override
	public boolean isStorageActive() {
		return isActive;
	}

	@Override
	public void activateStorage() throws Exception {
		this.doConnect();
		isActive = true;
		System.out.println("--- Repository Activated. ---");
	}

	@Override
	public Exception deactivateStorage() {
		this.doDisconnect();

		return null;
	}

	@Deprecated
	@Override
	public AdapterFactory getAdapterFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends CDOObject> Object modify(T object, ITransactionalOperation<T> operation) {
		CDOTransaction transaction = this.session.openTransaction();

		try {
			T transactionalObject = transaction.getObject(object);
			Object result = operation.execute(transactionalObject, transaction);
			transaction.commit();

			if (result instanceof CDOObject) {
				return this.view.getObject((CDOObject) result);
			}
			return result;
		} catch (CommitException ex) {
			throw WrappedException.wrap(ex);
		} finally {
			transaction.close();
		}
	}

	// -- service support ---------------------------------

	public void setConfiguration(IWikiConfiguration wikiConfiguration) {
		this.wikiConfiguration = wikiConfiguration;
	}

	protected IWikiConfiguration getWikiConfiguration() {
		return this.wikiConfiguration;
	}

	@Deprecated
	public synchronized void startup() {
		//:FVK:	StorageCdoActivator.setStorageCdo(this);
	}

	public synchronized void shutdown() {
		//
	}
	
}
