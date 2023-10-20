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
package org.apache.wiki.preferences;

import java.util.HashMap;
import com.google.gson.Gson;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.WikiContext.TimeFormat;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.util.PropertyReader;
import org.apache.wiki.util.TextUtil;
import org.eclipse.jface.preference.IPreferenceStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Represents an object which is used to store user preferences.
 */
public class Preferences extends HashMap<String, String> {

	private static final long serialVersionUID = 1782510796921957634L;

	private static final Logger logger = Logger.getLogger(Preferences.class);

	/**
	 * The name under which a Preferences object is stored in the HttpSession. Its value is
	 * {@value}.
	 */
	public static final String SESSIONPREFS = "prefs";

	/**
	 * This is an utility method which is called to make sure that the JSP pages do have proper
	 * access to any user preferences. It should be called from the commonheader.jsp.
	 * <p>
	 * This method reads user cookie preferences and mixes them up with any default preferences (and
	 * in the future, any user-specific preferences) and puts them all in the session, so that they
	 * do not have to be rewritten again.
	 * <p>
	 * This method will remember if the user has already changed his prefs.
	 *
	 * @param pageContext The JSP PageContext.
	 */
	public static void setupPreferences(final PageContext pageContext) {
		// HttpSession session = pageContext.getSession();
		// if( session.getAttribute( SESSIONPREFS ) == null )
		// {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		reloadPreferences(request);
		// }
	}

	/**
	 * Reloads the preferences from the HttpServletRequest into the WikiContext.
	 *
	 * @param request The http servlet request.
	 */
	// FIXME: The way that date preferences are chosen is currently a bit wacky: it all gets saved
	// to the cookie based on the browser state
	// with which the user happened to first arrive to the site with. This, unfortunately, means
	// that even if the user changes e.g.
	// language preferences (like in a web cafe), the old preferences still remain in a site cookie.
	public static void reloadPreferences(HttpServletRequest request) {
		final Preferences prefs = new Preferences();
		Engine engine = ContextUtil.findContext(request).getEngine();

		// :FVK: - replaced: Properties props = PropertyReader.loadWebAppProps(
		// pageContext.getServletContext() );
		IPreferenceStore props = engine.getWikiPreferences();

		final WikiContext ctx = ContextUtil.findContext(request);
		InternationalizationManager i18n = engine.getManager(InternationalizationManager.class);
		final String dateFormat = i18n.get(getLocale(ctx), "common.datetimeformat");

		/*:FVK:
		*///@formatter:off
		prefs.put("SkinName", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.skinname", "PlainVanilla"));
		prefs.put("DateFormat", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.dateformat", dateFormat));
		prefs.put("TimeZone", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.timezone", java.util.TimeZone.getDefault().getID()));
		prefs.put("Orientation", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.orientation", "fav-left"));
		prefs.put("Sidebar", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.sidebar", "active"));
		prefs.put("Layout", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.layout", "fluid"));
		prefs.put("Language", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.language", getLocale(ctx).toString()));
		prefs.put("SectionEditing", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.sectionediting", "true"));
		prefs.put("Appearance", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.appearance", "true"));

		prefs.put("SidePanel", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.sidepanel", "menu"));

		//editor cookies
		prefs.put("autosuggest", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.autosuggest", "true"));
		prefs.put("tabcompletion", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.tabcompletion", "true"));
		prefs.put("smartpairs", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.smartpairs", "false"));
		prefs.put("livepreview", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.livepreview", "true"));
		prefs.put("previewcolumn", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.previewcolumn", "true"));

		// FIXME: editormanager reads jspwiki.editor -- which of both properties should continue
		prefs.put("editor", TextUtil.getStringProperty(props, "jspwiki.defaultprefs.template.editor", "plain"));
		//@formatter:on

		parseJSONPreferences(request, prefs);
		request.getSession().setAttribute(SESSIONPREFS, prefs);
	}

	/**
	 * Parses new-style preferences stored as JSON objects and stores them in the session.
	 * Everything in the cookie is stored.
	 *
	 * @param request
	 * @param prefs   The default hashmap of preferences
	 */
	private static void parseJSONPreferences(final HttpServletRequest request, final Preferences prefs) {
		final String prefVal = TextUtil.urlDecodeUTF8(HttpUtil.retrieveCookieValue(request, "ElWikiUserPrefs"));
		if (prefVal != null) {
			// Convert prefVal JSON to a generic hashmap
			@SuppressWarnings("unchecked")
			final Map<String, String> map = new Gson().fromJson(prefVal, Map.class);
			for (String key : map.keySet()) {
				key = TextUtil.replaceEntities(key);
				// Sometimes this is not a String as it comes from the Cookie set by Javascript
				final Object value = map.get(key);
				if (value != null) {
					prefs.put(key, value.toString());
				}
			}
		}
	}

	/**
	 * Returns a preference value programmatically. FIXME
	 *
	 * @param wikiContext
	 * @param name
	 * @return the preference value
	 */
	public static String getPreference(final WikiContext wikiContext, final String name) {
		if (wikiContext == null) {
			return null;
		}

		HttpServletRequest request = wikiContext.getHttpRequest();
		if (request == null) {
			return null;
		}

		Preferences prefs = (Preferences) request.getSession().getAttribute(SESSIONPREFS);
		if (prefs != null) {
			return prefs.get(name);
		}

		return null;
	}

	/**
	 * Returns a preference value programmatically. FIXME
	 *
	 * @param pageContext
	 * @param name
	 * @return the preference value
	 */
	public static String getPreference(final PageContext pageContext, final String name) {
		final Preferences prefs = (Preferences) pageContext.getSession().getAttribute(SESSIONPREFS);
		if (prefs != null) {
			return prefs.get(name);
		}

		return null;
	}

	public static Locale getConfiguredLocale(Engine engine) {
		Locale result = null;
		if (engine != null) {
			String locale = engine.getWikiPreferences().getString("jspwiki.preferences.default-locale");
			try {
				result = LocaleUtils.toLocale(locale);
			} catch (final IllegalArgumentException iae) {
				logger.error(iae.getMessage());
			}
		}

		return result;
	}

	/**
	 * Get Locale according to user-preference settings or the user browser locale
	 *
	 * @param context The context to examine.
	 * @return a Locale object.
	 * @since 2.8
	 */
	public static Locale getLocale(WikiContext context) {
		Locale loc = null;

		String langSetting = getPreference(context, "Language");

		// parse language and construct valid Locale object
		if (langSetting != null) {
			String language = "";
			String country = "";
			String variant = "";

			String[] res = StringUtils.split(langSetting, "-_");
			if (res.length > 2) {
				variant = res[2];
			}
			if (res.length > 1) {
				country = res[1];
			}
			if (res.length > 0) {
				language = res[0];
				loc = new Locale(language, country, variant);
			}
		}

		// see if default locale is set server side
		if (loc == null && context != null) {
			loc = getConfiguredLocale(context.getEngine());
		}

		// otherwise try to find out the browser's preferred language setting, or use the JVM's default
		if (loc == null) {
			HttpServletRequest request = (context != null) ? context.getHttpRequest() : null;
			loc = (request != null) ? request.getLocale() : Locale.getDefault();
		}

		//:FVK: logger.debug("using locale " + loc.toString());
		return loc;
	}

	/**
	 * Get SimpleTimeFormat according to user browser locale and preferred time formats. If not
	 * found, it will revert to whichever format is set for the default.
	 *
	 * @param context WikiContext to use for rendering.
	 * @param tf      Which version of the dateformat you are looking for?
	 * @return A SimpleTimeFormat object which you can use to render
	 * @since 2.8
	 */
	public static SimpleDateFormat getDateFormat(final WikiContext context, final TimeFormat tf) {
		final InternationalizationManager imgr = context.getEngine().getManager(InternationalizationManager.class); 
		final Locale clientLocale = getLocale(context);
		final String prefTimeZone = getPreference(context, "TimeZone");
		String prefDateFormat;

		logger.debug("Checking for preferences...");
		switch (tf) {
		case DATETIME:
			prefDateFormat = getPreference(context, "DateFormat");
			logger.debug("Preferences fmt = " + prefDateFormat);
			if (prefDateFormat == null) {
				prefDateFormat = imgr.get(clientLocale, "common.datetimeformat");
				logger.debug("Using locale-format = " + prefDateFormat);
			}
			break;

		case TIME:
			prefDateFormat = imgr.get("common.timeformat");
			break;

		case DATE:
			prefDateFormat = imgr.get("common.dateformat");
			break;

		default:
			throw new InternalWikiException("Got a TimeFormat for which we have no value!");
		}

		try {
			final SimpleDateFormat fmt = new SimpleDateFormat(prefDateFormat, clientLocale);
			if (prefTimeZone != null) {
				final TimeZone tz = TimeZone.getTimeZone(prefTimeZone);
				// TimeZone tz = TimeZone.getDefault();
				// tz.setRawOffset(Integer.parseInt(prefTimeZone));
				fmt.setTimeZone(tz);
			}

			return fmt;
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Locates the i18n ResourceBundle given. This method interprets the request locale, and uses
	 * that to figure out which language the user wants.
	 *
	 * @param context {@link WikiContext} holding the user's locale
	 * @return A localized string (or from the default language, if not found)
	 * @throws MissingResourceException If the bundle cannot be found
	 * @see org.apache.wiki.i18n.InternationalizationManager
	 */
	public static ResourceBundle getBundle(WikiContext context) throws MissingResourceException {
		final Locale loc = getLocale(context);
		final InternationalizationManager i18n = context.getEngine().getManager(InternationalizationManager.class);
		return i18n.getBundle(loc);
	}

	/**
	 * A simple helper function to render a date based on the user preferences. This is useful for
	 * example for all plugins.
	 *
	 * @param context The context which is used to get the preferences
	 * @param date    The date to render.
	 * @param tf      In which format the date should be rendered.
	 * @return A ready-rendered date.
	 * @since 2.8
	 */
	public static String renderDate(final WikiContext context, final Date date, final TimeFormat tf) {
		final DateFormat df = getDateFormat(context, tf);
		return df.format(date);
	}

}
