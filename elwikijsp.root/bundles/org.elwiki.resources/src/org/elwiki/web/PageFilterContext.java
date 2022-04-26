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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

@Deprecated
public class PageFilterContext extends HttpFilter {

	private static final long serialVersionUID = -4683126485266937165L;
	private static final Logger log = Logger.getLogger(PageFilterContext.class);

	private IWikiConfiguration wikiConfiguration;

	private Engine engine;

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
		Map.entry("/editGroup.cmd", ContextEnum.GROUP_EDIT),
		Map.entry("/viewGroup.cmd", ContextEnum.GROUP_VIEW)
	); //@formatter:on

	/**
	 * Creates instanse of PageFilterContext.
	 */
	public PageFilterContext() {
		super();
	}

	protected Engine getEngine() {
		if (this.engine == null) {
			if (null == (this.engine = ResourcesActivator.getService(Engine.class))) {
				//TODO: handle the crash - there is no Engine service.
				throw new NullPointerException("missed Engine service.");
			} // TODO: else - error: missed service.
			this.wikiConfiguration = this.engine.getWikiConfiguration();
		}

		return this.engine;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doFilter(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.debug("doFilter() " + httpRequest.getRequestURI());
		Engine servicesRef = getEngine();

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
		httpRequest.setAttribute(Context.ATTR_WIKI_CONTEXT, wikiContext);

		log.debug("context:  " + ((wikiContext != null) ? wikiContext.getName() : "NULL"));

		response.setContentType("text/html; charset=" + servicesRef.getContentEncoding());

		super.doFilter(httpRequest, response, chain);
	}

}
