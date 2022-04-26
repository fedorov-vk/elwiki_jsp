package org.elwiki.web.jsp;

import javax.servlet.Servlet;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

//@formatter:off
@Component(
    service=Servlet.class,
    property= {
    	"osgi.http.whiteboard.servlet.pattern=/*",
    	HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=("
    	+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=eclipse)"},
    scope=ServiceScope.PROTOTYPE,
    name="partJspServlet")
//@formatter:on
public class JspServletPart extends ServletWrapper {

	private static final Logger log = Logger.getLogger(JspServletPart.class);
	private static String ALIAS = "/";

	@Activate
	protected void startup() {
		log.debug("startup JspServletPart.");
	}
	
	@Override
	protected String getAlias() {
		return ALIAS;
	}

}
