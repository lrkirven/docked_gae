package com.zarcode.data.model;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "Feedback") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FeedbackDO {
	
	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long feedbackId = null; 

	@Persistent
	private String llId = null;
	
	@Persistent
	private String emailAddr = null;
	
	@Persistent
	private String value = null;
	
	@Persistent
	private int result = 0;
	
	@Persistent
	private Date createDate = null;
	
	
	public FeedbackDO() {
	}
	
	@XmlElement
	public Long getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(Long feedbackId) {
		this.feedbackId = feedbackId;
	}
	
	@XmlElement
	public String getLlId() {
		return llId;
	}
	
	public void setLlId(String llId) {
		this.llId = llId;
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
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@XmlElement
	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
}