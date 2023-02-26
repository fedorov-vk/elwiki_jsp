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
	//Context prCtx = engine.getCurrentContext(); //:FVK: ??
	WikiContext wikiContext = ContextUtil.findContext( pageContext );
	//String redir = (String)wikiContext.getVariable("redirect");
	//if( redir == null ) redir = wikiContext.getConfiguration().getFrontPage();
%>

<c:set var="redirect"><wiki:Variable var='redirect' default='<%=wikiContext.getConfiguration().getFrontPage() %>' /></c:set>

<div class="page-content">

<form id="idScopeSet" action="<wiki:Link path='cmd.scope' format='url'/>"
      class="form-frame scope-set active"
      accept-charset="<wiki:ContentEncoding/>">

  <input type="hidden" name="redirect" value="${redirect}" />

  <div class="form-group ">
    <span class="form-col-20 control-label"></span>

    <span class="dropdown" style="display:inline-block" >
      <button id="idBtnSetScope" class="btn btn-success" type="submit" name="action" value="submitscope">
        <fmt:message key='scope.save.submit'/>
      </button>
    </span>

    <wiki:Link cssClass="btn btn-danger pull-right"  pageName="${redirect}" >
      <fmt:message key='scope.cancel.submit'/>
    </wiki:Link>
  </div>

  <br/>

  <div class="form-group">
    <label class="control-label form-col-20" for="prefOrientation"><fmt:message key="scope.show.topics"/></label>
    <div class="btn-group" data-toggle="buttons">
      <label class="btn btn-default" >
        <input id="idScopeAreaSwitchAll" type="radio" data-pref="scopearea"
               name="scopearea" ${empty prefs.scopearea ? "checked='checked'" : ""} value="all">
        <fmt:message key="scope.btn.all" />
      </label>
      <label class="btn btn-default">
        <input id="idScopeAreaSwitchSelected" type="radio" data-pref="scopearea"
               name="scopearea" ${not empty prefs.scopearea ? "checked='checked'" : ""} value="selected">
        <fmt:message key="scope.btn.selected" />
      </label>
    </div>
  </div>

  <div class="form-group">
    <label class="control-label form-col-20" for="idScopeList"><fmt:message key="scope.label.selection"/></label>
    <select id="idScopeList" style="min-width:200px;" class="" name="ScopeList" data-pref="ScopeList">
      <%-- no need to use EditorIterator tags--%>
      <!-- <c:forEach items="${scopes}" var="scope">
        <option value='${scope}' ${prefs.scope==scope ? 'selected="selected"' : ''} >${scope}</option>
      </c:forEach> -->
    </select>

    &nbsp;&nbsp;&nbsp;
    <button id="idBtnScopeNew" class="btn " type="button" name="new">
      <fmt:message key="scope.btn.new"/>
    </button>
    &nbsp;&nbsp;&nbsp;
    <button id="idBtnScopeEdit" class="btn " type="button" name="edit">
      <fmt:message key="scope.btn.edit"/>
    </button>
    &nbsp;&nbsp;&nbsp;
    <button id="idBtnScopeRemove" class="btn " type="button" name="remove">
      <fmt:message key="scope.btn.remove"/>
    </button>
  </div>

</form>

<div id="idTopicsSelect" class="form-frame scope-select">
  <div class="form-group ">
    <span class="form-col-20 control-label"></span>
    <button id="idBtnScopeTopicsSelected" class="btn btn-success" type="submit" name="action" value="submitscope">
        <fmt:message key='scope.selection.ok'/>
    </button>
    <button id="idBtnScopeTopicsCancel" class="btn btn-danger pull-right">
      <fmt:message key='scope.selection.cancel'/>
    </button>
  </div>

  <br/>

  <div class="form-group">
    <label class="control-label form-col-20" for="idScopeName">Наименование scope</label>
    <span class="control-label form-col-20">
    <input id="idScopeName" class="form-control" type="text" name="assertedName" size="20"
       autofocus="autofocus" value="" />
    </span>
  </div>

  <div class="form-group ">
    <label class="control-label form-col-20" style="vertical-align:top;" for="SoftScopeContainer">
      Scope topics
    </label>
    
      <div id="SoftScopeContainer" class="SoftScopeContainer form-col-50">
      </div>
  </div>

</div>

</div>
<script src="<wiki:Link format='url' path='scripts/scope-controller.js'/>"></script>
<!-- ~~ END ~~ ScopeContent.jsp -->
