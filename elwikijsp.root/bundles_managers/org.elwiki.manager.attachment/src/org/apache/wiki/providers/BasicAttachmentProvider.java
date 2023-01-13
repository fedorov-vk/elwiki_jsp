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
import org.elwiki_data.PageAttachment;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.providers.AttachmentProvider;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.search.QueryItem;
import org.apache.wiki.auth.IPermissionManager;
import org.elwiki_data.PageAttachment;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.pages0.PageTimeComparator;
import org.apache.wiki.util.FileUtil;
import org.apache.wiki.util.TextUtil;
import org.eclipse.emf.common.util.EList;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.IWikiPreferences;
import org.elwiki.services.ServicesRefs;

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
	
	private IWikiConfiguration wikiconfiguration;
	
    private Engine m_engine;
    
    /*
     * Disable client cache for files with patterns
     * since 2.5.96
     */
    private Pattern m_disableCache = null;

    
    /** The property name for specifying which attachments are not cached.  Value is <tt>{@value}</tt>. */
    public static final String PROP_DISABLECACHE = "jspwiki.basicAttachmentProvider.disableCache";

    /** The name of the property file. */
    public static final String PROPERTY_FILE = "attachment.properties";

    /** The default extension for the page attachment directory name. */
    public static final String DIR_EXTENSION = "-att";
    
    /** The default extension for the attachment directory. */
    public static final String ATTDIR_EXTENSION = "-dir";
    
    private static final Logger log = Logger.getLogger( BasicAttachmentProvider.class );

    /**
     *  {@inheritDoc}
     */
    @Override
    public void initialize( final Engine engine ) throws NoRequiredPropertyException, IOException {
        m_engine = engine;
        wikiconfiguration = m_engine.getWikiConfiguration();

        final String patternString = TextUtil.getStringProperty(engine.getWikiPreferences(), PROP_DISABLECACHE, null );
        if ( patternString != null ) {
            m_disableCache = Pattern.compile(patternString);
        }
    }

    /**
     *  Finds storage dir, and if it exists, makes sure that it is valid.
     *
     *  @param wikipage Page to which this attachment is attached.
     */
    @Deprecated
    private File findPageDir( String wikipage ) throws ProviderException {
        wikipage = mangleName( wikipage );

        final File f = wikiconfiguration.getAttachmentPath().append(wikipage + DIR_EXTENSION).toFile();
        if( f.exists() && !f.isDirectory() ) {
            throw new ProviderException( "Storage dir '" + f.getAbsolutePath() + "' is not a directory!" );
        }

        return f;
    }

    private static String mangleName( final String wikiname ) {
        return TextUtil.urlEncodeUTF8( wikiname );
    }

    private static String unmangleName( final String filename )
    {
        return TextUtil.urlDecodeUTF8( filename );
    }
    
    /**
     *  Finds the dir in which the attachment lives.
     */
    private File findAttachmentDir( final PageAttachment att ) throws ProviderException {
    	//:FVK: workaround. (here shoul added algotithm with hoerarhy of directories, for controling maximuum count of files in the one directory.)
    	File f = this.wikiconfiguration.getAttachmentPath().toFile();
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
     * Goes through list of attachments of page and decides which attachment version is the newest in that page.
     * @param page TODO
     *
     * @return Latest version number in the repository, or 0, if there is no page in the repository.
     */
	private int findLatestVersion(WikiPage page, PageAttachment att) throws ProviderException {
		String attName = att.getName();
		int version = 0;
		for (PageAttachment attachment : page.getAttachments()) {
			if (attName.equals(attachment.getName()) && (version < attachment.getVersion())) {
				version = attachment.getVersion();
			}
		}

		return version;
	}

    /**
     *  Returns the file extension.  For example "test.png" returns "png".
     *  <p>
     *  If file has no extension, will return "bin"
     *  
     *  @param filename The file name to check
     *  @return The extension.  If no extension is found, returns "bin".
     */
    protected static String getFileExtension( final String filename ) {
        String fileExt = "bin";

        final int dot = filename.lastIndexOf('.');
        if( dot >= 0 && dot < filename.length()-1 ) {
            fileExt = mangleName( filename.substring( dot+1 ) );
        }

        return fileExt;
    }

    /**
     *  Writes the page properties back to the file system.
     *  Note that it WILL overwrite any previous properties.
     */
    @Deprecated
    private void putPageProperties( final PageAttachment att, final Properties properties ) throws IOException, ProviderException {
        final File attDir = findAttachmentDir( att );
        final File propertyFile = new File( attDir, PROPERTY_FILE );
        try( final OutputStream out = new FileOutputStream( propertyFile ) ) {
            properties.store( out, " JSPWiki page properties for " + att.getName() + ". DO NOT MODIFY!" );
        }
    }

    /**
     *  Reads page properties from the file system.
     */
    private Properties getPageProperties( final PageAttachment att ) throws IOException, ProviderException {
        final Properties props = new Properties();
        final File propertyFile = new File( findAttachmentDir(att), PROPERTY_FILE );
        if( propertyFile.exists() ) {
            try( final InputStream in = new FileInputStream( propertyFile ) ) {
                props.load( in );
            }
        }
        
        return props;
    }

    /**
     *  {@inheritDoc}
     */
	@Override
	public PageAttachment putAttachmentData(WikiPage wikiPage, PageAttachment att, InputStream data)
			throws ProviderException, IOException {
		PageAttachment result = null;
		File attDir = findAttachmentDir(att);

		if (!attDir.exists()) {
			attDir.mkdirs();
		}

		int latestVersion = findLatestVersion(wikiPage, att);
		att.setVersion(latestVersion + 1);
		File newfile = File.createTempFile(ATTFILE_PREFIX, ATTFILE_SUFFIX, attDir);
		att.setPlace(newfile.getCanonicalPath());

		try (final OutputStream out = new FileOutputStream(newfile)) {
			log.info("Uploading attachment " + att.getName() + " to page " + wikiPage.getName());
			log.info("Saving attachment contents to " + newfile.getAbsolutePath());
			FileUtil.copyContents(data, out);

			PageManager pm = ServicesRefs.getPageManager();
			result = pm.addAttachment(wikiPage, att);
		} catch (final Exception ex) {
			log.error("Could not save attachment data: ", ex);
			throw new IOException(ex);
		}

		return result;
	}

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getProviderInfo() {
        return "";
    }

    private File findFile( final File dir, final PageAttachment att ) throws FileNotFoundException, ProviderException {
        int version = att.getVersion();
        if( version == WikiProvider.LATEST_VERSION ) {
            version = findLatestVersion( null, att );
        }

        final String ext = getFileExtension( att.getName() );
        File f = new File( dir, version + "." + ext );

        if( !f.exists() ) {
            if( "bin".equals( ext ) ) {
                final File fOld = new File( dir, version + "." );
                if( fOld.exists() ) {
                    f = fOld;
                }
            }
            if( !f.exists() ) {
                throw new FileNotFoundException( "No such file: " + f.getAbsolutePath() + " exists." );
            }
        }

        return f;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public InputStream getAttachmentData( final PageAttachment att ) throws IOException, ProviderException {
        final File attDir = findAttachmentDir( att );
        try {
            final File f = findFile( attDir, att );
            return new FileInputStream( f );
        } catch( final FileNotFoundException e ) {
            log.error( "File not found: " + e.getMessage() );
            throw new ProviderException( "No such page was found." );
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public List< PageAttachment > listAttachments( final WikiPage page ) throws ProviderException {
		List<PageAttachment> result = new ArrayList<>();
		
		//TODO: inefficient loops.
		Set<String> attNames = new HashSet<>();
		for (PageAttachment attItem : page.getAttachments()) {
			attNames.add(attItem.getName());
		}
		for (String attName : attNames) {
			int version = 0;
			PageAttachment lastAtt = null;
			for (PageAttachment attachment : page.getAttachments()) {
				if (attName.equals(attachment.getName()) && (version < attachment.getVersion())) {
					version = attachment.getVersion();
					lastAtt = attachment;
				}
			}
			if (lastAtt != null) {
				result.add(lastAtt);
			}
		}

		/*:FVK:
        final File dir = findPageDir( page.getName() );
        final String[] attachments = dir.list();
        if( attachments != null ) {
            //  We now have a list of all potential attachments in the directory.
            for( final String attachment : attachments ) {
                final File f = new File( dir, attachment );
                if( f.isDirectory() ) {
                    String attachmentName = unmangleName( attachment );

                    //  Is it a new-stylea attachment directory?  If yes, we'll just deduce the name.  If not, however,
                    //  we'll check if there's a suitable property file in the directory.
                    if( attachmentName.endsWith( ATTDIR_EXTENSION ) ) {
                        attachmentName = attachmentName.substring( 0, attachmentName.length() - ATTDIR_EXTENSION.length() );
                    } else {
                        final File propFile = new File( f, PROPERTY_FILE );
                        if( !propFile.exists() ) {
                            //  This is not obviously a JSPWiki attachment, so let's just skip it.
                            continue;
                        }
                    }

                    final PageAttachment att = getAttachmentInfo( page, attachmentName, WikiProvider.LATEST_VERSION );
                    //  Sanity check - shouldn't really be happening, unless you mess with the repository directly.
                    if( att == null ) {
                        throw new ProviderException( "Attachment disappeared while reading information:"
                                + " if you did not touch the repository, there is a serious bug somewhere. " + "Attachment = " + attachment
                                + ", decoded = " + attachmentName );
                    }

                    result.add( att );
                }
            }
        }
        */

        return result;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Collection< PageAttachment > findAttachments( final QueryItem[] query ) {
        return new ArrayList<>();
    }

    /**
     *  {@inheritDoc}
     */
    // FIXME: Very unoptimized.
    @Override
    @Deprecated
    public List< PageAttachment > listAllChanged( final Date timestamp ) throws ProviderException {
    	/*:FVK:
        final File attDir = new File( m_storageDir );
        if( !attDir.exists() ) {
            throw new ProviderException( "Specified attachment directory " + m_storageDir + " does not exist!" );
        }
        */

        final ArrayList< PageAttachment > list = new ArrayList<>();
        /*:FVK:
        final String[] pagesWithAttachments = attDir.list( new AttachmentFilter() );

        if( pagesWithAttachments != null ) {
            for( final String pagesWithAttachment : pagesWithAttachments ) {
                String pageId = unmangleName( pagesWithAttachment );
                pageId = pageId.substring( 0, pageId.length() - DIR_EXTENSION.length() );

                final Collection< PageAttachment > c = listAttachments( Wiki.contents().page( pageId ) );
                for( final PageAttachment att : c ) {
                    if( att.getLastModify().after( timestamp ) ) {
                        list.add( att );
                    }
                }
            }
        }
        */

        //:FVK: list.sort( new PageTimeComparator() );

        return list;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public PageAttachment getAttachmentInfo( final WikiPage page, final String name, int version ) throws ProviderException {
        final PageAttachment att = page.getAttachments().get(0);
        //:FVK:  = new Attachment( m_engine, page.getName(), name );
        
        final File dir = findAttachmentDir( att );
        if( !dir.exists() ) {
            // log.debug("Attachment dir not found - thus no attachment can exist.");
            return null;
        }
        
        if( version == WikiProvider.LATEST_VERSION ) {
            version = findLatestVersion(null, att);
        }

        att.setVersion( version );
        
        // Should attachment be cachable by the client (browser)?
        if( m_disableCache != null ) {
            final Matcher matcher = m_disableCache.matcher( name );
            if( matcher.matches() ) {
                att.setCacheable( false );
            }
        }

        // System.out.println("Fetching info on version "+version);
        try {
            final Properties props = getPageProperties( att );
            att.setAuthor( props.getProperty( version+".author" ) );
            final String changeNote = props.getProperty( version+".changenote" );
            if( changeNote != null ) {
                att.setChangeNote(changeNote);
            }

            final File f = findFile( dir, att );
            att.setSize( f.length() );
            att.setLastModify( new Date( f.lastModified() ) );
        } catch( final FileNotFoundException e ) {
            log.error( "Can't get attachment properties for " + att, e );
            return null;
        } catch( final IOException e ) {
            log.error("Can't read page properties", e );
            throw new ProviderException("Cannot read page properties: "+e.getMessage());
        }
        // FIXME: Check for existence of this particular version.

        return att;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public List< PageAttachment > getVersionHistory( final PageAttachment att ) {
        final ArrayList< PageAttachment > list = new ArrayList<>();
        try {
            final int latest = findLatestVersion( null, att );
            for( int i = latest; i >= 1; i-- ) {
            	/*:FVK:
                final PageAttachment a = getAttachmentInfo( Wiki.contents().page( m_engine, att.getParentName() ), att.getFileName(), i );
                if( a != null ) {
                    list.add( a );
                }*/
            }
        } catch( final ProviderException e ) {
            log.error( "Getting version history failed for page: " + att, e );
            // FIXME: Should this fail?
        }

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
    public void deleteAttachment( final PageAttachment att ) throws ProviderException {
        final File dir = findAttachmentDir( att );
        final String[] files = dir.list();
        for( final String s : files ) {
            final File file = new File( dir.getAbsolutePath() + "/" + s );
            file.delete();
        }
        dir.delete();
    }

    /**
     *  Returns only those directories that contain attachments.
     */
    public static class AttachmentFilter implements FilenameFilter {
        /**
         *  {@inheritDoc}
         */
        @Override
        public boolean accept( final File dir, final String name )
        {
            return name.endsWith( DIR_EXTENSION );
        }
    }

    /**
     *  Accepts only files that are actual versions, no control files.
     */
    public static class AttachmentVersionFilter implements FilenameFilter {
        /**
         *  {@inheritDoc}
         */
        @Override
        public boolean accept( final File dir, final String name )
        {
            return !name.equals( PROPERTY_FILE );
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void moveAttachmentsForPage( final String oldParent, final String newParent ) throws ProviderException {
        final File srcDir = findPageDir( oldParent );
        final File destDir = findPageDir( newParent );

        log.debug( "Trying to move all attachments from " + srcDir + " to " + destDir );

        // If it exists, we're overwriting an old page (this has already been confirmed at a higher level), so delete any existing attachments.
        if( destDir.exists() ) {
            log.error( "Page rename failed because target directory " + destDir + " exists" );
        } else {
            // destDir.getParentFile().mkdir();
            srcDir.renameTo( destDir );
        }
    }

}

