package com.zarcode.data.rssfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.zarcode.common.EmailHelper;
import com.zarcode.common.EscapeChars;
import com.zarcode.common.Util;
import com.zarcode.data.dao.EventDao;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.platform.loader.JDOLoaderServlet;
import com.zarcode.data.model.MsgEventDO;
import com.zarcode.data.model.ReportDO;
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
    	EventDao eventDao = null;
    	
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
	    		
	    			if (eventDao == null) {
	    				eventDao = new EventDao();
	    			}
	    			
	    			top = res.get(0);
		    	    feed = new SyndFeedImpl();
		    	    feed.setFeedType(feedType);
		    	    feed.setAuthor("LazyLaker");
		    	    feed.setTitle(top.getName());
		    	    feed.setLink("http://www.lazylaker.net/");
		    	    feed.setDescription("This lake can be found in " + top.getRegion());
		    	    List<MsgEventDO> msgs = eventDao.getNextEventsByResourceId(top.getResourceId());
					if (msgs != null && msgs.size() > EventDao.PAGESIZE) {
						logger.info("Found " + msgs.size() + " msg(s) for location: " + top.getName());
						msgs = msgs.subList(0, EventDao.PAGESIZE);
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
			    	    for (j=0; j<msgs.size(); j++) {
			    	    	MsgEventDO e = msgs.get(j);
				    	    entry = new SyndEntryImpl();
				    	    if (e.getTitle() != null && e.getTitle().length() > 0) {
				    	    	title = e.getTitle();
				    	    }
				    	    else {
				    	    	title = e.getUsername() + " at " + e.getLocation();
				    	    }
				    	    entry.setTitle(title);
				    	    if (e.getPhotoUrl() != null) {
				    	    	entry.setLink(e.getPhotoUrl());
				    	    }
				    	    entry.setPublishedDate(e.getCreateDate());
				    	    entry.setAuthor(e.getUsername());
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
