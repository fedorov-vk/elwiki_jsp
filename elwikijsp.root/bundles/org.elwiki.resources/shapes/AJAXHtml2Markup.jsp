<%--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
--%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.apache.wiki.htmltowiki.HtmlStringToWikiTranslator" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%!
	Logger log;
	public void jspInit()
	{
		log = Logger.getLogger("XHRHtml2Markup_jsp");
	}
%>
<%
	WikiContext wikiContext = ContextUtil.findContext( pageContext );
	Engine engine = wikiContext.getEngine();
	if( wikiContext == null )//:FVK: impossible. how? but the engine already readed from it.
	{
		// Copied from a top-level jsp -- which would be a better place to put this
		wikiContext = Wiki.context().create(engine, request, WikiContext.PAGE_VIEW);
		request.setAttribute(WikiContext.ATTR_WIKI_CONTEXT, wikiContext);
	}
	AuthorizationManager authorizationManager = engine.getManager(AuthorizationManager.class);
	if( !authorizationManager.hasAccess(wikiContext, response) ) {
		return;
	}

	response.setContentType("text/html; charset=" + engine.getContentEncoding());
	//response.setHeader( "Cache-control", "max-age=0" );
	//response.setDateHeader( "Expires", new Date().getTime() );
	//response.setDateHeader( "Last-Modified", new Date().getTime() );

	String htmlText = request.getParameter("htmlPageText");

	if( htmlText != null )
	{
%>
<%= new HtmlStringToWikiTranslator().translate(htmlText, wikiContext) %>
<%
	}
%>
