package org.elwiki.web.jsp;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.context.ServletContextHelper;

//@formatter:off
@Component(
	service = ServletContextHelper.class,
	scope = ServiceScope.BUNDLE, property = {
		"osgi.http.whiteboard.context.name=eclipse",
		"osgi.http.whiteboard.context.path=/"},
	name = "partContextHelper")
//@formatter:on
public class ContextHelperPart extends ServletContextHelper implements HttpContext {

	private static final Logger log = Logger.getLogger(ContextHelperPart.class);

	@Reference
	HttpService httpService;
	HttpContext defaultContext;

	@Activate
	protected void startup() {
		this.defaultContext = httpService.createDefaultHttpContext();
	}

	@Override
	public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return defaultContext.handleSecurity(request, response);
	}

	@Override
	public URL getResource(String name) {
		//:FVK: System.out.println("---- PageServletContextHelper: ----");
		URL url = defaultContext.getResource(name);
		//:FVK: System.out.println("---- url: " + url);
		return url;
	}

	@Override
	public String getMimeType(String name) {
		return defaultContext.getMimeType(name);
	}

}
