package com.zarcode.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.zarcode.common.GeoUtil;

import ch.hsr.geohash.GeoHash;

@XmlRootElement(name = "HotSpot") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class HotSpotDO implements Serializable {
	
	private Logger logger = Logger.getLogger(HotSpotDO.class.getName());
	
	@NotPersistent
	private double bearing = 0;
	
	@NotPersistent
	private double distanceAwayInMiles = 0;

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long hotSpotId = null; 
	
	@Persistent
	private String desc = null;
	
	@Persistent
	private String notes = null;

	@Persistent
	private int category = 0;
	
	@Persistent
	private String llId = null;
	
	@Persistent
	private String idClear = null;
	
	@Persistent
	private String resKey = null;
	
	@Persistent
	private double lat = 0;
	
	@Persistent
	private double lng = 0;
	
	@Persistent
	private String location = null;
	
	@Persistent
	private Long geoHashBits = null;
	
	@Persistent
	private Date createDate = null;
	
	@Persistent
	private int rating = 0;
	
	@Persistent
	private boolean publicFlag = true;
	
	public void postCreation() {
		logger.info("lat=" + lat + " lng=" + lng);
		GeoHash geoKey = GeoHash.withBitPrecision(lat, lng, GeoUtil.MAX_GEOHASH_BIT_PRECISION);
		setGeoHashBits(geoKey.longValue());
	}
	
	public void postReturn() {
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Implements Comparable Interface
	//
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	@XmlElement
	public Long getHotSpotId() {
		return hotSpotId;
	}

	public void setHotSpotId(Long hotSpotId) {
		this.hotSpotId = hotSpotId;
	}
	
	@XmlElement
	public Long getGeoHashBits() {
		return geoHashBits;
	}

	public void setGeoHashBits(Long geoHashBits) {
		this.geoHashBits = geoHashBits;
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
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@XmlElement
	public boolean getPublicFlag() {
		return publicFlag;
	}

	public void setPublicFlag(boolean publicFlag) {
		this.publicFlag = publicFlag;
	}
	
	@XmlElement
	public String getResKey() {
		return resKey;
	}

	public void setResKey(String resKey) {
		this.resKey = resKey;
	}
	
	@XmlElement
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	@XmlElement
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
	
	@XmlElement
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@XmlElement
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}
	
	@XmlElement
	public double getDistanceAwayInMiles() {
		return distanceAwayInMiles;
	}

	public void setDistanceAwayInMiles(double d) {
		this.distanceAwayInMiles = d;
	}
	
    @XmlElement
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String toString() {
		return "HotSpotDO::" + hotSpotId + " (llId=" + llId + ")";
	}

}