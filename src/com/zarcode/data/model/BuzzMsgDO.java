package com.zarcode.data.model;

import java.io.Serializable;
import java.net.URLDecoder;
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
import javax.xml.bind.annotation.XmlTransient;

import ch.hsr.geohash.GeoHash;

import com.google.appengine.api.datastore.Text;
import com.zarcode.app.AppCommon;
import com.zarcode.common.ApplicationProps;
import com.zarcode.data.dao.BuzzDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.platform.model.AbstractLoaderDO;
import com.zarcode.platform.model.AppPropDO;
import com.zarcode.security.BlockTea;

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
	private int commentCounter = 0;
	
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
	private String idClear = null;
	
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
		
		/*
		 * URL decode
		 */
		llId = URLDecoder.decode(llId);
		
		/*
		 * decrypt llId
		 */
		AppPropDO p1 = ApplicationProps.getInstance().getProp("CLIENT_TO_SERVER_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String plainText = BlockTea.decrypt(llId, p1.getStringValue());
		logger.info("Decrypted llId: " + plainText + " Encrypted llId: " + llId);
		idClear = plainText;
	}
	
	public void postReturn(List<CommentDO> listOfComments) {
		int j = 0;
		UserDO user = null;
		UserDao userDao = new UserDao();
		CommentDO comment = null;
		if (messageDataText != null) {
			this.messageData = messageDataText.getValue();
		}
		timeDisplay = AppCommon.generateTimeOffset(createDate);
		if (listOfComments != null && listOfComments.size() > 0) {
			for (j=0; j<listOfComments.size(); j++) {
				comment = listOfComments.get(j);
				comment.postReturn();
			}
			setComments(listOfComments);
			setCommentCounter(( listOfComments == null ? 0 : listOfComments.size() ));
		}
		user = userDao.getUserByIdClear(this.idClear);
		if (user != null) {
			setProfileUrl(user.getProfileUrl());
			setUsername(user.getDisplayName());
		}
		else {
			setUsername(AppCommon.UNKNOWN);
		}
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
	
	@XmlTransient
	public String getIdClear() {
		return idClear;
	}

	public void setIdClear(String idClear) {
		this.idClear = idClear;
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