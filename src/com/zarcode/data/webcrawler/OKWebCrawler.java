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

import com.zarcode.app.AppCommon;
import com.zarcode.common.EscapeChars;
import com.zarcode.common.Util;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.platform.loader.JDOLoaderServlet;
import com.zarcode.data.model.ReportDO;
import com.zarcode.data.webcrawler.TXWebCrawler.TexasTagNodeVisitor;

public class OKWebCrawler extends WebCrawler {

	private Logger logger = Logger.getLogger(OKWebCrawler.class.getName());
	
	private static final String PROVIDER = "www.wildlifedepartment.com";
	
	private final String[] URL_LIST =  {
		"http://www.wildlifedepartment.com/fishokc.htm",
		"http://www.wildlifedepartment.com/fishne.htm",
		"http://www.wildlifedepartment.com/fishse.htm",
		"http://www.wildlifedepartment.com/fishsw.htm",
		"http://www.wildlifedepartment.com/fishnw.htm"
	};
	
	public static final Map<Integer, Integer> CRAWL_MAP = new HashMap<Integer, Integer>() 
    {
        {
             put(Calendar.MONDAY, 1);
             put(Calendar.TUESDAY, 1);
             put(Calendar.THURSDAY, 1);
             put(Calendar.FRIDAY, 1);
        }
    };
	
	private final String STATE = "OK";
	
	/**
	 * This class is custom parsing to return fishing reports on the wildlife.utah.gov.
	 * 
	 * @author lazar
	 *
	 */
	public class OklahomaTagNodeVisitor implements TagNodeVisitor {
		
  	    private ReportDO report = null;
  	    private ReportDao reportDao = new ReportDao();
  	   	private Date reportDate = null;
  	   	
  	    // March 28, 2011 11:22 AM
	 	private DateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy hh:mm aaa");
  	    
  	    public OklahomaTagNodeVisitor() {
  	    }
  	    
		public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
  	    	String tagValue = null;
  	    	
  	        if (htmlNode instanceof TagNode) {
  	            TagNode tag = (TagNode) htmlNode;
  	            String tagName = tag.getName();
  	            
  	            if ("p".equalsIgnoreCase(tagName)) {
	            	String alignName = tag.getAttributeByName("align");
	            	if (alignName != null && alignName.equalsIgnoreCase("center")) {
	            		tagValue = tag.getText().toString();
	            		if (tagValue != null) {
	            			String dateStr = tagValue.trim();
	            			try {
	        					reportDate = formatter.parse(dateStr);
	        					logger.info("Report Date: " + reportDate);
	        				}
	        				catch (Exception e) {
	        					logger.warning("Unable to parse report date --- " + dateStr + "\n" + Util.getStackTrace(e));
	        					reportDate = new Date();
	        				}
	        				// continue
	        				return true;
	            		}
	            	}
	            }
  	          	if ("p".equalsIgnoreCase(tagName)) {
  	            	String className = tag.getAttributeByName("class");
  	            	if (className != null && className.equalsIgnoreCase("MsoNormal")) {
  	            		report = new ReportDO();
    	            	report.setReportedBy(PROVIDER);
    	            	report.setState(STATE);
    	            	report.setReportDate(reportDate);
  	            	}
  	            }
  	            else if ("b".equalsIgnoreCase(tagName) && report != null) {
	            	tagValue = tag.getText().toString();
	            	if (tagValue != null) {
	            		String t = tagValue.trim();
	            		if (report.getKeyword() != null) {
	            			t = report.getKeyword() + t;
	            		}
	            		report.setKeyword(t);
	            		StringBuilder sb = new StringBuilder();
	  	            	sb.append(STATE);
	  	            	sb.append(":");
	  	            	String uniqueKey = report.getKeyword();
	  	            	uniqueKey = uniqueKey.trim();
	  	            	uniqueKey = uniqueKey.toUpperCase();
	  	            	uniqueKey = AppCommon.itrim(uniqueKey);
	  	            	uniqueKey = EscapeChars.forXML(uniqueKey);
	  	            	sb.append(uniqueKey);
	  	            	report.setReportKey(sb.toString());
	            	}
	            }
  	            else if ("span".equalsIgnoreCase(tagName) && report != null) {
  	               tagValue = tag.getText().toString();
  	               if (tagValue != null) {
  	            	   String t = tagValue.trim();
  	            	   if (t.equalsIgnoreCase(report.getKeyword())) {
  	            		   return true;
  	            	   }
  	               }
  	               if (tagValue != null && tagValue.length() < 25) {
  	            	   return true;
  	               }
  	               if (tagValue != null) {
  	            	   if (report.getKeyword() != null) {
  	            		   report.setReportBody(tagValue);
  	            		   reportDao.addOrUpdateReport(report);
  	            	   	}
  	            	   	report = null;
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
				flag = true;
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
 	         	root.traverse(new OklahomaTagNodeVisitor());
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
