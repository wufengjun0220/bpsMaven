package com.mingtech.application.pool.edu.domain;

public class CreditPedBean {
	
	private String id;
	private String acceptor;//保贴人
	private Double billAmtCount;//总额度
	private String bpsNo;//票据池编号
	private String bpsName;//票据池名称
	private String isGroup;//是否集团
	private String guarantDiscName;//保贴人名称
	private String guarantDiscNo;//保贴编号
	private String creditObjType;//额度主题类型
	
	public String getGuarantDiscName() {
		return guarantDiscName;
	}
	public void setGuarantDiscName(String guarantDiscName) {
		this.guarantDiscName = guarantDiscName;
	}
	public String getGuarantDiscNo() {
		return guarantDiscNo;
	}
	public void setGuarantDiscNo(String guarantDiscNo) {
		this.guarantDiscNo = guarantDiscNo;
	}
	public String getCreditObjType() {
		return creditObjType;
	}
	public void setCreditObjType(String creditObjType) {
		this.creditObjType = creditObjType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public Double getBillAmtCount() {
		return billAmtCount;
	}
	public void setBillAmtCount(Double billAmtCount) {
		this.billAmtCount = billAmtCount;
	}
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getBpsName() {
		return bpsName;
	}
	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}
	public String getIsGroup() {
		return isGroup;
	}
	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}	
	
}