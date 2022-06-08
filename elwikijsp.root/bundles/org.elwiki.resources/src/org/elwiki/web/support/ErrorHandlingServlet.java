package org.elwiki.web.support;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

//@formatter:off
@Component(
	service=Servlet.class,
	property= {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE + "=java.io.IOException",
		HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE + "=500"},
	scope=ServiceScope.PROTOTYPE,
	name="partErrorHandlingServlet")
//@formatter:on
public class ErrorHandlingServlet extends HttpServlet {

	private static final long serialVersionUID = 2969542914627506516L;
	private static final Logger log = Logger.getLogger(ErrorHandlingServlet.class);

	@Activate
	protected void startup() {
		log.debug("startup ErrorHandlingServlet.");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		resp.getWriter().write("<html><body>You need to provide an input!</body></html>");
	}

}
