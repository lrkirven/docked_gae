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
             put(Calendar.MONDAY, 1);
             put(Calendar.TUESDAY, 1);
             put(Calendar.THURSDAY, 1);
             put(Calendar.FRIDAY, 1);
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
  	            	   		}
  	            	   		catch (Exception e) {
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
	            
	            /*
	            
	            InputStream is = url.openStream();
	            
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
	 			Document doc = tidy.parseDOM(is, os);
	 			Date reportDate = null;
	 			
	 			// 2010-04-30
	 			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	 			//
	 			// try parsing out the reports
	 			//
	 			NodeList tableList = doc.getElementsByTagName("table");
	 			if (tableList != null && tableList.getLength() == 5) {
	 				logger.info("Found main reports table");
	 			}
	 			else {
	 				throw new WebCrawlException("Expected <table> tag count has changed", urlStr);
	 			}
	 		
	 			ReportDO report = null;
	 			Node dataTag = null;
	 			Node tdTag = null;
	 			Node aTag = null;
	 			ReportDao reportDao = new ReportDao();
	 			String str = null;
	 			List<String> textList = null;
	 			
	 			//
	 			// ASSUMPTION: Target table is index 2
	 			//
 				Node node = tableList.item(2);
    	        if (node != null) {
    	        	NodeList rowList = node.getChildNodes();
    	        	for (j=0; j<rowList.getLength(); j++) {
    	        		Node n = rowList.item(j);
    	        		if (n.getNodeName().equalsIgnoreCase("tr")) {
    	        			tdTag = n.getFirstChild();
    	        			//
    	        			// peeking into <tr> tag to see if we have the 
    	        			// right contents
    	        			//
    	        			if (tdTag != null && tdTag.getNodeName().equalsIgnoreCase("td")) {
    	        				NodeList tdData = n.getChildNodes();
    	        				//
    	        				// traverse this column data 
    	        				//
    	        				// 1) <b><a href="/hotspots/detailed.php?id=1165257723">Baker Reservoir</a></b>
    	        				// 2) <br><i>2010-04-30</i><br>
    	        				// 3) <i>Fair</i>
    	        				//
    	        				if (tdData != null) {
    	        					Node tdNode = null;
    	        					int findCounter = 0;
    	        					for (k=0; k<tdData.getLength(); k++) {
    	        						tdNode = tdData.item(k);
    	        						if (tdNode != null) {
    	        							logger.info("Processing node --> " + tdNode.getNodeName());
    	        							textList = new ArrayList<String>();
    	        							findMatchingNodes(tdNode, Node.TEXT_NODE, textList);
    	        							logger.info("Found Text Node(s): " + textList.size());
    	        							if (textList.size() > 0) {
    	        								//
    	        								// 0) Name of the water resource
    	        								// 1) date of the report
    	        								// 2) some general condition.. not using at this time
    	        								//
    	        								if (findCounter == 0) {
    	        									if (report == null) {
    	        										report = new ReportDO();
    	        										report.setReportedBy(PROVIDER);
    	        										report.setState(STATE);
    	        									}
    	        									report.setKeyword(textList.get(0));
    	        									StringBuilder sb = new StringBuilder();
    	    	        							sb.append(STATE);
    	    	        							sb.append(":");
    	    	        							String uniqueKey = report.getKeyword();
    	    	        							uniqueKey= uniqueKey.toUpperCase();
    	    	        							uniqueKey = EscapeChars.forXML(uniqueKey);
    	    	        							sb.append(uniqueKey);
    	    	        							report.setReportKey(sb.toString());
    	        									try {
    	        										Date d = formatter.parse(textList.get(1));
    	        										report.setReportDate(d);
    	        									}
    	        									catch (Exception e) {
    	        										throw new WebCrawlException("Unexpected date format", urlStr);
    	        									}
    	        								}
    	        								//
    	        								// 0) the actual report
    	        								//
    	        								else if (findCounter == 1) {
    	        									report.setReportBody(textList.get(0));
    	        									logger.info(report.toString());
    	        									reportDao.addOrUpdateReport(report);
    		    	        						report = null;
    	        								}
    	        								findCounter++;
    	        							}
    	        						}
    	        						else {
    	        							throw new WebCrawlException("Unexpected missing <td> tag", urlStr);
    	        						}
    	        					}
	    	        			}
    	        			}
    	        			else {
    	        				continue;
    	        			}
    	        		}
    	        	}
    	        }
    	        */
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
