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
 page import="java.util.stream.*" %><%@
 page import="org.apache.wiki.api.core.*" %><%@
 page import="org.elwiki_data.*" %><%@
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

	String permissionAction = request.getParameter("permission"); 
	WikiPage wikiPage = wikiContext.getPage();
	PageAclEntry aclEntry = null;
	String function = "";
	String permission = "";
	String roles = "";
	for(PageAclEntry aclEntry1 : wikiPage.getPageAcl()) {
		if( aclEntry1.getPermission().equals(permissionAction) ) {
			aclEntry = aclEntry1;
			break;
		}
	}
	if( aclEntry != null ) {
		function = aclEntry.isAllow()? "allow" : "disallow";
		permission = aclEntry.getPermission();
		roles = aclEntry.getRoles().stream().collect(Collectors.joining("\r"));
	}
%>

<c:set var="function" value="<%=function%>" />
<c:set var="permission" value="<%=permission%>" />
<c:set var="roles" value="<%=roles%>" />
<c:set var="redirect"><wiki:Variable var='redirect' default='<%=wikiContext.getPageId()%>' /></c:set>

<div class="page-content">

<form action="<wiki:Link format='url' path='<%=ContextEnum.PAGE_EDIT_ACL.getUri()%>'/>"
      id="idAclSet"
      method="POST" accept-charset="<wiki:ContentEncoding />" >

  <input type="hidden" name="redirect" value="${redirect}" />

  <div class="form-group ">
    <span class="form-col-20 control-label"></span>
    <span class="dropdown" style="display:inline-block" >
      <button id="idBtnSetAcl" class="btn btn-success" type="submit" name="action" value="save">
        <fmt:message key='acl.page.permission.submit.save'/>
      </button>
    </span>
      <wiki:Link context="<%=WikiContext.PAGE_ACL%>" pageId="<%=wikiContext.getPageId()%>" 
                 cssClass="btn btn-danger pull-right" >
        <fmt:message key='acl.page.permission.submit.cancel'/>
      </wiki:Link>
  </div>

  <div class="form-group">
    <label class="control-label form-col-20" for="editor"><fmt:message key="acl.page.label.function"/></label>
    <select class="" id="function" name="function">
        <option value='allow' ${function=='allow'? 'selected' : ''}>ALLOW</option>
        <option value='disallow' ${function=='disallow'? 'selected' : ''}>DISALLOW</option>
    </select>
  </div>

  <div class="form-group">
    <label class="control-label form-col-20" for="editor"><fmt:message key="acl.page.label.permission"/></label>
    <select class="" id="permission" name="permission">
        <option value='view'    ${permission=='view'? 'selected' : ''}>view</option>
        <option value='edit'    ${permission=='edit'? 'selected' : ''}>edit</option>
        <option value='comment' ${permission=='comment'? 'selected' : ''}>comment</option>
        <option value='upload'  ${permission=='upload'? 'selected' : ''}>upload</option>
        <option value='rename'  ${permission=='rename'? 'selected' : ''}>rename</option>
        <option value='modify'  ${permission=='modify'? 'selected' : ''}>modify</option>
        <option value='delete'  ${permission=='delete'? 'selected' : ''}>delete</option>
    </select>
  </div>

  <div class="form-group">
    <label class="control-label form-col-20"><fmt:message key="acl.page.label.roles" /></label>
    <textarea class="form-control form-col-50" rows="5" cols="30"
               name="roles" id="roles" >${roles}</textarea>
  </div>
  <div class="help-block form-col-offset-20"><fmt:message key="acl.page.permission.roles"/></div>

</form>

</div>
<!-- ~~ END ~~ EditAclContent.jsp -->