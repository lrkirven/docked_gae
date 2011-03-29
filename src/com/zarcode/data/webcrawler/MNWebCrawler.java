package com.zarcode.data.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.*;

import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.zarcode.common.EscapeChars;
import com.zarcode.common.Util;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.platform.loader.JDOLoaderServlet;
import com.zarcode.data.model.ReportDO;

public class MNWebCrawler extends WebCrawler {

	private Logger logger = Logger.getLogger(MNWebCrawler.class.getName());
	
	private final String RSS_FEED_URL = "http://clicks.skem1.com/archive/rss.php?c=4027&l=11";
	
	private static final Map<String, Integer> KNOWN_AREA_KEYS = new HashMap<String, Integer>() 
	{
		{
			put("International Falls", 1);
			put("Kabetogama", 1);
			put("Cook/Tower - Lake Vermilion", 1);
			put("Ely", 1);
			put("Grand Rapids", 1);
			put("Baudette/Lake of the Woods & Rainy River", 1);
			put("Bemidji", 1);
			put("Walker/Leech Lake", 1);
			put("Bemidji", 1);
			put("Cass Lake & Deer River/ Winnibigoshish & Cutfoot Sioux lakes", 1);
			put("Park Rapids", 1);
			put("Detroit Lakes", 1);
			put("Otter Tail Country/Battle Lake/Pelican Rapids", 1);
			put("Miltona", 1);
			put("Pine River Area Lakes", 1);
			put("Brainerd Lakes Area", 1);
			put("Willmar Area Lakes", 1);
			put("Isle/Onamia/Lake Mille Lacs Area Lakes", 1);
			put("Northeast Metro/Chisago Lakes Area", 1);
			put("White Bear Lake", 1);
			put("Waconia", 1);
			put("Lake City", 1);
			put("Lanesboro", 1);
			put("Albert Lea", 1);
			put("Ortonville", 1);
		}
    };
	
	public static final Map<Integer, Integer> CRAWL_MAP = new HashMap<Integer, Integer>()  {
        {
             put(Calendar.THURSDAY, 1);
             put(Calendar.FRIDAY, 1);
        }
    };
	
	private final String STATE = "MN";
	
	@Override
	public boolean readyToCrawl() {
		boolean flag = false;
		Calendar now = Calendar.getInstance();
	
		Integer dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		if (CRAWL_MAP.containsKey(dayOfWeek)) {
			flag = true;
		}
		return flag;
	}
	
	private void parseFeedContents(String contents, Date publishDate) {
		
		logger.info("Entered.");
		StringReader r = new StringReader(contents);
		StringWriter w = new StringWriter();
		
        Tidy tidy = new Tidy();
		tidy.setMakeClean(true);
		tidy.setXmlOut(true);
		tidy.setDropFontTags(true);
		tidy.setXHTML(true);
	 	tidy.setRawOut(true);
	 	tidy.setSmartIndent(true);
	 	tidy.setWord2000(true);
	 	tidy.setDropEmptyParas(true);
	    tidy.setShowWarnings(false);
	    tidy.setFixComments(true);
	 	   	
		OutputStream os = null;
		Document doc = tidy.parseDOM(r, w);

		ReportDO report = null;
		ReportDao reportDao = new ReportDao();
		int i = 0;
		boolean foundMatch = false;
		NodeList pList = doc.getElementsByTagName("p");
		if (pList != null) {
		
			for (i=0; i<pList.getLength(); i++) {
				Node node = pList.item(i);
				List<String> textList = new ArrayList<String>();
				findMatchingNodes(node, Node.TEXT_NODE, textList);
				if (textList.size() > 0) {
					String val = textList.get(0);
					if (KNOWN_AREA_KEYS.containsKey(val)) {
						logger.info("Found locale -- Starting report: " + val);
						report = new ReportDO();
						report.setKeyword(val);
						StringBuilder sb = new StringBuilder();
						sb.append(STATE);
						sb.append(":");
						String uniqueKey = report.getKeyword();
						uniqueKey= uniqueKey.toUpperCase();
						uniqueKey = EscapeChars.forXML(uniqueKey);
						sb.append(uniqueKey);
						report.setReportKey(sb.toString());
						report.setReportDate(publishDate);
						report.setState(STATE);
						foundMatch = true;
					}
					else if (foundMatch) {
						logger.info("Closing report: " + val);
						report.setReportBody(val);
						reportDao.addOrUpdateReport(report);
						foundMatch = false;
						report = null;
					}
				}
			}
		}
		logger.info("Exit.");
	}
	
	private void doRss() {
		XmlReader reader = null;

		try {

			URL url  = new URL(RSS_FEED_URL);
			reader = new XmlReader(url);
		    SyndFeed feed = new SyndFeedInput().build(reader);
		    System.out.println("Feed Title: "+ feed.getAuthor());
		    
		    SyndEntry latest = null;
		    Date last = null;

			for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
				SyndEntry entry = (SyndEntry) i.next();
				Date pDate = entry.getPublishedDate();
				if (last == null) {
					last = pDate;
					latest = entry;
				}
				else if (pDate.getTime() > last.getTime()) {
					last = pDate;
					latest = entry;
				}
				logger.info("DESC: " + entry.getDescription().getValue());
		 	}
			
			if (latest != null) {
				logger.info("Last Minn Report Feed: " + latest.getTitle());
				logger.info("Link: " + latest.getLink());
				logger.info("URI: " + latest.getUri());
				
				List contents = latest.getContents();
				if (contents != null && contents.size() > 0) {
					int i = 0;
					SyndContentImpl val = null;
					for (i=0; i<contents.size(); i++) {
						val = (SyndContentImpl)contents.get(i);
						parseFeedContents(val.getValue(), last);
					}
				}
				else {
					logger.info("No Contents");
				}
			}
		} 
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (Exception e) {
				}
			}
		}
	}


	@Override
    public void doCrawl(HttpServletRequest req) throws WebCrawlException {
    	int i = 0;
    	int j = 0;
    	int k = 0;
    	String msg = null;
    	String urlStr = null;
    	
    	logger.info("Starting RSS processing");
    	this.doRss();
    	logger.info("RSS Done.");
    	
    } // doCrawl
}
