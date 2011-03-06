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

@XmlRootElement(name = "LocalStatus") 
public class LocalStatusDO implements Serializable {

	private int numOfLocalLakers = 0;
	
	private int numOfActiveLakers = 0;
	
	private boolean userOnWater = false;
	
	private String resKey = null;
	
	private String resourceName = null;
	
	private String resourceState = null;

	private Date resourceLastUpdate = null;
	
	private String userToken = null;

	@XmlElement
	public int getNumOfLocalLakers() {
		return numOfLocalLakers;
	}

	public void setHowManyOnWater(int numOfLocalLakers) {
		this.numOfLocalLakers = numOfLocalLakers;
	}
	
	@XmlElement
	public int getNumOfLazyLakers() {
		return numOfActiveLakers;
	}

	public void setNumOfLazyLakers(int numOfLazyLakers) {
		this.numOfActiveLakers = numOfLazyLakers;
	}
	
	@XmlElement
	public String getResKey() {
		return resKey;
	}

	public void setResKey(String resKey) {
		this.resKey = resKey;
	}
	
	@XmlElement
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	@XmlElement
	public String getResourceState() {
		return resourceState;
	}

	public void setResourceState(String resourceState) {
		this.resourceState = resourceState;
	}
	
	@XmlElement
	public boolean getUserOnWater() {
		return userOnWater;
	}

	public void setUserOnWater(boolean userOnWater) {
		this.userOnWater = userOnWater;
	}
	
	@XmlElement
	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
	@XmlElement
	public Date getResourceLastUpdate() {
		return resourceLastUpdate;
	}
	
	public void setResourceLastUpdate(Date resourceLastUpdate) {
		this.resourceLastUpdate = resourceLastUpdate;
	}
	
}