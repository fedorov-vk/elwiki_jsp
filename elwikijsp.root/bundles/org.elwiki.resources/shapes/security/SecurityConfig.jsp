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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="/shapes/Error.jsp" %>
<%@ page import="java.security.Principal" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="org.apache.wiki.api.core.*" %>
<%@ page import="org.apache.wiki.Wiki" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.apache.wiki.preferences.Preferences" %>
<%@ page import="org.apache.wiki.util.TextUtil" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<fmt:setLocale value="${prefs.Language}" />
<%!
	Logger log;
	public void jspInit()
	{
		Logger log = Logger.getLogger("SecurityConfig_jsp");
	}
%>
<!doctype html>
<html lang="<c:out value='${prefs.Language}' default='en'/>">
<%
	WikiContext wikiContext = ContextUtil.findContext( pageContext );
	Engine engine = wikiContext.getEngine();
	AuthorizationManager authorizationManager = engine.getManager(AuthorizationManager.class);

	//:FVK: ?? WikiContext wikiContext = Wiki.context().create( wiki, request, WikiContext.PAGE_NONE );
	if(!authorizationManager.hasAccess( wikiContext, response )) {
		return;
	}
	SecurityVerifier verifier = new SecurityVerifier(engine, wikiContext.getWikiSession());
	response.setContentType("text/html; charset="+engine.getContentEncoding() ); 
%>

<head>
  <title><wiki:Variable var="applicationname" />: ElWiki Security Configuration Verifier</title>
  <base href="../"/>
  <!--:FVK: is deprecated? Because "jspwiki.css" has been seen in the 210 template. 
  <link rel="stylesheet" media="screen, projection" type="text/css" href="<wiki:Link format="url" templatefile="jspwiki.css"/>"/>
   -->
</head>
<body>
<div id="wikibody" class="container-fixed">
<div id="page">
<div id="pagecontent">

<h1>JSPWiki Security Configuration Verifier</h1>

<p>Эта страница проверяет конфигурацию безопасности ElWiki и пытается определить, работает ли она так, как должна.
Хотя ElWiki поставляется с некоторыми разумными настройками конфигурации по умолчанию из коробки,
 не всегда очевидно, какие настройки следует изменить, если вам нужно настроить безопасность...
 и рано или поздно это случается почти с каждым.</p>

<p>Эта страница динамически генерируется ElWiki.
В ней рассматриваются параметры аутентификации, авторизации и политики безопасности.
Когда нам покажется, что что-то выглядит забавно, мы попытаемся сообщить,
 в чем может заключаться проблема, и дадим рекомендации по ее устранению.</p>

<blockquote>
<p>Пожалуйста, удалите этот JSP, когда вы закончите устранение неполадок в вашей системе.
Эти диагностические данные, представленные на странице,
 сами по себе не представляют угрозы безопасности вашей системы,
 но они предоставляют значительный объем контекстной информации,
 которая может быть полезна злоумышленнику.
В настоящее время эта страница не ограничена, что означает, что ее может просматривать любой желающий:
 приятные люди, злые люди и все, кто находится между ними.
Вы были предупреждены. Следует отключить её...</p>
</blockquote>

<!--
  *********************************************
  **** A U T H E N T I C A T I O N         ****
  *********************************************
-->
<h2>Конфигурация аутентификации</h2>
<!--
  *********************************************
  **** Container Authentication Verifier   ****
  *********************************************
-->
<h3>Container-Managed Authentication</h3>

<p>Не поддерживается в ElWiki.</p>

<!--
  *********************************************
  **** JAAS Authentication Config Verifier ****
  *********************************************
-->
<h3>JAAS Login Configuration</h3>

<!-- Notify users which JAAS configs we need to find -->
<p>ElWiki подключает свой собственный JAAS для определения процесса аутентификации и не полагается на конфигурацию JRE.
По умолчанию ElWiki настраивает свой стек входа в систему JAAS на использование AccountRegistryLoginModule.
Вы можете указать пользовательский модуль входа, установив  <code>jspwiki.loginModule.class</code> в <code>preferences.ini</code>.</p>

<wiki:Messages div="information" topic='<%=SecurityVerifier.INFO+"java.security.auth.login.config"%>' prefix="Good news: "/>
<wiki:Messages div="warning" topic='<%=SecurityVerifier.WARNING+"java.security.auth.login.config"%>' prefix="We found some potential problems with your configuration: "/>
<wiki:Messages div="error" topic='<%=SecurityVerifier.ERROR+"java.security.auth.login.config"%>' prefix="We found some errors with your configuration: " />

<!-- Print JAAS configuration status -->
<p>Конфигурация входа в систему JAAS настроена правильно,
 если свойство <code>jspwiki.loginModule.class</code> указывает класс, который можно найти в classpath.
Этот класс также должен быть реализацией LoginModule.
Проверяется наличие обоих условий.</p>

<wiki:Messages div="information" topic="<%=SecurityVerifier.INFO_JAAS%>" prefix="Good news: "/>
<wiki:Messages div="warning" topic="<%=SecurityVerifier.WARNING_JAAS%>" prefix="We found some potential problems with your configuration: "/>
<wiki:Messages div="error" topic="<%=SecurityVerifier.ERROR_JAAS%>" prefix="We found some errors with your configuration: " />

<!--
  *********************************************
  **** A U T H O R I Z A T I O N           ****
  *********************************************
-->
<h2>Authorization Configuration</h2>

<!--
  *********************************************
  **** Container Authorization Verifier    ****
  *********************************************
-->
<h3>Container-Managed Authorization</h3>

<p>Не поддерживается в ElWiki.</p>

<!--
  *********************************************
  **** Java Security Policy Verifier       ****
  *********************************************
-->
<h3>Security Policy</h3>
<p>ElWiki разрешает действия пользователя, на основе заданных для групп стандартных политик безопасности Java 2.
По умолчанию ElWiki использует политики безопасности сконфигурированные при инсталляции.
Эти политики не зависит от вашей глобальной политики безопасности в масштабах JVM, если она у вас есть.
При проверке авторизации JSPWiki сначала производится обращение к глобальной политике, а затем к локальной политике.</p>

<p>Давайте проверим политики локальной безопасности.
Для этого мы анализируем политику безопасности и проверяем каждый блок <code>grant</code>.
Если мы видим запись <code>permission</code>, которая подписана, то проверяем,
 что псевдоним сертификата существует в нашем хранилище ключей.
Само хранилище ключей также должно существовать в файловой системе.
И в качестве дополнительной проверки мы попытаемся загрузить каждый класс <code>Permission</code> в память,
 чтобы убедиться, что classloader ElWiki может их найти.</p>

<wiki:Messages div="information" topic="<%=SecurityVerifier.INFO_POLICY%>" prefix="Good news: "/>
<wiki:Messages div="warning" topic="<%=SecurityVerifier.WARNING_POLICY%>" prefix="We found some potential problems with your configuration: "/>
<wiki:Messages div="error" topic="<%=SecurityVerifier.ERROR_POLICY%>" prefix="We found some errors with your configuration: " />
<%--
<%
  if ( !verifier.isSecurityPolicyConfigured() )
  {
%>
    <p>Note: JSPWiki's Policy file parser is stricter than the default parser that ships with the JVM.
     If you encounter parsing errors, make sure you have the correct comma and semicolon delimiters
     in your policy file <code>grant</code> entries. The <code>grant</code> blocks must follow this format:</p>
    <blockquote>
      <pre>grant signedBy "signer_names", codeBase "URL",
    principal principal_class_name "principal_name",
    principal principal_class_name "principal_name",
    ... {

    permission permission_class_name "target_name", "action";
    permission permission_class_name "target_name", "action";
};</pre>
    </blockquote>

    <p>Note: JSPWiki versions prior to 2.4.6 accidentally omitted commas after the <code>signedBy</code> entries,
     so you should fix this if you are using a policy file based on a version earlier than 2.4.6.</p>
<%
  }
%>
--%>

<h2>Access Control Validation</h2>

<h3>Security Policy Restrictions</h3>

<p>Теперь начинается <em>по-настоящему</em> веселая часть.
Используя текущую политику безопасности, мы протестируем PagePermission,
 которыми обладает каждая роль JSPWiki для ряда страниц.
Роли, которые мы будем тестировать, включают стандартные роли ElWiki (Authenticated, All и т.д.),
 а также любые другие, которые возможно перечислены в политике безопасности.
В дополнение к PagePermission, мы также протестируем WikiPermissions.
Результаты этих тестов должны подсказать, какое поведение можно ожидать на основе файла политик безопасности.
Если у нас возникли проблемы с поиском, синтаксическим анализом или проверкой файла политики,
 то эти тесты, скорее всего, завершатся неудачей.</p>

<p>Цвета в каждой ячейке показывают результаты теста.
<span style="background-color: #c0ffc0;">&nbsp;Зеленый&nbsp;</span> означает успех;
<span style="background-color: #ffc0c0;">&nbsp;красный&nbsp;</span> означает неудачу.
При наведении курсора мыши на имя роли или отдельную ячейку
 - отобразится более подробная информация о роли или тесте.</p>

<%=verifier.policyRoleTable()%>

<div class="information">
Важно: эти тесты не учитывают какие-либо списки контроля доступа на уровне страницы.
ACL страниц, если они существуют, будут иметь дополнительные правила доступа, помимо приведенных в таблице.

<%--
<%
  if ( isContainerAuth )
  {
%>
In addition, because you are using container-managed security,
 constraints on user activities might be stricter than what is shown in this table.
If the container requires that users accessing <code>Edit.jsp</code> possess the container role "Admin",
 for example, this will override an "edit" PagePermission granted to role "Authenticated." See below.
<%
  }
%>
--%>
</div>

<%--
<%
  if ( isContainerAuth )
  {
%>
    <h3>Web Container Restrictions</h3>

    <p>Here is how your web container will control role-based access to some common JSPWiki actions and their assocated JSPs. These restrictions will be enforced even if your Java security policy is more permissive.</p>

    <p>The colors in each cell show the results of the test. <span style="background-color: #c0ffc0;">&nbsp;Green&nbsp;</span> means success; <span style="background-color: #ffc0c0;">&nbsp;red&nbsp;</span> means failure.</p>

    <!-- Print table showing role restrictions by JSP -->
    <%=verifier.containerRoleTable()%>

    <div class="information">Important: these tests do not take into account any page-level access control lists. Page ACLs, if they exist, will contrain access further than what is shown in the table.</div>

    <!-- Remind the admin their container needs to return the roles -->
    <p>Note that your web container will allow access to these pages <em>only</em> if your container's authentication realm returns the roles
<%
    Principal[] roles = verifier.webContainerRoles();
    for( int i = 0; i < roles.length; i++ )
    {
%>&nbsp;<strong><%=(roles[i].getName() + (i<(roles.length-1)?",":""))%></strong><%
    }
%>
    If your container's realm returns other role names, users won't be able to access the pages they should be allowed to see -- because the role names don't match. In that case, You should adjust the <code>&lt;role-name&gt;</code> entries in <code>web.xml</code> appropriately to match the role names returned by your container's authorization realm.</p>

    <p>Now we are going to compare the roles listed in your security policy with those from your <code>web.xml</code> file. The ones we care about are those that aren't built-in roles like "All", "Anonymous", "Authenticated" or "Asserted". If your policy shows roles other than these, we need to make sure your container knows about them, too. Container roles are defined in <code>web.xml</code> in blocks such as these:</p>
    <blockquote><pre>&lt;security-role&gt;
  &lt;description&gt;
    This logical role includes all administrative users
  &lt;/description&gt;
  &lt;role-name&gt;Admin&lt;/role-name&gt;
&lt;/security-role&gt;</pre></blockquote>

    <wiki:Messages div="information" topic="<%=SecurityVerifier.INFO_ROLES%>" prefix="Good news: "/>
    <wiki:Messages div="error" topic="<%=SecurityVerifier.ERROR_ROLES%>" prefix="We found some errors with your configuration: " />

<%
  }
%>
--%>

<h2>User and Group Databases</h2>

<h3>User Database Configuration</h3>
<p>The user database stores user profiles. It's pretty important that it functions properly. We will try to determine what your current UserDatabase implementation is, based on the current value of the <code>jspwiki.userdatabase</code> property in your <code>preferences.ini</code> file. In addition, once we establish that the UserDatabase has been initialized properly, we will try to add (then, delete) a random test user. If all of these things work they way they should, then you should have no problems with user self-registration.</p>

<wiki:Messages div="information" topic="<%=SecurityVerifier.INFO_DB%>" prefix="Good news: "/>
<wiki:Messages div="warning" topic="<%=SecurityVerifier.WARNING_DB%>" prefix="We found some potential problems with your configuration: "/>
<wiki:Messages div="error" topic="<%=SecurityVerifier.ERROR_DB%>" prefix="We found some errors with your configuration: " />

<h3>Group Database Configuration</h3>
<p>The group database stores wiki groups. It's pretty important that it functions properly.
We will try to determine what your current AccountRegistry implementation is,
 based on the current value of the <code>jspwiki.groupdatabase</code> property
 in your <code>preferences.ini</code> file.
In addition, once we establish that the AccountRegistry has been initialized properly,
 we will try to add (then, delete) a random test group.
If all of these things work they way they should, then you should have no problems
 with wiki group creation and editing.</p>

<wiki:Messages div="information" topic="<%=SecurityVerifier.INFO_GROUPS%>" prefix="Good news: "/>
<wiki:Messages div="warning" topic="<%=SecurityVerifier.WARNING_GROUPS%>" prefix="We found some potential problems with your configuration: "/>
<wiki:Messages div="error" topic="<%=SecurityVerifier.ERROR_GROUPS%>" prefix="We found some errors with your configuration: " />

<!-- We're done... -->
</div>
</div>
</div>
<!-- "script" -->
<wiki:IncludeResources type="stylesheet"/>
</body>
</html>
