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
package org.elwiki.api.plugin;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.oro.text.regex.Pattern;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.modules.ModuleManager;

public interface PluginManager extends ModuleManager {

    /** The name of the body content. Current value is "_body". */
    String PARAM_BODY      = "_body";

    /** The name of the command line content parameter. The value is "_cmdline". */
    String PARAM_CMDLINE   = "_cmdline";

    /**
     *  The name of the parameter containing the start and end positions in the read stream of the plugin text (stored as a two-element
     *  int[], start and end resp.).
     */
    String PARAM_BOUNDS    = "_bounds";

    /** A special name to be used in case you want to see debug output */
    String PARAM_DEBUG     = "debug";

    /**
     * Indicates whether plugin execution is enabled or disabled.
     * 
     * @param enabled True, if plugins should be globally enabled; false, if disabled.
     */
    void enablePlugins( boolean enabled );

    /**
     * Returns plugin execution status. If false, plugins are not executed when they are encountered on a WikiPage, and an
     * empty string is returned in their place.
     * 
     * @return True, if plugins are enabled; false otherwise.
     */
    boolean isEnabledPlugins();
    
    /**
     * Returns plugin insert pattern.
     * 
     * @return plugin insert pattern.
     */
    Pattern getPluginPattern();

    /**
     * Executes a plugin class in the given context.
     *
     * @param context The current WikiContext.
     * @param pluginName The name of the plugin.
     * @param params A parsed map of key-value pairs.
     * @return Whatever the plugin returns.
     * @throws PluginException If the plugin execution failed for some reason.
     */
    String execute( WikiContext context, String pluginName, Map< String, String > params ) throws PluginException;

    /**
     * Parses plugin arguments.  Handles quotes and all other kewl stuff.
     *
     * <h3>Special parameters</h3>
     * The plugin body is put into a special parameter defined by {@link #PARAM_BODY}; the plugin's command line into a parameter defined
     * by {@link #PARAM_CMDLINE}; and the bounds of the plugin within the wiki page text by a parameter defined by {@link #PARAM_BOUNDS},
     * whose value is stored as a two-element int[] array, i.e., <tt>[start,end]</tt>.
     *
     * @param argstring The argument string to the plugin.  This is typically a list of key-value pairs, using "'" to escape
     * spaces in strings, followed by an empty line and then the plugin body.  In case the parameter is null, will return an
     * empty parameter list.
     * @return A parsed list of parameters.
     * @throws IOException If the parsing fails.
     */
    Map< String, String > parseArgs( String argstring ) throws IOException;

    /**
     *  Parses a plugin.  Plugin commands are of the form:<br/>
     *  {@code [{INSERT myplugin WHERE param1=value1, param2=value2}]}<br/>
     *  myplugin may either be a class name or a plugin alias.
     *  <P>
     *  This is the main entry point that is used.
     *
     *  @param context The current WikiContext.
     *  @param commandline The full command line, including plugin name, parameters and body.
     *  @return HTML as returned by the plugin, or possibly an error message.
     *  @throws PluginException From the plugin itself, it propagates, waah!
     */
    String execute( WikiContext context, String commandline ) throws PluginException;
    
    /**
     * Get a {@link WikiPlugin}.
     * 
     * @param pluginName plugin's classname
     * @param context {@link ResourceBundle} with i18ned text for exceptions.
     * @return a {@link WikiPlugin}.
     * @throws PluginException if there is a problem building the {@link WikiPlugin}.
     */
    WikiPlugin getWikiPlugin( String pluginName, WikiContext context ) throws PluginException;

}
