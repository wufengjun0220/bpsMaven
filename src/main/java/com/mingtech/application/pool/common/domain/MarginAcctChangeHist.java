package com.mingtech.application.pool.common.domain;

import java.util.Date;



/**
 * PedMarginAcctChangeHist entity. @author MyEclipse Persistence Tools
 */

public class MarginAcctChangeHist implements java.io.Serializable {


	private static final long serialVersionUID = -736352045900981384L;
	private String id;
	private String bpsNo;//票据池编号
	private String oldAcctNo;//原保证金账号
	private String newAcctNo;//新保证金账号
	private Date createDate;//创建时间

	public MarginAcctChangeHist() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getOldAcctNo() {
		return this.oldAcctNo;
	}

	public void setOldAcctNo(String oldAcctNo) {
		this.oldAcctNo = oldAcctNo;
	}

	public String getNewAcctNo() {
		return this.newAcctNo;
	}

	public void setNewAcctNo(String newAcctNo) {
		this.newAcctNo = newAcctNo;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}