package com.mingtech.application.autotask.core.domain;

public class BooleanAutoTaskResult {
	private boolean success;
	private String info;
	
	public BooleanAutoTaskResult(boolean success){
		this.success = success;
	}
	public BooleanAutoTaskResult(boolean success,String info){
		this.success = success;
		if(info!=null){
			this.info = info;
		}
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
}
