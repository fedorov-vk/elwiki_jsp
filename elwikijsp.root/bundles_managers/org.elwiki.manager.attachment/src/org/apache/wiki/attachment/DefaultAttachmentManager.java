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
package org.apache.wiki.attachment;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.attachment.IDynamicAttachment;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAttachment;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.engine.Initializable;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.providers.AttachmentProvider;
import org.apache.wiki.api.providers.PageProvider;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.api.search.SearchManager;
//:FVK:import org.apache.wiki.attachment0.DynamicAttachment;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.providers.BasicAttachmentProvider;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.util.ClassUtil;
import org.apache.wiki.util.TextUtil;
import org.eclipse.emf.common.util.EList;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.services.ServicesRefs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 *  Default implementation for {@link AttachmentManager}
 *
 * {@inheritDoc}
 *
 *  @since 1.9.28
 */
@Component(name = "elwiki.DefaultAttachmentManager", service = AttachmentManager.class, //
		factory = "elwiki.AttachmentManager.factory")
public class DefaultAttachmentManager implements AttachmentManager, Initializable {

    /** List of attachment types which are forced to be downloaded */
	@Deprecated
    private String[] m_forceDownloadPatterns;

    private static final Logger log = Logger.getLogger( DefaultAttachmentManager.class );
    private AttachmentProvider m_provider;
    private Engine m_engine;
    @Deprecated
    private CacheManager m_cacheManager = CacheManager.getInstance();
    @Deprecated
    private Cache m_dynamicAttachments;

	/**
     * Creates instance of DefaultAttachmentManager.
     */
    public DefaultAttachmentManager() {
		super();
		// TODO Auto-generated constructor stub
	}

    // -- service handling ---------------------------(start)--

    @WikiServiceReference
    private PageManager pageManager;

    @WikiServiceReference
    private ReferenceManager referenceManager;

    /**
     * This component activate routine. Does all the real initialization.
     * 
     * @param componentContext
     * @throws WikiException
     */
    @Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		Object obj = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
		if (obj instanceof Engine engine) {
			initialize(engine);
		}
	}
    
    // -- service handling -----------------------------(end)--

	/**
     *  Creates a new AttachmentManager.  Note that creation will never fail, but it's quite likely that attachments do not function.
     *  <p><b>DO NOT CREATE</b> an AttachmentManager on your own, unless you really know what you're doing. Just use
     *  Wikiengine.getAttachmentManager) if you're making a module for JSPWiki.
     *
     *  @param engine The wikiengine that owns this attachment manager.
     *  @param props A list of properties from which the AttachmentManager will seek its configuration. Typically this is the "jspwiki.properties".
     */
    // FIXME: Perhaps this should fail somehow.
	@Override
	public void initialize(Engine engine) throws WikiException {
    //public DefaultAttachmentManager( final Engine engine, final Properties props ) {
        m_engine = engine;

        try {
			m_provider = new BasicAttachmentProvider(); 
			m_provider.initialize( m_engine );
		} catch (NoRequiredPropertyException e1) {
            log.error( "Attachment provider did not find a property that it needed: " + e1.getMessage(), e1 );
            m_provider = null; // No, it did not work.
		} catch (IOException e1) {
            log.error( "Attachment provider reports IO error", e1 );
            m_provider = null;
		}

		m_forceDownloadPatterns = new String[0];
    }

	/** {@inheritDoc} */
    @Override
    public boolean attachmentsEnabled() {
        return m_provider != null;
    }

    /** {@inheritDoc} */
    @Override
    public String getAttachmentName( final WikiContext context, final String attachmentname ) {
        final AttachmentContent att;
        try {
            att = getAttachmentContent( context, attachmentname );
        } catch( final ProviderException e ) {
            log.warn( "Finding attachments failed: ", e );
            return null;
        }

        if( att != null ) {
            return att.getPageAttachment().getName();
        } else if( attachmentname.indexOf( '/' ) != -1 ) {
            return attachmentname;
        }

        return null;
    }

    /** {@inheritDoc} 
     * @throws ProviderException */
	@Override
	public AttachmentContent getAttachmentContent(WikiPage wikiPage, String attachmentName, int... version)
			throws ProviderException {
		AttachmentContent attachmentInfo = m_provider.getAttachmentContent(wikiPage, attachmentName, version);

		return attachmentInfo;
	}

    /** {@inheritDoc} */
    @Override
	public AttachmentContent getAttachmentContent(WikiContext context, String attachmentName, int version)
			throws ProviderException {
        if( m_provider == null ) {
            return null;
        }

        WikiPage currentPage = null;

        if( context != null ) {
            currentPage = context.getPage();
        }

        // If the page cannot be determined, we cannot possibly find the attachments.
        if(currentPage == null) {
            return null;
        }

        AttachmentContent attachmentContent = m_provider.getAttachmentContent(currentPage, attachmentName, version);

        return attachmentContent;
    }

    /** {@inheritDoc} */
    @Override
    public List< PageAttachment > listAttachments( final WikiPage wikipage ) throws ProviderException {
        if( m_provider == null ) {
            return new ArrayList<>();
        }

        final List< PageAttachment > atts = new ArrayList<>( m_provider.listAttachments( wikipage ) );
        atts.sort( Comparator.comparing( PageAttachment::getName, this.pageManager.getPageSorter() ) );

        return atts;
    }

    /** {@inheritDoc} */
    @Override
    public boolean forceDownload( String name ) {
        if( name == null || name.length() == 0 ) {
            return false;
        }

        name = name.toLowerCase();
        if( name.indexOf( '.' ) == -1 ) {
            return true;  // force download on attachments without extension or type indication
        }

        for( final String forceDownloadPattern : m_forceDownloadPatterns ) {
            if( name.endsWith( forceDownloadPattern ) && forceDownloadPattern.length() > 0 ) {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getAttachmentStream( final WikiContext ctx, final AttachmentContent att ) throws ProviderException, IOException {
        if( m_provider == null ) {
            return null;
        }

        /*:FVK:
        if( att instanceof DynamicAttachment ) {
            return ( ( DynamicAttachment )att ).getProvider().getAttachmentData( ctx, att );
        }
        */

        return m_provider.getAttachmentData( att );
    }

    /** {@inheritDoc} */
    @Override
    public void storeDynamicAttachment( final WikiContext ctx, final IDynamicAttachment att ) {
        m_dynamicAttachments.put( new Element( att.getName(), att ) );
    }

    /** {@inheritDoc} */
    /*:FVK:
    @Override
    public DynamicAttachment getDynamicAttachment( final String name ) {
        final Element element = m_dynamicAttachments.get( name );
        if( element != null ) {
            return ( DynamicAttachment )element.getObjectValue();
        } else {
            // Remove from cache, it has expired.
            m_dynamicAttachments.put( new Element( name, null ) );
            return null;
        }
    }
    */


	@Override
	public void storeAttachment(WikiPage wikiPage, AttachmentContent attContent, String attName, InputStream data)
			throws IOException, ProviderException {
		if (m_provider == null) {
			return;
		}

		// Checks if the actual, real page exists without any modifications or aliases.
		// We cannot store an attachment to a non-existent page.
		if (wikiPage == null || !ServicesRefs.getPageManager().pageExists(wikiPage)) {
			// the caller should catch the exception and use the exception text as an i18n key
			throw new ProviderException("attach.parent.not.exist");
		}
		
		m_provider.putAttachmentData(wikiPage, attContent, attName, data);
	}

    /** {@inheritDoc} */
    @Override
    public List<AttachmentContent> getVersionHistory( final String attachmentName ) throws ProviderException {
        if( m_provider == null ) {
            return null;
        }

        final AttachmentContent att = getAttachmentContent( (WikiContext)null, attachmentName );
        if( att != null ) {
            return m_provider.getVersionHistory( att.getPageAttachment() );
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<PageAttachment> getAllAttachments() throws ProviderException {
        if( attachmentsEnabled() ) {
            return m_provider.listAllChanged( new Date( 0L ) );
        }

        return new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    public AttachmentProvider getCurrentProvider() {
        return m_provider;
    }

    /** {@inheritDoc} */
    @Override
    public void deleteVersion( final PageAttachment att ) throws ProviderException {
        if( m_provider == null ) {
            return;
        }

        m_provider.deleteVersion( att );
    }

    /** {@inheritDoc} */
    @Override
    // FIXME: Should also use events!
    public void deleteAttachment( final AttachmentContent att ) throws ProviderException {
        if( m_provider == null ) {
            return;
        }

        m_provider.deleteAttachment( att );
     // :FVK: ServicesRefs.getSearchManager().pageRemoved( att );
        this.referenceManager.clearPageEntries( att.getPageAttachment().getName() );
    }

    /** {@inheritDoc} */
	@Override
	public void deleteAttachmentById(String attachmentId) throws ProviderException {
		PageProvider pageProvider = pageManager.getProvider();
		pageProvider.deleteAttachment(attachmentId);
	}
    
}
