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

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.elwiki_data.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.api.attachment.AttachmentManager" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.elwiki.permissions.*" %>
<%@ page import="org.apache.wiki.pages0.PageManager" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.api.references.ReferenceManager" %>
<%@ page import="org.apache.wiki.rss.*" %>
<%@ page import="org.apache.wiki.util.*" %>
<%!
    Logger log = Logger.getLogger("JSPWiki");
%>
<%
/*
     *  This JSP creates support for the SisterSites standard, as specified by
     *  http://usemod.com/cgi-bin/mb.pl?SisterSitesImplementationGuide
     */
    Engine wiki = Wiki.engine().find( getServletConfig() );
    // Create wiki context and check for authorization
    WikiContext wikiContext = Wiki.context().create( wiki, request, ContextEnum.PAGE_RSS.getRequestContext() );
    if( !WikiEngine.getAuthorizationManager().hasAccess( wikiContext, response ) ) return;
    
    Set< String > allPages = WikiEngine.getReferenceManager().findCreated();
    
    response.setContentType("text/plain; charset=UTF-8");
    for( String pageName : allPages ) {
        // Let's not add attachments.
        if( WikiEngine.getAttachmentManager().getAttachmentInfoName( wikiContext, pageName ) != null ) continue;

        WikiPage wikiPage = WikiEngine.getPageManager().getPage( pageName );
        if( wikiPage != null ) { // there's a possibility the wiki page may get deleted between the call to reference manager and now...
            PagePermission permission = PermissionFactory.getPagePermission( wikiPage, "view" );
            boolean allowed = WikiEngine.getAuthorizationManager().checkPermission( wikiContext.getWikiSession(), permission );
            if( allowed ) {
                String url = wikiContext.getViewURL( pageName );
                out.write( url + " " + pageName + "\n" );
            }
        }
    }
%>
