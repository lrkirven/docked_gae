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
import com.zarcode.data.dao.RegionDao;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.data.model.ReportDO;

public class MSWebCrawler extends WebCrawler {

	private Logger logger = Logger.getLogger(MSWebCrawler.class.getName());
	
	private static final String PROVIDER = "mdwfp.com";
	
	private final String[] URL_LIST =  {
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=1",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=2",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=3",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=4",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=5",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=6",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=7",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=8",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=9",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=10",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=11",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=12",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=14",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=15",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=19",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=20",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=21",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=22",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=23",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=24",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=25",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=26",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=27",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=28",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=31",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=33",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=36",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=37",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=38",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=39",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=40",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=41",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=43",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=44",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=45",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=47",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=49",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=50",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=52",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=53",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=108",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=109",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=110",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=111",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=112",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=115",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=118",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=120",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=127",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=129",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=135",
		"http://home.mdwfp.com/Fisheries/FishingReportsInfo.aspx?id=137"
	};
	
	public class MissTagNodeVisitor implements TagNodeVisitor {
	
		private String keyword = null;
  	    private String dateStr = null;
  	    private String reportStr = null;
  	    private Date reportDate = null;
  	    private boolean bReportRegionUpdated = false;
  	    
  	    public MissTagNodeVisitor() {
  	    }
		
		public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
  	    	ReportDO report = null;
  	    	String urlStr = null;
  	    	
  	    	ReportDao reportDao = new ReportDao();
  	    	
  	    	// 05/10/2010
	 		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
  	    	
  	        if (htmlNode instanceof TagNode) {
  	            TagNode tag = (TagNode) htmlNode;
  	            String tagName = tag.getName();
  	            if ("span".equalsIgnoreCase(tagName)) {
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
	    	        				if (!bReportRegionUpdated) {
	    	        					bReportRegionUpdated = true;
	    	        					RegionDao regionDao = new RegionDao();
	    	        					regionDao.updateRegionByState(STATE, reportDate);
	    	        				}
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
        	 put(Calendar.MONDAY, 1);
             put(Calendar.TUESDAY, 1);
             put(Calendar.THURSDAY, 1);
             put(Calendar.FRIDAY, 1);
        }
    };

	private final String STATE = "MS";
	
	@Override
	public boolean readyToCrawl() {
		boolean flag = false;
		Calendar now = Calendar.getInstance();
	
		if (isFeedUpdated(STATE)) {
			logger.info("Feed is updated --> " + STATE);
			return flag;
		}
		else {
			logger.info("Feed is NOT UPDATED.");
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
	         	root.traverse(new MissTagNodeVisitor());
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
