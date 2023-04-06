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
	GROUP_VIEW( "cmd.viewGroup", "%u", "?group=%n", "PreferencesContent.jsp" ),
	GROUP_CREATE( "cmd.createGroup", "%u", "", "NewGroupContent.jsp" ),
	GROUP_EDIT( "cmd.editGroup", "%u", "?id=%n", "EditGroupContent.jsp" ),
    GROUP_DELETE( "cmd.deleteGroup", "%u", "?group=%n", null ),

    PAGE_VIEW( "cmd.view", "%u", "?pageId=%n", "PageContent.jsp" ),
    PAGE_PREVIEW( "cmd.preview", "%u", "?pageId=%n", "PreviewContent.jsp" ),
    PAGE_CREATE( "cmd.createPage", "%u", "?pageId=%n", "CreatePageContent.jsp" ),
    PAGE_EDIT( "cmd.edit", "%u", "?pageId=%n", "EditContent.jsp" ),
    PAGE_ACL( "cmd.pageAcl", "%u", "?pageId=%n", "AclPageContent.jsp" ),
    PAGE_EDIT_ACL( "cmd.editAcl", "%u", "?pageId=%n", "AclEditContent.jsp" ),
    PAGE_DELETE( "cmd.deletePage", "%u", "?pageId=%n", null ),
    PAGE_INFO( "cmd.info", "%u", "?pageId=%n", "InfoContent.jsp" ),
    PAGE_COMMENT( "cmd.comment", "%u", "?pageId=%n", "CommentContent.jsp" ),
    PAGE_CONFLICT( "PageModified.jsp", "%u", "?page=%n", "ConflictContent.jsp" ),
    PAGE_DIFF( "cmd.diff", "%u", "?pageId=%n", "InfoContent.jsp" ),
    PAGE_NONE( "", "%u", "%n", null ),
    PAGE_RENAME( "cmd.rename", "%u", "?pageId=%n", "InfoContent.jsp" ),
    PAGE_RSS( "rss.jsp", "%u", "", null ),

    @Deprecated
    ATTACHMENT_UPLOAD( "cmd.upload", "%u", "?pageId=%n", "AttachmentTab.jsp" ),
    ATTACHMENT_DOGET( "attach", "%u", "/%n", null ),
    ATTACHMENT_DOPOST( "attach", "%u", "/%n", null ),
    ATTACHMENT_INFO( "cmd.infoAttachment", "%u", "?id=%n", "InfoAttachmentContent.jsp" ),
    ATTACHMENT_DELETE( "cmd.deleteAttachment", "%u", "?pageId=%n", null ),

    REDIRECT( "", "%u", "%n", null ),

    WIKI_ADMIN( "cmd.admin", "%u", "", "AdminContent.jsp" ),
    WIKI_SECURE( "cmd.secure", "%u", "", "SecurityConfig.jsp" ),
    WIKI_ERROR( "Error.jsp", "%u", "", "DisplayMessage.jsp" ),
    WIKI_FIND( "cmd.find", "%u", "", "FindContent.jsp" ),
    WIKI_INSTALL( "Install.jsp", "%u", "", null ),
    WIKI_LOGIN( "cmd.login", "%u", "?redirect=%n", "LoginContent.jsp" ),
    WIKI_LOSTPASSWORD( "cmd.lostpassword", "%u", "?redirect=%n", "LoginContent.jsp" ),
    WIKI_LOGOUT( "cmd.logout", "%u", "", null ),
    WIKI_MESSAGE( "cmd.message", "%u", "?redirect=%n", "MessageContent.jsp" ),
    WIKI_PREFS( "cmd.prefs", "%u", "", "PreferencesContent.jsp" ),
    WIKI_WORKFLOW( "cmd.workflow", "%u", "", "WorkflowContent.jsp" ),
    WIKI_SCOPE( "cmd.scope", "%u", "", "ScopeContent.jsp" ),
	WIKI_PERSIST_CONTENT( "cmd.persistContent", "%u", "",
			// :FVK: workaround, this contentTemplate should be null,
			// but error expected:
			//   at org.apache.wiki.tags.ContentTag.doEndTag(ContentTag.java:160)
			// when saved wiki content under admin.
			// Using "PageContent.jsp" - temporary fiction
			"PageContent.jsp" ); 
	//@formatter:on

	private final String urlPattern;
	private final String uri;
	private final String contentTemplate;

	/**
	 * Creates a variable that describes the context of ElWiki.
	 *
	 * @param uri             this string is the URI for activating this context from an HTTP request.
	 * @param prefix          this is the prefix for forming the request URI.
	 * @param postfix         this is the postfix for forming the request URI.
	 * @param contentTemplate the name of the JSP page, for generating HTML content.
	 */
	ContextEnum(String uri, String prefix, String postfix, String contentTemplate) {
		this.urlPattern = prefix + uri + postfix;
		this.uri = uri;
		this.contentTemplate = contentTemplate;
	}

	public String getRequestContext() {
		String name = this.name();
		return name;
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

	/**
	 * Returns URI template by name of wiki context.
	 * 
	 * @param contextName Required name of wiki context.
	 * @return URI template according to the wiki context.
	 * @throws Exception if the contextName is invalid.
	 */
	static String getUriTemplate(String contextName) throws Exception {
		ContextEnum contextEnum = null;
		try {
			contextEnum = ContextEnum.valueOf(contextName);
			return contextEnum.getUrlPattern();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public static String getWikiContextName(String uri) {
		String result = WikiContext.PAGE_VIEW; // default context if uri is not relevant. 
		for (ContextEnum item : ContextEnum.values()) {
			if(item.getUri().equals(uri)) {
				result = item.name();
				break;
			}
		}
		return result;
	}

}
