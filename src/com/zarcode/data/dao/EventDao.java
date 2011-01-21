package com.zarcode.data.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.queries.GeoHashCircleQuery;

import com.zarcode.data.model.CommentDO;
import com.zarcode.data.model.EventSequenceDO;
import com.zarcode.data.model.MsgEventDO;
import com.zarcode.platform.dao.BaseDao;
import com.zarcode.platform.loader.AbstractLoaderDao;

public class EventDao extends BaseDao implements AbstractLoaderDao {
	
	private Logger logger = Logger.getLogger(EventDao.class.getName());

	public static final int PAGESIZE = 50;

	/**
	 * 10 miles (16093.44 meters)
	 */
	private static final double DEFAULT_RADIUS = 16093.44;
	
	private static final String SEQ_KEY = "SINGLETON";
	
	private static final long CURRENT_VER = 0;
	
	private int version = 0;
	
	private void createSequenceSingleton() {
		EventSequenceDO seq = null;
		seq = new EventSequenceDO();
    	seq.setId(SEQ_KEY);
    	seq.setSequenceNum(new Long(0));
    	seq.setSequenceVer(new Long(0));
    	pm.makePersistent(seq);
	}
	
	/*
	private Long getNextSequence() {
		Long nextSeqNum = null;
		EventSequenceDO seq = null;
		Date now = new Date();
		
		//
		// first time for EventSequence 
		//
		try {
			 seq = pm.getObjectById(EventSequenceDO.class, SEQ_KEY);
		}
		catch (JDOObjectNotFoundException e) {
            createSequenceSingleton();
		}

		Transaction tx = pm.currentTransaction();
		
        try {
            tx.begin();
            seq = pm.getObjectById(EventSequenceDO.class, SEQ_KEY);
            if (seq == null) {
            	createSequenceSingleton();
            }
            // nextSeqNum = seq.incrementSequenceBy(1);
            // this.sequenceVer = seq.getSequenceVer();
            nextSeqNum = now.getTime(); 
            this.sequenceVer = new Long(CURRENT_VER);
            
            pm.makePersistent(seq);
            tx.commit();
        } 
        finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return nextSeqNum;
	}
	*/
	
	public void loadObject(Object dataObject) {
		addEvent((MsgEventDO)dataObject);
	}
	
	public long deleteAll(Class cls) {
		long rows = 0;
		Query q = pm.newQuery(cls);
		rows = q.deletePersistentAll();
		return rows;
	}
	
	public void deleteInstance(MsgEventDO m) {
		long rows = 0;
		pm.deletePersistent(m);
	}
	
	public MsgEventDO addEvent(MsgEventDO event) {
		MsgEventDO res = null;
		Long eventId = null;
		Date now = new Date();
		if (event != null) {
			Long tm = now.getTime();
			event.setEventId(null);
			event.setCreateDate(new Date());
			event.setTimestamp(tm);
			event.setVersion(this.version);
  	      	pm.makePersistent(event); 
  	      	res = event;
  	      	eventId = event.getEventId();
  	      	logger.info("Added new event --> " + event);
		}
        return res; 
	}
	
	public CommentDO addComment(CommentDO comment) {
		CommentDO res = null;
		Long commentId = null;
		Date now = new Date();
		if (comment != null) {
			Long tm = now.getTime();
			comment.setCommentId(null);
			comment.setCreateDate(new Date());
			comment.setTimestamp(tm);
  	      	pm.makePersistent(comment); 
  	      	res = comment;
  	      	commentId = comment.getCommentId();
  	      	logger.info("Added new comment --> " + comment);
		}
        return res; 
	}
	
	private List<MsgEventDO> getAllByKeys(List<Long> listOfKeys) {
		int i = 0;
		Long key = null;
		MsgEventDO event = null;
		List<MsgEventDO> listOfEvents = null;
	
		if (listOfKeys != null) {
			listOfEvents =  new ArrayList<MsgEventDO>();
			for (i=0; i<listOfKeys.size(); i++) {
				key = listOfKeys.get(i);
				logger.info("Get MsgEventDO by key=" + key);
				event = (MsgEventDO)pm.getObjectById(MsgEventDO.class, key);
				listOfEvents.add(event);
			}
		}
		return listOfEvents;
		
	}
	
	public List<CommentDO> getCommentsViaMsgEvent(MsgEventDO msgEvent) {
		int i = 0;
		List<CommentDO> list = null;
		Date now = new Date();
		
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("timestamp > ");
		sb.append(0);
		sb.append(" && msgEventId == ");
		sb.append(msgEvent.getEventId());
		sb.append(")");
		Query query = pm.newQuery(CommentDO.class, sb.toString());
		query.setOrdering("timestamp desc");
		list = (List<CommentDO>)query.execute();
		if (list != null && list.size() > 0) {
			int len = list.size();
			for (i=0; i<len; i++) {
				CommentDO comm = (list.get(i));
				comm.postReturn();
			}
		}
			
		return list;
	}
	
	public List<MsgEventDO> getNextEventsByResourceId(Long resourceId) {
		int i = 0;
		List<Long> listOfKeys = null;
		List<MsgEventDO> listOfEvents = null;
		Transaction tx = pm.currentTransaction();
		Date now = new Date();
		
		logger.info("Getting messages by resourceId=" + resourceId);
		
		/*
		Long limit = lastSeq + PAGESIZE;
		if (limit > Long.MAX_VALUE) {
			limit = Long.MAX_VALUE;
		}
		*/
		try {
			tx.begin();
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append("timestamp > ");
			sb.append(0);
			sb.append(" && version == ");
			sb.append(CURRENT_VER);
			sb.append(" && resourceId == ");
			sb.append(resourceId);
			sb.append(")");
			Query query = pm.newQuery(MsgEventDO.class, sb.toString());
			query.setOrdering("timestamp desc");
			listOfEvents = (List<MsgEventDO>)query.execute();
			if (listOfEvents != null && listOfEvents.size() > 0) {
				int len = listOfEvents.size();
				for (i=0; i<len; i++) {
					MsgEventDO m = (listOfEvents.get(i));
					m.postReturn();
					List<CommentDO> listOfComments = getCommentsViaMsgEvent(m);
					if (listOfComments.size() > 0) {
						m.setComments(listOfComments);
					}
				}
			}
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return listOfEvents;
	}
	
	public List<MsgEventDO> getPrevEventsByResourceId(Long resourceId, Long lastSeq) {
		int i = 0;
		List<Long> listOfKeys = null;
		List<MsgEventDO> listOfEvents = null;
		Transaction tx = pm.currentTransaction();
		Date now = new Date();
		try {
			tx.begin();
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append("sequenceNum < ");
			sb.append(lastSeq);
			sb.append(" && sequenceVer == ");
			sb.append(CURRENT_VER);
			sb.append(" && resourceId == ");
			sb.append(resourceId);
			sb.append(")");
			Query query = pm.newQuery(MsgEventDO.class, sb.toString());
			query.setRange(0, PAGESIZE);
			query.setOrdering("sequenceNum");
			listOfEvents = (List<MsgEventDO>)query.execute();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return listOfEvents;
	}
	
	public List<MsgEventDO> getAllEvents() {
		int i = 0;
		List<Long> listOfKeys = null;
		List<MsgEventDO> listOfEvents = null;
		Query query = pm.newQuery(MsgEventDO.class);
		query.setOrdering("timestamp asc");
		listOfEvents = (List<MsgEventDO>)query.execute();
		return listOfEvents;
	}
	
	public List<MsgEventDO> getNextEvents(Long lastSeq, Long seqVer) {
		int i = 0;
		List<Long> listOfKeys = null;
		List<MsgEventDO> listOfEvents = null;
		Transaction tx = pm.currentTransaction();
		Long limit = lastSeq + PAGESIZE;
		try {
			tx.begin();
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append("sequenceNum > ");
			sb.append(lastSeq);
			sb.append(" && sequenceNum < ");
			sb.append(limit);
			sb.append(" && sequenceVer == ");
			sb.append(seqVer);
			sb.append(")");
			Query query = pm.newQuery(MsgEventDO.class, sb.toString());
			query.setOrdering("sequenceNum");
			listOfEvents = (List<MsgEventDO>)query.execute();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return listOfEvents;
	}
	
	public List<MsgEventDO> getPrevEvents(Long firstSeq, Long seqVer) {
		List<Long> listOfKeys = null;
		List<MsgEventDO> listOfEvents = null;
		Transaction tx = pm.currentTransaction();
		Long start = firstSeq - PAGESIZE;
		
		if (start < 1) {
			logger.warning("getPrevEvents(): start sequence is less than 1");
			return listOfEvents;
		}
		try {
			tx.begin();
			//
			// only get keys of objects
			//
			StringBuilder sb = new StringBuilder();
			sb.append("select eventId from ");
			sb.append(MsgEventDO.class.getName());
			sb.append("where sequenceNum > ");
			sb.append(start);
			sb.append(" && sequenceNum < ");
			sb.append(firstSeq);
			sb.append(" && sequenceVer == ");
			sb.append(seqVer);
			Query query = pm.newQuery(sb.toString());
			query.setOrdering("sequenceNum");
			listOfKeys = (List<Long>)query.execute();
			//
			// now get all of the objects for these keys
			//
			listOfEvents = (List<MsgEventDO>)pm.getObjectsById(listOfKeys);
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return listOfEvents;
	}
	
	public MsgEventDO getEventById(Long eventId) {
		MsgEventDO res = null;
		res = pm.getObjectById(MsgEventDO.class, eventId);
		return res;
	}
	
	public void incrementCommentCounter(Long eventId) {
		MsgEventDO res = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			res = pm.getObjectById(MsgEventDO.class, eventId);
			int count = res.getCommentCounter();
			count++;
			res.setCommentCounter(count);
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	/*
	public MsgEventDO updateEventFeatureId(MsgEventDO event, String featureId) {
		MsgEventDO res = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			res = pm.getObjectById(MsgEventDO.class, event.getEventId());
			res.setGoogleMapFeatureId(featureId);
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return res;
	}
	*/
	
	public List<MsgEventDO> findClosest(double lat, double lng, long last, boolean next) {
		int i = 0;
		int retryCounter = 0;
		List<MsgEventDO> res = null;
		List<GeoHash> geoKeys = null;
		GeoHashCircleQuery geoQuery = null;
		double radius = DEFAULT_RADIUS;
		
		logger.info("Starting with lat=" + lat + " lng=" + lng + " radius=" + radius);
		
		WGS84Point pt = new WGS84Point(lat, lng);
		
		geoQuery = new GeoHashCircleQuery(pt, radius);
		geoKeys = geoQuery.getSearchHashes();
		
		while (retryCounter < 2) {
			if (geoKeys != null && geoKeys.size() > 0) {
				res = _findClosest(geoKeys);
				if (res != null && res.size() > 0) {
					break;
				}
				retryCounter++;
				radius = radius * 2;
				geoQuery = new GeoHashCircleQuery(pt, radius);
				geoKeys = geoQuery.getSearchHashes();
				logger.info("Trying again with radius=" + radius);
			}
			else {
				radius = radius * 2;
				geoQuery = new GeoHashCircleQuery(pt, radius);
				geoKeys = geoQuery.getSearchHashes();
				logger.info("Trying again with radius=" + radius);
			}
		}
		
		List<MsgEventDO> results = null;
		
		if (res != null && res.size() > 0) {
			if (last > 0) {
				results = new ArrayList<MsgEventDO>();
				MsgEventDO e = null;
				int leng = res.size();
				for (i=0; i<leng; i++) {
					e = res.get(i);
					if (next) {
						if (e.getTimestamp() > last) {
							results.add(e);
						}
					}
					else {
						if (e.getTimestamp() < last) {
							results.add(e);
						}
					}
				}
			}
			else {
				results = res;
			}
		}
		
		return results;
	}
	
	private List<MsgEventDO> _findClosest(List<GeoHash> geoKeys) {
		int i = 0;
		GeoHash hash = null;
		List<MsgEventDO> res = null;
		
		logger.info("# of geo hash key(s) found: " + geoKeys.size());
		
		Transaction tx = pm.currentTransaction();
		try {
			// tx.begin();
			//
			// only get keys of objects
			//
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			int keyCount = geoKeys.size();
			String geoHashKeyStr = null;
			for (i=0; i<keyCount; i++) {
				hash = geoKeys.get(i);
				geoHashKeyStr = hash.toBase32();
				logger.info( i + ") geoHashKeyStr: " + geoHashKeyStr);
				if (geoHashKeyStr.length() == 6) {
					sb.append("geoHashKey6 == ");
					sb.append("'");
					sb.append(geoHashKeyStr);
					sb.append("'");
				}
				else if (geoHashKeyStr.length() == 5) {
					sb.append("geoHashKey4 == ");
					sb.append("'");
					sb.append(geoHashKeyStr.substring(0, 4));
					sb.append("'");
				}
				else if (geoHashKeyStr.length() == 4) {
					sb.append("geoHashKey4 == ");
					sb.append("'");
					sb.append(geoHashKeyStr);
					sb.append("'");
				}
				else if (geoHashKeyStr.length() == 3) {
					sb.append("geoHashKey2 == ");
					sb.append("'");
					sb.append(geoHashKeyStr.substring(0, 2));
					sb.append("'");
				}
				else if (geoHashKeyStr.length() == 2) {
					sb.append("geoHashKey2 == ");
					sb.append("'");
					sb.append(geoHashKeyStr);
					sb.append("'");
				}
				if ((i+1) < keyCount) {
					sb.append(" || ");
				}
			}
			sb.append(")");
			logger.info("Query string: " + sb.toString());
			Query query = pm.newQuery(MsgEventDO.class, sb.toString());
			query.setOrdering("sequenceNum");
			res = (List<MsgEventDO>)query.execute();
			// tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		
		logger.info("_findClosest(): Exit");
		
		return res;
	}
	
	public List<MsgEventDO> getEventsByIds(List<Long> keys) {
		int i = 0; 
		MsgEventDO event = null;
		List<MsgEventDO> list = null;
		list = (List<MsgEventDO>)pm.getObjectsById(keys);
		return list;
	}
	
}
