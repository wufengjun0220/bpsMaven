package com.mingtech.application.pool.bank.message.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * TMessage entity. @author MyEclipse Persistence Tools
 */

public class TMessageRecord implements java.io.Serializable {

	// Fields

	private String id;
	private String role;
	private String busiType;
	private String phoneNo;
	private String msgContent;
	private Date createTime;
	private Date updateTime;
	private String sendResult;// 0:待发送：1:成功2:失败
	private String sendResultDesc;// 中文状态

	private String onlineNo;       //在线协议编号
	private String addresseeName;     //联系人名称
	// Constructors

	/** default constructor */
	public TMessageRecord() {
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getBusiType() {
		return this.busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getPhoneNo() {
		return this.phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getMsgContent() {
		return this.msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getSendResult() {
		return this.sendResult;
	}

	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getSendResultDesc() {
			String statusDesc = "";
			if("0".equals(this.getSendResult() )|| StringUtils.isEmpty(this.getSendResult())){
				statusDesc = "待处理";
			}else if("1".equals(this.getSendResult())){
				statusDesc = "处理完成";
			}else if("2".equals(this.getSendResult())){
				statusDesc = "处理失败";
			}
			return statusDesc;
	}

	public void setSendResultDesc(String sendResultDesc) {
		this.sendResultDesc = sendResultDesc;
	}



	public String getOnlineNo() {
		return onlineNo;
	}

	public void setOnlineNo(String onlineNo) {
		this.onlineNo = onlineNo;
	}

	public String getAddresseeName() {
		return addresseeName;
	}

	public void setAddresseeName(String addresseeName) {
		this.addresseeName = addresseeName;
	}
	

}