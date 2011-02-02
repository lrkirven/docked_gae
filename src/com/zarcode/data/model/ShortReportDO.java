package com.zarcode.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.appengine.api.datastore.Text;
import com.zarcode.app.AppCommon;
import com.zarcode.platform.model.AbstractLoaderDO;

@XmlRootElement(name = "ShortReport") 
public class ShortReportDO implements Serializable {

	@NotPersistent
	private String timeDisplay = null;
	
	@NotPersistent
	private Long reportId;
	
	@NotPersistent
	private String state = null;

	@NotPersistent
	private String keyword = null;
	
	public ShortReportDO() {
	}
	
	public ShortReportDO(ReportDO report) {
		this.timeDisplay = report.getTimeDisplay();
		this.reportId = report.getReportId();
		this.state = report.getState();
		this.keyword = report.getKeyword();
	}
	
	@XmlElement
	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	
	
	@XmlElement
	public String getTimeDisplay() {
		return timeDisplay;
	}

	public void setTimeDisplay(String timeDisplay) {
		this.timeDisplay = timeDisplay;
	}
	
	@XmlElement
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	@XmlElement
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}