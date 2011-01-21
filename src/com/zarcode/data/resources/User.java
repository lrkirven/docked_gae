package com.zarcode.data.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.Util;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.model.LocalStatusDO;
import com.zarcode.data.model.MsgEventDO;
import com.zarcode.data.model.ReadOnlyUserDO;
import com.zarcode.data.model.UserDO;
import com.zarcode.data.model.WaterResourceDO;
import com.zarcode.platform.model.AppPropDO;
import com.zarcode.security.BlockTea;

@Path("/users")
public class User extends ResourceBase {
	
	private Logger logger = Logger.getLogger(User.class.getName());
	
	@Context 
	UriInfo uriInfo = null;
    
	String container = null;
	
	private static final int MAXPAGE = 10;
	
	private static final String ANONYMOUS = "ABC123";
	
	private static final String ANONYMOUS_KEY = "ABCDEF7891011121314";
	
	@POST
	@Produces("text/plain")
	@Path("/register")
	public String addUserStr(String userStr) {
		List<MsgEventDO> res = null;
		UserDao dao = null;
		int rows = 0;
		String resp = null;
		UserDO newUser = null;
	
		logger.info(userStr);
		
		if (userStr != null && userStr.length() > 0) {
			newUser = new Gson().fromJson(userStr, UserDO.class);
			try {
				if (newUser != null) {
					dao.registerUser(newUser);
				}
			}
			catch (Exception e) {
				logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
			}
		}
		else {
			logger.warning("User JSON instance is empty");
		}
		return resp;
	}
	
	@GET
	@Produces({"application/json", "application/xml"})
	@Path("/active")
    public List<UserDO> getActiveUsers() {
		List<UserDO> activeUsers = null;
		UserDao userDao = null;
		
		try {
			userDao = new UserDao();
			activeUsers = userDao.getActiveUsers();
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		logger.info("# of users returned: " + (activeUsers == null ? 0 : activeUsers.size()));
        return activeUsers;
	}
	
	private void testEncrypt() {
		String src = "MY_DEVICE_ID=48A9371F-DADD-5CA2-B491-A425DB3C5C8A";
		logger.info("*** Test 1");
		BlockTea.BIG_ENDIAN = false;
		String encrypted = BlockTea.encrypt(src, "ABCDEF7891011121314");
		logger.info("Encrypted ---->" + encrypted + "<---");
		String decrypted = BlockTea.decrypt(encrypted, "ABCDEF7891011121314");
		logger.info("Decrypted ---->" + decrypted + "<---");
	}
	
	@GET
	@Produces("application/json")
	@Path("/ping/{llId}")
    public LocalStatusDO ping(@PathParam("llId") String llId, @QueryParam("lat") double lat, @QueryParam("lng") double lng, @QueryParam("devId") String deviceId) {
		String resp = null;
		UserDao userDao = null;
		UserDO user = null;
		ReadOnlyUserDO readOnlyUser = null;
		WaterResourceDao waterResDao = null;
		List<UserDO> usersAtLake = null;
		List<UserDO> totalActiveUsers = null;
		LocalStatusDO empty = new LocalStatusDO();
		boolean anonymous = false;
		
		if (llId == null || llId.length() == 0) {
			return empty;
		}
		
		if (ANONYMOUS.equalsIgnoreCase(llId)) {
			anonymous = true;
			logger.info("ANONYMOUS user accessing the service.");
		}
		else {
			AppPropDO prop = ApplicationProps.getInstance().getProp("CLIENT_TO_SERVER_SECRET");
			BlockTea.BIG_ENDIAN = true;
			String plainText = BlockTea.decrypt(llId, prop.getStringValue());
			logger.info("Decrypted llId: " + plainText + " Encrypted llId: " + llId);
			llId = plainText;
		}
		
		
		//
		// status to return to user
		//
		LocalStatusDO status = new LocalStatusDO();
		
		try {
			userDao = new UserDao();
			if (anonymous) {
				BlockTea.BIG_ENDIAN = false;
				logger.info("Incoming deviceId: " + deviceId);
				String plainText = BlockTea.decrypt(deviceId, ANONYMOUS_KEY);
				String [] keys = plainText.split("=");
				if (keys.length == 2 && keys[0].equalsIgnoreCase("MY_DEVICE_ID")) {
					readOnlyUser = userDao.getReadOnlyUsersByDeviceId(keys[1]);
					//
					// if null, add the anonymous user
					//
					if (readOnlyUser == null) {
						logger.info("Adding anonymous user --- deviceId=" + keys[1]);
						readOnlyUser = new ReadOnlyUserDO();
						readOnlyUser.setDeviceId(keys[1]);
						userDao.addReadOnlyUser(readOnlyUser);
					}
					else {
						logger.info("Found previously anonymous user from device id -- " + keys[1]);
					}
				}
				else {
					logger.warning("*** A client is trying to access service with invalid deviceId --> " + plainText);
					return empty;
				}
			}
			else {
				user = userDao.getAndUpdateUser(llId, lat, lng);
			}
	
			waterResDao = new WaterResourceDao();
			//
			// best indicates that the user is inside my water resource polygon
			//
			WaterResourceDO best = waterResDao.findBestResource(lat, lng);
			//
			// build status to return to user
			//
			if (best != null) {
				status.setUserOnWater(true);
				Long resId = best.getResourceId();
				if (resId != null) {
					status.setResourceId(resId.intValue());
				}
				status.setResourceState(best.getState());
				status.setResourceName(best.getName());
				status.setResourceLastUpdate(best.getLastUpdate());
				if (anonymous) {
					userDao.updateResourceAnonymous(readOnlyUser.getUserId(), best.getResourceId());
					logger.info("Updated anonymous user [ " + readOnlyUser.getDeviceId() + " ] at resourceId=" +  best.getResourceId());
				}
				else {
					userDao.updateResource(user.getUserId(), best.getResourceId());
					logger.info("Updated user [ " + user.getUsername() + " ] at resourceId=" +  best.getResourceId());
				}
				usersAtLake = userDao.getUsersByResourceId(best.getResourceId());
				int totalUsers = userDao.getTotalUsersByResourceId(best.getResourceId());
				status.setHowManyOnWater(totalUsers);
			}
			else {
				if (anonymous) {
					userDao.updateResourceAnonymous(readOnlyUser.getUserId(), null);
				}
				else {
					userDao.updateResource(user.getUserId(), null);
					
				}
			}
			totalActiveUsers = userDao.getActiveUsers();
			if (totalActiveUsers != null) {
				status.setNumOfLazyLakers(totalActiveUsers.size());
			}
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
        return status;
    }
	
	@GET 
	@Path("/local")
	@Produces("application/json")
	public List<UserDO> getLocalUsers(@QueryParam("lat") double lat, @QueryParam("lng") double lng) {
		List<UserDO> list = null;
		List<UserDO> empty = new ArrayList<UserDO>();
		UserDao userDao = null;
		
		logger.info("Entered");
		try {
			userDao = new UserDao();
			list = userDao.findClosest(lat, lng);
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		logger.info("Exit");
		return (list == null ? empty : list);
		
	} // getLocalUsers
	
	@GET 
	@Path("/lake/{resourceId}")
	@Produces("application/json")
	public List<UserDO> getUsersByResourceId(@PathParam("resourceId") Long resourceId) {
		List<UserDO> list = null;
		List<UserDO> empty = new ArrayList<UserDO>();
		UserDao userDao = null;
		
		logger.info("Entered");
		try {
			userDao = new UserDao();
			list = userDao.getUsersByResourceId(resourceId);
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		logger.info("Exit");
		
		return (list == null ? empty : list);
		
	} // getUsersByResourceId
	
	
} // User
