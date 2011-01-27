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
import com.zarcode.data.exception.InvalidEmailAddrException;
import com.zarcode.data.exception.MissingUserAccountException;
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
		        		try {
		        			createNewUserAccountByGoogleUser(user);
		        		}
		        		catch (Exception e) {
		        			logger.severe("EXCEPTION ::: Trying to register user -- " + e.getMessage());
		        			resp.sendRedirect("/registrationFail");
		        		}
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
	    
	    
	    public static void createNewUserAccountByGoogleUser(User user) throws MissingUserAccountException {
	    	UserDao userDao = new UserDao();
	    	UserDO newUser = new UserDO();
	    	if (user != null) {
    			String emailAddr = user.getEmail();
    			newUser.setEmailAddr(emailAddr);
    			String llId = user.getFederatedIdentity() + "::" + UUID.randomUUID().toString();
    			newUser.setLLId(llId);
    			String[] addrList = emailAddr.split("@");
    			newUser.setDisplayName(addrList[0]);
    			newUser.setFederatedId(user.getFederatedIdentity());
    			newUser.setAuthDomain(user.getAuthDomain());
    			userDao.addUser(newUser);
    			logger.info("Created new user --- llId=" + llId + " emailAddr=" + emailAddr);
	    	}
	    	else {
	    		throw new MissingUserAccountException();
	    	}
	    }
	    
	    public static void createNewUserAccountByEmailAddr(String emailAddr) throws InvalidEmailAddrException {
	    	UserDao userDao = new UserDao();
	    	UserDO newUser = new UserDO();
	    	if (emailAddr != null && emailAddr.length() > 0) {
	    		String email = emailAddr.toLowerCase();
    			newUser.setEmailAddr(email);
    			String randomVal = UUID.randomUUID().toString();
    			/**
    			 * MAJOR HACK 
    			 */
    			/*
    			String lastChar = null;
    			for (;;) {
    				lastChar = randomVal.substring(randomVal.length()-1, randomVal.length());
    				if (lastChar != null && lastChar.equals("0")) {
    					randomVal = randomVal.substring(0, randomVal.length()-1);
    				}
    				else {
    					break;
    				}
    			}
    			*/
    			String llId = email + "::" + randomVal;
    			newUser.setLLId(llId);
    			String[] addrList = email.split("@");
    			newUser.setDisplayName(addrList[0]);
    			userDao.addUser(newUser);
    			logger.info("Created new user --- llId=" + llId + " emailAddr=" + email);
	    	}
	    	else {
	    		throw new InvalidEmailAddrException();
	    	}
	    }
	    
	    public static UserDO createNewUserAccountByEmailAddr2(String emailAddr, String displayName) throws InvalidEmailAddrException {
	    	UserDao userDao = new UserDao();
	    	UserDO newUser = new UserDO();
	    	if (emailAddr != null && emailAddr.length() > 0) {
	    		String email = emailAddr.toLowerCase();
    			newUser.setEmailAddr(email);
    			newUser.setDisplayName(displayName);
    			String randomVal = UUID.randomUUID().toString();
    			String llId = email + "::" + randomVal;
    			newUser.setLLId(llId);
    			String[] addrList = email.split("@");
    			newUser.setDisplayName(addrList[0]);
    			userDao.addUser(newUser);
    			logger.info("Created new user --- llId=" + llId + " emailAddr=" + email);
	    	}
	    	else {
	    		throw new InvalidEmailAddrException();
	    	}
	    	return newUser;
	    }
}
