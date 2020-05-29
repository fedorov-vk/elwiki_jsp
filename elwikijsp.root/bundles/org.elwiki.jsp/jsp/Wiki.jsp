<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<%@ page import="java.util.Date" %>
<%@ page import="org.apache.commons.lang3.time.StopWatch" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.WatchDog" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.AuthorizationManager" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.ui.TemplateManager" %>
<%@ page import="org.apache.wiki.util.*" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>

<%!
    Logger log = Logger.getLogger("JSPWiki");
%>

<%
    Engine wiki = Wiki.engine().find( getServletConfig() );
%>

<!doctype html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>OSGi - Sample Web Application</title>
</head>
<body>

	<div style="text-align: center; margin: 50px auto;">
		<a href="get-data">
			<img src="avatar.jpg" alt="My avatar" width=200/>
		</a>

<p>
Текущее время: <%= new java.util.Date () %>

		<h2>
			<% out.println( "It works.\n (org.elwiki.jsp/jsp/Wiki.jsp)" ); %>
		</h2>

	</div>
</body>
</html>
