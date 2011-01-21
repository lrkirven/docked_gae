package com.zarcode.data.resources;

import java.nio.ByteBuffer;
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
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.Util;
import com.zarcode.data.dao.EventDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.platform.model.AppPropDO;
import com.zarcode.data.model.CommentDO;
import com.zarcode.data.model.MsgEventDO;
import com.zarcode.data.model.WaterResourceDO;
import com.zarcode.security.BlockTea;

@Path("/events")
public class MsgEvent extends ResourceBase {
	
	private Logger logger = Logger.getLogger(MsgEvent.class.getName());
	
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
	@Path("/{resourceId}/msgEvent")
	public MsgEventDO addMsgEventToRegion(@PathParam("resourceId") Long resourceId, @QueryParam("id") String id, String event,  @QueryParam("fbPostFlag") int fbPostFlag) {
		List<MsgEventDO> res = null;
		EventDao dao = null;
		WaterResourceDao waterResDao = null;
		UserDao userDao = null;
		int rows = 0;
		MsgEventDO evt = null;
		MsgEventDO newEvent = null;
	
		logger.info("Process NEW event: " + event);
		
		if (fbPostFlag == 1) {
			logger.info("Let's try to post this message to fb as well ...");
			/*
			 TinyFBClient fb = new TinyFBClient( "Application ID", "Secret Key");
			 TreeMap<String, String> tm = new TreeMap<String, String>();
			 int status;
			 ClientResponse c = null;

			 // get friends and display one at random
			 tm.put("method", "friends.get");
			 currentUsersFriends = fb.call(tm);
			 JSONArray resultArray = new JSONArray(currentUsersFriends);
			 */
		}
		
		if (context != null) {
			if (!context.isSecure()) {
				logger.warning("*** REJECTED -- Request is not SECURE ***");
				return newEvent;
			}
		}
		
		userDao = new UserDao();
		
		if (!userDao.isValidUser(id, false)) {
			logger.warning("*** REJECTED AN INVALID ID [" + id + "] FROM NETWORK ***");
			return newEvent;
		}
		
		if (event != null && event.length() > 0) {
			evt = new Gson().fromJson(event, MsgEventDO.class);
			try {
				if (evt != null) {
					evt.postCreation();
					dao = new EventDao();
					newEvent = dao.addEvent(evt);
					//
					// since event was created inside lake area, update last communication
					//
					if (evt.getResourceId() > 0) {
						try {
							waterResDao = new WaterResourceDao();
							waterResDao.updateLastUpdate(evt.getResourceId());
							logger.info("Updated lastUpdated for resource=" + evt.getResourceId());
						}
						catch (JDOObjectNotFoundException ex) {
							logger.severe("Unable to update lastUpdated timestamp for water resource");
						}
					}
					logger.info("Successfully added new event -- " + newEvent);
				}
			}
			catch (Exception e) {
				logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
			}
		}
		else {
			logger.warning("Event JSON instance is empty");
		}
		logger.info("Returning: " + newEvent);
		return newEvent;
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
	
	private void testEncrypt() {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		byte key[] = prop.getStringValue().getBytes();
		String src = "lrkirven@gmail.com";
		byte plainSource[] = src.getBytes();
		logger.info("*** Test 1");
		BlockTea.BIG_ENDIAN = false;
		String encrypted = BlockTea.encrypt(src, prop.getStringValue());
		logger.info("Encrypted ---->" + encrypted + "<---");
		String decrypted = BlockTea.decrypt(encrypted, prop.getStringValue());
		logger.info("Decrypted ---->" + decrypted + "<---");
		
		logger.info("*** Test 2");
		String str = "hello world";
		try {
			int[] l = BlockTea.strToLongs2(str.getBytes("UTF-8"));
			for (int i=0; i<l.length; i++) {
				logger.info(i + ") " + Integer.toHexString(l[i])); 
			}
			ByteBuffer buf = BlockTea.longsToStr2(l);
			logger.info("RESULT: " + new String(buf.array()));
		}
		catch (Exception e) {
		}
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
