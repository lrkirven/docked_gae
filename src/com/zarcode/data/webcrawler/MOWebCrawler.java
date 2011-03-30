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
	
	private static final String PROVIDER = "mdc.mo.gov";
	
	private final String[] URL_LIST =  {
		"http://extra.mdc.mo.gov/fish/fishrt/",
	};
	
	
	public class MOTagNodeVisitor implements TagNodeVisitor {
	
  	    private Date reportDate = null;
  	   	private ReportDao reportDao = new ReportDao();
  	    private ReportDO report = null;
  	    private boolean bStart = false;
  	    private boolean bOpen = false;
  	    
  	    public MOTagNodeVisitor() {
  	    }
		
		public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
  	    	String tagValue = null;
  	    	String resName = null;
  	    	
  	    	// 05/10/2010
	 		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
  	    	
  	        if (htmlNode instanceof TagNode) {
  	            TagNode tag = (TagNode) htmlNode;
  	            String tagName = tag.getName();
  	            if ("h3".equalsIgnoreCase(tagName)) {
  	            	tagValue = tag.getText().toString();
  	            	if (tagValue != null && tagValue.equalsIgnoreCase("Statewide Weekly Fishing Report")) {
  	            		bStart = true;
  	            	}
  	            }
  	            else if ("h4".equalsIgnoreCase(tagName)) {
  	            	tagValue = tag.getText().toString();
  	            	if (tagValue != null && tagValue.equalsIgnoreCase("RIVERS")) {
  	            		return true;
  	            	}
  	            	if (tagValue != null && tagValue.equalsIgnoreCase("LAKES")) {
  	            		bOpen = true;
  	            	}
  	            	else if (bOpen) {
  	            		resName = tagValue.substring(0, tagValue.length()-1);
  	            		report = new ReportDO();
        				report.setReportedBy(PROVIDER);
        				report.setKeyword(resName);
        				report.setReportDate(reportDate);
        				report.setState(STATE);
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
  	            else if ("hr".equalsIgnoreCase(tagName)) {
	            	bOpen = false;
	            }
  	            else if ("p".equalsIgnoreCase(tagName)) {
  	            	if (report != null) {
  	            		tagValue = tag.getText().toString();
  	            		if (tagValue != null) {
  	            			report.setReportBody(tagValue);
  	            		}
  	            		reportDao.addOrUpdateReport(report);
  	            		report = null;
  	            	}
  	            	if (bStart) {
  	            		bStart = false;
  	            		tagValue = tag.getText().toString();
  	            		if (tagValue != null) {
  	            			String[] dateStrArry = tagValue.split("at");
  	            			if (dateStrArry.length > 0) {
  	            				String dateStr = dateStrArry[0];
  	            				try {
    	        					reportDate = formatter.parse(dateStr);
    	        				}
    	        				catch (Exception e) {
    	        				}
  	            			}
;  	            		}
  	            	}
  	        	} 
  	        }
  	        // tells visitor to continue traversing the DOM tree
  	        return true;
  	        	
  	    } // visit
		 
	} // MOTagNodeVisitor
	
	public static final Map<Integer, Integer> CRAWL_MAP = new HashMap<Integer, Integer>()  {
        {
        	put(Calendar.MONDAY, 1);
            put(Calendar.TUESDAY, 1);
            put(Calendar.THURSDAY, 1);
            put(Calendar.FRIDAY, 1);
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
				ReportDao reportDao = new ReportDao();
				long rows = reportDao.deleteByState(STATE);
				logger.info("Existing rows deleted --> " + rows);
			}
			flag = true;
		}
		return flag;
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
    	String urlStr = null;
    	
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
