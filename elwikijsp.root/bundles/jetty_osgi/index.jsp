<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c"%>
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>OSGi - Sample Web Application</title>
</head>
<body>
	<div style="text-align: center; margin: 50px auto;">
		<a href="get-data"> <img src="avatar.jpg" alt="My avatar" />
		</a>
		<h2>
			<%
			out.println("It works.\n(basic page)");
			%>
		</h2>
		<c:out value="16+64*2" />
	</div>
</body>
</html>
