package com.zarcode.data.maint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.zarcode.common.Util;
import com.zarcode.data.dao.BuzzDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.dao.WaterResourceDao;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.data.model.ReadOnlyUserDO;

public class Cleaner extends HttpServlet {

	private Logger logger = Logger.getLogger(Cleaner.class.getName());

	private static int MAX_DAYS_MESSAGE_IN_SYS = 90;
	private static int MAX_DAYS_ANONYMOUS_USER_IN_SYS = 7;
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    	int i = 0;
	    	long msecInDay = 86400000;
	    	BuzzDao eventDao = null;
	    	UserDao userDao = null;
			List<BuzzMsgDO> list = null;
			List<ReadOnlyUserDO> tempUsers = null;
			BuzzMsgDO msg = null;
			ReadOnlyUserDO tempUser = null;
			Calendar now = Calendar.getInstance();
			
			long msgLife = now.getTimeInMillis() - (MAX_DAYS_MESSAGE_IN_SYS * msecInDay);
			long anonymousUserLife = now.getTimeInMillis() - (MAX_DAYS_ANONYMOUS_USER_IN_SYS * msecInDay);
			
			logger.info("**** CLEANER: STARTING ****");
			
			int buzzMsgsDeleted = 0;
			int anonymousUsersDeleted = 0;
			
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
			}
			catch (Exception e) {
				logger.severe("[EXCEPTION]\n" + Util.getStackTrace(e));
			}
			logger.info("# of buzz message(s) deleted: " + buzzMsgsDeleted);
			logger.info("# of anonymous user(s) deleted: " + anonymousUsersDeleted);
			logger.info("**** CLEANER: DONE ****");
	    } // doGet
}
