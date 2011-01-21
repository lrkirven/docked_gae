package com.zarcode.data.model;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.zarcode.platform.model.AbstractLoaderDO;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class EventSequenceDO  extends AbstractLoaderDO implements Serializable {

	@PrimaryKey 
	private String id = null; 

	@Persistent
	private Long sequenceVer = null;
	
	@Persistent
	private Long sequenceNum = null;

	public void postCreation() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Long getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(Long sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	
	public Long getSequenceVer() {
		return sequenceVer;
	}

	public void setSequenceVer(Long sequenceVer) {
		this.sequenceVer = sequenceVer;
	}
	
	public Long incrementSequenceBy(long val) {
		sequenceNum += val;
		if (sequenceVer == null) {
			sequenceVer = new Long(0);
		}
		if (sequenceNum == null) {
			sequenceNum = new Long(0);
		}
		if (this.sequenceNum == Long.MAX_VALUE) {
			this.sequenceNum = new Long(0);
			sequenceVer += val;
		}
		return sequenceNum;
	}

}