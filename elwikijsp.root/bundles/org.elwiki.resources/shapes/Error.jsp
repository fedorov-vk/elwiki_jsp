<%@ page isErrorPage="false" language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<%@ page import="java.io.*"%>
<%@ page import="org.apache.log4j.*"%>
<%@ page import="org.apache.wiki.api.core.*"%>
<%@ page import="org.apache.wiki.api.core.ContextEnum"%>
<%@ page import="org.apache.wiki.api.core.Engine"%>
<%@ page import="org.apache.wiki.Wiki"%>
<%@ page import="org.apache.wiki.util.FileUtil"%>
<%!Logger log;

	public void jspInit() {
		log = Logger.getLogger("shapes/Error_jsp");
	}%>
<%
// workaround. Avoid problem - Eclipse Equinox mechanism does not initialise Attribute "javax.servlet.error.status_code". 
Throwable exception0 = org.apache.jasper.runtime.JspRuntimeLibrary.getThrowable(request);
request.setAttribute("elwiki.jsp.error.exception", exception0); //:FVK: workaround?
log.debug("Error.jsp caught:", exception0);

Throwable realcause = null;
String msg = exception0.getMessage();
if (msg == null || msg.length() == 0) {
	msg = "An unknown exception " + exception0.getClass().getName() + " was caught by Error.jsp.";
}
//
//  This allows us to get the actual cause of the exception.
//  Note the cast; at least Tomcat has two classes called "JspException" imported in JSP pages.
//
if (exception0 instanceof javax.servlet.jsp.JspException) {
	log.debug("IS JSPEXCEPTION");
	realcause = ((javax.servlet.jsp.JspException) exception0).getCause();
	log.debug("REALCAUSE=" + realcause);
} else {
	realcause = exception0;
}
%>
<!DOCTYPE html>
<html>
<head>
<title>Error page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
  <div style="margin:10pt;">
	<h3>ElWiki has detected an error</h3>

	<table>
		<tr>
			<td align='right'>Error Message:</td>
			<td><span
				style="background-color: #f2dede; border-color: #ebccd1; color: #a94442; border-radius: 4px; padding: 3px;">
					<%=realcause.getMessage()%>
			</span></td>
		</tr>
		<tr>
			<td align='right'>Exception:</td>
			<td><code><%=realcause.getClass().getName()%></code></td>
		</tr>
		<tr>
			<td align='right'>Place where detected:</td>
			<td><code><%=FileUtil.getThrowingMethod(realcause)%></code></td>
		</tr>
	</table>

	<p>
		If you have changed the shapes, please do check them. This error
		message may show up because of that. If you have not changed them, and
		you are either installing JSPWiki for the first time or have changed
		configuration, then you might want to check your configuration files.
		If you are absolutely sure that JSPWiki was running quite okay or you
		can't figure out what is going on, then by all means, come over to <a
			href="http://jspwiki.apache.org/">jspwiki.apache.org</a> and tell us.
		There is more information in the log file (like the full stack trace,
		which you should add to any error report).
	</p>
	<p>And don't worry - it's just a computer program. Nothing really
		serious is probably going on: at worst you can lose a few nights
		sleep. It's not like it's the end of the world.</p>

	<button onclick="history.back()"
		style="border-radius: 4px; padding: 4px;">Back to Previous
		Page</button>
  </div>
</body>
</html>