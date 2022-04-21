package org.elwiki.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.elwiki.services.ServicesRefs;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

//@formatter:off
//@Component(
//		property= {
//	    	"osgi.http.whiteboard.filter.pattern=/*.cmd",
//	        "osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=org.elwiki.resources.httpcontext)"
//	    },
//	    scope=ServiceScope.PROTOTYPE,
//	    name = "web.PageFilterAuthentication",
//	    immediate = true
//)
//@formatter:on
@Deprecated
public class PageFilterAuthentication extends HttpFilter {

	private static final long serialVersionUID = 3385815640489532788L;
	private static final Logger log = Logger.getLogger(PageFilterAuthentication.class);

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doFilter(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		log.debug("doFilter()");
		String uri = httpRequest.getRequestURI();
		System.out.println("PageFilterAuthentication, URI: " + uri);

		/*TODO: :FVK: для Wiki.jsp, т.е. view.cmd
		if (false == ServicesRefs.getAuthorizationManager().hasAccess(ServicesRefs.getCurrentContext(), response)) {
			return;
		}
		*/

		super.doFilter(httpRequest, response, chain);
	}

}
