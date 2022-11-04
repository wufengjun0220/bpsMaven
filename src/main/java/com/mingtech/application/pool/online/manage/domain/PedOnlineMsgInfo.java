package com.mingtech.application.pool.online.manage.domain;

import java.util.Date;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * PedOnlineMsgInfo entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineMsgInfo implements java.io.Serializable {

	// Fields

	private String id;
	private String addresseeNo;       //联系人编号
	private String addresseeName;     //联系人名称
	private String addresseeRole;     //联系人身份  0：员工 1：客户
	private String addresseePhoneNo;  //联系人电话
	private String onlineProtocolType;//在线协议类型
	private String onlineNo;          //在线协议编号
	private String onlineProtocolId;  //在线协议主键
	private Date createTime;          //创建时间
	private Date updateTime;          //最近修改时间
	//页面展示
	private String addresseeRoleDesc;
	private String modeType;          //修改模式 MOD_01:新增 MOD_02:修改 MOD_03:删除 不存库

	// Constructors

	/** default constructor */
	public PedOnlineMsgInfo() {
	}

	/** full constructor */
	public PedOnlineMsgInfo(String addresseeNo, String addresseeName,
			String addresseeRole, String addresseePhoneNo,
			String onlineProtocolType, String onlineNo,
			String onlineProtocolId, Date createTime, Date updateTime) {
		this.addresseeNo = addresseeNo;
		this.addresseeName = addresseeName;
		this.addresseeRole = addresseeRole;
		this.addresseePhoneNo = addresseePhoneNo;
		this.onlineProtocolType = onlineProtocolType;
		this.onlineNo = onlineNo;
		this.onlineProtocolId = onlineProtocolId;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddresseeNo() {
		return this.addresseeNo;
	}

	public void setAddresseeNo(String addresseeNo) {
		this.addresseeNo = addresseeNo;
	}

	public String getAddresseeName() {
		return this.addresseeName;
	}

	public void setAddresseeName(String addresseeName) {
		this.addresseeName = addresseeName;
	}

	public String getAddresseeRole() {
		return this.addresseeRole;
	}

	public void setAddresseeRole(String addresseeRole) {
		this.addresseeRole = addresseeRole;
	}

	public String getAddresseePhoneNo() {
		return this.addresseePhoneNo;
	}

	public void setAddresseePhoneNo(String addresseePhoneNo) {
		this.addresseePhoneNo = addresseePhoneNo;
	}

	public String getOnlineProtocolType() {
		return this.onlineProtocolType;
	}

	public void setOnlineProtocolType(String onlineProtocolType) {
		this.onlineProtocolType = onlineProtocolType;
	}

	public String getOnlineNo() {
		return this.onlineNo;
	}

	public void setOnlineNo(String onlineNo) {
		this.onlineNo = onlineNo;
	}

	public String getOnlineProtocolId() {
		return this.onlineProtocolId;
	}

	public void setOnlineProtocolId(String onlineProtocolId) {
		this.onlineProtocolId = onlineProtocolId;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getModeType() {
		return modeType;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}

	public String getAddresseeRoleDesc() {
		if(PublicStaticDefineTab.ROLE_0.equals(this.getAddresseeRole())){
			return "员工";
		}else if(PublicStaticDefineTab.ROLE_1.equals(this.getAddresseeRole())){
			return "客户";
		}else{
			return addresseeRoleDesc;
		}
	}

	public void setAddresseeRoleDesc(String addresseeRoleDesc) {
		this.addresseeRoleDesc = addresseeRoleDesc;
	}
	
	

}