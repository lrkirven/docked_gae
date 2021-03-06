package com.zarcode.data.webcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import com.zarcode.common.EscapeChars;
import com.zarcode.common.Util;
import com.zarcode.data.dao.RegionDao;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.data.model.ReportDO;

public class TXWebCrawler extends WebCrawler {
	
	private static final String PROVIDER = "tpwd.state.tx.us";

	private Logger logger = Logger.getLogger(TXWebCrawler.class.getName());
	
	private final String[] URL_LIST =  {
		"http://www.tpwd.state.tx.us/fishboat/fish/action/reptform1.php?water=Fresh",
		"http://www.tpwd.state.tx.us/fishboat/fish/action/reptform1.php?water=Salt"
	};
	
	public static final Map<Integer, Integer> CRAWL_MAP =  new HashMap<Integer, Integer>() 
    {
        {
             put(Calendar.TUESDAY, 1);
             put(Calendar.THURSDAY, 1);
             put(Calendar.FRIDAY, 1);
        	 put(Calendar.SATURDAY, 1);
        }
    };
	
	private final String STATE = "TX";
	
	public class TexasTagNodeVisitor implements TagNodeVisitor {
		
  	    private Date reportDate = null;
  	    private ReportDO report = null;
  	    private boolean reportsFound = false;
  	    private ReportDao reportDao = new ReportDao();
  	    private boolean bReportRegionUpdated = false;
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
    	        				if (!bReportRegionUpdated) {
    	        					bReportRegionUpdated = true;
    	        					RegionDao regionDao = new RegionDao();
    	        					regionDao.updateRegionByState(STATE, reportDate);
    	        				}
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
		
		if (isFeedUpdated(STATE)) {
			logger.info("Feed is updated --> " + STATE);
			return flag;
		}
		else {
			logger.info("Feed is NOT UPDATED.");
			Calendar now = Calendar.getInstance();
			Integer dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
			if (CRAWL_MAP.containsKey(dayOfWeek)) {
				flag = true;
				ReportDao reportDao = new ReportDao();
				long rows = reportDao.deleteByState(STATE);
				logger.info("Existing rows deleted --> " + rows);
			}
			else {
				logger.warning("Not schedued to run today");
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
