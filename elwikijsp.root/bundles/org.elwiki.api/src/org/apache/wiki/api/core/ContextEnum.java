/*
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
 */
package org.apache.wiki.api.core;


/**
 * Every enum contains 3 strings:
 * <ul>
 * <li> request context ID
 * <li> URL pattern
 * <li> JSP file name from the templates 
 * </ul>
 */
public enum ContextEnum {

    GROUP_DELETE( "deleteGroup", "%uDeleteGroup.jsp?group=%n", null ),
    GROUP_EDIT( "editGroup", "%ueditGroup.cmd?group=%n", "EditGroupContent.jsp" ),
    GROUP_VIEW( "viewGroup", "%uGroup.jsp?group=%n", "GroupContent.jsp" ),

    PAGE_ATTACH( "att", "%uattach/%n", null ),
    PAGE_COMMENT( "comment", "%uComment.jsp?page=%n", "CommentContent.jsp" ),
    PAGE_CONFLICT ( "conflict", "%uPageModified.jsp?page=%n", "ConflictContent.jsp" ),
    PAGE_DELETE( "del", "%uDelete.jsp?page=%n", null ),
    PAGE_DIFF( "diff", "%udiff.cmd?page=%n", "DiffContent.jsp" ),
    PAGE_EDIT( "edit", "%uedit.cmd?page=%n", "EditContent.jsp" ),
    PAGE_INFO( "info", "%uinfo.cmd?page=%n", "InfoContent.jsp" ),
    PAGE_NONE( "", "%u%n", null ),
    PAGE_PREVIEW( "preview", "%upreview.cmd?page=%n", "PreviewContent.jsp" ),
    PAGE_RENAME( "rename", "%urename.cmd?page=%n", "InfoContent.jsp" ),
    PAGE_RSS( "rss", "%urss.jsp", null ),
    PAGE_UPLOAD( "upload", "%uupload.cmd?page=%n", "AttachmentTab.jsp" ),
    PAGE_VIEW( "view", "%uview.cmd?pageId=%n", "PageContent.jsp" ),

    //:FVK: PAGE_VIEWID( "viewId", "%uWiki.jsp?pageId=%n", "PageContent.jsp" ),
    
    REDIRECT( "", "%u%n", null ),

    WIKI_ADMIN( "admin", "%uadmin/Admin.jsp", "AdminContent.jsp" ),
    WIKI_CREATE_GROUP( "createGroup", "%uNewGroup.jsp", "NewGroupContent.jsp" ),
    WIKI_ERROR( "error", "%uError.jsp", "DisplayMessage.jsp" ),
    WIKI_FIND( "find", "%uSearch.jsp", "FindContent.jsp" ),
    WIKI_INSTALL( "install", "%uInstall.jsp", null ),
    WIKI_LOGIN( "login", "%ulogin.cmd?redirect=%n", "LoginContent.jsp" ),
    WIKI_LOGOUT( "logout", "%ulogout.cmd", null ),
    WIKI_MESSAGE( "message", "%uMessage.jsp", "DisplayMessage.jsp" ),
    WIKI_PREFS( "prefs", "%uprefs.cmd", "PreferencesContent.jsp" ),
    WIKI_WORKFLOW( "workflow", "%uWorkflow.jsp", "WorkflowContent.jsp" );

	private final String requestContext;
	private final String urlPattern;
    private final String contentTemplate;

    ContextEnum( final String requestContext, final String urlPattern, final String contentTemplate ) {
        this.requestContext = requestContext;
        this.urlPattern = urlPattern;
        this.contentTemplate = contentTemplate;
    }
    
    public String getRequestContext() {
    	return requestContext;
    }
    
    public String getUrlPattern() {
    	return urlPattern;
    }
    
    public String getContentTemplate() {
    	return contentTemplate;
    }

}
