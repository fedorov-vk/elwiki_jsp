package org.elwiki.storage.cdo.internal.service;

import org.apache.log4j.Logger;
import org.apache.wiki.api.IStorageCdo;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.jaxb_rw.archive.JAXBdataIO;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PagesStore;

/**
 * @author vfedorov
 */
public class StorageCdo extends Lifecycle implements IStorageCdo {

	protected static final Logger log = Logger.getLogger(StorageCdo.class);

	/** The property name for specifying host of CDO repository. Value is <tt>{@value}</tt>. */
	private static final String STORAGE_CDO_HOST = "storage.cdo.host";
	/** The property name for specifying port of CDO repository. Value is <tt>{@value}</tt>. */
	private static final String STORAGE_CDO_PORT = "storage.cdo.port";
	/** The property name for specifying name of CDO repository. Value is <tt>{@value}</tt>. */
	private static final String STORAGE_CDO_REPOSITORY = "storage.cdo.repository";

	private IWikiConfiguration wikiConfiguration;

	private ComposedAdapterFactory adapterFactory;

	private CDONet4jSession session;

	protected CDOView view;

	private PagesStore pagesStore;

	// == CODE ================================================================

	/**
	 * Constructor.
	 */
	public StorageCdo() {
		this.adapterFactory = new ComposedAdapterFactory(EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry());
		System.out.println("--FVK------------------------- INSTANCE:: " + this + "," + this.hashCode());
	}

	@Override
	public boolean isStorageActive() {
		return this.isActive();
	}

	JAXBdataIO jaxbDataIO = new JAXBdataIO();
	
	@Override
	public void activateStorage() throws Exception {
		try {
			// makes this before invokes secureLogin(). Due to this is initialisation the CDO access.
			this.activate();
			System.out.println("-- StorageCdo activated.");
			// jaxbDataIO.startWrite(getPagesStore());
		} catch (LifecycleException e) {
			//:FVK:			MessageDialog.openError(Display.getDefault().getActiveShell(), "Ошибка",
			//					"Ошибка связи с сервером данных.\n\n" + e.getMessage());
			// DbCoreLog.logError(e);
			throw new Exception("Ошибка связи с сервером данных.", e); 
		}
	}

	@Override
	public Exception deactivateStorage() {
		return this.deactivate();
	}

	@Override
	public AdapterFactory getAdapterFactory() {
		return this.adapterFactory;
	}

	@Override
	protected void doActivate() throws Exception {
		super.doActivate();

		IPreferenceStore props = getWikiConfiguration().getWikiPreferences();
		String host = props.getString(STORAGE_CDO_HOST);
		String port = props.getString(STORAGE_CDO_PORT);
		String repository = props.getString(STORAGE_CDO_REPOSITORY);

		log.debug(String.format("%s:%s - %s", host, port, repository));
		/* :FVK: -- workaround - for testing */
		if (host.length() == 0)
		{
			host = "localhost";
		}
		if (port.length() == 0) {
			port = "2037";
		}
		if (repository.length() == 0) {
			repository = "inventory";
		}
		log.debug(String.format("%s:%s - %s", host, port, repository));

		/*
		 * :FVK: -- old version. IConnector connector =
		 * Net4jUtil.getConnector(IPluginContainer.INSTANCE, "tcp", server);
		 * CDONet4jSessionConfiguration config =
		 * CDONet4jUtil.createNet4jSessionConfiguration();
		 * config.setConnector(connector); config.setRepositoryName(repository);
		 */

		// CDONet4jSessionConfiguration / workaround.
		CDONet4jSessionConfiguration sessionConfiguration = CDONet4jUtil.createNet4jSessionConfiguration();
		IManagedContainer container = IPluginContainer.INSTANCE;
		Net4jUtil.prepareContainer(container); // Register Net4j factories
		TCPUtil.prepareContainer(container); // Register TCP factories
		CDONet4jUtil.prepareContainer(container); // Register CDO factories
		try {
			sessionConfiguration.setConnector(Net4jUtil.getConnector(container, "tcp", host + ":" + port));
		} catch (Exception e) {
			// DbCoreLog.logError(e);
			throw e;
		}
		sessionConfiguration.setRepositoryName(repository);

		// TODO01: set user ID.
		// sessionConfiguration.setUserID(PluginActivator.getUserID());

		// Таймаут операций с CDO сервером - задан в 1 минуту (тик - 1ms).
		int SIGNAL_TIME_OUT = 60 * 1000;
		sessionConfiguration.setSignalTimeout(SIGNAL_TIME_OUT);
		// CDONet4jSession session = sessionConfiguration.openNet4jSession();

		this.session = sessionConfiguration.openNet4jSession();
		/*
		 * int COMMIT_TIMEOUT = 500; // seconds. Options options =
		 * session.options(); options.setCommitTimeout(COMMIT_TIMEOUT);
		 */
		this.view = this.session.openView();
		this.view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

		// initialiseDataStructure();
		// setListeners0();
		// setListeners1();
	}

	@Override
	protected void doDeactivate() throws Exception {
		this.session.close();
		this.session = null;
		this.view = null;
		// :FVK: this.locationArea = null;
		// :FVK: this.station = null;
		super.doDeactivate();
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
			String path = "Pages/"; // workaround.
			if (!this.view.hasResource(path)) {
				PagesStore ps = Elwiki_dataFactory.eINSTANCE.createPagesStore();
				createResource(path, ps);
			}
			CDOResource resource = this.view.getResource(path);
			this.pagesStore = (PagesStore) resource.getContents().get(0);
		}
		return this.pagesStore;
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
