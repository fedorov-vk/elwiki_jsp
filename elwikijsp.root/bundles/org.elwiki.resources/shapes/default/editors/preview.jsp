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

<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.api.ui.*" %>
<%@ page import="org.apache.wiki.ui.*" %>
<%@ page import="org.apache.wiki.util.TextUtil" %>
<%@ page import="org.apache.wiki.filters0.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%--
        This is a special editor component for JSPWiki preview storage.
--%>
<%
   WikiContext context = ContextUtil.findContext( pageContext );
   String usertext = (String)pageContext.getAttribute( EditorManager.ATTR_EDITEDTEXT, PageContext.REQUEST_SCOPE );
   if( usertext == null ) usertext = "";

   String action = "comment".equals(request.getParameter("action")) ?
                   context.getURL( WikiContext.PAGE_COMMENT, context.getName() ) :
                   context.getURL( WikiContext.PAGE_EDIT, context.getName() );
%>
<form action="<%=action%>"
      method="post" accept-charset="<wiki:ContentEncoding/>"
       class=""
          id="editform"
     enctype="application/x-www-form-urlencoded">

  
    <%-- Edit.jsp & Comment.jsp rely on these being found.  So be careful, if you make changes. --%>
    <input type="hidden" name="author" value="${author}" />
    <input type="hidden" name="link" value="${link}" />
    <input type="hidden" name="remember" value="${remember}" />
    <input type="hidden" name="changenote" value="${changenote}" />

    <input type="hidden" name="page" value="<wiki:Variable var='pagename' />" />
    <input type="hidden" name="action" value="save" />
    <input type="hidden" name="<%=SpamFilter.getHashFieldName(request)%>"value="${lastchange}" />
  
  <textarea class="hidden" readonly="readonly"
              id="editorarea" name="<%=EditorManager.REQ_EDITEDTEXT%>"
            rows="4"
            cols="80"><%=TextUtil.replaceEntities(usertext)%></textarea>

  <div class="form-group">
    <input class="btn btn-primary" type="submit" name="edit" value="<fmt:message key='editor.preview.edit.submit'/>"
      accesskey="e"
          title="<fmt:message key='editor.preview.edit.title'/>" />
    <input class="btn btn-primary" type="submit" name="ok" value="<fmt:message key='editor.preview.save.submit'/>"
      accesskey="s"
          title="<fmt:message key='editor.preview.save.title'/>" />
    <input class="btn btn-danger pull-right" type="submit" name="cancel" value="<fmt:message key='editor.preview.cancel.submit'/>"
      accesskey="q"
          title="<fmt:message key='editor.preview.cancel.title'/>" />
  </div>

</form>
