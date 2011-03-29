package com.zarcode.data.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import com.zarcode.data.model.GeoHash2ResourceMapDO;
import com.zarcode.data.model.ReportDO;
import com.zarcode.data.model.ShortReportDO;
import com.zarcode.data.model.WaterResourceDO;
import com.zarcode.platform.dao.BaseDao;

public class ReportDao extends BaseDao {
	
	private Logger logger = Logger.getLogger(ReportDao.class.getName());

	private static final int PAGESIZE = 25;

	
	public void addOrUpdateReport(ReportDO report) {
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			StringBuilder key = new StringBuilder();
			key.append(report.getState());
			key.append(":");
			key.append(report.getKeyword());
			ReportDO r = getReportByReportKey(key.toString());
	
			if (r == null) {
				report.setReportId(null);
				report.setLastUpdated(new Date());
				pm.makePersistent(report); 
				logger.info("Adding new report --> " + report.getReportId());
			}
			else {
				r.setLastUpdated(new Date());
				r.setReportBody(report.getReportBody());
				pm.makePersistent(r);
				logger.info("Updating report --> " + r.getReportId());
			}
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
	}  // addOrUpdateReport
	
	public ReportDO getReportById(Long reportId) {
		ReportDO res = null;
		res = pm.getObjectById(ReportDO.class, reportId);
		res.postReturn();
		return res;
	}
	
	public List<ReportDO> getReportsByState(String state) {
		int i = 0;
		ReportDO report = null;
		List<ReportDO> res = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("state == ");
		sb.append("'");
		sb.append(state);
		sb.append("'");
		Query query = pm.newQuery(ReportDO.class);
		query.setFilter("state == stateParam");
		query.setOrdering("keyword asc");
		query.declareParameters("String stateParam");
		res = (List<ReportDO>)query.execute(state);
		
		if (res != null && res.size() > 0) {
			for (i=0; i<res.size(); i++) {
				report = res.get(i);
				report.postReturn();
			}
		}
		return res;
		
	} // getReportsByState
	
	public List<ShortReportDO> getShortReportsByState(String state) {
		int i = 0;
		ReportDO report = null;
		List<ReportDO> res = null;
		List<ShortReportDO> modList = null;
		ShortReportDO s = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("state == ");
		sb.append("'");
		sb.append(state);
		sb.append("'");
		Query query = pm.newQuery(ReportDO.class);
		query.setFilter("state == stateParam");
		query.setOrdering("keyword asc");
		query.declareParameters("String stateParam");
		res = (List<ReportDO>)query.execute(state);
		
		if (res != null && res.size() > 0) {
			modList = new ArrayList<ShortReportDO>();
			for (i=0; i<res.size(); i++) {
				report = res.get(i);
				report.postReturn();
				s = new ShortReportDO(report);
				modList.add(s);
			}
		}
		return modList;
		
	} // getReportsByState
	
	public long deleteByState(String state) {
		long rows = 0;
		Query query = pm.newQuery(ReportDO.class);
	    query.setFilter("state == stateParam");
	    query.declareParameters("String stateParam");
	    rows = query.deletePersistentAll(state);
	    return rows;
	}
	
	
	ReportDO getReportByReportKey(String reportKey) {
		List<ReportDO> res = null;
		ReportDO report = null;
		Query query = null;
		
		//
		// build where clause
		//
		StringBuilder sb = new StringBuilder();
		sb.append("reportKey == reportKeyParam");
		
		query = pm.newQuery(ReportDO.class, sb.toString());
		query.declareParameters("String reportKeyParam");
		res = (List<ReportDO>)query.execute(reportKey);
		
		if (res != null && res.size() > 0) {
			report = res.get(0);
		}
		return report;
	}
	
	/*
	public List<ReportDO> getReportsByState(String state) {
		List<ReportDO> res = null;
		Query query = pm.newQuery(ReportDO.class);
		query.setFilter("state == stateParam");
		query.declareParameters("String stateParam");
		query = pm.newQuery(ReportDO.class);
		res = (List<ReportDO>)query.execute(state);
		return res;
	}
	
	public List<ReportDO> getReportsById(Long resId) {
		List<ReportDO> res = null;
		Query query = pm.newQuery(ReportDO.class, "resourceId == resourceIdParam");
		query.declareParameters("Long resourceIdParam");
		query = pm.newQuery(ReportDO.class);
		res = (List<ReportDO>)query.execute(resId);
		return res;
	}
	
	public List<ReportDO> getReportsByRadius(double lat, double lng, int radius) {
		int i = 0; 
		ReportDO report = null;
		List<ReportDO> list = null;
	
		Query query = pm.newQuery(ReportDO.class);
		query = pm.newQuery(ReportDO.class);
		query.setFilter("latestFlag == latestFlagParam");
		query.declareParameters("boolean latestFlagParam");
		List<ReportDO> res = (List<ReportDO>)query.execute(true);
		if (res != null && res.size() > 0) {
			int count = (res == null ? 0 : res.size());
			logger.info("getReportsByRadius(): Found " + count + " report(s)");
			list = new ArrayList<ReportDO>();
			for (i=0; i<count; i++) {
				report = res.get(i);
				double dist = distanceBtwAB(lat, lng, report.getLat(), report.getLng());
				if (dist < radius) {
					list.add(report);
				}
			}
		}
		return list;
	}
	*/
	
}
