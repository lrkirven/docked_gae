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
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.platform.loader.JDOLoaderServlet;
import com.zarcode.data.model.ReportDO;
import com.zarcode.data.webcrawler.TXWebCrawler.TexasTagNodeVisitor;

public class ARWebCrawler extends WebCrawler {
	
	private static final String PROVIDER = "http://www.agfc.com/";

	private Logger logger = Logger.getLogger(TXWebCrawler.class.getName());
	
	private final String[] URL_LIST =  {
		"http://www.agfc.com/fishing/Pages/FishingReports.aspx"
	};
	
	public static final Map<Integer, Integer> CRAWL_MAP =  new HashMap<Integer, Integer>() 
    {
        {
        	 put(Calendar.MONDAY, 1);
             put(Calendar.TUESDAY, 1);
             put(Calendar.THURSDAY, 1);
             put(Calendar.FRIDAY, 1);
        }
    };
	
	private final String STATE = "AR";
	
	public class ArkTagNodeVisitor implements TagNodeVisitor {
		
		private boolean reportsFound = false;
  	    private Date reportDate = null;
  	    private ReportDO report = null;
  	    private ReportDao reportDao = new ReportDao();
  	   	// March 23, 2010
	 	private DateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy");
	 	
  	    public ArkTagNodeVisitor(Date reportDate) {
  	    	this.reportDate = reportDate;
  	    }
  	    
  	    public void setReportDate(Date d) {
  	    	this.reportDate = d;
  	    }
		
		public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
  	    	String tagValue = null;
  	    	
  	        if (htmlNode instanceof TagNode) {
  	            TagNode tag = (TagNode) htmlNode;
  	            String tagName = tag.getName();
  	            if ("p".equalsIgnoreCase(tagName)) {
  	            	tagValue = tag.getText().toString();
  	            	String styleName = tag.getAttributeByName("style");
  	            	if (styleName != null && styleName.equalsIgnoreCase("font-size: 15px; font-weight: bold;")) {
  	            	
  	            		//
  	            		// save previous report
  	            		// 
  	            		if (report != null) {
  	            			reportDao.addOrUpdateReport(report);
  	            		}
  	            		
  	            		tagValue = tagValue.trim();
  	            		report = new ReportDO();
  	            		report.setReportedBy(PROVIDER);
  	            		report.setKeyword(tagValue);
  	            		report.setReportDate(reportDate);
  	            		report.setState(STATE);
  	            		StringBuilder sb = new StringBuilder();
  	            		sb.append(STATE);
  	            		sb.append(":");
  	            		String uniqueKey = report.getKeyword();
  	            		uniqueKey= uniqueKey.toUpperCase();
  	            		uniqueKey = EscapeChars.forXML(uniqueKey);
  	            		sb.append(uniqueKey);
  	            		report.setReportKey(sb.toString());
  	            	}
  	            	else if (report != null) {
  	            		tagValue = tag.getText().toString();
  	            		String body = null;
  	            		if (report.getReportBody() != null) {
  	            			body = report.getReportBody() + tagValue;
  	            		}
  	            		else {
  	            			body = tagValue;
  	            		}
  	            		report.setReportBody(body);
  	            		
  	            		/**
  	            		 * Special case to quit parsing
  	            		 */
  	            		if (report.getKeyword().equalsIgnoreCase("Horseshoe Lake")) {
  	            			reportDao.addOrUpdateReport(report);
  	            			return false;
  	            		}
  	            	}
  	        	} 
  	        }
  	            
  	        // tells visitor to continue traversing the DOM tree
  	        return true;
  	        	
  	    } // visit
		 
	} // ArkTagNodeVisitor
	
	@Override
	public boolean readyToCrawl() {
		boolean flag = false;
		
		if (isFeedUpdated(STATE)) {
			logger.info("Feed is updated --> " + STATE);
			return flag;
		}
		else {
			logger.info("Feed is NOT UPDATED.");
			flag = true;
			Calendar now = Calendar.getInstance();
			Integer dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
			if (CRAWL_MAP.containsKey(dayOfWeek)) {
				flag = true;
				ReportDao reportDao = new ReportDao();
				long rows = reportDao.deleteByState(STATE);
				logger.info("Existing rows deleted --> " + rows);
			}
		}
		return flag;
		
	} // readyToCrawl 

	@Override
    public void doCrawl(HttpServletRequest req) throws WebCrawlException {
    	int i = 0;
    	int j = 0;
    	int k = 0;
    	String str = null;
    	Date reportDate = null;
    	DateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy");
    	
    	try {
    		for (k=0; k<URL_LIST.length; k++) {
    			str = URL_LIST[k];
    			logger.info("Processing URL: " + str);
    			URL url = new URL(str);
 	            CleanerProperties props = new CleanerProperties();
 	            // set some properties to non-default values
 	         	props.setTranslateSpecialEntities(true);
 	         	props.setTransResCharsToNCR(true);
 	         	props.setOmitComments(true);
 	         	TagNode root = new HtmlCleaner(props).clean(url);
 	         	List<TagNode> tags = root.getElementListHavingAttribute("href", true);
 	         	Pattern p1 = Pattern.compile(".*Weekly Fishing Report$", Pattern.CASE_INSENSITIVE);
 	         	Pattern p2 = Pattern.compile(" Weekly Fishing Report$", Pattern.CASE_INSENSITIVE);
 	         	String targetUrl = null;
 	         	if (tags != null) {
 	         		for (i=0; i<tags.size(); i++) {
 	         			TagNode t = tags.get(i);
 	         			String val = t.getText().toString();
 	         			val = val.trim();
 	         			if (val != null) {
 	         				Matcher m = p1.matcher(val);
 	         				/*
 	         				 * once we have a match, start parsing at new URL
 	         				 */
 	         				if (m.matches()) {
 	         					Matcher m2 = p2.matcher(val);
 	         					String dateStr = m2.replaceAll("");
 	         					try {
 	    	        				reportDate = formatter.parse(dateStr);
 	    	        				logger.info("Report Date: " + reportDate);
 	    	        			}
 	    	        			catch (Exception e) {
 	    	        				logger.warning("Unable to parse report date --- " + dateStr);
 	    	        			}
 	         					targetUrl = t.getAttributeByName("href");
 	         					
 	         					URL url2 = new URL(targetUrl);
 	         	 	            CleanerProperties prop2 = new CleanerProperties();
 	         	 	            // set some properties to non-default values
 	         	 	            prop2.setTranslateSpecialEntities(true);
 	         	 	         	prop2.setTransResCharsToNCR(true);
 	         	 	         	prop2.setOmitComments(true);
 	         	 	         	TagNode nextRoot = new HtmlCleaner(prop2).clean(url2);
 	         	 	         	nextRoot.traverse(new ArkTagNodeVisitor(reportDate));
 	         					break;
 	         				}
 	         			}
 	         		}
 	         	}
    		}
        } 
    	catch (MalformedURLException e) {
    		logger.severe(Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), str);
        }
    	catch (IOException e) {
    		logger.severe(Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), str);
        }
    	catch (Exception e) {
    		logger.severe(Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), str);
        }
    } // doCrawl
}
