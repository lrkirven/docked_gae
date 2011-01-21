package com.zarcode.data.model;

import com.zarcode.common.ApplicationProps;
import com.zarcode.platform.model.AppPropDO;
import com.zarcode.security.BlockTea;

public class SecurityTokenDO {

	private String nickname = null;
	
	private String logoutUrl = null;

	private String emailAddr = null;
	
	private String deviceId = null;
	
	private String llId = null;
	
	private String userId = null;
	
	private String authDomain = null;
	
	private String picasaUser = null;
	
	private String picasaPassword = null;
	
	private String fbLazyLakerKey = null;
	
	private String fbLazyLakerSecret = null;
	
	private String serverSecret = null;
	
	public SecurityTokenDO() {
		AppPropDO p1 = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		AppPropDO p2 = ApplicationProps.getInstance().getProp("CLIENT_TO_SERVER_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(p2.getStringValue(), p1.getStringValue());
		this.serverSecret = cipherText;
	}
	
	public void postCreation() {
	}
	
	public String getFbLazyLakerKey() {
		return fbLazyLakerKey;
	}
	
	public void setFbLazyLakerKey(String fbLazyLakerKey) {
		this.fbLazyLakerKey = fbLazyLakerKey;
	}
	
	public void encryptThenSetFbLazyLakerKey(String key) {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(key, prop.getStringValue());
		this.fbLazyLakerSecret = cipherText;
	}
	
	public String getFbLazyLakerSecret() {
		return fbLazyLakerSecret;
	}
	
	public void setFbLazyLakerSecret(String fbLazyLakerSecret) {
		this.fbLazyLakerSecret = fbLazyLakerSecret;
	}
	
	public void encryptThenSetFbLazyLakerSecret(String secret) {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(secret, prop.getStringValue());
		this.fbLazyLakerSecret = cipherText;
	}
	
	public String getPicasaUser() {
		return picasaUser;
	}
	
	public void setPicasaUser(String picasaUser) {
		this.picasaUser = picasaUser;
	}
	
	public void encryptThenSetPicasaUser(String pUser) {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(pUser, prop.getStringValue());
		this.picasaUser = cipherText;
	}
	
	
	public String getPicasaPassword() {
		return picasaPassword;
	}
	
	public void setPicasaPassword(String picasaPassword) {
		this.picasaPassword = picasaPassword;
	}
	
	public void encryptThenSetPicasaPassword(String pw) {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(pw, prop.getStringValue());
		this.picasaPassword = cipherText;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getAuthDomain() {
		return authDomain;
	}
	
	public void setAuthDomain(String authDomain) {
		this.authDomain = authDomain;
	}
	
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	public void encryptThenSetEmailAddr(String emailAddr) {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(emailAddr, prop.getStringValue());
		this.emailAddr = cipherText;
	}
	
	public String getLogoutUrl() {
		return logoutUrl;
	}
	
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	
	public void encryptThenSetLogoutUrl(String logoutUrl) {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(logoutUrl, prop.getStringValue());
		this.logoutUrl = cipherText;
	}
	
	public String getLlId() {
		return llId;
	}
	
	public void setLlId(String llId) {
		this.llId = llId;
	}
	
	public void encryptThenSetLLId(String llId) {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(llId, prop.getStringValue());
		this.llId = cipherText;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setServer(String serverSecret) {
		this.serverSecret = serverSecret;
	}
	
	public String getServerSecret() {
		return serverSecret;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void encryptThenSetUserId(String userId) {
		AppPropDO prop = ApplicationProps.getInstance().getProp("SERVER_TO_CLIENT_SECRET");
		BlockTea.BIG_ENDIAN = false;
		String cipherText = BlockTea.encrypt(userId, prop.getStringValue());
		this.userId = cipherText;
	}
	
}