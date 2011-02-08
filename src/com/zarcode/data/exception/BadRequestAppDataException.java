package com.zarcode.data.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class BadRequestAppDataException extends WebApplicationException {

	private static String MSG = "Request is missing required data to process it";
	
	public BadRequestAppDataException() {
		super(Response.status(Status.BAD_REQUEST).entity(MSG).type("text/plain").build()); 
	}
	
}
