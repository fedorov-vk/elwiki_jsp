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
<!-- ~~ START ~~ UserBox.jsp (shapes/default) -->
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
	//:FVK: duplicate - WikiContext wikiContext = ContextUtil.findContext(pageContext);
%>
<c:set var="redirect"><%=wikiContext.getPageId()%></c:set>
<c:set var="username"><wiki:UserName /></c:set>
<c:set var="useruid"><wiki:UserProfile property='uid' /></c:set>
<c:set var="loginstatus"><wiki:Variable var='loginstatus'/></c:set>

<div class="cage pull-right userbox user-${loginstatus}" tabindex="0">

  <%-- <div onclick="" class="btn">
      FFS the onclick="" is needed for hover effect on ipad https://www.codehaven.co.uk/fix-css-hover-on-iphone-ipad/ --%>
  <a href="#" aria-label="<fmt:message key='userbox.button'/>" class="btn">
    <span class="icon-user"></span><span class="caret"></span>
  </a>
  <ul class="dropdown-menu pull-right" data-hover-parent=".userbox">
    <li>
      <wiki:UserCheck status="anonymous">
        <wiki:Link context='<%=WikiContext.WIKI_PREFS%>'>
          <span class="icon-user"></span>
          <fmt:message key="fav.greet.anonymous"/>
        </wiki:Link>
      </wiki:UserCheck>
      <wiki:UserCheck status="known"><%-- asserted or authenticated --%>
        <wiki:LinkTo pageId="${useruid}">
          <span class="icon-user" ></span>
          <wiki:UserCheck status="asserted">
            <fmt:message key="fav.greet.asserted"><fmt:param>${username}</fmt:param></fmt:message>
          </wiki:UserCheck>
          <wiki:UserCheck status="authenticated">
            <fmt:message key="fav.greet.authenticated"><fmt:param>${username}</fmt:param></fmt:message>
          </wiki:UserCheck>
        </wiki:LinkTo>
      </wiki:UserCheck>
    </li>

    <li class="divider"></li>

    <li class="dropdown-header">
      <%--
           user preferences button: preferences; groups
      --%>
      <wiki:CheckRequestContext context='<%=WikiContext.NONE_WIKI_PREFS%>'>
        <wiki:CheckRequestContext context='<%=WikiContext.NONE_PAGE_PREVIEW%>'>
          <wiki:Link context='<%=WikiContext.WIKI_PREFS%>' cssClass="btn btn-default btn-block">
            <wiki:Param name='redirect' value='${redirect}'/>
           <fmt:message key="actions.prefs" />
          </wiki:Link>
        <wiki:Permission permission="createGroups">
          <wiki:Link context='<%=WikiContext.WIKI_PREFS%>' ref="section-groups" cssClass="btn btn-default btn-block">
            <wiki:Param name='redirect' value='${redirect}'/>
            <span class="icon-group"></span> <fmt:message key="actions.groups" />
          </wiki:Link>
        </wiki:Permission>
        </wiki:CheckRequestContext>
      </wiki:CheckRequestContext>
      <%--
           login button
      --%>
      <wiki:UserCheck status="notAuthenticated">
        <wiki:CheckRequestContext context='<%=WikiContext.NONE_WIKI_LOGIN%>'>
        <wiki:Permission permission="login">
          <wiki:Link context='<%=WikiContext.WIKI_LOGIN%>' cssClass="btn btn-primary btn-block login">
            <wiki:Param name='redirect' value='${redirect}'/>
            <span class="icon-signin"></span>
            <fmt:message key="actions.login" />
          </wiki:Link>
        </wiki:Permission>
        <wiki:Permission permission='editProfile'>
          <wiki:Link context='<%=WikiContext.WIKI_LOGIN%>' cssClass="btn btn-link btn-block register">
            <wiki:Param name='redirect' value='${redirect}'/>
            <wiki:Param name='tab' value='register'/>
            <fmt:message key="actions.registernow" />
          </wiki:Link>
        </wiki:Permission>
        </wiki:CheckRequestContext>
      </wiki:UserCheck>
      <%--
           logout button
      --%>
      <wiki:UserCheck status="authenticated">
        <wiki:Link context='<%=WikiContext.WIKI_LOGOUT%>' cssClass="btn btn-default btn-block logout" datamodal=".modal">
          <span class="icon-signout"></span>
          <fmt:message key="actions.logout"/>
          <div class="modal"><fmt:message key='actions.confirmlogout'/></div>
        </wiki:Link>
      </wiki:UserCheck>
    </li>
  </ul>
</div>
<!-- ~~ END ~~ UserBox.jsp  -->