package com.zarcode.data.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import com.zarcode.app.AppCommon;
import com.zarcode.data.model.BucketDO;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.data.model.UserDO;
import com.zarcode.platform.dao.BaseDao;

public class BucketDao extends BaseDao {
	
	private Logger logger = Logger.getLogger(BucketDao.class.getName());

	
	public List<BucketDO> getAllActiveBuckets() {
		List<BucketDO> res = null;
		StringBuilder sb = new StringBuilder();
		Query query = pm.newQuery(BucketDO.class);
		query.setFilter("fullFlag == fullFlagParam");
		query.declareParameters("boolean fullFlagParam");
		query.setOrdering("timestamp desc");
		res = (List<BucketDO>)query.execute(false);
		return res;
	}
	
	public BucketDO addBucket(BucketDO bucket) {
		BucketDO newBucket = null;
		Transaction tx = pm.currentTransaction();
		double lat = 0;
		double lng = 0;
		
		try {
			tx.begin();
			Date now = new Date();
			bucket.setTimestamp(now.getTime());
			newBucket = pm.makePersistent(bucket); 
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
		return newBucket;
	}
	
	public void decrementRemaining(BucketDO bucket) {
		BucketDO res = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			res = pm.getObjectById(BucketDO.class, bucket.getBucketId());
			Integer rem = res.getRemainingPhotos();
			if (rem > 10) {
				rem--;
				res.setRemainingPhotos(rem);
			}
			else {
				rem--;
				res.setRemainingPhotos(rem);
				res.setFullFlag(true);
			}
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}
	
	public void markFull(BucketDO bucket) {
		BucketDO res = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			res = pm.getObjectById(BucketDO.class, bucket.getBucketId());
			res.setFullFlag(true);
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}
	
	
}
