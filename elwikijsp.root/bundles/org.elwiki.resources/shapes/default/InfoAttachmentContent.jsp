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
--%><!-- ~~ START ~~ InfoContent.jsp -->
<%@ page import="java.util.Iterator"%>
<%@ page import="org.eclipse.emf.common.util.EList" %>
<%@ page import="org.apache.wiki.api.core.*"%>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.elwiki.authorize.login.*" %>
<%@ page import="org.elwiki.permissions.*" %>
<%@ page import="org.apache.wiki.attachment.*" %>
<%@ page import="org.apache.wiki.api.i18n.InternationalizationManager" %>
<%@ page import="org.apache.wiki.api.attachment.*" %>
<%@ page import="org.apache.wiki.pages0.PageManager" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.api.ui.progress.ProgressManager" %>
<%@ page import="org.apache.wiki.util.TextUtil" %>
<%@ page import="org.elwiki_data.*" %>
<%@ page import="org.elwiki.services.ServicesRefs" %>
<%@ page import="java.security.Permission" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
  WikiContext ctx = ContextUtil.findContext(pageContext);
  WikiPage wikiPage = ctx.getPage();
  int attCount = ServicesRefs.getAttachmentManager().listAttachments( ctx.getPage() ).size();
  String attTitle = LocaleSupport.getLocalizedMessage(pageContext, "attach.tab");
  if( attCount != 0 ) attTitle += " (" + attCount + ")";

  String creationAuthor ="";

  //FIXME -- seems not to work correctly for attachments !!
  WikiPage firstPage = ServicesRefs.getPageManager().getPage( wikiPage.getName(), 1 );
  if( firstPage != null )
  {
    creationAuthor = firstPage.getAuthor();

    if( creationAuthor != null && creationAuthor.length() > 0 )
    {
      creationAuthor = TextUtil.replaceEntities(creationAuthor);
    }
    else
    {
      creationAuthor = Preferences.getBundle( ctx, InternationalizationManager.CORE_BUNDLE ).getString( "common.unknownauthor" );
    }
  }

  int itemcount = 0;  //number of page versions
  try
  {
    itemcount = wikiPage.getVersion(); /* highest version */
  }
  catch( Exception  e )  { /* dont care */ }

  int pagesize = 20;
  int startitem = itemcount-1; /* itemcount==1-20 -> startitem=0-19 ... */

  String parm_start = (String)request.getParameter( "start" );
  if( parm_start != null ) startitem = Integer.parseInt( parm_start ) ;

  /* round to start of block: 0-19 becomes 0; 20-39 becomes 20 ... */
  if( startitem > -1 ) startitem = ( startitem / pagesize ) * pagesize;

  /* startitem drives the pagination logic */
  /* startitem=-1:show all; startitem=0:show block 1-20; startitem=20:block 21-40 ... */
%>
<div class="page-content">

<%--:FVK:
<wiki:PageExists>
--%>

<%
  int MAXATTACHNAMELENGTH = 30;
%>
<c:set var="progressId" value="<%=ServicesRefs.getProgressManager().getNewProgressIdentifier()%>" />
<wiki:Permission permission="upload">

<!-- TODO: understand and release follow //wiki:Link path='attach'// :FVK:  -->
  <form action="<wiki:Link path='attach' format='url'><wiki:Param name='progressid' value='${progressId}'/></wiki:Link>"
         class="accordion-close"
            id="uploadform"
        method="post" accept-charset="<wiki:ContentEncoding/>"
       enctype="multipart/form-data" >

  <%-- Do NOT change the order of wikiname and content, otherwise the
       servlet won't find its parts. --%>

  <h4><span class="icon-paper-clip"></span> <fmt:message key="info.uploadnew"/></h4>

    <div class="form-group">
      <label class="control-label form-col-20" for="files"><fmt:message key="attach.add.selectfile"/></label>
      <ul class="list-group form-col-50">
        <li class="list-group-item droppable">
          <label>Select files <%--or drop them here!--%></label>
          <input type="file" name="files" id="files" size="60"/>
          <a class="hidden delete btn btn-danger btn-xs pull-right">Delete</a>
        </li>
      </ul>
    </div>
    <div class="form-group">
      <label class="control-label form-col-20" for="changenote"><fmt:message key="attach.add.changenote"/></label>
      <input class="form-control form-col-50" type="text" name="changenote" id="changenote" maxlength="80" size="60" />
    </div>
    <div class="form-group">
    <input type="hidden" name="nextpage" value="<wiki:Link context='<%=WikiContext.PAGE_INFO%>' format='url'/>" /><%-- *** --%>
    <input type="hidden" name="page" value="<wiki:Variable var="pagename"/>" />
    <input class="btn btn-success form-col-offset-20 form-col-50"
           type="submit" name="upload" id="upload" disabled="disabled" value="<fmt:message key='attach.add.submit'/>" />
    <%--<input type="hidden" name="action" value="upload" />--%>
    </div>
    <div class="hidden form-col-offset-20 form-col-50 progress progress-striped active">
      <div class="progress-bar" data-progressid="${progressId}" style="width: 100%;"></div>
    </div>

  </form>
</wiki:Permission>
<wiki:Permission permission="!upload">
  <div class="block-help bg-warning"><fmt:message key="attach.add.permission"/></div>
</wiki:Permission>


<form action="<wiki:Link format='url' context='<%=WikiContext.PAGE_DELETE%>' ><wiki:Param name='tab' value='attach' /></wiki:Link>"
           class="form-group"
              id="deletePageVersionForm"
          method="post" accept-charset="<wiki:ContentEncoding />" >

<%-- See Nav.jsp  "view" menu item
  <c:set var="parentPage"><wiki:ParentPageName/></c:set>
  <a class="btn btn-primary" href="<wiki:Link pageName='${parentPage}' format='url' />" >
    <fmt:message key="info.backtoparentpage" >
      <fmt:param><span class="badge">${parentPage}</span></fmt:param>
    </fmt:message>
  </a>
--%>
  <wiki:Permission permission="delete">
    <input class="btn btn-danger" type="submit" name="delete-all" id="delete-all"
      data-modal="+ .modal"
           value="<fmt:message key='info.deleteattachment.submit' />" />
    <div class="modal"><fmt:message key='info.confirmdelete'/></div>
  </wiki:Permission>
</form>


<%-- TODO why no pagination here - number of attach versions of one page limited ?--%>
  <h4 id="info-attachment-history"><fmt:message key='info.attachment.history' /></h4>
  <div class="slimbox-attachments table-filter-sort-condensed-striped">
  <table class="table" aria-describedby="info-attachment-history">
    <tr>
      <th scope="col"><fmt:message key="info.attachment.name"/></th>
      <th scope="col"><fmt:message key="info.version"/></th>
      <th scope="col"><fmt:message key="info.date"/></th>
      <th scope="col"><fmt:message key="info.size"/></th>
      <th scope="col"><fmt:message key="info.attachment.type"/></th>
      <th scope="col"><fmt:message key="info.author"/></th>
      <%--
      <wiki:Permission permission="upload">
         <th scope="col"><fmt:message key="info.actions"/></th>
      </wiki:Permission>
      --%>
      <th scope="col"><fmt:message key="info.changenote"/></th>
    </tr>

	
<%-- <wiki:HistoryIterator id="hist"> --%>
<%
	Engine engine = ctx.getEngine();
	PageManager pageManager = engine.getManager(PageManager.class);
	String attId = request.getParameter("id");
	//FIXME: here pageAttachment - can be null (not found).
	PageAttachment pageAttachment = pageManager.getPageAttachmentById(attId);

	EList<AttachmentContent> atts = pageAttachment.getAttachContents();
	Iterator<AttachmentContent> iter = atts.iterator();
	while (iter.hasNext()) {
		AttachmentContent content = iter.next();
%>
	<c:set var="att" value="<%=pageAttachment%>" />
	<c:set var="attContent" value="<%=content%>" />
    <tr>

      <td class="attach-name"><wiki:LinkTo version="${attContent.version}"><c:out value="${att.name}" /></wiki:LinkTo></td>

      <td><c:out value="${attContent.version}"/></td>

	  <td class="nowrap" data-sortvalue="${attContent.creationDate.time}">
	    <fmt:formatDate value="${attContent.creationDate}" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" />
	  </td>

      <td class="nowrap" title="${attContent.size} bytes" data-sortvalue="${attContent.size}">
        <%=org.apache.commons.io.FileUtils.byteCountToDisplaySize( content.getSize() )%>
      </td>

      <%-- see styles/fontjspwiki/icon.less : icon-file-<....>-o  --%>
      <c:set var="parts" value="${fn:split(att.name, '.')}" />
      <c:set var="type" value="${ fn:length(parts)>1 ? fn:escapeXml(parts[fn:length(parts)-1]) : ''}" />
      <td class="attach-type"><span class="icon-file-${fn:toLowerCase(type)}-o"></span>${type}</td>

      <td><c:out value="${fn:escapeXml(attContent.author)}"/></td>
      <%--
      // FIXME: This needs to be added, once we figure out what is going on.
      <wiki:Permission permission="upload">
         <td>
            <input type="button"
                   value="Restore"
                   url="<wiki:Link format='url' context='<%=WikiContext.ATTACHMENT_UPLOAD%>'/>"/>
         </td>
      </wiki:Permission>
      --%>

      <td class="changenote"><c:out value="${attContent.changeNote}"/></td>

    </tr>

<% }%>
<%-- </wiki:HistoryIterator> --%>

  </table>
  </div>

<%--:FVK:
</wiki:PageExists>
--%>

<wiki:NoSuchPage>
  <div class="danger">
  <fmt:message key="common.nopage">
    <fmt:param><a class="createpage" href="<wiki:EditLink format='url'/>"><fmt:message key="common.createit"/></a></fmt:param>
  </fmt:message>
  </div>
</wiki:NoSuchPage>

</div>
<!-- ~~ END ~~ InfoContent.jsp -->
