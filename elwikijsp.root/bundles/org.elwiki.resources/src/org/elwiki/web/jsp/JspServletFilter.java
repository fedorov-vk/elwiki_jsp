package org.elwiki.web.jsp;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
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
import org.apache.wiki.api.ui.AllCommands;
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

	private static final String PATH_PAGE_VIEW = "/shapes/PageViewTemplate.jsp";
	private static final String PATH_READER_VIEW = "/shapes/reader/ViewTemplate.jsp";
	private static final String PATH_RAW_VIEW = "/shapes/raw/ViewTemplate.jsp";
	private static final String PATH_ADMIN_VIEW = "/shapes/admin/Admin.jsp";
	private static final String PATH_SECURITY_VIEW = "/shapes/security/SecurityConfig.jsp";

	@Reference
	private Engine engine;

	private ServletContext servletContext;
	private String m_wiki_encoding;
	private boolean useEncoding;

	@Activate
	protected void startup() {
		log.debug("«web» start " + JspServletFilter.class.getSimpleName());
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.servletContext = config.getServletContext();
		this.m_wiki_encoding = this.engine.getWikiPreferences().getString(IWikiPreferences.PROP_ENCODING);
		this.useEncoding = !TextUtil.getBooleanProperty(this.engine.getWikiPreferences(),
				Engine.PROP_NO_FILTER_ENCODING, false);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * Get WikiContext according given URI. If URI is not determined as known - then returns
	 * default, ContextEnum.PAGE_VIEW.
	 * 
	 * @param adaptableUri
	 * @return Wiki context.
	 */
	private ContextEnum getContextEnum(String adaptableUri) {
		return Arrays.stream(ContextEnum.values()).filter(item -> item.getUri().equals(adaptableUri)) //
				.findFirst().orElse(ContextEnum.PAGE_VIEW);
	}
	
	@Override
	protected void doFilter(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain)
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
				super.doFilter(httpRequest, httpResponse, chain);
				return;
			}

			/* Create Wiki context according to required URI.
			 */
			ContextEnum contextEnum = getContextEnum(uri.substring(1)); // URI without first symbol '/'.
			Context wikiContext = Wiki.context().create(engine, httpRequest, contextEnum.getRequestContext());
			httpRequest.setAttribute(Context.ATTR_WIKI_CONTEXT, wikiContext);
			ThreadUtil.setCurrentRequest(httpRequest);

			log.debug("context:  " + ((wikiContext != null) ? wikiContext.getName() : "NULL"));

			httpResponse.setContentType("text/html; charset=" + engine.getContentEncoding());

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
					cmdCode.applyPrologue(httpRequest, httpResponse);
				}

				RequestDispatcher rqDispatcher = null;
				if (!isAjaxPage) {
					String shape = wikiContext.getShape();
					rqDispatcher = switch (shape) {
					case "reader" -> httpRequest.getRequestDispatcher(PATH_READER_VIEW);
					case "raw" -> httpRequest.getRequestDispatcher(PATH_RAW_VIEW);
					case "admin" -> httpRequest.getRequestDispatcher(PATH_ADMIN_VIEW);
					case "security" -> httpRequest.getRequestDispatcher(PATH_SECURITY_VIEW);
					default -> httpRequest.getRequestDispatcher(PATH_PAGE_VIEW);
					};
				} else {
					rqDispatcher = httpRequest.getRequestDispatcher(uri);
				}
				rqDispatcher.include(httpRequest, httpResponse);

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
