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
<!-- ~~ START ~~ PreferencesContentRAP.jsp -->
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.ui.*" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<div class="page-content">

<%
Context ctx = ContextUtil.findContext( pageContext );
String redir = request.getParameter("redirect");
if( redir == null ) redir = ctx.getConfiguration().getFrontPage();
%>

<script>
    window.addEventListener('message', function(event) {
      let pageId="<%=redir%>";
      console.log(pageId);
      alert(`Получено ${event.data} из ${event.origin}`);
      window.location.replace('/cmd.view?pageId='+pageId);
    });
</script>

<%--
      let xhr = new XMLHttpRequest();

      var body = 'action=' + encodeURIComponent('setAssertedName') +
      '&redirect=' + encodeURIComponent('39');
      
      xhr.open('POST', '/cmd.prefs', true);
      xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
      xhr.onload = function(){
    	  console.log(xhr.response)
    	  document.body.innerHTML = xhr.response
      }
      xhr.send(body);
--%>
  
<%--

<div style="position: absolute; width: 100%; height: 100%;">
 --%>
<iframe src="/preferences" 
 style="border:thin; position: absolute; width: 70%; height: 90%;">
<p>К сожалению, ваш браузер не поддерживает iframes.
Sorry your browser does not support iframes.</p>
</iframe>

<%--
</div>

<wiki:UserCheck status="notAuthenticated">
  <%@ include file="/templates/default/PreferencesTab.jsp" %>
</wiki:UserCheck>

<wiki:UserCheck status="authenticated">
<div class="tabs">


  <h3 id="section-prefs">
    <fmt:message key="prefs.tab.prefs" />
  </h3>
  <%@ include file="/templates/default/PreferencesTab.jsp" %>

  <wiki:Permission permission="editProfile">
  <wiki:UserProfile property="exists">
    <c:set var="profileTab" value="${param.tab == 'profile' ? 'data-activePane' : ''}"/>
    <h3 ${profileTab} id="section-profile"><fmt:message key="prefs.tab.profile"/></h3>
    <%@ include file="/templates/default/ProfileTab.jsp" %>
    <% -- <%=LocaleSupport.getLocalizedMessage(pageContext, "prefs.tab.profile")%> -- %>
  </wiki:UserProfile>
  </wiki:Permission>

  <wiki:Permission permission="createGroups"><%-- use WikiPermission -- %>
    <c:set var="groupTab" value="${param.tab == 'groups' ? 'data-activePane' : ''}"/>
    <wiki:CheckRequestContext context='viewGroup|editGroup|createGroup'>
       <c:set var="groupTab">data-activePane</c:set>
    </wiki:CheckRequestContext>
    <h3 ${groupTab} id="section-groups"><fmt:message key="group.tab" /></h3>
    <%@ include file="/templates/default/GroupTab.jsp" %>
  </wiki:Permission>

</div>
</wiki:UserCheck>
 --%>
</div>
<!-- ~~ END ~~ PreferencesContentRAP.jsp -->
