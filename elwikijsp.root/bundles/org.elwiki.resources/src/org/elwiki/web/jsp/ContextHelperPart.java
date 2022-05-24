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
//:FVK		System.err.println("PageServletContextHelper: ");
//:FVK		System.out.println(" (1) before: " + name);
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
			
			if(name.endsWith(".cmd")) { //:FVK: workaround.
				name = name.replaceAll(".cmd$", ".jsp");
			}
		}
//:FVK		System.out.println(" (2)  after: " + name);
		URL url = defaultContext.getResource(name);
//:FVK		System.out.println(" (3) url: " + url);
		return url;
	}

	@Override
	public String getMimeType(String name) {
		return defaultContext.getMimeType(name);
	}

}
