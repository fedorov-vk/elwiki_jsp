package org.elwiki.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;

import org.apache.log4j.Logger;
import org.apache.wiki.api.cfgoptions.OptionString;
import org.apache.wiki.api.cfgoptions.Options;
import org.apache.wiki.api.exceptions.WikiException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;

public class GlobalPreferencesOptions extends Options {

	private static final Logger log = Logger.getLogger(GlobalPreferencesImpl.class);

	private static final String SERVLET_MAPPING = "PreferencesGlobal";

	private static final String PROP_APP_NAME = "applicationName";
	
	/**
	 * Property name for where the ElWiki work directory should be. If not specified, sets to workspace
	 * place.
	 */
	private static final String SYSTEM_PROP_WORK_DIR = "elwiki.workDir";

	/**
	 * Property name for where the H2 database should be located. If not specified, sets to area in the
	 * workspace place - ${workspace_loc}/.
	 */
	private static final String SYSTEM_PROP_H2_DATABASE_PLACE = "elwiki.h2.database.place";

	/**
	 * Property name for where the ElWiki attachment directory should be. If not specified, it is
	 * defined as a 'pages_attachment' directory located in the Workbench workspace directory.
	 */
	private static final String SYSTEM_PROP_ATTACHMENTDIR = "elwiki.attachmentDir";
	
	private static String ATTACHMENTS_SUBDIRECTORY = "pages_attachment";

	private OptionString optAppName;

	private final IPath pathWorkspace = ResourcesPlugin.getWorkspace().getRoot().getLocation();
	private final IPath pathWorkDir;
	private final IPath dbStorePath;
	private final IPath attachmentStorePath;

	public GlobalPreferencesOptions(BundleContext bundleContext) {
		super(bundleContext);

		IPath path;
		path = getDirectoryPlace(SYSTEM_PROP_WORK_DIR);
		pathWorkDir = (path != null) ? path : pathWorkspace;
		log.info("    Working directory is: '" + pathWorkDir + "'");

		path = getDirectoryPlace(SYSTEM_PROP_H2_DATABASE_PLACE);
		dbStorePath = (path != null) ? path : pathWorkspace;
		log.info("          H2 location is: '" + dbStorePath + "'");

		path = getDirectoryPlace(SYSTEM_PROP_ATTACHMENTDIR);
		if (path != null) {
			attachmentStorePath = path;
		} else {
			// default attachment directory of ElWiki - in the Workbench`s workspace area.
			attachmentStorePath = pathWorkDir.append(ATTACHMENTS_SUBDIRECTORY);
			// Create attachment subdirectory in the default workspace.
			File dir = this.attachmentStorePath.toFile();
			try {
				if (!dir.exists()) {
					dir.mkdirs();
				}
				if (!dir.exists() || !dir.canWrite() || !dir.canRead()) {
					throw new WikiException("Cannot write to attachment directory: " + dir.getAbsolutePath());
				}
			} catch (Exception e) {
				log.error("Fail of create attachment directory: " + e.getMessage());
			}
		}
		log.info(" Attachment directory is: '" + attachmentStorePath + "'");
	}

	private IPath getDirectoryPlace(String preferenceKey) {
		IPath path = null;
		String dir = System.getProperties().getProperty(preferenceKey, null);
		if (dir != null && !dir.isBlank()) {
			try {
				path = checkDirectory(dir, preferenceKey);
			} catch (IOException e) {
				log.error("Directory specified by " + preferenceKey + " if failed due to: " + e.getMessage());
			}
		}
		return path;
	}

	private IPath checkDirectory(String dir, String specifier) throws IOException {
		File fileDir = new File(dir);
		String absoluteDir = fileDir.getAbsolutePath();
		if (!fileDir.exists()) {
			throw new AccessDeniedException("\"" + absoluteDir + "\" directory does not exist.");
		}
		if (!fileDir.canRead()) {
			throw new AccessDeniedException("No permission to read directory \"" + absoluteDir + "\"");
		}
		if (!fileDir.canWrite()) {
			throw new AccessDeniedException("No permission to write to directory \"" + absoluteDir + "\"");
		}
		if (!fileDir.isDirectory()) {
			throw new NotDirectoryException("Target of " + specifier + " - is not a directory \"" + absoluteDir + "\"");
		}
		return new Path(fileDir.getCanonicalPath());
	}

	@Override
	protected String getServletMapping() {
		return SERVLET_MAPPING;
	}

	@Override
	protected String getPreferencesSectionName() {
		return "Global preferences";
	}

	@Override
	protected void populateOptions(BundleContext bundleContext) {
		String infoAppName = "Specifying the Application name.";
		optAppName = new OptionString(bundleContext, PROP_APP_NAME, "Application name", infoAppName, jsonTracker);
		options.add(optAppName);
		actions.add(optAppName);
	}

	public String getApplicationName() {
		return optAppName.getInstanceValue();
	}

	public IPath getWorkDir() {
		return pathWorkDir;
	}

	public IPath getDbPlace() {
		return dbStorePath;
	}

	public IPath getAttachmentPath() {
		return attachmentStorePath;
	}

	public IPath getWorkspacePath() {
		return pathWorkspace;
	}

}
