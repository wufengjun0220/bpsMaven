package com.mingtech.framework.adapter;


import com.alibaba.fastjson.JSONObject;

public class Response {
	
	private String code;//码值
	private String desc;//描述
	private JSONObject body;//内容
	private String flowNo;//流水号
	private boolean sendToThird;//发送第三方平台
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getFlowNo() {
		return flowNo;
	}
	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}
	public boolean isSendToThird() {
		return sendToThird;
	}
	public void setSendToThird(boolean sendToThird) {
		this.sendToThird = sendToThird;
	}
	public JSONObject getBody() {
		return body;
	}
	public void setBody(JSONObject body) {
		this.body = body;
	}
}
