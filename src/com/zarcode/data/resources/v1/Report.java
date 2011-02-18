package com.zarcode.data.resources.v1;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.zarcode.common.Util;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.exception.BadRequestAppDataException;
import com.zarcode.data.model.ReportDO;
import com.zarcode.data.model.ShortReportDO;
import com.zarcode.data.model.WaterResourceDO;
import com.zarcode.data.resources.ResourceBase;

@Path("/v1/reports")
public class Report extends ResourceBase {
	
	private Logger logger = Logger.getLogger(Report.class.getName());
	
	@Context 
	UriInfo uriInfo = null;
    
	@Context 
    Request request = null;
	
	String container = null;
	
	private static final int MAXPAGE = 10;
	
	@GET 
	@Path("/latest/{resname}")
	@Produces("application/json")
	public ReportDO getLatestReport(@PathParam("resname") String resName) {
		int i = 0;
		List<ReportDO> list = null;
		String xmlStr = null;
		Long id = null;
		ReportDao reportDao = null;
		WaterResourceDao waterDao = null;
		WaterResourceDO res = null;
		ReportDO latestReport = null;
		
		logger.info("getLatestReport(): Entered -- resName: " + resName);
		
		try {
			waterDao = new WaterResourceDao();
			res = waterDao.getResourceByName(resName);
		}
		catch (Exception e) {
		}
		
		if (res == null) {
			logger.warning("getLatestReport(): Unable to find water resource.");
		}
		
		try {
			if (res != null) {
				// list = reportDao.getReportsById(res.getResourceId());
				if (list != null && list.size() > 0) {
					latestReport = list.get(0);
				}
			}
		}
		catch (Exception e) {
			logger.severe("getLatestReport: " + Util.getStackTrace(e));
			throw new BadRequestAppDataException();
		}
		return latestReport;
	}
	
	@GET 
	@Path("/byradius/{radius}")
	@Produces("application/json")
	public List<ReportDO> getReportsByRadius(@PathParam("radius") int radius, @QueryParam("lat") double lat, @QueryParam("lng") double lng) {
		int i = 0;
		List<ReportDO> list = null;
		ReportDao reportDao = null;
		WaterResourceDO res = null;
		
		logger.info("getReportsByRadius(): Entered -- radius=" + radius);
		
		try {
			if (res != null) {
				// list = reportDao.getReportsByRadius(lat, lng, radius);
			}
		}
		catch (Exception e) {
			logger.severe("getReportsByRadius: " + Util.getStackTrace(e));
			throw new BadRequestAppDataException();
		}
		return list;
	}
	
	@GET 
	@Path("/reportId/{reportId}")
	@Produces("application/json")
	public ReportDO getReportByReportId(@PathParam("reportId") long reportId) {
		int i = 0;
		List<ReportDO> list = null;
		ReportDao reportDao = null;
		ReportDO res = null;
		
		logger.info("Requesting reportId=" + reportId);
		
		try {
			reportDao = new ReportDao();
			res = reportDao.getReportById(reportId);
		}
		catch (Exception e) {
			logger.severe("getReportByReportId: " + Util.getStackTrace(e));
			throw new BadRequestAppDataException();
		}
		return res;
	}
	
	@GET 
	@Path("/state/{state}")
	@Produces("application/json")
	public List<ReportDO> getReportsByState(@PathParam("state") String state) {
		int i = 0;
		List<ReportDO> list = null;
		ReportDao reportDao = null;
		
		logger.info("getReportsByState(): Entered -- state=" + state);
	
		if (state != null & state.length() == 2) {
			try {
				state = state.toUpperCase();
				reportDao = new ReportDao();
				list = reportDao.getReportsByState(state);
			}
			catch (Exception e) {
				logger.severe("getReportsByState: " + Util.getStackTrace(e));
				throw new BadRequestAppDataException();
			}
		}
		return list;
	}
	
	@GET 
	@Path("/lakes/{state}")
	@Produces("application/json")
	public List<ShortReportDO> getLakesByState(@PathParam("state") String state) {
		int i = 0;
		List<ShortReportDO> list = null;
		ReportDao reportDao = null;
		
		logger.info("getLakesByState(): Entered -- state=" + state);
	
		if (state != null & state.length() == 2) {
			try {
				state = state.toUpperCase();
				reportDao = new ReportDao();
				list = reportDao.getShortReportsByState(state);
			}
			catch (Exception e) {
				logger.severe("getLakesByState: " + Util.getStackTrace(e));
				throw new BadRequestAppDataException();
			}
		}
		return list;
	}
	
}
