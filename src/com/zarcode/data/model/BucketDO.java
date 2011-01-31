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

import com.google.appengine.api.datastore.Text;
import com.zarcode.app.AppCommon;
import com.zarcode.platform.model.AbstractLoaderDO;

@XmlRootElement(name = "Bucket") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BucketDO implements Serializable {
	
	/**
	 * logger
	 */
	private Logger logger = Logger.getLogger(BucketDO.class.getName());
	
	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long bucketId = null; 
	
	@Persistent
	private String albumId = null; 
	
	@Persistent
	private Integer remainingPhotos = null;
	
	@Persistent
	private Long bytesUsed = null;
	
	@Persistent
	private Long timestamp = null;
	
	@Persistent
	private boolean fullFlag = false;
	
	
	@XmlElement
	public Long getBucketId() {
		return bucketId;
	}

	public void setBucketId(Long bucketId) {
		this.bucketId = bucketId;
	}
	
	
	@XmlElement
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	@XmlElement
	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	
	@XmlElement
	public Integer getRemainingPhotos() {
		return remainingPhotos;
	}

	public void setRemainingPhotos(Integer remainingPhotos) {
		this.remainingPhotos = remainingPhotos;
	}
	
	@XmlElement
	public boolean getFullFlag() {
		return fullFlag;
	}

	public void setFullFlag(boolean fullFlag) {
		this.fullFlag = fullFlag;
	}
	
    @XmlElement
	public Long getBytesUsed() {
		return bytesUsed;
	}

	public void setBytesUsed(Long bytesUsed) {
		this.bytesUsed = bytesUsed;
	}
	
	public String toString() {
		return "ActiveBucketDO::" + albumId + " (bytesUsed=" + bytesUsed + ")";
	}

}