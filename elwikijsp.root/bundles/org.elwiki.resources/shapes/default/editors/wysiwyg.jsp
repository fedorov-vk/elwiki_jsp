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
<!-- ~~ START ~~ wysiwyg.jsp -->
<%--
<%@ page import="org.apache.commons.lang3.*" %>
--%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.elwiki.permissions.*" %>
<%@ page import="org.apache.wiki.filters0.*" %>
<%@ page import="org.apache.wiki.pages0.PageManager" %>
<%@ page import="org.apache.wiki.parser0.MarkupParser" %>
<%@ page import="org.apache.wiki.render0.*" %>
<%@ page import="org.apache.wiki.ui.*" %>
<%@ page import="org.apache.wiki.api.filters.*" %>
<%@ page import="org.apache.wiki.api.ui.*" %>
<%@ page import="org.apache.wiki.util.TextUtil" %>
<%@ page import="org.apache.wiki.api.variables.VariableManager" %>
<%@ page import="org.elwiki_data.*" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<wiki:RequestResource type="stylesheet" resource="shapes/default/haddock-wysiwyg.css" />
<wiki:RequestResource type="script" resource="scripts/haddock-wysiwyg.js" />
<%--
    This provides a wysiwy editor for JSPWiki. (based on mooeditable)
--%>
<%
	WikiContext context = ContextUtil.findContext( pageContext );
    Engine engine = context.getEngine();

    context.setVariable( WikiContext.VAR_WYSIWYG_EDITOR_MODE, Boolean.TRUE );
    context.setVariable( VariableManager.VAR_RUNFILTERS,  "false" );

    WikiPage wikiPage = context.getPage();
    String originalCCLOption = (String)wikiPage.getAttribute( MarkupParser.PROP_CAMELCASELINKS );
//:FVK:    wikiPage.setAttribute( MarkupParser.PROP_CAMELCASELINKS, "false" );

    String usertext = ContextUtil.getEditedText(pageContext);
%>
<c:set var='context'><wiki:Variable var='requestcontext' /></c:set>
<wiki:CheckRequestContext context="<%=WikiContext.PAGE_EDIT%>">
<wiki:NoSuchPage> <%-- this is a new page, check if we're cloning --%>
<%
	String clone = request.getParameter( "clone" );
  if( clone != null )
  {
    WikiPage p = engine.getManager(PageManager.class).getPage( clone );
    if( p != null )
    {
        AuthorizationManager mgr = engine.getManager(AuthorizationManager.class);
        PagePermission pp = new PagePermission( p, PagePermission.VIEW_ACTION );

        try
        {
          if( mgr.checkPermission( context.getWikiSession(), pp ) )
          {
            usertext = engine.getManager(PageManager.class).getPureText( p );
          }
        }
        catch( Exception e ) {  /*log.error( "Accessing clone page "+clone, e );*/ }
    }
  }
%>
</wiki:NoSuchPage>
<%
  if( usertext == null )
  {
    usertext = engine.getManager(PageManager.class).getPureText( context.getPage() );
  }
%>
</wiki:CheckRequestContext>
<%
    if( usertext == null ) usertext = "";

    String pageAsHtml;
    try
    {
        pageAsHtml = engine.getManager(RenderingManager.class).getHTML( context, usertext );
    }
        catch( Exception e )
    {
        pageAsHtml = "<div class='error'>Error in converting wiki-markup to well-formed HTML <br/>" + e.toString() +  "</div>";
        /*
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        pageAsHtml += "<pre>" + sw.toString() + "</pre>";
        */
    }

   // Disable the WYSIWYG_EDITOR_MODE and reset the other properties immediately
   // after the XHTML for wysiwyg editor has been rendered.
   context.setVariable( WikiContext.VAR_WYSIWYG_EDITOR_MODE, Boolean.FALSE );
   context.setVariable( VariableManager.VAR_RUNFILTERS,  null );
//:FVK:   wikiPage.setAttribute( MarkupParser.PROP_CAMELCASELINKS, originalCCLOption );

   /*not used
   String templateDir = (String)engine.getWikiProperties().get( Engine.PROP_TEMPLATEDIR );
   String protocol = "http://";
   if( request.isSecure() ) {
       protocol = "https://";
   }
   */
   FilterManager filterManager = engine.getManager(FilterManager.class);
   ISpamFilter SpamFilter = filterManager.getSpamFilter();
   EditorManager editorManager = engine.getManager(EditorManager.class);
%>

<form method="post" accept-charset="<wiki:ContentEncoding/>"
      action="<wiki:CheckRequestContext
     context='<%=WikiContext.PAGE_EDIT%>'><wiki:EditLink format='url'/></wiki:CheckRequestContext><wiki:CheckRequestContext
     context='<%=WikiContext.PAGE_COMMENT%>'><wiki:CommentLink format='url'/></wiki:CheckRequestContext>"
       class="editform wysiwyg"
          id="editform"
     enctype="application/x-www-form-urlencoded" >

  <%-- Edit.jsp relies on these being found.  So be careful, if you make changes. --%>
  <input type="hidden" name="page" value="<wiki:Variable var='pagename' />" />
  <input type="hidden" name="action" value="save" />
  <%=SpamFilter.insertInputFields( pageContext )%>
  <input type="hidden" name="<%=SpamFilter.getHashFieldName(request)%>" value="${lastchange}" />
  <%-- This following field is only for the SpamFilter to catch bots which are just randomly filling all fields and submitting.
       Normal user should never see this field, nor type anything in it. --%>
  <input class="hidden" type="text" name="<%=SpamFilter.getBotFieldName()%>" id="<%=SpamFilter.getBotFieldName()%>" value="" />


  <div class="form-inline form-group">

    <div class="form-group dropdown">
    <button class="btn btn-success" type="submit" name="ok" accesskey="s">
      <%-- :FVK: follow "edit" - is wrong constant of Context.EDIT definition. --%>
      <fmt:message key='editor.plain.save.submit${ context == "edit" ? "" : ".comment" }'/>
      <span class="caret"></span>
    </button>
    <ul class="dropdown-menu" data-hover-parent="div">
      <li class="dropdown-header">
        <input class="form-control" type="text" name="changenote" id="changenote" size="80" maxlength="80"
             placeholder="<fmt:message key='editor.plain.changenote'/>"
             value="${changenote}" />
      </li>
      <wiki:CheckRequestContext context="<%=WikiContext.PAGE_COMMENT%>">
      <li class="divider" />
      <li class="dropdown-header">
        <fmt:message key="editor.commentsignature"/>
      </li>
      <li class="dropdown-header">
        <input class="form-control" type="text" name="author" id="authorname"  size="80" maxlength="80"
             placeholder="<fmt:message key='editor.plain.name'/>"
             value="${author}" />
      </li>
      <li  class="dropdown-header">
        <label class="btn btn-default btn-xs" for="rememberme">
          <input type="checkbox" name="remember" id="rememberme" ${ remember ? "checked='checked'" : "" } />
          <fmt:message key="editor.plain.remember"/>
        </label>
      </li>
      <li  class="dropdown-header">
        <input class="form-control" type="text" name="link" id="link" size="80" maxlength="80"
               placeholder="<fmt:message key='editor.plain.email'/>"
               value="${link}" />
      </li>
      </wiki:CheckRequestContext>
    </ul>
    </div>

  <div class="btn-group editor-tools">

    <div class="btn-group config">
      <%-- note: 'dropdown-toggle' is only here to style the last button properly! --%>
      <button class="btn btn-default dropdown-toggle"><span class="icon-wrench"></span><span class="caret"></span></button>
      <ul class="dropdown-menu" data-hover-parent="div">
            <li>
              <a>
                <label for="livepreview">
                  <input type="checkbox" data-cmd="livepreview" id="livepreview" ${prefs.livepreview ? 'checked="checked"' : ''}/>
                  <fmt:message key='editor.plain.livepreview'/> <span class="icon-refresh"/>
                </label>
              </a>
            </li>
            <li>
              <a>
                <label for="previewcolumn">
                  <input type="checkbox" data-cmd="previewcolumn" id="previewcolumn" ${prefs.previewcolumn ? 'checked="checked"' : ''}/>
                  <fmt:message key='editor.plain.sidebysidepreview'/> <span class="icon-columns"/>
                </label>
              </a>
            </li>

      </ul>
    </div>

    <c:set var="editors" value="<%=editorManager.getEditorList()%>" />
    <c:if test='${fn:length(editors)>1}'>
   <div class="btn-group config">
      <%-- note: 'dropdown-toggle' is only here to style the last button properly! --%>
      <button class="btn btn-default dropdown-toggle"><span class="icon-pencil"></span><span class="caret"></span></button>
      <ul class="dropdown-menu" data-hover-parent="div">
        <c:forEach items="${editors}" var="edt">
          <c:choose>
            <c:when test="${edt != prefs.editor}">
              <li>
                <wiki:Link context="<%=WikiContext.PAGE_EDIT%>" cssClass="editor-type">${edt}</wiki:Link>
              </li>
            </c:when>
            <c:otherwise>
              <li class="active"><a>${edt}</a></li>
            </c:otherwise>
          </c:choose>
      </c:forEach>
      </ul>
    </div>
    </c:if>

  </div>

  <div class="form-group pull-right">

  <%-- is PREVIEW functionality still needed - with livepreview ?
  <input class="btn btn-primary" type="submit" name="preview" accesskey="v"
         value="<fmt:message key='editor.plain.preview.submit'/>"
         title="<fmt:message key='editor.plain.preview.title'/>" />
  --%>
  <input class="btn btn-danger" type="submit" name="cancel" accesskey="q"
         value="<fmt:message key='editor.plain.cancel.submit'/>"
         title="<fmt:message key='editor.plain.cancel.title'/>" />

  </div>
  <%--TODO: allow page rename as part of an edit session
    <wiki:Permission permission="rename">
    <div class="form-group form-inline">
    <label for="renameto"><fmt:message key='editor.renameto'/></label>
    <input type="text" name="renameto" value="<wiki:Variable var='pagename' />" size="40" />
    <input type="checkbox" name="references" checked="checked" />
    <fmt:message key="info.updatereferrers"/>
    </div>
    </wiki:Permission>
  --%>

  </div>

  <div class="row edit-area livepreview previewcolumn"><%-- .livepreview  .previewcolumn--%>
      <div>
        <%--
        XSS note
        Textareas automatically decodes html entities : so &lt;  is converted to <
        To avoid this, double escape the & char =>  so &amp;lt; is converted to &lt;
        --%>
        <textarea name="htmlPageText"
             autofocus="autofocus"><%= pageAsHtml.replace("&", "&amp;")%></textarea>
      </div>
      <div class="ajaxpreview">Preview comes here</div>
  </div>

  <div class="resizer" data-pref="editorHeight"
       title="<fmt:message key='editor.plain.edit.resize'/>"></div>

</form>
<script type="text/javascript">
//<![CDATA[

Wiki.add("[name=htmlPageText]", function( element){

    function containerHeight(){ return editor.container.getStyle("height"); }
    function editorHeight(){ return editor.iframe.getStyle("height"); }
    function editorContent(){ return editor.getContent(); }
    function resizePreview(){
        preview.setStyle("height", containerHeight());
        form.htmlPageText.setStyle("height", editorHeight());
    }

    var form = element.form,
        editor,
        preview = form.getElement(".ajaxpreview"),
        resizer = form.getElement(".resizer"),
        resizeCookie = "editorHeight",
        html2markup = Wiki.getXHRPreview( editorContent, preview );

    Wiki.configPrefs( form, function(cmd, isChecked){
        if(isChecked && (cmd=="livepreview")){ html2markup(); }
    });

    element.mooEditable({
        dimensions:{
            x: "100%",
            y: "100%"
        },
        extraCSS: "body{padding:.5em;}",
  		externalCSS: $("main-stylesheet").href,
  		onAttach: function(){

  		    editor = this;
            Wiki.resizer(resizer, $$(editor.iframe), resizePreview );
            html2markup();
            resizePreview();

  		},
  		onChange: html2markup,
  		onEditorKeyUp: html2markup,
        onEditorPaste: html2markup,
	    actions: 'formatBlock | bold italic strikethrough | justifyleft justifyright justifycenter justifyfull | insertunorderedlist insertorderedlist indent outdent insertHorizontalRule / undo redo removeformat | createlink unlink | urlimage | toggleview'
	});

});

//]]>
</script>
<!-- ~~ END ~~ wysiwyg.jsp -->
