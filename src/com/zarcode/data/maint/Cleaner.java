package com.zarcode.data.maint;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.PhotoEntry;
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.Util;
import com.zarcode.data.dao.BuzzDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.gdata.PicasaClient;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.data.model.ReadOnlyUserDO;
import com.zarcode.platform.model.AppPropDO;

public class Cleaner extends HttpServlet {

	private Logger logger = Logger.getLogger(Cleaner.class.getName());

	private static int MAX_DAYS_MESSAGE_IN_SYS = 90;
	private static int MAX_DAYS_ANONYMOUS_USER_IN_SYS = 7;
	private static long MSEC_IN_DAY = 86400000;
	
	private int cleanPicasaPhotos() {
		int i = 0;
		int j = 0;
		int totalPhotosDeleted = 0;
		
		AlbumEntry album = null;
		PhotoEntry photo = null;
		List<PhotoEntry> photos = null;
		PicasawebService service = new PicasawebService("DockedMobile");
		AppPropDO p1 = ApplicationProps.getInstance().getProp("PICASA_USER");
		String username = p1.getStringValue();
		AppPropDO p2 = ApplicationProps.getInstance().getProp("PICASA_PASSWORD");
		String password = p2.getStringValue();
		Date createDate = null;
		Calendar now = Calendar.getInstance();
		
		long photoLife = now.getTimeInMillis() - (MAX_DAYS_MESSAGE_IN_SYS * MSEC_IN_DAY);
		int photosDeleted = 0;
		
		try {
			PicasaClient client = new PicasaClient(service, username, password);
			List<AlbumEntry> albums = client.getAlbums();
			if (albums != null && albums.size() > 0) {
				for (i=0; i<albums.size(); i++) {
					album = albums.get(i);
					photos = client.getPhotos(album);
					if (photos != null && photos.size() > 0) {
						photosDeleted = 0;
						for (j=0; j<photos.size(); j++) {
							photo = photos.get(j);
							createDate = photo.getFeaturedDate();
							if (createDate.getTime() < photoLife) {
								photo.delete();
								photosDeleted++;
								totalPhotosDeleted++;
							}
						}
					}
				}
				
			}
		}
		catch (Exception e) {
			logger.severe("EXCEPTION :::: PICASA CLEANER FAILED -- " + e.getMessage());
		}
		return totalPhotosDeleted;
	}
	
	private int cleanAnonymousUsers() {
		int i = 0;
    	UserDao userDao = null;
		List<ReadOnlyUserDO> tempUsers = null;
		ReadOnlyUserDO tempUser = null;
		Calendar now = Calendar.getInstance();
		int anonymousUsersDeleted = 0;
		
		long anonymousUserLife = now.getTimeInMillis() - (MAX_DAYS_ANONYMOUS_USER_IN_SYS * MSEC_IN_DAY);
		
		userDao = new UserDao();
		tempUsers = userDao.getAllReadOnlyUsers();
		
		if (tempUsers != null && tempUsers.size() > 0) {
			for (i=0; i<tempUsers.size(); i++) {
				tempUser = tempUsers.get(i);
				if (tempUser.getLastUpdate().getTime() < anonymousUserLife) {
					userDao.deleteReadOnlyUser(tempUser);
					anonymousUsersDeleted++;
				}
			}
		}
		return anonymousUsersDeleted;
	}
	
	private int cleanBuzzMsgs() {
		int i = 0;
		int buzzMsgsDeleted = 0;
		
    	BuzzDao eventDao = null;
    	UserDao userDao = null;
		List<BuzzMsgDO> list = null;
		List<ReadOnlyUserDO> tempUsers = null;
		BuzzMsgDO msg = null;
		ReadOnlyUserDO tempUser = null;
		Calendar now = Calendar.getInstance();
		
		long msgLife = now.getTimeInMillis() - (MAX_DAYS_MESSAGE_IN_SYS * MSEC_IN_DAY);
		
		try {
			eventDao = new BuzzDao();
			list = eventDao.getAllMsgs();
			if (list != null && list.size() > 0) {
				for (i=0; i<list.size(); i++) {
					msg = list.get(i);
					if (msg.getCreateDate().getTime() < msgLife) {
						eventDao.deleteInstance(msg);
						buzzMsgsDeleted++;
					}
				}
			}
			
		}
		catch (Exception e) {
			logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
		}
		return buzzMsgsDeleted;
	}
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    	int i = 0;
	    	BuzzDao eventDao = null;
	    	UserDao userDao = null;
			List<BuzzMsgDO> list = null;
			List<ReadOnlyUserDO> tempUsers = null;
			BuzzMsgDO msg = null;
			ReadOnlyUserDO tempUser = null;
			Calendar now = Calendar.getInstance();
			
			long msgLife = now.getTimeInMillis() - (MAX_DAYS_MESSAGE_IN_SYS * MSEC_IN_DAY);
			
			logger.info("**** CLEANER: STARTING **** [ Timestamp :: " + now.getTimeInMillis() + " ]");
			
			int buzzMsgsDeleted = cleanBuzzMsgs();
			logger.info("# of buzz msg(s) deleted: " + buzzMsgsDeleted);
		
			int photosDeleted = cleanPicasaPhotos();
			logger.info("# of picasa photo(s) deleted: " + photosDeleted);
			
			int anonymousUsersDeleted = cleanAnonymousUsers();
			Calendar done = Calendar.getInstance();
			logger.info("# of anonymous user(s) deleted: " + anonymousUsersDeleted);
			
			long duration = done.getTimeInMillis() - now.getTimeInMillis();
			logger.info("**** CLEANER: DONE **** [ Elapsed Msec(s): " + duration);
			
	    } // doGet
}
