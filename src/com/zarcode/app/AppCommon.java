package com.zarcode.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class AppCommon {
	
	private static Logger logger = Logger.getLogger(AppCommon.class.getName());
	
	public static final String ANONYMOUS = "ABC123";
	
	public static final String APPNAME = "Docked";
	
	public static final String UNKNOWN = "UNKNOWN";
	
	public static final int MAX_GEOHASH_BIT_PRECISION = 60;
	
	public static double distanceBtwAB(double lat1, double lng1, double lat2, double lng2) {
		double earthR = 6371; // km
		double dLat = Math.toRadians(lat2-lat1);
		double dlng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + 
			Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *  
			Math.sin(dlng/2) * Math.sin(dlng/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		// dist in km
		double d = earthR * c;
		double distInMiles = (d/0.621371192);
		return distInMiles;
	}
	
	public static double calcBearing(double lat1, double lng1, double lat2, double lng2) {
		double bearing = 0.0;
		// determine angle
		bearing = Math.atan2(Math.sin(lng2-lng1) * Math.cos(lat2), (Math.cos(lat1) * Math.sin(lat2)) - (Math.sin(lat1) * Math.cos(lat2) * Math.cos(lng2-lng1)));
		// convert to degrees
		bearing =  Math.toDegrees(bearing);
		// use % to turn -90 = 270
		bearing = (bearing + 360.0) % 360;
		return bearing;
	}
	
	public static String generateTimeOffset(Date d) {
		String timeOffset = "[UNKNOWN TIME]";
		Date now = new Date();
		long diff = now.getTime() - d.getTime();
		if (diff > 0) {
			long days = (diff/(1000 * 60 * 60 * 24));
			if (days > 5) {
				SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
				String s = formatter.format(d);
				timeOffset = s;
			}
			else if (days > 2) {
				SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
				// SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
				String s = formatter.format(d);
				timeOffset = s;
			}
			else if (days > 1) {
				timeOffset = days + " days ago";
			}
			else if (days == 1) {
				timeOffset = "yesterday";
			}
			else {
				long hours  = (diff/(1000 * 60 * 60));
				if (hours == 1) {
					timeOffset = "1 hr ago";
				}
				else if (hours > 1) {
					timeOffset = hours + " hrs ago";
				}
				else {
					long mins = (diff/(1000 * 60));
					if (mins == 1) {
						timeOffset = "a minute ago";
					}
					if (mins > 1) {
						timeOffset = mins + " mins ago";
					}
					else {
						long secs = (diff/(1000));
						if (secs > 1) {
							timeOffset = secs + " secs ago";
						}
						else {
							timeOffset = "just now";
						}
					}
				}
			}
		}
		
		return timeOffset;
	}

	public static String generateActiveKey() {
		DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd hh");
		String dateString = formatter.format(Calendar.getInstance().getTime());
		int mins = Calendar.getInstance().get(Calendar.MINUTE);
		if (mins < 15) {
			dateString += ":00";
		}
		else if (mins < 30) {
			dateString += ":15";
		}
		else if (mins < 45) {
			dateString += ":30";
		}
		else {
			dateString += ":45";
		}
		return dateString;
	}

}
