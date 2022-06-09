<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%--
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
<!-- ~~ START ~~ Header.jsp (templates/default) --><%@
 page import="org.apache.wiki.api.core.*" %><%@
 page import="org.elwiki.services.ServicesRefs" %><%@
 taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<c:set var="frontpage"><wiki:Variable var="jspwiki.frontPage" /></c:set>
<% String pageId = ServicesRefs.getPageManager().getMainPageId(); %>
<c:set var="frontpageId" value="<%=pageId%>" />
<c:set var="frontpageName" value="<%=ServicesRefs.getPageManager().getPageById(pageId).getName()%>" />

<div class="header">
  <c:set var="titlebox"><wiki:InsertPage pageId="41" /></c:set>
  <c:if test="${!empty titlebox}"><div class="titlebox">${titlebox}</div></c:if>

  <div class="topline">
    <div class="cage pull-left" tabindex="0">
        <a class="logo pull-left"
           href="<wiki:Link page='${frontpageId}' format='url' />"
           title="<fmt:message key='actions.home.title' ><fmt:param>${frontpageName}</fmt:param></fmt:message>">
           apache<b>jsp&#x03C9;iki</b>
        </a>

        <wiki:PageExists pageId="2">
        <ul class="dropdown-menu" data-hover-parent=".cage">
          <li class="logo-menu"><wiki:InsertPage pageId="2" /></li>
        </ul>
        </wiki:PageExists>
    </div>

	<%@ include file="/templates/default/UserBox.jsp" %>
	<%@ include file="/templates/default/SearchBox.jsp" %>

    <div class="pagename" title="<wiki:PageName />">
      <wiki:CheckRequestContext context='viewGroup|createGroup|editGroup'><span class="icon-group"></span></wiki:CheckRequestContext>
      <wiki:PageType type="attachment"><span class="icon-paper-clip"></span></wiki:PageType>

        <c:choose>
          <c:when test="${not empty fn:substringBefore(param.page,'_blogentry_')}">
            <wiki:Link>${fn:replace(fn:replace(param.page,'_blogentry_',' ['),'_','#')}]</wiki:Link>
          </c:when>
          <c:when test="${not empty fn:substringBefore(param.page,'_comments_')}">
            <wiki:Link>${fn:replace(fn:replace(param.page,'_comments_',' ['),'_','#')}]</wiki:Link>
          </c:when>
          <c:otherwise><a href="#top" tabindex="-1"><wiki:PageName /></a></c:otherwise>
        </c:choose>

    </div>

  </div>
  <%@ include file="/templates/default/Nav.jsp" %>
</div>
<!-- ~~ END ~~ Header.jsp  -->