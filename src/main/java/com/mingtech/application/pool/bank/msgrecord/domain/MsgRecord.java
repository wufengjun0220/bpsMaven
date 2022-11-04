package com.mingtech.application.pool.bank.msgrecord.domain;

import java.util.Date;
/**
 * 记录发送和接收的报文，网银信贷核心
 * @author wbmengdepeng
 *
 */
public class MsgRecord{
	private String id;
	private String msgCode;//报文号
	private String msgMole;//发送接收标志 1发送  2接收
	private Date msgDate;//发送接收时间
	public String getMsgCode() {
		return msgCode;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public Date getMsgDate() {
		return msgDate;
	}
	public void setMsgDate(Date msgDate) {
		this.msgDate = msgDate;
	}
	public String getMsgMole() {
		return msgMole;
	}
	public void setMsgMole(String msgMole) {
		this.msgMole = msgMole;
	}
	
	
}
