package com.zarcode.data.model;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.zarcode.platform.model.AbstractLoaderDO;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GeoHash2ResourceMapDO extends AbstractLoaderDO implements Serializable {
	
	private Logger logger = Logger.getLogger(GeoHash2ResourceMapDO.class.getName());

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long mapId = null; 
	
	@Persistent
	private Long resourceId;
	
	@Persistent
	private String region = null;
	
	@Persistent
	private String map = null;
	
	@Persistent
	private String geoHashKey12 = null;
	
	@Persistent
	private String geoHashKey6 = null;
	
	@Persistent
	private String geoHashKey4 = null;
	
	@Persistent
	private String geoHashKey2 = null;
	
	public void postCreation() {
	}
	
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}
	
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	
	public String getGeoHashKey() {
		return geoHashKey12;
	}

	public void setGeoHashKey(String geoHashKey) {
		this.geoHashKey12 = geoHashKey;
		this.geoHashKey6 = geoHashKey.substring(0, 6);
		this.geoHashKey4 = geoHashKey.substring(0, 4);
		this.geoHashKey2 = geoHashKey.substring(0, 2);
	}
	
	public String toString() {
		return "GeoHash2ResourceMapDO::" + mapId;
	}

}