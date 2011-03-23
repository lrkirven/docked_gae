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

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.zarcode.app.AppCommon;
import com.zarcode.common.EmailHelper;
import com.zarcode.common.Util;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.platform.loader.JDOLoaderServlet;

/**
 * This servlet starts the Task Queues on GAE to slowly crawl all of the sites
 * to retrieve the report data.
 * 
 * @author lazar
 *
 */
public class ReportCrawler extends HttpServlet {

	private Logger logger = Logger.getLogger(ReportCrawler.class.getName());

	
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    }
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/reportCrawlerTask"));
		resp.setContentType("text/html");
		resp.getWriter().println("<b>Report Crawler has been started -- Wait for email for results</b><br><br><a href=\"/_admin\">Back to Admin Console</a>");
    }
    
}
