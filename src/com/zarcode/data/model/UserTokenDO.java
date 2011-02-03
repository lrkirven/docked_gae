package com.zarcode.data.model;

import java.io.Serializable;
import java.util.Calendar;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.zarcode.platform.model.AbstractLoaderDO;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserTokenDO  extends AbstractLoaderDO implements Serializable {

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long tokenId = null; 

	@Persistent
	private String llId = null;
	
	@Persistent
	private String token = null;
	
	@Persistent
	private Long expiredVal = null;;
	

	public void postCreation() {
	}
	
	public Long getTokenId() {
		return tokenId;
	}

	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}
	
	public Long getExpiredVal() {
		return expiredVal;
	}
	
	public void setExpiredVal(Long expiredVal) {
		this.expiredVal = expiredVal;
	}
	
	public String getLlId() {
		return llId;
	}
	
	public void setLlId(String llId) {
		this.llId = llId;
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public boolean isExpired() {
		boolean expired = true;
		Calendar now = Calendar.getInstance();
		if (now.getTimeInMillis() > expiredVal) {
			expired = true;
		}
		else {
			expired = false;
		}
		return expired;
	}
	
}