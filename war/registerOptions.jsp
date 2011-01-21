<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.zarcode.data.model.UserDO" %>
<%@ page import="com.zarcode.platform.model.AppPropDO" %>
<%@ page import="com.zarcode.common.ApplicationProps" %>
<%@ page import="com.zarcode.shared.model.OpenIdProviderDO" %>
<%@ page import="com.zarcode.data.dao.OpenIdProviderDao" %>
<%

	int i = 0;
	OpenIdProviderDao dao = new OpenIdProviderDao();
	List<OpenIdProviderDO> list = dao.getAll();
	UserService userService = UserServiceFactory.getUserService();
	if (userService != null) {
		StringBuilder sb = new StringBuilder();
		if (request != null) {
			for (i=0; i<list.size(); i++) {
				OpenIdProviderDO p = (OpenIdProviderDO)list.get(i);
		    	String providerUrl = p.getUrl();
		     	String loginUrl = userService.createLoginURL(request.getRequestURI(), null, providerUrl, null);
		     	String link = "<a href='" + loginUrl + "'>" +  "<img width='50%' height='100px' src='" + p.getImageUrl() + "'></a><br><br>";
		     	sb.append(link);
		     }
			pageContext.setAttribute("providerMenu", sb.toString());
		}
	}
%>
<html>
<head>
<style type="text/css">
#butt {
    height:100px;
    font-family:verdana,arial,helvetica,sans-serif;
    font-size:20px;
 }
</style>
</head>
<body TOPMARGIN=0 LEFTMARGIN=0 MARGINHEIGHT=0 MARGINWIDTH=0>
<table bgcolor="#CCCCCC" width="100%" height="100%"><tr><td>
<p style="margin-right: 20pt; margin-left: 20pt;"><img src="/images/user.png" width="150" height="150" /></p>
<p style="margin-right: 20pt; margin-left: 20pt; font-family: Verdana; color:#232928; font-size:30pt;"><b>To register, please select a provider below that matches your email address: </b><br></p>
<p style="margin-right: 20pt; margin-left: 20pt; font-family: Verdana; color:#232928; font-size:45pt;" />
<%= pageContext.getAttribute("providerMenu") %>
</p>
</td></tr>
<tr><td>
<p style="margin-right: 20pt; margin-left: 20pt; font-family: Verdana; color:#232928; font-size:30pt;">
<FORM ENCTYPE="multipart/form-data" ACTION="/manualRegister" METHOD=POST>
	  	<table border="2" width="100%" height="150px" >
            <tr><td colspan="2"><p align="center"><b>Or enter your email address:</b></td></tr>
            <tr><td></td>
            <td><input type="text" name="emailAddr" size="150"  /></td></tr>
			<tr><td colspan="2"><p align="right"><INPUT id="butt" width="100%" TYPE="submit" VALUE="Register via email address" ></p></td></tr>
        <table>
     </center> 
</FORM>
</p></td></tr>
</table>
</body>
</html> 