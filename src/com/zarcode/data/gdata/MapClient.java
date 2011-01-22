package com.zarcode.data.gdata;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gdata.client.maps.MapsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.extensions.CustomProperty;
import com.google.gdata.data.maps.FeatureEntry;
import com.google.gdata.data.maps.FeatureFeed;
import com.google.gdata.data.maps.MapEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.XmlBlob;
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.Util;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.platform.gdata.GDataClient;

public class MapClient extends GDataClient {
	
	private Logger logger = Logger.getLogger(MapClient.class.getName());
	
	private URL mapUrl = null;
	
	private String userId = null;
	
	private String mapId = null;

	public MapClient() {
	}
	
	public void initialize() throws Exception {
		super.initialize();
		myService = new MapsService("Lazy Laker");
		try {
			prop = ApplicationProps.getInstance().getProp("GMAP_DATA_USER_ID");
			if (prop == null) {
				throw new Exception("Application properties are not provisioned");
			}
			userId = prop.getStringValue();
			prop = ApplicationProps.getInstance().getProp("GMAP_DATA_MAP_ID");
			if (prop == null) {
				throw new Exception("Application properties are not provisioned");
			}
			mapId = prop.getStringValue();
			myService.setUserCredentials(username, password);
			logger.info("Authenicated!!");
		} 
		catch(AuthenticationException e) {
			logger.severe("CREDENTIALS -- [EXCEPTION]\n" + Util.getStackTrace(e));
			throw new Exception("Bad Credentials with Google Map Data Source");
		}
		try {
			mapUrl = new URL("http://maps.google.com/maps/feeds/features/" + userId + "/" + mapId + "/full");
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
			throw new Exception("Bad Url");
		}
		/*
		makeMapAPIVisible();
		*/
	}
	
	/*
	public MapEntry createDefaultMap() throws ServiceException, IOException {

	  	final URL feedUrl = new URL("http://maps.google.com/maps/feeds/maps/" + USER_ID + "/full");
	  	MapFeed resultFeed = myService.getFeed(feedUrl, MapFeed.class);
	  	URL mapUrl = new URL(resultFeed.getEntryPostLink().getHref());
	   
	  	//
	  	// Create a MapEntry object
	  	//
	  	MapEntry myEntry = new MapEntry();
	  	myEntry.setTitle(new PlainTextConstruct("FishPixGeoModel"));
	  	myEntry.setSummary(new PlainTextConstruct(""));
	  	Person author = new Person("L. Kirven", null, "lrkirven@gmail.com");
	  	myEntry.getAuthors().add(author);

	  	return myService.insert(mapUrl, myEntry);
	}
	*/
	
	public MapEntry getMap() {
		MapEntry map = null;
		
		try {
			map = myService.getEntry(mapUrl, MapEntry.class);
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		return map;
	}
	
	public MapEntry makeMapAPIVisible() {
		MapEntry map = null;
		CustomProperty prop = null;
		try {
			map = myService.getEntry(mapUrl, MapEntry.class);
			if (map != null) {
				prop = new CustomProperty();
				prop.setName("api_visible");
				prop.setValue("1");
				map.addCustomProperty(prop);
				map.update();
				logger.info("Updated map to make it 'api_visible'");
			}
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		return map;
	}
	
	public List<FeatureEntry> getAllFeatures() throws ServiceException, IOException {
		int i = 0;
		List<FeatureEntry> res = null;
		FeatureEntry entry = null;
		
		FeatureFeed featureFeed = myService.getFeed(mapUrl, FeatureFeed.class);
	    
		for (i=0; i<featureFeed.getEntries().size(); i++) {
		  
			if (res == null) {
				res = new ArrayList<FeatureEntry>();
			}
			entry = featureFeed.getEntries().get(i);
			res.add(entry);
		}
	  	return res;
	}
	
	public List<FeatureEntry> findClosest(double lat, double lng, int radiusInKilometers) {
		int i = 0;
		Long eventId = null;
		TextConstruct text = null;
		List<FeatureEntry> res = null;
		URL url = null;
		StringBuilder sb = null;
		
		logger.info("Starting search: lat=" + lat + " lng=" + lng + " radius=" + radiusInKilometers);
		
		try {
			sb = new StringBuilder();
			sb.append("http://maps.google.com/maps/feeds/features/");
			sb.append(userId);
			sb.append("/");
			sb.append(mapId);
			sb.append("/");
			sb.append("snippet?");
			sb.append("lat");
			sb.append("=");
			sb.append(lat);
			sb.append("&");
			sb.append("lng");
			sb.append("=");
			sb.append(lng);
			sb.append("&");
			sb.append("radius");
			sb.append("=");
			sb.append(radiusInKilometers);
			url = new URL(sb.toString());
			logger.info("Invoke request from feed ...");
			FeatureFeed featureFeed = myService.getFeed(url, FeatureFeed.class);
			if (featureFeed != null && featureFeed.getEntries().size() > 0) {
				logger.info("# of matches found: " + featureFeed.getEntries().size());
				res = new ArrayList<FeatureEntry>();
				for (i=0; i<featureFeed.getEntries().size(); i++) {
				    FeatureEntry entry = featureFeed.getEntries().get(i);
				    res.add(entry);
				}
			}
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		return res;
	}
	
	public String addMediaEventToMap(BuzzMsgDO event) {
		String featureId = null;
		String styleName = "#style6";
		String htmlStr = "<table style=\"width:auto\"><tr><td><img src=\"" + event.getPhotoUrl() + "\"></td></tr></table>";
		
		StringBuilder sb = new StringBuilder();
		sb.append("<Placemark>");
		sb.append("<name>");
		sb.append(event.getTimestamp());
		sb.append("</name>");
		sb.append("<description><![CDATA[");
		sb.append(htmlStr);
		sb.append("]]></description>");
		sb.append("<styleUrl>");
		sb.append(styleName);
		sb.append("</styleUrl>");
		sb.append("<Point>");
		sb.append("<coordinates>");
		sb.append(event.getLat());
		sb.append(",");
		sb.append(event.getLng());
		sb.append(",");
		sb.append(0.0);
		sb.append("</coordinates>");
		sb.append("</Point>");
		sb.append("</Placemark>");
		
		try {
			FeatureEntry entry = 
				addFeature(event.getMsgId().toString(), sb.toString());
			featureId = entry.getId();
		}
		catch (Exception e) {
			logger.severe("addEventToMap(): [EXCETPTION]\n" + Util.getStackTrace(e));
		}
		return featureId;
	}
	
	private FeatureEntry addFeature(String title, String xmlStr) throws ServiceException, IOException {
		FeatureEntry entry = new FeatureEntry();
		try {
			XmlBlob kml = new XmlBlob();
	    	kml.setBlob(xmlStr);
	    	//
	    	// Set the KML for this feature
	    	// Note that the KML should only include <Placemark> entries
	    	//
	    	entry.setKml(kml);
	    	entry.setTitle(new PlainTextConstruct(title));
	  	}	 
	  	catch(NullPointerException e) {
		 	 logger.severe("addFeature(): [EXCEPTION]\n" + Util.getStackTrace(e));
	  	}
	  	//
	  	// Insert the feature entry using the #post URL
	  	//
	  	return myService.insert(mapUrl, entry);
	  	
	} // addFeature

}
