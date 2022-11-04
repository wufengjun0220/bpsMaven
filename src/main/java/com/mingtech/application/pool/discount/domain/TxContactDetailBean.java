package com.mingtech.application.pool.discount.domain;

/**
 * 贴现协议联系人信息
 * */
public class TxContactDetailBean {
	
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TxContactDetailBean() {
		super();
	}
	private String modeTypeDesc;	//	修改类型 MOD_01-新增  MOD_02-维护 MOD_03=删除  其他-修改
	private String contactsType;	//	客户类型
	private String hisContactsType;	//	历史客户类型
	private String contactsTel;		//	手机号码
	private String hisContactsTel;	//	历史手机号码
	private String contactsName;	//	姓名
	private String hisContactsName;	//	历史姓名
	private String contactsNo;		//	客户号
	private String hisContactsNo;	//	历史客户号
	
	public String getContactsNo() {
		return contactsNo;
	}
	public String getHisContactsType() {
		return hisContactsType;
	}
	public void setHisContactsType(String hisContactsType) {
		this.hisContactsType = hisContactsType;
	}
	public String getHisContactsTel() {
		return hisContactsTel;
	}
	public void setHisContactsTel(String hisContactsTel) {
		this.hisContactsTel = hisContactsTel;
	}
	public String getHisContactsName() {
		return hisContactsName;
	}
	public void setHisContactsName(String hisContactsName) {
		this.hisContactsName = hisContactsName;
	}
	public String getHisContactsNo() {
		return hisContactsNo;
	}
	public void setHisContactsNo(String hisContactsNo) {
		this.hisContactsNo = hisContactsNo;
	}
	public void setContactsNo(String contactsNo) {
		this.contactsNo = contactsNo;
	}
	public String getContactsName() {
		return contactsName;
	}
	public void setContactsName(String contactsName) {
		this.contactsName = contactsName;
	}
	public String getContactsType() {
		return contactsType;
	}
	public void setContactsType(String contactsType) {
		this.contactsType = contactsType;
	}
	public String getContactsTel() {
		return contactsTel;
	}
	public void setContactsTel(String contactsTel) {
		this.contactsTel = contactsTel;
	}
	public TxContactDetailBean(String contactsNo, String contactsName,
			String contactsType, String contactsTel) {
		super();
		this.contactsNo = contactsNo;
		this.contactsName = contactsName;
		this.contactsType = contactsType;
		this.contactsTel = contactsTel;
	}
	public String getModeTypeDesc() {
		return modeTypeDesc;
	}
	public void setModeTypeDesc(String modeTypeDesc) {
		this.modeTypeDesc = modeTypeDesc;
	}
	
	
}
