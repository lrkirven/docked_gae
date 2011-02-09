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

	
	public UserTokenDO getTokenByIdClear(String idClear) {
		UserTokenDO found = null;
		List<UserTokenDO> res = null;
		Query query = pm.newQuery(UserTokenDO.class);
		query.setFilter("idClear == idClearParam");
		query.declareParameters("String idClearParam");
		res = (List<UserTokenDO>)query.execute(idClear);
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
	
	public UserTokenDO generateTokenByIdClear(String idClear) {
		UserTokenDO newToken = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			UserTokenDO token = getTokenByIdClear(idClear);
			if (token == null) {
				token = new UserTokenDO();
				Date now = new Date();
				token.setExpiredVal(now.getTime() + MSEC_IN_DAY);
				String t = generateUniqueStr();
				token.setToken(t);
				token.setIdClear(idClear);
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
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return newToken;
	}
	
	private String generateUniqueStr() {
		String val = UUID.randomUUID().toString();
		val = val.replace("-", "");
		return val;
	}
	
}
