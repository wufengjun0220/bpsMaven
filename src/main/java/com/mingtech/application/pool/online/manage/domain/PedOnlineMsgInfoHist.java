package com.mingtech.application.pool.online.manage.domain;

import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * PedOnlineMsgInfoHist entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineMsgInfoHist implements java.io.Serializable {

	// Fields

	private String id;                 //主键ID
	private String addresseeNo;        //联系人编号
	private String addresseeName;      //联系人名称
	private String addresseeRole;      //联系人身份
	private String addresseePhoneNo;   //联系人电话
	private String onlineProtocolType; //在线协议类型
	private String onlineNo;           //在线协议编号
	private String onlineProtocolId;   //在线协议主键
	private String modeType;           //修改模式
	private String modeMark;           //修改标记
	private String lastSourceId;       //上一次修改的id
	private Date createTime;         //创建时间
	private Date updateTime;         //最近修改时间
	//页面展示
	private String addresseeRoleDesc;
	private String modeTypeDesc;

	// Constructors

	/** default constructor */
	public PedOnlineMsgInfoHist() {
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

	public String getModeType() {
		return this.modeType;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}

	public String getModeMark() {
		return this.modeMark;
	}

	public void setModeMark(String modeMark) {
		this.modeMark = modeMark;
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

	public String getLastSourceId() {
		return lastSourceId;
	}

	public void setLastSourceId(String lastSourceId) {
		this.lastSourceId = lastSourceId;
	}



	public String getAddresseeRoleDesc() {
		if(PublicStaticDefineTab.ROLE_0.equals(this.getAddresseeRole())){
			return "员工-"+this.getAddresseeRole();
		}else if(PublicStaticDefineTab.ROLE_1.equals(this.getAddresseeRole())){
			return "客户-"+this.getAddresseeRole();
		}else{
			return addresseeRoleDesc;
		}
	}
	
	public String getModeTypeDesc() {
		return modeTypeDesc = DictionaryCache.getModeTypeName(this.getModeType());
	}

	

}