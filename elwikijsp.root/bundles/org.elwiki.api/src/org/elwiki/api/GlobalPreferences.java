package org.elwiki.api;

import org.eclipse.core.runtime.IPath;
import org.elwiki.api.component.WikiManager;

public interface GlobalPreferences extends WikiManager {

	String getApplicationName();

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

}
