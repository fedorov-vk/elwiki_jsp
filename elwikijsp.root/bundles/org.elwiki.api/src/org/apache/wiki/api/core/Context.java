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

import javax.servlet.http.HttpServletRequest;

import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki_data.WikiPage;

import java.security.Principal;
import java.util.Map;


/**
 *  <p>Provides state information throughout the processing of a page.  A Context is born when the JSP pages that are the main entry
 *  points, are invoked.  The JSPWiki engine creates the new Context, which basically holds information about the page, the
 *  handling engine, and in which context (view, edit, etc) the call was done.</p>
 *  <p>A Context also provides request-specific variables, which can be used to communicate between plugins on the same page, or
 *  between different instances of the same plugin. A Context variable is valid until the processing of the WikiPage has ended. For
 *  an example, please see the Counter plugin.</p>
 *  <p>When a Context is created, it automatically associates a {@link Session} object with the user's
 *  HttpSession. The Session contains information about the user's authentication status, and is consulted by {@link #getCurrentUser()}
 *  object.</p>
 *  <p>Do not cache the WikiPage object that you get from the WikiContext; always use getPage()!</p>
 *
 *  @see org.apache.wiki.plugin.Counter
 */
public interface Context extends Cloneable, Command {

    /** User is doing administrative things. */
    public static final String ADMIN = ContextEnum.WIKI_ADMIN.getRequestContext();

    /** User is downloading an attachment. */
    public static final String ATTACH = ContextEnum.PAGE_ATTACH.getRequestContext();

    /** User is commenting something. */
    public static final String COMMENT = ContextEnum.PAGE_COMMENT.getRequestContext();

    /** User has an internal conflict, and does quite not know what to do. Please provide some counseling. */
    public static final String CONFLICT = ContextEnum.PAGE_CONFLICT.getRequestContext();

    /** User wishes to create a new group */
    public static final String CREATE_GROUP = ContextEnum.WIKI_CREATE_GROUP.getRequestContext();

    /** User is deleting a page or an attachment. */
    public static final String DELETE = ContextEnum.PAGE_DELETE.getRequestContext();

    /** User is deleting an existing group. */
    public static final String DELETE_GROUP = ContextEnum.GROUP_DELETE.getRequestContext();

    /** User is viewing a DIFF between the two versions of the page. */
    public static final String DIFF = ContextEnum.PAGE_DIFF.getRequestContext();

    /** The EDIT context - the user is editing the page. */
    public static final String EDIT = ContextEnum.PAGE_EDIT.getRequestContext();

    /** User is editing an existing group. */
    public static final String EDIT_GROUP = ContextEnum.GROUP_EDIT.getRequestContext();

    /** An error has been encountered and the user needs to be informed. */
    public static final String ERROR = ContextEnum.WIKI_ERROR.getRequestContext();

    /** User is searching for content. */
    public static final String FIND = ContextEnum.WIKI_FIND.getRequestContext();

    /** User is viewing page history. */
    public static final String INFO = ContextEnum.PAGE_INFO.getRequestContext();

    /** User is administering JSPWiki (Install, SecurityConfig). */
    public static final String INSTALL = ContextEnum.WIKI_INSTALL.getRequestContext();

    /** User is preparing for a login/authentication. */
    public static final String LOGIN = ContextEnum.WIKI_LOGIN.getRequestContext();

    /** User is preparing to log out. */
    public static final String LOGOUT = ContextEnum.WIKI_LOGOUT.getRequestContext();

    /** JSPWiki wants to display a message. */
    public static final String MESSAGE = ContextEnum.WIKI_MESSAGE.getRequestContext();

    /** This is not a JSPWiki context, use it to access static files. */
    public static final String NONE = ContextEnum.PAGE_NONE.getRequestContext();

    /** Same as NONE; this is just a clarification. */
    public static final String OTHER = ContextEnum.PAGE_NONE.getRequestContext();

    /** User is editing preferences */
    public static final String PREFS = ContextEnum.WIKI_PREFS.getRequestContext();

    /** User is previewing the changes he just made. */
    public static final String PREVIEW = ContextEnum.PAGE_PREVIEW.getRequestContext();

    /** User is renaming a page. */
    public static final String RENAME = ContextEnum.PAGE_RENAME.getRequestContext();

    /** RSS feed is being generated. */
    public static final String RSS = ContextEnum.PAGE_RSS.getRequestContext();

    /** User is uploading something. */
    public static final String UPLOAD = ContextEnum.PAGE_UPLOAD.getRequestContext();

    /** The VIEW context - the user just wants to view the page contents. */
    public static final String VIEW = ContextEnum.PAGE_VIEW.getRequestContext();

    /** User is viewing an existing group */
    public static final String VIEW_GROUP = ContextEnum.GROUP_VIEW.getRequestContext();

    /** User wants to view or administer workflows. */
    public static final String WORKFLOW = ContextEnum.WIKI_WORKFLOW.getRequestContext();

    /** Persisting wiki content. */
    public static final String PERSIST_CONTENT = ContextEnum.WIKI_PERSIST_CONTENT.getRequestContext();

    /** Delete all version of attached file. */
    public static final String ATTACHMENT_DELETE = ContextEnum.ATTACHMENT_DELETE.getRequestContext();

    /** Inspect all versions of attached file. */
    public static final String ATTACHMENT_INFO = ContextEnum.ATTACHMENT_INFO.getRequestContext();

    /**
     *  Is used to choose between the different date formats that JSPWiki supports.
     *  <ul>
     *   <li>TIME: A time format, without  date</li>
     *   <li>DATE: A date format, without a time</li>
     *   <li>DATETIME: A date format, with a time</li>
     *  </ul>
     *
     *  @since 2.8
     */
    public enum TimeFormat {
        /** A time format, no date. */
        TIME,

        /** A date format, no time. */
        DATE,

        /** A date+time format. */
        DATETIME
    }
	
    String ATTR_WIKI_CONTEXT = "jspwiki.context";

    /**
     *  Variable name which tells whether plugins should be executed or not. Value can be either {@code Boolean.TRUE} or
     *  {@code Boolean.FALSE}. While not set it's value is {@code null}.
     */
    String VAR_EXECUTE_PLUGINS = "_PluginContent.execute";

    /** Name of the variable which is set to Boolean.TRUE or Boolean.FALSE depending on whether WYSIWYG is currently in effect. */
    String VAR_WYSIWYG_EDITOR_MODE = "WYSIWYG_EDITOR_MODE";
    
    /**
     *  Returns the WikiPage that is being handled.
     *
     *  @return the WikiPage which was fetched.
     */
    WikiPage getPage();

	/**
	 * Returns the ID of WikiPage that is being handled.
	 *
	 * @return the ID of WikiPage which was fetched. In case of a problem with the
	 *         current page - returns or ID of 'main' page of wiki, or 'SystemInfo'
	 *         page.
	 */
	String getPageId();

    /**
     *  Sets the WikiPage that is being handled.
     *
     *  @param wikiPage The wikipage
     *  @since 2.1.37.
     */
    void setPage( WikiPage wikiPage );
    
    /**
     *  Gets a reference to the real WikiPage whose content is currently being rendered. If your plugin e.g. does some variable setting, be
     *  aware that if it is embedded in the LeftMenu or some other WikiPage added with InsertPageTag, you should consider what you want to
     *  do - do you wish to really reference the "master" WikiPage or the included page.
     *  <p>
     *  For example, in the default template, there is a WikiPage called "LeftMenu". Whenever you access a page, e.g. "Main", the master
     *  WikiPage will be Main, and that's what the getPage() will return - regardless of whether your plugin resides on the LeftMenu or on
     *  the Main page.  However, getRealPage() will return "LeftMenu".
     *
     *  @return A reference to the real page.
     *  @see org.apache.wiki.tags.InsertPageTag
     *  @see org.apache.wiki.parser.JSPWikiMarkupParser
     */
    WikiPage getRealPage();

    /**
     *  Sets a reference to the real WikiPage whose content is currently being rendered.
     *  <p>
     *  Sometimes you may want to render the WikiPage using some other page's context. In those cases, it is highly recommended that you set
     *  the setRealPage() to point at the real WikiPage you are rendering.  Please see InsertPageTag for an example.
     *  <p>
     *  Also, if your plugin e.g. does some variable setting, be aware that if it is embedded in the LeftMenu or some other WikiPage added
     *  with InsertPageTag, you should consider what you want to do - do you wish to really reference the "master" WikiPage or the included
     *  page.
     *
     *  @param wikiPage  The real WikiPage which is being rendered.
     *  @return The previous real page
     *  @since 2.3.14
     *  @see org.apache.wiki.tags.InsertPageTag
     */
    WikiPage setRealPage( WikiPage wikiPage );

	/**
	 * Returns WikiPage by specified pageId.
	 * 
	 * @param pageId
	 *            Page identifier for find page.
	 * 
	 * @return WikiPage page, paossibly <code>null</code>, if the page is not found for any reason.
	 */
	WikiPage getPageById(String pageId);
    
	/**
	 * Returns the wiki configuration.
	 * 
	 * @return The wiki configuration.
	 */
	IWikiConfiguration getConfiguration();
    
    /**
     *  Returns the handling engine.
     *
     *  @return The wikiengine owning this context.
     */
    Engine getEngine();

    /**
     *  Sets the request context.  See above for the different request contexts (VIEW, EDIT, etc.)
     *
     *  @param context The request context (one of the predefined contexts.)
     */
    void setRequestContext( String context );

    /**
     *  Gets a previously set variable.
     *
     *  @param key The variable name.
     *  @return The variable contents.
     */
    < T > T getVariable( String key );

    /**
     *  Sets a variable.  The variable is valid while the WikiContext is valid, i.e. while WikiPage processing continues.  The variable data
     *  is discarded once the WikiPage processing is finished.
     *
     *  @param key The variable name.
     *  @param data The variable value.
     */
    void setVariable( String key, Object data );

    /**
     * This is just a simple helper method which will first check the context if there is already an override in place, and if there is not,
     * it will then check the given properties.
     *
     * @param key What key are we searching for?
     * @param defValue Default value for the boolean
     * @return {@code true} or {@code false}.
     */
    boolean getBooleanWikiProperty( String key, boolean defValue );

    /**
     *  This method will safely return any HTTP parameters that might have been defined.  You should use this method instead
     *  of peeking directly into the result of getHttpRequest(), since this method is smart enough to do all of the right things,
     *  figure out UTF-8 encoded parameters, etc.
     *
     *  @since 2.0.13.
     *  @param paramName Parameter name to look for.
     *  @return HTTP parameter, or null, if no such parameter existed.
     */
    String getHttpParameter( String paramName );

    /**
     *  If the request did originate from a HTTP request, then the HTTP request can be fetched here.  However, it the request
     *  did NOT originate from a HTTP request, then this method will return null, and YOU SHOULD CHECK FOR IT!
     *
     *  @return Null, if no HTTP request was done.
     *  @since 2.0.13.
     */
    HttpServletRequest getHttpRequest();

    /**
     *  Sets the template to be used for this request.
     *
     *  @param dir The template name
     *  @since 2.1.15.
     */
    void setTemplate( String dir );

    /**
     *  Gets the template that is to be used throughout this request.
     *
     *  @since 2.1.15.
     *  @return template name
     */
    String getTemplate();

    /**
     *  Returns the Session associated with the context. This method is guaranteed to always return a valid Session.
     *  If this context was constructed without an associated HttpServletRequest, it will return a guest session.
     *
     *  @return The Session associate with this context.
     */
    Session getWikiSession();

    /**
     *  Convenience method that gets the current user. Delegates the lookup to the Session associated with this Context.
     *  May return null, in case the current user has not yet been determined; or this is an internal system. If the Session has not
     *  been set, <em>always</em> returns null.
     *
     *  @return The current user; or maybe null in case of internal calls.
     */
    Principal getCurrentUser();

    /**
     *  Returns true, if the current user has administrative permissions (i.e. the omnipotent AllPermission).
     *
     *  @since 2.4.46
     *  @return true, if the user has all permissions.
     */
    boolean hasAdminPermissions();

    /**
     *  A shortcut to generate a VIEW url.
     *
     *  @param pageId The string to which to link.
     *  @return An URL to the page.  This honours the current absolute/relative setting.
     */
    default String getViewURL( String pageId )  {
        return getURL( Context.VIEW, pageId, null );
    }

    /**
     *  Figure out to which WikiPage we are really going to.  Considers special WikiPage names from the jspwiki.properties, and possible aliases.
     *
     *  @return A complete URL to the new WikiPage to redirect to
     *  @since 2.2
     */
    String getRedirectURL();

    /**
     * Returns the Command associated with this Context.
     *
     * @return the command
     */
    Command getCommand();

    /**
     *  Creates an URL for the given request context.
     *
     *  @param context e.g. WikiContext.EDIT
     *  @param page The WikiPage to which to link
     *  @return An URL to the page.
     */
    default String getURL( final String context, final String page ) {
        return getURL( context, page, null );
    }

    /**
     *  Returns an URL from a page. It this Context instance was constructed with an actual HttpServletRequest, we will attempt to
     *  construct the URL using HttpUtil, which preserves the HTTPS portion if it was used.
     *
     *  @param context The request context (e.g. WikiContext.UPLOAD)
     *  @param page The WikiPage to which to link
     *  @param params A list of parameters, separated with "&amp;"
     *
     *  @return An URL to the given context and page.
     */
    default String getURL( final String context, final String page, final String params ) {
        // FIXME: is rather slow
        return getEngine().getURL( context, page, params );
    }

    /** {@inheritDoc} */
    Context clone();

    /**
     *  Creates a deep clone of the Context. This is useful when you want to be sure that you don't accidentally mess with page
     *  attributes, etc.
     *
     *  @since  2.8.0
     *  @return A deep clone of the Context.
     */
    Context deepClone();

}
