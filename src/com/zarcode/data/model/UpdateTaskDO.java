package com.zarcode.data.model;


public class UpdateTaskDO {

	private String llId = null;
	
	private String object = null;
	
	private String field = null;
	
	private String value = null;
	
	private int result = 0;
	
	public UpdateTaskDO() {
	}
	
	public String getLlId() {
		return llId;
	}
	
	public void setLlId(String llId) {
		this.llId = llId;
	}
	
	public String getObject() {
		return object;
	}
	
	public void setObject(String object) {
		this.object = object;
	}
	
	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
}