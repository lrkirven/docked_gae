package com.zarcode.data.maint;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class GeoRSSUpload extends HttpServlet {

	private Logger logger = Logger.getLogger(GeoRSSUpload.class.getName());
	
	private StringBuilder report = null;
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("geoRssFile");
        

        if (blobKey != null) {
        	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            try {
 				Transaction txn = ds.beginTransaction();
	 			Queue queue = QueueFactory.getDefaultQueue();
	 			queue.add(TaskOptions.Builder.withUrl("/georsswrite").param("blobKey", blobKey.getKeyString()));
	 			txn.commit();
 	        } 
 			catch (DatastoreFailureException e) {

 	        }
 			resp.setContentType("text/html");
 			resp.getWriter().println("<b>Successfully uploaded file.</b>");
        }
        else {
 			resp.setContentType("text/html");
 			resp.getWriter().println("Upload FAILED --- BLOB ERROR");
        }
    }
	
}