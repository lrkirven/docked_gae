package com.zarcode.server;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.zarcode.client.RegisterService;
import com.zarcode.data.dao.OpenIdProviderDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.model.UserDO;
import com.zarcode.security.AppRegister;
import com.zarcode.security.OpenIdManager;
import com.zarcode.shared.model.OpenIdProviderDO;
import com.dominicsayers.isemail.IsEMail;
import com.dominicsayers.isemail.IsEMailResult;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RegisterServiceImpl extends RemoteServiceServlet implements RegisterService {
	
	private Logger logger = Logger.getLogger(RegisterServiceImpl.class.getName());

	public String greetServer(String input) {
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}
	
	public List<String> setup(String requestUri) {
		List<String> res = null;
		
		logger.info("Using requestUri --> " + requestUri);
		UserService userService = UserServiceFactory.getUserService();
		if (userService != null) {
			res = OpenIdManager.generateLoginUrls(requestUri, userService); 
		}
		return res;
	}
	
	public String manualRegister(String emailAddr) {
		String res = null;
		final boolean CHECK_DNS = false;
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		
		logger.info("Incoming email address --> " + emailAddr);
		
		try {
			
			UserDao userDao = new UserDao();
			if (userDao.userExists(emailAddr)) {
				res = "Email address is already been registered.";
			}
			else {
				IsEMailResult result = IsEMail.is_email_verbose(emailAddr, CHECK_DNS);
				switch (result.getState()) {
				case OK:
					res = "Verified";
					AppRegister.createNewUserAccountByEmailAddr(emailAddr);
					HttpSession session = this.getThreadLocalRequest().getSession(true);
					session.setAttribute("REGISTERING_EMAILADDR", emailAddr);
					break;
		
				case WARNING:
					res = "Your email address did not pass our verification process (WARNING). Please select another email account for registration.";
					break;
		
				case ERROR:
					res = "Your email address is not VALID. Please select another email account for registration.";
					break;
				}
			}
		}
		catch (Exception e) {
			res = "Your email address did not pass our verification process (DNS). Please select another email account for registration.";
		}
		return res;
	}
	
	public List<OpenIdProviderDO> getProviders() {
		List<OpenIdProviderDO> list = null;
		OpenIdProviderDao dao = new OpenIdProviderDao();
		list = dao.getAll();
		return list;
	}
}

