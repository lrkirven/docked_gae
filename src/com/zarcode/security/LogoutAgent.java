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

public class LogoutAgent extends HttpServlet {

		private static final Map<String, String> openIdProviders;
    	static {
     	    openIdProviders = new HashMap<String, String>();
      	  	openIdProviders.put("Google", "google.com/accounts/o8/id");
       	 	openIdProviders.put("Yahoo", "yahoo.com");
        	openIdProviders.put("MySpace", "myspace.com");
        	openIdProviders.put("AOL", "aol.com");
        	openIdProviders.put("Facebook", "facebook.com");
        	openIdProviders.put("MyOpenId.com", "myopenid.com");
   	 	}
    
	    @Override
	    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	            throws IOException {
	        UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser(); // or req.getUserPrincipal()
	        Set<String> attributes = new HashSet();

	        PrintWriter out = resp.getWriter();
	        
	        if (user != null) {
	            resp.sendRedirect("/registerReturn");
	        } 
	        else {
	        	resp.sendRedirect("/logout");
	        	String logoutUrl = "https://mail.google.com/mail/?logout&hl=en";
	        	resp.setContentType("text/html");
	            out.println("<b>LazyLaker has logged you out</b><br>To logout of Goggle, go here:<br>");
	            out.println("[<a href=\"" + logoutUrl + "\">Google Logout</a>]<br>");
	        }

	    }
}
