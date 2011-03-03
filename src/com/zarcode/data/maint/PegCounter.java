package com.zarcode.data.maint;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.zarcode.data.dao.PegCounterDao;

public class PegCounter {
	
	public static final String NO_OF_ACTIVE_USERS = "NO_OF_ACTIVE_USERS";
	public static final String NO_BUZZ_MSG = "NO_BUZZ_MSG_";
	public static final String NO_BUZZ_COMMENTS = "NO_BUZZ_COMMENTS_";
	public static final String NO_HOT_SPOTS = "NO_HOTSPOTS_";
	public static final String NO_PINGS_PER_RESOURCE = "NO_PINGS_PER_RESOURCE";
	
	public static final int DAILY = 0;
	public static final int WEEKLY = 1;
	public static final int MONTHLY = 2;
	public static final int YEARLY = 3;

	private static PegCounterDao pegDao = new PegCounterDao();
	/**
	 * logger
	 */
	private static Logger logger = Logger.getLogger(PegCounter.class.getName());
	
	public static void clobber(String pegName, long value) {
		pegDao.update(pegName, value);
	}
	
	public static void customIncr(String pegName, String key, int freq) {
		StringBuilder sb = new StringBuilder();
		sb.append(pegName);
		sb.append(":");
		if (key != null) {
			key = key.toUpperCase();
			sb.append(key);
		}
		else {
			sb.append("UNKNOWN");
		}
		incr(sb.toString(), freq);
	}
	
	public static void incr(String pegName, int freq) {
		Format formatter = null;
		String tm = null;
		StringBuilder sb = new StringBuilder();
		if (freq == DAILY) {
			formatter = new SimpleDateFormat("MMMddyyyy");
			tm = formatter.format(new Date());
			sb.append(pegName);
			sb.append(".DAILY.");
			sb.append(tm.toUpperCase());
		}
		else if (freq == WEEKLY) {
			formatter = new SimpleDateFormat("MMMddyyyy");
			tm = formatter.format(new Date());
			sb.append(pegName);
			sb.append(".WEEKLY.");
			sb.append(tm.toUpperCase());
		}
		else if (freq == MONTHLY) {
			formatter = new SimpleDateFormat("MMMyyyy");
			tm = formatter.format(new Date());
			sb.append(pegName);
			sb.append(".MONTHLY.");
			sb.append(tm.toUpperCase());
		}
		else if (freq == YEARLY) {
			formatter = new SimpleDateFormat("yyyy");
			tm = formatter.format(new Date());
			sb.append(pegName);
			sb.append(".YEARLY.");
			sb.append(tm.toUpperCase());
		}
		pegDao.increment(sb.toString(), 1);
	}
	
	public static void decr(String pegName, int freq) {
		Format formatter = null;
		String tm = null;
		StringBuilder sb = new StringBuilder();
		if (freq == DAILY) {
			formatter = new SimpleDateFormat("MMMddyyyy");
			tm = formatter.format(new Date());
			sb.append(pegName);
			sb.append(".DAILY.");
			sb.append(tm.toUpperCase());
		}
		else if (freq == WEEKLY) {
			formatter = new SimpleDateFormat("MMMddyyyy");
			tm = formatter.format(new Date());
			sb.append(pegName);
			sb.append(".WEEKLY.");
			sb.append(tm.toUpperCase());
		}
		else if (freq == MONTHLY) {
			formatter = new SimpleDateFormat("MMMyyyy");
			tm = formatter.format(new Date());
			sb.append(pegName);
			sb.append(".MONTHLY.");
			sb.append(tm.toUpperCase());
		}
		else if (freq == YEARLY) {
			formatter = new SimpleDateFormat("yyyy");
			tm = formatter.format(new Date());
			sb.append(pegName);
			sb.append(".YEARLY.");
			sb.append(tm.toUpperCase());
		}
		pegDao.increment(sb.toString(), -1);
	}

}