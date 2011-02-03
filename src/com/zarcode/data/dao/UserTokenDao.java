package com.zarcode.data.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import com.zarcode.data.model.BucketDO;
import com.zarcode.data.model.UserTokenDO;
import com.zarcode.platform.dao.BaseDao;

public class UserTokenDao extends BaseDao {
	
	private Logger logger = Logger.getLogger(UserTokenDao.class.getName());
	
	private static long MSEC_IN_DAY = 86400000;

	
	public UserTokenDO getTokenByLlId(String llId) {
		UserTokenDO found = null;
		List<UserTokenDO> res = null;
		Query query = pm.newQuery(UserTokenDO.class);
		query.setFilter("llId == llIdParam");
		query.declareParameters("String llIdParam");
		res = (List<UserTokenDO>)query.execute(llId);
		if (res != null && res.size() > 0) {
			found = res.get(0);
		}
		return found;
	}
	
	public UserTokenDO getTokenByTokenStr(String token) {
		UserTokenDO found = null;
		List<UserTokenDO> res = null;
		Query query = pm.newQuery(UserTokenDO.class);
		query.setFilter("token == tokenParam");
		query.declareParameters("String tokenParam");
		res = (List<UserTokenDO>)query.execute(token);
		if (res != null && res.size() > 0) {
			found = res.get(0);
		}
		return found;
	}
	
	public UserTokenDO generateToken(String llId) {
		UserTokenDO newToken = null;
		UserTokenDO token = getTokenByLlId(llId);
		if (token == null) {
			token = new UserTokenDO();
			Date now = new Date();
			token.setExpiredVal(now.getTime() + MSEC_IN_DAY);
			String t = generateUniqueStr();
			token.setToken(t);
			token.setLlId(llId);
			token.setTokenId(null);
			newToken = pm.makePersistent(token); 
		}
		else {
			Date now = new Date();
			token.setExpiredVal(now.getTime() + MSEC_IN_DAY);
			String t = generateUniqueStr();
			token.setToken(t);
			newToken = token;
		}
		return newToken;
	}
	
	private String generateUniqueStr() {
		String val = UUID.randomUUID().toString();
		val = val.replace("-", "");
		return val;
	}
	
}
