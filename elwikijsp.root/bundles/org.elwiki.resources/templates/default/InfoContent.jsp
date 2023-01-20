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
<%@ page import="org.apache.wiki.tags.HistoryIteratorTag" %>
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
<fmt:setBundle basename="templates.default"/>
<%
  Context ctx = ContextUtil.findContext(pageContext);
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

<wiki:PageExists>

<wiki:PageType type="page"> <%-- TODO: remove wiki:PageType tag. --%>
  <div class="form-frame">
  <p>
  <fmt:message key='info.lastmodified'>
    <fmt:param><span class="badge"><wiki:PageVersion >1</wiki:PageVersion></span></fmt:param>
    <fmt:param>
      <a href="<wiki:DiffLink format='url' version='latest' newVersion='previous' />"
        title="<fmt:message key='info.pagediff.title' />" >
        <fmt:formatDate value="<%= wikiPage.getLastModifiedDate() %>" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" />
      </a>
    </fmt:param>
    <fmt:param><wiki:Author /></fmt:param>
  </fmt:message>
  <wiki:RSSImageLink mode="wiki" title="<fmt:message key='info.feed'/>" />
  </p>

  <wiki:CheckVersion mode="notfirst">
  <p>
    <fmt:message key='info.createdon'>
      <fmt:param>
        <wiki:Link version="1">
          <fmt:formatDate value="<%= firstPage.getLastModifiedDate() %>" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" />
        </wiki:Link>
      </fmt:param>
      <fmt:param><%=creationAuthor%></fmt:param>
    </fmt:message>
  </p>
  </wiki:CheckVersion>

  <wiki:Permission permission="rename">
    <wiki:Messages div="alert alert-danger" topic="rename" prefix='<%=LocaleSupport.getLocalizedMessage(pageContext,"prefs.errorprefix.rename")%>'/>
    <form action="<wiki:Link format='url' context='<%=ContextEnum.PAGE_RENAME.getRequestContext()%>' pageId='<%=ctx.getPageId()%>' />"
           class="form-group form-inline"
              id="renameform"
          method="post" accept-charset="<wiki:ContentEncoding />" >

      <input type="hidden" name="page" value="<wiki:Variable var='pagename' />" />
      <input class="btn btn-success" type="submit" name="rename" value="<fmt:message key='info.rename.submit' />" />
      <input class="form-control form-col-50" type="text" name="renameto"
             value="<c:out value='${param.renameto}' default='<%= wikiPage.getName() %>'/>" size="40" />
      <label class="btn btn-default" for="references">
        <input type="checkbox" name="references" id="references" checked="checked" />
        <fmt:message key="info.updatereferrers"/>
      </label>
    </form>
  </wiki:Permission>
  <wiki:Permission permission="!rename">
    <p class="text-warning"><fmt:message key="info.rename.permission"/></p>
  </wiki:Permission>

  <wiki:Permission permission="delete">
    <form action="<wiki:Link format='url' context='<%=ContextEnum.PAGE_DELETE.getRequestContext()%>' pageId='<%=ctx.getPageId()%>' />"
           class="form-group"
              id="deletePageForm"
          method="post" accept-charset="<wiki:ContentEncoding />" >
      <input class="btn btn-danger" type="submit" name="deletepage" id="idDeletePage"
        data-modal="+ .modal"
             value="<fmt:message key='info.delete.submit'/>" />
      <div class="modal"><fmt:message key='info.confirmdelete'/></div>
    </form>
  </wiki:Permission>
  <wiki:Permission permission="!delete">
    <p class="text-warning"><fmt:message key="info.delete.permission"/></p>
  </wiki:Permission>

  </div>


  <div class="tabs">
    <h4 id="history"><fmt:message key="info.history"/></h4>

    <wiki:SetPagination start="<%=startitem%>" total="<%=itemcount%>" pagesize="<%=pagesize%>" maxlinks="9"
                       fmtkey="info.pagination"
                         href='<%=ctx.getURL(ContextEnum.PAGE_INFO.getRequestContext(), wikiPage.getName(), "start=%s")%>' />

    <c:set var="first" value="<%=startitem%>"/>
    <c:set var="last" value="<%=startitem + pagesize%>"/>

    <div class="table-filter-sort-condensed-striped">
    <table class="table" aria-describedby="history">
      <tr>
        <th scope="col"><fmt:message key="info.version"/></th>
        <th scope="col"><fmt:message key="info.date"/></th>
        <th scope="col"><fmt:message key="info.size"/></th>
        <th scope="col"><fmt:message key="info.author"/></th>
        <th scope="col"><fmt:message key="info.changes"/></th>
        <th scope="col"><fmt:message key="info.changenote"/></th>
      </tr>

<%--
      <wiki:HistoryIterator id="pageContent">
      <c:if test="${ first == -1 || ((pageContent.version > first ) && (pageContent.version <= last )) }">
--%>
<%
	int first = startitem;
	int last = startitem + pagesize;
	EList<PageContent> contents = wikiPage.getPageContents();
	Iterator<PageContent> iter = contents.iterator();
	while (iter.hasNext()) {
		PageContent pageContent = iter.next();
		int ver = pageContent.getVersion();
		String version = String.valueOf(ver);
		if (first == -1 || ((ver > first) && (ver <= last )) ) {
%>
	  <c:set var="pageContent" value="<%=pageContent%>" />
      <tr>
        <td>
          <wiki:Link version="<%=version%>"><%=version%></wiki:Link>
        </td>

        <td class="nowrap" data-sortvalue="${pageContent.creationDate.time}">
<%-- TODO: :FVK: - you should specify the output format. After a short investigation, it was found that - the following lines are possible: pattern, timeZone ==null.
        <fmt:formatDate value="${pageContent.lastModify}" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" />
 --%>
        <fmt:formatDate value="${pageContent.creationDate}" />
        </td>

        <c:set var="pageSize">"${pageContent.length}"</c:set>
        <td class="nowrap" title="${pageSize} bytes">
          <%-- <fmt:formatNumber value='${pageSize/1000}' maxFractionDigits='3' minFractionDigits='1'/>&nbsp;<fmt:message key="info.kilobytes"/> --%>
          <%= org.apache.commons.io.FileUtils.byteCountToDisplaySize( pageContent.getLength() ) %>
        </td>
        <td><wiki:Author /></td>

        <td class="nowrap">
          <wiki:CheckVersion mode="notfirst">
            <wiki:DiffLink version="current" newVersion="previous"><fmt:message key="info.difftoprev"/></wiki:DiffLink>
            <wiki:CheckVersion mode="notlatest"> | </wiki:CheckVersion>
          </wiki:CheckVersion>
          <wiki:CheckVersion mode="notlatest">
            <wiki:DiffLink version="latest" newVersion="current"><fmt:message key="info.difftolast"/></wiki:DiffLink>
          </wiki:CheckVersion>
        </td>

        <td class="changenote">${pageContent.changeNote}</td>

      </tr>
<% }}%>
<%-- 
    </c:if>
    </wiki:HistoryIterator>
--%>

    </table>
    </div>
    ${pagination}

    <h4 id="page-refs"><fmt:message key="info.tab.links" /></h4>
    <table class="table" aria-describedby="page-refs">
      <tr>
      <th scope="col"><fmt:message key="info.tab.incoming" /></th>
      <th scope="col"><fmt:message key="info.tab.outgoing" /></th>
      </tr>
      <tr>
      <td>
        <div class="tree list-hover">
          <wiki:Link><wiki:PageName /></wiki:Link>
          <wiki:Plugin plugin="ReferringPagesPlugin" args="before='*' after='\n' " />
        </div>
      </td>
      <td>
        <div class="tree list-hover">
          <wiki:Plugin plugin="ReferredPagesPlugin" args="depth='1' type='local'" />
        </div>
      </td>
      </tr>
    </table>

    <%-- DIFF section --%>
    <wiki:CheckRequestContext context='diff'>
      <h4 data-activePane id="diff"><fmt:message key="diff.tab" /></h4>
      <wiki:Include page="DiffTab.jsp"/>
    </wiki:CheckRequestContext>

  </div>

</wiki:PageType>

</wiki:PageExists>

<wiki:NoSuchPage>
  <div class="danger">
  <fmt:message key="common.nopage">
    <fmt:param><a class="createpage" href="<wiki:EditLink format='url'/>"><fmt:message key="common.createit"/></a></fmt:param>
  </fmt:message>
  </div>
</wiki:NoSuchPage>

</div>
<!-- ~~ END ~~ InfoContent.jsp -->
