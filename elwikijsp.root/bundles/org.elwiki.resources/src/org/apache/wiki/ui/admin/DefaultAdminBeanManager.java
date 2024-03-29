/*
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
 */
package org.apache.wiki.ui.admin;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.apache.wiki.api.Release;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.modules.ModuleManager;
import org.apache.wiki.api.modules.WikiModuleInfo;
import org.apache.wiki.ui.admin.beans.CoreBean;
import org.apache.wiki.ui.admin.beans.FilterBean;
import org.apache.wiki.ui.admin.beans.PluginBean;
import org.apache.wiki.ui.admin.beans.SearchManagerBean;
import org.apache.wiki.ui.admin.beans.UserBean;
import org.apache.wiki.ui.admin0.AdminBean;
import org.apache.wiki.ui.admin0.AdminBeanManager;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiComponent;
import org.elwiki.api.event.EngineEvent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * Provides a manager class for all AdminBeans within JSPWiki. This class also manages registration
 * for any AdminBean which is also a JMX bean.
 *
 * @since 2.5.52
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultAdminBeanManager",
	service = { AdminBeanManager.class, WikiComponent.class, EventHandler.class },
	property = {
		EventConstants.EVENT_TOPIC + "=" + EngineEvent.Topic.ALL,
	},
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultAdminBeanManager implements AdminBeanManager, WikiComponent, EventHandler {

	private ArrayList<AdminBean> m_allBeans;
	private MBeanServer m_mbeanServer;

	private static final Logger log = Logger.getLogger(DefaultAdminBeanManager.class);

	/**
	 * Constructs a new AdminBeanManager instance.
	 */
	public DefaultAdminBeanManager() {
		log.info("Using JDK 1.5 Platform MBeanServer");
		m_mbeanServer = MBeanServerFactory15.getServer();

		if (m_mbeanServer != null) {
			log.info(m_mbeanServer.getClass().getName());
			log.info(m_mbeanServer.getDefaultDomain());
		}
	}

	// -- OSGi service handling --------------------( start )--

	@WikiServiceReference
	private Engine m_engine;

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// nothing to do.
	}

	private void initializeStageTwo() throws WikiException {
		reload();
	}

	// -- OSGi service handling ----------------------( end )--

	private String getJMXTitleString(final int title) {
		switch (title) {
		case AdminBean.CORE:
			return "Core";

		case AdminBean.EDITOR:
			return "Editors";

		case AdminBean.UNKNOWN:
		default:
			return "Unknown";
		}
	}

	/**
	 * Register an AdminBean. If the AdminBean is also a JMX MBean, it also gets registered to the
	 * MBeanServer we've found.
	 *
	 * @param ab AdminBean to register.
	 */
	private void registerAdminBean(final AdminBean ab) {
		try {
			if (ab instanceof DynamicMBean && m_mbeanServer != null) {
				final ObjectName name = getObjectName(ab);
				if (!m_mbeanServer.isRegistered(name)) {
					m_mbeanServer.registerMBean(ab, name);
				}
			}

			m_allBeans.add(ab);

			log.info("Registered new admin bean " + ab.getTitle());
		} catch (final InstanceAlreadyExistsException e) {
			log.error("Admin bean already registered to JMX", e);
		} catch (final MBeanRegistrationException e) {
			log.error("Admin bean cannot be registered to JMX", e);
		} catch (final NotCompliantMBeanException e) {
			log.error("Your admin bean is not very good", e);
		} catch (final MalformedObjectNameException e) {
			log.error("Your admin bean name is not very good", e);
		} catch (final NullPointerException e) {
			log.error("Evil NPE occurred", e);
		}
	}

	private ObjectName getObjectName(final AdminBean ab) throws MalformedObjectNameException {
		final String component = getJMXTitleString(ab.getType());
		final String title = ab.getTitle();
		return new ObjectName(Release.APPNAME + ":component=" + component + ",name=" + title);
	}

	/**
	 * Registers all the beans from a collection of WikiModuleInfos. If some of the beans fail, logs the
	 * message and keeps going to the next bean.
	 *
	 * @param c Collection of WikiModuleInfo instances
	 */
	private void registerBeans(final Collection<WikiModuleInfo> c) {
		for (final WikiModuleInfo wikiModuleInfo : c) {
			final String abname = wikiModuleInfo.getAdminBeanClass();
			try {
				if (abname != null && abname.length() > 0) {
					final Class<?> abclass = Class.forName(abname);
					final AdminBean ab = (AdminBean) abclass.getDeclaredConstructor().newInstance();
					registerAdminBean(ab);
				}
			} catch (final Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	// FIXME: Should unload the beans first.
	private void reload() {
		m_allBeans = new ArrayList<>();

		try {
			registerAdminBean(new CoreBean(m_engine));
			registerAdminBean(new UserBean(m_engine));
			registerAdminBean(new SearchManagerBean(m_engine));
			registerAdminBean(new PluginBean(m_engine));
			registerAdminBean(new FilterBean(m_engine));
		} catch (final NotCompliantMBeanException e) {
			log.error(e.getMessage(), e);
		}
		for (final ModuleManager moduleManager : m_engine.getManagers(ModuleManager.class)) {
			registerBeans(moduleManager.modules());
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.wiki.ui.admin.AdminBeanManager#getAllBeans()
	 */
	@Override
	public List<AdminBean> getAllBeans() {
		if (m_allBeans == null) {
			reload();
		}

		return m_allBeans;
	}

	/* (non-Javadoc)
	 * @see org.apache.wiki.ui.admin.AdminBeanManager#findBean(java.lang.String)
	 */
	@Override
	public AdminBean findBean(final String id) {
		for (final AdminBean ab : m_allBeans) {
			if (ab.getId().equals(id)) {
				return ab;
			}
		}

		return null;
	}

	/**
	 * Provides a JDK 1.5-compliant version of the MBeanServerFactory. This will simply bind to the
	 * platform MBeanServer.
	 */
	private static final class MBeanServerFactory15 {
		private MBeanServerFactory15() {
		}

		public static MBeanServer getServer() {
			return ManagementFactory.getPlatformMBeanServer();
		}
	}

	/**
	 * Returns the type identifier for a string type.
	 *
	 * @param type A type string.
	 * @return A type value.
	 */
	@Override
	public int getTypeFromString(final String type) {
		if ("core".equals(type)) {
			return AdminBean.CORE;
		} else if ("editors".equals(type)) {
			return AdminBean.EDITOR;
		}

		return AdminBean.UNKNOWN;
	}

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		switch (topic) {
		// Initialize.
		case EngineEvent.Topic.INIT_STAGE_TWO:
			try {
				initializeStageTwo();
			} catch (WikiException e) {
				log.error("Failed initialization of references for DefaultAdminBeanManager.", e);
			}
			break;
		case EngineEvent.Topic.SHUTDOWN: {
			for (final AdminBean m_allBean : m_allBeans) {
				try {
					final ObjectName on = getObjectName(m_allBean);
					if (m_mbeanServer.isRegistered(on)) {
						m_mbeanServer.unregisterMBean(on);
						log.info("Unregistered AdminBean " + m_allBean.getTitle());
					}
				} catch (final MalformedObjectNameException e) {
					log.error("Malformed object name when unregistering", e);
				} catch (final InstanceNotFoundException e) {
					log.error("Object was registered; yet claims that it's not there", e);
				} catch (final MBeanRegistrationException e) {
					log.error("Registration exception while unregistering", e);
				}
			}
		}
			break;
		}
	}

}
