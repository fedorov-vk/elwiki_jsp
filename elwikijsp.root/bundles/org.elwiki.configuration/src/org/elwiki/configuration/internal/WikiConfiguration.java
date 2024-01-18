package org.elwiki.configuration.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.IWikiPreferences;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki.configuration.bundle.ConfigurationActivator;
import org.osgi.framework.BundleContext;

/**
 * @author vfedorov
 */
public class WikiConfiguration implements IWikiConfiguration {

	private static final Logger log = Logger.getLogger(WikiConfiguration.class);

	/** The name used for the default template. The value is {@value}. */
	private final String DEFAULT_TEMPLATE_NAME = "default";

	/** File name for properties of any interwiki references. Current value - {@value} */
	private final String FILE_INTERWIKI_REFERENCES = "interwikirefs.properties";

	/** Should the user info be saved with the page data as well? */
	private boolean m_saveUserInfo;

	/** If true, uses UTF8 encoding for all data */
	@Deprecated // see this.isUseUtf8() 
	private boolean m_useUTF8 = true;

	/** Stores the base URL. */
	private String m_baseURL;

	/**
	 * Stores the template path. This is relative to "shapes". (:FVK: here it is necessary to eliminate
	 * connection with resource plugin)
	 */
	private String m_templateDir;

	/** The default front page name. Defaults to "Main". */
	private String m_frontPage;

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

		this.m_saveUserInfo = this.prefs.getBoolean(IWikiPreferences.PROP_STOREUSERNAME);

		this.m_baseURL = getStringProperty(IWikiPreferences.PROP_BASEURL, IWikiPreferences.DEFAULT_BASEURL);

		/* :FVK: if (!this.m_baseURL.endsWith("/")) {
			this.m_baseURL = this.m_baseURL + "/";
		}*/

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
