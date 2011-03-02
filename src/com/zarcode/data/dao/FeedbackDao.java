package com.zarcode.data.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import com.zarcode.app.AppCommon;
import com.zarcode.data.model.FeedbackDO;
import com.zarcode.data.model.PegCounterDO;
import com.zarcode.platform.dao.BaseDao;

public class FeedbackDao extends BaseDao {
	
	private Logger logger = Logger.getLogger(FeedbackDao.class.getName());

	/**
	 * Gets all available feedback.
	 * 
	 * @return
	 */
	public List<FeedbackDO> getAll() {
		List<FeedbackDO> res = null;
		StringBuilder sb = new StringBuilder();
		Query query = pm.newQuery(FeedbackDO.class);
		res = (List<FeedbackDO>)query.execute();
		return res;
	}
	
	/**
	 * Delete single instance of user provided feedback.
	 * 
	 * @param feedback
	 */
	public void deleteInstance(FeedbackDO feedback) {
		long rows = 0;
		pm.deletePersistent(feedback);
	}

	/**
	 * Adds user feedback to be collected and mailed later.
	 * 
	 * @param feedback
	 * @return
	 */
	public FeedbackDO addFeedback(FeedbackDO feedback) {
		FeedbackDO newFeedback = null;
		Date now = new Date();
		feedback.setCreateDate(now);
		feedback.setFeedbackId(null);
		newFeedback = pm.makePersistent(feedback); 
		return newFeedback;
	}
	
}
