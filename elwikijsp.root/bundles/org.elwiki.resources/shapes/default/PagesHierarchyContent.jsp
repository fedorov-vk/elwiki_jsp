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
<style>
/* Tree CSS */
ul.tree, ul.tree li, ul.tree ul {
	position: relative;
	cursor: pointer;
	zoom: 1;
}

ul.tree li, li.drag, li.drag ul li  {
	margin: 0;
	line-height: 20px;
	list-style-type: none;
}

ul.tree, ul.tree ul, li.drag ul {
	margin: 0;
	padding: 0 0 0 30px;
}

ul.tree li span, li.drag span {
	color: #111;
	display: block;
}

ul.tree li {
	list-style-type: none;
	padding-left: 19px;
	line-height: 18px;
	margin: 3px 0;
	background: url(/Assets/Bullet.png) no-repeat 0 2px;
}

ul.tree li.nodrop {
	background-image: url(/Assets/Blocked.png);
}

ul.tree li.nodrop span {
	color: #800;
}

ul.tree li.nodrag span {
	color: #999;
}

div.treeIndicator {
	margin: 0;
	position: absolute;
	width: 100px;
	height: 1px;
	background-color: #000;
	border-top: 1px solid #999;
	border-bottom: 1px solid #999;
	z-index: 50;

	/* For IE */
	overflow: hidden;
	line-height: 1px;
}

li.drag {
	position: absolute;
	z-index: 50;
}

li.drag span.dispose {
	display: none;
}

span.dispose {
	float: right;
	color: #800;
}
</style>
<!-- ~~ START ~~ PagesHierarchyContent.jsp  --><%@
 page import="org.apache.wiki.api.core.*" %><%@
 page import="org.apache.wiki.attachment.*" %><%@
 page import="javax.servlet.jsp.jstl.fmt.*" %><%@
 taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %><%@
 taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="shapes.default"/>
<%
  WikiContext c = ContextUtil.findContext( pageContext );
%>
<%-- Main Content Section --%>
<%-- This has been source ordered to come first in the markup (and on small devices)
     but to be to the right of the nav on larger screens --%>
<div class="page-content <wiki:Variable var='page-styles' default='' />">

<div id="AllPagesContainer"></div>

</div>
<script src="<wiki:Link format='url' path='scripts/hierarchy-controller.js'/>"></script>
<script type="text/javascript">
	window.addEvent('domready', function() {
		// Example #1
		new Tree('tree', {

			checkDrag : function(element) {
				return !element.hasClass('nodrag');
			},

			checkDrop : function(element) {
				return !element.hasClass('nodrop');
			}

		});
		/*
		 var dispose = new Element('span.dispose[text=(remove)]').addEvents({

		 mousedown: function(event){
		 event.preventDefault();
		 },

		 click: function(){
		 this.getParent('li').dispose();
		 }

		 });
		 */
		document.id('tree').addEvents({

			'mouseover:relay(li)' : function() {
				this.getElement('span').adopt(dispose);
			},

			mouseleave : function() {
				dispose.dispose();
			}

		});

		var i = 1;
		document.id('addItem').addEvent(
				'click',
				function() {
					new Element('li').adopt(
							new Element('span[text=New Item #' + (i++) + ']'))
							.inject('tree');
				});
	});
</script>

<!-- ~~ END ~~ PagesHierarchyContent.jsp  -->