package com.zarcode.data.exception;

public class InvalidEmailAddrException extends Exception {

	private static String MSG = "Requested email address is missing or is INVALID";
	
	public InvalidEmailAddrException() {
		super(MSG);
	}
	
}
