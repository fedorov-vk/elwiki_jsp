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
 * Every enumeration contains 3 strings:
 * <ol>
 * <li>request context ID
 * <li>URL pattern
 * <li>JSP file name from the templates
 * </ol>
 */
public enum ContextEnum {

	//@formatter:off
    GROUP_DELETE( "deleteGroup", "%ucmd.deleteGroup?group=%n", null ),
    GROUP_EDIT( "editGroup", "%ucmd.editGroup?group=%n", "EditGroupContent.jsp" ),
    GROUP_VIEW( "viewGroup", "%ucmd.viewGroup?group=%n", "PreferencesContent.jsp" ),

    PAGE_ATTACH( "att", "%uattach/%n", null ),
    PAGE_COMMENT( "comment", "%uComment.jsp?page=%n", "CommentContent.jsp" ),
    PAGE_CONFLICT ( "conflict", "%uPageModified.jsp?page=%n", "ConflictContent.jsp" ),
    PAGE_DELETE( "del", "%uDelete.jsp?page=%n", null ),
    PAGE_DIFF( "diff", "%ucmd.diff?page=%n", "DiffContent.jsp" ),
    PAGE_EDIT( "edit", "%ucmd.edit?page=%n", "EditContent.jsp" ),
    PAGE_INFO( "info", "%ucmd.info?page=%n", "InfoContent.jsp" ),
    PAGE_NONE( "", "%u%n", null ),
    PAGE_PREVIEW( "preview", "%ucmd.preview?page=%n", "PreviewContent.jsp" ),
    PAGE_RENAME( "rename", "%ucmd.rename?page=%n", "InfoContent.jsp" ),
    PAGE_RSS( "rss", "%urss.jsp", null ),
    PAGE_UPLOAD( "upload", "%ucmd.upload?page=%n", "AttachmentTab.jsp" ),
    PAGE_VIEW( "view", "%ucmd.view?pageId=%n", "PageContent.jsp" ),

    REDIRECT( "", "%u%n", null ),

    WIKI_ADMIN( "admin", "%uadmin/Admin.jsp", "AdminContent.jsp" ),
    WIKI_CREATE_GROUP( "createGroup", "%ucmd.createGroup", "NewGroupContent.jsp" ),
    WIKI_ERROR( "error", "%uError.jsp", "DisplayMessage.jsp" ),
    WIKI_FIND( "find", "%ucmd.find", "FindContent.jsp" ),
    WIKI_INSTALL( "install", "%uInstall.jsp", null ),
    WIKI_LOGIN( "login", "%ucmd.login?redirect=%n", "LoginContent.jsp" ),
    WIKI_LOGOUT( "logout", "%ucmd.logout", null ),
    WIKI_MESSAGE( "message", "%uMessage.jsp", "DisplayMessage.jsp" ),
    WIKI_PREFS( "prefs", "%ucmd.prefs", "PreferencesContent.jsp" ),
    WIKI_PREFS_RAP( "prefsRap", "%ucmd.prefsRap", "PreferencesContentRap.jsp" ),
    WIKI_WORKFLOW( "workflow", "%uWorkflow.jsp", "WorkflowContent.jsp" );
	//@formatter:on

	private final String requestContext;
	private final String urlPattern;
	private final String contentTemplate;

	ContextEnum(final String requestContext, final String urlPattern, final String contentTemplate) {
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
