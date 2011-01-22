package com.zarcode.data.rssfeed;

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
import com.zarcode.app.AppCommon;
import com.zarcode.common.Util;
import com.zarcode.data.dao.BuzzDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.data.model.WaterResourceDO;

/**
 * This servlet returns a RSS Feed of messages for the closest lake.
 * 
 * @author Administrator
 */
public class LLRssFeeder extends HttpServlet {

	private Logger logger = Logger.getLogger(LLRssFeeder.class.getName());
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	logger.info("doPost(): Entered"); 
    	SyndFeed feed = createRssFeed(req);
    	logger.info("doPost(): Processing Done"); 
    	returnResp(resp, feed);
    }
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	SyndFeed feed = createRssFeed(req);
    	returnResp(resp, feed);
    }
    
    private void returnResp(HttpServletResponse resp, SyndFeed feed) {
    	resp.setContentType("application/rss+xml; charset=UTF-8");
    	try {
    		java.io.PrintWriter out = resp.getWriter();
        	SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, out);
            out.close();
    	}
    	catch (Exception e) {
    		logger.warning("{EXCEPTION]\n" + Util.getStackTrace(e));
    	}
    }
    
    private SyndFeed createRssFeed(HttpServletRequest req) {
    	int i = 0;
    	int j = 0;
    	SyndFeed feed = null;
    	String latParam = null;
    	String lngParam = null;
    	String feedType = "rss_2.0";
    	WaterResourceDO top = null;
    	BuzzDao buzzDao = null;
    	UserDao userDao = new UserDao();
    	
    	//
    	// get closest water resources for the user
    	//
    	latParam = req.getParameter("lat");
    	lngParam = req.getParameter("lng");
    	
    	if (latParam != null && lngParam != null) {
	    	double lat = Double.parseDouble(latParam);
	    	double lng = Double.parseDouble(lngParam);
	    	WaterResourceDao dao = new WaterResourceDao();
	    	List<WaterResourceDO> res = dao.findClosest(lat, lng, 5);
	    	
	    	try {
	    		if (res != null && res.size() > 0) {
	    		
	    			if (buzzDao == null) {
	    				buzzDao = new BuzzDao();
	    			}
	    			if (userDao == null) {
	    				userDao = new UserDao();
	    			}
	    			
	    			top = res.get(0);
		    	    feed = new SyndFeedImpl();
		    	    feed.setFeedType(feedType);
		    	    feed.setAuthor(AppCommon.APPNAME);
		    	    feed.setTitle(top.getName());
		    	    feed.setLink("http://www.lazylaker.net/");
		    	    feed.setDescription("This lake can be found in " + top.getRegion());
		    	    List<BuzzMsgDO> msgs = buzzDao.getNextEventsByResourceId(top.getResourceId());
					if (msgs != null && msgs.size() > BuzzDao.PAGESIZE) {
						logger.info("Found " + msgs.size() + " msg(s) for location: " + top.getName());
						msgs = msgs.subList(0, BuzzDao.PAGESIZE);
					}
					else {
						logger.warning("Unable to find any message(s) for " + top.getName());
					}
	
			    	List entries = null;
			    	
		    	    if (msgs != null && msgs.size() > 0) {
			    	    SyndEntry entry = null;
			    	    SyndContent description = null;
			    	    entries = new ArrayList();
			    	    	
				    	String title = null;
				    	String displayName = null;
			    	    for (j=0; j<msgs.size(); j++) {
			    	    	BuzzMsgDO e = msgs.get(j);
				    	    entry = new SyndEntryImpl();
				    	    displayName = userDao.getDisplayName(e.getLlId());
				    	    if (e.getTitle() != null && e.getTitle().length() > 0) {
				    	    	title = e.getTitle();
				    	    }
				    	    else {
				    	    	title = displayName + " at " + e.getLocation();
				    	    }
				    	    entry.setTitle(title);
				    	    if (e.getPhotoUrl() != null) {
				    	    	entry.setLink(e.getPhotoUrl());
				    	    }
				    	    entry.setPublishedDate(e.getCreateDate());
				    	    entry.setAuthor(displayName);
				    	    description = new SyndContentImpl();
				    	    description.setType("text/plain");
				    	    description.setValue(e.getMessageData());
				    	    entry.setDescription(description);
				    	    entries.add(entry);
			    	    }
		    	    }
		    	    // update feed
		    	    feed.setEntries(entries);
	    		}
	    	}
	    	catch (Exception e) {
	    		logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
	    	}
    	}
    	return feed;
    }
}
