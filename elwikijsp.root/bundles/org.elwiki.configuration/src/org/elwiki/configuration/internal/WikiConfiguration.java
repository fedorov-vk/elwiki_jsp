package org.elwiki.configuration.internal;

import java.io.IOException;
import java.io.InputStream;
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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.configuration.ScopedPreferenceStore;
import org.elwiki.configuration.bundle.ConfigurationActivator;
import org.osgi.framework.BundleContext;

/**
 * @author vfedorov
 */
public class WikiConfiguration implements IWikiConfiguration {

	private static final Logger log = Logger.getLogger(WikiConfiguration.class);

	/** Property name for the default front page. */
	private static final String PROP_FRONTPAGE = "jspwiki.frontPage";

	/** The name used for the default template. The value is {@value}. */
	private final String DEFAULT_TEMPLATE_NAME = "default";

	/** File name for properties of any interwiki references. Current value - {@value} */
	private final String FILE_INTERWIKI_REFERENCES = "interwikirefs.properties";

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

	// -- OSGi service handling ----------------------(start)--

	protected void startup(BundleContext bc) {
		log.debug("** Startup ** Configuration ** Service **");

		/* :FVK: if (!this.m_baseURL.endsWith("/")) {
			this.m_baseURL = this.m_baseURL + "/";
		}*/

		this.m_frontPage = getStringProperty(PROP_FRONTPAGE, "Main");
	}

	protected void shutdown() {
		//
	}

	// -- OSGi service handling ------------------------(end)--

	/**
	 * Default Constructor.
	 */
	public WikiConfiguration() {
		super();
		String cfgBundleName = ConfigurationActivator.getContext().getBundle().getSymbolicName();
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
