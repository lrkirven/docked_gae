package com.zarcode.data.maint;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KML2GeoConverter extends HttpServlet {

	private Logger logger = Logger.getLogger(KML2GeoConverter.class.getName());
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	int i = 0;
    	int j = 0;
    	String target = "http://maps.google.com/maps/ms?hl=en&ie=UTF8&vps=2&jsv=332a&oe=UTF8&msa=0&msid=213056035367874200355.00048903990ca02aa7a68&output=kml";
    
    	String url = URLEncoder.encode(target);
    	resp.sendRedirect("http://www.bing.com/maps/GeoCommunity.asjx?action=retrieverss&mkt=en&mapurl=" + url);
    }
    
}
