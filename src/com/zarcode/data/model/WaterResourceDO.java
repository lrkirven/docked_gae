package com.zarcode.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ch.hsr.geohash.WGS84Point;

import com.zarcode.app.AppCommon;
import com.zarcode.platform.model.AbstractLoaderDO;
import com.zarcode.utils.SearchJanitorUtils;

@XmlRootElement(name = "Resource") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class WaterResourceDO  extends AbstractLoaderDO implements Serializable {
	
	private Logger logger = Logger.getLogger(WaterResourceDO.class.getName());

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long resourceId;
	
	@Persistent
	private String name = null;
	
	@Persistent
	private String state = null;
	
	@Persistent
	private String region = null;
	
	@Persistent
	private List<Double> polygonPoints = null;
	
	@Persistent
	private String reportKey = null;
	
	@Persistent
	private String guid = null;
	
	@Persistent
	private Set<String> fts = null;
	
	@Persistent
	private String content = null;
	
	@Persistent
	private Date lastUpdate = null;
	
	@NotPersistent
	private int numActiveUsers = 0;
	
	@NotPersistent
	private String lastUpdateText = null;
	
	public static final int MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX = 200;
	
	public void postCreation() {
		logger.info("Entered --> " + content);
		if (content != null && content.length() > 0) {
			fts = new HashSet<String>();
        	Set<String> newFtsTokens = SearchJanitorUtils.getTokensForIndexingOrQuery(content, MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);
        	logger.info("getTokensForIndexingOrQuery returned: " + newFtsTokens);
        	fts.clear();
        	for (String token : newFtsTokens) {
        		logger.info("Adding token=" + token);
        		fts.add(token);
        	}         
		}
		else {
			logger.warning("*** content is [EMPTY] ***");
		}
	}
	
	public void postReturn() {
		lastUpdateText = AppCommon.generateTimeOffset(lastUpdate);
		logger.info("postReturn: lastUpdateText=" + lastUpdateText);
	}

	@XmlElement
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	
	@XmlElement
	public List<Double> getPolygonPoints() {
		return polygonPoints;
	}

	public void setPolygonPoints(List<Double> polygonPoints) {
		this.polygonPoints = polygonPoints;
	}
	
	public List<WGS84Point> getPolygon() {
		int i = 0;
		String latLngStr = null;
		WGS84Point pt = null;
		List<WGS84Point> res = null;
		double lat = 0;
		double lng = 0;
		
		if (polygonPoints != null && polygonPoints.size() > 0) {
			res = new ArrayList<WGS84Point>();
			for (i=0; i<polygonPoints.size(); i+=2) {
				lat = polygonPoints.get(i);
				lng = polygonPoints.get(i+1);
				pt = new WGS84Point(lat, lng);
				res.add(pt);
			}
		}
		return res;
	}

	public void setPolygon(List<WGS84Point> polygon) {
		int i = 0;
		WGS84Point pt = null;
		StringBuilder sb = new StringBuilder();
		
		if (polygon != null && polygon.size() > 0) {
			polygonPoints = new ArrayList<Double>();
			for (i=0; i<polygon.size(); i++) {
				pt = polygon.get(i);
				polygonPoints.add(pt.getLat());
				polygonPoints.add(pt.getLng());
			}
		}
	}
	
	
	@XmlElement
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@XmlElement
	public Set<String> getFts() {
		return fts;
	}

	public void setFts(Set<String> fts) {
		this.fts = fts;
	}
	
	@XmlElement
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@XmlElement
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	@XmlElement
	public String getReportKey() {
		return reportKey;
	}

	public void setReportKey(String reportKey) {
		if (state == null) {
			// reportKey format: <state-abbrev>:<lake-name>
			String[] keyParts = reportKey.split(":");
			if (keyParts != null && keyParts.length == 2) {
				state = keyParts[0];
			}
		}
		this.reportKey = reportKey;
	}
	
	@XmlElement
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@XmlElement
	public String getLastUpdateText() {
		return lastUpdateText;
	}

	public void setLastUpdateText(String t) {
		this.lastUpdateText = t;
	}
	
	@XmlElement
	public int getNumActiveUsers() {
		return numActiveUsers;
	}

	public void setNumActiveUsers(int n) {
		this.numActiveUsers = n;
	}
	
	public String toString() {
		String str = "WaterResourceDO::[reportKey=" + reportKey + " state=" + state + "]";
		return str;
	}
	
}