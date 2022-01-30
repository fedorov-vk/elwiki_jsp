<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- ~~ START ~~ PageMiddle.jsp --><%@
 page import="org.apache.wiki.*" %><%@
 page import="org.elwiki.services.ServicesRefs" %><%@
 taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>

<wiki:CheckRequestContext context="!edit">
  <c:set var="sidebarState"><wiki:Variable var="sidebar" default="${prefs.Sidebar}" /></c:set>
  <c:set var="sidebarCookie" value="Sidebar" />
  <wiki:CheckRequestContext context='login|prefs|createGroup|viewGroup|conflict'>
    <c:set var="sidebarState" value="" />
    <c:set var="sidebarCookie" value="" />
  </wiki:CheckRequestContext>

  <div class="content ${sidebarState}" data-toggle="li#menu,.sidebar>.close"
                                       data-toggle-pref="${sidebarCookie}" >
    <div class="page" role="main">
</wiki:CheckRequestContext>
<wiki:CheckRequestContext context="edit">
  <div class="content" data-toggle="li#menu,.sidebar>.close" >
    <div class="page">
</wiki:CheckRequestContext>

      <%-- <wiki:Content/>
      <%@ include file="/t#k/${PageContent.jsp" %>
      <%@ include file="/t#k/PageContent.jsp" %>
       --%>
      <wiki:Content/>
      <%@ include file="/t#k/PageInfo.jsp" %>
    </div>
    <%@ include file="/t#k/Sidebar.jsp" %>
  </div>

<!-- ~~ END ~~ PageMiddle.jsp -->