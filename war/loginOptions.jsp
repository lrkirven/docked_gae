<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.zarcode.data.model.UserDO" %>
<%@ page import="com.zarcode.data.model.SecurityTokenDO" %>
<%@ page import="com.zarcode.data.dao.UserDao" %>
<html>
<head>
</head>
<body>
	<b>LazyLaker Sign In<b><br><br>Please select your from the following OpenId providers:<br>
	[<a href=" + loginUrl + "\">" + providerName + "</a>]
</body>
</html> 