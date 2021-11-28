package org.elwiki.configuration.internal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wiki.util.TextUtil;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.IWikiPreferences;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki.configuration.bundle.ConfigurationActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author vfedorov
 */
public class WikiConfiguration implements IWikiConfiguration {

	private static final Logger log = Logger.getLogger(WikiConfiguration.class);

    /** Property for application name */
    String PROP_APPNAME = "jspwiki.applicationName";

	/** Store the file path to the page's attachment data. */
	private IPath attachmentStorePath;

	/** The location where the work directory is. */
	private IPath pathWorkDir;

	/** Should the user info be saved with the page data as well? */
	private boolean m_saveUserInfo;

	/** If true, uses UTF8 encoding for all data */
	@Deprecated // see this.isUseUtf8() 
	private boolean m_useUTF8 = true;

	/** Stores the base URL. */
	private String m_baseURL;

	/** If true, all titles will be cleaned. */
	private boolean m_beautifyTitle;

	/** Stores the template path. This is relative to "templates". */
	private String m_templateDir;

	/** The default front page name. Defaults to "Main". */
	private String m_frontPage;

	/** Workbench's workspace area. */
	private final IPath pathWorkspace;

	private final IPreferenceStore prefs;

	private final IPreferenceStore prefsInterWikiRef;

	/**
	 * Stores wikiengine attributes.</br>
	 * Эта карта для хранения различных объектов, в процессе работы приложения.
	 */
	private final Map<String, Object> m_attributes = Collections.synchronizedMap(new HashMap<>());

	// == CODE ================================================================

	/**
	 * Default Constructor.
	 */
	public WikiConfiguration() {
		super();
		String cfgBundleName = ConfigurationActivator.getContext().getBundle().getSymbolicName();

		String extraBundle = System.getProperties().getProperty(IWikiPreferences.SYSPROP_CUSTOM_CONFIG, null);
		if (extraBundle != null && Platform.getBundle(extraBundle) != null) {
			cfgBundleName = extraBundle;
		}

		this.prefs = new ScopedPreferenceStore(InstanceScope.INSTANCE, cfgBundleName);
		this.prefsInterWikiRef = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				cfgBundleName + "/" + IWikiPreferences.NODE_INTERWIKI_REFERENCES);

		this.pathWorkspace = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		
		// :FVK: refactoring
        m_useUTF8 = StandardCharsets.UTF_8.name().equals( TextUtil.getStringProperty( prefs, IWikiPreferences.PROP_ENCODING, StandardCharsets.ISO_8859_1.name() ) );
		
	}

	@Override
	public IPath getAttachmentPath() {
		return this.attachmentStorePath;
	}

	@Override
	public IPath getWorkDir() {
		return this.pathWorkDir;
	}

	@Override
	public boolean isUseUtf8() {
		return "UTF-8".equalsIgnoreCase(getContentEncoding());
	}

	@Override
	public String getContentEncoding() {
		String contentEncoding = this.prefs.getString(IWikiPreferences.PROP_ENCODING);
		if (contentEncoding.length() == 0) {
			return IWikiPreferences.DEFAULT_ENCODING;
		}
		return contentEncoding;
	}

	@Override
	public boolean isBeautifyTitle() {
		return this.m_beautifyTitle;
	}

	@Override
	public String getTemplateDir() {
		return this.m_templateDir;
	}

    /** {@inheritDoc} */
	@Override
	public String getFrontPage() {
		return this.m_frontPage;
	}

	@Override
	public IPreferenceStore getWikiPreferences() {
		return this.prefs;
	}

	// -- :FVK: refactoring (some global utility methods) ----------------------

    /** {@inheritDoc} */
    @Override
    public String encodeName( final String pagename ) throws IOException {
        try {
            return URLEncoder.encode( pagename, m_useUTF8 ? "UTF-8" : "ISO-8859-1" );
        } catch( final UnsupportedEncodingException e ) {
            // :FVK: throw new InternalWikiException( "ISO-8859-1 not a supported encoding!?!  Your platform is borked." , e);
        	throw new IOException( "ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String decodeName( final String pagerequest ) throws IOException {
        try {
            return URLDecoder.decode( pagerequest, m_useUTF8 ? "UTF-8" : "ISO-8859-1" );
        } catch( final UnsupportedEncodingException e ) {
            //:FVK: throw new InternalWikiException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.", e);
        	throw new IOException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Charset getContentEncodingCs() {
        if( m_useUTF8 ) {
            return StandardCharsets.UTF_8;
        }
        return StandardCharsets.ISO_8859_1;
    }
    
	// -- service support ---------------------------------

	public synchronized void startup(BundleContext bc) {
		log.debug("** Startup ** Configuration ** Service *****************************************");

		/*
		// Get customized preferences from file, if they are defined.
		String propertyFile = System.getProperties().getProperty(IWikiPreferences.SYSPROP_CUSTOM_CONFIG, null);
		if (propertyFile != null) {
			log.info("Reading additional properties from '" + propertyFile + "' and merge to cascade.");
			File file = (new Path(propertyFile)).toFile();
			try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
				IPreferencesService service = PreferencesService.getDefault();
				IExportedPreferences expPrefs = service.readPreferences(inputStream);
				service.applyPreferences(expPrefs / *, filters * /);
				String[] names = expPrefs.childrenNames();
				Preferences node = expPrefs.node(names[0]);
				// String[] keys = expPrefs.keys();
				for (String name : node.childrenNames()) {
					String value = node.get(name, null);
					this.prefs.putValue(name, value);
				}
			} catch (IOException | CoreException | SecurityException | BackingStoreException e) {
				// TODO Auto-generated catch block
				log.error("Unable to load and setup properties from " + propertyFile + ".", e);
				//:FVK: e.printStackTrace();
			}
		}*/

		// Finds working directory or take the default (workspace place of Workbench).
		try {
			this.pathWorkDir = getDirectoryPlace(IWikiPreferences.PROP_WORKDIR);
		} catch (Exception e) {
			log.error("Working directory is assigned by default. Its definition in configuration have problems :: "
					+ e.getMessage());
			// default workplace of ElWiki - Workbench`s workspace area.
			this.pathWorkDir = this.pathWorkspace;
		}
		log.info("Working directory is: '" + this.pathWorkDir + "'");

		// Finds attachment`s directory or take the default (in the workspace place of Workbench).
		try {
			this.attachmentStorePath = getDirectoryPlace(IWikiPreferences.PROP_ATTACHMENTDIR);
		} catch (Exception e) {
			log.error("Attachment directory is assigned by default. Its definition in configuration have problems :: "
					+ e.getMessage());
			// default attachment directory of ElWiki - in the Workbench`s workspace area.
			this.attachmentStorePath = this.pathWorkspace.append("pages_attachment");
			
			// Create attachment subdirectory in the default workspace.
			File dir = this.attachmentStorePath.toFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!dir.exists() || !dir.canWrite() || !dir.canRead()) {
				log.error("Cannot write to attachment directory: " + dir.getAbsolutePath());
			}
		}
		log.info("Attachment directory is: '" + this.attachmentStorePath + "'");

		// ----------------------------------------

		this.m_saveUserInfo = this.prefs.getBoolean(IWikiPreferences.PROP_STOREUSERNAME);

		this.m_baseURL = this.prefs.getString(IWikiPreferences.PROP_BASEURL);

		/* :FVK: if (!this.m_baseURL.endsWith("/")) {
			this.m_baseURL = this.m_baseURL + "/";
		}*/

		this.m_beautifyTitle = this.prefs.getBoolean(IWikiPreferences.PROP_BEAUTIFYTITLE);

		this.m_templateDir = this.prefs.getString(IWikiPreferences.PROP_TEMPLATEDIR);
		if (m_templateDir.length() == 0) {
			this.m_templateDir = "default";
		}

		this.m_frontPage = TextUtil.getStringProperty(this.prefs, IWikiPreferences.PROP_FRONTPAGE, "Main" );
	}

	/**
	 * Copy preferences data.
	 * 
	 * @param srcPrefs source preferenses node.
	 * @param dstPrefs destination preferenses node.
	 *
	 * @throws BackingStoreException
	 * @throws IllegalStateException
	 */
	private void preferencesCopy(Preferences srcPrefs, Preferences dstPrefs)
			throws BackingStoreException, IllegalStateException {
		String[] prefsKeys = srcPrefs.keys();

		for (String key : prefsKeys) {
			String propName = (String) key;
			String propVal = srcPrefs.get(propName, null);
			dstPrefs.put(propName, propVal);
		}
	}

	/**
	 * Gets string path of directory from preferences store and check it sanity for local file
	 * system.
	 * 
	 * @param preferenceKey
	 * @return
	 * @throws Exception
	 */
	//TODO: - возвращаемый Exception трансформировать в ElWiki-ErrorException.
	private IPath getDirectoryPlace(String preferenceKey) throws Exception {
		IPath result;

		String dir = prefs.getString(preferenceKey);
		if (dir != null && dir.length()!=0 ) {
			Path pathDir = new Path(dir);
			File fileDir = pathDir.toFile();
			if (!fileDir.exists()) {
				throw new AccessDeniedException("Directory does not exist: " + dir);
			}
			if (!fileDir.canRead()) {
				throw new AccessDeniedException("No permission to read directory: " + dir); 
			}
			if (!fileDir.canWrite()) {
				throw new AccessDeniedException("No permission to write to directory: " + dir);
			}
			if (!fileDir.isDirectory()) {
				throw new NotDirectoryException(preferenceKey + " does not point to a directory: " + dir);
			}

			result = pathDir;
		} else {
			throw new Exception("Directory does not defined, by " + preferenceKey + "=<directory>");
		}

		return result;
	}

	public synchronized void shutdown() {
		//
	}

	// -- any attributes support ------------------------------------

	@Override
	public Object getAttribute(String key) {
		return this.m_attributes.get(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		this.m_attributes.put(key, value);
	}

    /** {@inheritDoc} */
	@Override
	public Collection<String> getAllInlinedImagePatterns() {
		String patternsSeq = this.prefs.getString(IWikiPreferences.NODE_INLINE_PATTERNS);
		return Arrays.asList(patternsSeq.split(","));
	}

	@Override
	public String getInterWikiURL(String wikiName) {
		return this.prefsInterWikiRef.getString(wikiName);
	}
	
    /** {@inheritDoc} */
	@Override
	public Collection<String> getAllInterWikiLinks() {
		List<String> result = new ArrayList<>();
		String keysSeq = this.prefsInterWikiRef.getString("keys");
		String[] keys = keysSeq.split(":");
		for (String key : keys) {
			String val = this.prefsInterWikiRef.getString(key);
			if (val.trim().length() > 0) {
				result.add(val);
			}
		}
		return result;
	}

	@Override
	public IPath getWorkspacePath() {
		return this.pathWorkspace;
	}

    /** {@inheritDoc} */
    @Override
    public String getApplicationName() {
        final String appName = TextUtil.getStringProperty( prefs, PROP_APPNAME, ":FVK: Release.APPNAME" );
        return TextUtil.cleanString( appName, TextUtil.PUNCTUATION_CHARS_ALLOWED );
    }

    // -- :FVK: -- Далее - 'динамическая конфигурация'. То есть, данные относятся к сессии HTTP.

    private String baseURL;
    
    /** {@inheritDoc} */
    @Override
    public String getBaseURL() {
        return this.baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL= baseURL; 
    }

}