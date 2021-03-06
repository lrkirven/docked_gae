package com.zarcode.data.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import com.zarcode.app.AppCommon;
import com.zarcode.data.model.ReportDO;
import com.zarcode.data.model.ReportRegionDO;
import com.zarcode.data.model.UserDO;
import com.zarcode.platform.dao.BaseDao;
import com.zarcode.platform.loader.AbstractLoaderDao;
import com.zarcode.common.*;

public class RegionDao extends BaseDao implements AbstractLoaderDao {
	
	private Logger logger = Logger.getLogger(RegionDao.class.getName());

	
	public List<ReportRegionDO> getAllRegions() {
		List<ReportRegionDO> res = null;
		StringBuilder sb = new StringBuilder();
		Query query = pm.newQuery(ReportRegionDO.class);
		res = (List<ReportRegionDO>)query.execute();
		return res;
	}
	
	public ReportRegionDO addRegion(ReportRegionDO region) {
		ReportRegionDO newRegion = null;
		Date now = new Date();
		region.setLastUpdated(now);
		region.setRegionId(null);
		newRegion = pm.makePersistent(region); 
		logger.info("addRegion(): Added ReportRegion --> " + newRegion);
		return newRegion;
	}
	
	public ReportRegionDO getRegionByState(String state) {
		int i = 0;
		ReportRegionDO region = null;
		List<ReportRegionDO> res = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("state == ");
		sb.append("'");
		sb.append(state);
		sb.append("'");
		Query query = pm.newQuery(ReportRegionDO.class);
		query.setFilter("state == stateParam");
		query.declareParameters("String stateParam");
		res = (List<ReportRegionDO>)query.execute(state);
		if (res.size() > 0) {
			region = res.get(0);
		}
		return region;
		
	} // getRegionByState
	
	public void updateRegionByState(String state, Date reportDate) {
		ReportRegionDO res = null;
		Date now = new Date();
		logger.info("updateRegionByState(): Updating state --> " + state + " " + reportDate);
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			logger.info("updateRegionByState(): Getting instance for state=" + state);
			res = getRegionByState(state);
			if (res == null) {
				logger.info("updateRegionByState(): Adding missing ReportRegion ...");
				res = new ReportRegionDO();
				res.setState(state);
				res.setLastReportDate(reportDate);
				res = addRegion(res);
			}
			else {
				ReportRegionDO r = (ReportRegionDO)pm.getObjectById(ReportRegionDO.class, res.getRegionId());
				logger.info("updateRegionByState(): Updating lastUpdate/reportDate ...");
				r.setLastUpdated(now);
				r.setLastReportDate(reportDate);
			}
			tx.commit();
		}
		catch (Exception e) {
			logger.severe("updateRegionByState: FAILED -- [EXCEPTION]\n" + Util.getStackTrace(e));
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	@Override
	public void loadObject(Object dataObject) {
		this.addRegion((ReportRegionDO)dataObject);
	}
	
	
}
