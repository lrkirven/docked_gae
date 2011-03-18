package com.zarcode.security;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;
import com.zarcode.common.Util;
import com.zarcode.platform.dao.AppPropDao;
import com.zarcode.platform.model.AppPropDO;

public class AuthSubAgent extends HttpServlet {
	
		private static Logger logger = Logger.getLogger(AuthSubAgent.class.getName());
		
	    @Override
	    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    	
	    	logger.info("Incoming request ...");
	    	
	    	// Store the incoming request URL
	    	String nextUrl = request.getRequestURL().toString();
	    	String startVal = request.getParameter("start");
	    	
	    	if (startVal == null) {
	    		logger.info("handle return REQUEST from Google Data ...");
	    		handleReturnReqFromGoogleData(request, response);
	    	}
	    	else {
	    		boolean starting = Boolean.parseBoolean(request.getParameter("start"));
	    		if (starting) {
	    			logger.info("Prompt user to add Picasa scope ...");
	    			// Generate the AuthSub URL
	    			String requestUrl = AuthSubUtil.getRequestUrl("http://dockedmobile.appspot.com/_authSub", "http://picasaweb.google.com/data/", 
	    					false, true);
	    			response.getWriter().print("<a href=\"" + requestUrl + "\">Request token for the Google Picasa Scope</a>");
	    		}
	    		else {
	    			logger.info("Request is not VALID");
	    			response.getWriter().print("<h3>Request in not VALID</h3>");
	    		}
	    	}
	    }
	    
	    
		private void handleReturnReqFromGoogleData(HttpServletRequest request, HttpServletResponse response) {
			String sessionToken = null;
			
			logger.info("Return REQUEST processing ...");
	
			try {
				// Find the AuthSub token and upgrade it to a session token.
				String singleUseToken = AuthSubUtil.getTokenFromReply(request
						.getQueryString());
				
				logger.info("Got singleUseToken --> " + singleUseToken);
				singleUseToken = URLDecoder.decode(singleUseToken, "UTF-8"); 
				// Upgrade the single-use token to a multi-use session token.
				sessionToken = AuthSubUtil.exchangeForSessionToken(singleUseToken, null);
				
				logger.info("Exhanged for session token --> " + sessionToken);
	
				if (sessionToken != null) {
					
					logger.info("Saving session token --> " + sessionToken);
					
					AppPropDao dao = new AppPropDao();
					AppPropDO prop = new AppPropDO();
					prop.setName("AUTHSUB_SESSION_TOKEN");
					prop.setStringValue(sessionToken);
					dao.addProp(prop);
	
					// Write token to response
					response.getWriter().print(
							"<h3>A Google Data session token was found "
									+ "for your account!</h3>");
					response.getWriter()
							.print("<p>Token: " + sessionToken + "</p>");
				} 
				else {
					// If no session token is set, allow users to authorize this
					// sample app
					// to fetch personal Google Data feeds by directing them to an
					// authorization page.
					
					logger.warning("*** No session token found ***");
	
					// Generate AuthSub URL
					String nextUrl = request.getRequestURL().toString();
					String requestUrl = AuthSubUtil.getRequestUrl("http://dockedmobile.appspot.com/_authSub", 
							"https://picasaweb.google.com/data/", false, true);
	
					// Write AuthSub URL to response
					response.getWriter().print(
							"<h3>A Google Data session token could not "
									+ "be found for your account.</h3>");
					response.getWriter().print(
									"<p>In order to see your data, you must "
											+ "first authorize access to your personal feeds. Start this "
											+ "process by choosing a service from the list below:</p>");
					response.getWriter().print("<ul><li><a href=\"" + requestUrl + "\">"
									+ "Google Picasa</a></li></ul>");
				}
			} 
			catch (AuthenticationException e) {
				logger.severe("User is not authorized -- EXCEPTION -- " + Util.getStackTrace(e));
			} 
			catch (GeneralSecurityException e) {
				logger.severe("Security EXCEPTION -- " + Util.getStackTrace(e));
				// Handle
			} 
			catch (NullPointerException e) {
				logger.severe("Null Pointer EXCEPTION -- " + Util.getStackTrace(e));
				// Ignore
			} 
			catch (Exception e) {
				logger.severe("EXCEPTION -- " + Util.getStackTrace(e));
			}
		}
}