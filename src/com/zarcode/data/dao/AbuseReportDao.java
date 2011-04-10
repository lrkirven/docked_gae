package com.zarcode.data.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;

import com.zarcode.data.model.AbuseReportDO;
import com.zarcode.data.model.FeedbackDO;
import com.zarcode.platform.dao.BaseDao;

public class AbuseReportDao extends BaseDao {
	
	private Logger logger = Logger.getLogger(AbuseReportDao.class.getName());

	/**
	 * Gets all available feedback.
	 * 
	 * @return
	 */
	public List<AbuseReportDO> getAll() {
		List<AbuseReportDO> res = null;
		StringBuilder sb = new StringBuilder();
		Query query = pm.newQuery(AbuseReportDO.class);
		res = (List<AbuseReportDO>)query.execute();
		return res;
	}
	
	/**
	 * Delete single instance of user provided feedback.
	 * 
	 * @param feedback
	 */
	public void deleteInstance(AbuseReportDO report) {
		long rows = 0;
		pm.deletePersistent(report);
	}

	public AbuseReportDO addReport(AbuseReportDO report) {
		AbuseReportDO newReport = null;
		Date now = new Date();
		report.setReportDate(now);
		report.setAbuseReportId(null);
		newReport = pm.makePersistent(report); 
		return newReport;
	}
	
}
