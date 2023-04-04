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
<!-- ~~ START ~~ GroupTab.jsp -->
<%@ page errorPage="/shapes/Error.jsp" %>
<%@ page import="java.security.Principal" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.elwiki.api.authorization.IGroupWiki" %>
<%@ page import="org.elwiki.api.authorization.*" %>
<%@ page import="org.elwiki.permissions.GroupPermission" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.util.comparators.PrincipalComparator" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
	//:FVK: duplicate - WikiContext wikiContext = ContextUtil.findContext( pageContext );
	//:FVK: duplicate - Engine engine = wikiContext.getEngine();
	AuthorizationManager authorizationManager = engine.getManager(AuthorizationManager.class);
	AccountManager accountManager = engine.getManager(AccountManager.class);
%>
<wiki:CheckRequestContext context="!<%=WikiContext.GROUP_CREATE%>"><c:set var="createFormClose" value="-close"/></wiki:CheckRequestContext>
<wiki:Permission permission="createGroups">
  <form action="<wiki:Link format='url' path='cmd.createGroup'/>"
         class="accordion${createFormClose}"
        method="post" accept-charset="<wiki:ContentEncoding />" >

    <h4><fmt:message key="newgroup.heading.create"/></h4>
    <input type="hidden" name="action"  value="save" />

    <fmt:message key='newgroup.errorprefix' var="msg"/>
    <wiki:Messages div="alert alert-danger form-col-offset-20 form-col-50" topic="group" prefix="${msg}"/>

    <div class="form-group">
      <label class="control-label form-col-20"><fmt:message key="group.name" /></label>
      <input type="text" size="30"
           class="form-control form-col-50"
            name="group" id="group"
     placeholder="<fmt:message key="grp.newgroupname"/>"   >
    </div>

    <div class="form-group">
      <label class="control-label form-col-20"><fmt:message key="group.members" /></label>
      <textarea class="form-control form-col-50" rows="5" cols="30"
                 name="members" id="members" ></textarea>
    </div>
    <div class="help-block form-col-offset-20"><fmt:message key="editgroup.memberlist"/></div>
    <%--<p class="help-block form-col-offset-20"><fmt:message key="grp.formhelp"/></p>--%>

    <input class="btn btn-success form-col-offset-20" type="submit" value="<fmt:message key='grp.savenewgroup'/>" />

  </form>
</wiki:Permission>

<wiki:CheckRequestContext context="!<%=WikiContext.GROUP_CREATE%>">
  <fmt:message key='group.errorprefix' var="msg"/>
  <wiki:Messages div="alert alert-danger" topic="group" prefix="${msg}" />
</wiki:CheckRequestContext>

<form action="<wiki:Link format='url' path='cmd.deleteGroup'/>"
      class="hidden"
        name="deleteGroupForm" id="deleteGroupForm"
      method="POST" accept-charset="UTF-8">
  <input type="hidden" name="group" value="${group.name}" />
  <input type="submit" name="ok"
   data-modal="+ .modal"
        value="<fmt:message key="actions.deletegroup"/>" />
  <div class="modal"><fmt:message key='grp.deletegroup.confirm'/></div>
</form>

<h4 id="allgroups"><fmt:message key='grp.allgroups'/></h4>
<div class="table-filter-sort-condensed-striped">
  <table class="table" aria-described-by="allgroups">
    <caption class="hide">Group Details</caption>
    <tr>
      <th scope="col"><fmt:message key="group.name"/></th>
      <th scope="col"><fmt:message key="group.members"/></th>
      <th scope="col"><fmt:message key="group.created"/></th>
      <th scope="col"><fmt:message key="group.thecreator"/></th>
      <th scope="col"><fmt:message key="group.modified"/></th>
      <th scope="col"><fmt:message key="group.themodifier"/></th>
      <th scope="col"><fmt:message key="group.actions"/></th>
    </tr>
    <tbody>
    <%
     List<IGroupWiki> groups = accountManager.getGroups();
     Collections.sort(groups);
    %>

	<c:forEach var="group" items="<%=groups%>" varStatus="status">
    <tr class="${param.group == group.name ? 'highlight' : ''}"> <!-- :FVK: here highligh specified group from parameters. -->

      <!--:FVK: <td><wiki:Link path='Group.jsp'><wiki:Param name='group' value='${group.name}'/>${group.name}</wiki:Link></td>-->
      <td><c:if test="${group.name =='Admin'}"><span class="icon-unlock-alt"></span> </c:if>${group.name}</td>

      <td>
        <c:forEach items="${group.memberNames}" var="member" varStatus="iter2Status">
          <c:if test="${not iter2Status.first}">, </c:if>${member}
        </c:forEach>
      </td>

      <!-- :FVK: ~ это оригинальный код Wiki 
      <td><fmt:formatDate value="${group.created}" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" /></td>
      -->
      <td><fmt:formatDate value="${group.created}" pattern="dd-MM-yyyy" timeZone="GMT+2" /></td>

      <td>${group.creator}</td>

      <!-- :FVK: ~ это оригинальный код Wiki 
      <td><fmt:formatDate value="${group.lastModifiedDate}" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" /></td>
      -->
      <td><fmt:formatDate value="${group.lastModifiedDate}" pattern="dd-MM-yyyy" timeZone="GMT+2" /></td>

      <td>${group.modifier}</td>

      <td class="nowrap">
      <%-- :FVK: why can't the wiki:Permission be used here?
        We can't use wiki:Permission, cause we are in a loop; so let's just borrow some code from PermissionTag.java
      --%>
     <%
      	IGroupWiki group = (IGroupWiki)pageContext.getAttribute("group");
      	String grouName = group.getName();
      	Session wikiSession = wikiContext.getWikiSession();
      	if( authorizationManager.checkPermission(wikiSession, new GroupPermission( grouName, "edit" )) ) {
      %>
          <a class="btn btn-xs btn-primary"
             href="<wiki:Link context='<%=WikiContext.GROUP_EDIT%>' format='url' id='${group.uid}'/>">
             <fmt:message key="actions.editgroup"/>
          </a>
     <% } %>
     <%
      	if( authorizationManager.checkPermission(wikiSession, new GroupPermission( grouName, "delete" )) ) {
      %>
        <button class="btn btn-xs btn-danger" type="button"
            onclick="document.deleteGroupForm.group.value ='${group.uid}';document.deleteGroupForm.ok.click();">
          <fmt:message key="actions.deletegroup"/>
        </button>
     <% } %>
      </td>
    </tr>
    </c:forEach>

    </tbody>
  </table>
</div>
<!-- ~~ END ~~ GroupTab.jsp -->
