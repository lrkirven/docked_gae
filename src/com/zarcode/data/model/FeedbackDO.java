package com.zarcode.data.model;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


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
	
	public Long getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(Long feedbackId) {
		this.feedbackId = feedbackId;
	}
	
	public String getLlId() {
		return llId;
	}
	
	public void setLlId(String llId) {
		this.llId = llId;
	}
	
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
}