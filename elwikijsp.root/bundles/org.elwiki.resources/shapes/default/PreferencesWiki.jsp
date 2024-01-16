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
<!-- ~~ START ~~ PreferencesWiki.jsp -->
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.ui.*" %>
<%@ page import="org.apache.wiki.pages0.*" %>
<%@ page import="org.elwiki.api.component.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.stream.*" %>
<%@ page import="org.apache.commons.lang3.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
	//Context prCtx = engine.getCurrentContext(); //:FVK: ??
	WikiContext wikiContext = ContextUtil.findContext( pageContext );
	Engine engine = wikiContext.getEngine();
	PageManager pageManager = engine.getManager(PageManager.class);
	//String redir = (String)wikiContext.getVariable("redirect");
	//if( redir == null ) redir = wikiContext.getConfiguration().getFrontPage();
%>

<c:set var="redirect"><wiki:Variable var='redirect' default='<%=pageManager.getMainPageId()%>' /></c:set>

<div class="page-content">

<h3>ElWiki Preferences</h3>

<form id="wikiPreferences" action="<wiki:Link format='url' path='<%=ContextEnum.WIKI_PREFERENCES.getUri()%>'/>"
      accept-charset="<wiki:ContentEncoding />" >

  <input type="hidden" name="redirect" value="${redirect}" />

  <div class="form-group ">
    <span class="form-col-20 control-label"></span>

    <span class="dropdown" style="display:inline-block" >
      <button id="idBtnAllApply" class="btn btn-success" type="submit" name="action" value="submiprefs">
        <fmt:message key='prefs.save.prefs.submit'/>
      </button>
    </span>

    <wiki:Link pageId='${redirect}' cssClass='btn btn-danger pull-right'>
      <fmt:message key='prefs.cancel.submit'/>
    </wiki:Link>
  </div>
</form>


<%
List<WikiPrefs> components = engine.getConfigurableManagers();
%>
<div class="leftAccordion">
<%= components.stream().map(e -> e.getConfigurationEntry()).collect(Collectors.joining()) %>
</div>

</div>
<%-- :FVK: workaround, возможно не требуется.
<script src="<wiki:Link format='url' path='scripts/configure-controller.js'/>"></script>
--%>
<!-- ~~ END ~~ PreferencesWiki.jsp -->
