package com.zarcode.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.logging.Logger;

import com.zarcode.common.ApplicationProps;
import com.zarcode.data.dao.UserDao;
import com.zarcode.platform.model.AppPropDO;
import com.zarcode.data.model.UserDO;

public class AppRegister extends HttpServlet {
	
		private static Logger logger = Logger.getLogger(AppRegister.class.getName());
		
		public static final String SIMPLE_PASSWORD = "lflsfsfnsflsfj";

	    @Override
	    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	            throws IOException {
	    	UserDao userDao = null;
	    	
	    	if (!req.isSecure()) {
	    		resp.sendRedirect("/loginErrorNotSecure");
	    	}
	    	else {
		        UserService userService = UserServiceFactory.getUserService();
		        User user = userService.getCurrentUser();
		        Set<String> attributes = new HashSet();
	
		        resp.setContentType("text/html");
		        PrintWriter out = resp.getWriter();
	
		        resp.setContentType("text/xml");
		        if (user != null) {
		        	userDao = new UserDao();
		        	if (!userDao.userExists(user.getEmail())) {
		        		createNewUserAccountByGoogleUser(user);
		        	}
		        	HttpSession session = req.getSession(true);
					session.setAttribute("REGISTERING_EMAILADDR", user.getEmail());
		        	resp.sendRedirect("/registerReturn");
		        } 
		        else {
		        	//
		        	// start Open ID process to authenticate the user
		        	//
		        	resp.sendRedirect("/registerOptions");
		        }
	    	}
	    }
	    
	    
	    public static void createNewUserAccountByGoogleUser(User user) {
	    	UserDao userDao = new UserDao();
	    	UserDO newUser = new UserDO();
    		String emailAddr = user.getEmail();
    		newUser.setEmailAddr(emailAddr);
    		String llId = user.getFederatedIdentity() + "::" + UUID.randomUUID().toString();
    		newUser.setLLId(llId);
    		String[] addrList = emailAddr.split("@");
    		newUser.setUsername(addrList[0]);
    		newUser.setFederatedId(user.getFederatedIdentity());
    		newUser.setAuthDomain(user.getAuthDomain());
    		userDao.addUser(newUser);
    		logger.info("Created new user --- llId=" + llId + " emailAddr=" + emailAddr);
	    }
	    
	    public static void createNewUserAccountByEmailAddr(String emailAddr) {
	    	UserDao userDao = new UserDao();
	    	UserDO newUser = new UserDO();
    		newUser.setEmailAddr(emailAddr);
    		String llId = emailAddr + "::" + UUID.randomUUID().toString();
    		newUser.setLLId(llId);
    		String[] addrList = emailAddr.split("@");
    		newUser.setUsername(addrList[0]);
    		userDao.addUser(newUser);
    		logger.info("Created new user --- llId=" + llId + " emailAddr=" + emailAddr);
	    }
}
