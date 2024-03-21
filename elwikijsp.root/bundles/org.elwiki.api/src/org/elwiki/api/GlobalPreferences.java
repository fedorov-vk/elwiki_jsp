package org.elwiki.api;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.elwiki.api.component.IModulePreferences;

public interface GlobalPreferences extends IModulePreferences {

	interface Prefs {
		String APP_NAME = "applicationName";

		/** If this property is set to false, we don't allow the creation of empty pages. */
		String ALLOW_CREATION_OF_EMPTY_PAGES = "allowCreationOfEmptyPages";
	}

	String getApplicationName();

	/**
	 * If this property is set to false, we don't allow the creation of empty pages.
	 * 
	 * @return
	 */
	boolean isAllowCreationOfEmptyPages();	

	/**
	 * Returns the ElWiki working directory. Can be set with "elwiki.workDir".
	 * 
	 * @return The working directory.
	 */
	IPath getWorkDir();

	/**
	 * Returns the H2 database path. Database has content of pages.
	 *
	 * @return A path to where the H2 database is placed in the local file system.
	 */
	IPath getDbPlace();

	/**
	 * Returns the attachment path. The attachment path is where the attachment files is located in the
	 * file system.
	 *
	 * @return A path to where the attachment files is placed in the local file system.
	 */
	IPath getAttachmentPath();

	/**
	 * Returns the workspace path. The workspace path is where the workspace is located in the file
	 * system.
	 * 
	 * @return the workspace path.
	 */
	IPath getWorkspacePath();

	/**
	 * Returns the current template directory.
	 * 
	 * @return The template directory as initialized by the configuration.
	 */
	String getTemplateDir();

	/**
	 * Returns a collection of all image types that get inlined.
	 *
	 * @return A Collection of Strings with a regexp pattern.
	 */
	public Collection<String> getAllInlinedImagePatterns();
}
