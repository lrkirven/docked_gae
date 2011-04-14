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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.zarcode.common.EscapeChars;
import com.zarcode.common.Util;
import com.zarcode.data.dao.RegionDao;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.platform.loader.JDOLoaderServlet;
import com.zarcode.data.model.ReportDO;
import com.zarcode.data.webcrawler.TXWebCrawler.TexasTagNodeVisitor;

public class UTWebCrawler extends WebCrawler {

	private Logger logger = Logger.getLogger(UTWebCrawler.class.getName());
	
	private static final String PROVIDER = "wildlife.utah.gov";
	
	private final String[] URL_LIST =  {
		"http://wildlife.utah.gov/hotspots/reports_cr.php",
		"http://wildlife.utah.gov/hotspots/reports_nr.php",
		"http://wildlife.utah.gov/hotspots/reports_ne.php",
		"http://wildlife.utah.gov/hotspots/reports_se.php",
		"http://wildlife.utah.gov/hotspots/reports_sr.php"
	};
	
	public static final Map<Integer, Integer> CRAWL_MAP = new HashMap<Integer, Integer>() 
    {
        {
             put(Calendar.TUESDAY, 1);
             put(Calendar.WEDNESDAY, 1);
             put(Calendar.THURSDAY, 1);
             put(Calendar.FRIDAY, 1);
             put(Calendar.SATURDAY, 1);
        }
    };
	
	private final String STATE = "UT";
	
	/**
	 * This class is custom parsing to return fishing reports on the wildlife.utah.gov.
	 * 
	 * @author lazar
	 *
	 */
	public class UtahTagNodeVisitor implements TagNodeVisitor {
		
  	    private ReportDO report = null;
  	    private boolean reportsFound = false;
  	    private ReportDao reportDao = new ReportDao();
  	    private boolean bReportOpen = false;
  	    private boolean bReportRegionUpdated = false;
  	    
  	    // 2011-03-12
	 	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
  	    
  	    public UtahTagNodeVisitor() {
  	    }
		
		public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
  	    	String tagValue = null;
  	    	Date reportDate = null;
  	    	
  	        if (htmlNode instanceof TagNode) {
  	            TagNode tag = (TagNode) htmlNode;
  	            String tagName = tag.getName();
  	            /*
  	             * start and end of report object
  	             */
  	            if ("td".equalsIgnoreCase(tagName)) {
  	            	String className = tag.getAttributeByName("class");
  	            	if (className != null && className.equalsIgnoreCase("body12")) {
  	            		if (report == null) {
  	            			report = new ReportDO();
    	            		report.setReportedBy(PROVIDER);
    	            		report.setState(STATE);
  	            		}
  	            		else {
  	            			tagValue = tag.getText().toString();
  	            			if (tagValue != null) {
  	            				report.setReportBody(tagValue);
  	            			}
  	            			reportDao.addOrUpdateReport(report);
  	            			report = null;
  	            		}
  	            	}
  	            }
  	            else if ("a".equalsIgnoreCase(tagName) && report != null) {
	            	tagValue = tag.getText().toString();
	            	if (tagValue != null) {
	            		report.setKeyword(tagValue);
	            		StringBuilder sb = new StringBuilder();
	  	            	sb.append(STATE);
	  	            	sb.append(":");
	  	            	String uniqueKey = report.getKeyword();
	  	            	uniqueKey= uniqueKey.toUpperCase();
	  	            	uniqueKey = EscapeChars.forXML(uniqueKey);
	  	            	sb.append(uniqueKey);
	  	            	report.setReportKey(sb.toString());
	            	}
	            }
  	            else if ("i".equalsIgnoreCase(tagName) && report != null) {
  	               tagValue = tag.getText().toString();
  	               if (tagValue != null) {
  	            	   if (report.getReportDate() == null) {
  	            		   logger.info("Found dateStr: " + tagValue);
  	            	   		try {
  	            	   			reportDate = formatter.parse(tagValue);
  	            	   			if (!bReportRegionUpdated) {
        							bReportRegionUpdated = true;
	        						RegionDao regionDao = new RegionDao();
	        						regionDao.updateRegionByState(STATE, reportDate);
	        					}
  	            	   		}
  	            	   		catch (Exception e) {
  	            	   			logger.warning("Unable to parse report date --- " + tagValue);
  	            	   		}
  	            	   		if (reportDate != null) {
  	            	   			report.setReportDate(reportDate);
  	            	   		}
  	               		}
  	            	}
  	        	} 
			}
  	            
  	        // tells visitor to continue traversing the DOM tree
  	        return true;
  	        	
  	    } // visit
		 
	} // UtahTagNodeVisitor
	
	@Override
	public boolean readyToCrawl() {
		boolean flag = false;
		if (isFeedUpdated(STATE)) {
			logger.info("Feed is updated --> " + STATE);
			return flag;
		}
		else {
			Calendar now = Calendar.getInstance();
			Integer dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
			if (CRAWL_MAP.containsKey(dayOfWeek)) {
				flag = true;
				ReportDao reportDao = new ReportDao();
				long rows = reportDao.deleteByState(STATE);
				logger.info("Existing rows deleted --> " + rows);
			}
			else {
				logger.warning("Not day of week to crawl --> " + dayOfWeek);
			}
		}
		return flag;
	}


	@Override
    public void doCrawl(HttpServletRequest req) throws WebCrawlException {
    	int i = 0;
    	String msg = null;
    	String urlStr = null;
    	
    	try {
    		for (i=0; i<URL_LIST.length; i++) {
    			urlStr = URL_LIST[i];
    			logger.info("Processing URL: " + urlStr);
	            URL url = new URL(urlStr);
	            CleanerProperties props = new CleanerProperties();
 	            // set some properties to non-default values
 	         	props.setTranslateSpecialEntities(true);
 	         	props.setTransResCharsToNCR(true);
 	         	props.setOmitComments(true);
 	         	TagNode root = new HtmlCleaner(props).clean(url);
 	         	root.traverse(new UtahTagNodeVisitor());
    		}
        } 
    	catch (MalformedURLException e) {
    		logger.severe(Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), urlStr);
        }
    	catch (IOException e) {
    		logger.severe(Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), urlStr);
        }
    	catch (Exception e) {
    		logger.severe(Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), urlStr);
        }
    } // doCrawl
}
