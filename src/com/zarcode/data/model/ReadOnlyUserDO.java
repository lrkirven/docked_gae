package com.zarcode.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zarcode.platform.model.AbstractLoaderDO;

@XmlRootElement(name = "ReadOnlyUser") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ReadOnlyUserDO extends AbstractLoaderDO implements Serializable {

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long userId = null; 
	
	@Persistent
	private String deviceId = null;
	
	@Persistent
	private String resKey = null;
	
	@Persistent
	private double lat = 0;
	
	@Persistent
	private double lng = 0;
	
	@Persistent
	private Long geoHashBits = null;
	
	@Persistent
	private String activeKey = null;
	
	@Persistent
	private Date lastUpdate = null;
	
	@Persistent
	private Date createDate = null;
	
	public ReadOnlyUserDO() {
	}
	
	public void postCreation() {
	}
	
	@XmlElement
	public Long getUserId() {
		return userId;
	}
	
	@XmlElement
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
	
	@XmlElement
	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
	
	@XmlElement
	public Long getGeoHashBits() {
		return geoHashBits;
	}

	public void setGeoHashBits(Long geoHashBits) {
		this.geoHashBits = geoHashBits;
		setLastUpdate(new Date());
	}
	
	@XmlElement
	public String getActiveKey() {
		return activeKey;
	}
	
	public void setActiveKey(String activeKey) {
		this.activeKey = activeKey;
	}
	
	@XmlElement
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	@XmlElement
	public String getResKey() {
		return resKey;
	}
	
	public void setResKey(String resKey) {
		this.resKey = resKey;
		setLastUpdate(new Date());
	}
	
	@XmlElement
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@XmlElement
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}