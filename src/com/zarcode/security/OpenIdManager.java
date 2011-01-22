package com.zarcode.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.zarcode.common.ApplicationProps;
import com.zarcode.data.dao.UserDao;
import com.zarcode.platform.model.AppPropDO;
import com.zarcode.data.model.UserDO;

public class OpenIdManager extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(OpenIdManager.class.getName());

	private static final List<String> openIdProviders;
   	static {
   	    openIdProviders = new ArrayList<String>();
   	  	openIdProviders.add("google.com/accounts/o8/id");
   	 	openIdProviders.add("yahoo.com");
      	openIdProviders.add("aol.com");
  	}
    
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserDao userDao = null;
	    	
	   	UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
	    	
    	if (!req.isSecure()) {
    		resp.sendRedirect("/loginErrorNotSecure");
    	}
    	else {
	         // or req.getUserPrincipal()
	        Set<String> attributes = new HashSet();

	        PrintWriter out = resp.getWriter();
	        
	        logger.info("Trying to authenicate user via Open ID ...");
	        
	        if (user != null) {
	        	logger.info("Found user instance -- returning results to mobile.");
	        	userDao = new UserDao();
	        	if (!userDao.userExists(user.getEmail())) {
	        		UserDO newUser = new UserDO();
	        		String emailAddr = user.getEmail();
	        		newUser.setEmailAddr(emailAddr);
	        		newUser.setLLId(user.getFederatedIdentity());
	        		String[] addrList = emailAddr.split("@");
	        		newUser.setDisplayName(addrList[0]);
	        		userDao.addUser(newUser);
	        	}
	        	HttpSession session = req.getSession(true);
				session.setAttribute("REGISTERING_EMAILADDR", user.getEmail());
	            resp.sendRedirect("/registerReturn");
	        } 
	        else {
	        	logger.info("Unable to find user instance -- Present user with possible Open ID providers to authenicate you.");
	            resp.sendRedirect("/registerOptions");
	        }
    	}
	} // doGet
	    
    public static List<String> generateLoginUrls(String requestUri, UserService userService) {
    	int i = 0;
		Set<String> attributes = new HashSet();
    	List<String> urls = new ArrayList<String>();
    	for (i=0; i<openIdProviders.size(); i++) {
    		String providerUrl = openIdProviders.get(i);
            String loginUrl = userService.createLoginURL(requestUri, null, providerUrl, attributes);
            logger.info(i + ") loginUrl=" + loginUrl);
            urls.add(loginUrl);
        }
    	return urls;
    }
	    
}
