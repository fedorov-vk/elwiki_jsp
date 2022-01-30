package org.elwiki.web;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.WikiContext;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.eclipse.core.internal.preferences.ImmutableMap;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.resources.ResourcesActivator;
import org.elwiki.services.ServicesRefs;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class PageFilterContext extends HttpFilter {

	private static final long serialVersionUID = -4683126485266937165L;
	private static final Logger log = Logger.getLogger(PageFilterContext.class);

	private IWikiConfiguration wikiConfiguration;
	private Engine servicesRef;

	//@formatter:off
	private Map<String, ContextEnum> cmd2context = Map.ofEntries(
		Map.entry("/view.cmd", ContextEnum.PAGE_VIEW),
		Map.entry("/login.cmd", ContextEnum.WIKI_LOGIN),
		Map.entry("/logout.cmd", ContextEnum.WIKI_LOGOUT),
		Map.entry("/edit.cmd", ContextEnum.PAGE_EDIT),
		Map.entry("/prefs.cmd", ContextEnum.WIKI_PREFS),
		Map.entry("/info.cmd", ContextEnum.PAGE_INFO),
		Map.entry("/rename.cmd", ContextEnum.PAGE_RENAME),
		Map.entry("/diff.cmd", ContextEnum.PAGE_DIFF),
		Map.entry("/upload.cmd", ContextEnum.PAGE_DIFF),
		Map.entry("/editGroup.cmd", ContextEnum.GROUP_EDIT)
	); //@formatter:on

	/**
	 * Creates instanse of PageFilterContext.
	 */
	public PageFilterContext() {
		super();
		if (null != (this.servicesRef = ResourcesActivator.getService(Engine.class))) {
			this.wikiConfiguration = servicesRef.getWikiConfiguration();
		} // TODO: else - error: missed service.
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doFilter(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.debug("doFilter() " + httpRequest.getRequestURI());

		// workaround - skip all, except *.cmd, *.jsp
		String uri = httpRequest.getRequestURI();
		if (false == uri.matches(".+?(\\.cmd|\\.jsp)$")) { // :FVK: uri.matches(".+?(\\.js|\\.css)$")
			System.err.println("PFC original resource: " + uri);
			super.doFilter(httpRequest, response, chain);
		}
		System.out.println("PageFilterContext, URI: " + uri);

		ContextEnum context = (cmd2context.containsKey(uri)) ? cmd2context.get(uri) : ContextEnum.PAGE_VIEW;
		Context wikiContext = new WikiContext(servicesRef, httpRequest, context.getRequestContext());
		ServicesRefs.setCurrentContext(wikiContext);
		httpRequest.setAttribute(Context.ATTR_CONTEXT, wikiContext);

		log.debug("context:  " + ((wikiContext != null) ? wikiContext.getName() : "NULL"));

		response.setContentType("text/html; charset=" + this.servicesRef.getContentEncoding());

		super.doFilter(httpRequest, response, chain);
	}

}
