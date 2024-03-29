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

<%@ page import="java.util.Date" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.apache.wiki.filters0.*" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.api.ui.EditorManager" %>
<%@ page import="org.apache.wiki.ui.TemplateManager" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%!
    Logger log = Logger.getLogger("JSPWiki");
%>

<%
Engine wiki = Wiki.engine().find( getServletConfig() );
    // Create wiki context and check for authorization
    WikiContext wikiContext = Wiki.context().create( wiki, request, ContextEnum.PAGE_PREVIEW.getRequestContext() );
    if( !WikiEngine.getAuthorizationManager().hasAccess( wikiContext, response ) ) return;
    if( wikiContext.getCommand().getTarget() == null ) {
        response.sendRedirect( wikiContext.getURL( wikiContext.getRequestContext(), wikiContext.getName() ) );
        return;
    }
    String pagereq = wikiContext.getName();

    pageContext.setAttribute( EditorManager.ATTR_EDITEDTEXT, session.getAttribute( EditorManager.REQ_EDITEDTEXT ), PageContext.REQUEST_SCOPE );

    String lastchange = SpamFilter.getSpamHash( wikiContext.getPage(), request );

    pageContext.setAttribute( "lastchange", lastchange, PageContext.REQUEST_SCOPE );

    // Set the content type and include the response content
    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
    String contentPage = WikiEngine.getTemplateManager().findJSP( pageContext, wikiContext.getShape(), "ViewTemplate.jsp" );
%><wiki:Include page="<%=contentPage%>" />

