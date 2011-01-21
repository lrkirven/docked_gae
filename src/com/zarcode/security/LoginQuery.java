package com.zarcode.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoginQuery extends HttpServlet {

	    @Override
	    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	            throws IOException {
	        UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser(); // or req.getUserPrincipal()
	        Set<String> attributes = new HashSet();

	        resp.setContentType("text/xml");
	        PrintWriter out = resp.getWriter();

	        if (user != null) {
	        	// check if user does not exist, add them
	        	out.println("<loginQuery>");
	        	out.println("<status>200</status>");
	        	out.println("<name>" + user.getNickname() + "</name>");
	        	out.println("<emailAddr>" + user.getEmail() + "</emailAddr>");
	        	out.println("<logoutURL>" + userService.createLogoutURL(req.getRequestURI()) + "</logoutURL>");
	        	out.println("</loginQuery>"); 
	        } 
	        else {
	        	out.println("<loginQuery>");
	        	out.println("<status>401</status>");
	        	out.println("</loginQuery>"); 
	        }
	    }
}
