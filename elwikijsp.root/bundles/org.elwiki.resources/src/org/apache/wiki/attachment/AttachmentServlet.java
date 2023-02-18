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
package org.apache.wiki.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.Permission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.RedirectException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.ui.progress.ProgressItem;
import org.apache.wiki.api.ui.progress.ProgressManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.HttpUtil;
import org.apache.wiki.util.TextUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.permissions.PagePermission;
import org.elwiki.permissions.PermissionFactory;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * This is the chief JSPWiki attachment management servlet. It is used for both uploading new content and
 * downloading old content. It can handle most common cases, e.g. check for modifications and return 304's as
 * necessary.
 * <p>
 * Authentication is done using JSPWiki's normal AAA framework.
 * <p>
 * This servlet is also capable of managing dynamically created attachments.
 *
 *
 * @since 1.9.45.
 */
//@formatter:off
@Component(
  service=Servlet.class,
  property= {
  	HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/attach/*",
  	HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=("
  	+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=eclipse)"},
  scope=ServiceScope.PROTOTYPE,
  name="web.AttachmentServlet")
//@formatter:on
public class AttachmentServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(AttachmentServlet.class);

	private static final int BUFFER_SIZE = 8192;

	private static final long serialVersionUID = 3257282552187531320L;

	private static final String HDR_VERSION = "version";
	// private static final String HDR_NAME        = "page";

	/** Default expiry period is 1 day */
	protected static final long DEFAULT_EXPIRY = 1 * 24 * 60 * 60 * 1000;

	@Reference
	private Engine m_engine;

	@Reference
	private AuthorizationManager authorizationManager;

	@Reference
	private AttachmentManager attachmentManager;
	
	@Reference
	private ProgressManager progressManager;
	
	@Reference
	private PageManager pageManager;

	/** The maximum size that an attachment can be. */
	private int m_maxSize = Integer.MAX_VALUE;

	/** List of attachment types which are allowed */
	private String[] m_allowedPatterns;

	private String[] m_forbiddenPatterns;

	//
	// Not static as DateFormat objects are not thread safe.
	// Used to handle the RFC date format = Sat, 13 Apr 2002 13:23:01 GMT
	//
	//private final DateFormat rfcDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

	@Activate
	protected void startup() {
		log.debug("«web» start " + AttachmentServlet.class.getSimpleName());
	}

	/**
	 * Initializes the servlet from Engine properties.
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		IPreferenceStore props = m_engine.getWikiPreferences();
		String allowed = TextUtil.getStringProperty(props, AttachmentManager.PROP_ALLOWEDEXTENSIONS, null);
		m_maxSize = TextUtil.getIntegerProperty(props, AttachmentManager.PROP_MAXSIZE, Integer.MAX_VALUE);

		if (allowed != null && allowed.length() > 0) {
			m_allowedPatterns = allowed.toLowerCase().split("\\s");
		} else {
			m_allowedPatterns = new String[0];
		}

		String forbidden = TextUtil.getStringProperty(props, AttachmentManager.PROP_FORBIDDENEXTENSIONS, null);
		if (forbidden != null && forbidden.length() > 0) {
			m_forbiddenPatterns = forbidden.toLowerCase().split("\\s");
		} else {
			m_forbiddenPatterns = new String[0];
		}
	}

	private boolean isTypeAllowed(String name) {
		if (name == null || name.length() == 0)
			return false;

		name = name.toLowerCase();

		for (int i = 0; i < m_forbiddenPatterns.length; i++) {
			if (name.endsWith(m_forbiddenPatterns[i]) && m_forbiddenPatterns[i].length() > 0)
				return false;
		}

		for (int i = 0; i < m_allowedPatterns.length; i++) {
			if (name.endsWith(m_allowedPatterns[i]) && m_allowedPatterns[i].length() > 0)
				return true;
		}

		return m_allowedPatterns.length == 0;
	}

	/**
	 * Implements the OPTIONS method.
	 *
	 * @param req The servlet request
	 * @param res The servlet response
	 */

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse res) {
		res.setHeader("Allow", "GET, PUT, POST, OPTIONS, PROPFIND, PROPPATCH, MOVE, COPY, DELETE");
		res.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Serves a GET with two parameters: 'wikiname' specifying the wikiname of the attachment, 'version' specifying
	 * the version indicator.
	 *
	 */
	// FIXME: Messages would need to be localized somehow.
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		WikiContext context = Wiki.context().create(m_engine, req, WikiContext.ATTACHMENT_DOWNLOAD);
		String version = req.getParameter(HDR_VERSION);
		String nextPage = req.getParameter("nextpage");
		String attachmentName;//:FVK: = context.getPage().getName();
		attachmentName = req.getRequestURI().substring(8);
		int ver = WikiProvider.LATEST_VERSION;

		if (attachmentName == null) {
			log.info("Invalid attachment name.");
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try (OutputStream out = res.getOutputStream()) {
			log.debug("Attempting to download att " + attachmentName + ", version " + version);
			if (version != null) {
				ver = Short.parseShort(version);
			}

			AttachmentContent attContent = attachmentManager.getAttachmentContent( //
					context.getPage(), attachmentName, ver);
			if (attContent != null) {
				/* Check if the user has permissions for this attachment.
				 */
				Permission permission = PermissionFactory.getPagePermission(context.getPage(),
						PagePermission.VIEW_ACTION);
				if (!this.authorizationManager.checkPermission(context.getWikiSession(), permission)) {
					log.debug("User does not have permission for view attachment (PAGE VIEW action).");
					res.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				/* Check if the client already has a version of this attachment.
				 */
				/*:FVK: TODO: 
				if( HttpUtil.checkFor304( req, attachmentName, attContent.getCreationDate() ) ) {
				    log.debug( "Client has latest version already, sending 304..." );
				    res.sendError( HttpServletResponse.SC_NOT_MODIFIED );
				    return;
				}*/

				String fileName = attContent.getPageAttachment().getName();
				String mimetype = getMimeType(context, fileName);
				res.setContentType(mimetype);

				/* We use 'inline' instead of 'attachment' so that user agents
				 * can try to automatically open the file.
				 */
				res.addHeader("Content-Disposition", "inline; filename=\"" + fileName + "\";");
				res.addDateHeader("Last-Modified", attContent.getCreationDate().getTime());

				// If a size is provided by the provider, report it.
				if (attContent.getSize() >= 0) {
					res.setContentLength((int) attContent.getSize());
				}

				try (InputStream in = this.attachmentManager.getAttachmentStream(context, attContent)) {
					int read;
					byte[] buffer = new byte[BUFFER_SIZE];

					while ((read = in.read(buffer)) > -1) {
						out.write(buffer, 0, read);
					}
				}

				log.debug("Attachment " + fileName + " sent to " + req.getRemoteUser() + " on "
						+ HttpUtil.getRemoteAddress(req));

				if (nextPage != null) {
					res.sendRedirect(validateNextPage(TextUtil.urlEncodeUTF8(nextPage),
							m_engine.getURL(ContextEnum.WIKI_ERROR.getRequestContext(), "", null)));
				}

			} else {
				String msg = "Attachment '" + attachmentName + "', version " + ver + " does not exist.";
				log.info(msg);
				res.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
			}
		} catch (ProviderException pe) {
			log.debug("Provider failed while reading", pe);
			//
			//  This might fail, if the response is already committed.  So in that
			//  case we just log it.
			//
			sendError(res, "Provider error: " + pe.getMessage());
		} catch (NumberFormatException nfe) {
			log.warn("Invalid version number: " + version);
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid version number");
		} catch (SocketException se) {
			//
			//  These are very common in download situations due to aggressive
			//  clients.  No need to try and send an error.
			//
			log.debug("I/O exception during download", se);
		} catch (IOException ioe) {
			//
			//  Client dropped the connection or something else happened.
			//  We don't know where the error came from, so we'll at least
			//  try to send an error and catch it quietly if it doesn't quite work.
			//
			log.debug("I/O exception during download", ioe);
			sendError(res, "Error: " + ioe.getMessage());
		}
	}

	void sendError(HttpServletResponse res, String message) throws IOException {
		try {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		} catch (IllegalStateException e) {
			// ignore
		}
	}

	/**
	 * Returns the mime type for this particular file. Case does not matter.
	 *
	 * @param ctx      WikiContext; required to access the ServletContext of the request.
	 * @param fileName The name to check for.
	 * @return A valid mime type, or application/binary, if not recognized
	 */
	private static String getMimeType(WikiContext ctx, String fileName) {
		String mimetype = null;

		HttpServletRequest req = ctx.getHttpRequest();
		if (req != null) {
			ServletContext s = req.getSession().getServletContext();

			if (s != null) {
				mimetype = s.getMimeType(fileName.toLowerCase());
			}
		}

		if (mimetype == null) {
			mimetype = "application/binary";
		}

		return mimetype;
	}

	/**
	 * Grabs mime/multipart data and stores it into the temporary area. Uses other parameters to determine which
	 * name to store as.
	 *
	 * <p>
	 * The input to this servlet is generated by an HTML FORM with two parts. The first, named 'page', is the
	 * WikiName identifier for the parent file. The second, named 'content', is the binary content of the file.
	 *
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			String nextPage = upload(req);
			req.getSession().removeAttribute("msg");
			res.sendRedirect(nextPage);
		} catch (RedirectException e) {
			//TODO: remove from here accessing into ServicesRefs.
			Session session = ServicesRefs.getSessionMonitor().getWikiSession(req);
			session.addMessage(e.getMessage());

			req.getSession().setAttribute("msg", e.getMessage());
			res.sendRedirect(e.getRedirect());
		}
	}

	/**
	 * Validates the next page to be on the same server as this webapp. Fixes [JSPWIKI-46].
	 */
	private String validateNextPage(String nextPage, String errorPage) {
		if (nextPage.contains("://")) {
			// It's an absolute link, so unless it starts with our address, we'll log an error.
			if (!nextPage.startsWith(m_engine.getWikiConfiguration().getBaseURL())) {
				log.warn("Detected phishing attempt by redirecting to an unsecure location: " + nextPage);
				nextPage = errorPage;
			}
		}

		return nextPage;
	}

	/**
	 * Uploads a specific mime multipart input set, intercepts exceptions.
	 *
	 * @param req The servlet request
	 * @return The page to which we should go next.
	 * @throws RedirectException If there's an error and a redirection is needed
	 * @throws IOException       If upload fails
	 */
	protected String upload(HttpServletRequest req) throws RedirectException, IOException {
		String msg;
		String attName = "(unknown)";
		String errorPage = m_engine.getURL(ContextEnum.WIKI_ERROR.getRequestContext(), "", null); // If something bad happened, Upload should be able to take care of most stuff
		String nextPage = errorPage;
		String progressId = req.getParameter("progressid");

		// Check that we have a file upload request
		if (!ServletFileUpload.isMultipartContent(req)) {
			throw new RedirectException("Not a file upload", errorPage);
		}

		try {
			FileItemFactory factory = new DiskFileItemFactory();

			// Create the context _before_ Multipart operations, otherwise strict servlet containers may fail when setting encoding.
			WikiContext context = Wiki.context().create(m_engine, req, WikiContext.ATTACHMENT_DOWNLOAD);
			UploadListener pl = new UploadListener();

			this.progressManager.startProgress(pl, progressId);

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			if (!context.hasAdminPermissions()) {
				upload.setFileSizeMax(m_maxSize);
			}
			upload.setProgressListener(pl);
			List<FileItem> items = upload.parseRequest(req);

			WikiPage wikiPage = null;

			String changeNote = null;
			//FileItem actualFile = null;
			List<FileItem> fileItems = new ArrayList<>();

			for (FileItem item : items) {
				if (item.isFormField()) {
					String fieldName = item.getFieldName();
					switch (fieldName) {
					case "idpage":
						String pageId = item.getString("UTF-8");
						wikiPage = this.pageManager.getPageById(pageId);
						break;
					case "changenote":
						changeNote = item.getString("UTF-8");
						if (changeNote != null) {
							changeNote = TextUtil.replaceEntities(changeNote);
						}
						break;
					case "nextpage":
						nextPage = validateNextPage(item.getString("UTF-8"), errorPage);
						break;
					}
				} else {
					fileItems.add(item);
				}
			}

			if (fileItems.size() == 0) {
				throw new RedirectException("Broken file upload", errorPage);

			} else {
				for (FileItem actualFile : fileItems) {
					String filename = actualFile.getName();
					long fileSize = actualFile.getSize();
					try (InputStream in = actualFile.getInputStream()) {
						executeUpload(context, in, filename, nextPage, wikiPage, changeNote, fileSize);
					}
				}
			}

		} catch (ProviderException e) {
			msg = "Upload failed because the provider failed: " + e.getMessage();
			log.warn(msg + " (attachment: " + attName + ")", e);

			throw new IOException(msg);
		} catch (IOException e) {
			// Show the submit page again, but with a bit more intimidating output.
			msg = "Upload failure: " + e.getMessage();
			log.warn(msg + " (attachment: " + attName + ")", e);

			throw e;
		} catch (FileUploadException e) {
			// Show the submit page again, but with a bit more intimidating output.
			msg = "Upload failure: " + e.getMessage();
			log.warn(msg + " (attachment: " + attName + ")", e);

			throw new IOException(msg, e);
		} finally {
			this.progressManager.stopProgress(progressId);
			// FIXME: In case of exceptions should absolutely remove the uploaded file.
		}

		return nextPage;
	}

	/**
	 *
	 * @param context       the wiki context
	 * @param data          the input stream data
	 * @param filename      the name of the file to upload
	 * @param errorPage     the place to which you want to get a redirection
	 * @param parentPage    the page to which the file should be attached
	 * @param changenote    The change note
	 * @param contentLength The content length
	 * @throws RedirectException If the content needs to be redirected
	 * @throws IOException       If there is a problem in the upload.
	 * @throws ProviderException If there is a problem in the backend.
	 */
	protected void executeUpload(WikiContext context, InputStream data, String filename, String errorPage,
			WikiPage parentPage, String changenote, long contentLength)
			throws RedirectException, IOException, ProviderException {
		try {
			filename = AttachmentManager.validateFileName(filename);
		} catch (WikiException e) {
			// this is a kludge, the exception that is caught here contains the i18n key
			// here we have the context available, so we can internationalize it properly :
			throw new RedirectException(
					Preferences.getBundle(context, InternationalizationManager.CORE_BUNDLE).getString(e.getMessage()),
					errorPage);
		}

		//
		//  FIXME: This has the unfortunate side effect that it will receive the
		//  contents.  But we can't figure out the page to redirect to
		//  before we receive the file, due to the stupid constructor of MultipartRequest.
		//

		if (!context.hasAdminPermissions()) {
			if (contentLength > m_maxSize) {
				// FIXME: Does not delete the received files.
				throw new RedirectException("File exceeds maximum size (" + m_maxSize + " bytes)", errorPage);
			}

			if (!isTypeAllowed(filename)) {
				throw new RedirectException("Files of this type may not be uploaded to this wiki", errorPage);
			}
		}

		Principal user = context.getCurrentUser();

		log.debug("file=" + filename);

		if (data == null) {
			log.error("File could not be opened.");
			throw new RedirectException("File could not be opened.", errorPage);
		}

		/* Check if we're allowed to do this?
		 */
		Permission permission = PermissionFactory.getPagePermission(parentPage, PagePermission.UPLOAD_ACTION);
		if (this.authorizationManager.checkPermission(context.getWikiSession(), permission)) {
			AttachmentContent attContent = Elwiki_dataFactory.eINSTANCE.createAttachmentContent();
			attContent.setCreationDate(new Date());
			attContent.setSize(contentLength);
			if (user != null) {
				attContent.setAuthor(user.getName());
			}

			if (changenote != null && changenote.length() > 0) {
				attContent.setChangeNote(changenote);
			}

			attachmentManager.storeAttachment(parentPage, attContent, filename, data);

			log.info("User " + user + " uploaded attachment to " + parentPage + " called " + filename + ", size "
					+ contentLength);
		} else {
			throw new RedirectException("No permission to upload a file", errorPage);
		}
	}

	/**
	 * Provides tracking for upload progress.
	 *
	 */
	private static class UploadListener extends ProgressItem implements ProgressListener {
		public long m_currentBytes;
		public long m_totalBytes;

		@Override
		public void update(long recvdBytes, long totalBytes, int item) {
			m_currentBytes = recvdBytes;
			m_totalBytes = totalBytes;
		}

		@Override
		public int getProgress() {
			return (int) (((float) m_currentBytes / m_totalBytes) * 100 + 0.5);
		}
	}

}
