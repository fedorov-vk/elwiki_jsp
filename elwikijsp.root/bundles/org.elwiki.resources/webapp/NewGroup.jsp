<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<!-- ~~ START ~~ NewGroup.jsp -->
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.api.exceptions.NoSuchPrincipalException" %>
<%@ page import="org.apache.wiki.auth.WikiSecurityException" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.elwiki.api.authorization.WrapGroup" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.ui.TemplateManager" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%!Logger log = Logger.getLogger("JSPWiki");%>

<%
Engine wiki = Wiki.engine().find( getServletConfig() );
    // Create wiki context and check for authorization
    Context wikiContext = Wiki.context().create( wiki, request, ContextEnum.GROUP_CREATE.getRequestContext() );
    if(!ServicesRefs.getAuthorizationManager().hasAccess( wikiContext, response )) return;
    
    // Extract the current user, group name, members and action attributes
    Session wikiSession = wikiContext.getWikiSession();
    GroupManager groupMgr = ServicesRefs.getGroupManager();
    WrapGroup group = null;
    try 
    {
        group = groupMgr.parseGroup( wikiContext, true );
        pageContext.setAttribute ( "Group", group, PageContext.REQUEST_SCOPE );
    } catch ( WikiSecurityException e ) {
        wikiSession.addMessage( GroupManager.MESSAGES_KEY, e.getMessage() );
        response.sendRedirect( "Group.jsp" );
    }
    
    // Are we saving the group?
    if( "save".equals(request.getParameter("action")) )
    {
        // Validate the group
        groupMgr.validateGroup( wikiContext, group );
        
        try 
        {
            groupMgr.getGroup( group.getName() );

            // Oops! The group already exists. This is mischief!
            ResourceBundle rb = Preferences.getBundle( wikiContext, "CoreResources");
            wikiSession.addMessage( GroupManager.MESSAGES_KEY,
                                    MessageFormat.format(rb.getString("newgroup.exists"),group.getName()));
        }
        catch ( NoSuchPrincipalException e )
        {
            // Group not found; this is good!
        }

        // If no errors, save the group now
        if ( wikiSession.getMessages( GroupManager.MESSAGES_KEY ).length == 0 )
        {
            try
            {
                groupMgr.setGroup( wikiSession, group );
            }
            catch( WikiSecurityException e )
            {
                // Something went horribly wrong! Maybe it's an I/O error...
                wikiSession.addMessage( GroupManager.MESSAGES_KEY, e.getMessage() );
            }
        }
        if ( wikiSession.getMessages( GroupManager.MESSAGES_KEY ).length == 0 )
        {
            response.sendRedirect( "Group.jsp?group=" + group.getName() );
            return;
        }
    }

    // Set the content type and include the response content
    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
    String contentPage = ServicesRefs.getTemplateManager().findJSP( pageContext, wikiContext.getShape(), "ViewTemplate.jsp" );
%><wiki:Include page="<%=contentPage%>" />
<!-- ~~ END ~~ NewGroup.jsp -->