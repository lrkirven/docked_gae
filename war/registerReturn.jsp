<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.zarcode.data.model.UserDO" %>
<%@ page import="com.zarcode.platform.model.AppPropDO" %>
<%@ page import="com.zarcode.common.ApplicationProps" %>
<%@ page import="com.zarcode.data.model.SecurityTokenDO" %>
<%@ page import="com.zarcode.data.dao.UserDao" %>
<%
	HttpSession sess = request.getSession();
	if (sess != null) {
		String emailAddr = (String)sess.getAttribute("REGISTERING_EMAILADDR");
		if (emailAddr != null) {
			UserDao dao = new UserDao();
			UserDO user = dao.getUserByEmailAddr(emailAddr);	
			if (user != null) {
				String nickname = user.getDisplayName();
				String llId = user.getLLId();
				AppPropDO p0 = ApplicationProps.getInstance().getProp("PICASA_USER");
				AppPropDO p1 = ApplicationProps.getInstance().getProp("PICASA_PASSWORD");
				AppPropDO p2 = ApplicationProps.getInstance().getProp("FB_LAZYLAKER_API_KEY");
				AppPropDO p3 = ApplicationProps.getInstance().getProp("FB_LAZYLAKER_SECRET");
				SecurityTokenDO token = new SecurityTokenDO();
				token.encryptThenSetEmailAddr(emailAddr);
				token.encryptThenSetLLId(llId);
				token.setNickname(nickname);
				token.encryptThenSetPicasaUser(p0.getStringValue());
				token.encryptThenSetPicasaPassword(p1.getStringValue());
				token.encryptThenSetFbLazyLakerKey(p2.getStringValue());
				token.encryptThenSetFbLazyLakerSecret(p3.getStringValue());
				pageContext.setAttribute("securityToken", token);
			}
		}
	}
	
	
%>
<securityToken>
	<emailAddr><jsp:getProperty name="securityToken" property="emailAddr" /></emailAddr>
	<nickname><jsp:getProperty name="securityToken" property="nickname" /></nickname>
	<llId><jsp:getProperty name="securityToken" property="llId" /></llId>
	<picasaUser><jsp:getProperty name="securityToken" property="picasaUser" /></picasaUser>
	<picasaPassword><jsp:getProperty name="securityToken" property="picasaPassword" /></picasaPassword>
	<fbLazyLakerKey><jsp:getProperty name="securityToken" property="fbLazyLakerKey" /></fbLazyLakerKey>
	<fbLazyLakerSecret><jsp:getProperty name="securityToken" property="fbLazyLakerSecret" /></fbLazyLakerSecret>
	<serverCode><jsp:getProperty name="securityToken" property="serverSecret" /></serverCode>
</securityToken>