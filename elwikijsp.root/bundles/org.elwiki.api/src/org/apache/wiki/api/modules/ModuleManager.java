package org.apache.wiki.api.modules;

import java.util.Collection;

@Deprecated
public interface ModuleManager {

	/**
	 * Location of the property-files of plugins. (Each plugin should include this property-file in its jar-file)
	 */
	/* Example of content "ini/jspwiki_module.xml" :
	<plugin class="org.apache.wiki.plugin.IfPlugin">
	  <author>Janne Jalkanen</author>
	  <minVersion>2.4</minVersion>
	  <alias>If</alias>
	</plugin>
	*/
	@Deprecated
    String PLUGIN_RESOURCE_LOCATION = "ini/jspwiki_module.xml";

    /**
     *  Returns true, if the given module is compatible with this version of JSPWiki.
     *
     *  @param info The module to check
     *  @return True, if the module is compatible.
     */
    boolean checkCompatibility( WikiModuleInfo info );

    /**
     * Returns the {@link WikiModuleInfo} information about the provided moduleName.
     *
     * @param moduleName
     * @return The wikiModuleInfo
     */
    WikiModuleInfo getModuleInfo( String moduleName );

    /**
     * Returns a collection of modules currently managed by this ModuleManager.  Each
     * entry is an instance of the WikiModuleInfo class.  This method should return something
     * which is safe to iterate over, even if the underlying collection changes.
     *
     * @return A Collection of WikiModuleInfo instances.
     */
    Collection< WikiModuleInfo > modules();

}
