<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Licensed to EPL 2.0
--%>
<!-- ~~ START ~~ ScopeContent.jsp -->
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.ui.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang3.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
	//Context prCtx = ThreadUtil.getCurrentContext(); //:FVK: ContextUtil.findContext( pageContext );
	WikiContext wikiContext = ContextUtil.findContext( pageContext );
	//String redir = (String)wikiContext.getVariable("redirect");
	//if( redir == null ) redir = wikiContext.getConfiguration().getFrontPage();
%>

<c:set var="redirect"><wiki:Variable var='redirect' default='<%=wikiContext.getConfiguration().getFrontPage() %>' /></c:set>

<div class="page-content">

<form id="idFormCreatePage" action="<wiki:Link format='url' path='<%=ContextEnum.PAGE_CREATE.getUri()%>'/>"
      class="form-frame"
      accept-charset="<wiki:ContentEncoding/>">

  <input type="hidden" name="redirect" value="${redirect}" />

  <div class="form-group ">
    <span class="form-col-20 control-label"></span>

    <span class="dropdown" style="display:inline-block" >
      <button id="idBtnCreate" class="btn btn-success" type="submit" name="action" value="create">
        Create
      </button>
    </span>

    &nbsp; &nbsp;
    <span class="dropdown" style="display:inline-block" >
      <button id="idBtnCreateEdit" class="btn btn-success" type="submit" name="action" value="createedit">
        Create & Edit
      </button>
    </span>

    <wiki:Link pageId="${redirect}" cssClass="btn btn-danger pull-right">
      Cancel
    </wiki:Link>
  </div>

  <div class="form-group">
    <label class="control-label form-col-20" for="idScopeName">Имя страницы</label>
    <span class="control-label form-col-50">
    <input id="idPageName" class="form-control" type="text" name="pageName" size="20"
       autofocus="autofocus" value="" />
    </span>
  </div>

</form>

</div>
