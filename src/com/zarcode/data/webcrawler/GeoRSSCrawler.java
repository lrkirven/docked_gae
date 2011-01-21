package com.zarcode.data.webcrawler;

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
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.platform.loader.JDOLoaderServlet;
import com.zarcode.data.model.ReportDO;

/**
 * This servlet will crawl public Google Map Geo-RSS feed generated from my custom Lake designs.
 * 
 * @author Administrator
 */
public class GeoRSSCrawler extends HttpServlet {

	private Logger logger = Logger.getLogger(GeoRSSCrawler.class.getName());
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	logger.info("doPost(): Entered"); 
    	int counter = doCrawl(req);
    	logger.info("doPost(): Processing Done"); 
    	returnResp(resp, counter);
    }
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	logger.info("doGet(): Entered"); 
    	int counter = doCrawl(req);
    	logger.info("doGet(): Processing Done"); 
    	returnResp(resp, counter);
    }
    
    private void returnResp(HttpServletResponse resp, int counter) {
    	resp.setContentType("text/html; charset=UTF-8");
    	try {
    		java.io.PrintWriter out = resp.getWriter();
    		out.println("<html><head></head><body>");
        	out.println("RESULTS: # of URLs processed in this request : " + counter);
        	out.println("</body></html>"); 
    	}
    	catch (Exception e) {
    		logger.warning("{EXCEPTION]\n" + Util.getStackTrace(e));
    	}
    }
    
    private int doCrawl(HttpServletRequest req) {
    	int count = 0;
    	WebCrawler crawler = null;

    	// Google
    	crawler = new GoogleGeoRSSCrawler();
    	try {
    		crawler.doCrawl(req);
    		count = 1;
    	}
    	catch (Exception e) {
    		logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
    	}
    	return count;
    }
}
