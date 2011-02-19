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

@XmlRootElement(name = "PegCounter") 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class PegCounterDO implements Serializable {
	
	/**
	 * logger
	 */
	private Logger logger = Logger.getLogger(PegCounterDO.class.getName());
	
	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long pegCounterId = null; 
	
	@Persistent
	private String name = null; 
	
	@Persistent
	private Long counter = null;
	
	@Persistent
	private Long timestamp = null;
	
	
	@XmlElement
	public Long getPegCounterId() {
		return pegCounterId;
	}

	public void setPegCounterId(Long pegCounterId) {
		this.pegCounterId = pegCounterId;
	}
	
	
	@XmlElement
	public Long getCounter() {
		return counter;
	}

	public void setCounter(Long counter) {
		this.counter = counter;
	}
	
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "PegCounterDO::" + name + " counter=" + counter + ")";
	}

}