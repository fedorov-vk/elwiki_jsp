package org.elwiki.data.persist.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.IDGeneration;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.VersioningMode;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.h2.jdbcx.JdbcDataSource;

/**
 * Provides:</br>
 * - location of DB H2</br>
 * - access to the CDO repository
 * 
 * @author vfedorov
 */
abstract class Repository implements IPasswordCredentialsProvider {

	private String name = "repo_elwiki";

	private boolean tcpDisabled = false;
	private int tcpPort = 2038;

	private IRepository repository;

	private IAcceptor tcpAcceptor;

	private VersioningMode versioningMode = VersioningMode.Normal; // :FVK:
	private IDGeneration idGeneration = IDGeneration.Counter;

	protected CDOSession session;
	protected CDOView view;

	public CDOSession getSession() {
		return session;
	}

	/**
	 * Open: session, view.
	 */
	public void doConnect() {
		this.session = openSession();
		/*
		  CDOBranchManager branchManager = session.getBranchManager(); branchManager.addListener(branchManagerListener);
		  CDOBranch mainBranch = branchManager.getMainBranch(); mainBranch.addListener(mainBranchListener);
		 */
		this.view = this.session.openView();

	}

	/**
	 * Close session.
	 */
	public void doDisconnect() {
		try {
			closeSession();
		} finally {
			session = null;
		}

		LifecycleUtil.deactivate(tcpAcceptor);
		tcpAcceptor = null;

		LifecycleUtil.deactivate(repository);
		repository = null;
	}

	protected void closeSession() {
		session.close();
	}

	abstract protected String getFolder();

	abstract protected String getH2CacheSize();

	/**
	 * Repository name.
	 * 
	 * @return Repository name.
	 */
	private String getName() {
		return name;
	}

	public final String getConnectorType() {
		return "jvm";
	}

	public final String getConnectorDescription() {
		return "local";
	}

	public final VersioningMode getVersioningMode() {
		return versioningMode;
	}

	private IDGeneration getIDGeneration() {
		return idGeneration;
	}

	protected Map<String, String> getRepositoryProperties() {
		VersioningMode versioningMode = getVersioningMode();

		Map<String, String> props = new HashMap<>();
		props.put(IRepository.Props.OVERRIDE_UUID, ""); // :FVK: == inventory ?
		props.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(versioningMode.isSupportingAudits()));
		props.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(versioningMode.isSupportingBranches()));
		props.put(IRepository.Props.ID_GENERATION_LOCATION, getIDGeneration().getLocation().toString());
		return props;
	}

	public CDOSession openSession() {
		String repositoryName = getName();

		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:" + getFolder() + "/" + getName() + ";CACHE_SIZE=" + getH2CacheSize());

		IDBStore store = getStore(dataSource);
		repository = CDOServerUtil.createRepository(repositoryName, store, getRepositoryProperties());

		IManagedContainer container = getContainer();
		CDOServerUtil.addRepository(container, repository); // :FVK: зачем? (там фигурирует Lifecycle ...)

		String connectorType = getConnectorType();
		String connectorDescription = getConnectorDescription();
		Net4jUtil.getAcceptor(container, connectorType, connectorDescription);

		if (!tcpDisabled) {
			tcpAcceptor = Net4jUtil.getAcceptor(container, "tcp", "0.0.0.0:" + tcpPort);
		}

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

		CDOSessionConfiguration sessionConfiguration = createSessionConfiguration();
		sessionConfiguration.setPassiveUpdateEnabled(true);
		sessionConfiguration.setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
		sessionConfiguration.setCredentialsProvider(this);

		CDOSession session = sessionConfiguration.openSession();
		session.options().setGeneratedPackageEmulationEnabled(true);
		session.options().setLobCache(new CDOLobStoreImpl(new File(getFolder(), "lobs")));

		/*if (SET_USER_NAME && StringUtil.isEmpty(session.getUserID()))
		{
		}
		*/

		return session;
	}

	protected IDBStore getStore(JdbcDataSource dataSource) {
		VersioningMode versioningMode = VersioningMode.Normal;
		boolean supportingAudits = versioningMode.isSupportingAudits();
		boolean supportingBranches = versioningMode.isSupportingBranches();

		IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(supportingAudits,
				supportingBranches, false);
		mappingStrategy.setProperties(getMappingStrategyProperties());

		IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
		IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
		IDBStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

		return store;
	}

	protected Map<String, String> getMappingStrategyProperties() {
		Map<String, String> props = new HashMap<>();
		props.put(IMappingStrategy.Props.QUALIFIED_NAMES, "true");
		props.put(CDODBUtil.PROP_COPY_ON_BRANCH, "true");
		return props;
	}

	public IManagedContainer getContainer() {
		return IPluginContainer.INSTANCE;
	}

	protected CDOSessionConfiguration createSessionConfiguration() {
		IConnector connector = getConnector();

		CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
		config.setConnector(connector);
		config.setRepositoryName(getName());

		/*if (READABLE_IDS)
		{
		}*/

		return config;
	}

	protected IConnector getConnector() {
		IManagedContainer container = getContainer();
		return Net4jUtil.getConnector(container, getConnectorType(), getConnectorDescription());
	}

	@Override
	public boolean isInteractive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPasswordCredentials getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}
}
