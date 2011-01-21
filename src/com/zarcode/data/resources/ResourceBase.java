package com.zarcode.data.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import net.sf.jsr107cache.Cache;


public class ResourceBase {

	protected Cache cache = null;
	
	public ResourceBase() {
        Map props = new HashMap();
        props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
		try {
 	       CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
 	       cache = cacheFactory.createCache(props);
 	   	} 
		catch (CacheException e) {
 	   	}
	}
}
