package org.elwiki.data.persist.internal;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wiki.api.IStorageCdo;
import org.apache.wiki.api.exceptions.WikiException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.net4j.util.WrappedException;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.component.WikiManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki.data.persist.IDataStore;
import org.elwiki.data.persist.json.JsonDeserialiser;
import org.elwiki.data.persist.json.JsonSerialiser;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PagesStore;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Provides access to the CDO repository.
 * 
 * @author vfedorov
 */
//@formatter:off
@Component(
	name = "elwiki.StorageCdo",
	service = { IStorageCdo.class, WikiManager.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DataStore extends Repository implements IDataStore, IStorageCdo {

	protected static final Logger log = Logger.getLogger(DataStore.class);

	protected static final String H2_CACHE_SIZE = "elwiki.h2.cache_size";
	protected static final String H2_CACHE_SIZE_DEFAULT = "131072";

	private static final String PAGES_STORE_PATH = "Pages/";

	private PagesStore pagesStore;

	private ScopedPreferenceStore prefs;

	/**
	 * Instantiate DataStore.
	 */
	public DataStore() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@Reference
	private GlobalPreferences globalPreferences;

	@Activate
	protected void startup(BundleContext bc) {
		String bundleName = bc.getBundle().getSymbolicName();
		this.prefs = new ScopedPreferenceStore(InstanceScope.INSTANCE, bundleName);
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// nothing to do.	
	}

	// -- OSGi service handling ------------------------(end)--

	@Override
	protected String getFolder() {
		return globalPreferences.getDbPlace().toString();
	}

	@Override
	protected String getH2CacheSize() {
		String h2CacheSize = this.prefs.getDefaultString(H2_CACHE_SIZE);
		try {
			Integer.parseInt(h2CacheSize);
			return h2CacheSize;
		} catch (NumberFormatException e) {
			return H2_CACHE_SIZE_DEFAULT;
		}
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

			if (result instanceof CDOObject cdoObject) {
				return this.view.getObject(cdoObject);
			}
			return result;
		} catch (CommitException ex) {
			throw WrappedException.wrap(ex);
		} finally {
			transaction.close();
		}
	}

	@Override
	public void loadAllContent() throws IOException {
		JsonDeserialiser jsonDeserialiser = new JsonDeserialiser(globalPreferences);
		jsonDeserialiser.LoadData();
	}

	@Override
	public void saveAllContent() throws IOException {
		JsonSerialiser jsonSerialiser = new JsonSerialiser(globalPreferences);
		jsonSerialiser.SaveData();
	}

}
