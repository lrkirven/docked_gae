package com.zarcode.data.webcrawler;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zarcode.app.AppCommon;
import com.zarcode.common.EmailHelper;
import com.zarcode.common.Util;
import com.zarcode.data.exception.WebCrawlException;

/**
 * This servlet actually does the work as a Queue Task crawl the supported
 * report sites and gathering the data.
 * 
 * @author lazar
 *
 */
public class ReportCrawlerTask extends HttpServlet {

	private Logger logger = Logger.getLogger(ReportCrawlerTask.class.getName());

	private final static String TX 		= "TX";
	private final static String UT 		= "UT";
	private final static String MN 		= "MN";
	private final static String MS 		= "MS";
	private final static String MO 		= "MO";

	private final String[] SRC_LIST =  {
		TX,	
		UT,
		// MINN,
		MO,
		MS
	};
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	int counter = doCrawl(req);
    	resp.setContentType("text/plain");
    	resp.getWriter().println(counter);
    }
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	int counter = doCrawl(req);
        resp.setContentType("text/plain");
        resp.getWriter().println(counter);
    }
    
    private int doCrawl(HttpServletRequest req) {
    	int i = 0;
    	int counter = 0;
    	String state = null;
    	WebCrawler crawler = null;
    	
    	for (i=0; i<SRC_LIST.length; i++) {
    		state = SRC_LIST[i];
    		if (state.equalsIgnoreCase(TX)) {
    			crawler = new TXWebCrawler();
    		}
    		else  if (state.equalsIgnoreCase(UT)) {
    			crawler = new UTWebCrawler();
    		}
    		else  if (state.equalsIgnoreCase(MN)) {
    			crawler = new MNWebCrawler();
    		}
    		else  if (state.equalsIgnoreCase(MS)) {
    			crawler = new MSWebCrawler();
    		}
    		else  if (state.equalsIgnoreCase(MO)) {
    			crawler = new MOWebCrawler();
    		}
    		try {
    			if (crawler.readyToCrawl()) {
    				crawler.doCrawl(req);
    				counter++;
    			}
    			else {
    				logger.info("Report feed is UPDATED - " + state);
    			}
    		}
    		catch (WebCrawlException e) {
    			StringBuilder sb = new StringBuilder();
    			sb.append("It looks like our expected HTML landscape has changed.\n\n");
    			sb.append("[STATE]\n");
    			sb.append(state);
    			sb.append("\n\n");
    			sb.append("[URL]\n");
    			sb.append(e.getUrl());
    			sb.append("\n\n");
    			sb.append("[EXCEPTION]\n");
    			sb.append(Util.getStackTrace(e));
    			EmailHelper.sendAppAlert("*** Docked Crawling Report ***", sb.toString(), AppCommon.APPNAME);
    			logger.severe("Crawling failed for state=" + state + " [EXCEPTION]\n" + Util.getStackTrace(e));
    		}
    	}
    	
    	return counter;
    }
}
