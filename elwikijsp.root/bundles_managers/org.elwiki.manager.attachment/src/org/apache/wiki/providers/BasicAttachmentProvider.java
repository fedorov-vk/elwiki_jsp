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
package org.apache.wiki.providers;

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAttachment;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.osgi.framework.BundleContext;

import net.sf.ehcache.Element;

import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.providers.AttachmentProvider;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.search.QueryItem;
import org.elwiki_data.PageAttachment;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.pages0.PageTimeComparator;
import org.apache.wiki.util.FileUtil;
import org.apache.wiki.util.TextUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.component.WikiPrefs;
import org.elwiki.configuration.IWikiConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides basic, versioning attachments.<br/>
 * This class converted from version of JSPwiki - porting code based on
 * attachment items of EMF model.<br/>
 * Attachment files are stored in the location of attachment area (with random
 * names). Metadata of attachment items are stored in the CDO repository, and
 * are linked with corresponded wiki page.
 *
 * Old format:
 *  <PRE>
 *   Structure is as follows:
 *      attachment_dir/
 *         ThisPage/
 *            attachment.doc/
 *               attachment.properties
 *               1.doc
 *               2.doc
 *               3.doc
 *            picture.png/
 *               attachment.properties
 *               1.png
 *               2.png
 *         ThatPage/
 *            picture.png/
 *               attachment.properties
 *               1.png
 *             
 *  </PRE>
 */
public class BasicAttachmentProvider implements AttachmentProvider {

	private static final String ATTFILE_PREFIX = "att-";

	private static final String ATTFILE_SUFFIX = ".dat";
	
	private IWikiConfiguration wikiConfiguration;
	
	private GlobalPreferences globalPreferences;
	
    private Engine m_engine;
    
    /** The name of the property file. */
    public static final String PROPERTY_FILE = "attachment.properties";

    /** The default extension for the page attachment directory name. */
    public static final String DIR_EXTENSION = "-att";
    
    /** The default extension for the attachment directory. */
    public static final String ATTDIR_EXTENSION = "-dir";
    
    private static final Logger log = Logger.getLogger( BasicAttachmentProvider.class );

    BasicAttachmentProviderOptionsImpl options;

	public void startup(BundleContext bundleContext) {
		options = new BasicAttachmentProviderOptionsImpl(bundleContext);
	}

    /**
     *  {@inheritDoc}
     */
    @Override
    public void initialize( final Engine engine ) throws NoRequiredPropertyException, IOException {
        m_engine = engine;
        wikiConfiguration = m_engine.getWikiConfiguration();
        globalPreferences = m_engine.getManager(GlobalPreferences.class);

        options.initialize(m_engine);
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getProviderInfo() {
        return "Provider: " + this.getClass().getName();
    }
    
    /**
	 * Finds the dir in which the attachment lives.
	 * <p>
	 * TODO: :FVK: The implementation should be extended to optimize directories
	 * containing attachments data files. A tree is planned in the file system to
	 * minimize the number of files per directory containing attachments
	 */
    @Deprecated
    private File findAttachmentDir( final AttachmentContent att ) throws ProviderException {
    	//:FVK: workaround. (here shoul added algotithm with hierarhy of directories, for controling maximuum count of files in the one directory.)
    	File f = this.globalPreferences.getAttachmentPath().toFile();
    	/*:FVK:
        File f = new File( findPageDir( att.getParentName() ), mangleName( att.getFileName() + ATTDIR_EXTENSION ) );

        //  Migration code for earlier versions of JSPWiki. Originally, we used plain filename.  Then we realized we need
        //  to urlencode it.  Then we realized that we have to use a postfix to make sure illegal file names are never formed.
        if( !f.exists() ) {
            File oldf = new File( findPageDir( att.getParentName() ), mangleName( att.getFileName() ) );
            if( oldf.exists() ) {
                f = oldf;
            } else {
                oldf = new File( findPageDir( att.getParentName() ), att.getFileName() );
                if( oldf.exists() ) {
                    f = oldf;
                }
            }
        }
        */

        return f;
    }

    /**
     *  {@inheritDoc}
     */
	@Override
	public void putAttachmentData(WikiPage wikiPage, AttachmentContent attContent, String attName, InputStream data)
			throws IOException, ProviderException {
		File attDir = findAttachmentDir(null);

		if (!attDir.exists()) {
			attDir.mkdirs(); //workaround.
		}

		File newfile = File.createTempFile(ATTFILE_PREFIX, ATTFILE_SUFFIX, attDir);
		attContent.setPlace(newfile.getName());

		try (final OutputStream out = new FileOutputStream(newfile)) {
			log.info("Uploading attachment " + attName + " to page " + wikiPage.getName());
			log.info("Saving attachment contents to " + newfile.getAbsolutePath());
			FileUtil.copyContents(data, out);

			PageManager pm = m_engine.getManager(PageManager.class);
			pm.addAttachment(wikiPage, attContent, attName);
		} catch (final Exception ex) {
			log.error("Could not save attachment data: ", ex);
			throw new IOException(ex);
		}
	}

    /**
     *  {@inheritDoc}
     */
    @Override
    public FileInputStream getAttachmentData( final AttachmentContent att ) throws IOException, ProviderException {
        IPath attachmentDirectory = this.globalPreferences.getAttachmentPath();
        try {
            File f = attachmentDirectory.append(att.getPlace()).toFile();
            return new FileInputStream( f );
        } catch( final FileNotFoundException e ) {
            log.error( "File not found: " + e.getMessage() );
            throw new ProviderException( "No such attachment was found." );
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public List< PageAttachment > listAttachments( final WikiPage page ) throws ProviderException {
		log.debug("Listing attachments for page: " + page);
		List<PageAttachment> result = new ArrayList<>();

		for (PageAttachment pageAttachment : page.getAttachments()) {
			pageAttachment.forLastContent();
			result.add(pageAttachment);
		}

        return result;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public List< PageAttachment > listAllChanged( final Date timestamp ) throws ProviderException {
    	/* Now - nothing to do.
    	 * FIXME: :FVK: Add into CDO manager - finding page attachment relate to given date.
    	 */

        final ArrayList< PageAttachment > list = new ArrayList<>();
        //:FVK: list.sort( new PageTimeComparator() );

        return list;
    }

    /**
     *  {@inheritDoc}
     */
	@Override
	public AttachmentContent getAttachmentContent(WikiPage page, String name, int... version) throws ProviderException {
        if(page == null) {
            return null;
        }
        String pageId = page.getId();
		log.debug("Getting attachment for pageId: " + pageId);
		
		PageAttachment pageAttachment = null;
		for (PageAttachment attachItem : page.getAttachments()) {
			if (attachItem.getName().equals(name)) {
				pageAttachment = attachItem;
				break;
			}
		}

		if (pageAttachment == null) {
			log.debug("Attachment \"" + name + "\"not found - thus no attachment can exist.");
			return null;
		}

		AttachmentContent attachmentContent;
		if(version.length == 0 || version[0] == WikiProvider.LATEST_VERSION) {
			attachmentContent = pageAttachment.forLastContent();
		} else {
			attachmentContent = pageAttachment.forVersionContent(version[0]);
		}

		return attachmentContent;
	}

    /**
     *  {@inheritDoc}
     */
    @Override
    public List<AttachmentContent> getVersionHistory( final PageAttachment att ) {
    	final ArrayList< AttachmentContent > list = new ArrayList<>(att.getAttachContents());

        return list;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void deleteVersion( final PageAttachment att ) throws ProviderException {
        // FIXME: Does nothing yet.
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void deleteAttachment( final AttachmentContent att ) throws ProviderException {
        final File dir = findAttachmentDir( att );
        for(AttachmentContent attachContent : att.getPageAttachment().getAttachContents()) {
        	File file = new File(dir, attachContent.getPlace());
        	file.delete();
        }
        // FIXME: Delete PageAttachment from CDO repository.
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void moveAttachmentsForPage( final String oldParent, final String newParent ) throws ProviderException {
    	log.debug( "Trying to move all attachments from " + oldParent + " to " + newParent );
		/*
		* FIXME: :FVK: Add into CDO manager - move Attachments from one Page to another..
		*/
    }

	@Override
	public String getConfigurationEntry() {
		return options.getConfigurationJspPage();
	}

}

