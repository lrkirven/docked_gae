package com.zarcode.data.gdata;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthSigner;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.Link;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.CommentEntry;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.GphotoFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.TagEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.zarcode.common.ApplicationProps;
import com.zarcode.data.resources.v1.Buzz;
import com.zarcode.platform.model.AppPropDO;
import com.zarcode.common.Util;

public class PicasaClient {
	
	private Logger logger = Logger.getLogger(PicasaClient.class.getName());

	private static final String API_PREFIX = "https://picasaweb.google.com/data/feed/api/user/";

	private final PicasawebService service;
  
	private static final String SCOPE = "https://picasaweb.google.com/data/"; 


	/**
	 * Constructs a new client with the given username and password.
	 */
	public PicasaClient(PicasawebService service) throws Exception {
		this.service = service;

		try {
			/*
			OAuthSigner signer = new OAuthHmacSha1Signer();
			GoogleOAuthParameters params = setupOAuthCredentials();
			service.setOAuthCredentials(params, signer);
			GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(signer);
			*/
			AppPropDO p = ApplicationProps.getInstance().getProp("AUTHSUB_SESSION_TOKEN");
			String authSubToken = p.getStringValue(); 
			service.useSsl();
			service.setAuthSubToken(authSubToken);
		} 
		catch (Exception e) {
			throw new Exception("Unable to configure application credentials");
		}
	}
  
	private GoogleOAuthParameters setupOAuthCredentials() {
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();

		// Set your OAuth Consumer Key (which you can register at
		// https://www.google.com/accounts/ManageDomains).
		AppPropDO p1 = ApplicationProps.getInstance().getProp("GDATA_OAUTH_CONSUMER_KEY");
		AppPropDO p2 = ApplicationProps.getInstance().getProp("GDATA_OAUTH_CONSUMER_SECRET");
		String consumerKey = p1.getStringValue();
		oauthParameters.setOAuthConsumerKey(consumerKey);

		// Initialize the OAuth Signer.  2-Legged OAuth must use HMAC-SHA1, which
		// uses the OAuth Consumer Secret to sign the request.  The OAuth Consumer
		// Secret can be obtained at https://www.google.com/accounts/ManageDomains.
		String secret = p2.getStringValue();
		logger.info("Consumer Key: " + consumerKey + " Secret: " + secret);
		oauthParameters.setOAuthConsumerSecret(secret);
  
		// Set the scope for this particular service.
		logger.info("Scope: " + SCOPE);
		oauthParameters.setScope(SCOPE); 
		
		return oauthParameters;
	}
  
	private URL prepareUrl(String url) {
		URL feedUrl = null;
		AppPropDO p1 = ApplicationProps.getInstance().getProp("PICASA_USER");
		String modURL = url + "?xoauth_requestor_id=" + p1.getStringValue() + "&max-results=1000";
		try {
			logger.info("Using Google Data Url: " + modURL);
			feedUrl = new URL(modURL);
		}
		catch (Exception e) {
			logger.severe("prepareUrl(): [EXCEPTION]\n\n" + Util.getStackTrace(e));
		}
		return feedUrl;
	}

  /**
   * Retrieves the albums for the given user.
   */
  public List<AlbumEntry> getAlbums(String username) throws IOException,
      ServiceException {

    String albumUrl = API_PREFIX + username;
    URL feedUrl = prepareUrl(albumUrl);
    UserFeed userFeed = getFeedByURL(feedUrl, UserFeed.class);
    
    List<GphotoEntry> entries = userFeed.getEntries();
    List<AlbumEntry> albums = new ArrayList<AlbumEntry>();
    for (GphotoEntry entry : entries) {
    	GphotoEntry adapted = entry.getAdaptedEntry();
    	if (adapted instanceof AlbumEntry) {
    		albums.add((AlbumEntry) adapted);
    	}
    }
    return albums;
  }

  /**
   * Retrieves the albums for the currently logged-in user.  This is equivalent
   * to calling {@link #getAlbums(String)} with "default" as the username.
   */
  public List<AlbumEntry> getAlbums() throws IOException, ServiceException {
	  return getAlbums("default");
  }

  /**
   * Retrieves the tags for the given user.  These are tags aggregated across
   * the entire account.
   */
  public List<TagEntry> getTags(String uname) throws IOException,
      ServiceException {
	  String tagUrl = API_PREFIX + uname + "?kind=tag";
	  UserFeed userFeed = getFeedByURL(prepareUrl(tagUrl), UserFeed.class);

	  List<GphotoEntry> entries = userFeed.getEntries();
	  List<TagEntry> tags = new ArrayList<TagEntry>();
	  
	  for (GphotoEntry entry : entries) {
		  GphotoEntry adapted = entry.getAdaptedEntry();
		  if (adapted instanceof TagEntry) {
			  tags.add((TagEntry) adapted);
		  }
	  }
	  return tags;
  }

  /**
   * Retrieves the tags for the currently logged-in user.  This is equivalent
   * to calling {@link #getTags(String)} with "default" as the username.
   */
  public List<TagEntry> getTags() throws IOException, ServiceException {
    return getTags("default");
  }

  /**
   * Retrieves the photos for the given album.
   */
  public List<PhotoEntry> getPhotos(AlbumEntry album) throws IOException,
      ServiceException {

    String feedHref = getLinkByRel(album.getLinks(), Link.Rel.FEED);
    URL feedUrl = prepareUrl(feedHref);
    AlbumFeed albumFeed = getFeedByURL(feedUrl, AlbumFeed.class);

    List<GphotoEntry> entries = albumFeed.getEntries();
    List<PhotoEntry> photos = new ArrayList<PhotoEntry>();
    for (GphotoEntry entry : entries) {
      GphotoEntry adapted = entry.getAdaptedEntry();
      if (adapted instanceof PhotoEntry) {
        photos.add((PhotoEntry) adapted);
      }
    }
    return photos;
  }

  /**
   * Retrieves the comments for the given photo.
   */
  public List<CommentEntry> getComments(PhotoEntry photo) throws IOException,
      ServiceException {

    String feedHref = getLinkByRel(photo.getLinks(), Link.Rel.FEED);
    AlbumFeed albumFeed = getFeed(feedHref, AlbumFeed.class);

    List<GphotoEntry> entries = albumFeed.getEntries();
    List<CommentEntry> comments = new ArrayList<CommentEntry>();
    for (GphotoEntry entry : entries) {
      GphotoEntry adapted = entry.getAdaptedEntry();
      if (adapted instanceof CommentEntry) {
        comments.add((CommentEntry) adapted);
      }
    }
    return comments;
  }

  /**
   * Retrieves the tags for the given taggable entry.  This is valid on user,
   * album, and photo entries only.
   */
  public List<TagEntry> getTags(GphotoEntry<?> parent) throws IOException,
      ServiceException {

    String feedHref = getLinkByRel(parent.getLinks(), Link.Rel.FEED);
    feedHref = addKindParameter(feedHref, "tag");
    AlbumFeed albumFeed = getFeed(feedHref, AlbumFeed.class);

    List<GphotoEntry> entries = albumFeed.getEntries();
    List<TagEntry> tags = new ArrayList<TagEntry>();
    for (GphotoEntry entry : entries) {
      GphotoEntry adapted = entry.getAdaptedEntry();
      if (adapted instanceof TagEntry) {
        tags.add((TagEntry) adapted);
      }
    }
    return tags;
  }

  /**
   * Album-specific insert method to insert into the gallery of the current
   * user, this bypasses the need to have a top-level entry object for parent.
   */
  public AlbumEntry insertAlbum(AlbumEntry album)
      throws IOException, ServiceException {
    String feedUrl = API_PREFIX + "default";
    return service.insert(new URL(feedUrl), album);
  }

  /**
   * Insert an entry into another entry.  Because our entries are a hierarchy,
   * this lets you insert a photo into an album even if you only have the
   * album entry and not the album feed, making it quicker to traverse the
   * hierarchy.
   */
  public <T extends GphotoEntry> T insert(GphotoEntry<?> parent, T entry)
      throws IOException, ServiceException {

    String feedUrl = getLinkByRel(parent.getLinks(), Link.Rel.FEED);
    return service.insert(new URL(feedUrl), entry);
  }

  /**
   * Helper function to allow retrieval of a feed by string url, which will
   * create the URL object for you.  Most of the Link objects have a string
   * href which must be converted into a URL by hand, this does the conversion.
   */
  @Deprecated
  public <T extends GphotoFeed> T getFeed(String feedHref, Class<T> feedClass) throws IOException, ServiceException {
	  return service.getFeed(new URL(feedHref), feedClass);
  }
  
  public <T extends GphotoFeed> T getFeedByURL(URL feedUrl, Class<T> feedClass) throws IOException, ServiceException {
	  return service.getFeed(feedUrl, feedClass);
  }

  /**
   * Helper function to add a kind parameter to a url.
   */
  public String addKindParameter(String url, String kind) {
    if (url.contains("?")) {
      return url + "&kind=" + kind;
    } else {
      return url + "?kind=" + kind;
    }
  }

  /**
   * Helper function to get a link by a rel value.
   */
  public String getLinkByRel(List<Link> links, String relValue) {
    for (Link link : links) {
      if (relValue.equals(link.getRel())) {
        return link.getHref();
      }
    }
    throw new IllegalArgumentException("Missing " + relValue + " link.");
  }
}

