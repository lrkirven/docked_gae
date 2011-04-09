package com.zarcode.data.resources;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.zarcode.common.Util;
import com.zarcode.data.exception.RequestNotSecureException;


public class ResourceBase {

	protected Cache cache = null;
	
	private static final String WEBPURIFY_API_KEY = "0c27007ca617fcfa2858805a018a3758";
	
	private static String WEB_PURIFY_URL = "http://api1.webpurify.com/services/rest/?api_key=" + WEBPURIFY_API_KEY;
	
	public ResourceBase() {
        Map props = new HashMap();
        props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
		try {
 	       CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
 	       cache = cacheFactory.createCache(props);
 	   	} 
		catch (CacheException e) {
 	   	}
	}
	
	public void requireSSL(SecurityContext context, Logger logger) throws RequestNotSecureException {
		if (context != null) {
			if (!context.isSecure()) {
				logger.severe("Somebody is trying to access the service over http (instead of https).");
				throw new RequestNotSecureException();
			}
		}
	}
	
	
	protected String doWebPurify(Logger logger, String userProvidedMsg) {
		int i = 0;
		String purifiedText = null;
	    String line = null;
		String inputLine = null;
		final String METHOD = "webpurify.live.replace";
		final String REPLACESYMBOL = "*";
		
		purifiedText = userProvidedMsg;
		
		try {
			String targetUrl = WEB_PURIFY_URL + "&method=" + METHOD + "&replacesymbol=" + REPLACESYMBOL + "&text=" + userProvidedMsg;
			
			logger.info("Trying URL: " + targetUrl);
			URL webPurifyWS = new URL(targetUrl);
			
			/*
	        BufferedReader reader = new BufferedReader(new InputStreamReader(webPurifyWS.openStream()));
	        while ((line = reader.readLine()) != null) {
	        	logger.info("LINE :: " + line);
	        }
	        reader.close();
	        */
		
			/*
			URLConnection conn = webPurifyWS.openConnection();
			conn.setRequestProperty("Accept", "application/xml");
			conn.setRequestProperty("Content-Language", "en-US");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder = factory.newDocumentBuilder(); 
			Document doc = builder.parse(conn.getInputStream());
			

			Node node = null;
			NodeList list = doc.getChildNodes();
			if (list != null && list.getLength() > 0) {
				for (i=0; i<list.getLength(); i++) {
					node = list.item(i);
					logger.info("Node Name --> " + node.getNodeName() + " Val --> " + node.getNodeValue());
				}
			}
			*/
			
		}
		catch (Exception e) {
			logger.severe("WEBPURIFY COMMUNICATION [ EXCEPTION ] ---> " + Util.getStackTrace(e));
			purifiedText = userProvidedMsg;
		}
		return purifiedText;	
	}
}
