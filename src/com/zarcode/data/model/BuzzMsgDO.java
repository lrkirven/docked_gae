package com.zarcode.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ch.hsr.geohash.GeoHash;

import com.google.appengine.api.datastore.Text;
import com.zarcode.app.AppCommon;
import com.zarcode.platform.model.AbstractLoaderDO;

@XmlRootElement(name = "BuzzMsg") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BuzzMsgDO extends AbstractLoaderDO implements Serializable, Comparable<BuzzMsgDO> {
	
	private static final int MAX_MESSAGE_LENGTH = 140;
	
	private Logger logger = Logger.getLogger(BuzzMsgDO.class.getName());
	
	@NotPersistent
	private String timeDisplay = null;
	
	@NotPersistent
	private List<CommentDO> comments = null;
	
	@NotPersistent
	private String messageData = null;
	
	@NotPersistent
	private String profileUrl = null;
	
	@NotPersistent
	private String username = null;
	
	@NotPersistent
	private String userLocalTime = null;

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long msgId = null; 
	
	@Persistent
	private String title = null;

	@Persistent
	private int version = 0;
	
	@Persistent
	private Long timestamp = null;
	
	@Persistent
	private String photoUrl = null;
	
	@Persistent
	private String llId = null;
	
	@Persistent
	private String location = null;
	
	@Persistent
	private Long resourceId = null;
	
	@Persistent
	private Text messageDataText = null;
	
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
	private Date createDate = null;
	
	@Persistent
	private int badCounter = 0;
	
	@Persistent
	private int commentCounter = 0;
	
	
	public void postCreation() {
		logger.info("lat=" + lat + " lng=" + lng);
		GeoHash geoKey = GeoHash.withCharacterPrecision(lat, lng, 12);
		setGeoHashKey(geoKey.toBase32());
		createDate = new Date();
		/*
		 * check max message length
		 */
		if (messageData != null && messageData.length() > MAX_MESSAGE_LENGTH) {
			messageData = messageData.substring(0, MAX_MESSAGE_LENGTH);
		}
		messageDataText = new Text(messageData);
	}
	
	public void postReturn() {
		if (messageDataText != null) {
			this.messageData = messageDataText.getValue();
		}
		timeDisplay = AppCommon.generateTimeOffset(createDate);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Implements Comparable Interface
	//
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	public int compareTo(BuzzMsgDO n) {
		if (n.timestamp < this.timestamp) {
			return 1;
		}
		else if (n.timestamp == this.timestamp) {
			return 0;
		}
		else {
			return -1;
		}
	}
	
	
	@XmlElement
	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
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
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	@XmlElement
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	
	@XmlElement
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@XmlElement
	public int getBadCounter() {
		return badCounter;
	}

	public void setBadCounter(int badCounter) {
		this.badCounter = badCounter;
	}
	
	@XmlElement
	public int getCommentCounter() {
		return this.commentCounter;
	}

	public void setCommentCounter(int commentCounter) {
		this.commentCounter = commentCounter;
	}
	
	@XmlElement
	public String getTimeDisplay() {
		return timeDisplay;
	}

	public void setTimeDisplay(String timeDisplay) {
		this.timeDisplay = timeDisplay;
	}
	
	@XmlElement
	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	@XmlElement
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@XmlElement
	public String getLlId() {
		return llId;
	}

	public void setLlId(String llId) {
		this.llId = llId;
	}
	
	@XmlElement
	public String getMessageData() {
		return messageData;
	}

	public void setMessageData(String messageData) {
		this.messageData = messageData;
	}
	
	public void setComments(List<CommentDO> list) {
		this.comments = list;
	}
	
	@XmlElement
	public List<CommentDO> getComments() {
		return comments;
	}
	
	@XmlElement
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@XmlElement
	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String url) {
		this.profileUrl = url;
	}
	
	@XmlElement
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
    @XmlElement
	public Date getCreateDate() {
		return createDate;
	}
    
    public void setUserLocalTime(String userLocalTime) {
		this.userLocalTime = userLocalTime;
	}
    
    @XmlElement
    public String getUserLocalTime() {
    	return userLocalTime;
    }

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String toString() {
		return "BuzzMsgDO::" + msgId + " (llId=" + llId + " timestamp=" + timestamp + ")";
	}

}