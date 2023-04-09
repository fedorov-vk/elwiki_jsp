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
<!-- ~~ START ~~ AclPageContent.jsp (shapes/default) --><%@
 page import="java.util.*" %><%@
 page import="java.util.stream.*" %><%@
 page import="javax.servlet.jsp.jstl.fmt.*" %><%@
 page import="org.elwiki_data.*" %><%@
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

<%--
<c:set var="permissions"   value="<%=tm.listTimeZones(pageContext)%>" />
 --%>

<form action="<wiki:Link format='url' path='cmd.createAcl'/>"
         class="accordion-close"
        method="post" accept-charset="<wiki:ContentEncoding />" >

    <h4>Создать новое правило доступа</h4>
    <input type="hidden" name="action"  value="save" />

	<!-- TODO: error handling support. -->
    <fmt:message key='newgroup.errorprefix' var="msg"/>
    <wiki:Messages div="alert alert-danger form-col-offset-20 form-col-50" topic="group" prefix="${msg}"/>

    <div class="form-group">
      <label class="control-label form-col-20"><fmt:message key='acl.page.label.function'/></label>
      <select class="" id="rule" name="rule">
        <option value='allow'>ALLOW</option>
        <option value='disallow'>DISALLOW</option>
      </select>
    </div>

    <div class="form-group">
      <label class="control-label form-col-20"><fmt:message key='acl.page.label.permission'/></label>
      <select class="" id="permission" name="permission">
          <option value='view'>view</option>
          <option value='edit'>edit</option>
          <option value='comment'>comment</option>
          <option value='upload'>upload</option>
          <option value='rename'>rename</option>
          <option value='modify'>modify</option>
          <option value='delete'>delete</option>
      </select>
    </div>

    <div class="form-group">
      <label class="control-label form-col-20"><fmt:message key='acl.page.label.roles'/></label>
      <textarea class="form-control form-col-50" rows="5" cols="30"
                 name="roles" id="roles" ></textarea>
    </div>
    <div class="help-block form-col-offset-20"><fmt:message key="acl.page.permission.roles"/></div>

    <input class="btn btn-success form-col-offset-20" type="submit" value="<fmt:message key='acl.page.permission.submit.save'/>" />

</form>

  <table class="table">
    <tr>
      <th scope="col">Function</th>
      <th scope="col">Permission</th>
      <th scope="col">Roles</th>
      <th scope="col">Actions</th>
    </tr>
    <tbody>
    <%
     List<PageAclEntry> pageAcl = wikiContext.getPage().getPageAcl();
     //Collections.sort(pageAcl);
	 // <c:forEach var="aclEntry" items="< % =pageAcl % >" varStatus="status">*/
	 // </c:forEach>
     for( PageAclEntry aclEntry : wikiContext.getPage().getPageAcl() ) {
    %>
	<tr>
	  <td>
	    <%= aclEntry.isAllow()? "allow" : "disallow" %>
      </td>
	  <td>
	    <%= aclEntry.getPermission() %>
	  </td>
	  <td>
	    <%= aclEntry.getRoles().stream().collect(Collectors.joining(", ")) %>
	  </td>
	  <td>
	  	<wiki:Link context='<%=WikiContext.PAGE_EDIT_ACL%>' format='anchor' cssClass="btn btn-xs btn-primary"
	  	           id='<%=wikiContext.getPage().getId()%>'>
	  	  <wiki:Param name='permission' value='<%=aclEntry.getPermission()%>'/>
	  	  <fmt:message key="acl.page.permission.edit"/>
	  	</wiki:Link>
        <button class="btn btn-xs btn-danger" type="button"
            onclick="document.deleteGroupForm.group.value ='${group.uid}';document.deleteGroupForm.ok.click();">
          <fmt:message key="acl.page.permission.delete"/>
        </button>
	  </td>
	</tr>
	<%}%>
    </tbody>
  </table>
</div>
<!-- ~~ END ~~ AclPageContent.jsp -->
