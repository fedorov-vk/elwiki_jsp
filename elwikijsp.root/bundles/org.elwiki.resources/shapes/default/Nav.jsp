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
<!-- ~~ START ~~ Nav.jsp (shapes/default) -->
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.attachment.*" %>
<%@ page import="org.apache.wiki.api.attachment.*" %>
<%@ page import="org.apache.wiki.pages0.PageManager" %>
<%@ page import="org.apache.wiki.tags.*" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
  WikiContext ctx = ContextUtil.findContext( pageContext );

  String text = ServicesRefs.getPageManager().getText( ctx.getPage() );
  StringTokenizer tokens = new StringTokenizer( text );
  //avg reading speeds: https://iovs.arvojournals.org/article.aspx?articleid=2166061
%>
<c:set var="attachments" value="<%= ServicesRefs.getAttachmentManager().listAttachments( ctx.getPage() ).size() %>" />

<c:set var="wordCount" value="<%= tokens.countTokens() %>" />
<c:set var="readingTime" value="${wordCount / 228}" />


<%-- navigation bar --%>
<div class="navigation" role="navigation">

<ul class="nav nav-pills pull-left">
  <%-- toggle sidebar &#9776; - ☰ &#8801; - ≡ --%>
  <li id="menu"><a href="#">&#9776;</a></li>

  <li id="selectorsidebar">
    <a href="#">
        <span>&hellip;</span>
         <!-- &#9617; - ░ -->
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu" data-hover-parent="li">
    <li data-toggle="buttons" data-pref="SidePanel">
      <label class="btn btn-default" data-toggle-target="sidepanel-menu" data-pref-value="menu">
        <input type="radio" data-pref="SidePanel"
               name="selectorSidePanel" ${prefs.SidePanel eq 'menu' ? "checked='checked'" : ""}>
          Side menu
      </label>
      <label class="btn btn-default" data-toggle-target="sidepanel-pages" data-pref-value="hierarchy">
        <input type="radio" data-pref="SidePanel"
               name="selectorSidePanel" ${prefs.SidePanel eq 'hierarchy' ? "checked='checked'" : ""}>
          Pages hierarchy
      </label>
    </li>
    </ul>
  </li>

  <li id="cmd_scope">
    <wiki:Link path="cmd.scope" title="<fmt:message key='scope.cmd.title'/>" >
      <wiki:Param name='redirect' value='<%=ctx.getPageId()%>'/>
      <fmt:message key='scope.cmd' />
      ${empty prefs.scopearea ? "All" : prefs.scopearea}
    </wiki:Link>
  </li>

  <c:set var="refresh_breadCrumbTrail_attr"><wiki:Breadcrumbs /></c:set>
  <%-- don't show the breadcrumbs if it has none or only one item --%>
  <c:if test="${fn:length(breadCrumbTrail) gt 2}">
  <li id="trail" tabindex="0">
    <a href="#">
        <span>&hellip;</span>
        <span><fmt:message key="actions.trail"/></span>
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu" data-hover-parent="li">
      <%--
      <li class="dropdown-header"><fmt:message key="header.yourtrail"/></li>
      <li class="divider"></li>
      --%>
      <li><wiki:Breadcrumbs separator="" /></li>
      <%--:FVK: old code.
      <c:forEach items="${breadCrumbTrail}" varStatus="status" begin="2">
          <c:set var="crumb" value="${breadCrumbTrail[fn:length(breadCrumbTrail) - status.index]}" />
          <li><wiki:Translate>[${crumb}]</wiki:Translate></li>
      </c:forEach>
       --%>

    </ul>
  </li>
  </c:if>

</ul>

<ul class="nav nav-pills pull-right">

    <c:set var="page">
      <wiki:PageType type="page"><wiki:PageName/></wiki:PageType>
      <wiki:PageType type="attachment"><wiki:ParentPageName/></wiki:PageType>
    </c:set>
    <c:set var="pageId"><wiki:PageId/></c:set>

  <%-- view --%>

  <%-- context upload -> context view&tab=attach ... --%>
  <%--
  <c:if test="${param.tab eq 'attach'}">
  <li id="view">
    <wiki:Link pageName="${page}" >
        <span class="icon-view-menu"></span>
        <span><fmt:message key="view.tab"/></span>
    </wiki:Link>
  </li>
  </c:if>
  --%>
  <wiki:CheckRequestContext context='<%=ContextUtil.compose(
    WikiContext.PAGE_INFO, WikiContext.PAGE_DIFF, WikiContext.ATTACHMENT_UPLOAD, WikiContext.PAGE_RENAME,
    WikiContext.PAGE_EDIT, WikiContext.PAGE_COMMENT, WikiContext.PAGE_CONFLICT)%>'>
  <li id="view">
    <wiki:Link pageName="${pageId}" >
        <span class="icon-view-menu"></span>
        <span><fmt:message key="view.tab"/></span>
    </wiki:Link>
  </li>
  </wiki:CheckRequestContext>

  <%-- attachment   : included in the info menu
  <wiki:CheckRequestContext context='<%=ContextUtil.compose(
    Context.PAGE_VIEW, Context.PAGE_INFO, Context.PAGE_RENAME, Context.PAGE_DIFF,
    Context.PAGE_EDIT, Context.PAGE_COMMENT, Context.PAGE_CONFLICT)%>'>
  <wiki:PageExists>
  <c:if test="${param.tab ne 'attach'}"><!-- context upload -> context view&tab=attach ... -- >
  <li id="attach"
   class="<wiki:Permission permission='!upload'>disabled</wiki:Permission>">
    <wiki:Link pageName="${page}" context="<%=WikiContext.ATTACHMENT_UPLOAD%>" accessKey="a" >
      <span class="icon-paper-clip"></span>
      <span><fmt:message key='attach.tab'/></span>
      <c:if test="${attachments > 0}"><span class="badge">${attachments}</span></c:if>
    </wiki:Link>
  </li>
  </c:if>
  </wiki:PageExists>
  </wiki:CheckRequestContext>
  --%>

  <%-- info --%>
  <wiki:CheckRequestContext context='<%=ContextUtil.compose(
    WikiContext.PAGE_VIEW, WikiContext.PAGE_INFO, WikiContext.ATTACHMENT_UPLOAD, WikiContext.PAGE_RENAME,
    WikiContext.PAGE_EDIT, WikiContext.PAGE_COMMENT, WikiContext.PAGE_CONFLICT)%>'>
  <wiki:PageExists>
  <li id="info" tabindex="0" role="contentinfo">
      <a href="#" accessKey="i">
        <span class="icon-info-menu"></span>
        <span><fmt:message key='info.tab'/></span>
        <c:if test="${attachments > 0}"><span class="badge">${attachments}</span></c:if>
        <wiki:PageExists><span class="caret"></span></wiki:PageExists>
      </a>
    <ul class="dropdown-menu pull-right" data-hover-parent="li">
      <li class="dropdown-header"><fmt:message key="info.version"/> : <span class="badge"><wiki:PageVersion /></span></li>
      <li class="dropdown-header"><fmt:message key="info.date"/> :
        <span>
        <wiki:CheckVersion mode="latest">
          <wiki:DiffLink version="latest" newVersion="previous"><wiki:PageDate format='${prefs["DateFormat"]}'/></wiki:DiffLink>
        </wiki:CheckVersion>
        <wiki:CheckVersion mode="notlatest">
          <wiki:DiffLink version="current" newVersion="latest"><wiki:PageDate format='${prefs["DateFormat"]}'/></wiki:DiffLink>
        </wiki:CheckVersion>
        </span>
      </li>
      <li class="dropdown-header">
        <fmt:message key="info.author"/> :
          <wiki:Author />
        <%-- :FVK: -но, вызывает ошибку оригинальный код, а именно /format="plain"/ для wiki:Author-
		  <wiki:Author format="plain"/>
        --%>
      </li>
      <li class="dropdown-header">
        <wiki:RSSImageLink mode="wiki" title="<fmt:message key='info.feed'/>"/>
      </li>
      <li class="divider"></li>
      <li class="dropdown-header">
        <c:set var="disabledBtn" value=""/>
        <wiki:CheckRequestContext context="<%=WikiContext.PAGE_INFO%>"><c:set var="disabledBtn" value="disabled" /></wiki:CheckRequestContext>
        <wiki:Link cssClass="btn btn-xs btn-default ${disabledBtn}"
                    context="<%=WikiContext.PAGE_INFO%>" pageId="${pageId}" tabindex="0">
          <fmt:message key="info.moreinfo"/>
        </wiki:Link>
      </li>
      <li class="dropdown-header">
        <c:set var="disabledBtn" value=""/>
        <wiki:CheckRequestContext context="<%=WikiContext.ATTACHMENT_UPLOAD%>"><c:set var="disabledBtn" value="disabled" /></wiki:CheckRequestContext>
        <wiki:Permission permission='!upload'><c:set var="disabledBtn" value="disabled" /></wiki:Permission>
        <wiki:Link cssClass="btn btn-xs btn-default ${disabledBtn}"
                    context="<%=WikiContext.ATTACHMENT_UPLOAD%>" pageId="${pageId}" tabindex="0">
          <span class="icon-paper-clip"></span>
          <fmt:message key='edit.tab.attachments'/>
          <c:if test="${attachments > 0}"><span class="badge">${attachments}</span></c:if>
        </wiki:Link>
      </li>
      <li class="divider"></li>
      <li class="dropdown-header">
        <fmt:message key="info.readingtime">
            <fmt:param><fmt:formatNumber pattern="#.#" value="${readingTime}" /></fmt:param>
            <fmt:param>${wordCount}</fmt:param>
        </fmt:message>
      </li>
      <c:set var="keywords"><wiki:Variable var='keywords' default='' /></c:set>
      <c:if test="${!empty keywords}">
      <li class="dropdown-header">
        <fmt:message key="info.keywords">
            <fmt:param>${keywords}</fmt:param>
        </fmt:message>
      </li>
      </c:if>
    </ul>
  </li>
  </wiki:PageExists>
  </wiki:CheckRequestContext>

  <%-- edit --%>
  <wiki:PageType type="page">
  <wiki:CheckRequestContext context='<%=ContextUtil.compose(
    WikiContext.PAGE_VIEW, WikiContext.PAGE_INFO, WikiContext.PAGE_DIFF, WikiContext.ATTACHMENT_UPLOAD, WikiContext.PAGE_RENAME)%>'>
	<li id="edit" class="<wiki:Permission permission='!edit'>disabled</wiki:Permission>">
      <wiki:PageType type="page">
        <wiki:Link context="<%=WikiContext.PAGE_EDIT%>" pageId="${pageId}" accessKey="e" >
          <span class="icon-pencil"></span>
          <span><fmt:message key='actions.edit'/></span>
        </wiki:Link>
      </wiki:PageType>
      <wiki:PageType type="attachment">
        <wiki:Link context="<%=WikiContext.PAGE_EDIT%>" pageName="<wiki:ParentPageName />" accessKey="e" >
          <span class="icon-pencil"></span>
          <span><fmt:message key='actions.edit'/></span>
        </wiki:Link>
      </wiki:PageType>
    </li>
  </wiki:CheckRequestContext>
  </wiki:PageType>

  <%-- create page --%>
  <wiki:PageType type="page">
  <wiki:CheckRequestContext context='<%=ContextUtil.compose(
    WikiContext.PAGE_VIEW, WikiContext.PAGE_INFO, WikiContext.PAGE_DIFF, WikiContext.ATTACHMENT_UPLOAD, WikiContext.PAGE_RENAME)%>'>
	<li id="menuCreatePage" class="<wiki:Permission permission='!edit'>disabled</wiki:Permission>">
      <wiki:PageType type="page">
        <wiki:Link context="<%=WikiContext.PAGE_CREATE%>" pageId="${pageId}" >
          <wiki:Param name="redirect" value="${pageId}"/>
          <span class="icon-pencil"></span>
          <span>Create</span>
        </wiki:Link>
      </wiki:PageType>
    </li>
  </wiki:CheckRequestContext>
  </wiki:PageType>

  <%-- delete page --%>
<%-- 
  <wiki:PageType type="page">
  <wiki:CheckRequestContext context='<%=ContextUtil.compose(
    Context.PAGE_VIEW, Context.PAGE_INFO, Context.PAGE_DIFF, Context.ATTACHMENT_UPLOAD, Context.PAGE_RENAME)%>'>
	<li id="menuCreatePage" class="<wiki:Permission permission='!edit'>disabled</wiki:Permission>">
      <wiki:PageType type="page">
        <wiki:Link context="<%=WikiContext.PAGE_DELETE%>" pageId="<%=ctx.getPageId()%>" >
          <wiki:Param name='redirect' value='<%=ctx.getPageId()%>'/>
          <span class="icon-pencil"></span>
          <span>Create</span>
        </wiki:Link>
      </wiki:PageType>
    </li>
  </wiki:CheckRequestContext>
  </wiki:PageType>
 --%>

  <%-- help slimbox-link --%>
  <wiki:CheckRequestContext context='<%=WikiContext.WIKI_FIND%>'>
  <li>
    <a class="slimbox-link" href="<wiki:Link format='url' pageName='SearchPageHelp' ><wiki:Param name='shape' value='reader'/></wiki:Link>">
      <span class="icon-help-menu"></span>
      <span><fmt:message key="find.tab.help" /></span>
    </a>
  </li>
  </wiki:CheckRequestContext>
  <wiki:CheckRequestContext context='<%=ContextUtil.compose(WikiContext.PAGE_EDIT, WikiContext.PAGE_COMMENT)%>'>
  <li>
    <a class="slimbox-link" href="<wiki:Link format='url' pageName='EditPageHelp' ></wiki:Link>">
      <span class="icon-help-menu"></span>
      <span><fmt:message key="edit.tab.help" /></span>
    </a>
    <%--
      <wiki:NoSuchPage pageName="EditPageHelp">
        <div class="error">
        <fmt:message key="comment.edithelpmissing">
        <fmt:param><wiki:EditLink pageName="EditPageHelp">EditPageHelp</wiki:EditLink></fmt:param>
        </fmt:message>
        </div>
      </wiki:NoSuchPage>
    --%>
  </li>
  </wiki:CheckRequestContext>
  <wiki:CheckRequestContext context='<%=WikiContext.WIKI_LOGIN%>'>
  <li>
    <a class="slimbox-link" href="<wiki:Link format='url' pageName='LoginHelp' ><wiki:Param name='shape' value='reader'/></wiki:Link>">
      <span class="icon-help-menu"></span>
      <span><fmt:message key="login.tab.help" /></span>
    </a>
  </li>
  <%--
  <wiki:NoSuchPage pageName="LoginHelp">
  <div class="error">
    <fmt:message key="login.loginhelpmissing">
       <fmt:param><wiki:EditLink pageName="LoginHelp">LoginHelp</wiki:EditLink></fmt:param>
    </fmt:message>
  </div>
  </wiki:NoSuchPage>
  --%>
  </wiki:CheckRequestContext>


  <%-- more menu --%>
  <li id="more" tabindex="0">
    <a href="#">
        <span class="icon-ellipsis-v"></span>
        <span><fmt:message key="actions.more"/></span>
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu pull-right" data-hover-parent="li">
      <wiki:PageExists>
      <wiki:CheckRequestContext context='<%=ContextUtil.compose(
        WikiContext.PAGE_VIEW, WikiContext.PAGE_INFO, WikiContext.PAGE_DIFF, WikiContext.ATTACHMENT_UPLOAD, WikiContext.PAGE_PREVIEW)%>' >

        <%-- VIEW RAW PAGE SOURCE --%>
        <li>
          <wiki:CheckVersion mode="latest">
            <wiki:Link cssClass="slimbox-link" pageId="${pageId}">
              <wiki:Param name='shape' value='raw'/>
              <fmt:message key='actions.rawpage' />
            </wiki:Link>
          </wiki:CheckVersion>
          <wiki:CheckVersion mode="notlatest">
            <wiki:Link cssClass="slimbox-link" version='${param.version}' pageId="${pageId}">
              <wiki:Param name='shape' value='raw'/>
              <fmt:message key='actions.rawpage' />
            </wiki:Link>
          </wiki:CheckVersion>
        </li>

        <%-- Show Reader View --%>
        <li>
          <wiki:CheckVersion mode="latest">
            <wiki:Link cssClass="interwiki" pageId="${pageId}">
              <wiki:Param name='shape' value='reader'/>
              <fmt:message key='actions.showreaderview' />
            </wiki:Link>
          </wiki:CheckVersion>
          <wiki:CheckVersion mode="notlatest">
            <wiki:Link cssClass="interwiki" version="${param.version}" pageId="${pageId}">
              <wiki:Param name='shape' value='reader'/>
              <fmt:message key='actions.showreaderview' />
            </wiki:Link>
          </wiki:CheckVersion>
        </li>

      </wiki:CheckRequestContext>
      </wiki:PageExists>


      <%-- ADD COMMENT --%>
      <wiki:CheckRequestContext context='<%=ContextUtil.compose(
        WikiContext.PAGE_VIEW, WikiContext.PAGE_INFO, WikiContext.PAGE_DIFF, WikiContext.ATTACHMENT_UPLOAD)%>'>
      <wiki:PageExists>
      <wiki:Permission permission="comment">
        <wiki:PageType type="page">
          <li>
            <wiki:Link context="<%=WikiContext.PAGE_COMMENT%>" pageId="${pageId}">
              <span class="icon-plus"></span> <fmt:message key="actions.comment" />
            </wiki:Link>
          </li>
        </wiki:PageType>
        <wiki:PageType type="attachment">
          <li>
            <%--
            <wiki:Link pageName="<wiki:ParentPageName />" context="<%=WikiContext.PAGE_COMMENT%>" title="<fmt:message key='actions.comment.title' />">
              <fmt:message key="actions.comment" />
            </wiki:Link>
            --%>
            <wiki:LinkToParent><fmt:message key="actions.addcommenttoparent" /></wiki:LinkToParent>
	      </li>
        </wiki:PageType>
      </wiki:Permission>
      </wiki:PageExists>
      </wiki:CheckRequestContext>

      <%-- WORKFLOW --%>
      <wiki:CheckRequestContext context='!workflow'>
      <wiki:UserCheck status="authenticated">
        <li>
          <wiki:Link path="Workflow.jsp">
            <fmt:message key='actions.workflow' />
          </wiki:Link>
        </li>
      </wiki:UserCheck>
      </wiki:CheckRequestContext>

      <%-- Persisting wiki content --%>
      <wiki:CheckRequestContext context="<%=WikiContext.PAGE_VIEW%>">      
      <wiki:Permission permission="<%=PermissionTag.ALL_PERMISSION%>">
        <li class="divider"></li>
        <li>
          <wiki:Link path="<%=ContextEnum.WIKI_PERSIST_CONTENT.getUri()%>">
            <wiki:Param name='redirect' value='<%=ctx.getPageId()%>'/>
            <wiki:Param name='action' value='save'/>
            Save wiki content
          </wiki:Link>
          <wiki:Link path="<%=ContextEnum.WIKI_PERSIST_CONTENT.getUri()%>">
            <wiki:Param name='redirect' value='<%=ctx.getPageId()%>'/>
            <wiki:Param name='action' value='load'/>
            Load wiki content
          </wiki:Link>
        </li>
      </wiki:Permission>
      </wiki:CheckRequestContext>

      <%-- GROUPS : moved to the UserBox.jsp
      <wiki:CheckRequestContext context='!creategroup' >
      <wiki:Permission permission="createGroups">
        <li>
          <wiki:Link path="cmd.createGroup" title="<fmt:message key='actions.creategroup.title'/>" >
            <fmt:message key='actions.creategroup' />
          </wiki:Link>
        </li>
      </wiki:Permission>
      </wiki:CheckRequestContext>
      --%>

      <%-- divider --%>
      <wiki:PageExists pageId="w3">

        <wiki:CheckRequestContext context='<%=ContextUtil.compose(
          WikiContext.PAGE_VIEW, WikiContext.PAGE_INFO, WikiContext.PAGE_DIFF, WikiContext.ATTACHMENT_UPLOAD, WikiContext.GROUP_CREATE)%>'>
	      <wiki:PageExists>
            <li class="divider "></li>
          </wiki:PageExists>
        </wiki:CheckRequestContext>

        <wiki:CheckRequestContext context='<%=ContextUtil.compose(WikiContext.WIKI_PREFS, WikiContext.PAGE_EDIT)%>'>
          <wiki:UserCheck status="authenticated">
            <li class="divider "></li>
          </wiki:UserCheck>
        </wiki:CheckRequestContext>

        <li class="more-menu"><wiki:InsertPage pageId="w3" /></li>

      </wiki:PageExists>

    </ul>
  </li>

</ul>

</div>
<!-- ~~ END ~~ Nav.jsp -->
