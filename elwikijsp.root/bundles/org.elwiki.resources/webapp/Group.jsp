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
<!-- ~~ START ~~ Group.jsp -->
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.apache.wiki.api.exceptions.NoSuchPrincipalException" %>
<%@ page import="org.apache.wiki.auth.WikiSecurityException" %>
<%@ page import="org.elwiki.api.authorization.WrapGroup" %>
<%@ page import="org.elwiki.api.authorization.*" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.ui.TemplateManager" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%!Logger log = Logger.getLogger("JSPWiki");%>

<%
    Engine wiki = Wiki.engine().find( getServletConfig() );
    // Create wiki context and check for authorization
    Context wikiContext = Wiki.context().create( wiki, request, ContextEnum.GROUP_VIEW.getRequestContext() );
    if(!ServicesRefs.getAuthorizationManager().hasAccess( wikiContext, response )) return;
    
    // Extract the current user, group name, members
    Session wikiSession = wikiContext.getWikiSession();
    IGroupManager groupMgr = ServicesRefs.getGroupManager();
    WrapGroup group = null;
    try {
        group = groupMgr.parseGroup( wikiContext, false );
        pageContext.setAttribute ( "Group", group, PageContext.REQUEST_SCOPE );
    } catch ( NoSuchPrincipalException e ) {
        // New group; let GroupContent print out the message...
    } catch ( WikiSecurityException e ) {
        wikiSession.addMessage( IGroupManager.MESSAGES_KEY, e.getMessage() );
    }
    
    // Set the content type and include the response content
    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
    String contentPage = ServicesRefs.getTemplateManager().findJSP( pageContext, wikiContext.getShape(), "ViewTemplate.jsp" );
%><wiki:Include page="<%=contentPage%>" />
<!-- ~~ END ~~ Group.jsp -->