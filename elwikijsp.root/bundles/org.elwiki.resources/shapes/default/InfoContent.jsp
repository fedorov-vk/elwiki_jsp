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
--%><!-- ~~ START ~~ InfoContent.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="/shapes/Error.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="org.eclipse.emf.common.util.EList" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.apache.wiki.api.variables.*" %>
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
<%@ page import="java.security.Permission" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
	WikiContext wikiContext = ContextUtil.findContext( pageContext );
	Engine engine = wikiContext.getEngine();
	PageManager pageManager = engine.getManager(PageManager.class);
	VariableManager variableManager = engine.getManager(VariableManager.class);
	AttachmentManager attachmentManager = engine.getManager(AttachmentManager.class);

	WikiPage wikiPage = wikiContext.getPage();
	int attCount = attachmentManager.listAttachments( wikiContext.getPage() ).size();
	String attTitle = LocaleSupport.getLocalizedMessage(pageContext, "attach.tab");
	if( attCount != 0 ) {
		attTitle += " (" + attCount + ")";
	}

	String creationAuthor ="";

	//FIXME -- seems not to work correctly for attachments !!
	WikiPage firstPage = pageManager.getPage( wikiPage.getName(), 1 );
	if( firstPage != null ) {
		creationAuthor = firstPage.getAuthor();
		if( creationAuthor != null && creationAuthor.length() > 0 ) {
			creationAuthor = TextUtil.replaceEntities(creationAuthor);
		} else {
			creationAuthor = Preferences.getBundle(wikiContext).getString("common.unknownauthor");
		}
	}

	int itemcount = 0;  //number of page versions
	try {
		itemcount = wikiPage.getVersion(); /* highest version */
	} catch( Exception  e )  { /* dont care */ }

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
    <form action="<wiki:Link format='url' context='<%=WikiContext.PAGE_RENAME%>' pageId='<%=wikiContext.getPageId()%>' />"
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
    <form action="<wiki:Link format='url' context='<%=WikiContext.PAGE_DELETE%>' pageId='<%=wikiContext.getPageId()%>' />"
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
    <%-- Page versions history section --%>
    <h4 id="history"><fmt:message key="info.history"/></h4>

    <wiki:SetPagination start="<%=startitem%>" total="<%=itemcount%>" pagesize="<%=pagesize%>" maxlinks="9"
                       fmtkey="info.pagination"
                         href='<%=wikiContext.getURL(ContextEnum.PAGE_INFO.getRequestContext(), wikiPage.getName(), "start=%s")%>' />

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

    <c:set var="pageContents" value="<%=wikiPage.getPageContentsReversed()%>"/>
    <c:set var="maxVersion" value="${pageContents[0].version}"/>
	<c:forEach var="pageContent" items="${pageContents}" varStatus="status">
    <c:set var="version" value="${pageContent.version}"/>
    <c:if test="${ first == -1 || ((version > first ) && (version <= last )) }">
      <tr>
        <td><wiki:Link version="${version}"><c:out value="${version}"/></wiki:Link></td>

        <td class="nowrap" data-sortvalue="${pageContent.creationDate.time}">
          <fmt:formatDate value="${pageContent.creationDate}" pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}" />
        </td>

        <c:set var="pageLength" value="${pageContent.length}"/>
        <td class="nowrap" title="${pageLength} bytes">
          <%=org.apache.commons.io.FileUtils.byteCountToDisplaySize((Integer)pageContext.getAttribute("pageLength"))%>
        </td>

        <td><c:out value="${fn:escapeXml(pageContent.author)}"/></td>

        <td class="nowrap">
          <c:if test="${not status.last}">
            <c:set var="nextVersion" value="${pageContents[status.index+1].version}" />
            <wiki:Link version="${version}" compareToVersion="${nextVersion}" context="<%=WikiContext.PAGE_DIFF%>" pageId="<%=wikiContext.getPageId()%>"><fmt:message key="info.difftoprev"/></wiki:Link>
          </c:if>
          ${not status.last && not status.first? '|' : ''}
          <c:if test="${not status.first}">
            <wiki:Link version="${maxVersion}" compareToVersion="${version}" context="<%=WikiContext.PAGE_DIFF%>" pageId="<%=wikiContext.getPageId()%>"><fmt:message key="info.difftolast"/></wiki:Link>
          </c:if>
        </td>

        <td class="changenote">${pageContent.changeNote}</td>

      </tr>
    </c:if>
    </c:forEach>    
    </table>

    </div>
    ${pagination}

    <%-- Page references section --%>
    <h4 id="page-refs"><fmt:message key="info.tab.links" /></h4>
    <table class="table" aria-describedby="page-refs">
      <tr>
      <th scope="col"><fmt:message key="info.tab.incoming" /></th>
      <th scope="col"><fmt:message key="info.tab.outgoing" /></th>
      </tr>
      <tr>
      <td>
        <div class="tree list-hover">
        <%
        List<PageReference> inReferences = pageManager.getPageReferrers(wikiPage.getId());
        %>
        <c:set var="inReferences" value="<%=inReferences%>" />
        <c:if test="${empty inReferences}"><fmt:message key="info.tab.links.nothing"/></c:if>
        <c:if test="${not empty inReferences}">
		<%
		for (PageReference pageReference : inReferences) {
				String pageId = pageReference.getWikipage().getId();
				String href = wikiContext.getURL( WikiContext.PAGE_VIEW, pageId );
				WikiPage refPage = pageManager.getPageById(pageId);
				String name = (refPage != null)? refPage.getName() : "unknown page";
				String link = String.format("<li><a class=\"wikipage\" href=\"%s\">%s</a><br/>\n", href, name);
				out.append(link);
			}
		%>
        </c:if>
        </div>
      </td>
      <td>
        <div class="tree list-hover">
        <%
        List<PageReference> outReferences = wikiPage.getPageReferences();
        %>
        <c:set var="outReferences" value="<%=outReferences%>" />
        <c:if test="${empty outReferences}"><fmt:message key="info.tab.links.nothing"/></c:if>
        <c:if test="${not empty outReferences}">
		<%
		for (PageReference pageReference : outReferences) {
				String pageId = pageReference.getPageId();
				String href = wikiContext.getURL( WikiContext.PAGE_VIEW, pageId );
				WikiPage refPage = pageManager.getPageById(pageId);
				String name = (refPage != null)? refPage.getName() : "unknown page";
				String link = String.format("<li><a class=\"wikipage\" href=\"%s\">%s</a><br/>\n", href, name);
				out.append(link);
			}
		%>
        </c:if>
        </div>
      </td>
      </tr>
    </table>

    <%-- Page versions difference section --%>
    <wiki:CheckRequestContext context='<%=WikiContext.PAGE_DIFF%>'>
      <h4 data-activePane id="diff"><fmt:message key="diff.tab" /></h4>
      <c:set var="olddiff" value="${param.r1}" />
      <c:set var="newdiff" value="${param.r2}" />
      <c:set var="pageContents" value="<%=wikiPage.getPageContentsReversed()%>"/>
      <c:set var="diffprovider" value='<%=variableManager.getVariable(wikiContext,"jspwiki.diffProvider")%>' />
      <form action="<wiki:Link path='cmd.diff' format='url' />"
             class="diffbody form-inline"
            method="get" accept-charset="UTF-8">
        <input type="hidden" name="page" value="<wiki:PageName />" />

        <p class="btn btn-default btn-block">
          <fmt:message key="diff.difference">
            <fmt:param>
              <select class="form-control" id="r1" name="r1" onchange="this.form.submit();" >
              <c:forEach items="${pageContents}" var="i">
                <option value="${i.version}" ${i.version == olddiff ? 'selected="selected"' : ''} >${i.version}</option>
              </c:forEach>
              </select>
            </fmt:param>
            <fmt:param>
              <select class="form-control" id="r2" name="r2" onchange="this.form.submit();" >
              <c:forEach items="${pageContents}" var="i">
                <option value="${i.version}" ${i.version == newdiff ? 'selected="selected"' : ''} >${i.version}</option>
              </c:forEach>
              </select>
            </fmt:param>
          </fmt:message>
        </p>

        <c:if test='${diffprovider eq "ContextualDiffProvider"}' >     
          <div class="diffnote">     
            <a href="#change-1" title="<fmt:message key='diff.gotofirst.title'/>" class="diff-nextprev" >     
              <fmt:message key="diff.gotofirst"/>     
            </a>     
          </div>     
        </c:if>     

        <wiki:InsertDiff><p></p><p class="warning"><fmt:message key="diff.nodiff"/></p></wiki:InsertDiff>     

      </form>     
    </wiki:CheckRequestContext>

  </div>

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
