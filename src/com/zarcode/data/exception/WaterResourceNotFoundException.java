package com.zarcode.data.exception;

public class WaterResourceNotFoundException extends Exception {
	private final static String message = "Water Resource Not Found";
	
	public WaterResourceNotFoundException() {
		super(message);
	}
}
