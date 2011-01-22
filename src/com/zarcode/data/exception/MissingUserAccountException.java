package com.zarcode.data.exception;

public class MissingUserAccountException extends Exception {

	private static String MSG = "Unable to process request because request cannot find a VALID user account";
	
	public MissingUserAccountException() {
		super(MSG);
	}
	
}
