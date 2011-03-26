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
import com.zarcode.data.webcrawler.MissWebCrawler.MissTagNodeVisitor;

public class TexasWebCrawler extends WebCrawler {
	
	private static final String PROVIDER = "tpwd.state.tx.us";

	private Logger logger = Logger.getLogger(TexasWebCrawler.class.getName());
	
	private final String[] URL_LIST =  {
		"http://www.tpwd.state.tx.us/fishboat/fish/action/reptform1.php?water=Fresh",
		"http://www.tpwd.state.tx.us/fishboat/fish/action/reptform1.php?water=Salt"
	};
	
	public static final Map<Integer, Integer> CRAWL_MAP =  new HashMap<Integer, Integer>() 
    {
        {
             put(Calendar.MONDAY, 1);
             put(Calendar.WEDNESDAY, 1);
             put(Calendar.FRIDAY, 1);
        }
    };
	
	private final String STATE = "TX";
	
	public class TexasTagNodeVisitor implements TagNodeVisitor {
		
  	    private Date reportDate = null;
  	    private ReportDO report = null;
  	    private boolean reportsFound = false;
  	    private ReportDao reportDao = new ReportDao();
  	   	// March 23, 2010
	 	private DateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy");
  	    
  	    public TexasTagNodeVisitor() {
  	    }
		
		public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
  	    	String tagValue = null;
  	    	
  	        if (htmlNode instanceof TagNode) {
  	            TagNode tag = (TagNode) htmlNode;
  	            String tagName = tag.getName();
  	            if ("th".equalsIgnoreCase(tagName)) {
  	            	tagValue = tag.getText().toString();
  	            	if (tagValue != null && tagValue.equalsIgnoreCase("report")) {
  	            		reportsFound = true;
  	            	}
  	            }
  	            else if ("h2".equalsIgnoreCase(tagName)) {
	            	tagValue = tag.getText().toString();
	            	if (tagValue != null) {
	            		Pattern p1 = Pattern.compile("Week of.*", Pattern.CASE_INSENSITIVE);
	            		Pattern p2 = Pattern.compile("Week of ", Pattern.CASE_INSENSITIVE);
	    	    		Matcher m1 = p1.matcher(tagValue);
	    	    		if (m1.matches()) {
	    	    			Matcher m2 = p2.matcher(tagValue);
    	        			logger.info("Got a Report Date -- " + tagValue);
	    	    			String dateStr = m2.replaceAll("");
	    	    			try {
    	        				reportDate = formatter.parse(dateStr);
    	        				logger.info("Report Date: " + reportDate);
    	        			}
    	        			catch (Exception e) {
    	        				logger.warning("Unable to parse report date --- " + dateStr);
    	        			}
	    	    		}
	    	    		else {
    	        			logger.info("No Match  -- " + tagValue);
	    	    		}
	            	}
	            }
  	            else if ("a".equalsIgnoreCase(tagName) && reportsFound) {
  	               tagValue = tag.getText().toString();
  	               if (tagValue != null) {
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
  	        	} 
  	            else if ("td".equalsIgnoreCase(tagName) && reportsFound) {
  	            	tagValue = tag.getText().toString();
  	            	if (report != null && tagValue != null) {
  	            		report.setReportBody(tagValue);
  	            		// logger.info("Adding report --> " + report.getKeyword());
  	            		reportDao.addOrUpdateReport(report);
  	            		report = null;
  	            	}
  	            }
  	        }
  	            
  	        // tells visitor to continue traversing the DOM tree
  	        return true;
  	        	
  	    } // visit
		 
	} // TexasTagNodeVisitor
	
	@Override
	public boolean readyToCrawl() {
		boolean flag = false;
		
		logger.info("Entered");
		
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
			}
			else {
				logger.info("Not day of week to crawl for this state=" + STATE);
			}
		}
		return flag;
		
	} // readyToCrawl 

	@Override
    public void doCrawl(HttpServletRequest req) throws WebCrawlException {
    	int i = 0;
    	int j = 0;
    	int k = 0;
    	String msg = null;
    	String str = null;
    	
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
 	         	root.traverse(new TexasTagNodeVisitor());
    			
 	         	/*
	            URL url = new URL(str);
	            
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
	 			
	
	 			NodeList headerList = doc.getElementsByTagName("h2");
	 			if (headerList.getLength() == 2) {
	 				logger.info("Found date header");
	 				Node headerNode = headerList.item(1);
	 				if (headerNode.getNodeType() == Node.TEXT_NODE) {
	 					String headerStr = headerNode.getNodeValue();
	 					if (headerStr != null) {
	 						String dataStr = headerStr.substring(8, headerStr.length()-1);
	 						dataStr = dataStr.trim();
	 						try {
	 							// May 5, 2010
	 							DateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
	 							reportDate = (Date)formatter.parse(dataStr);
	 						}
	 						catch (Exception e) {
	 							throw new WebCrawlException("Page data format has changed", str);
	 						}
	 					}
	 					else {
	 						throw new WebCrawlException("Page header is EMPTY", str);
	 					}
	 				}
	 				else {
	 					
	 				}
	 			}
	 			else {
	 				throw new WebCrawlException("Page header might have changed", str);
	 			}
	 			
	 			//
	 			// try parsing out the reports
	 			//
	 			NodeList tableList = doc.getElementsByTagName("table");
	 			if (tableList != null && tableList.getLength() == 1) {
	 				logger.info("Found main reports table");
	 			}
	 			else {
	 				throw new WebCrawlException("Page expected table count has changed", str);
	 			}
	 		
	 			ReportDO report = null;
	 			Node dataTag = null;
	 			Node tdTag = null;
	 			List<String> textList = null;
	 			ReportDao reportDao = new ReportDao();
	 			
	 			//
	 			// find table
	 			//
	 			if (tableList != null) {
	 				Node node = null;
	 				int count = tableList.getLength();
	 				for (i=0; i<count; i++) {
	 					node = tableList.item(i);
		    	        if (node != null) {
		    	        	NodeList rowList = node.getChildNodes();
		    	        	for (j=0; j<rowList.getLength(); j++) {
		    	        		Node n = rowList.item(j);
		    	        		if (n.getNodeName().equalsIgnoreCase("tr")) {
		    	        			tdTag = n.getFirstChild();
		    	        			if (tdTag != null && tdTag.getNodeName().equalsIgnoreCase("td")) {
		    	        				//
		    	        				// retrieve data from <td> tags
		    	        				//
		    	        				textList = new ArrayList<String>();
		    	        				findMatchingNodes(tdTag, Node.TEXT_NODE, textList);
		    	        				if (textList.size() > 0) {
		    	        					report = new ReportDO();
		    	        					report.setReportedBy(PROVIDER);
		    	        					report.setState(STATE);
		    	        					report.setReportDate(reportDate);
		    	        					String tdValue = EscapeChars.forXML(textList.get(0));
		    	        					report.setKeyword(tdValue);
		    	        					StringBuilder sb = new StringBuilder();
    	        							sb.append(STATE);
    	        							sb.append(":");
    	        							sb.append(report.getKeyword());
    	        							report.setReportKey(sb.toString());
		    	        				}
		    	        			}
		    	        			else {
		    	        				continue;
		    	        			}
		    	        			//
		    	        			// get next <td> column 
		    	        			//
		    	        			NodeList tdList = n.getChildNodes();
		    	        			if (tdList != null && tdList.getLength() > 1) {
		    	        				tdTag = tdList.item(1);
		    	        				if (tdTag != null) {
		    	        					textList = new ArrayList<String>();
		    	        					findMatchingNodes(tdTag, Node.TEXT_NODE, textList);
		    	        					if (textList.size() > 0) {
		    	        						report.setReportBody(textList.get(0));
		    	        						logger.info("Adding/Updating report --> " + report.getKeyword());
		    	        						reportDao.addOrUpdateReport(report);
		    	        						report = null;
		    	        					}
		    	        				}
		    	        			}
		    	        		}
		    	        	}
		    	        }
	 				}
	 			}
	 			*/
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
