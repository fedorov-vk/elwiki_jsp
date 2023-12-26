package org.elwiki.configuration.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.util.TextUtil;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
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

	/** The name used for the default template. The value is {@value}. */
	private final String DEFAULT_TEMPLATE_NAME = "default";

	/** File name for properties of any interwiki references. Current value - {@value} */
	private final String FILE_INTERWIKI_REFERENCES = "interwikirefs.properties";

	/** Property for application name */
	String PROP_APPNAME = "jspwiki.applicationName";

	/** Store the file path to the H2 database. */
	private IPath dbStorePath;

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

	/**
	 * Stores the template path. This is relative to "shapes". (:FVK: here it is necessary to eliminate
	 * connection with resource plugin)
	 */
	private String m_templateDir;

	/** The default front page name. Defaults to "Main". */
	private String m_frontPage;

	/** Workbench's workspace area. */
	private final IPath pathWorkspace;

	/** Wiki configuration preferences store. */
	private final IPreferenceStore prefs;

	/** InterWiki references preferences map. */
	private final Map<String, String> interwikiLinks = new HashMap<>();

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
		this.pathWorkspace = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		try {
			checkDirectory(this.pathWorkspace.toFile(), "${workspace_loc}");
		} catch (IOException e) {
			log.error("Workspace directory can not used. It has problem :: " + e.getMessage());
		}

		// Read InterWiki links.
		try (InputStream stream = //
				FileLocator.openStream(ConfigurationActivator.getContext().getBundle(),
						Path.fromPortableString(FILE_INTERWIKI_REFERENCES), false)) {
			Properties config = new Properties();
			config.load(stream);
			for (Entry<Object, Object> entry : config.entrySet()) {
				if (entry.getKey() instanceof String key && entry.getValue() instanceof String value)
					interwikiLinks.put(key, value);
			}
		} catch (IOException e) {
			log.error("Failed to read InterWiki links", e);
		}

		// :FVK: refactoring
		m_useUTF8 = StandardCharsets.UTF_8.name()
				.equals(getStringProperty(IWikiPreferences.PROP_ENCODING, StandardCharsets.ISO_8859_1.name()));

	}

	@Override
	public String getDbPlace() {
		return this.dbStorePath.toOSString();
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
	@Deprecated
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
	public String encodeName(final String pagename) throws IOException {
		try {
			return URLEncoder.encode(pagename, m_useUTF8 ? "UTF-8" : "ISO-8859-1");
		} catch (final UnsupportedEncodingException e) {
			// :FVK: throw new InternalWikiException( "ISO-8859-1 not a supported encoding!?!  Your platform is borked." , e);
			throw new IOException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
		}
	}

	/** {@inheritDoc} */
	@Override
	public String decodeName(final String pagerequest) throws IOException {
		try {
			return URLDecoder.decode(pagerequest, m_useUTF8 ? "UTF-8" : "ISO-8859-1");
		} catch (final UnsupportedEncodingException e) {
			//:FVK: throw new InternalWikiException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.", e);
			throw new IOException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
		}
	}

	/** {@inheritDoc} */
	@Override
	public Charset getContentEncodingCs() {
		if (m_useUTF8) {
			return StandardCharsets.UTF_8;
		}
		return StandardCharsets.ISO_8859_1;
	}

	// -- service support ---------------------------------

	protected void startup(BundleContext bc) {
		log.debug("** Startup ** Configuration ** Service **");

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

		// Finds H2 database directory or take the default (in the workspace place of Workbench).
		try {
			this.dbStorePath = getDirectoryPlace(IWikiPreferences.PROP_H2_DATABASE_PLACE);
		} catch (Exception e) {
			log.error("H2 database directory is assigned by default. Its definition in configuration have problems :: "
					+ e.getMessage());
			this.dbStorePath = this.pathWorkDir;
		}

		// Finds attachment`s directory or take the default (in the workspace place of Workbench).
		try {
			this.attachmentStorePath = getDirectoryPlace(IWikiPreferences.PROP_ATTACHMENTDIR);
		} catch (Exception e) {
			log.error("Attachment directory is assigned by default. Its definition in configuration have problems :: "
					+ e.getMessage());
			// default attachment directory of ElWiki - in the Workbench`s workspace area.
			this.attachmentStorePath = this.pathWorkDir.append("pages_attachment");

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

		this.m_baseURL = getStringProperty(IWikiPreferences.PROP_BASEURL, IWikiPreferences.DEFAULT_BASEURL);

		/* :FVK: if (!this.m_baseURL.endsWith("/")) {
			this.m_baseURL = this.m_baseURL + "/";
		}*/

		this.m_beautifyTitle = this.prefs.getBoolean(IWikiPreferences.PROP_BEAUTIFYTITLE);

		this.m_templateDir = this.prefs.getString(IWikiPreferences.PROP_TEMPLATEDIR);
		enforceValidTemplateDirectory();

		this.m_frontPage = getStringProperty(IWikiPreferences.PROP_FRONTPAGE, "Main");
	}

	/**
	 * Checks if the template directory specified in the wiki's properties actually exists. If it
	 * doesn't, then {@code m_templateDir} is set to {@link #DEFAULT_TEMPLATE_NAME}.
	 * <p>
	 * This checks the existence of the <tt>ViewTemplate.jsp</tt> file, which exists in every template
	 * using {@code m_servletContext.getRealPath("/")}.
	 * <p>
	 * {@code m_servletContext.getRealPath("/")} can return {@code null} on certain servers/conditions
	 * (f.ex, packed wars), an extra check against {@code m_servletContext.getResource} is made.
	 */
	void enforceValidTemplateDirectory() {
		if (m_templateDir.length() == 0) {
			this.m_templateDir = DEFAULT_TEMPLATE_NAME;
		}

		//TODO: перенести вычисление размещения JSP - относительно osgi-bundle, вместо данных из ServletContext. 
		// Место размещения JSP файлов темплейта - определялось из ServletContext.
		// Для ElWiki - это работает через bundle.
		/*
		if (m_servletContext != null) {
			final String viewTemplate = "shapes" + File.separator + getTemplateDir() + File.separator
					+ "ViewTemplate.jsp";
			boolean exists = new File(m_servletContext.getRealPath("/") + viewTemplate).exists();
			if (!exists) {
				try {
					final URL url = m_servletContext.getResource(viewTemplate);
					exists = url != null && !url.getFile().isEmpty();
				} catch (final MalformedURLException e) {
					log.warn("template not found with viewTemplate " + viewTemplate);
				}
			}
			if (!exists) {
				log.warn(getTemplateDir() + " template not found, updating WikiEngine's default template to "
						+ DEFAULT_TEMPLATE_NAME);
				m_templateDir = DEFAULT_TEMPLATE_NAME;
			}
		}
		*/
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
	@Deprecated
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
	 * Gets string path of directory from preferences store and check it sanity for local file system.
	 * 
	 * @param preferenceKey
	 * @return
	 * @throws IOException
	 */
	private IPath getDirectoryPlace(String preferenceKey) throws IOException {
		String dir = getStringProperty(preferenceKey, "");
		if (dir != null && dir.length() != 0) {
			return checkDirectory(new File(dir), preferenceKey);
		}
		throw new IllegalArgumentException("Directory does not defined, by " + preferenceKey + "=<directory>");
	}

	private IPath checkDirectory(File fileDir, String specifier) throws IOException {
		String dir = fileDir.getAbsolutePath();
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
			throw new NotDirectoryException("Target of " + specifier + " - is not a directory:: " + dir);
		}
		return new Path(fileDir.getCanonicalPath());
	}

	protected void shutdown() {
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

	/** {@inheritDoc} */
	@Override
	public String getInterWikiURL(String wikiName) {
		return this.interwikiLinks.get(wikiName);
	}

	/** {@inheritDoc} */
	@Override
	public Collection<String> getAllInterWikiLinks() {
		return this.interwikiLinks.keySet();
	}

	@Override
	public IPath getWorkspacePath() {
		return this.pathWorkspace;
	}

	/** {@inheritDoc} */
	@Override
	public String getApplicationName() {
		final String appName = getStringProperty(PROP_APPNAME, ":FVK: Release.APPNAME");
		return TextUtil.cleanString(appName, TextUtil.PUNCTUATION_CHARS_ALLOWED);
	}

	// -- :FVK: -- Далее - 'динамическая конфигурация'. То есть, данные относятся к сессии HTTP.

	private String baseURL;

	/** {@inheritDoc} */
	@Override
	public String getBaseURL() {
		return this.baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	/** {@inheritDoc} */
	@Override
	public String getStringProperty(String key, String defval) {
		String val = System.getProperties().getProperty(key, System.getenv(StringUtils.replace(key, ".", "_")));
		if (val == null) {
			val = this.prefs.getString(key);
		}
		if (val == null || val.length() == 0) {
			return defval;
		}
		return val.trim();
	}

	/** {@inheritDoc} */
	@Override
	public String getRequiredProperty(String key) throws NoSuchElementException {
		final String value = getStringProperty(key, null);
		if (value == null) {
			throw new NoSuchElementException("Required property not found: " + key);
		}
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public boolean getBooleanProperty(String key, boolean defval) {
		String val = System.getProperties().getProperty(key, System.getenv(StringUtils.replace(key, ".", "_")));
		if (val == null) {
			val = this.prefs.getString(key);
		}
		if (val == null || val.length() == 0) {
			return defval;
		}

		var value = BooleanUtils.toBooleanObject(val);
		return (value != null) ? value : false;
	}

	public int getIntegerProperty(final String key, final int defVal) {
		String val = System.getProperties().getProperty(key, System.getenv(StringUtils.replace(key, ".", "_")));
		if (val == null) {
			val = this.prefs.getString(key);
		}
		return NumberUtils.toInt(val, defVal);
	}

}
