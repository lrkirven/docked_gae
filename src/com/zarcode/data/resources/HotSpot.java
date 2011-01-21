package com.zarcode.data.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.zarcode.common.Util;
import com.zarcode.data.dao.EventDao;
import com.zarcode.data.dao.HotSpotDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.model.CommentDO;
import com.zarcode.data.model.HotSpotDO;
import com.zarcode.data.model.MsgEventDO;
import com.zarcode.data.model.WaterResourceDO;

@Path("/hotSpots")
public class HotSpot extends ResourceBase {
	
	private Logger logger = Logger.getLogger(HotSpot.class.getName());
	
	@Context 
	HttpHeaders headers = null;
	
	@Context 
	SecurityContext context = null;
	
	@Context 
	UriInfo uriInfo = null;
    
	@Context 
    Request request = null;
	
	String container = null;
	
	private static final int MAXPAGE = 10;
	
	@POST
	@Produces("application/json")
	@Path("/{resourceId}/hotSpot")
	public HotSpotDO addHotSpot(@PathParam("resourceId") Long resourceId, @QueryParam("id") String id,  @QueryParam("e") String emailAddr, String hotSpot) {
		List<MsgEventDO> res = null;
		HotSpotDao dao = null;
		WaterResourceDao waterResDao = null;
		UserDao userDao = null;
		int rows = 0;
		HotSpotDO spot = null;
		HotSpotDO newSpot = null;
	
		logger.info("Process NEW hotspot: " + hotSpot);
		
		if (context != null) {
			if (!context.isSecure()) {
				logger.warning("*** REJECTED -- Request is not SECURE ***");
				return newSpot;
			}
		}
		
		userDao = new UserDao();
		if (!userDao.isValidUser(id, false)) {
			logger.warning("*** REJECTED AN INVALID ID [" + id + "] FROM NETWORK ***");
			return newSpot;
		}
		
		if (hotSpot != null && hotSpot.length() > 0) {
			spot = new Gson().fromJson(hotSpot, HotSpotDO.class);
			try {
				if (spot != null) {
					spot.postCreation();
					dao = new HotSpotDao();
					newSpot = dao.addHotSpot(spot);
					//
					// since event was created inside lake area, update last communication
					//
					if (spot.getResourceId() > 0) {
						try {
							waterResDao = new WaterResourceDao();
							waterResDao.updateLastUpdate(spot.getResourceId());
							logger.info("Updated lastUpdated for resource=" + spot.getResourceId());
						}
						catch (JDOObjectNotFoundException ex) {
							logger.severe("Unable to update lastUpdated timestamp for water resource");
						}
					}
					logger.info("Successfully added new hotSpot -- " + spot);
				}
			}
			catch (Exception e) {
				logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
			}
		}
		else {
			logger.warning("Event JSON instance is empty");
		}
		logger.info("Returning: " + newSpot);
		return newSpot;
	}
	
	@POST
	@Produces("application/json")
	@Path("/{resourceId}/comment")
	public CommentDO addCommentToMsgEvent(@PathParam("resourceId") Long resourceId, @QueryParam("id") String id, String comment) {
		List<MsgEventDO> res = null;
		EventDao dao = null;
		UserDao userDao = null;
		WaterResourceDao waterResDao = null;
		int rows = 0;
		CommentDO comm = null;
		CommentDO newComm = null;
	
		logger.info("Process NEW comment: " + comment);
		
		if (context != null) {
			if (!context.isSecure()) {
				logger.warning("*** REJECTED -- Request is not SECURE ***");
				return newComm;
			}
		}
		//
		// validate user
		//
		userDao = new UserDao();
		if (!userDao.isValidUser(id, false)) {
			logger.warning("*** REJECTED AN INVALID ID [" + id + "] FROM NETWORK ***");
			return newComm;
		}
		
		if (comment != null && comment.length() > 0) {
			comm = new Gson().fromJson(comment, CommentDO.class);
			try {
				if (comm != null && comm.getMsgEventId() > 0) {
					comm.postCreation();
					dao = new EventDao();
					newComm = dao.addComment(comm);
					newComm.postReturn();
					dao.incrementCommentCounter(comm.getMsgEventId());
					//
					// since event was created inside lake area, update last communication
					//
					if (comm.getResourceId() > 0) {
						try {
							waterResDao = new WaterResourceDao();
							waterResDao.updateLastUpdate(comm.getResourceId());
							logger.info("Updated lastUpdated for resource=" + comm.getResourceId());
						}
						catch (JDOObjectNotFoundException ex) {
							logger.severe("Unable to update lastUpdated timestamp for water resource");
						}
					}
					
					logger.info("Successfully added new comment -- " + newComm);
				}
				else {
					logger.severe("*** Incoming comment did not contain REQUIRED msgEventId ***");
				}
			}
			catch (Exception e) {
				logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
			}
		}
		else {
			logger.warning("Event JSON instance is empty");
		}
		logger.info("Returning: " + newComm);
		
		return newComm;
	}
	
	@GET 
	@Path("/bylatlng")
	@Produces("application/json")
	public List<MsgEventDO> getMsgEventsByLatLng(@QueryParam("lat") double lat, @QueryParam("lng") double lng) {
		int i = 0;
		List<MsgEventDO> results = null;
		List<MsgEventDO> list = null;
		List<WaterResourceDO> resourceList = null;
		WaterResourceDO res = null;
		EventDao eventDao = null;
		WaterResourceDao waterResDao = null;
		boolean bFindAll = false;
		
		logger.info("Entered");
		try {
			eventDao = new EventDao();
			results = new ArrayList<MsgEventDO>();
			waterResDao = new WaterResourceDao();
			logger.info("QUERY: Searching for local water resources ...");
			resourceList = waterResDao.findClosest(lat, lng, 3);
			//
			// if we have found some local lakes, let's see what recents events that they 
			// have
			//
			if (resourceList != null && resourceList.size() > 0) {
				logger.info("RESULT: Found " + resourceList.size() + " local lakes ...");
				for (i=0; i<resourceList.size(); i++) {
					res = resourceList.get(i);
					list = eventDao.getNextEventsByResourceId(res.getResourceId());
					if (list != null && list.size() > 0) {
						logger.info("Num of message(s): " + list.size() + " -- at resource --> "  + res.getName() + 
								" id=" +  res.getResourceId());
						results.addAll(list);
					}
					else {
						logger.info("ZERO messages at resource --> " + res.getName() + 
								" id=" + res.getResourceId());
					}
				}
				logger.info("# of recent event(s) from resources --> " + (results == null ? 0 : results.size()));
			}
			else {
				logger.info("RESULT: No Matches.");
			}
			//
			// sort events
			//
			if (results.size() > 0) {
				Collections.sort(results);
				if (results.size() >= EventDao.PAGESIZE) {
					int start = results.size() - EventDao.PAGESIZE;
					results = results.subList(start, EventDao.PAGESIZE);
				}
			}
		
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		logger.info("Exit");
		
		return results;
		
	}
	
	@GET 
	@Path("/{resourceId}")
	@Produces("application/json")
	public List<MsgEventDO> getMsgEventsByRegion(@PathParam("resourceId") Long resourceId, @QueryParam("lat") double lat, @QueryParam("lng") double lng) {
		int i = 0;
		List<MsgEventDO> results = null;
		List<MsgEventDO> list = null;
		List<WaterResourceDO> resourceList = null;
		WaterResourceDO res = null;
		EventDao eventDao = null;
		WaterResourceDao waterResDao = null;
		boolean bFindAll = false;
		
		logger.info("Entered");
		try {
			eventDao = new EventDao();
			
			if (bFindAll) {
				results = new ArrayList<MsgEventDO>();
				waterResDao = new WaterResourceDao();
				logger.info("QUERY: Searching for local water resources ...");
				resourceList = waterResDao.findClosest(lat, lng, 3);
				//
				// if we have found some local lakes, let's see what recents events that they 
				// have
				//
				if (resourceList != null && resourceList.size() > 0) {
					logger.info("RESULT: Found " + resourceList.size() + " local lakes ...");
					for (i=0; i<resourceList.size(); i++) {
						res = resourceList.get(i);
						list = eventDao.getNextEventsByResourceId(res.getResourceId());
						if (list != null && list.size() > 0) {
							results.addAll(list);
						}
					}
					logger.info("# of recent event(s) from resources --> " + (results == null ? 0 : results.size()));
				}
				else {
					logger.info("RESULT: No Matches.");
				}
				//
				// sort events
				//
				if (results.size() > 0) {
					Collections.sort(results);
					if (results.size() >= EventDao.PAGESIZE) {
						int start = results.size() - EventDao.PAGESIZE;
						results = results.subList(start, EventDao.PAGESIZE);
					}
				}
			}
			else {
				logger.info("Trying query with resourceId=" + resourceId);
				list = eventDao.getNextEventsByResourceId(resourceId);
				results = list;
				if (results != null && results.size() > EventDao.PAGESIZE) {
					results = results.subList(0, EventDao.PAGESIZE);
				}
			}
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		logger.info("Exit");
		
		return results;
		
	}
	
} // Event
