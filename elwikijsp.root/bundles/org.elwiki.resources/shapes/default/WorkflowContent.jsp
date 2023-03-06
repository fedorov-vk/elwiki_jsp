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
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="/shapes/Error.jsp" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<div class="page-content table-filter-sort">

<wiki:UserCheck status="authenticated">
<h3><fmt:message key="workflow.heading" /></h3>
<p><fmt:message key="workflow.instructions"/></p>

<%-- Pending Decisions --%>
<div class="tabs">
<h4>
  <fmt:message key="workflow.decisions.heading" />
  <span class="badge">${empty decisions ? "empty" : fn:length(decisions)}</span>
</h4>

<c:if test="${empty decisions}">
  <div class="information"><fmt:message key="workflow.noinstructions"/></div>
</c:if>

<c:if test="${!empty decisions}">

  <p id="workflow-actor-instructions"><fmt:message key="workflow.actor.instructions"/></p>

  <table class="table table-striped table-condensed" aria-describedby="workflow-actor-instructions">
    <tr><%-- 5/45/15/15/20--%>
      <th scope="col"><fmt:message key="workflow.id"/></th>
      <th scope="col"><fmt:message key="workflow.item"/></th>
      <th scope="col"><fmt:message key="workflow.requester"/></th>
      <th scope="col"><fmt:message key="workflow.startTime"/></th>
      <th scope="col"><fmt:message key="workflow.actions"/></th>
    </tr>
    <tbody>
      <c:forEach var="decision" items="${decisions}">
        <tr>

          <%-- Workflow ID --%>
          <td rowspan=2><span class="badge" style="background:SlateGray;">${decision.workflowId}</span></td>

          <%-- Name of item --%>
          <td style="background:AliceBlue;">
            <fmt:message key="${decision.messageKey}">
              <c:forEach var="messageArg" items="${decision.facts}">
                <fmt:param>${messageArg}</fmt:param>
              </c:forEach>
            </fmt:message>
          </td>

          <%-- Requester: decision.actor.name --%>
          <td style="background:AliceBlue;">${decision.submitter.name}</td>

          <%-- When did the actor start this step? --%>
          <td style="background:AliceBlue;">
            <fmt:formatDate value="${decision.startTime}" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" />
		  </td>

          <%-- Possible actions (outcomes) --%>
          <td class="nowrap" style="background:AliceBlue;">
            <form action="<wiki:Link context='<%=WikiContext.WIKI_WORKFLOW%>' format='url'/>"
                      id="decision.${decision.id}"
                  method="POST" accept-charset="UTF-8">
              <input type="hidden" name="action" value="decide" />
              <input type="hidden" name="id" value="${decision.id}" />
              <c:forEach var="outcome" items="${decision.availableOutcomes}">
                <button class="btn btn-xs btn-default" type="submit" name="outcome" value="${outcome.messageKey}">
                  <fmt:message key="${outcome.messageKey}"/>
                </button>
              </c:forEach>
            </form>
          </td>

        </tr>

        <c:if test="${!empty decision.facts}">
        <tr class="workflow-details">
          <td colspan=4>
          	<dl class="page-content">
            <c:forEach var="fact" items="${decision.facts}">
              <dt style="font-weight:normal; font-family:monospace; text-decoration:underline;">
                <fmt:message key="${fact.messageKey}" /></dt>
              <dd style="margin-left:0;"><pre><c:out escapeXml="false" value="${fn:trim(fact.value)}" /></pre></dd>
                <%-- may contain a full dump for a version diff,  ico save-wiki-page   approval flow --%>
            </c:forEach>
            </dl>
          </td>
        </tr>
        </c:if>

      </c:forEach>
    </tbody>
  </table>
</c:if>

<!-- Running workflows for which current user is the owner -->
<h4>
  <fmt:message key="workflow.workflows.heading" />
  <span class="badge">${empty workflows ? "empty" : fn:length(workflows)}</span>
</h4>

<c:if test="${empty workflows}">
  <div class="information"><fmt:message key="workflow.noinstructions"/></div>
</c:if>

<c:if test="${!empty workflows}">

  <p id="workflow-owner-instructions"><fmt:message key="workflow.owner.instructions"/></p>

  <table class="table" aria-describedby="workflow-owner-instructions">
    <tr>
      <th scope="col"><fmt:message key="workflow.id"/></th>
      <th scope="col"><fmt:message key="workflow.item"/></th>
      <th scope="col"><fmt:message key="workflow.actor"/></th>
      <th scope="col"><fmt:message key="workflow.startTime"/></th>
      <th scope="col"><fmt:message key="workflow.actions"/></th>
    </tr>
    <tbody>
      <c:forEach var="workflow" items="${workflows}">
        <tr>
          <%-- Workflow ID --%>
          <td><span class="badge" style="background:SlateGray;">${workflow.id}</span></td>

          <%-- Name of item --%>
          <td style="background:AliceBlue;">
            <fmt:message key="${workflow.messageKey}">
              <c:forEach var="messageArg" items="${workflow.messageArguments}">
                <fmt:param><c:out value="${messageArg}"/></fmt:param>
              </c:forEach>
            </fmt:message>
          </td >

          <%-- Current actor --%>
          <td style="background:AliceBlue;">${workflow.currentActor.name}</td>

          <%-- When did the actor start this step? --%>
          <td style="background:AliceBlue;">
            <fmt:formatDate value="${workflow.currentStep.startTime}" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" />
          </td>

          <%-- Actions --%>
          <td style="background:AliceBlue;">
            <form id="workflow.${workflow.id}"
              action="<wiki:Link context='<%=WikiContext.WIKI_WORKFLOW%>' format='url'/>"
              method="POST" accept-charset="UTF-8">
              <input class="btn btn-danger btn-xs" type="submit" name="submit" value="<fmt:message key="outcome.step.abort" />" />
              <input type="hidden" name="action" value="abort" />
              <input type="hidden" name="id" value="${workflow.id}" />
            </form>
          </td>

        </tr>
      </c:forEach>
    </tbody>
  </table>
</c:if>

</div><%-- class=tabs --%>
</wiki:UserCheck>

<wiki:UserCheck status="notAuthenticated">
  <div class="info"><fmt:message key="workflow.beforelogin"/></div>
</wiki:UserCheck>
</div>
