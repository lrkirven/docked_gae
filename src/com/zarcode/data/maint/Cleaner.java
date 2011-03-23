package com.zarcode.data.maint;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.PhotoEntry;
import com.zarcode.app.AppCommon;
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.EmailHelper;
import com.zarcode.common.Util;
import com.zarcode.data.dao.BuzzDao;
import com.zarcode.data.dao.FeedbackDao;
import com.zarcode.data.dao.PegCounterDao;
import com.zarcode.data.dao.UserDao;
import com.zarcode.data.gdata.PicasaClient;
import com.zarcode.data.model.BuzzMsgDO;
import com.zarcode.data.model.CommentDO;
import com.zarcode.data.model.FeedbackDO;
import com.zarcode.data.model.PegCounterDO;
import com.zarcode.data.model.ReadOnlyUserDO;
import com.zarcode.platform.model.AppPropDO;

public class Cleaner extends HttpServlet {

	private Logger logger = Logger.getLogger(Cleaner.class.getName());
	
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    	int i = 0;
	    	
			Queue queue = QueueFactory.getDefaultQueue();
 			queue.add(TaskOptions.Builder.withUrl("/cleanerTask"));
 			
			resp.setContentType("text/html");
 			resp.getWriter().println("<b>Cleaner Task has been started -- Wait for email for results</b>");
			
			
	    } // doGet
}
