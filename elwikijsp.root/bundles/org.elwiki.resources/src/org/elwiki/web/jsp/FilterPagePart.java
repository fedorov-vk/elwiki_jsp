package org.elwiki.web.jsp;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.util.TextUtil;
import org.eclipse.core.runtime.Platform;
import org.elwiki.configuration.IWikiPreferences;
import org.elwiki.internal.CmdCode;
import org.elwiki.services.ServicesRefs;
import org.elwiki.web.ElwikiServletResponseWrapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

//@formatter:off
/**
 * @author v.fedorov
 *
 */
@Component(
	property= {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN + "=/*",
		HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=("
		+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=eclipse)"},
    scope=ServiceScope.PROTOTYPE,
    name = "part00.FilterPage"
)
//@formatter:on
public class FilterPagePart extends HttpFilter implements Filter {

	private static final long serialVersionUID = 5461829698518145349L;
	private static final Logger log = Logger.getLogger(FilterPagePart.class);

	private static final String PATH_PAGE_VIEW = "/templates/PageViewTemplate.jsp";
	private static final String PATH_READER_VIEW = "/templates/reader/ViewTemplate.jsp";
	private static final String PATH_RAW_VIEW = "/templates/raw/ViewTemplate.jsp";

	@Reference
	private Engine engine;
	private ServletContext context;
	private String m_wiki_encoding;
	private boolean useEncoding;

	@Activate
	protected void startup() {
		log.debug("startup FilterPagePart.");
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.context = config.getServletContext();
		this.m_wiki_encoding = this.engine.getWikiPreferences().getString(IWikiPreferences.PROP_ENCODING);
		this.useEncoding = !TextUtil.getBooleanProperty(this.engine.getWikiPreferences(),
				Engine.PROP_NO_FILTER_ENCODING, false);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doFilter(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// :FVK: из кода JSPwiki, класс WikiJSPFilter: ... : final WatchDog w =
		// WatchDog.getCurrentWatchDog( m_engine );
		boolean isAjaxPage = false; //:FVK: workaround flag, for avoid adding page layout for AJAX page.

		// skip all, except cmd.*, AJAX.
		String uri = httpRequest.getRequestURI();
		log.debug("doFilter()\n requested URI: " + httpRequest.getRequestURI());
		if (uri.startsWith("/cmd.")) {
			log.debug("Request URI starts with '/cmd.'");
		} else if (uri.matches(".+?AJAX.+?$")) {//:FVK: - workaround, using the specified part of the name.
			log.debug("Request URI is matched '.+?AJAX.+?$'.");
			isAjaxPage = true;
		} else {
			log.debug("Request URI isn't matched anything.");
			// System.err.println("PFC original resource: " + uri);
			try {
				super.doFilter(httpRequest, response, chain);
			} catch (IOException e) {
				log.error("Error 1", e); //TODO: define message.
			} catch (ServletException e) {
				log.error("Error 2", e); //TODO: define message.
			} catch (Exception e) {
				log.error("Error 3", e); //TODO: define message.
			}
		}

		/* Create Wiki context.
		 */
		ContextEnum cmdContext;
		Context wikiContext;
		cmdContext = Platform.getAdapterManager().getAdapter(uri.substring(1), ContextEnum.class); // without first '/'.
		wikiContext = Wiki.context().create(engine, httpRequest, cmdContext.getRequestContext());
		ServicesRefs.setCurrentContext(wikiContext);
		httpRequest.setAttribute(Context.ATTR_WIKI_CONTEXT, wikiContext);

		log.debug("context:  " + ((wikiContext != null) ? wikiContext.getName() : "NULL"));

		response.setContentType("text/html; charset=" + engine.getContentEncoding());

		/* Authorize.
		 */
		/*TODO: :FVK: для Wiki.jsp, т.е. cmd.view
		if (false == ServicesRefs.getAuthorizationManager().hasAccess(ServicesRefs.getCurrentContext(), response)) {
			return;
		}
		*/

		/* Make content of page.
		 */
		ElwikiServletResponseWrapper responseWrapper = //
				new ElwikiServletResponseWrapper(response, m_wiki_encoding, useEncoding);

		CmdCode cmdCode = null;
		try {
			{
				HttpSession session = httpRequest.getSession();

				log.debug("HttpSession ID: " + session.getId());
				log.debug("    RequestURI: " + httpRequest.getRequestURI());
			}

			cmdCode = Platform.getAdapterManager().getAdapter(wikiContext.getContextCmd(), CmdCode.class);
			if (cmdCode != null) {
				// Code for context`s command: execute prolog.
				try {
					cmdCode.applyPrologue(httpRequest, responseWrapper);
				} catch (Exception e) {
					// TODO: Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!isAjaxPage) {
				String template = wikiContext.getTemplate();
				switch (template) {
				case "reader":
					//:FVK: - workaround. view only page, for skin=reader
					httpRequest.getRequestDispatcher(PATH_READER_VIEW).include(httpRequest, responseWrapper);
					break;
				case "raw":
					//:FVK: - workaround. view only page, for skin=raw
					httpRequest.getRequestDispatcher(PATH_RAW_VIEW).include(httpRequest, responseWrapper);
					break;
				default:
					// chain.doFilter(request, response);
					httpRequest.getRequestDispatcher(PATH_PAGE_VIEW).include(httpRequest, responseWrapper);
					break;
				}
			} else {
				httpRequest.getRequestDispatcher(uri).include(httpRequest, responseWrapper);
			}

			try {
				// :FVK: w.enterState( "Delivering response", 30 );
				/*
				String r = filter(wikiContext, responseWrapper);

				if (!isAjaxPage) {// :FVK: workaround. - avoid: java.lang.IllegalStateException: WRITER
					if (useEncoding) {
						final OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(),
								response.getCharacterEncoding());
						out.write(r);
						out.flush();
						out.close();
					} else {
						response.getWriter().write(r);
					}
				}
				*/

				// Clean up the UI messages and loggers
				wikiContext.getWikiSession().clearMessages();

				// fire PAGE_DELIVERED event
				// :FVK: fireEvent( WikiPageEvent.PAGE_DELIVERED, pagename );

			} catch (Exception ex) {
				log.debug("internal fail", ex);
			} finally {
				// :FVK: w.exitState();
			}
		} finally {
			if (cmdCode != null) {
				// Code for context`s command: execute epilogue.
				try {
					cmdCode.applyEpilogue();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			ServicesRefs.removeCurrentContext();
		}
	}

	/**
	 * Goes through all types and writes the appropriate response.
	 *
	 * @param wikiContext The usual processing context
	 * @param response    The source string
	 * @return The modified string with all the insertions in place.
	 * @throws IOException
	 */
	private String filter(final Context wikiContext, ElwikiServletResponseWrapper response) throws IOException {
		String string = response.getHtmlContent();

		if (wikiContext != null) {
			final String[] resourceTypes = TemplateManager.getResourceTypes(wikiContext);
			for (final String resourceType : resourceTypes) {
				string = insertResources(wikiContext, string, resourceType);
			}

			// Add HTTP header Resource Requests
			final String[] headers = TemplateManager.getResourceRequests(wikiContext,
					TemplateManager.RESOURCE_HTTPHEADER);

			for (final String header : headers) {
				String key = header;
				String value = "";
				final int split = header.indexOf(':');
				if (split > 0 && split < header.length() - 1) {
					key = header.substring(0, split);
					value = header.substring(split + 1);
				}

				response.addHeader(key.trim(), value.trim());
			}
		}

		return string;
	}

	/**
	 * Inserts whatever resources were requested by any plugins or other components
	 * for this particular type.
	 *
	 * @param wikiContext The usual processing context
	 * @param string      The source string
	 * @param type        Type identifier for insertion
	 * @return The filtered string.
	 */
	private String insertResources(final Context wikiContext, final String string, final String type) {
		if (wikiContext == null) {
			return string;
		}

		final String marker = TemplateManager.getMarker(wikiContext, type);
		final int idx = string.indexOf(marker);
		if (idx == -1) {
			return string;
		}

		log.debug("...Inserting...");

		final String[] resources = TemplateManager.getResourceRequests(wikiContext, type);
		final StringBuilder concat = new StringBuilder(resources.length * 40);

		for (final String resource : resources) {
			log.debug("...:::" + resource);
			concat.append(resource);
		}

		return TextUtil.replaceString(string, idx, idx + marker.length(), concat.toString());
	}

}
