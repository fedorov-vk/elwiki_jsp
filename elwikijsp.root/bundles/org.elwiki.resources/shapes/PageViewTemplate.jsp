<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="/shapes/Error.jsp" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.commons.lang3.time.StopWatch" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.*" %>
<%@ page import="org.apache.wiki.WatchDog" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.ui.TemplateManager" %>
<%@ page import="org.apache.wiki.util.*" %>
<%@ page import="org.elwiki_data.*" %>
<%@ page import="org.elwiki_data.impl.*" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%!
  Logger log;
  //Engine wiki; 
  public void jspInit()
  {
    //wiki = ServicesRefs.Instance; //:FVK: workaround.
    log = Logger.getLogger("PageViewTemplate_jsp");
  }
%>
<%
   log.debug("<-begin-> PageViewTemplate.jsp");
%>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<!DOCTYPE html>
<html lang="<c:out value='${prefs.Language}' default='en'/>">
<head>
<title>
    <fmt:message key="view.title.view">
      <fmt:param><wiki:Variable var="ApplicationName" /></fmt:param>
      <fmt:param><wiki:PageName /></fmt:param>
    </fmt:message>
</title>
<%@ include file="/shapes/default/commonheader.jsp" %>
  <wiki:CheckVersion mode="notlatest">
    <meta name="robots" content="noindex,nofollow" />
  </wiki:CheckVersion>
  <wiki:CheckRequestContext context="<%=ContextUtil.compose(WikiContext.PAGE_DIFF, WikiContext.PAGE_INFO)%>">
    <meta name="robots" content="noindex,nofollow" />
  </wiki:CheckRequestContext>
  <wiki:CheckRequestContext context="!<%=WikiContext.PAGE_VIEW%>">
    <meta name="robots" content="noindex,follow" />
  </wiki:CheckRequestContext>
</head>
<!-- ~~ Page Head ~~ -->
<body class="context-<wiki:Variable var='requestcontext' default='' />">
<div class="container${prefs.Layout=='fixed' ? ' ' : '-fluid ' } ${prefs.Orientation} fixed-header">
<%@ include file="/shapes/default/Header.jsp" %>
<!-- ~~ Page Middle ~~ -->
<wiki:CheckRequestContext context="!<%=WikiContext.PAGE_EDIT%>">
  <c:set var="sidebarState"><wiki:Variable var="sidebar" default="${prefs.Sidebar}" /></c:set>
  <c:set var="sidebarCookie" value="Sidebar" />
  <wiki:CheckRequestContext context='login'>
  <%--:FVK:
  <%=WikiContext.WIKI_LOGIN%>|<%=WikiContext.WIKI_PREFS%>|<%=WikiContext.GROUP_CREATE%>|<%=WikiContext.GROUP_VIEW%>|<%=WikiContext.PAGE_CONFLICT%>'>
  --%>
    <c:set var="sidebarState" value="" />
    <c:set var="sidebarCookie" value="" />
  </wiki:CheckRequestContext>

  <div class="content ${sidebarState}" data-toggle="li#menu,.sidebar>.close"
                                       data-toggle-pref="${sidebarCookie}" >
    <div class="page" role="main">
</wiki:CheckRequestContext>
<wiki:CheckRequestContext context="<%=WikiContext.PAGE_EDIT%>">
  <div class="content" data-toggle="li#menu,.sidebar>.close" >
    <div class="page">
</wiki:CheckRequestContext>

      <wiki:Content/>
      <%--
      <%@ include file="/shapes/default/${PageContent.jsp" %>
      <%@ include file="/shapes/default/PageContent.jsp" %>
       --%>
      <%@ include file="/shapes/default/PageInfo.jsp" %>
    </div>
    <%@ include file="/shapes/default/Sidebar.jsp" %>
  </div>
<!-- ~~ PageBottom ~~ -->
  <%@ include file="/shapes/default/Footer.jsp" %>
</div>
<!-- "stylesheet" -->
<wiki:IncludeResources type="stylesheet"/>
<!-- "inlinecss" -->
<wiki:IncludeResources type="inlinecss" />
<!-- "script" -->
<wiki:IncludeResources type="script"/>
<!-- "jsfunction" -->
<script type="text/javascript">//<![CDATA[
<wiki:IncludeResources type="jsfunction"/>
//]]></script>
</body>
</html>
<%
   log.debug("<-end-> PageViewTemplate.jsp");
%>
