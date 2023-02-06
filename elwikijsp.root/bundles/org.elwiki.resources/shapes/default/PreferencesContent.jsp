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
<!-- ~~ START ~~ PreferencesContent.jsp -->
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.ui.*" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<div class="page-content">

<wiki:UserCheck status="notAuthenticated">
  <%@ include file="/shapes/default/PreferencesTab.jsp" %>
</wiki:UserCheck>

<wiki:UserCheck status="authenticated">
<div class="tabs">

  <h3 id="section-prefs">
    <fmt:message key="prefs.tab.prefs" />
  </h3>
  <%@ include file="/shapes/default/PreferencesTab.jsp" %>

  <wiki:Permission permission="editProfile">
  <wiki:UserProfile property="exists">
    <c:set var="profileTab" value="${param.tab == 'profile' ? 'data-activePane' : ''}"/>
    <h3 ${profileTab} id="section-profile"><fmt:message key="prefs.tab.profile"/></h3>
    <%@ include file="/shapes/default/ProfileTab.jsp" %>
    <%-- <%=LocaleSupport.getLocalizedMessage(pageContext, "prefs.tab.profile")%> --%>
  </wiki:UserProfile>
  </wiki:Permission>

  <wiki:Permission permission="createGroups"><%-- use WikiPermission --%>
    <c:set var="groupTab" value="${param.tab == 'groups' ? 'data-activePane' : ''}"/>
    <wiki:CheckRequestContext context='<%=ContextUtil.compose(
      WikiContext.GROUP_VIEW, WikiContext.GROUP_EDIT, WikiContext.GROUP_CREATE)%>'>
       <c:set var="groupTab">data-activePane</c:set>
    </wiki:CheckRequestContext>
    <h3 ${groupTab} id="section-groups"><fmt:message key="group.tab" /></h3>
    <%@ include file="/shapes/default/GroupTab.jsp" %>
  </wiki:Permission>

</div>
</wiki:UserCheck>

</div>
<!-- ~~ END ~~ PreferencesContent.jsp -->