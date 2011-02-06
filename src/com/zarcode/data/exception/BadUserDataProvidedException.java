package com.zarcode.data.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class BadUserDataProvidedException extends WebApplicationException {

	private static String MSG = "Client provided bad user data to reject the request";
	
	public BadUserDataProvidedException() {
		super(Response.status(Status.UNAUTHORIZED).entity(MSG).type("text/plain").build()); 
	}
	
}
