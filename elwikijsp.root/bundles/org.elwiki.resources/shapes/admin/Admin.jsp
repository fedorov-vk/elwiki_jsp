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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="/shapes/Error.jsp" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.apache.wiki.ui.admin.*" %>
<%@ page import="org.apache.wiki.ui.admin0.*" %>
<%@ page import="org.apache.wiki.ui.TemplateManager" %>
<%@ page import="org.apache.wiki.util.TextUtil" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.commons.lang3.time.StopWatch" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<fmt:setLocale value="${prefs.Language}" />
<%!
  public void jspInit()
  {
	wiki = ServicesRefs.Instance; //:FVK: workaround.
  }
  Logger log = Logger.getLogger("JSPWiki");
  Engine wiki;
%>
<%
	String bean = request.getParameter("bean");
    Engine wiki = ServicesRefs.Instance;//:FVK: Wiki.engine().find( getServletConfig() );
    // Create wiki context and check for authorization
    Context wikiContext = Wiki.context().create( wiki, request, Context.WIKI_ADMIN );

    //:FVK: надо вернуть код -- if(!ServicesRefs.getAuthorizationManager().hasAccess( wikiContext, response ) ) return;

    // Set the content type and include the response content
    response.setContentType("text/html; charset="+wiki.getContentEncoding() );

    pageContext.setAttribute( "engine", wiki, PageContext.REQUEST_SCOPE );
    pageContext.setAttribute( "context", wikiContext, PageContext.REQUEST_SCOPE );

    if( request.getMethod().equalsIgnoreCase("post") && bean != null ) {
    	AdminBean ab = ServicesRefs.getAdminBeanManager().findBean( bean );

        if( ab != null ) {
            ab.doPost( wikiContext );
        } else {
            wikiContext.getWikiSession().addMessage( "No such bean "+bean+" was found!" );
        }
    }

    //:FVK: Ð°ÑÐ³ÑÐ¼ÐµÐ½Ñ Ð´Ð»Ñ <wiki:Include> -- String contentPage = ServicesRefs.getTemplateManager().findJSP( pageContext, wikiContext.getShape(), "admin/AdminTemplate.jsp" );
%>
<wiki:Include page="AdminTemplate.jsp" />
