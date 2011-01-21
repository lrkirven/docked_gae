package com.zarcode.data.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.zarcode.common.EmailHelper;
import com.zarcode.common.Util;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.platform.loader.JDOLoaderServlet;

public class CronCrawler extends HttpServlet {

	private Logger logger = Logger.getLogger(CronCrawler.class.getName());
	
	private final static String TEXAS 		= "TX";
	private final static String UTAH 		= "UT";
	private final static String MINN 		= "MN";
	private final static String MISS 		= "MS";

	/*
	private final String[] SRC_LIST =  {
		TEXAS,
		UTAH	
	};
	*/
	private final String[] SRC_LIST =  {
		// TEXAS,	
		// UTAH,
		// MINN,
		MISS
	};
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	logger.info("doPost(): Entered"); 
    	int counter = doCrawl(req);
    	logger.info("doPost(): Processing Done"); 
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
    		if (state.equalsIgnoreCase(TEXAS)) {
    			crawler = new TexasWebCrawler();
    		}
    		else  if (state.equalsIgnoreCase(UTAH)) {
    			crawler = new UtahWebCrawler();
    		}
    		else  if (state.equalsIgnoreCase(MINN)) {
    			crawler = new MinnWebCrawler();
    		}
    		else  if (state.equalsIgnoreCase(MISS)) {
    			crawler = new MissWebCrawler();
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
    			EmailHelper.sendAppAlert("*** pix fishing crawling changes ****", sb.toString());
    			logger.severe("Crawling failed for state=" + state + " [EXCEPTION]\n" + Util.getStackTrace(e));
    		}
    	}
    	
    	return counter;
    }
}
