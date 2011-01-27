package com.zarcode.data.model;


public class RegisterTokenDO {

	private String emailAddr = null;
	
	private String displayName = null;
	
	private String registerSecret = null;
	
	public RegisterTokenDO() {
	}
	
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getRegisterSecret() {
		return registerSecret;
	}
	
	public void setRegisterSecret(String s) {
		this.registerSecret = s;
	}
	
}