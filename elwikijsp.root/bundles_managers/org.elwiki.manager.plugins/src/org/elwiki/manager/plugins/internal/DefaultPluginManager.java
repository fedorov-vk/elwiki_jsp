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

package org.elwiki.manager.plugins.internal;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.modules.BaseModuleManager;
import org.apache.wiki.api.modules.WikiModuleInfo;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.FileUtil;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.localization.BundleLocalization;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.api.plugin.PluginManager;
import org.elwiki.api.plugin.WikiPlugin;
import org.elwiki.configuration.IWikiConfiguration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 *  Manages plugin classes.  There exists a single instance of PluginManager
 *  per each instance of Engine, that is, each JSPWiki instance.
 *  <P>
 *  A plugin is defined to have three parts:
 *  <OL>
 *    <li>The plugin class
 *    <li>The plugin parameters
 *    <li>The plugin body
 *  </ol>
 *
 *  For example, in the following line of code:
 *  <pre>
 *  [{INSERT org.elwiki.plugins.FunnyPlugin  foo='bar'
 *  blob='goo'
 *
 *  abcdefghijklmnopqrstuvw
 *  01234567890}]
 *  </pre>
 *
 *  The plugin class is "org.elwiki.plugins.FunnyPlugin", the
 *  parameters are "foo" and "blob" (having values "bar" and "goo",
 *  respectively), and the plugin body is then
 *  "abcdefghijklmnopqrstuvw\n01234567890".   The plugin body is
 *  accessible via a special parameter called "_body".
 *  <p>
 *  If the parameter "debug" is set to "true" for the plugin,
 *  JSPWiki will output debugging information directly to the page if there
 *  is an exception.
 *  <P>
 *  The class name can be shortened, and marked without the package.
 *  For example, "FunnyPlugin" would be expanded to
 *  "org.elwiki.plugins.FunnyPlugin" automatically.
 *  <P>
 *  Even though the nominal way of writing the plugin is
 *  <pre>
 *  [{INSERT pluginclass WHERE param1=value1...}],
 *  </pre>
 *  it is possible to shorten this quite a lot, by skipping the
 *  INSERT, and WHERE words, and dropping the package name.  For
 *  example:
 *
 *  <pre>
 *  [{INSERT org.apache.wiki.plugin.Counter WHERE name='foo'}]
 *  </pre>
 *
 *  is the same as
 *  <pre>
 *  [{Counter name='foo'}]
 *  </pre>
 *  <h3>Plugin property files</h3>
 *  <p>
 *  Since 2.3.25 you can also define a generic plugin XML properties file per
 *  each JAR file.
 *  <pre>
 *  <modules>
 *   <plugin class="org.apache.wiki.foo.TestPlugin">
 *       <author>Janne Jalkanen</author>
 *       <script>foo.js</script>
 *       <stylesheet>foo.css</stylesheet>
 *       <alias>code</alias>
 *   </plugin>
 *   <plugin class="org.apache.wiki.foo.TestPlugin2">
 *       <author>Janne Jalkanen</author>
 *   </plugin>
 *   </modules>
 *  </pre>
 *  <h3>Plugin lifecycle</h3>
 *
 *  <p>Plugin can implement multiple interfaces to let JSPWiki know at which stages they should
 *  be invoked:
 *  <ul>
 *  <li>InitializablePlugin: If your plugin implements this interface, the initialize()-method is
 *      called once for this class
 *      before any actual execute() methods are called.  You should use the initialize() for e.g.
 *      precalculating things.  But notice that this method is really called only once during the
 *      entire Engine lifetime.  The InitializablePlugin is available from 2.5.30 onwards.</li>
 *  <li>ParserStagePlugin: If you implement this interface, the executeParse() method is called
 *      when JSPWiki is forming the DOM tree.  You will receive an incomplete DOM tree, as well
 *      as the regular parameters.  However, since JSPWiki caches the DOM tree to speed up later
 *      places, which means that whatever this method returns would be irrelevant.  You can do some DOM
 *      tree manipulation, though.  The ParserStagePlugin is available from 2.5.30 onwards.</li>
 *  <li>Plugin: The regular kind of plugin which is executed at every rendering stage.  Each
 *      new page load is guaranteed to invoke the plugin, unlike with the ParserStagePlugins.</li>
 *  </ul>
 */
//@formatter:off
@Component(
	name = "elwiki.DefaultPluginManager",
	service = { PluginManager.class, WikiManager.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class DefaultPluginManager extends BaseModuleManager implements PluginManager, WikiManager, EventHandler {

	private static final Logger log = Logger.getLogger(DefaultPluginManager.class);

	private static final String PLUGIN_INSERT_PATTERN = "\\{?(INSERT)?\\s*([\\w\\._]+)[ \\t]*(WHERE)?[ \\t]*";

	private static final String PLIGIN_ID = "org.elwiki.api"; 

	private static final String ID_EXTENSION_WIKI_PLUGIN = "WikiPlugin";

    private static final int QUOTE_CHARACTER = '\'';
    private static final int COLON_CHARACTER = ':';

	private Pattern m_pluginPattern;

	/** Indicates whether plugin execution is enabled or disabled. */
	private boolean m_pluginsEnabled = true;

	/** Keeps a list of all known plugin classes. */
	private Map<String, WikiPluginInfo> m_pluginClassMap = new HashMap<>();

	/**
	 * Creates instance of PluginManager.
	 */
	public DefaultPluginManager() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	@Reference
	private BundleLocalization bundleLocalization;
	
	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

    @WikiServiceReference
    protected Engine m_engine;

	/**
	 * This component activate routine. Initializes basic stuff.
	 *
	 * @param componentContext
	 * @throws WikiException
	 */
	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		// BundleContext bc = componentContext.getBundleContext();
		// bundle = bc.getBundle();
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
        registerPlugins();

        final PatternCompiler compiler = new Perl5Compiler();
        try {
            m_pluginPattern = compiler.compile( PLUGIN_INSERT_PATTERN );
        } catch( final MalformedPatternException e ) {
            log.fatal( "Internal error: someone messed with pluginmanager patterns.", e );
            throw new InternalWikiException( "PluginManager patterns are broken" , e );
        }
	}

	// -- OSGi service handling ------------------------(end)--

	/**
	 * Register all plugins which have created a resource containing its properties.
	 */
	private void registerPlugins() {
		log.info("<-start-> Registering plugins.");
		int pluginsCount = 0;

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep = registry.getExtensionPoint(PLIGIN_ID, ID_EXTENSION_WIKI_PLUGIN);
		if (ep != null) {
			for (IConfigurationElement el : ep.getConfigurationElements()) {
				WikiPluginInfo pluginInfo = WikiPluginInfo.newInstance(el, m_engine);
				if (pluginInfo != null) {
					pluginsCount++;
					String name;
					// Register the plugin with the its name.
					name = pluginInfo.getName();
					if (name != null) {
						// log.debug("Registering plugin [name]: " + name);
						m_pluginClassMap.put(name, pluginInfo);
					}
					// Register the plugin with a short convenient name.
					name = pluginInfo.getAlias();
					if (name != null) {
						// log.debug("Registering plugin [shortName]: " + name);
						m_pluginClassMap.put(name, pluginInfo);
					}
				}
			}
		}
		log.info("<-end-> Registering plugins. Registered (" + pluginsCount + ") plugins.");
    }

	/** {@inheritDoc} */
	@Override
	public void enablePlugins(boolean enabled) {
		m_pluginsEnabled = enabled;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEnabledPlugins() {
		return m_pluginsEnabled;
	}

    /** {@inheritDoc} */
    @Override
    public Pattern getPluginPattern() {
		return m_pluginPattern;
	}

    /** Outputs a HTML-formatted version of a stack trace. */
    private String stackTrace( final Map<String,String> params, final Throwable t ) {
    	assert false;
    	/*:FVK: TODO: ...
        final Element div = XhtmlUtil.element( XHTML.div, "Plugin execution failed, stack trace follows:" );
        div.setAttribute( XHTML.ATTR_class, "debug" );

        final StringWriter out = new StringWriter();
        t.printStackTrace( new PrintWriter( out ) );
        div.addContent( XhtmlUtil.element( XHTML.pre, out.toString() ) );
        div.addContent( XhtmlUtil.element( XHTML.b, "Parameters to the plugin" ) );

        final Element list = XhtmlUtil.element( XHTML.ul );
        for( final Map.Entry< String, String > e : params.entrySet() ) {
            final String key = e.getKey();
            list.addContent( XhtmlUtil.element( XHTML.li, key + "'='" + e.getValue() ) );
        }
        div.addContent( list );
        return XhtmlUtil.serialize( div );
        */
    	return ":FVK: ~~~~~~~~~~~~~~~~~~~~~~~~\n~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
    }

    /** {@inheritDoc} */
    @Override
	public String execute(WikiContext context, String pluginName, Map<String, String> params) throws PluginException {
		if (!m_pluginsEnabled) {
			return "";
		}

		var value = BooleanUtils.toBooleanObject(params.get(PARAM_DEBUG));
		boolean debug = (value != null) ? value : false;
		try {
			//   Get...
			WikiPlugin plugin = getWikiPlugin(pluginName, context);
			if (plugin == null) {
				return "Plugin '" + pluginName + "' not compatible with this version of JSPWiki";
			}

			//  ...and launch.
			try {
				return plugin.execute(context, params);
			} catch (PluginException e) {
				if (debug) {
					return stackTrace(params, e);
				}

				// Just pass this exception onward.
				throw (PluginException) e.fillInStackTrace();
			} catch (Throwable t) {
				// But all others get captured here.
				log.info("Plugin failed while executing:", t);
				if (debug) {
					return stackTrace(params, t);
				}

				throw new PluginException(
						PluginsActivator.getMessage("plugin.error.failed", Preferences.getLocale(context)), t);
			}

		} catch (ClassCastException e) {
			String pattern = PluginsActivator.getMessage("plugin.error.notawikiplugin", Preferences.getLocale(context));
			throw new PluginException(MessageFormat.format(pattern, pluginName), e);
		}
	}

    /** {@inheritDoc} */
    @Override
    public Map< String, String > parseArgs( final String argstring ) throws IOException {
        final Map< String, String > arglist = new HashMap<>();
        //  Protection against funny users.
        if( argstring == null ) {
            return arglist;
        }

        arglist.put( PARAM_CMDLINE, argstring );
        final StringReader in = new StringReader( argstring );
        final StreamTokenizer streamTokenizer = new StreamTokenizer( in );
        streamTokenizer.eolIsSignificant( true );

        String param = null;
        String value;
        boolean potentialEmptyLine = false;
        boolean quit = false;
        while( !quit ) {
            final String s;
            final int type = streamTokenizer.nextToken();

            switch( type ) {
			case COLON_CHARACTER:
				potentialEmptyLine = true;
			case StreamTokenizer.TT_EOF:
				quit = true;
				s = null;
				break;

            case StreamTokenizer.TT_WORD:
                s = streamTokenizer.sval;
                potentialEmptyLine = false;
                break;

            case StreamTokenizer.TT_EOL:
                quit = potentialEmptyLine;
                potentialEmptyLine = true;
                s = null;
                break;

            case StreamTokenizer.TT_NUMBER:
                s = Integer.toString( ( int )streamTokenizer.nval );
                potentialEmptyLine = false;
                break;

            case QUOTE_CHARACTER:
                s = streamTokenizer.sval;
                break;

            default:
                s = null;
            }

            //  Assume that alternate words on the line are parameter and value, respectively.
            if( s != null ) {
                if( param == null ) {
                    param = s;
                } else {
                    value = s;
                    arglist.put( param, value );
                    param = null;
                }
            }
        }

        //  Now, we'll check the body.
        if( potentialEmptyLine ) {
            final StringWriter out = new StringWriter();
            FileUtil.copyContents( in, out );
            final String bodyContent = out.toString();
            if( bodyContent != null ) {
                arglist.put( PARAM_BODY, bodyContent );
            }
        }

        return arglist;
    }

    /** {@inheritDoc} */
    @Override
    public String execute( final WikiContext context, final String commandline ) throws PluginException {
        if( !m_pluginsEnabled ) {
            return "";
        }

        final PatternMatcher matcher = new Perl5Matcher();

        try {
            if( matcher.contains( commandline, m_pluginPattern ) ) {
                final MatchResult res = matcher.getMatch();
                final String plugin = res.group( 2 );
                final int endIndex = commandline.length() - ( commandline.charAt( commandline.length() - 1 ) == '}' ? 1 : 0 );
                final String args = commandline.substring( res.endOffset( 0 ), endIndex );
                final Map< String, String > arglist = parseArgs( args );
                return execute( context, plugin, arglist );
            }
		} catch (final NoSuchElementException e) {
			final String msg = "Missing parameter in plugin definition: " + commandline;
			log.warn(msg, e);
			String pattern = PluginsActivator.getMessage("plugin.error.missingparameter",
					Preferences.getLocale(context));
			throw new PluginException(MessageFormat.format(pattern, commandline));
		} catch (final IOException e) {
			final String msg = "Zyrf.  Problems with parsing arguments: " + commandline;
			log.warn(msg, e);
			String pattern = PluginsActivator.getMessage("plugin.error.parsingarguments",
					Preferences.getLocale(context));
			throw new PluginException(MessageFormat.format(pattern, commandline));
		}

        // FIXME: We could either return an empty string "", or the original line.  If we want unsuccessful requests
        // to be invisible, then we should return an empty string.
        return commandline;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Collection< WikiModuleInfo > modules() {
        return modules( m_pluginClassMap.values().iterator() );
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public WikiPluginInfo getModuleInfo( final String moduleName) {
        return m_pluginClassMap.get(moduleName);
    }

	/**
	 * Creates a {@link WikiPlugin}.
	 *
	 * @param pluginName plugin's classname
	 * @param context    The current WikiContext. Required for selecting locale of messages. If
	 *                   <code>null</code> - then chosen default locale.
	 * @return a {@link WikiPlugin}.
	 * @throws PluginException if there is a problem building the {@link WikiPlugin}.
	 */
	@Override
	public WikiPlugin getWikiPlugin(String pluginName, WikiContext context) throws PluginException {
		ResourceBundle rb = PluginsActivator.getBundle(Preferences.getLocale(context));
		WikiPlugin plugin = null;
		WikiPluginInfo pluginInfo = m_pluginClassMap.get(pluginName);

		if (pluginInfo == null) {
			throw new PluginException(MessageFormat.format(rb.getString("plugin.error.couldnotfind"), pluginName));
		}

		if (!checkCompatibility(pluginInfo)) {
			//TODO: investigate compatibility checking. (make another abstraction, for example version of package...)
			String msg = "Plugin '" + pluginInfo.getName() + "' not compatible with this version of JSPWiki";
			log.info(msg);
		} else {
			plugin = pluginInfo.getPluginInstance();
		}

		return plugin;
	}
	
	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
			break;
		}*/		
	}

}
