package com.mingtech.application.pool.online.manage.domain;

import java.util.Date;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * PedOnlineBlackInfo entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineBlackInfo implements java.io.Serializable {

	// Fields

	private String id;
	private String custNo;     //客户号
	private String custName;   //客户名称
	private String custOrgcode;//组织结构代码
	private String openFlag;   //是否禁入
	private String appNo;      //经办人
	private String status;     //状态 0：失效 1：生效
	private Date startDate;    //开始日期
	private Date endtDate;     //结束日期
	private String validDate;  //有效期
	private String dateType;   //日期类型  年、月、日
	private String deprtId;  //报送机构
	private String deprtName;  //报送机构名称
	private Date createTime;   //创建时间
	private Date updateTime;   //最近修改时间
	//页面展示
	private String statusDesc;//
	private String dateTypeDesc;   //日期类型  年、月、日


	// Constructors

	/** default constructor */
	public PedOnlineBlackInfo() {
	}


	public String getDeprtId() {
		return deprtId;
	}


	public void setDeprtId(String deprtId) {
		this.deprtId = deprtId;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndtDate() {
		return endtDate;
	}


	public void setEndtDate(Date endtDate) {
		this.endtDate = endtDate;
	}


	public String getValidDate() {
		return validDate;
	}


	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}


	public String getDeprtName() {
		return deprtName;
	}


	public void setDeprtName(String deprtName) {
		this.deprtName = deprtName;
	}


	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustOrgcode() {
		return this.custOrgcode;
	}

	public void setCustOrgcode(String custOrgcode) {
		this.custOrgcode = custOrgcode;
	}

	public String getOpenFlag() {
		return this.openFlag;
	}

	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}

	public String getAppNo() {
		return this.appNo;
	}

	public void setAppNo(String appNo) {
		this.appNo = appNo;
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


	public String getStatusDesc() {
		if(PublicStaticDefineTab.STATUS_0.equals(this.getStatus())){
			return "失效-"+this.getStatus();
		}else if(PublicStaticDefineTab.STATUS_1.equals(this.getStatus())){
			return "生效-"+this.getStatus();
		}
		return statusDesc;
	}


	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}


	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}


	public String getDateTypeDesc() {
		if(PublicStaticDefineTab.YEAR_FLAG.equals(this.getDateType())){
			return this.getValidDate()+"年";
		}else if(PublicStaticDefineTab.MONTH_FLAG.equals(this.getDateType())){
			return this.getValidDate()+"月";
		}else if(PublicStaticDefineTab.DAY_FLAG.equals(this.getDateType())){
			return this.getValidDate()+"日";
		}else{
			return dateTypeDesc;
		}
	}

	

}