package com.zarcode.data.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CommentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.Utils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zarcode.common.EscapeChars;
import com.zarcode.common.Util;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.data.model.ReportDO;

public class MOWebCrawler extends WebCrawler {

	private Logger logger = Logger.getLogger(MOWebCrawler.class.getName());
	
	private static final String PROVIDER = "mdwfp.com";
	
	private final String[] URL_LIST =  {
		"extra.mdc.mo.gov",
	};
	
	
	public class MOTagNodeVisitor implements TagNodeVisitor {
	
		private String keyword = null;
  	    private String dateStr = null;
  	    private String reportStr = null;
  	    private Date reportDate = null;
  	   	private ReportDao reportDao = new ReportDao();
  	    
  	    public MOTagNodeVisitor() {
  	    }
		
		public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
  	    	ReportDO report = null;
  	    	String urlStr = null;
  	    	String tagValue = null;
  	    	boolean bOpen = false;
  	    	String resName = null;
  	    	
  	    	
  	    	// 05/10/2010
	 		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
  	    	
  	        if (htmlNode instanceof TagNode) {
  	            TagNode tag = (TagNode) htmlNode;
  	            String tagName = tag.getName();
  	            if ("h4".equalsIgnoreCase(tagName)) {
  	            	tagValue = tag.getText().toString();
  	            	if (tagValue != null && tagValue.equalsIgnoreCase("LAKES")) {
  	            		bOpen = true;
  	            	}
  	            	else if (bOpen) {
  	            		resName = tagValue.substring(0, tagValue.length()-2);
  	            		report = new ReportDO();
        				report.setReportedBy(PROVIDER);
        				report.setKeyword(resName);
        				report.setReportDate(reportDate);
        				report.setReportBody(reportStr);
        				report.setState(STATE);
  	            	}
  	            }
  	            else if ("hr".equalsIgnoreCase(tagName)) {
	            	bOpen = false;
	            }
  	            else if ("span".equalsIgnoreCase(tagName)) {
  	               String idName = tag.getAttributeByName("id");
  	               logger.info("SPAN :: " + idName); 
  	               if (idName != null) {
      	              if (idName.equalsIgnoreCase("ctl00_ContentPlaceHolder1_FormView1_NameLabel")) {
	    		    			keyword = getTagNodeContents(tag);
	    		    			logger.info("Found keyword: " + keyword);
	    		    		}
	    		    		else if (idName.equalsIgnoreCase("ctl00_ContentPlaceHolder1_FormView1_Label1")) {
	    		    			dateStr = getTagNodeContents(tag);
	    		    			logger.info("Found dateStr: " + dateStr);
	    	        			try {
	    	        				reportDate = formatter.parse(dateStr);
	    	        			}
	    	        			catch (Exception e) {
	    	        				// throw new WebCrawlException(e.getMessage(), urlStr);
	    	        			}
	    		    		}
	    		    		else if (idName.equalsIgnoreCase("ctl00_ContentPlaceHolder1_FormView1_FishingReportLabel2")) {
	    		    			reportStr = getTagNodeContents(tag);
	    	        			logger.info("Using report text: " + reportStr);
	    		    			//
		        				// create report object
		        				//
		        				report = new ReportDO();
		        				report.setReportedBy(PROVIDER);
		        				report.setKeyword(keyword);
		        				report.setReportDate(reportDate);
		        				report.setReportBody(reportStr);
		        				report.setState(STATE);
		        				StringBuilder sb = new StringBuilder();
								sb.append(STATE);
								sb.append(":");
								String uniqueKey = report.getKeyword();
								uniqueKey= uniqueKey.toUpperCase();
								uniqueKey = EscapeChars.forXML(uniqueKey);
								sb.append(uniqueKey);
								report.setReportKey(sb.toString());
								reportDao.addOrUpdateReport(report);
	    		    		}
  	               		}
  	            	}
  	        	} 
  	        	// tells visitor to continue traversing the DOM tree
  	        	return true;
  	        	
  	    	} // visit
		 
	} // MissTagNodeVisitor
	
	public static final Map<Integer, Integer> CRAWL_MAP = new HashMap<Integer, Integer>()  {
        {
             put(Calendar.THURSDAY, 1);
        }
    };

	private final String STATE = "MO";
	
	@Override
	public boolean readyToCrawl() {
		boolean flag = false;
		Calendar now = Calendar.getInstance();
	
		if (isFeedUpdated(STATE)) {
			logger.info("Feed is updated --> " + STATE);
			return flag;
		}
		else {
			Integer dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
			if (CRAWL_MAP.containsKey(dayOfWeek)) {
				flag = true;
			}
			flag = true;
		}
		return flag;
	}
	
	@Override
	public  String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	    	line = line.replace("<p>", "");
	    	line = line.replace("</p>", "");
	    	sb.append(line + "\n");
	    }
	    is.close();
	    return sb.toString();
	}

	protected String getTagNodeContents(TagNode node) {
		String res = null;
		Node n = null;
		TagNode t = null;
		int i = 0;
	
		if (node.getText() != null) {
			res = node.getText().toString();
			logger.info("Top found -- Returning res : " + res);
		}
		else {
			String found = null;
			TagNode[] list = node.getElementsByAttValue("class", "MsoNormal", true, false);
			for (i=0; i<list.length; i++) {
				t = list[i];
				if (found == null && t.getText() != null) {
					found = t.getText().toString();
					logger.info("Found option : " + found);
				}
				else if (t.getText() != null) {
					String str = t.getText().toString();
					if (str != null && str.length() > found.length()) {
						found = str;
						logger.info("Found better option : " + found);
					}
				}
			}
			res = found;
		}
		
		return res;
	}

	@Override
    public void doCrawl(HttpServletRequest req) throws WebCrawlException {
    	int i = 0;
    	int j = 0;
    	int k = 0;
    	String msg = null;
    	String urlStr = null;
		String keyword = null;
	 	Date reportDate = null;
    	
    	try {
    		for (k=0; k<URL_LIST.length; k++) {
    			urlStr = URL_LIST[k];
    			logger.info("Processing URL: " + urlStr);
	            URL url = new URL(urlStr);
	            /*
	             * start-up HtmlCleaner
	             */
	            CleanerProperties props = new CleanerProperties();
	         	props.setTranslateSpecialEntities(true);
	         	props.setTransResCharsToNCR(true);
	         	props.setOmitComments(true);
	         	TagNode root = new HtmlCleaner(props).clean(url);
	         	root.traverse(new MOTagNodeVisitor());
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
