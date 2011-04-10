package com.zarcode.data.model;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "AbuseReport") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AbuseReportDO {
	
	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long abuseReportId = null; 

	@NotPersistent
	private String llId = null;
	
	@Persistent
	private String idClear = null;
	
	@Persistent
	private String emailAddr = null;
	
	@Persistent
	private String value = null;
	
	@Persistent
	private int result = 0;
	
	@Persistent
	private Date reportDate = null;
	
	
	public AbuseReportDO() {
	}
	
	@XmlElement
	public Long getAbuseReportId() {
		return abuseReportId;
	}

	public void setAbuseReportId(Long abuseReportId) {
		this.abuseReportId = abuseReportId;
	}
	
	@XmlTransient
	public String getLlId() {
		return llId;
	}
	
	public void setLlId(String llId) {
		this.llId = llId;
	}
	
	@XmlTransient
	public String getIdClear() {
		return idClear;
	}

	public void setIdClear(String idClear) {
		this.idClear = idClear;
	}
	
	@XmlTransient
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	@XmlElement
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@XmlElement
	public Date getReportDate() {
		return reportDate;
	}
	
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@XmlElement
	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AbuseReportDO::\n\n");
		sb.append("Reported By: ");
		sb.append(emailAddr);
		sb.append("\n\n");
		sb.append("Report Data: ");
		sb.append(value);
		sb.append("\n\n");
		sb.append("Report Date: ");
		sb.append(reportDate);
		sb.append("\n\n");
		return sb.toString();
	}
	
}