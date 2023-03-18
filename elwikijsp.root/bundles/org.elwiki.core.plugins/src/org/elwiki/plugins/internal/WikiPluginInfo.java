package org.elwiki.plugins.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.modules.WikiModuleInfo;
import org.apache.wiki.api.plugin.InitializablePlugin;
import org.apache.wiki.api.plugin.Plugin;
import org.apache.wiki.preferences.Preferences;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 *  Contains information about a bunch of plugins.
 */
// FIXME: This class needs a better interface to return all sorts of possible information
//  from the plugin XML.
//  In fact, it probably should have some sort of a superclass system.
final class WikiPluginInfo extends WikiModuleInfo {

	private static final Logger log = Logger.getLogger(WikiPluginInfo.class);
	
    private String    m_className;
    private String    m_alias;
    private String    m_ajaxAlias;
    private Class<Plugin>  m_clazz;

    private boolean m_initialized = false;

	/** Implementation of WikiPlugin class. */
	private Plugin wikiPlugin;

    /**
     * Creates a new plugin info object which can be used to access a plugin.<br/>
     * At the end, PluginInfo contains intialized plugin instance.
     * 
     * @param el Configuration element of extension point section 'WikiPlugin'. 
     * @param engine Wiki Engine.
     * @return A WikiPluginInfo object. (can be <code>NULL</code>)
     */
    public static WikiPluginInfo newInstance(IConfigurationElement el, Engine engine) {
    	WikiPluginInfo info = null;
		String name = el.getAttribute("name");
		if (StringUtils.isNotBlank(name)) {
			try {
				info = new WikiPluginInfo(name);
				info.build(el);
				info.initializePlugin(engine );
			} catch (Exception e) {
				log.info("Fail load plugin " + name);
			}
		}

		return info;
	}

    /**
     *  Create a new WikiPluginInfo based on the Class information.
     *
     *  @param clazz The class to check
     *  @return A WikiPluginInfo instance
     */
    @Deprecated
    protected static WikiPluginInfo newInstance( final Class< ? > clazz ) {
    	return new WikiPluginInfo( clazz.getName() );
    }

    /**
     * Create instance of WikiPluginInfo.
     *
     * @param name Name of wiki plugin.
     */
    private WikiPluginInfo(String name ) {
        super( name );
    }

	@SuppressWarnings("unchecked")
	private void build(IConfigurationElement el) throws Exception {
		this.m_alias = el.getAttribute("alias");
		this.m_ajaxAlias = el.getAttribute("ajaxAlias");

		String contributorName = el.getContributor().getName();
		String className = el.getAttribute("class");
		Bundle bundle = Platform.getBundle(contributorName);
		Class<?> clazz = bundle.loadClass(className);
		if (Plugin.class.isAssignableFrom(clazz)) {
			this.m_clazz = (Class<Plugin>) clazz;
			this.m_className = clazz.getName();
		} else {
			throw new PluginException("Plugin " + m_name + " is not implemented wiki plugin.");
		}
	}
    
	/**
     *  Initializes a plugin, if it has not yet been initialized. If the plugin extends {@link HttpServlet} it will automatically
     *  register it as AJAX using {@link WikiAjaxDispatcherServlet#registerServlet(String, WikiAjaxServlet)}.
	 * @param engine The Engine
     */
	protected void initializePlugin(Engine engine) throws PluginException {
        if( !m_initialized ) {
            // This makes sure we only try once per class, even if init fails.
            m_initialized = true;

			Locale locale = Preferences.getConfiguredLocale(engine);
			if (locale == null)
				locale = Locale.getDefault();
			ResourceBundle rb = PluginsActivator.getBundle(locale);

			try {
				this.wikiPlugin = m_clazz.getDeclaredConstructor().newInstance();
			} catch (IllegalAccessException e1) {
				throw new PluginException(MessageFormat.format(rb.getString("plugin.error.notallowed"), //
						m_name), e1);
			} catch (Exception e2) {
				throw new PluginException(MessageFormat.format(rb.getString("plugin.error.cannotinstantiate"), //
						m_name), e2);
			}
			
            try {
                if( wikiPlugin instanceof InitializablePlugin initializablePlugin) {
                	initializablePlugin.initialize( engine );
                }
                if( wikiPlugin instanceof WikiAjaxServlet wikiAjaxServlet) {
                	WikiAjaxDispatcher wikiAjaxDispatcher = engine.getManager(WikiAjaxDispatcher.class);
                	wikiAjaxDispatcher.registerServlet( wikiAjaxServlet );
                	final String ajaxAlias = this.getAjaxAlias();
                	if (StringUtils.isNotBlank(ajaxAlias)) {
                		wikiAjaxDispatcher.registerServlet( this.getAjaxAlias(), wikiAjaxServlet );
                	}
                }
            } catch( final Exception e ) {
            	//TODO: externalize message.
                log.info( "Cannot initialize plugin " + this.m_name, e );
            }
        }
    }

	public Plugin getPluginInstance() {
		return this.wikiPlugin;
	}
	
    /**
     *  Returns the full class name of this object.
     *  @return The full class name of the object.
     */
	@Deprecated
    public String getClassName() {
        return m_className;
    }

    /**
     *  Returns the alias name for this object.
     *  @return An alias name for the plugin.
     */
    public String getAlias() {
        return m_alias;
    }

    /**
     *  Returns the ajax alias name for this object.
     *  @return An ajax alias name for the plugin.
     */
    public String getAjaxAlias() {
        return m_ajaxAlias;
    }

    /**
     *  Returns a text for IncludeResources.
     *
     *  @param type Either "script" or "stylesheet"
     *  @return Text, or an empty string, if there is nothing to be included.
     */
    public String getIncludeText( final String type ) {
        try {
            if( "script".equals( type ) ) {
                return getScriptText();
            } else if( "stylesheet".equals( type ) ) {
                return getStylesheetText();
            }
        } catch( final Exception ex ) {
            // We want to fail gracefully here
            return ex.getMessage();
        }

        return null;
    }

    private String getScriptText() throws IOException {
        if( m_scriptText != null ) {
            return m_scriptText;
        }

        if( m_scriptLocation == null ) {
            return "";
        }

        try {
            m_scriptText = getTextResource(m_scriptLocation);
        } catch( final IOException ex ) {
            // Only throw this exception once!
            m_scriptText = "";
            throw ex;
        }

        return m_scriptText;
    }

    private String getStylesheetText() throws IOException {
        if( m_stylesheetText != null ) {
            return m_stylesheetText;
        }

        if( m_stylesheetLocation == null ) {
            return "";
        }

        try {
            m_stylesheetText = getTextResource(m_stylesheetLocation);
        } catch( final IOException ex ) {
            // Only throw this exception once!
            m_stylesheetText = "";
            throw ex;
        }

        return m_stylesheetText;
    }

    /**
     *  Returns a string suitable for debugging.  Don't assume that the format would stay the same.
     *
     *  @return Something human-readable
     */
    @Override
    public String toString() {
        return "Plugin :[name=" + m_name + "][className=" + m_className + "]";
    }

} // WikiPluginInfo
