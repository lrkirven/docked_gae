package com.zarcode.data.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.queries.GeoHashCircleQuery;

import com.zarcode.app.AppCommon;
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.GeoUtil;
import com.zarcode.data.model.ReadOnlyUserDO;
import com.zarcode.data.model.UserDO;
import com.zarcode.platform.dao.BaseDao;
import com.zarcode.platform.loader.AbstractLoaderDao;
import com.zarcode.platform.model.AppPropDO;
import com.zarcode.security.BlockTea;

public class UserDao extends BaseDao implements AbstractLoaderDao {
	
	private Logger logger = Logger.getLogger(UserDao.class.getName());
	
	/**
	 * 10 miles (16093.44 meters)
	 */
	private static final double DEFAULT_RADIUS = 16093.44;
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Implements AbstractLoaderDao Interface
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void loadObject(Object dataObject) {
		addUser((UserDO)dataObject);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	public void registerUser(UserDO user) {
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			
			user.setCreateDate(new Date());
			user.setLastUpdate(new Date());
			pm.makePersistent(user); 
			//
			// commit changes
			//
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}  // registerUser
	
	public boolean userExists(String emailAddr) {
		boolean found = false;
	
		if (emailAddr != null) {
			String eAddr = emailAddr.toLowerCase();
			Query query = pm.newQuery(UserDO.class);
			query.setFilter("emailAddr == emailAddrParam");
			query.declareParameters("String emailAddrParam");
			List<UserDO> res = (List<UserDO>)query.execute(eAddr);
			//
			// if result contains something, user is there
			//
			if (res != null && res.size() > 0) {
				found = true;
			}
		}
		
		return found;
	}
	
	public UserDO addUser(UserDO user) {
		UserDO newUser = null;
		Transaction tx = pm.currentTransaction();
		double lat = 0;
		double lng = 0;
		
		try {
			tx.begin();
			Date now = new Date();
			user.setCreateDate(now);
			user.setLastUpdate(now);
			if (user.getEmailAddr() != null) {
				String email = user.getEmailAddr();
				user.setEmailAddr(email.toLowerCase());
			}
			String dateString = AppCommon.generateActiveKey();
			logger.info("activeKey: " + dateString);
			user.setActiveKey(dateString);
			newUser = pm.makePersistent(user); 
			//
			// commit changes
			//
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return newUser;
		
	}  // addUser
	
	public void addReadOnlyUser(ReadOnlyUserDO user) {
		Transaction tx = pm.currentTransaction();
		double lat = 0;
		double lng = 0;
		
		try {
			tx.begin();
			Date now = new Date();
			user.setCreateDate(now);
			user.setLastUpdate(now);
			String dateString = AppCommon.generateActiveKey();
			logger.info("activeKey: " + dateString);
			user.setActiveKey(dateString);
			pm.makePersistent(user); 
			//
			// commit changes
			//
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}  // addReadOnlyUser
	
	public void updateResource(Long userId, String resKey) {
		UserDO target = null;
		String dateString = AppCommon.generateActiveKey();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			target = pm.getObjectById(UserDO.class, userId);
			if (target != null) {
				target.setResKey(resKey);
				target.setLastUpdate(new Date());
				target.setActiveKey(dateString);
			}
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}
	
	
	public void updateResourceAnonymous(Long userId, String resKey) {
		ReadOnlyUserDO target = null;
		String dateString = AppCommon.generateActiveKey();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			target = pm.getObjectById(ReadOnlyUserDO.class, userId);
			if (target != null) {
				target.setResKey(resKey);
				target.setLastUpdate(new Date());
				target.setActiveKey(dateString);
			}
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}
	
	public UserDO getAndUpdateUser(String llId, double lat, double lng) {
		UserDO target = null;
		String dateString = AppCommon.generateActiveKey();
		
		logger.info("getAndUpdateUser(): activeKey=" + dateString);
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query query = pm.newQuery(UserDO.class);
			query.setFilter("llId == llIdParam");
			query.declareParameters("String llIdParam");
			List<UserDO> res = (List<UserDO>)query.execute(llId);
			if (res != null && res.size() > 0) {
				target = res.get(0);
				target.setLat(lat);
				target.setLng(lng);
				target.setActiveKey(dateString);
				GeoHash geoHashKey = GeoHash.withBitPrecision(lat, lng, GeoUtil.MAX_GEOHASH_BIT_PRECISION);
				target.setGeoHashBits(geoHashKey.longValue());
			}
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return target;
		
	} // getAndUpdateUser
	
	public UserDO updateDisplayName(UserDO user, String value) {
		UserDO target = null;
		String dateString = AppCommon.generateActiveKey();
		Date now = new Date();
		
		logger.info("updateDisplayName(): activeKey=" + dateString);
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query query = pm.newQuery(UserDO.class);
			query.setFilter("userId == userIdParam");
			query.declareParameters("String userIdParam");
			List<UserDO> res = (List<UserDO>)query.execute(user.getUserId());
			if (res != null && res.size() > 0) {
				target = res.get(0);
				target.setActiveKey(dateString);
				target.setLastUpdate(now);
				target.setDisplayName(value);
			}
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return target;
		
	} // updateDisplayName
	
	public UserDO updateProfileUrl(UserDO user, String value) {
		UserDO target = null;
		String dateString = AppCommon.generateActiveKey();
		Date now = new Date();
		
		logger.info("updateProfileUrl(): activeKey=" + dateString);
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query query = pm.newQuery(UserDO.class);
			query.setFilter("userId == userIdParam");
			query.declareParameters("String userIdParam");
			List<UserDO> res = (List<UserDO>)query.execute(user.getUserId());
			if (res != null && res.size() > 0) {
				target = res.get(0);
				target.setActiveKey(dateString);
				target.setLastUpdate(now);
				target.setProfileUrl(value);
			}
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return target;
		
	} // updateProfileUrl
	
	public boolean isValidUser(String id, boolean encrypt) {
		UserDO target = null;
		String llId = null;

		AppPropDO prop = ApplicationProps.getInstance().getProp("CLIENT_TO_SERVER_SECRET");
		
		// TEA tea = new TEA(prop.getStringValue().getBytes());
		// tea.decrypt(crypt)
		
		llId = id;
		
		Query query = pm.newQuery(UserDO.class);
		query.setFilter("llId == llIdParam");
		query.declareParameters("String llIdParam");
		List<UserDO> res = (List<UserDO>)query.execute(llId);
			
		return (res != null && res.size() > 0); 
		
	} // isValidUser
	
	public int getTotalUsersByResKey(String resKey) {
		int total = 0;
		List<UserDO> res1 = null;
		List<ReadOnlyUserDO> res2 = null;
		
		try {
			res1 = getUsersByResKey(resKey);
			if (res1 != null) {
				total += res1.size();
			}
			res2 = this.getReadOnlyUsersByResKey(resKey);
			if (res2 != null) {
				total += res2.size();
			}
			logger.info("*** Total user(s) at resKey (" + resKey + "): " + total);
		}
		catch (Exception e) {
		}
		
		return total;
	}
	
	public List<UserDO> getUsersByResKey(String resKey) {
		List<UserDO> res = null;
		UserDO target = null;
		
		String dateString = AppCommon.generateActiveKey();
		
		logger.info("getUsersByResKey(): Entered - resKey=" + resKey +
				" activeKey=" + dateString);
		//
		// building query
		//
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("resKey == ");
		sb.append(resKey);
		sb.append(" && activeKey == ");
		sb.append("'");
		sb.append(dateString);
		sb.append("'");
		sb.append(")");
		
		Query query = pm.newQuery(UserDO.class,  sb.toString());
		res = (List<UserDO>)query.execute(resKey);
		
		logger.info("getUsersByResKey(): Matches found: " + 
				(res == null ? 0 : res.size()));
		
		return res;
	}
	
	public String getDisplayName(String llId) {
		UserDO res = null;
		List<UserDO> list = null;
		UserDO target = null;
		String displayName = null;
		
		Query query = pm.newQuery(UserDO.class);
		query.setFilter("llId == llIdParam");
		query.declareParameters("String llIdParam");
		list = (List<UserDO>)query.execute(llId);
		if (list != null && list.size() > 0) {
			target = list.get(0);
			if (target != null) {
				displayName = target.getDisplayName();
			}
		}
		return displayName;
	}
	
	@Deprecated 
	public UserDO getUserByLLID(String llId, boolean encrypted) {
		UserDO res = null;
		List<UserDO> list = null;
		UserDO target = null;
		
		if (encrypted) {
			AppPropDO prop = ApplicationProps.getInstance().getProp("CLIENT_TO_SERVER_SECRET");
			String plainText = BlockTea.decrypt(llId, prop.getStringValue());
			logger.info("Decrypted llId: " + plainText + " Encrypted llId: " + llId);
			llId = plainText;
		}
		
		Query query = pm.newQuery(UserDO.class);
		query.setFilter("llId == llIdParam");
		query.declareParameters("String llIdParam");
		list = (List<UserDO>)query.execute(llId);
		if (list != null && list.size() > 0) {
			res = list.get(0);
		}
		return res;
	}
	
	public UserDO getUserByIdClear(String idClear) {
		UserDO res = null;
		List<UserDO> list = null;
		UserDO target = null;
		
		Query query = pm.newQuery(UserDO.class);
		query.setFilter("llId == llIdParam");
		query.declareParameters("String llIdParam");
		list = (List<UserDO>)query.execute(idClear);
		if (list != null && list.size() > 0) {
			res = list.get(0);
		}
		return res;
	}
	
	public UserDO getUserByEmailAddr(String emailAddr) {
		UserDO res = null;
		List<UserDO> list = null;
		UserDO target = null;
		
		if (emailAddr != null) {
			String email = emailAddr.toLowerCase();
			Query query = pm.newQuery(UserDO.class);
			query.setFilter("emailAddr == emailAddrParam");
			query.declareParameters("String emailAddrParam");
			list = (List<UserDO>)query.execute(email);
			if (list != null && list.size() > 0) {
				res = list.get(0);
				logger.info("Found the user with emailAddr=" + emailAddr);
			}
			else {
				logger.info("Unable to find user with emailAddr=" + emailAddr);
			}
		}
		return res;
	}
	
	public void deleteReadOnlyUser(ReadOnlyUserDO user) {
		long rows = 0;
		pm.deletePersistent(user);
	}
	
	public ReadOnlyUserDO getReadOnlyUsersByDeviceId(String deviceId) {
		ReadOnlyUserDO res = null;
		List<ReadOnlyUserDO> list = null;
		
		Query query = pm.newQuery(ReadOnlyUserDO.class);
		query.setFilter("deviceId == deviceIdParam");
		query.declareParameters("String deviceIdParam");
		list = (List<ReadOnlyUserDO>)query.execute(deviceId);
		if (list != null && list.size() > 0) {
			res = list.get(0);
		}
		return res;
	}
	
	public List<ReadOnlyUserDO> getAllReadOnlyUsers() {
		ReadOnlyUserDO res = null;
		List<ReadOnlyUserDO> list = null;
		
		Query query = pm.newQuery(ReadOnlyUserDO.class);
		list = (List<ReadOnlyUserDO>)query.execute();
		
		return list;
	}
	
	public List<ReadOnlyUserDO> getReadOnlyUsersByResKey(String resKey) {
		List<ReadOnlyUserDO> list = null;
		
		String dateString = AppCommon.generateActiveKey();
		
		logger.info("getReadOnlyUsersByResourceId(): Entered - resKey=" + resKey +
				" activeKey=" + dateString);
		//
		// building query
		//
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("resKey == ");
		sb.append(resKey);
		sb.append(" && activeKey == ");
		sb.append("'");
		sb.append(dateString);
		sb.append("'");
		sb.append(")");
		
		Query query = pm.newQuery(ReadOnlyUserDO.class,  sb.toString());
		list = (List<ReadOnlyUserDO>)query.execute(resKey);
		
		logger.info("getUsersByResourceId(): Matches found: " + 
				(list == null ? 0 : list.size()));
		
		return list;
	}
	
	public List<UserDO> getActiveUsers() {
		List<UserDO> res = null;
		UserDO target = null;
		
		Query query = pm.newQuery(UserDO.class);
		query.setFilter("activeKey == activeKeyParam");
		query.declareParameters("String activeKeyParam");
		String dateString = AppCommon.generateActiveKey(); 
		logger.info("Using activeKey=" + dateString);
		res = (List<UserDO>)query.execute(dateString);
		/*
		res = (List<UserDO>)query.execute();
		*/
		return res;
	}
	
	public List<UserDO> findClosest(double lat, double lng) {
		int i = 0;
		int retryCounter = 0;
		List<UserDO> res = null;
		List<GeoHash> geoKeys = null;
		GeoHashCircleQuery geoQuery = null;
		double radius = DEFAULT_RADIUS;
		
		logger.info("Starting with lat=" + lat + " lng=" + lng + " radius=" + radius);
		
		WGS84Point pt = new WGS84Point(lat, lng);
		
		geoQuery = new GeoHashCircleQuery(pt, radius);
		geoKeys = geoQuery.getSearchHashes();
		
		while (retryCounter < 3) {
			if (geoKeys != null && geoKeys.size() > 0) {
				res = _findClosest(geoKeys);
				if (res != null && res.size() > 0) {
					break;
				}
				retryCounter++;
				radius = radius * 2;
				geoQuery = new GeoHashCircleQuery(pt, radius);
				geoKeys = geoQuery.getSearchHashes();
				logger.info("Trying again with radius=" + radius);
			}
			else {
				radius = radius * 2;
				geoQuery = new GeoHashCircleQuery(pt, radius);
				geoKeys = geoQuery.getSearchHashes();
				logger.info("Trying again with radius=" + radius);
			}
		}
		
		return res;
	}
	
	private List<UserDO> _findClosest(List<GeoHash> geoKeys) {
		int i = 0;
		GeoHash hash = null;
		List<UserDO> res = null;
		List<UserDO> results = new ArrayList<UserDO>();
		
		logger.info("# of geo hash key(s) found: " + geoKeys.size());
		
		Transaction tx = pm.currentTransaction();
		try {
			// tx.begin();
			//
			// only get keys of objects
			//
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			int keyCount = geoKeys.size();
			String geoHashKeyStr = null;
			for (i=0; i<keyCount; i++) {
				hash = geoKeys.get(i);
				geoHashKeyStr = hash.toBase32();
				logger.info( i + ") geoHashKeyStr: " + geoHashKeyStr);
				if (geoHashKeyStr.length() == 6) {
					sb.append("geoHashKey6 == ");
					sb.append("'");
					sb.append(geoHashKeyStr);
					sb.append("'");
				}
				else if (geoHashKeyStr.length() == 5) {
					sb.append("geoHashKey4 == ");
					sb.append("'");
					sb.append(geoHashKeyStr.substring(0, 4));
					sb.append("'");
				}
				else if (geoHashKeyStr.length() == 4) {
					sb.append("geoHashKey4 == ");
					sb.append("'");
					sb.append(geoHashKeyStr);
					sb.append("'");
				}
				else if (geoHashKeyStr.length() == 3) {
					sb.append("geoHashKey2 == ");
					sb.append("'");
					sb.append(geoHashKeyStr.substring(0, 2));
					sb.append("'");
				}
				else if (geoHashKeyStr.length() == 2) {
					sb.append("geoHashKey2 == ");
					sb.append("'");
					sb.append(geoHashKeyStr);
					sb.append("'");
				}
				if ((i+1) < keyCount) {
					sb.append(" || ");
				}
			}
			sb.append(" && activeKey == ");
			String dateString = AppCommon.generateActiveKey();
			sb.append("'");
			sb.append(dateString);
			sb.append("'");
			
			sb.append(")");
			logger.info("Query string: " + sb.toString());
			Query query = pm.newQuery(UserDO.class, sb.toString());
			res = (List<UserDO>)query.execute();
			
			////////////////////////////////////////////////////////////////////////
			//
			// only return the active users
			//
			////////////////////////////////////////////////////////////////////////
			int j = 0;
			UserDO u = null;
			String activeKey = AppCommon.generateActiveKey();
			if (res != null && res.size() > 0) {
				for (j=0; j<res.size(); j++) {
					u = res.get(j);
					if (u != null && u.getActiveKey().equalsIgnoreCase(activeKey)) {
						results.add(u);
					}
				}
			}
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		
		logger.info("_findClosest(): Exit");
		
		return results;
	}
	
}
