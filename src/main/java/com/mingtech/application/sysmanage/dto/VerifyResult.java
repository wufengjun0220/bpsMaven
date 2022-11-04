package com.mingtech.application.sysmanage.dto;


/**
 * 校验结果
 * @date 2021-10-09
 */

public class VerifyResult {
	
	/**
	 * 结果 true or false
	 */
	
	private boolean result;
	
	/**
	 * 结果说明
	 * String 
	 */
	private String desc;

	
	
	public VerifyResult() {
	}

	public VerifyResult(boolean result, String desc) {
		this.result = result;
		this.desc = desc;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
	

}
