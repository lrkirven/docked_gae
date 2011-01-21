package com.zarcode.data.exception;

public class EmailAlreadyExistsException extends Exception {

	private static String MSG = "Requested email address is already reqgistered.";
	
	public EmailAlreadyExistsException() {
		super(MSG);
	}
	
}
