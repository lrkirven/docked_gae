package com.zarcode.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@XmlRootElement(name = "ReportSource") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ReportSourceDO implements Serializable {

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long sourceId;
	
	@Persistent
	private String name = null;
	
	@Persistent
	private String state = null;
	
	@Persistent
	private int updateDay = 0;
	
	@Persistent
	private String baseUrl = null;
	
	@XmlElement
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	
	@XmlElement
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@XmlElement
	public int getUpdateDay() {
		return updateDay;
	}

	public void setUpdateDay(int updateDay) {
		this.updateDay = updateDay;
	}
	
    @XmlElement
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}