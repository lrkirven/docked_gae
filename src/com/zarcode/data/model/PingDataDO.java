package com.zarcode.data.model;


public class PingDataDO {

	private String llId = null;
	
	private double lat = 0;
	
	private double lng = 0;
	
	private String deviceId = null;
	
	
	public PingDataDO() {
	}
	
	public String getLlId() {
		return llId;
	}
	
	public void setLlId(String llId) {
		this.llId = llId;
	}
	
	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public double getLng() {
		return lng;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
}