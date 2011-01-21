package com.zarcode.shared.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.zarcode.platform.model.AbstractLoaderDO;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class OpenIdProviderDO extends AbstractLoaderDO implements Serializable {

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long providerId  = null;

	@Persistent
	private String name = null;
	
	@Persistent
	private String url = null;
	
	@Persistent
	private String imageUrl = null;
	
	public OpenIdProviderDO() {
	}
	
	public void postCreation() {
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}