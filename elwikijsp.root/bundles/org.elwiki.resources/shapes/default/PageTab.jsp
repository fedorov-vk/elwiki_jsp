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
<!-- ~~ START ~~ PageTab.jsp -->
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.api.providers.WikiProvider" %>
<%@ page import="org.elwiki_data.*" %>
<%@ page import="org.apache.wiki.pages0.PageManager" %>
<%@ page import="org.apache.wiki.util.TextUtil" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>

<c:choose>

<c:when test="${param.tab == 'attach'}">
	<%@ include file="/shapes/default/AttachmentTab.jsp" %>
</c:when>

<c:otherwise>

<%-- If the page is an older version, then offer a note and a possibility to restore this version as the latest one. --%>
<wiki:CheckVersion mode="notlatest">
  <%
	WikiContext wikiContext = ContextUtil.findContext( pageContext );
	Engine engine = wikiContext.getEngine();
	PageManager pageManager = engine.getManager(PageManager.class);  
  %>
  <c:set var="thisVersion" value="<%=wikiContext.getPageVersion()%>" />
  <c:set var="latestVersion" value="<%=pageManager.getPage(wikiContext.getPage().getName(), WikiProvider.LATEST_VERSION).getLastVersion()%>" />

<!-- TODO: understand and release follow //wiki:Link path='Wiki.jsp'// :FVK:  -->
  <form action="<wiki:Link format='url' path='<%=ContextEnum.PAGE_VIEW.getUri()%>'/>"
        method="get" accept-charset='UTF-8'>

    <input type="hidden" name="pageId" value="${param.pageId}" />
    <div class="error center">
      <label>
      <fmt:message key="view.oldversion">
        <fmt:param>
          <%--<wiki:PageVersion/>--%>
          <select id="version" name="version" onchange="this.form.submit();">
          <c:forEach begin="1" end="${latestVersion == -1 ? thisVersion : latestVersion }" var="version">
            <option value="${version}" ${(thisVersion==version) ? 'selected="selected"':''}>${version}</option>
          </c:forEach>
          </select>
        </fmt:param>
      </fmt:message>
      </label>
      <div>
      <wiki:Link pageId="<%=wikiContext.getPageId()%>" cssClass="btn btn-primary">
        <fmt:message key="view.backtolatest" />
      </wiki:Link>
      <wiki:Link context="<%=WikiContext.PAGE_EDIT%>" version="${thisVersion}" cssClass="btn btn-danger">
        <fmt:message key="view.restore" />
      </wiki:Link>
      </div>
    </div>
  </form>
</wiki:CheckVersion>


<%--
ISWEBLOG= <%= ContextUtil.findContext( pageContext ).getPage().getAttribute( /*ATTR_ISWEBLOG*/ "weblogplugin.isweblog" ) %>
--%>
<%--  IF BLOCOMMENT PAGE:  insert back buttons to mainblog and blogentry permalink --%>
<c:set var="mainblogpage" value="${fn:substringBefore(param.page,'_comments_')}" />
<c:if test="${not empty mainblogpage}">
<wiki:PageExists pageName="${mainblogpage}">
  <p></p>
  <c:set var="blogentrypage" value="${fn:replace(param.page,'_comments_','_blogentry_')}" />
  <div class="pull-right">
      <wiki:Link cssClass="btn btn-xs btn-default" pageId="${mainblogpage}">
         <fmt:message key="blog.backtomain">
           <fmt:param>${mainblogpage}</fmt:param>
         </fmt:message>
      </wiki:Link>
      <wiki:Link cssClass="btn btn-xs btn-primary" pageId="${blogentrypage}">
         <fmt:message key="blog.permalink" />
      </wiki:Link>
  </div>
  <div class="weblogcommentstitle">
    <fmt:message key="blog.commenttitle" />
  </div>
</wiki:PageExists>
</c:if>

<%-- Inserts no text if there is no page. --%>
<wiki:InsertPage />

<%-- IF BLOGENTRY PAGE: insert blogcomment if appropriate. --%>
<c:set var="mainblogpage" value="${fn:substringBefore(param.page,'_blogentry_')}" />
<c:if test="${not empty mainblogpage}">
<wiki:PageExists pageName="${mainblogpage}">
  <p></p>
  <c:set var="blogcommentpage" value="${fn:replace(param.page,'_blogentry_','_comments_')}" />
  <div class="pull-right">
      <wiki:Link pageId="${mainblogpage}" cssClass="btn btn-xs btn-default">
      	<fmt:message key="blog.backtomain"><fmt:param>${mainblogpage}</fmt:param></fmt:message>
      </wiki:Link>
      <wiki:Link context="<%=WikiContext.PAGE_COMMENT%>" pageId="${blogcommentpage}" cssClass="btn btn-xs btn-default">
        <span class="icon-plus"></span>
        <fmt:message key="blog.addcomments" />
      </wiki:Link>
  </div>
  <c:if test="${not empty blogcommentpage}">
    <wiki:PageExists pageName="${blogcommentpage}">
      <div class="weblogcommentstitle">
        <fmt:message key="blog.commenttitle" />
      </div>
      <div class="weblogcomments">
        <wiki:InsertPage pageName="${blogcommentpage}" />
      </div>
    </wiki:PageExists>
  </c:if>
</wiki:PageExists>
</c:if>

<wiki:NoSuchPage>
<%-- FIXME: Should also note when a wrong version has been fetched. --%>
  <div class="error">
    <fmt:message key="common.nopage">
      <fmt:param>
        <wiki:Link context="<%=WikiContext.PAGE_EDIT%>" cssClass="createpage">
          <fmt:message key="common.createit" />
        </wiki:Link>
      </fmt:param>
    </fmt:message>
  </div>
</wiki:NoSuchPage>

</c:otherwise>
</c:choose>
<!-- ~~ END ~~ PageTab.jsp -->
