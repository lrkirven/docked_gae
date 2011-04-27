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
import com.zarcode.app.GeoRssUtil;
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
public class GoogleKMLCrawler extends WebCrawler {

	private Logger logger = Logger.getLogger(GoogleKMLCrawler.class.getName());
	
	private List<WGS84Point> convertLinearRing2Polygon(String dataStr) {
		int i = 0;
		List<WGS84Point> res = null;
		String latLngStr = null;
		double lat = 0;
		double lng = 0;
		WGS84Point pt = null;
		int numOfPoints = 0;
		
		logger.info("Incoming string: " + dataStr);
		
		if (dataStr != null) {
			res = new ArrayList<WGS84Point>();
			dataStr = dataStr.trim();
			String[] pointList = dataStr.split("\n"); 
			if (pointList != null && pointList.length > 0) {
				//
				// GeoRSS is returning starting pt as the last pt as well.
				//
				numOfPoints = pointList.length - 1;
				logger.info("Num of points found: " + numOfPoints);
				for (i=0; i<numOfPoints; i++) {
					latLngStr = pointList[i];
					logger.info(i + ") " + latLngStr);
					latLngStr = latLngStr.trim();
					String[] latLngList = latLngStr.split(",");
					// should have individual points here
					if (latLngList != null && latLngList.length == 3) {
						lat = Double.parseDouble(latLngList[1]);
						lng = Double.parseDouble(latLngList[0]);
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
	
	@Override
    public void doCrawl(HttpServletRequest req) throws WebCrawlException {
    	int i = 0;
    	int j = 0;
    	int k = 0;
    	String urlStr = null;
    	String name = null;
    	String desc = null;
 		WaterResourceDO res = null;
 		HashMap props = null;
 		List<WGS84Point> polygon = null;
		OutputStream os = null;
 		Document doc = null;
 		String[] targetList = null;
 		
 		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
 		
 		// starting timestamp
 		Date startTimestamp = new Date();
 		
 		String urlParam = req.getParameter("url");
 		String region = req.getParameter("region");
 		
 		if (urlParam != null) {
 			targetList = new String[1];
 			logger.info("Using user-provided URL: " + urlParam);
 			targetList[0] = urlParam;
 		}
 		else {
 			logger.severe("URL was not provided.");
 			return;
 		}
    	
    	try {
    		
			logger.info("Google map url: " + urlParam);
			
            URL url = new URL(urlParam);
            InputStream is = url.openStream();
            
 			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);
	        factory.setIgnoringComments(true);
	        factory.setIgnoringElementContentWhitespace(true);
	        DocumentBuilder builder = factory.newDocumentBuilder();
 			doc = builder.parse(is);
 			
 			Node n = null;
 			
 			WaterResourceDao dao = new WaterResourceDao();
 			long count = dao.deleteByRegion(region);
 			logger.info("Number of resources deleted: " + count);
 			
 			

 			///////////////////////////////////////////////////////////////////////////////
 			//
 			// Process header of Geo RSS feed
 			//
 			///////////////////////////////////////////////////////////////////////////////
 			
 			StringBuilder kBuilder = null;
 			String nodeName = null;
 			Date now = new Date();
 			NodeList markList = doc.getElementsByTagName("Placemark");
 			
 			if (markList != null && markList.getLength() > 0) {
 				
	 			logger.info("Found Placemark nodes --> " + markList.getLength());
	 			
 				for (i=0; i<markList.getLength(); i++) {
	 				Node header = markList.item(i);
	 				NodeList headerChildren = header.getChildNodes();
	 				logger.info("# of children in <Placemark> node: " + headerChildren.getLength());
	 				
	 				res = new WaterResourceDO();
	 				res.setRegion(region);
	 				res.setMap(region);
	 				res.setGuid("KML" + now.getTime());
	 				
		        	for (j=0; j<headerChildren.getLength(); j++) {
		        		n = headerChildren.item(j);
		        		logger.info("Found <Placemark> children node: " + n.getNodeName());
		        		nodeName = n.getNodeName();
		        		if (nodeName != null) {
		        			nodeName = nodeName.trim();
		        		}
		        		if ("name".equalsIgnoreCase(nodeName)) {
		        			name = n.getFirstChild().getNodeValue();
		        			logger.info("Found name: " + name);
		        			res.setName(name);
		        			res.setContent(name);
		        		}
		        		else if ("description".equalsIgnoreCase(nodeName)) {
		        			desc = getCharacterDataFromElement((Element)n);
		        			logger.info("Found description: " + desc);
		        			props = convertKVString2HashMap(desc);
 	        				if (props != null && props.containsKey("reportKey")) {
 	        					String resKey = (String)props.get("reportKey");
 	        					String[] keyParts = resKey.split(":");
 	        					String itemState = null;
 	        					String actualKey = null;
 	        					if (keyParts != null && keyParts.length == 2) {
 	        						itemState = keyParts[0];
 	        						actualKey = keyParts[1];
 	        						res.setState(itemState);
 	        						kBuilder =  new StringBuilder();
 	        						kBuilder.append(itemState);
 	        						kBuilder.append(actualKey.hashCode());
 	        						logger.info("Setting resKey: " + kBuilder.toString());
 	        						res.setResKey(kBuilder.toString());
 	        					}
 	        					else {
 	        						logger.warning("reportKey is not formatting correctly -- " + resKey);
 	        					}
 	        				}
 	        				else {
 	        					logger.warning("reportKey not found for resource=" + res.getName() + 
 	        							"[descDate:" + desc + "]");
 	        				}
 	        				if (props != null && props.containsKey("alias")) {
 	        					String alias = (String)props.get("alias");
 	        					logger.info("Setting alias: " + alias);
 	        					res.setAlias(alias);
 	        				}
		        		}
		        		else if ("Polygon".equalsIgnoreCase(nodeName)) {
		        			logger.info("Found Polygon: " + name);
		        			List<String> textList = new ArrayList<String>();
		        			GeoRssUtil.findMatchingNodes(n, Node.TEXT_NODE, textList);
	    					if (textList != null && textList.size() > 0) {
	    						logger.info("Found polygonStr: " + textList.size());
	    						String polygonStr = textList.get(1);
	    						polygonStr = polygonStr.trim();
	    						logger.info("Found 2 polygonStr: " + polygonStr);
	    						polygon = convertLinearRing2Polygon(polygonStr);
	    						res.setPolygon(polygon);
	    					}
		        		}
		        	}
		        	//
		        	// add resource
		        	//
		        	dao.addObject(res);
 				}
 			}
 			else {
 				logger.warning("Unable to channel node");
 				throw new WebCrawlException("Unable to find <channel> node in RSS feed.", urlStr);
 			}
 			logger.info("HEADER: [ name: " + name + " desc: " + desc + " ]");
 			
 			
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
