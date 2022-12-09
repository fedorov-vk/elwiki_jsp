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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.elwiki.permissions.*" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%! 
  Logger log;
  Engine wiki;
  public void jspInit()
  {
    log = Logger.getLogger("AJAXPreview_jsp");
    wiki = ServicesRefs.Instance; //:FVK: workaround.
  }
%>
<%
  Context wikiContext;
  wikiContext = (Context)request.getAttribute(Context.ATTR_WIKI_CONTEXT);
  if( wikiContext==null )
  {
    // Copied from a top-level jsp -- which would be a better place to put this 
    wikiContext = Wiki.context().create(wiki, request, ContextEnum.PAGE_VIEW.getRequestContext());
    request.setAttribute(Context.ATTR_WIKI_CONTEXT, wikiContext);
  }
  if( !ServicesRefs.getAuthorizationManager().hasAccess(wikiContext, response) ) return;

  response.setContentType("text/html; charset="+wiki.getContentEncoding());

  String wikimarkup = request.getParameter("wikimarkup");
%>
<wiki:Translate><%= wikimarkup %></wiki:Translate>
