package com.zarcode.data.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;
import com.zarcode.common.Util;
import com.zarcode.data.dao.BuzzDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.data.model.UserDO;
import com.zarcode.data.model.WaterResourceDO;

/**
 */
public class UserProfile extends HttpServlet {

	private Logger logger = Logger.getLogger(UserProfile.class.getName());
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	logger.info("doPost(): Entered"); 
    	returnResp(req, resp);
    }
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	logger.info("doGet(): Entered"); 
    	returnResp(req, resp);
    }
    
    private void returnResp(HttpServletRequest req, HttpServletResponse resp) {
    	UserDao userDao = new UserDao();
    	BuzzDao buzzDao = new BuzzDao();
    	BuzzMsgDO buzzMsg = null;
    	UserDO user = null;
    	
    	try {
    		String msgIdStr = req.getParameter("m");
    		if (msgIdStr != null) {
    			Long msgId = Long.parseLong(msgIdStr);
    			buzzMsg = buzzDao.getMsgById(msgId);
    			if (buzzMsg != null) {
    				user = userDao.getUserByLLID(buzzMsg.getLlId(), false);
    				if (user != null) {
    					if (user.getProfileUrl() == null) {
    						
    					}
    				}
    			}
    			resp.setContentType("image/jpeg");
    		}
    	}
    	catch (Exception e) {
    		logger.warning("{EXCEPTION]\n" + Util.getStackTrace(e));
    	}
    }
}
