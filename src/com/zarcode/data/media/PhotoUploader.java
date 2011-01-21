package com.zarcode.data.media;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

/*
import com.photobucket.api.service.DirectLoginStrategy;
import com.photobucket.api.service.PhotobucketService;
import com.photobucket.api.service.UploadStrategy;
import com.photobucket.api.service.exception.APIException;
import com.photobucket.api.service.model.Album;
import com.photobucket.api.service.model.Media;
import com.photobucket.api.service.model.User;
*/


import com.zarcode.common.Util;

public class PhotoUploader {
	
	private Logger logger = Logger.getLogger(PhotoUploader.class.getName());
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
	    BlobKey blobKey = blobs.get("photo");
	    if (blobKey != null) {
	    	ImagesService imagesService = ImagesServiceFactory.getImagesService();
	        Image image = ImagesServiceFactory.makeImageFromBlob(blobKey);
	    	String imageUrl = "http://happygilmore.com/imgfsfsf.jpg";
	    	returnResp(res, imageUrl);
	    }
	    else {
	    	returnError(res, 88);
	    }
	}
	
	public void savePhoto2PhotoBucket(String title, byte[] rawImage) {
		Date dateNow = new Date();
        String consumerKey = "";
        	/*
        	ResourceBundle.getBundle("consumer").getString("oauth.consumer");
        	*/
        String consumerSecretKey = "";
        	/*
        	ResourceBundle.getBundle("consumer").getString("oauth.consumer.key");
        	*/
        /*
        PhotobucketService service = new PhotobucketService(consumerKey,consumerSecretKey);
        User user = new User();
        user.setUsername("pbapi");
        user.setPassword("abc123");
        Album album = new Album();
        album.setPath("/api-test");
        Media media = new Media();
        String usernameDetails = "FASDDfsfdfsFDSFsf332SFSfsfs";
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
        StringBuilder nowYYYYMMDD = new StringBuilder( dateformatYYYYMMDD.format( dateNow ) );
        String nameOfPhotoImage = "img" + usernameDetails + "-" + nowYYYYMMDD.toString();
        media.setName(nameOfPhotoImage);
        media.setTitle(title);
        media.setDescription("Photo via LazyLaker.com");
        media.setType(Media.Type.IMAGE);    
        */
        
        /*
        try {
        	DirectLoginStrategy strategy = new DirectLoginStrategy(user);
            service.execute(strategy);
            user = strategy.getUser();

            UploadStrategy uploadStrategy = new UploadStrategy(user, album, media);
            uploadStrategy.setMedia(media);
            uploadStrategy.setByteArrayInputStream(new ByteArrayInputStream(rawImage));  
            uploadStrategy.setByteArrayInputStream(new ByteArrayInputStream(rawImage));   
            
            uploadStrategy.addFileUploadProgressEventListenter(new IFileUploadProgressEventListener() {
        		public void fileUploadProgressUpdate(FileUploadProgressEvent evt) {
                	System.out.println("Event Progress " + evt.getPercentComplete());
        		}
            });
                
            service.execute(uploadStrategy);
            media = uploadStrategy.getMedia();
            
            System.out.println("Media URL: " + media.getUrl());
            System.out.println("Media BrowseURL: " + media.getBrowseUrl());
            System.out.println("Media Thumb: " + media.getThumbUrl());
            System.out.println("Media Title: " + media.getTitle());
            System.out.println("Media Description: " + media.getDescription());
        } 
        catch (APIException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } 
        */
	}
	
	private void returnResp(HttpServletResponse resp, String imageUrl) {
    	resp.setContentType("text/xml");
    	try {
    		java.io.PrintWriter out = resp.getWriter();
    		out.println("<results>");
    		out.println("<status>0<status>");
    		out.println("<url>" + imageUrl + "</url>");
        	out.println("</results>"); 
    	}
    	catch (Exception e) {
    		logger.warning("{EXCEPTION]\n" + Util.getStackTrace(e));
    	}
    }
	
	private void returnError(HttpServletResponse resp, int errorCode) {
		String imageUrl = "<image url>";
    	resp.setContentType("text/xml");
    	try {
    		java.io.PrintWriter out = resp.getWriter();
    		out.println("<results>");
    		out.println("<status>" + errorCode + "<status>");
        	out.println("</results>"); 
    	}
    	catch (Exception e) {
    		logger.warning("{EXCEPTION]\n" + Util.getStackTrace(e));
    	}
    }
	
}
