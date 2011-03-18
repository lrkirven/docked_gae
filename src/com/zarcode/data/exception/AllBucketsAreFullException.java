package com.zarcode.data.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class AllBucketsAreFullException extends WebApplicationException {

	private static String MSG = "All of our photo buckets are full -- Support have been notified.";
	
	public AllBucketsAreFullException() {
		super(Response.status(Status.NOT_FOUND).entity(MSG).type("text/plain").build()); 
	}
	
}
