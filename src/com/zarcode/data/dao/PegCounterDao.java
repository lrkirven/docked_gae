package com.zarcode.data.dao;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import com.zarcode.data.model.BucketDO;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.data.model.PegCounterDO;
import com.zarcode.platform.dao.BaseDao;

public class PegCounterDao extends BaseDao {
	
	private Logger logger = Logger.getLogger(PegCounterDao.class.getName());
	
	/**
	 * Gets requested peg based upon the provided name.
	 * 
	 * @param pegName
	 * @return
	 */
	public PegCounterDO getPegCounter(String pegName) {
		List<PegCounterDO> res = null;
		PegCounterDO target = null;
		StringBuilder sb = new StringBuilder();
		Query query = pm.newQuery(PegCounterDO.class);
		query.setFilter("pegName == pegNameParam");
		query.declareParameters("String pegNameParam");
		res = (List<PegCounterDO>)query.execute(pegName);
		if (res != null && res.size() > 0) {
			target = res.get(0);
		}
		return target;
	}

	/**
	 * This method adds new peg for counting.
	 * 
	 * @param peg
	 * @return
	 */
	private PegCounterDO addPegCounter(PegCounterDO peg) {
		PegCounterDO newPeg = null;
		peg.setPegCounterId(null);
		peg.setCounter(new Long(0));
		peg.setLastUpdate(new Date());
		newPeg = pm.makePersistent(peg); 
		
		return newPeg;
	}
	
	public void deleteInstance(PegCounterDO peg) {
		long rows = 0;
		pm.deletePersistent(peg);
	}
	
	public List<PegCounterDO> getAllPegCounters() {
		int i = 0;
		List<PegCounterDO> list = null;
		Query query = pm.newQuery(PegCounterDO.class);
		list = (List<PegCounterDO>)query.execute();
		return list;
	}
	
	/**
	 * Increment the requested peg based upon the provided name.
	 * 
	 * @param pegName
	 * @param val
	 */
	public void increment(String pegName, long val) {
		PegCounterDO res = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			res = getPegCounter(pegName);
			if (res == null) {
				PegCounterDO p = new PegCounterDO();
				p.setPegName(pegName);
				res = addPegCounter(p);
			}
			Long count = res.getCounter();
			count += val;
			res.setLastUpdate(new Date());
			res.setCounter(count);
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}
	
	/**
	 * This method updates the actual peg count.
	 * 
	 * @param pegName
	 * @param val
	 */
	public void update(String pegName, long val) {
		PegCounterDO res = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			res = getPegCounter(pegName);
			if (res == null) {
				PegCounterDO p = new PegCounterDO();
				p.setPegName(pegName);
				res = addPegCounter(p);
			}
			res.setLastUpdate(new Date());
			res.setCounter(val);
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}
	
}
