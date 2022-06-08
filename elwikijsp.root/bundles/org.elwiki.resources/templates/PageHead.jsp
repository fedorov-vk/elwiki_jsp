<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- ~~ START ~~ PageHead.jsp -->
<%@ page import="org.apache.commons.lang3.time.StopWatch" %>
<%@ page import="org.apache.wiki.*" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.WatchDog" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.ui.TemplateManager" %>
<%@ page import="org.apache.wiki.util.*" %>
<%@ page import="org.elwiki_data.*" %>
<%@ page import="org.elwiki_data.impl.*" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%! Logger log = Logger.getLogger("PageHead"); %>
<%
   log.debug("PageHead.jsp");		
	//Engine wiki = Wiki.engine().find( getServletConfig() );
%>

<!-- ******* ViewTemplate jsp ******* (START) -->

<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<!doctype html>
<html lang="<c:out value='${prefs.Language}' default='en'/>">
<head>
<title>
    <fmt:message key="view.title.view">
      <fmt:param><wiki:Variable var="ApplicationName" /></fmt:param>
      <fmt:param><wiki:PageName /></fmt:param>
    </fmt:message>
</title>
<%@ include file="/templates/default/commonheader.jsp" %>
  <wiki:CheckVersion mode="notlatest">
    <meta name="robots" content="noindex,nofollow" />
  </wiki:CheckVersion>
  <wiki:CheckRequestContext context="diff|info">
    <meta name="robots" content="noindex,nofollow" />
  </wiki:CheckRequestContext>
  <wiki:CheckRequestContext context="!view">
    <meta name="robots" content="noindex,follow" />
  </wiki:CheckRequestContext>
</head>

<body class="context-<wiki:Variable var='requestcontext' default='' />">
<div class="container${prefs.Layout=='fixed' ? ' ' : '-fluid ' } ${prefs.Orientation} fixed-header">
<%@ include file="/templates/default/Header.jsp" %>
<!-- ~~ END ~~ PageHead.jsp -->
