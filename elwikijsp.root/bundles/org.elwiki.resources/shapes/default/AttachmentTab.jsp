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
<!-- ~~ START ~~ AttachmentTab.jsp -->
<%@ page import="java.util.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.api.ui.progress.*" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.apache.wiki.ui.progress.*" %>
<%@ page import="org.elwiki.permissions.*" %>
<%@ page import="org.elwiki_data.*" %>
<%@ page import="java.security.Permission" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
	int MAXATTACHNAMELENGTH = 30;
	WikiContext wikiContext = ContextUtil.findContext(pageContext);
	Engine engine = wikiContext.getEngine();
	WikiPage wikiPage = wikiContext.getPage();
	ProgressManager progressManager = engine.getManager(ProgressManager.class);
%>
<c:set var="progressId" value="<%=progressManager.getNewProgressIdentifier()%>" />
<div class="page-content">
<wiki:Permission permission="upload">

<!-- TODO: understand and release follow //wiki:Link path='attach'// :FVK:  -->
  <form action="<wiki:Link format='url' path='attach'><wiki:Param name='progressid' value='${progressId}'/></wiki:Link>"
         class="accordion<wiki:HasAttachments></wiki:HasAttachments>"
            id="uploadform"
        method="post"
       enctype="multipart/form-data" accept-charset="<wiki:ContentEncoding/>" >

    <h4><span class="icon-paper-clip"></span> <fmt:message key="attach.add"/></h4>
    <%--
     --%>
    <input type="hidden" name="nextpage" value="<wiki:Link format='url' context='<%=WikiContext.ATTACHMENT_UPLOAD%>' pageId='<%=wikiPage.getId()%>'/>"/>
    <input type="hidden" name="idpage" value="<%=wikiContext.getPageId()%>" />
    <input type="hidden" name="action" value="upload" />

    <wiki:Messages div="alert alert-danger" />

    <%-- <p><fmt:message key="attach.add.info" /></p> --%>
    <div class="form-group">
      <label class="control-label form-col-20" for="files"><fmt:message key="info.uploadnew.selectfiles.label"/></label>
      <ul class="list-group form-col-50">
        <li class="list-group-item droppable">
          <a class="hidden delete btn btn-danger btn-xs pull-right">Delete</a>
          <label><fmt:message key="info.uploadnew.selectfiles.button"/></label>
          <input type="file" name="files" id="files" size="60" multiple="multiple"/>
        </li>
      </ul>
    </div>
    <div class="form-group">
      <label class="control-label form-col-20" for="changenote"><fmt:message key="info.uploadnew.changenote"/></label>
      <input class="form-control form-col-50" type="text" name="changenote" id="changenote" maxlength="80" size="60" />
    </div>
    <div class="form-group">
      <label class="control-label form-col-20"></label>
      <input class="btn btn-success form-col-50"
             type="submit" name="upload" id="upload" disabled="disabled" value="<fmt:message key='attach.add.submit'/>" />
    </div>
    <div class="hidden form-col-offset-20 form-col-50 progress progress-striped active">
      <div class="progress-bar" data-progressid="${progressId}" style="width: 100%;"></div>
    </div>

  </form>
</wiki:Permission>
<wiki:Permission permission="!upload">
  <div class="warning"><fmt:message key="attach.add.permission"/></div>
</wiki:Permission>

<wiki:HasAttachments>

<%--<h3><fmt:message key="attach.list"/></h3>--%>

  <wiki:Permission permission="delete">
    <%-- hidden delete form --%>
    <form action="tbd"
           class="hidden"
            name="deleteForm" id="deleteForm"
          method="post" accept-charset="<wiki:ContentEncoding />" >
      <input id="delete-all" name="delete-all" type="submit"
        data-modal="+ .modal"
             value="Delete" />
      <div class="modal"><fmt:message key='attach.deleteconfirm'/></div>
    </form>
  </wiki:Permission>

  <h4 id="attach-list"><fmt:message key='attach.list'/></h4>
  <div class="slimbox-attachments table-filter-striped-sort-condensed">

  <table class="table" aria-describedby="attach-list">
    <tr>
      <th scope="col"><fmt:message key="info.attachment.name"/></th>
      <th scope="col"><fmt:message key="info.version"/></th>
      <th scope="col"><fmt:message key="info.date"/></th>
      <th scope="col"><fmt:message key="info.size"/></th>
      <th scope="col"><fmt:message key="info.attachment.type"/></th>
      <th scope="col"><fmt:message key="info.author"/></th>
      <th scope="col"><fmt:message key="info.actions"/></th>
      <th scope="col"><fmt:message key="info.changenote"/></th>
    </tr>

    <%--
      --%>

    <c:forEach var="pageAttachment" items="<%=wikiPage.getAttachments()%>" varStatus="status">
    <tr>

      <%-- see styles/fontjspwiki/icon.less : icon-file-<....>-o  --%>
      <%
      	PageAttachment pageAttachment = (PageAttachment)pageContext.getAttribute("pageAttachment");
      	AttachmentContent attContent = pageAttachment.forLastContent();
      %>
      <c:set var="attContent" value="<%=attContent%>" />
      <c:set var="size" value="${attContent.size}" />
      <c:set var="parts" value="${fn:split(pageAttachment.name, '.')}" />
      <c:set var="type" value="${ fn:length(parts)>1 ? fn:escapeXml(parts[fn:length(parts)-1]) : ''}" />

      <td class="attach-name" title="${pageAttachment.name}">
        <wiki:LinkTo><c:out value="${pageAttachment.name}" /></wiki:LinkTo>
      </td>

      <td><c:out value="${attContent.version}"/></td>

      <td class="nowrap" data-sortvalue="${attContent.creationDate.time}">
        <fmt:formatDate value="${attContent.creationDate}" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" />
      </td>

      <td class="nowrap" title="${size} bytes" data-sortvalue="${size}">
        <%=org.apache.commons.io.FileUtils.byteCountToDisplaySize( attContent.getSize() )%>
      </td>

      <td class="attach-type"><span class="icon-file-${fn:toLowerCase(type)}-o"></span>${type}</td>

      <td><c:out value="${fn:escapeXml(attContent.author)}"/></td>

      <td class="nowrap">
        <wiki:Link context="<%=WikiContext.ATTACHMENT_INFO%>" pageId="${pageAttachment.id}" 
                   title="<fmt:message key='attach.moreinfo.title'/>" cssClass="btn btn-primary btn-xs" >
          <wiki:Param name='idattach' value='${pageAttachment.id}' />
          <fmt:message key="attach.moreinfo"/>
        </wiki:Link>
        <wiki:Permission permission="delete">
          <input type="button"
                class="btn btn-danger btn-xs"
                value="<fmt:message key='attach.delete'/>"
                  src="<wiki:Link
						format='url'
						context='<%=WikiContext.ATTACHMENT_DELETE%>'
						pageId='<%=wikiPage.getId()%>'>
					  <wiki:Param name='idattach' value='<%=pageAttachment.getId()%>' />
			  	  </wiki:Link>"
              onclick="document.deleteForm.action=this.src; document.deleteForm['delete-all'].click();" />
        </wiki:Permission>
      </td>

      <td class="changenote"><c:out value="${attContent.changeNote}"/></td>

    </tr>
    </c:forEach>
    <%--
     --%>

  </table>
  </div>

</wiki:HasAttachments>

</div>
<!-- ~~ END ~~ AttachmentTab.jsp -->
