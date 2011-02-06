package com.zarcode.data.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class UnableToDecodeRequestException extends WebApplicationException {

	private static String MSG = "Unable to decode incoming request data from client";
	
	public UnableToDecodeRequestException() {
		super(Response.status(Status.BAD_REQUEST).entity(MSG).type("text/plain").build()); 
	}
	
}
