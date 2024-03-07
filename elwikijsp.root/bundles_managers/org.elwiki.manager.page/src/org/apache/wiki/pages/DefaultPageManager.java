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
package org.apache.wiki.pages;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.diff.DifferenceManager;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.RepositoryModifiedException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.providers.PageProvider;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.pages0.PageLock;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.pages0.PageSorter;
import org.apache.wiki.pages0.PageTimeComparator;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.workflow0.Decision;
import org.apache.wiki.workflow0.DecisionRequiredException;
import org.apache.wiki.workflow0.Fact;
import org.apache.wiki.workflow0.IWorkflowBuilder;
import org.apache.wiki.workflow0.Step;
import org.apache.wiki.workflow0.Workflow;
import org.apache.wiki.workflow0.WorkflowManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.elwiki.api.BackgroundThreads;
import org.elwiki.api.BackgroundThreads.Actor;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiComponent;
import org.elwiki.api.event.PageEvent;
import org.elwiki.api.event.SecurityEvent;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.WikiPrincipal;
import org.elwiki.pagemanager.internal.bundle.PageManagerActivator;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.UnknownPage;
import org.elwiki_data.WikiPage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

/**
 * Manages the WikiPages. This class functions as an unified interface towards the page providers. It handles initialization
 * and management of the providers, and provides utility methods for accessing the contents.
 * <p/>
 * Saving a page is a two-stage Task; first the pre-save operations and then the actual save. See the descriptions of the tasks
 * for further information.
 *
 * @since 2.0
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultPageManager",
	service = { PageManager.class, WikiComponent.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultPageManager implements PageManager, WikiComponent, EventHandler {

	private static final Logger log = Logger.getLogger(DefaultPageManager.class);

	static final String JSON_PAGESHIERARCHY = "pageshierarchyTracker";
	static final String JSON_ALLPAGESHIERARCHY = "allPagesHierarchyTracker";

	/** Idle lock reaping interval. Seconds.*/
	private static final int LOCK_REAPING_INTERVAL = 60;

	private static String ID_EXTENSION_PAGEPROVIDER = "pageProvider";

	private PageProvider m_provider;

	protected ConcurrentHashMap<String, PageLock> m_pageLocks = new ConcurrentHashMap<>();

	private int m_expiryTime;

	private Thread m_reaper = null;

	private PageSorter pageSorter = new PageSorter();

	private BundleContext bundleContext;
	
	/**
	 * Create instance of DefaultPageManager.
	 */
	public DefaultPageManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	// -- OSGi service handling ----------------------(start)--

    @Reference
    EventAdmin eventAdmin;
	
	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@Reference
	private GlobalPreferences globalPreferences;

	@WikiServiceReference
	private Engine m_engine;

	@WikiServiceReference
	private ReferenceManager referenceManager;

	@WikiServiceReference
	private TasksManager tasksManager;

	@WikiServiceReference
	private DifferenceManager differenceManager;

	@WikiServiceReference
	private AttachmentManager attachmentManager;

	@WikiServiceReference
	private WorkflowManager workflowManager;

	@Activate
	protected void startup(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		m_expiryTime = wikiConfiguration.getIntegerProperty(PROP_LOCKEXPIRY, 60);

		WikiAjaxDispatcher wikiAjaxDispatcher = m_engine.getManager(WikiAjaxDispatcher.class);
		wikiAjaxDispatcher.registerServlet(JSON_PAGESHIERARCHY, new JSONPagesHierarchyTracker(this.m_engine));
		wikiAjaxDispatcher.registerServlet(JSON_ALLPAGESHIERARCHY, new JSONAllPagesHierarchyTracker(this.m_engine));

		/*:FVK: - подключить CachingProvider
		final String classname;
        final boolean useCache = TextUtil.getBooleanProperty(props, PROP_USECACHE, true); 

		//  If user wants to use a cache, then we'll use the CachingProvider.
		if( useCache ) {
		    classname = "org.apache.wiki.providers.CachingProvider";
		} else {
		    classname = TextUtil.getRequiredProperty( props, PROP_PAGEPROVIDER );
		}
		
		pageSorter.initialize( props );
		
		try {
		    LOG.debug("Page provider class: '" + classname + "'");
		
		    final Class<?> providerclass = ClassUtil.findClass("org.apache.wiki.providers", classname);
		    m_provider = ( PageProvider ) providerclass.newInstance();
		
		    m_provider = new VersioningFileProvider(); 
		    		//new org.apache.wiki.providers.CachingProvider();
		
		    LOG.debug("Initializing page provider class " + m_provider);
		    m_provider.initialize(m_engine, props);
		} catch (final ClassNotFoundException e) {
			...
		*/

		try {
			String requiredId = getPreference(PageManager.Prefs.PAGE_MANAGER, String.class);			
			this.m_provider = getPageProvider(requiredId);
			/*TextUtil.getStringProperty(properties, PROP_PAGEPROVIDER, DEFAULT_PAGEPROVIDER)*/
			m_provider.initialize(m_engine); // :FVK: опционально, там пока нет кода.
		} catch (WikiException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// -- OSGi service handling ------------------------(end)--

	@Override
	public BundleContext getBundleContext() {
		return this.bundleContext;
	}

	/**
	 * Attempts to locate and initialize an PageProvider to use with this manager. Throws a WikiException
	 * if no entry is found, or if one fails to initialize.
	 * 
	 * @param requiredId required PageProvider ID for extension point.
	 * @return a PageProvider according to required ID.
	 * @throws WikiException
	 */
	private PageProvider getPageProvider(String requiredId) throws WikiException {
		String namespace = PageManagerActivator.PLIGIN_ID;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep;

		//
		// Load an PageProvider from Equinox extension "org.elwiki.manager.page.pageProvider".
		//
		Class<? extends PageProvider> clazzPageProvider = null;
		ep = registry.getExtensionPoint(namespace, ID_EXTENSION_PAGEPROVIDER);
		if (ep != null) {
			for (IConfigurationElement el : ep.getConfigurationElements()) {
				String contributorName = el.getContributor().getName();
				String className = el.getAttribute("class");
				String pageProviderId = el.getAttribute("id");
				if (pageProviderId.equals(requiredId)) {
					try {
						final Bundle bundle = Platform.getBundle(contributorName);
						Class<?> clazz = bundle.loadClass(className);
						try {
							clazzPageProvider = clazz.asSubclass(PageProvider.class);
						} catch (ClassCastException e) {
							log.fatal("Page provider " + className + " is not extends interface "
									+ PageProvider.class.getSimpleName(), e);
							throw new WikiException("Page provider " + className + " is not extends interface "
									+ PageProvider.class.getSimpleName(), e);
						}
					} catch (ClassNotFoundException e) {
						log.fatal("Page provider " + className + " cannot be found.", e);
						throw new WikiException("Page provider " + className + " cannot be found.", e);
					}
					break; // -- finalize for --
				}
			}
		}

		if (clazzPageProvider == null) {
			throw new NoRequiredPropertyException("Unable to find PageManager with ID=" + requiredId,
					PageManager.Prefs.PAGE_MANAGER);
		}

		PageProvider pageProvider = null;
		try {
			Class<?>[] parameterType = new Class[] { Engine.class };
			pageProvider = clazzPageProvider.getDeclaredConstructor(parameterType).newInstance(this.m_engine);
		} catch (InstantiationException | IllegalArgumentException e) {
			log.fatal("Page provider " + clazzPageProvider + " cannot be created.", e);
			throw new WikiException("Page provider " + clazzPageProvider + " cannot be created.", e);
		} catch (IllegalAccessException e) {
			log.fatal("You are not allowed to access page provider class " + clazzPageProvider, e);
			throw new WikiException("You are not allowed to access page provider class " + clazzPageProvider, e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug("Loaded page provider from extension: " + pageProvider);

		return pageProvider;
	}

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getProvider()
     */
    @Override
    public PageProvider getProvider() {
        return m_provider;
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getUpperPages()
     */
    @Override
    public Collection< WikiPage > getUpperPages() throws ProviderException {
    	return m_provider.getUpperPages();
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getAllPages()
     */
    @Override
    public Collection< WikiPage > getAllPages() throws ProviderException {
        return m_provider.getAllPages();
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getPageText(java.lang.String, int)
     */
    @Override
    public String getPageText( final String pageName, final int version ) throws ProviderException {
        if (pageName == null || pageName.length() == 0) {
            throw new ProviderException( "Illegal page name" );
        }
        String text;

        try {
            text = m_provider.getPageText( pageName, version );
        } catch ( final RepositoryModifiedException e ) {
            //  This only occurs with the latest version.
            log.info( "Repository has been modified externally while fetching page " + pageName );

            //  Empty the references and yay, it shall be recalculated
            final WikiPage p = m_provider.getPageInfo( pageName, version );

          //:FVK:this.referenceManager.updateReferences( p );
    		this.eventAdmin.sendEvent(new Event(PageEvent.Topic.REINDEX,
    				Map.of(PageEvent.PROPERTY_PAGE_ID, p.getId())));
            text = m_provider.getPageText( pageName, version );
        }

        return text;
    }

	@Override
	public String getPureText(WikiPage page, int version) {
		return (version == WikiProvider.LATEST_VERSION) ? //
				page.getContentText() : page.getContentText(version);
	}

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getPureText(String, int)
     */
    @Override
    public String getPureText( final String pageName, final int version ) {
        String result = null;
        try {
            result = getPageText( pageName, version );
        } catch( final ProviderException e ) {
            log.error( "ProviderException getPureText for page " + pageName + " [version " + version + "]", e );
        } finally {
            if( result == null ) {
                result = "";
            }
        }
        return result;
    }

	@Override
	public String getText(String pageName, int version) {
		final String result = getPureText(pageName, version);
		return TextUtil.replaceEntities(result);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(WikiPage page, int version) {
        //:FVK: --old code-- final String result = getPureText( page, version );
		String content = "";
		if (version == WikiProvider.LATEST_VERSION) {
			PageContent pc = page.getLastContent();
			if (pc != null) {
				content = pc.getContent();
			}
		} else {
			for (PageContent pageContent : page.getPageContents()) {
				if (pageContent.getVersion() == version) {
					content = pageContent.getContent();
					break;
				}
			}
		}
    	
        return TextUtil.replaceEntities( content );
    }

	@Override
	public void saveText(WikiContext context, String text, String author, String changenote) throws WikiException {
        // Check if page data actually changed; bail if not
        WikiPage page = context.getPage();
        String oldText = getPureText( page, context.getPageVersion() );
        String proposedText = TextUtil.normalizePostData( text );
        if ( oldText != null && oldText.equals( proposedText ) ) {
            return;
        }

        // Check if creation of empty pages is allowed; bail if not
		boolean allowEmpty = globalPreferences.isAllowCreationOfEmptyPages(); 
		if (!allowEmpty && !wikiPageExists(page) && text.trim().equals("")) {
			return;
		}

        // Create approval workflow for page save; add the diffed, proposed and old text versions as
        // Facts for the approver (if approval is required). If submitter is authenticated, any reject
        // messages will appear in his/her workflow inbox.
        IWorkflowBuilder builder = workflowManager.getWorkflowBuilder();
        Principal submitter = context.getCurrentUser();
        Step prepTask = this.tasksManager.buildPreSaveWikiPageTask( context, proposedText );
        Step completionTask = this.tasksManager.buildSaveWikiPageTask( context, author, changenote );
        String diffText = this.differenceManager.makeDiff( context, oldText, proposedText );
        boolean isAuthenticated = context.getWikiSession().isAuthenticated();
		List<Fact> facts = new ArrayList<>();
		facts.add(new Fact(WorkflowManager.WF_WP_SAVE_FACT_PAGE_NAME, page.getName()));
		facts.add(new Fact(WorkflowManager.WF_WP_SAVE_FACT_DIFF_TEXT, diffText));
		facts.add(new Fact(WorkflowManager.WF_WP_SAVE_FACT_PROPOSED_TEXT, proposedText));
		facts.add(new Fact(WorkflowManager.WF_WP_SAVE_FACT_CURRENT_TEXT, oldText));
		facts.add(new Fact(WorkflowManager.WF_WP_SAVE_FACT_IS_AUTHENTICATED, isAuthenticated));
        String rejectKey = isAuthenticated ? WorkflowManager.WF_WP_SAVE_REJECT_MESSAGE_KEY : null;
		Workflow workflow = builder.buildApprovalWorkflow(submitter, //
				WorkflowManager.WF_WP_SAVE_APPROVER, //
				prepTask, //
				WorkflowManager.WF_WP_SAVE_DECISION_MESSAGE_KEY, //
				facts, completionTask, rejectKey);
        workflow.start();

        // Let callers know if the page-save requires approval
        if ( workflow.getCurrentStep() instanceof Decision ) {
            throw new DecisionRequiredException( "The page contents must be approved before they become active." );
        }
    }

	/** {@inheritDoc} */
	@Override
	public void savePageComment(WikiContext wikiContext, String comment) throws WikiException {
		WikiPage page = wikiContext.getPage();
		m_provider.savePageComment(page, comment);		
	}

    /**
     * Returns the Engine to which this PageManager belongs to.
     *
     * @return The Engine object.
     */
    protected Engine getEngine() {
        return m_engine;
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#putPageText(org.apache.wiki.api.core.WikiPage, java.lang.String, String, String)
     */
    @Override
    public void putPageText( final WikiPage page, final String content, String author, String changenote ) throws ProviderException {
        if (page == null || page.getName() == null || page.getName().length() == 0) {
            throw new ProviderException("Illegal page name");
        }

        m_provider.putPageText(page, content, author, changenote);
    }
    
    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#lockPage(org.apache.wiki.api.core.WikiPage, java.lang.String)
     */
    @Override
    public PageLock lockPage( final WikiPage page, final String user ) {
		if (m_reaper == null) {
			// Start the lock reaper lazily.
			// We don't want to start it in the constructor, because starting threads in constructors
			// is a bad idea when it comes to inheritance.  Besides, laziness is a virtue.
			BackgroundThreads backgroundThreads = (BackgroundThreads) m_engine.getManager(BackgroundThreads.class);
			LockReaperActor lockReaperActor = new LockReaperActor();
			m_reaper = backgroundThreads.createThread("ElWiki Lock Reaper", LOCK_REAPING_INTERVAL, lockReaperActor);
			m_reaper.start();
		}

        // how - prior to or after actual lock?
		this.eventAdmin.sendEvent(new Event(PageEvent.Topic.LOCK,
				Map.of(PageEvent.PROPERTY_PAGE_ID, page.getId())));
        PageLock lock = m_pageLocks.get( page.getName() );

        if( lock == null ) {
            //
            //  Lock is available, so make a lock.
            //
            final Date d = new Date();
            lock = new PageLock( page, user, d, new Date( d.getTime() + m_expiryTime * 60 * 1000L ) );
            m_pageLocks.put( page.getName(), lock );
            log.debug( "Locked page " + page.getName() + " for " + user );
        } else {
            log.debug( "Page " + page.getName() + " already locked by " + lock.getLocker() );
            lock = null; // Nothing to return
        }

        return lock;
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#unlockPage(org.apache.wiki.pages0.PageLock)
     */
    @Override
    public void unlockPage( final PageLock lock ) {
        if (lock == null) {
            return;
        }

        m_pageLocks.remove( lock.getPageId() );
        log.debug( "Unlocked page " + lock.getPageId() );

		this.eventAdmin.sendEvent(new Event(PageEvent.Topic.UNLOCK,
				Map.of(PageEvent.PROPERTY_PAGE_ID, lock.getPageId())));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getCurrentLock(org.apache.wiki.api.core.WikiPage)
     */
    @Override
    public PageLock getCurrentLock( final WikiPage page ) {
        return m_pageLocks.get( page.getName() );
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getActiveLocks()
     */
    @Override
    public List< PageLock > getActiveLocks() {
        return  new ArrayList<>( m_pageLocks.values() );
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getPage(java.lang.String)
     */
    @Override
    public WikiPage getPage( final String pagereq ) {
        return getPage( pagereq, PageProvider.LATEST_VERSION );
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getPage(java.lang.String, int)
     */
    @Override
    public WikiPage getPage( final String pagereq, final int version ) {
        try {
            WikiPage p = getPageInfo( pagereq, version );
            if( p == null ) {
                p = null;
                //:FVK: попытка загрузки прикрепления 
                //:FVK: - это излишне так как AttachmentManager, похоже, надо упразднить.
                // p = Engine.getAttachmentManager().getAttachmentInfo( null, pagereq );
            }

            return p;
        } catch( final ProviderException e ) {
            log.error( "Unable to fetch page info for \"" + pagereq + "\" [version: " + version + "]", e );
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getPageInfo(java.lang.String, int)
     */
    @Override
    public WikiPage getPageInfo( final String pageName, final int version) throws ProviderException {
        if( pageName == null || pageName.length() == 0 ) {
            throw new ProviderException( "Illegal page name '" + pageName + "'" );
        }

        WikiPage page;

        try {
            page = m_provider.getPageInfo( pageName, version );
        } catch( final RepositoryModifiedException e ) {
            //  This only occurs with the latest version.
            log.info( "Repository has been modified externally while fetching info for " + pageName );
            page = m_provider.getPageInfo( pageName, version );
            if( page != null ) {
            	//:FVK:this.referenceManager.updateReferences( page );
            } else {
            	this.referenceManager.pageRemoved( Wiki.contents().page( pageName ) );
            }
        }

        return page;
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getVersionHistory(java.lang.String)
     */
    @Override
	public List<PageContent> getVersionHistory(WikiPage page) {
		List<PageContent> c = null;

		try {
			if (pageExists(page)) {
				c = m_provider.getVersionHistory(page);
			}

			/*:FVK: - это излишне так как AttachmentManager, похоже, надо упразднить.
			if( c == null ) {
			    c = ( List< T > )Engine.getAttachmentManager().getVersionHistory( pageName );
			}*/
		} catch (final ProviderException e) {
			log.error("ProviderException requesting version history for " + page.getName(), e);
		}

		return c;
	}

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getCurrentProvider()
     */
    @Override 
    public String getCurrentProvider() {
        return getProvider().getClass().getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.wiki.pages0.PageManager#getProviderDescription()
     */
    @Override 
    public String getProviderDescription() {
        return m_provider.getProviderInfo();
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getTotalPageCount()
     */
    @Override
    public int getTotalPageCount() {
        try {
            return m_provider.getAllPages().size();
        } catch( final ProviderException e ) {
            log.error( "Unable to count pages: ", e );
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getRecentChanges()
     */
    @Override
    public Set< WikiPage > getRecentChanges() {
        try {
            final TreeSet< WikiPage > sortedPages = new TreeSet<>( new PageTimeComparator() );
            sortedPages.addAll( getAllPages() );
          //:FVK: sortedPages.addAll( Engine.getAttachmentManager().getAllAttachments() );

            return sortedPages;
        } catch( final ProviderException e ) {
            log.error( "Unable to fetch all pages: ", e );
            return Collections.emptySet();
        }
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#pageExists(java.lang.String)
     */
    @Override
    public boolean pageExists( final String pageName ) throws ProviderException {
        if (pageName == null || pageName.length() == 0) {
            throw new ProviderException("Illegal page name");
        }

        return m_provider.pageExistsByName(pageName);
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#pageExists(java.lang.String, int)
     */
    @Override
    public boolean pageExists( final String pageName, final int version ) throws ProviderException {
        if( pageName == null || pageName.length() == 0 ) {
            throw new ProviderException( "Illegal page name" );
        }

        if( version == WikiProvider.LATEST_VERSION ) {
            return pageExists( pageName );
        }

        return m_provider.pageExists( pageName, version );
    }

    @Override
    public boolean pageExistsById(String pageId) {
    	return this.m_provider.pageExistsById(pageId);
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#pageExistsByName(java.lang.String)
     */
    @Override
    public boolean pageExistsByName( final String pageName ) {
    	return this.m_provider.pageExistsByName(pageName);
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#wikiPageExists(java.lang.String, int)
     */
    @Override
    public boolean wikiPageExists( final String page, final int version ) throws ProviderException {
    	return this.m_provider.pageExists(page, version);
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#deleteVersion(org.apache.wiki.api.core.WikiPage)
     */
    @Override
    public void deleteVersion( final WikiPage page ) throws ProviderException {
    	/*:FVK: моя реализация - не объединяет в один тип присоединения и страницы. 
        if( page instanceof PageAttachment ) {
            Engine.getAttachmentManager().deleteVersion( ( PageAttachment )page );
        } else */
    	{
        	//:FVK: m_provider.deleteVersion( page.getName(), page.getVersion() );
            // FIXME: If this was the latest, reindex Lucene, update RefMgr
        }
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#deletePage(java.lang.String)
     */
    @Override
    public void deletePage( final String pageName ) throws ProviderException {
        final WikiPage p = getPage( pageName );
        String pageId = p.getId();

        if( p != null ) {
        	/*:FVK: моя реализация - не объединяет в один тип присоединения и страницы.
            if( p instanceof PageAttachment ) {
                Engine.getAttachmentManager().deleteAttachment( ( PageAttachment )p );
            } else*/ 
            {
                final Collection< String > refTo = this.referenceManager.findRefersTo( pageName );
                // May return null, if the page does not exist or has not been indexed yet.

                /*:FVK: - это излишне так как AttachmentManager, похоже, надо упразднить.
                if( Engine.getAttachmentManager().hasAttachments( p ) ) {
                    final List< PageAttachment > attachments = Engine.getAttachmentManager().listAttachments( p );
                    for( final PageAttachment attachment : attachments ) {
                        if( refTo != null ) {
                            refTo.remove( attachment.getName() );
                        }

                        Engine.getAttachmentManager().deleteAttachment( attachment );
                    }
                }*/
                deletePage( p );
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#deletePage(org.apache.wiki.api.core.WikiPage)
     */
    @Override
	public void deletePage(final WikiPage page) throws ProviderException {
        String pageId = page.getId();
		this.eventAdmin.sendEvent(new Event(PageEvent.Topic.DELETE_REQUEST,
				Map.of(PageEvent.PROPERTY_PAGE_ID, pageId)));

		List<String> attachmentPlace = new ArrayList<>();
		for (PageAttachment att : page.getAttachments()) {
			for (AttachmentContent content : att.getAttachContents()) {
				attachmentPlace.add(content.getPlace());
			}
		}

		if (m_provider.deletePage(page.getName())) {
			attachmentManager.releaseAttachmentStore(attachmentPlace);
    		this.eventAdmin.sendEvent(new Event(PageEvent.Topic.DELETED,
    				Map.of(PageEvent.PROPERTY_PAGE_ID, pageId)));
		}
	}

	/**
	 * This is a simple reaper thread that runs roughly every minute or so (it's not really that
	 * important, as long as it runs), and removes all locks that have expired.
	 */
	private class LockReaperActor extends Actor {

		/**
		 * Create a LockReaper.
		 */
		public LockReaperActor() {

		}

		@Override
		public void backgroundTask() throws Exception {
			final Collection<PageLock> entries = m_pageLocks.values();
			for (final Iterator<PageLock> i = entries.iterator(); i.hasNext();) {
				final PageLock p = i.next();

				if (p.isExpired()) {
					i.remove();

					log.debug("Reaped lock: " + p.getPageId() + " by " + p.getLocker() + ", acquired "
							+ p.getAcquisitionTime() + ", and expired " + p.getExpiryTime());
				}
			}
		}

	}

    /**
     * For a single wiki page, replaces all Acl entries matching a supplied array of Principals with a new Principal.
     *
     * @param page the wiki page whose Acl is to be modified
     * @param oldPrincipals an array of Principals to replace; all AclEntry objects whose {@link AclEntry#getPrincipal()} method returns
     *                      one of these Principals will be replaced
     * @param newPrincipal the Principal that should receive the old Principals' permissions
     * @return <code>true</code> if the Acl was actually changed; <code>false</code> otherwise
     */
	/*:FVK: TODO: old ACL code.
    protected boolean changeAcl( final WikiPage page, final Principal[] oldPrincipals, final Principal newPrincipal ) {
        final Acl acl = page.getAcl();
        boolean pageChanged = false;
        if( acl != null ) {
            EList<AclEntry> entries = acl.getAclEntries();
            Iterator<AclEntry> iter = entries.iterator();
            final Collection< AclEntry > entriesToAdd = new ArrayList<>();
            final Collection< AclEntry > entriesToRemove = new ArrayList<>();
            while( iter.hasNext() ) {
                final AclEntry entry = iter.next();
                if( ArrayUtils.contains( oldPrincipals, entry.getPrincipal() ) ) {
                    // Create new entry
                    final AclEntry newEntry = Wiki.acls().entry();
                    newEntry.setPrincipal( newPrincipal );
                    EList<Permission> permissions = entry.getPermission();
                    for(Permission permission:entry.getPermission()) {
                    	newEntry.getPermission().add(permission);
                    }
                    pageChanged = true;
                    entriesToRemove.add( entry );
                    entriesToAdd.add( newEntry );
                }
            }
            for( final AclEntry entry : entriesToRemove ) {
                //:FVK: acl.removeEntry( entry );
            }
            for( final AclEntry entry : entriesToAdd ) {
            	//:FVK: acl.addEntry( entry );
            }
        }
        return pageChanged;
    }
    */

    /**
     * {@inheritDoc}
     * @see org.apache.wiki.pages0.PageManager#getPageSorter()
     */
    @Override
    public PageSorter getPageSorter() {
        return pageSorter;
    }

	@Override
	public WikiPage getPageById(String pageId) throws ProviderException {
		WikiPage page = null;
		page = this.m_provider.getPageById(pageId);
		return page;
	}
	
	@Override
	public String getMainPageId() {
		String mainPageId = this.m_provider.getMainPageId();

		return mainPageId;
	}

	@Override
	public WikiPage createPage(String pageName, String parentPageId) throws ProviderException {
		final WikiPage p = getPageById( parentPageId );
		return m_provider.createPage( pageName, "", p );		
	}

	@Override
	public void addAttachment(WikiPage wikiPage, AttachmentContent attContent, String attName) throws Exception {
		m_provider.addAttachment(wikiPage, attContent, attName);		
	}
	
	@Override
	public PageAttachment getPageAttachmentById(String pageAttachmentId) throws Exception {
		PageAttachment pageAttachment = null;
		pageAttachment = this.m_provider.getPageAttachmentById(pageAttachmentId);
		return pageAttachment;
	}

	@Override
	public void updateReferences(WikiPage page, Collection<String> pagesIds, Collection<String> unknownPages)
			throws ProviderException {
		m_provider.updateReferences(page, pagesIds, unknownPages);
	}

	@Override
	public List<PageReference> getPageReferrers(String pageId) throws WikiException {
		return m_provider.getPageReferrers(pageId);
	}

	@Override
	public Collection<WikiPage> getReferrersToUncreatedPages() throws ProviderException {
		List<UnknownPage> unknownPages = m_provider.getUnknownPages();
		Set<WikiPage> wikiPages = new HashSet<>();
		for (UnknownPage unknownPage : unknownPages) {
			WikiPage wikiPage = unknownPage.getWikipage();
			if (wikiPage != null) {
				wikiPages.add(wikiPage);
			}
		}
		return wikiPages;
	}

	@Override
	public Collection<UnknownPage> getUnknownPages() throws ProviderException {
		List<UnknownPage> unknownPages = m_provider.getUnknownPages();
		return unknownPages;
	}

	@Override
	public Collection<WikiPage> getUnreferencedPages() throws ProviderException {
		Collection<WikiPage> unreferencedPages = m_provider.getAllPages();
		Collection<PageReference> pageReferences = m_provider.getPageReferences();
		Set<String> referencedId = pageReferences.stream().map(PageReference::getPageId).collect(Collectors.toSet());

		unreferencedPages.removeIf(page -> referencedId.contains(page.getId()));

		return unreferencedPages;
	}

	/**
     * Listens for {@link SecurityEvent.Topic.PROFILE_NAME_CHANGED}
     * events. If a user profile's name changes, each page ACL is inspected. If an entry contains
     * a name that has changed, it is replaced with the new one. No events are emitted
     * as a consequence of this method, because the page contents are still the same; it is
     * only the representations of the names within the ACL that are changing.
	 */
	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		switch (topic) {
		case SecurityEvent.Topic.PROFILE_NAME_CHANGED:
			UserProfile[] profiles = (UserProfile[])event.getProperty(SecurityEvent.PROPERTY_PROFILES);
			Principal[] oldPrincipals = new Principal[] { //
					new WikiPrincipal(profiles[0].getLoginName()), //
					new WikiPrincipal(profiles[0].getFullname()), //
					new WikiPrincipal(profiles[0].getWikiName()) };
			final Principal newPrincipal = new WikiPrincipal(profiles[1].getFullname());

            // Examine each page ACL
            try {
                int pagesChanged = 0;
                final Collection< WikiPage > pages = getAllPages();
                for( final WikiPage page : pages ) {
                	//:FVK: TODO:...
                	/* here old code (with page's ACL
                    final boolean aclChanged = changeAcl( page, oldPrincipals, newPrincipal );
                    if( aclChanged ) {
                        // If the Acl needed changing, change it now
                        try {
                            this.aclManager.setPermissions( page, page.getAcl() );
                        } catch( final WikiSecurityException e ) {
                            log.error("Could not change page ACL for page " + page.getName() + ": " + e.getMessage(), e);
                        }
                        pagesChanged++;
                    }
                	*/
                }
                log.info( "Profile name change for '" + newPrincipal.toString() + "' caused " + pagesChanged + " page ACLs to change also." );
            } catch( final ProviderException e ) {
                // Oooo! This is really bad...
                log.error( "Could not change user name in Page ACLs because of Provider error:" + e.getMessage(), e );
            }
			break;
		}		
	}

	/** {@inheritDoc} */
	@Override
	public void movePage(PageMotionType motionType, String targetPageId, String movedPageId) throws ProviderException {
		m_provider.movePage(motionType, targetPageId, movedPageId);
	}

}
