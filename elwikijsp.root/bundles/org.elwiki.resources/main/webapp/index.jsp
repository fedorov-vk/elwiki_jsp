<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %>
<%@ page import="java.util.Date" %>
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
			<% out.println( "It works.\n (org.elwiki.jsp/jsp/)" ); %>
		</h2>
		<c:out value="16+64*2" />
	</div>
</body>
</html>
