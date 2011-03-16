package com.zarcode.data.resources.v1;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.Util;
import com.zarcode.data.dao.BucketDao;
import com.zarcode.data.gdata.PicasaClient;
import com.zarcode.data.model.BucketDO;
import com.zarcode.data.resources.ResourceBase;
import com.zarcode.platform.model.AppPropDO;

@Path("/v1/photos")
public class Photo extends ResourceBase {
	
	private Logger logger = Logger.getLogger(Photo.class.getName());
	
	@Context 
	UriInfo uriInfo = null;
    
	@Context 
    Request request = null;
	
	String container = null;
	
	private static int NO_ACTIVE_BUCKETS = 10;
	
	
	private BucketDO createBucket(AlbumEntry album) {
		BucketDO bucket = new BucketDO();
		bucket.setAlbumId(album.getId());
		bucket.setBytesUsed(album.getBytesUsed());
		bucket.setFullFlag(false);
		bucket.setRemainingPhotos(album.getPhotosLeft());
		return bucket;
	}

	@GET 
	@Path("/activeBucket")
	@Produces("application/json")
	public BucketDO getActiveBucket() {
		BucketDO target = null;
		int i = 0;
		AlbumEntry album = null;
		BucketDO b = null;
	
		/*
		 * get local buckets
		 */
		logger.info("Getting active buckets ... ");
		BucketDao dao = new BucketDao();
		List<BucketDO> buckets = dao.getAllActiveBuckets();
		logger.info("No of buckets found: " + (buckets == null ? 0 : buckets.size()));
		
		/*
		 * if local store is empty, go to picasa and create a local store
		 */
		if (buckets == null) {
			
			logger.info("Trying to go Picasa to create a local store of buckets ...");
			
			PicasawebService service = new PicasawebService("DockedMobile");
			
			AppPropDO p1 = ApplicationProps.getInstance().getProp("PICASA_USER");
			String username = p1.getStringValue();
			AppPropDO p2 = ApplicationProps.getInstance().getProp("PICASA_PASSWORD");
			String password = p2.getStringValue();
			Date createDate = null;
			Calendar now = Calendar.getInstance();
			
			try {
				logger.info(">>> Invoking Picasa service  ... " + username + " (" + password + ")");
				PicasaClient client = new PicasaClient(service);
				List<AlbumEntry> albums = client.getAlbums();
				logger.info(">>> Got response from Picasa service  ");
				
				int count = 0;
				if (albums != null && albums.size() > 0) {
					for (i=0; i<albums.size(); i++) {
						album = albums.get(i);
						if (album.getPhotosLeft() > 100) {
							b = createBucket(album);
							if (target == null) {
								target = b;
							}
							dao.addBucket(b);
							count++;
							if (count == NO_ACTIVE_BUCKETS) {
								break;
							}
						}
					}
				}
			}
			catch (Exception e) {
				logger.severe("EXCEPTION ::: TRYING TO GET TO PICASA " + Util.getStackTrace(e));
			}
			
		}
		/*
		 * okay, we have a local store, return first best album
		 */
		else {
			
			logger.info("Got a local store of buckets ... returning best available");
			
			for (i=0; i<buckets.size(); i++) {
				b = buckets.get(i);
				if (b.getRemainingPhotos() > 10) {
					target = b;
					break;
				}
				else {
					dao.markFull(b);
				}
			}
		}
		return target;
		
	} // getActiveBucket
	
	
}
