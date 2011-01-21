<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<head>
</head>

<html>
  <body scroll="no">
	<%
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
	    if (user != null) {
	    	if (!user.getEmail().equalsIgnoreCase("lrkirven@gmail.com")) {
	    		response.sendRedirect("/unauthorizedUser");	
	    	}
	    	session.setAttribute("USER", user);
	%>
	<p><h2>Admin Access</h2></p>
	<hr>
	<br>
	<FORM ENCTYPE="multipart/form-data" ACTION="/upload" METHOD=POST>
	  	<table border="2" >
            <tr><center><td colspan="2"><p align="center"><B>UPLOAD MY APP LOADER</B><center></td></tr>
            <tr><td><b>Choose the file To Upload:</b></td>
            <td><INPUT NAME="F1" TYPE="file"></td></tr>
			<tr><td colspan="2"><p align="right"><INPUT TYPE="submit" VALUE="Send File" ></p></td></tr>
        <table>
     </center>      
	</FORM>
	<br>
	<hr>
	<br>
	<FORM ACTION="/georssload" METHOD=POST>
		<input type="hidden" name="start" value="0">
	  	<table border="2" >
            <tr><center><td colspan="2"><p align="center"><B>LOAD GEO RSS URL</B><center></td></tr>
            <tr><td><b>Enter GeoRSS URL to load:</b></td>
            <td><input type="text" name="url" size="150"  /></td></tr>
			<tr><td colspan="2"><p align="right"><INPUT TYPE="submit" VALUE="Load URL" ></p></td></tr>
        <table>
     </center>      
	</FORM>
	<%
	    } 
	    else {
	%>
	
	<p><b>Admin Access</b><br>
	<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign In</a></p>
	
	<%
    	}
	%>
	
	</body>
</html>
