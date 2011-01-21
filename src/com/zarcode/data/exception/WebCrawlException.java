package com.zarcode.data.exception;

public class WebCrawlException extends Exception {

	private String url = null;
	
	public WebCrawlException(String msg, String url) {
		super(msg);
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
}
