package com.zarcode.data.resources;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.Util;
import com.zarcode.data.exception.RequestNotSecureException;
import com.zarcode.platform.model.AppPropDO;


public class ResourceBase {

	protected Cache cache = null;
	
	private static String WEB_PURIFY_URL = "http://api1.webpurify.com/services/rest/?api_key="; 
	
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
		AppPropDO p = ApplicationProps.getInstance().getProp("WEBPURIFY_API_KEY");
		String apiKey = p.getStringValue(); 
		
		try {
			String baseUrl = WEB_PURIFY_URL + apiKey;
			String targetUrl = baseUrl + "&method=" + METHOD + "&replacesymbol=" + REPLACESYMBOL + "&text=" + userProvidedMsg;
			logger.info("Trying URL: " + targetUrl);
			URL webPurifyWS = new URL(targetUrl);
			
			/*
			URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
			HTTPResponse response = fetcher.fetch(webPurifyWS);

			byte[] content = response.getContent();
			URL finalUrl = response.getFinalUrl();
			int responseCode = response.getResponseCode();
			logger.info("RESP CODe :: " + responseCode);
			List<HTTPHeader> headers = response.getHeaders();

			for (HTTPHeader header : headers) {
				String headerName = header.getName();
				logger.info("HEADER :: " + headerName);
				String headerValue = header.getValue();
			}
			*/

			
			/*
	        BufferedReader reader = new BufferedReader(new InputStreamReader(webPurifyWS.openStream()));
	        while ((line = reader.readLine()) != null) {
	        	logger.info("LINE :: " + line);
	        }
	        reader.close();
	        */
		
			/*
			HttpURLConnection conn = (HttpURLConnection) webPurifyWS.openConnection();
			conn.setRequestProperty("Accept", "application/xml");
			conn.setRequestProperty("Content-Language", "en-US");
			conn.addRequestProperty("Pragma","no-cache");
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			InputStream in = conn.getInputStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder = factory.newDocumentBuilder(); 
			Document doc = builder.parse(in);

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
