<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>OSGi - Sample Web Application</title>
</head>
<body>
<!-- :FVK: это отладка при разработке. -->
	<div style="text-align: center; margin: 50px auto;">
		<h2>
			<%
			out.println("It works. :path: <br/>(from \"/page\")");
			%>
		</h2>
	</div>
</body>
</html>