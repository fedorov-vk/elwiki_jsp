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

<?xml version="1.0" encoding="UTF-8"?>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="net.sf.ehcache.Cache" %>
<%@ page import="net.sf.ehcache.Element" %>
<%@ page import="net.sf.ehcache.CacheManager" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.WatchDog" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.elwiki.plugins.main.WeblogPlugin %>
<%@ page import="org.apache.wiki.pages0.PageManager" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.rss.*" %>
<%@ page import="org.apache.wiki.util.*" %>

<%!
    private Logger log = Logger.getLogger("JSPWiki");
    private CacheManager m_cacheManager = CacheManager.getInstance();
    private String cacheName = "jspwiki.rssCache";
    private Cache m_rssCache;
    private int m_expiryPeriod = 24*60*60;
    private int cacheCapacity = 1000;
%>

<%
if (m_cacheManager.cacheExists(cacheName)) {
        m_rssCache = m_cacheManager.getCache(cacheName);
    } else {
        log.info("cache with name " + cacheName +  " not found in ehcache.xml, creating it with defaults.");
        m_rssCache = new Cache(cacheName, cacheCapacity, false, false, m_expiryPeriod, m_expiryPeriod);
        m_cacheManager.addCache(m_rssCache);
    }
    Engine wiki = Wiki.engine().find( getServletConfig() );
    // Create wiki context and check for authorization
    WikiContext wikiContext = Wiki.context().create( wiki, request, ContextEnum.PAGE_RSS.getRequestContext() );
    if(!WikiEngine.getAuthorizationManager().hasAccess( wikiContext, response ) ) return;
    WikiPage wikipage = wikiContext.getPage();

    // Redirect if RSS generation not on
    if( WikiEngine.getRssGenerator() == null ) {
        response.sendError( 404, "RSS feeds are disabled at administrator request" );
        return;
    }

    if( wikipage == null || !WikiEngine.getPageManager().pageExistsByName( wikipage.getName() ) ) {
        response.sendError( 404, "No such page " + wikipage.getName() );
        return;
    }

    WatchDog w = WatchDog.getCurrentWatchDog( wiki );
    w.enterState("Generating RSS",60);
    
    // Set the mode and type for the feed
    String      mode        = request.getParameter("mode");
    String      type        = request.getParameter("type");
    
    if( mode == null || !(mode.equals(RssGenerator.MODE_BLOG) || mode.equals(RssGenerator.MODE_WIKI)) ) {
    	   mode = RssGenerator.MODE_BLOG;
    }
    if( type == null || !(type.equals(RssGenerator.RSS10) || type.equals(RssGenerator.RSS20) || type.equals(RssGenerator.ATOM ) ) ) {
    	   type = RssGenerator.RSS20;
    }
    // Set the content type and include the response content
    response.setContentType( RssGenerator.getContentType(type)+"; charset=UTF-8");

    StringBuffer result = new StringBuffer();
    SimpleDateFormat iso8601fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    Properties properties = wiki.getWikiConfiguration().getWikiPreferences();
    String channelDescription = TextUtil.getRequiredProperty( properties, RssGenerator.PROP_CHANNEL_DESCRIPTION );
    String channelLanguage    = TextUtil.getRequiredProperty( properties, RssGenerator.PROP_CHANNEL_LANGUAGE );

    //
    //  Now, list items.
    //
    List< WikiPage > changed;
    
    if( "blog".equals( mode ) ) {
        WeblogPlugin plug = new WeblogPlugin();
        changed = plug.findBlogEntries( wiki, wikipage.getName(), new Date(0L), new Date() );
    } else {
        changed = WikiEngine.getPageManager().getVersionHistory( wikipage.getName() );
    }
    
    //
    //  Check if nothing has changed, so we can just return a 304
    //
    boolean hasChanged = false;
    Date    latest     = new Date(0);

    for( Iterator< WikiPage > i = changed.iterator(); i.hasNext(); ) {
        WikiPage p = i.next();

        if( !HttpUtil.checkFor304( request, p.getName(), p.getLastModifiedDate() ) ) hasChanged = true;
        if( p.getLastModifiedDate().after( latest ) ) latest = p.getLastModifiedDate();
    }

    if( !hasChanged && changed.size() > 0 ) {
        response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        w.exitState();
        return;
    }

    response.addDateHeader("Last-Modified",latest.getTime());
    response.addHeader("ETag", HttpUtil.createETag( wikipage.getName(), wikipage.getLastModifiedDate() ) );
    
    //
    //  Try to get the RSS XML from the cache.  We build the hashkey
    //  based on the LastModified-date, so whenever it changes, so does
    //  the hashkey so we don't have to make any special modifications.
    //
    //  TODO: Figure out if it would be a good idea to use a disk-based cache here.
    //
    String hashKey = wikipage.getName()+";"+mode+";"+type+";"+latest.getTime();
    
    String rss = "";

    Element element = m_rssCache.get(hashKey);
    if (element != null) {
      rss = (String) element.getObjectValue();
    } else { 
        rss = WikiEngine.getRssGenerator().generateFeed( wikiContext, changed, mode, type );
        m_rssCache.put( new Element( hashKey, rss ) );
    }
    
    out.println( rss );
    
    w.exitState();
%>