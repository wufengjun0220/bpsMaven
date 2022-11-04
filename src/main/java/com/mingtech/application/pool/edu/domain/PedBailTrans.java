package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;

/**
 * 
 * @Title: 保证金支取审批记录对象
 * @Description: 总量模式时保证金支取需走审批流
 * @author Wufengjun
 * @date 2021-06-29
 */

public class PedBailTrans implements java.io.Serializable {


	private static final long serialVersionUID = 1L;
	private String id;//主键ID        
	private String bpsNo;//票据池编号
	private String custName;//客户名称
	private String customer;//核心客户号
	private String cretNo;//凭证编号  --？？
	private BigDecimal tranAmt; //交易金额     
	private String tranType;//交易类型   1：支取   2：存入
	private String drAcctNo;       //借方账号（保证金账号）
	private String drAcctName;    //借方账号名称（保证金账号名称）
	private String crAcctNo;     //贷款账号（入账账号）
	private String crAcctName;  //贷款账号名称（入账账号名称）
	private Date createDate;   //创建日期
	private Date createTime;//创建时间
	private Date updateTime;//修改时间
	private String userNo;    //客户经理编号  协议中的客户经理
	private String userName;//客户经理名称
	private String brcBld;      //客户经理所在的机构
	private String brcUser;		//上送核心的柜员
	private String remark;   //备注（附言）
	private String status;       //状态  SP_05新增  SP_01：提交审批   SP_02：处理中   SP_03：驳回   SP_04：通过
	private String auditRemark;//审批意见，显示全部岗位审批意见，含审批人跟审批意见
	private String planStatus;//支付状态  0-未处理/1-成功/2-失败/3-作废（超过一个自然日（24小时）未处理的）  
	private String usage;//用途
	private String SerSeqNo;//流水号
	private String statusDesc;//
	private String planStatusDesc;
	private String errLog;//错误日志
	
	
	public String getBrcUser() {
		return brcUser;
	}


	public void setBrcUser(String brcUser) {
		this.brcUser = brcUser;
	}


	public String getErrLog() {
		return errLog;
	}


	public void setErrLog(String errLog) {
		this.errLog = errLog;
	}


	/** default constructor */
	public PedBailTrans() {
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCustName() {
		return custName;
	}


	public void setCustName(String custName) {
		this.custName = custName;
	}


	public BigDecimal getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(BigDecimal tranAmt) {
		this.tranAmt = tranAmt;
	}


	public String getDrAcctNo() {
		return drAcctNo;
	}

	public void setDrAcctNo(String drAcctNo) {
		this.drAcctNo = drAcctNo;
	}
	public String getCrAcctNo() {
		return crAcctNo;
	}

	public void setCrAcctNo(String crAcctNo) {
		this.crAcctNo = crAcctNo;
	}

	
	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getDrAcctName() {
		return drAcctName;
	}

	public void setDrAcctName(String drAcctName) {
		this.drAcctName = drAcctName;
	}

	public String getCrAcctName() {
		return crAcctName;
	}

	public void setCrAcctName(String crAcctName) {
		this.crAcctName = crAcctName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBrcBld() {
		return brcBld;
	}

	public void setBrcBld(String brcBld) {
		this.brcBld = brcBld;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBpsNo() {
		return bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCretNo() {
		return cretNo;
	}

	public void setCretNo(String cretNo) {
		this.cretNo = cretNo;
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

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public String getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getSerSeqNo() {
		return SerSeqNo;
	}

	public void setSerSeqNo(String serSeqNo) {
		SerSeqNo = serSeqNo;
	}

	public String getStatusDesc() {
		return statusDesc = DictionaryCache.getFromPoolDictMap(String.valueOf(status));
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getPlanStatusDesc() {
		return planStatusDesc = DictionaryCache.getFromPoolDictMap(String.valueOf(planStatus));
	}

	public void setPlanStatusDesc(String planStatusDesc) {
		this.planStatusDesc = planStatusDesc;
	}
	
}