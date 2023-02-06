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
package org.apache.wiki.ui0;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.modules.BaseModuleManager;
import org.apache.wiki.api.modules.WikiModuleInfo;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.preferences.Preferences.TimeFormat;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.ui.admin0.AdminBeanManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.services.ServicesRefs;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * This class takes care of managing JSPWiki shapes. This class also provides the
 * ResourceRequest mechanism.
 *
 * Размещается в бандле ''org.elwiki.resources''. <br/>
 * Бандл - развернут в каталоге.
 * <p>
 * TODO: Распаковывать в каталог. Снята связь с WikiEngine, так как не требуется доступ по контексту
 * сервлета к локальному каталогу, доступ к каталогу через Bundle.
 */
@Component(name = "elwiki.DefaultTemplateManager", service = TemplateManager.class, //
		factory = "elwiki.TemplateManager.factory")
public class DefaultTemplateManager extends BaseModuleManager implements TemplateManager {

	private static final Logger log = Logger.getLogger(DefaultTemplateManager.class);

	private static final String SKIN_DIRECTORY = "skins";

	/** The default directory for the properties. Value is {@value}. */
	private static final String DIRECTORY = "shapes";

	/** The name of the default template. Value is {@value}. */
	String DEFAULT_TEMPLATE = "default";

	private Bundle bundle;

	// -- service handling ---------------------------(start)--

	/** Stores configuration. */
	@Reference //(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	/**
	 * DefaultTemplateManager initializer.
	 * 
	 * @param bc A bundle's execution context.
	 */
	@Activate
	protected void startup(BundleContext bc) {
		this.bundle = bc.getBundle();
	}

	// -- service handling -----------------------------(end)--
	
	/**
	 * Returns the full name (/shapes/foo/bar) for: template=foo, name=bar.
	 *
	 * @param template The name of the template.
	 * @param name     The name of the resource.
	 * @return The full name for a template.
	 */
	private static String makeFullJSPName(final String template, final String name) {
		return "/" + DIRECTORY + "/" + template + "/" + name;
	}

	/**
	 * Returns an absolute path to a given template.
	 */
	private static String getPath(final String template) {
		return "/" + DIRECTORY + "/" + template + "/";
	}


	/**
	 * Removes the template part of a name.
	 * :FVK: странная логика. надо проверить.
	 */
	private static String removeTemplatePart(String name) {
		int idx = 0;
		if (name.startsWith("/")) {
			idx = 1;
		}

		idx = name.indexOf('/', idx);
		if (idx != -1) {
			idx = name.indexOf('/', idx + 1); // Find second "/"
			if (idx != -1) {
				name = name.substring(idx + 1);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Final name = " + name);
		}
		return name;
	}

	@Override
	public URL getResourceUrl(String resourceName) {
		return this.bundle.getEntry(resourceName);
	}
	
	/** {@inheritDoc} */
	@Override
	// FIXME: Does not work yet
	public boolean templateExists(final String templateName) {
//////////		
		final ServletContext context = m_engine.getServletContext();
		//TODO: этот код устарел - переделать (в ElWiki инициализация относительно OSGi, а не сервлета. ServletContext==null).
		/*
		try (final InputStream in = context.getResourceAsStream(getPath(templateName) + "ViewTemplate.jsp")) {
			if (in != null) {
				return true;
			}
		} catch (final IOException e) {
			log.error(e.getMessage(), e);
		}
		*/

		return false;
	}

	/**
	 * Tries to locate a given resource from the template directory. If the given resource is not
	 * found under the current name, returns the path to the corresponding one in the default
	 * template.
	 *
	 * @param sContext The servlet context. (TODO: this param deprecated - not used)
	 * @param name     The name of the resource.
	 * @return The name of the resource which was found, or null.
	 */
	private String findResource(final ServletContext sContext, final String name) {
		String resourceName = name;
		try (final InputStream is = getResourceUrl(resourceName).openStream()) {
			if (is == null) {
				final String defname = makeFullJSPName(DEFAULT_TEMPLATE, removeTemplatePart(resourceName));
				try (final InputStream iis = getResourceUrl(defname).openStream()) {
					resourceName = iis != null ? defname : null;
				}
			}
		} catch (final IOException e) {
			log.error("unable to open " + name + " as resource stream", e);
		}
		return resourceName;
	}

	/**
	 * Attempts to find a resource from the given template, and if it's not found attempts to locate
	 * it from the default template.
	 * 
	 * @param sContext servlet context used to search for the resource. (TODO: this param deprecated - not used)
	 * @param template template used to search for the resource
	 * @param name     resource name
	 * @return the Resource for the given template and name.
	 */
	private String findResource(final ServletContext sContext, final String template, final String name) {
		if (name.charAt(0) == '/') {
			// This is already a full path
			return findResource(sContext, name);
		}
		final String fullname = makeFullJSPName(template, name);
		return findResource(sContext, fullname);
	}

	/** {@inheritDoc} */
	@Override
	public String findJSP(final PageContext pageContext, final String name) {
		return findResource(null, name);
	}

	/** {@inheritDoc} */
	@Override
	public String findJSP(final PageContext pageContext, final String template, final String name) {
		if (template == null || name == null) {
			log.fatal("findJSP() was asked to find a null template or name (" + template + "," + name + ")."
					+ " JSP page '" + ((HttpServletRequest) pageContext.getRequest()).getRequestURI() + "'");
			throw new InternalWikiException("Illegal arguments to findJSP(); please check logs.");
		}

		return findResource((ServletContext)null, template, name);
	}

	/** {@inheritDoc} */
	@Override
	public String findResource(final WikiContext ctx, final String template, final String name) {
		return findResource((ServletContext)null, template, name);
	}

	/*
	 *  Returns a property, as defined in the template.  The evaluation is lazy, i.e. the properties are not loaded until the template is
	 *  actually used for the first time.
	 */
	/*
	public String getTemplateProperty( WikiContext context, String key )
	{
	    String template = context.getShape();
	
	    try
	    {
	        Properties props = (Properties)m_propertyCache.getFromCache( template, -1 );
	
	        if( props == null )
	        {
	            try
	            {
	                props = getTemplateProperties( template );
	
	                m_propertyCache.putInCache( template, props );
	            }
	            catch( IOException e )
	            {
	                log.warn("IO Exception while reading template properties",e);
	
	                return null;
	            }
	        }
	
	        return props.getProperty( key );
	    }
	    catch( NeedsRefreshException ex )
	    {
	        // FIXME
	        return null;
	    }
	}
	*/

	/** {@inheritDoc} */
	@Override
	public Set<String> listSkins(final PageContext pageContext, final String template) {
		final String place = makeFullJSPName(template, SKIN_DIRECTORY);
		final ServletContext sContext = pageContext.getServletContext();
		final Set<String> skinSet = sContext.getResourcePaths(place);
		final Set<String> resultSet = new TreeSet<>();

		if (log.isDebugEnabled()) {
			log.debug("Listings skins from " + place);
		}

		if (skinSet != null) {
			final String[] skins = skinSet.toArray(new String[] {});
			for (final String skin : skins) {
				final String[] s = skin.split("/");
				if (s.length > 2 && skin.endsWith("/")) {
					final String skinName = s[s.length - 1];
					resultSet.add(skinName);
					if (log.isDebugEnabled()) {
						log.debug("...adding skin '" + skinName + "'");
					}
				}
			}
		}

		return resultSet;
	}

	/** {@inheritDoc} */
	public Map<String, String> listLanguages(final PageContext pageContext) {
		final Map<String, String> resultMap = new LinkedHashMap<>();
		final String clientLanguage = pageContext.getRequest().getLocale().toString();

		Enumeration<String> paths = this.bundle.getEntryPaths("/" + DIRECTORY + "/");
		while (paths.hasMoreElements()) {
			String name;
			name = paths.nextElement();
			log.debug(name);

			if (name.equals(I18NRESOURCE_EN)
					|| (name.startsWith(I18NRESOURCE_PREFIX) && name.endsWith(I18NRESOURCE_SUFFIX))) {
				if (name.equals(I18NRESOURCE_EN)) {
					name = I18NRESOURCE_EN_ID;
				} else {
					name = name.substring(I18NRESOURCE_PREFIX.length(), name.lastIndexOf(I18NRESOURCE_SUFFIX));
				}
				final Locale locale = new Locale(name.substring(0, 2), !name.contains("_") ? "" : name.substring(3, 5));
				String defaultLanguage = "";
				if (clientLanguage.startsWith(name)) {
					defaultLanguage = "ru"; // :FVK: workaround
					// = LocaleSupport.getLocalizedMessage( pageContext, I18NDEFAULT_LOCALE );
				}
				resultMap.put(name, locale.getDisplayName(locale) + " " + defaultLanguage);
			}
		}

		/* :FVK: старый код
		final List< String > entries = ClassUtil.classpathEntriesUnder( DIRECTORY );
		for( String name : entries ) {
		    if ( name.equals( I18NRESOURCE_EN ) || (name.startsWith( I18NRESOURCE_PREFIX ) && name.endsWith( I18NRESOURCE_SUFFIX ) ) ) {
		        if( name.equals( I18NRESOURCE_EN ) ) {
		            name = I18NRESOURCE_EN_ID;
		        } else {
		            name = name.substring( I18NRESOURCE_PREFIX.length(), name.lastIndexOf( I18NRESOURCE_SUFFIX ) );
		        }
		        final Locale locale = new Locale( name.substring( 0, 2 ), !name.contains( "_" ) ? "" : name.substring( 3, 5 ) );
		        String defaultLanguage = "";
		        if( clientLanguage.startsWith( name ) ) {
		            defaultLanguage = "ru"; //:FVK: workaround 
		            // = LocaleSupport.getLocalizedMessage( pageContext, I18NDEFAULT_LOCALE );
		        }
		        resultMap.put( name, locale.getDisplayName( locale ) + " " + defaultLanguage );
		    }
		}*/

		return resultMap;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, String> listTimeFormats(final PageContext pageContext) {
		final WikiContext context = ContextUtil.findContext(pageContext);
		IPreferenceStore props = this.wikiConfiguration.getWikiPreferences();
		final ArrayList<String> tfArr = new ArrayList<>(40);
		final LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();

		/* filter timeformat properties */
		/*:FVK:
		for( final Enumeration< ? > e = props.propertyNames(); e.hasMoreElements(); ) {
		    final String name = ( String )e.nextElement();
		    if( name.startsWith( TIMEFORMATPROPERTIES ) ) {
		        tfArr.add( name );
		    }
		}
		*/

		/* fetch actual formats */
		if (tfArr.size() == 0) {/* no props found - make sure some default formats are avail */
			tfArr.add("dd-MMM-yy");
			tfArr.add("d-MMM-yyyy");
			tfArr.add("EEE, dd-MMM-yyyy, zzzz");
		} else {
			Collections.sort(tfArr);

			/*:FVK:
			for (int i = 0; i < tfArr.size(); i++) {
			    tfArr.set(i, props.getProperty(tfArr.get(i)));
			}
			*/
		}

		final String prefTimeZone = (Preferences.getPreference(context, "TimeZone") == null) ? "UTC"
				: Preferences.getPreference(context, "TimeZone"); // :FVK: workaround.
		final TimeZone tz = TimeZone.getTimeZone(prefTimeZone);

		final Date d = new Date(); // current date
		try {
			// dummy format pattern
			final SimpleDateFormat fmt = Preferences.getDateFormat(context, TimeFormat.DATETIME);
			fmt.setTimeZone(tz);

			for (final String s : tfArr) {
				try {
					final String f = s;
					fmt.applyPattern(f);
					resultMap.put(f, fmt.format(d));
				} catch (final IllegalArgumentException e) {
				} // skip parameter
			}
		} catch (final IllegalArgumentException e) {
		} // skip parameter

		return resultMap;
	}

	/*
	 *  Always returns a valid property map.
	 */
	/*
	private Properties getTemplateProperties( String templateName )
	    throws IOException
	{
	    Properties p = new Properties();
	
	    ServletContext context = m_engine.getServletContext();
	
	    InputStream propertyStream = context.getResourceAsStream(getPath(templateName)+PROPERTYFILE);
	
	    if( propertyStream != null )
	    {
	        p.load( propertyStream );
	
	        propertyStream.close();
	    }
	    else
	    {
	        log.debug("Template '"+templateName+"' does not have a propertyfile '"+PROPERTYFILE+"'.");
	    }
	
	    return p;
	}
	*/

	/** {@inheritDoc} */
	@Override
	public Collection<WikiModuleInfo> modules() {
		return new ArrayList<>();
	}

	/** {@inheritDoc} */
	@Override
	public WikiModuleInfo getModuleInfo(final String moduleName) {
		return null;
	}

}
