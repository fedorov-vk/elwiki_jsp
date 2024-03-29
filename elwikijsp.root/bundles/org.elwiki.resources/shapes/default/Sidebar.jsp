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
<!-- ~~ START ~~ Sidebar.jsp (shapes/default) --><%@
 page import="javax.servlet.jsp.jstl.fmt.*" %><%@
 page import="org.apache.wiki.api.core.*" %><%@
 taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<div class="sidebar">

<%-- :FVK: workaround - это вызывало ошибку компиляции JSP -- заменил на ="False".
  <c:set var="isweblog">< %= ( String )ContextUtil.findContext( pageContext ).getPage().getAttribute( /*ATTR_ISWEBLOG*/ "weblogplugin.isweblog" ) %></c:set>
 --%>
  <c:set var="isweblog"><%= "False" %></c:set>

  <c:if test="${isweblog}">
  <wiki:Calendar pageformat="'${param.page}_blogentry_'ddMMyy'_1'"
                 urlformat="'Wiki.jsp?page=${param.page}&weblog.startDate='ddMMyy'&weblog.days=1'"/>
  </c:if>

  <wiki:Permission permission="view">

<div id="sidepanel-menu" class="stacked-sidemenu">
  <div class="leftmenu">
    <wiki:InsertPage pageId="w4" />
    <wiki:NoSuchPage pageId="w4">
      <div class="error">
        <wiki:EditLink pageId="w4">
          <fmt:message key="fav.nomenu"><fmt:param>LeftMenu</fmt:param></fmt:message>
        </wiki:EditLink>
      </div>
    </wiki:NoSuchPage>
  </div>

  <div class="leftmenufooter">
    <wiki:InsertPage pageId="w5" />
    <wiki:NoSuchPage pageId="w5">
      <div class="error">
        <wiki:EditLink pageId="w5">
          <fmt:message key="fav.nomenu"><fmt:param>LeftMenuFooter</fmt:param></fmt:message>
        </wiki:EditLink>
      </div>
    </wiki:NoSuchPage>
  </div>
</div>
<div id="sidepanel-pages" class="stacked-pages">
<!-- container of tree viewer of pages -->
<div id="SoftPagesContainer"></div>
</div>

  </wiki:Permission>

  <%--
  <div class="wikiversion text-center"><%=Release.APPNAME%> v<wiki:Variable var="elwikiversion" /> <wiki:RSSImageLink title='<%=LocaleSupport.getLocalizedMessage(pageContext,"fav.aggregatewiki.title")%>' /></div>
  --%>

</div>
<!-- ~~ END ~~ Sidebar.jsp -->
