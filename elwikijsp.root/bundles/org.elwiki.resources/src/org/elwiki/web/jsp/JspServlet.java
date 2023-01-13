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
    	HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/*",
    	HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=("
    	+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=eclipse)"},
    scope=ServiceScope.PROTOTYPE,
    name="web.JspServlet")
//@formatter:on
public class JspServlet extends JspServletWrapper {

	private static final Logger log = Logger.getLogger(JspServlet.class);
	private static String ALIAS = "/";

	@Activate
	protected void startup() {
		log.debug("«web» start " + JspServlet.class.getSimpleName());
	}

	@Override
	protected String getAlias() {
		return ALIAS;
	}

}
