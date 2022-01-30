package org.elwiki.web;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.elwiki.resources.ResourcesActivator;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

public class PageServletContextHelper implements HttpContext {

	private static final Logger log = Logger.getLogger(PageServletContextHelper.class);

	HttpContext defaultContext;

	public PageServletContextHelper() {
		HttpService httpService = ResourcesActivator.getService(HttpService.class);
		this.defaultContext = httpService.createDefaultHttpContext();
	}

	@Override
	public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return defaultContext.handleSecurity(request, response);
	}

	@Override
	public URL getResource(String name) {
		System.err.println("PageServletContextHelper: ");
		System.out.println(" (1) before: " + name);
		switch (name) {
		case "/wiki/commonheader.jsp":
			name = "/page/commonheader1.jsp";
			Assert.isTrue(false, "Not implemented"); 
			break;
		case "/t#k/localheader1.jsp": //TODO: :FVK: это не требуется более.
			name = "/page/localheader.jsp";
			break;
		default:
			if (name.startsWith("/t#k/")) {
				/* замена '/t#k/' на '/page/templates/default/' */
				name = name.replaceAll("/t#k/", "/page/templates/default/");
			} else { //:FVK: "/do/" - требуется для обращения к getRequestDispatcher(path).
				name = name.replaceAll("/do/", "/page/");
			}
			
			if(name.endsWith(".cmd")) {
				name = name.replaceAll(".cmd$", ".jsp");
			}
		}
		System.out.println(" (2)  after: " + name);
		URL url = defaultContext.getResource(name);
		System.out.println(" (3) url: " + url);
		return url;
	}

	@Override
	public String getMimeType(String name) {
		return defaultContext.getMimeType(name);
	}

}
