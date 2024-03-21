package org.elwiki.global_preferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiComponent;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

//@formatter:off
@Component(
	name = "elwiki.GlobalPreferences",
	service = { GlobalPreferences.class, WikiComponent.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class GlobalPreferencesImpl implements GlobalPreferences, WikiComponent {

	private static final Logger log = Logger.getLogger(GlobalPreferencesImpl.class);

	/**
	 * Property name for where the ElWiki work directory should be.<br/>
	 * If not specified, sets to workspace place.
	 */
	private static final String SYSTEM_PROP_WORK_DIR = "elwiki.workDir";

	/**
	 * Property name for where the H2 database should be located.<br/>
	 * If not specified, sets to area in the workspace place - ${workspace_loc}/.
	 */
	private static final String SYSTEM_PROP_H2_DATABASE_PLACE = "elwiki.h2.database.place";

	/**
	 * Property name for where the ElWiki attachment directory should be.<br/>
	 * If not specified, it is defined as a 'pages_attachment' directory located in the Workbench
	 * workspace directory.
	 */
	private static final String SYSTEM_PROP_ATTACHMENTDIR = "elwiki.attachmentDir";

	private static final String ATTACHMENTS_SUBDIRECTORY = "pages_attachment";

	/** Property node for set of inline image patterns. Current value - {@value} */
	private static final String NODE_INLINE_PATTERNS = "node.translatorReader.inlinePatterns";

	/**
	 * Property name for the template that is used.<br/>
	 * If not specified, sets to "default".
	 */
	private static final String PROP_TEMPLATEDIR = "jspwiki.templateDir";

	private final IPath pathWorkspace = ResourcesPlugin.getWorkspace().getRoot().getLocation();
	private IPath pathWorkDir;
	private IPath attachmentStorePath;
	private IPath dbStorePath;

	/**
	 * Stores the template path. This is relative to "shapes".<br/>
	 * (:FVK: here it is necessary to eliminate connection with resource plugin)
	 */
	private String m_templateDir;

	private BundleContext bundleContext;

	// -- OSGi service handling ----------------------(start)--

	@WikiServiceReference
	private Engine engine = null;

	@Activate
	protected void startup(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

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

		this.m_templateDir = getPreference(PROP_TEMPLATEDIR, String.class);
		enforceValidTemplateDirectory();

	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		//
	}

	// -- OSGi service handling ------------------------(end)--

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

	/**
	 * Checks if the template directory specified in the wiki's properties actually exists. If it
	 * doesn't, then {@code m_templateDir} is set to {@link #DEFAULT_TEMPLATE_NAME}.
	 * <p>
	 * This checks the existence of the <tt>ViewTemplate.jsp</tt> file, which exists in every template
	 * using {@code m_servletContext.getRealPath("/")}.
	 * <p>
	 * {@code m_servletContext.getRealPath("/")} can return {@code null} on certain servers/conditions
	 * (f.ex, packed wars), an extra check against {@code m_servletContext.getResource} is made.
	 */
	void enforceValidTemplateDirectory() {
		// TODO: перенести вычисление размещения JSP - относительно osgi-bundle, вместо данных из
		// ServletContext.
		// Место размещения JSP файлов темплейта - определялось из ServletContext.
		// Для ElWiki - это работает через bundle.
		/*
		if (m_servletContext != null) {
			final String viewTemplate = "shapes" + File.separator + getTemplateDir() + File.separator
					+ "ViewTemplate.jsp";
			boolean exists = new File(m_servletContext.getRealPath("/") + viewTemplate).exists();
			if (!exists) {
				try {
					final URL url = m_servletContext.getResource(viewTemplate);
					exists = url != null && !url.getFile().isEmpty();
				} catch (final MalformedURLException e) {
					log.warn("template not found with viewTemplate " + viewTemplate);
				}
			}
			if (!exists) {
				log.warn(getTemplateDir() + " template not found, updating WikiEngine's default template to "
						+ DEFAULT_TEMPLATE_NAME);
				m_templateDir = DEFAULT_TEMPLATE_NAME;
			}
		}
		*/
	}

	@Override
	public BundleContext getBundleContext() {
		return this.bundleContext;
	}

	@Override
	public String getApplicationName() {
		return getPreference(GlobalPreferences.Prefs.APP_NAME, String.class);
	}

	@Override
	public boolean isAllowCreationOfEmptyPages() {
		return getPreference(GlobalPreferences.Prefs.ALLOW_CREATION_OF_EMPTY_PAGES, Boolean.class);
	}

	@Override
	public IPath getWorkDir() {
		return pathWorkDir;
	}

	@Override
	public IPath getDbPlace() {
		return dbStorePath;
	}

	@Override
	public IPath getAttachmentPath() {
		return attachmentStorePath;
	}

	@Override
	public IPath getWorkspacePath() {
		return pathWorkspace;
	}

	/** {@inheritDoc} */
	@Override
	public String getTemplateDir() {
		return this.m_templateDir;
	}

	/** {@inheritDoc} */
	@Override
	public Collection<String> getAllInlinedImagePatterns() {
		String patternsSeq = getPreference(NODE_INLINE_PATTERNS, String.class);
		return Arrays.asList(patternsSeq.split(","));
	}

}
