package org.elwiki.web.jsp;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

//@formatter:off
@Component(
	property = {
		"osgi.http.whiteboard.filter.pattern=/*",
		"osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=eclipse)"},
	scope=ServiceScope.PROTOTYPE,
	name = "part02.FilterAuthentication"
)
//@formatter:on
public class FilterAuthenticationPart extends HttpFilter implements Filter {

	private static final long serialVersionUID = 3385815640489532788L;
	private static final Logger log = Logger.getLogger(FilterAuthenticationPart.class);

	@Activate
	protected void startup() {
		log.debug("startup FilterAuthenticationPart.");
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doFilter(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
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
