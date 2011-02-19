package com.zarcode.data.webcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.hsr.geohash.WGS84Point;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.apphosting.api.DeadlineExceededException;
import com.zarcode.common.PlatformCommon;
import com.zarcode.common.Util;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.data.model.WaterResourceDO;

/**
 * This is a web GeoRSS Webcrawler for accessing Google Maps feeds.
 * 
 * @author Administrator
 */
public class GoogleGeoRSSCrawler extends WebCrawler {

	private Logger logger = Logger.getLogger(GoogleGeoRSSCrawler.class.getName());
	
	private final String[] URL_LIST =  {
			// Southwest Region
			"http://maps.google.com/maps/ms?ie=UTF8&hl=en&vps=2&jsv=242c&msa=0&output=georss&msid=115945530916463422167.0004881ff70543eab31e6"
	};
	
 
	@Override
    public void doCrawl(HttpServletRequest req) throws WebCrawlException {
    	int i = 0;
    	int j = 0;
    	int k = 0;
    	String msg = null;
    	String urlStr = null;
    	String rssLink = null;
    	String rssTitle = null;
    	String rssDesc = null;
    	String temp = null;
		int itemCount = 0;
 		int memberCount = 0;
 		Node itemNode = null;
 		WaterResourceDO res = null;
 		HashMap props = null;
 		List<WGS84Point> polygon = null;
		OutputStream os = null;
 		Document doc = null;
 		int MAX_TIME_THREHOLD = 40;
 		String[] targetList = null;
 		
 		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
 		
 		// starting timestamp
 		Date startTimestamp = new Date();
 		
 		String urlParam = req.getParameter("url");
 		String startIndexParam = req.getParameter("start");
 		String deleteParam = req.getParameter("delete");
 		int startingIndex = Integer.parseInt(startIndexParam);
 		boolean deleteFlag = Boolean.parseBoolean(deleteParam);
 		logger.info("Starting index --> " + startingIndex + " Parameter Map: " + req.getParameterMap().toString());
 		
 		if (urlParam != null) {
 			targetList = new String[1];
 			logger.info("Using user-provided URL: " + urlParam);
 			targetList[0] = urlParam;
 		}
 		else {
 			targetList = URL_LIST;
 			logger.info("Using default URL(s)." + urlParam);
 		}
    	
    	try {
    		
    		for (k=0; k<targetList.length; k++) {
    			
    			// get URLs
    			urlStr = targetList[k];
    			
    			logger.info("Google map: " + urlStr);
    			
	            URL url = new URL(urlStr);
	            InputStream is = url.openStream();

	            
	 			//
	            // load XML RSS feed into local DOM
	            //
	 			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        factory.setNamespaceAware(true);
		        factory.setIgnoringComments(true);
		        factory.setIgnoringElementContentWhitespace(true);
		        DocumentBuilder builder = factory.newDocumentBuilder();
	 			doc = builder.parse(is);
	 			
	 			Node n = null;
	 			WaterResourceDao dao = new WaterResourceDao();
	 			

	 			///////////////////////////////////////////////////////////////////////////////
	 			//
	 			// Process header of Geo RSS feed
	 			//
	 			///////////////////////////////////////////////////////////////////////////////
	 		
	 			String nodeName = null;
	 			Date now = null;
	 			NodeList channelList = doc.getElementsByTagName("channel");
	 			
	 			if (channelList != null && channelList.getLength() > 0) {
	 				
	 				logger.info("Found channel node --> " + channelList.getLength());
	 				Node header = channelList.item(0);
	 				NodeList headerChildren = header.getChildNodes();
	 				logger.info("# of children in <channel> node: " + headerChildren.getLength());
	 				
    	        	for (j=0; j<headerChildren.getLength(); j++) {
    	        		
    	        		
    	        		n = headerChildren.item(j);
    	        		logger.info("Found <channel> children node: " + n.getNodeName());
    	        		nodeName = n.getNodeName();
    	        		if (nodeName != null) {
    	        			nodeName = nodeName.trim();
    	        		}
    	        		if ("link".equalsIgnoreCase(nodeName)) {
    	        			rssLink = n.getFirstChild().getNodeValue();
    	        			logger.info("Saving header value for link: " + rssLink);
    	        		}
    	        		else if ("title".equalsIgnoreCase(nodeName)) {
    	        			rssTitle = n.getFirstChild().getNodeValue();
    	        			logger.info("Saving header value for title: " + rssTitle);
    	        		}
    	        		else if ("description".equalsIgnoreCase(nodeName)) {
        					rssDesc = getCharacterDataFromElement((Element)n);
        					logger.info("Saving header value for description: " + rssDesc);
    	        			break;
    	        		}
    	        	}
	 			}
	 			else {
	 				logger.warning("Unable to channel node");
	 				throw new WebCrawlException("Unable to find <channel> node in RSS feed.", urlStr);
	 			}
	 			logger.info("HEADER: [ link: " + rssLink + " title: " + rssTitle + " desc: " + rssDesc + " ]");
	 			
	 			
	 			/*
	 			 * Cache XML document
	 			 */
	 			
	 			///////////////////////////////////////////////////////////////////////////////
	 			//
	 			// Process actual data in the Geo RSS feed
	 			//
	 			///////////////////////////////////////////////////////////////////////////////
	 			
	 			try {
	 				Transaction txn = ds.beginTransaction();
	 		  
	 				/*
		 			if (deleteFlag) {
		 				if (dao.deleteByRegion(rssTitle) > 0) {
		 					logger.info("Successfully, deleted regions ...");
		 				}
		 			}
		 			Queue queue = QueueFactory.getDefaultQueue();
		 			String docIdStr = newDoc.getDocId().toString();
		 			queue.add(TaskOptions.Builder.withUrl("/georsswrite").param("docId", docIdStr));
		 			*/
		 			
		 			txn.commit();

	 	        } 
	 			catch (DatastoreFailureException e) {

	 	         }
	 			
    		} // for loop for Map URLs
        } 
    	catch (DeadlineExceededException e1) {
    		logger.warning("Google HTTP Request Timeout has fired ... ");
    		Queue queue = QueueFactory.getDefaultQueue();
    		queue.add(TaskOptions.Builder.withUrl("/georssload").param("url", urlParam).param("start", "0").param("delete", "true"));
    	}
    	catch (ParserConfigurationException e) {
    		logger.severe("Unable to create XML parser to start DOM loading. [EXCEPTION]\n" + Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), urlStr);
        }
    	catch (SAXException e) {
    		logger.severe("Unable to parse incoming XML GeoRSS document. [EXCEPTION]\n" + Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), urlStr);
        }
    	catch (MalformedURLException e) {
    		logger.severe(Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), urlStr);
        }
    	catch (IOException e) {
    		logger.severe(Util.getStackTrace(e));
    		throw new WebCrawlException(e.getMessage(), urlStr);
        }
    }
	
	public String getCharacterDataFromElement(Element elem) {
		Node child = elem.getFirstChild();
	    if (child instanceof CharacterData) {
	    	CharacterData cd = (CharacterData) child;
	    	String str = cd.getData();
	    	// take of html
	    	str = str.substring(15);
	    	str = str.substring(0, str.length()-6);
	    	str = str.trim();
	    	logger.info("Data --> " + str);
	    	return str;
	    }
	   	logger.warning("First child is not instanceof 'CharacterData'");
	    return "";
	}
	
	private List<WGS84Point> convertGMLPosList2Polygon(String dataStr) {
		int i = 0;
		List<WGS84Point> res = null;
		String latLngStr = null;
		double lat = 0;
		double lng = 0;
		WGS84Point pt = null;
		int numOfPoints = 0;
		
		if (dataStr != null) {
			res = new ArrayList<WGS84Point>();
			dataStr = dataStr.trim();
			String[] pointList = dataStr.split("\n"); 
			if (pointList != null && pointList.length > 0) {
				//
				// GeoRSS is returning starting pt as the last pt as well.
				//
				numOfPoints = pointList.length - 1;
				for (i=0; i<numOfPoints; i++) {
					latLngStr = pointList[i];
					latLngStr = latLngStr.trim();
					String[] latLngList = latLngStr.split(" ");
					// should have individual points here
					if (latLngList != null && latLngList.length == 2) {
						lat = Double.parseDouble(latLngList[0]);
						lng = Double.parseDouble(latLngList[1]);
						pt = new WGS84Point(lat, lng);
						res.add(pt);
					}
					else {
						logger.warning("LatLng format is not as expected --> " + latLngStr);
					}
				}
			}
			else {
				logger.warning("LatLng List format is not as expected --> " + dataStr);
				
			}
		}
		
		return res;
	}
	
	private HashMap<String, String> convertKVString2HashMap(String dataStr) {
		int i = 0;
		String nvPair = null;
		HashMap<String, String> map = null;

		if (dataStr != null) {
			map = new HashMap<String, String>();
			String[] nameValueList = dataStr.split("\n"); 
			if (nameValueList != null && nameValueList.length > 0) {
				for (i=0; i<nameValueList.length; i++) {
					nvPair = nameValueList[i];
					String[] keyValue = nvPair.split("=");
					if (keyValue != null && keyValue.length == 2) {
						logger.info("Adding key=" + keyValue[0] + " val=" + keyValue[1]);
						map.put(keyValue[0], keyValue[1]);
					}
				}
			}
		}
		return map;
	}
}
