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
 * Every enumeration contains 5 strings:
 * <ol>
 * <li>request context ID
 * <li>request URI
 * <li>prefix of URL pattern
 * <li>postfix of URL pattern
 * <li>JSP file name from the templates
 * </ol>
 */
public enum ContextEnum {

	//@formatter:off
    GROUP_DELETE( "deleteGroup", "cmd.deleteGroup", "%u", "?group=%n", null ),
    GROUP_EDIT( "editGroup", "cmd.editGroup", "%u", "?group=%n", "EditGroupContent.jsp" ),
    GROUP_VIEW( "viewGroup", "cmd.viewGroup", "%u", "?group=%n", "PreferencesContent.jsp" ),

    PAGE_ATTACH( "att", "attach", "%u", "/%n", null ),
    PAGE_COMMENT( "comment", "cmd.comment", "%u", "?pageId=%n", "CommentContent.jsp" ),
    PAGE_CONFLICT( "conflict", "PageModified.jsp", "%u", "?page=%n", "ConflictContent.jsp" ),
    PAGE_CREATE( "createPage", "cmd.createPage", "%u", "?pageId=%n", "CreatePageContent.jsp" ),
    PAGE_DELETE( "deletePage", "cmd.deletePage", "%u", "?pageId=%n", null ),
    PAGE_DIFF( "diff", "cmd.diff", "%u", "?pageId=%n", "DiffContent.jsp" ),
    PAGE_EDIT( "edit", "cmd.edit", "%u", "?pageId=%n", "EditContent.jsp" ),
    PAGE_INFO( "info", "cmd.info", "%u", "?pageId=%n", "InfoContent.jsp" ),
    PAGE_NONE( "", "", "%u", "%n", null ),
    PAGE_PREVIEW( "preview", "cmd.preview", "%u", "?pageId=%n", "PreviewContent.jsp" ),
    PAGE_RENAME( "rename", "cmd.rename", "%u", "?pageId=%n", "InfoContent.jsp" ),
    PAGE_RSS( "rss", "rss.jsp", "%u", "", null ),
    PAGE_UPLOAD( "upload", "cmd.upload", "%u", "?pageId=%n", "AttachmentTab.jsp" ),
    PAGE_VIEW( "view", "cmd.view", "%u", "?pageId=%n", "PageContent.jsp" ),

    REDIRECT( "", "", "%u", "%n", null ),

    WIKI_ADMIN( "admin", "admin/Admin.jsp", "%u", "", "AdminContent.jsp" ),
    WIKI_CREATE_GROUP( "createGroup", "cmd.createGroup", "%u", "", "NewGroupContent.jsp" ),
    WIKI_ERROR( "error", "Error.jsp", "%u", "", "DisplayMessage.jsp" ),
    WIKI_FIND( "find", "cmd.find", "%u", "", "FindContent.jsp" ),
    WIKI_INSTALL( "install", "Install.jsp", "%u", "", null ),
    WIKI_LOGIN( "login", "cmd.login", "%u", "?redirect=%n", "LoginContent.jsp" ),
    WIKI_LOGOUT( "logout", "cmd.logout", "%u", "", null ),
    WIKI_MESSAGE( "message", "Message.jsp", "%u", "", "DisplayMessage.jsp" ),
    WIKI_PREFS( "prefs", "cmd.prefs", "%u", "", "PreferencesContent.jsp" ),
    WIKI_WORKFLOW( "workflow", "Workflow.jsp", "%u", "", "WorkflowContent.jsp" ),
    WIKI_SCOPE( "scope", "cmd.scope", "%u", "", "ScopeContent.jsp" ),
	WIKI_PERSIST_CONTENT( "persistContent", "cmd.persistContent", "%u", "",
			// :FVK: workaround, this contentTemplate should be null,
			// but error expected:
			//   at org.apache.wiki.tags.ContentTag.doEndTag(ContentTag.java:160)
			// when saved wiki content under admin.
			// Using "PageContent.jsp" - temporary fiction
			"PageContent.jsp" ); 
	//@formatter:on

	private final String requestContext;
	private final String urlPattern;
	private final String uri;
	private final String contentTemplate;

	/**
	 * Creates a variable that describes the context of ElWiki.
	 *
	 * @param requestContext this string represents the context identifier.
	 * @param uri this string is the URI for activating this context from an HTTP request.
	 * @param prefix this is the prefix for forming the request URI.
	 * @param postfix this is the postfix for forming the request URI.
	 * @param contentTemplate the name of the JSP page, for generating HTML content.
	 */
	ContextEnum(String requestContext, String uri, String prefix, String postfix, String contentTemplate) {
		this.requestContext = requestContext;
		this.urlPattern = prefix + uri + postfix;
		this.uri = uri;
		this.contentTemplate = contentTemplate;
	}

	public String getRequestContext() {
		return requestContext;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public String getUri() {
		return uri;
	}
	
	public String getContentTemplate() {
		return contentTemplate;
	}

}
