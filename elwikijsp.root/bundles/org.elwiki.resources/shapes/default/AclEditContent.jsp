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
<!-- ~~ START ~~ EditAclContent.jsp (shapes/default) --><%@
 page import="javax.servlet.jsp.jstl.fmt.*" %><%@
 page import="org.apache.wiki.api.core.*" %><%@
 taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
	//Context prCtx = engine.getCurrentContext(); //:FVK: ??
	WikiContext wikiContext = ContextUtil.findContext( pageContext );
	//String redir = (String)wikiContext.getVariable("redirect");
	//if( redir == null ) redir = wikiContext.getConfiguration().getFrontPage();
%>

<c:set var="redirect"><wiki:Variable var='redirect' default='<%=wikiContext.getPageId()%>' /></c:set>

<div class="page-content">

<form id="idAclSet" action="<wiki:Link path='<%=ContextEnum.PAGE_EDIT_ACL.getUri()%>' format='url'/>"
      accept-charset="<wiki:ContentEncoding/>">

  <input type="hidden" name="redirect" value="${redirect}" />

  <div class="form-group ">
    <span class="dropdown" style="display:inline-block" >
      <button id="idBtnSetAcl" class="btn btn-success" type="submit" name="action" value="save">
        <fmt:message key='acl.permission.submit.save'/>
      </button>
    </span>

    <wiki:Link cssClass="btn btn-danger pull-right"  pageName="${redirect}" >
      <fmt:message key='acl.permission.submit.cancel'/>
    </wiki:Link>
  </div>
</form>

<table>
    <tr>
      <th scope="col">Правило</th>
      <th scope="col">Permission</th>
      <th scope="col">Roles</th>
    </tr>

<tr>
<td>
  <div class="form-group">
    <label class="control-label form-col-20" for="editor"><fmt:message key="edit.chooseeditor"/></label>
    <select class="" id="editor" name="editor" data-pref="editor">
      <%-- no need to use EditorIterator tags--%>
      <c:forEach items="${editors}" var="edt">
        <option value='${edt}' ${prefs.editor==edt ? 'selected="selected"' : ''} >${edt}</option>
      </c:forEach>
    </select>
  </div>
</td>
<td>
</td>
<td>
</td>
</tr>

</table>

</div>
<!-- ~~ END ~~ EditAclContent.jsp -->
