package com.zarcode.data.resources.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;

import com.google.gdata.data.maps.FeatureEntry;
import com.zarcode.data.dao.BuzzDao;
import com.zarcode.data.dao.PegCounterDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.gdata.MapClient;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.data.model.UserDO;
import com.zarcode.data.model.WaterResourceDO;
import com.zarcode.data.resources.ResourceBase;

@Path("/v1/lakes")
public class Lake extends ResourceBase {
	
	private Logger logger = Logger.getLogger(Lake.class.getName());
	
	@Context 
	UriInfo uriInfo = null;
    
	@Context 
    Request request = null;
	
	String container = null;
	
	/*
	@GET 
	@Path("/dumpMap")
	@Produces("text/plain")
	public String queryMap() {
		int i = 0;
		int radius = 30;
		StringBuilder sb = null;
		List<EventDO> list = null;
		BuzzDao eventDao = null;
		List<FeatureEntry> features = null;
		MapClient mapClient = null;
		int counter = 0;
		
		logger.info("Entered");
		
		try {
			//
			// get keys from Google Map 
			//
			mapClient = new MapClient();
			mapClient.initialize();
			features = mapClient.getAllFeatures();
		
			//
			// get event based upon keys from map
			//
			if (features != null && features.size() > 0) {
				logger.info("Found feature(s) -- >" + features.size());
				sb = new StringBuilder();
				FeatureEntry entry = null;
				for (i=0; i<features.size(); i++) {
					entry = features.get(i);
					sb.append(entry.getTitle().getPlainText());
					sb.append("\n");
					counter++;
				}
			}
			else {
				logger.warning("Unable to find any event in the radius");
			}
			sb.append("\n");
			sb.append("TOTAL: " + counter);
		}
		catch (Exception e) {
			logger.severe(Util.getStackTrace(e));
		}
		return (sb == null ? "<EMPTY RESULTS>" : sb.toString());
	}
	*/
	
	@GET 
	@Path("/showGeoHash")
	@Produces("text/plain")
	public String showGeoHash(@QueryParam("lat") double lat, @QueryParam("lng") double lng, @QueryParam("radius") int radius) {
		int i = 0;
		StringBuilder sb = null;
		List<BuzzMsgDO> list = null;
		BuzzDao eventDao = null;
		List<FeatureEntry> features = null;
		MapClient mapClient = null;
		int counter = 0;
		logger.info("lat=" + lat + " lng=" + lng + " radius=" + radius);
		GeoHash hash = GeoHash.withCharacterPrecision(lat, lng, 12);
		return hash.toBase32();
	}

	/*
	public String findClosest(@QueryParam("lat") double lat, @QueryParam("lng") double lng) {
		int i = 0;
		StringBuilder sb = null;
		List<EventDO> list = null;
		BuzzDao eventDao = null;
		List<FeatureEntry> features = null;
		MapClient mapClient = null;
		int counter = 0;
		GeoHash hash = null;
		
		logger.info("lat=" + lat + " lng=" + lng);
		WGS84Point pt = new WGS84Point(lat, lng);
		GeoHashCircleQuery query = new GeoHashCircleQuery(pt, 30);
		List<GeoHash> res = query.getSearchHashes();
		
		if (res != null && res.size() > 0) {
			for (i=0; i<res.size(); i++) {
				hash = res.get(0);
				if (sb == null) {
					sb = new StringBuilder();
				}
				sb.append((i+1));
				sb.append(") ");
				sb.append(hash.toBase32());
				sb.append("\n");
			}
		}
		return (sb == null ? "<EMPTY RESULTS>" : sb.toString());
	}
	*/
	
	@GET 
	@Path("/closest")
	@Produces("application/json")
	public List<WaterResourceDO> findClosest(@QueryParam("lat") double lat, @QueryParam("lng") double lng) {
		int i = 0;
		StringBuilder sb = null;
		WaterResourceDao waterResDao = null;
		List<WaterResourceDO> emptySet = new ArrayList<WaterResourceDO>();
		int counter = 0;
		GeoHash hash = null;
		
		logger.info("lat=" + lat + " lng=" + lng);
		WGS84Point pt = new WGS84Point(lat, lng);
		waterResDao = new WaterResourceDao();
		List<WaterResourceDO> results = waterResDao.findClosest(lat, lng, 3);
	
		return (results == null ? emptySet : results);
	}
	
	
	@GET 
	@Path("/search")
	@Produces("application/json")
	public List<WaterResourceDO> searchLakes(@QueryParam("keyword") String keyword) {
		int i = 0;
		StringBuilder sb = null;
		WaterResourceDao waterResDao = null;
		UserDao userDao = null;
		List<WaterResourceDO> emptySet = new ArrayList<WaterResourceDO>();
		int counter = 0;
		GeoHash hash = null;
		
		logger.info("keyword=" + keyword);
		
		waterResDao = new WaterResourceDao();
		List<WaterResourceDO> results = waterResDao.search(keyword);
		
		logger.info("# of matches: " + (results == null ? 0 : results.size()));
		
		if (results.size() > 20) {
			results = results.subList(0, 20);
		}
		if (results.size() > 0) {
			userDao = new UserDao();
			WaterResourceDO lake = null;
			List<UserDO> users = null;
			for (i=0; i<results.size(); i++) {
				lake = results.get(i);
				lake.postReturn();
				users = userDao.getUsersByResourceId(lake.getResourceId());
				if (users != null && users.size() > 0) {
					PegCounterDao pegDao = new PegCounterDao();
					pegDao.update(PegCounterDao.NUM_OF_ACTIVE_USERS, users.size());
				}
			}
		}
	
		return (results == null ? emptySet : results);
	}
	
	/*
	@GET 
	@Path("/selectMap")
	@Produces("text/plain")
	public String selectMap(@QueryParam("lat") double lat, @QueryParam("lng") double lng, @QueryParam("radius") int radius) {
		int i = 0;
		StringBuilder sb = null;
		List<EventDO> list = null;
		BuzzDao eventDao = null;
		List<FeatureEntry> features = null;
		MapClient mapClient = null;
		int counter = 0;
		
		logger.info("Incoming search -- lat=" + lat + " lng=" + lng + " radius=" + radius);
		
		try {
			//
			// get keys from Google Map 
			//
			mapClient = new MapClient();
			mapClient.initialize();
			features = mapClient.findClosest(lat, lng, radius);
		
			//
			// get event based upon keys from map
			//
			if (features != null && features.size() > 0) {
				logger.info("Found feature(s) -- >" + features.size());
				sb = new StringBuilder();
				FeatureEntry entry = null;
				for (i=0; i<features.size(); i++) {
					entry = features.get(i);
					sb.append(entry.getTitle().getPlainText());
					sb.append("\n");
					counter++;
				}
			}
			else {
				logger.warning("Unable to find any event in the radius");
			}
			sb.append("\n");
			sb.append("TOTAL: " + counter);
		}
		catch (Exception e) {
			logger.severe(Util.getStackTrace(e));
		}
		return (sb == null ? "<EMPTY RESULTS>" : sb.toString());
	}
	*/
}
