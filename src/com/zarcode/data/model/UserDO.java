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
import javax.xml.bind.annotation.XmlTransient;

import com.zarcode.platform.model.AbstractLoaderDO;

@XmlRootElement(name = "User") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserDO extends AbstractLoaderDO implements Serializable {

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long userId = null; 
	
	@Persistent
	private String displayName = null;
	
	@Persistent
	private String emailAddr = null;

	@Persistent
	private String llId = null;
	
	@Persistent
	private String idClear = null;
	
	@Persistent
	private String deviceId = null;
	
	@Persistent
	private String authDomain = null;
	
	@Persistent
	private Long resourceId = null;
	
	@Persistent
	private double lat = 0;
	
	@Persistent
	private double lng = 0;
	
	@Persistent
	private String geoHashKey12 = null;
	
	@Persistent
	private String geoHashKey6 = null;
	
	@Persistent
	private String geoHashKey4 = null;
	
	@Persistent
	private String geoHashKey2 = null;
	
	@Persistent
	private String activeKey = null;
	
	@Persistent
	private Date lastUpdate = null;
	
	@Persistent
	private Date createDate = null;
	
	@Persistent
	private String profileUrl = null;
	
	public UserDO() {
	}
	
	public void postCreation() {
	}
	
	@XmlElement
	public Long getUserId() {
		return userId;
	}
	
	@XmlElement
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String d) {
		this.displayName = d;
	}
	
	@XmlElement
	public String getProfileUrl() {
		return profileUrl;
	}
	
	public void setProfileUrl(String p) {
		this.profileUrl = p;
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
	public String getGeoHashKey() {
		return geoHashKey12;
	}

	public void setGeoHashKey(String geoHashKey) {
		this.geoHashKey12 = geoHashKey;
		this.geoHashKey6 = geoHashKey.substring(0, 6);
		this.geoHashKey4 = geoHashKey.substring(0, 4);
		this.geoHashKey2 = geoHashKey.substring(0, 2);
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
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	@XmlElement
	public String getLLId() {
		return llId;
	}
	
	public void setLLId(String llId) {
		this.llId = llId;
	}
	
	@XmlTransient
	public String getIdClear() {
		return idClear;
	}
	
	public void setIdClear(String idClear) {
		this.idClear = idClear;
	}
	
	@XmlElement
	public String getAuthDomain() {
		return authDomain;
	}
	
	public void setAuthDomain(String authDomain) {
		this.authDomain = authDomain;
	}
	
	@XmlElement
	public Long getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
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