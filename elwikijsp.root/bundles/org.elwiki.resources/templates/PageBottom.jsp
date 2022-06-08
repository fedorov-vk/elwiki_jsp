<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- ~~ START ~~ PageBottom.jsp --><%@
 page import="org.apache.wiki.*" %><%@
 taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>

  <%@ include file="/templates/default/Footer.jsp" %>
</div>

</body>
</html>

<!-- ~~ END ~~ PageBottom.jsp -->