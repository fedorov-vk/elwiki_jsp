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
<!-- ~~ START ~~ PageContent.jsp  --><%@
 page import="org.apache.wiki.api.core.*" %><%@
 page import="org.apache.wiki.attachment.*" %><%@
 page import="javax.servlet.jsp.jstl.fmt.*" %><%@
 taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  Context c = ContextUtil.findContext( pageContext );
%>
<%-- Main Content Section --%>
<%-- This has been source ordered to come first in the markup (and on small devices)
     but to be to the right of the nav on larger screens --%>
<div class="page-content <wiki:Variable var='page-styles' default='' />">

  <%@ include file="/t#k/PageTab.jsp" %>

  <wiki:PageType type="attachment">
    <div><%-- insert the actual attachement, image, etc... --%>
      <wiki:Translate>[<%= ContextUtil.findContext( pageContext ).getPage().getName() %>]</wiki:Translate>
    </div>
  </wiki:PageType>

</div>
<!-- ~~ END ~~ PageContent.jsp  -->