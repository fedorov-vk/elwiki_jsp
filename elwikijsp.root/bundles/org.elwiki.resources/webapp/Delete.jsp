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

<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.elwiki_data.*" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.apache.wiki.pages0.PageManager" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.tags.BreadcrumbsTag" %>
<%@ page import="org.apache.wiki.tags.BreadcrumbsTag.FixedQueue" %>
<%@ page import="org.apache.wiki.ui.TemplateManager" %>
<%@ page import="org.apache.wiki.util.HttpUtil" %>
<%@ page import="org.apache.wiki.util.TextUtil" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>

<%!
    Logger log = Logger.getLogger("JSPWiki");
%>

<%
Engine wiki = Wiki.engine().find( getServletConfig() );
    // Create wiki context and check for authorization
    WikiContext wikiContext = Wiki.context().create( wiki, request, ContextEnum.PAGE_DELETE.getRequestContext() );
    if( !WikiEngine.getAuthorizationManager().hasAccess( wikiContext, response ) ) return;
    if( wikiContext.getCommand().getTarget() == null ) {
        response.sendRedirect( wikiContext.getURL( wikiContext.getRequestContext(), wikiContext.getName() ) );
        return;
    }
    String pagereq = wikiContext.getName();

    WikiPage wikipage      = wikiContext.getPage();
    WikiPage latestversion = WikiEngine.getPageManager().getPage( pagereq );

    String delete = request.getParameter( "delete" );
    String deleteall = request.getParameter( "delete-all" );

    if( latestversion == null )
    {
        latestversion = wikiContext.getPage();
    }

    // If deleting an attachment, go to the parent page.
    String redirTo = pagereq;
    if( wikipage instanceof PageAttachment ) {
        redirTo = ":FVK:"; // ((PageAttachment)wikipage).getParentName();
    }

    if( deleteall != null ) {
        log.info("Deleting page "+pagereq+". User="+request.getRemoteUser()+", host="+HttpUtil.getRemoteAddress(request) );

        WikiEngine.getPageManager().deletePage( pagereq );

        FixedQueue trail = (FixedQueue) session.getAttribute( BreadcrumbsTag.BREADCRUMBTRAIL_KEY );
        if( trail != null )
        {
            trail.removeItem( pagereq );
            session.setAttribute( BreadcrumbsTag.BREADCRUMBTRAIL_KEY, trail );
        }

        response.sendRedirect( TextUtil.replaceString( wiki.getURL( ContextEnum.PAGE_VIEW.getRequestContext(), redirTo, "tab="+request.getParameter("tab") ),"&amp;","&" ));
        return;
    } else if( delete != null ) {
        log.info("Deleting a range of pages from "+pagereq);

        for( Enumeration< String > params = request.getParameterNames(); params.hasMoreElements(); ) {
            String paramName = params.nextElement();

            if( paramName.startsWith("delver") ) {
                int version = Integer.parseInt( paramName.substring(7) );

                WikiPage p = WikiEngine.getPageManager().getPage( pagereq, version );

                log.debug("Deleting version "+version);
                WikiEngine.getPageManager().deleteVersion( p );
            }
        }

        response.sendRedirect(
            TextUtil.replaceString( wiki.getURL( ContextEnum.PAGE_VIEW.getRequestContext(), redirTo, "tab=" + request.getParameter( "tab" ) ),"&amp;","&" )
        );

        return;
    }

    // Set the content type and include the response content
    // FIXME: not so.
    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
    String contentPage = WikiEngine.getTemplateManager().findJSP( pageContext, wikiContext.getShape(), "EditTemplate.jsp" );
%><wiki:Include page="<%=contentPage%>" />

