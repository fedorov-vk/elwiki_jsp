package org.elwiki.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.api.core.Engine;

public class PageServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = -1850801757913315622L;

	private Engine engine;
	private ServletContext context;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		final ServletContext context = config.getServletContext();
		this.context = context;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// :FVK: final HttpServletResponseWrapper responseWrapper = new JSPWikiServletResponseWrapper( (
		// HttpServletResponse )response, m_wiki_encoding, useEncoding );

		String path = "/page/MyInsert.jsp";
		ServletContext servletContext = this.context;
		RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(path);
		requestDispatcher.include(request, response);
	}

}
