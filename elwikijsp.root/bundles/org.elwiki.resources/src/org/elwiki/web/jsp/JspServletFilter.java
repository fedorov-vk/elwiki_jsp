package org.elwiki.web.jsp;

import java.io.IOException;

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
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.util.ThreadUtil;
import org.eclipse.core.runtime.Platform;
import org.elwiki.configuration.IWikiPreferences;
import org.elwiki.internal.CmdCode;
import org.elwiki.web.support.ErrorHandlingServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.framework.Constants;

//@formatter:off
/**
 * @author v.fedorov
 *
 */
@Component(
	property= {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN + "=/*",
		HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=("
		+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=eclipse)",
		Constants.SERVICE_RANKING + "=100"},
    scope=ServiceScope.PROTOTYPE,
    name = "web.JspServletFilter"
)
//@formatter:on
public class JspServletFilter extends HttpFilter implements Filter {

	private static final long serialVersionUID = 5461829698518145349L;
	private static final Logger log = Logger.getLogger(JspServletFilter.class);

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
		log.debug("«web» start " + JspServletFilter.class.getSimpleName());
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
		try {
			httpRequest.setAttribute(ErrorHandlingServlet.ATTR_ELWIKI_ERROR_EXCEPTION, null);
			
			// :FVK: из кода JSPwiki, класс WikiJSPFilter: ... : final WatchDog w =
			// WatchDog.getCurrentWatchDog( m_engine );
			boolean isAjaxPage = false;

			String uri = httpRequest.getRequestURI();
			log.debug("◄►doFilter◄► " + uri);
			if (uri.startsWith("/cmd.")) {
				// catch URI "/cmd.*"
				log.debug("Request URI starts with '/cmd.'");
			} else if (uri.matches(".+?AJAX.+?$")) {//:FVK: - workaround, using the specified part of the name.
				// catch URI "/*AJAX*"
				log.debug("Request URI is matched '.+?AJAX.+?$'.");
				isAjaxPage = true;
			} else {
				// skip all other URI. 
				log.debug("Request URI isn't matched anything.");
				super.doFilter(httpRequest, response, chain);
				return;
			}

			/* Create Wiki context.
			 */
			ContextEnum cmdContext;
			Context wikiContext;
			cmdContext = Platform.getAdapterManager().getAdapter(uri.substring(1), ContextEnum.class); // without first '/'.
			wikiContext = Wiki.context().create(engine, httpRequest, cmdContext.getRequestContext());
			httpRequest.setAttribute(Context.ATTR_WIKI_CONTEXT, wikiContext);
			ThreadUtil.setCurrentRequest(httpRequest);

			log.debug("context:  " + ((wikiContext != null) ? wikiContext.getName() : "NULL"));

			response.setContentType("text/html; charset=" + engine.getContentEncoding());

			/* Authorize. (following code from prolog of JSPwiki JSP pages)
			 */
			/*TODO: :FVK: для Wiki.jsp, т.е. cmd.view
			if (false == ServicesRefs.getAuthorizationManager().hasAccess(ThreadUtil.getCurrentRequest(), response)) {
				return;
			}
			*/

			/* Make content of page.
			 */
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
					cmdCode.applyPrologue(httpRequest, response);
				}

				if (!isAjaxPage) {
					String template = wikiContext.getTemplate();
					switch (template) {
					case "reader":
						//:FVK: - workaround. view only page, for skin=reader
						httpRequest.getRequestDispatcher(PATH_READER_VIEW).include(httpRequest, response);
						break;
					case "raw":
						//:FVK: - workaround. view only page, for skin=raw
						httpRequest.getRequestDispatcher(PATH_RAW_VIEW).include(httpRequest, response);
						break;
					default:
						// chain.doFilter(request, response);
						httpRequest.getRequestDispatcher(PATH_PAGE_VIEW).include(httpRequest, response);
						break;
					}
				} else {
					httpRequest.getRequestDispatcher(uri).include(httpRequest, response);
				}

				/* TODO: check JSP error.
				if (httpRequest.getAttribute(ErrorHandlingServlet.ATTR_ELWIKI_ERROR_EXCEPTION) != null) {
					return;
				}*/

				try {
					// :FVK: w.enterState( "Delivering response", 30 );

					// String r = filtering(wikiContext, responseWrapper); ... -- deprecated.

					// Clean up the UI messages and loggers
					wikiContext.getWikiSession().clearMessages();

					// fire PAGE_DELIVERED event
					// :FVK: fireEvent( WikiPageEvent.PAGE_DELIVERED, pagename );
				} finally {
					// :FVK: w.exitState();
				}
			} finally {
				if (cmdCode != null) {
					// Code for context`s command: execute epilogue.
					cmdCode.applyEpilogue();
				}

				ThreadUtil.removeCurrentRequest();
			}
		} catch (Exception ex) {
			log.error("!!! ElWiki internal error.", ex);
			httpRequest.setAttribute(ErrorHandlingServlet.ATTR_ELWIKI_ERROR_EXCEPTION, ex);
			throw new ServletException("ElWiki internal error.", ex);
		}
	}

}
