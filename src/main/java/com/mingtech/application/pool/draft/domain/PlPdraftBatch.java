package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;

/**
 * 网银纸票出池批次信息表
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-20
 */

public class PlPdraftBatch implements java.io.Serializable {

	// Fields

	private String id;
	private String bpsNo;        //票据池编号                
	private String bpsName;      //票据池名称            
	private String batchNo;      //批次号                
	private String custNo;       //核心客户号  ：发起出池申请的客户，不一定为集团主户
	private String orgCode;      //客户组织机构代码      
	private String custName;     //客户名称              
	private String elsignature;  //电子签名              
	private String batchType;    //批次类型              
	private BigDecimal totalNum; //票据总张数            
	private BigDecimal totalAmt;     //票据总金额            
	private String workerName;   //（出池）经办人姓名    
	private String worlerPhoneNo;//（出池）经办人手机号  
	private String workerId;     //（出池）经办人身份证号
	private String useWay;       //（出池）用途  
	private String remark;       //备注
	private String status;       //状态	1生效 0失效
	private String isPoolOutEnd; //是否出池结束   1批次未出池  0批次已出池

	// Constructors

	/** default constructor */
	public PlPdraftBatch() {
	}

	/** full constructor */
	public PlPdraftBatch(String bpsNo, String bpsName, String batchNo,
			String custNo, String orgCode, String custName, String elsignature,
			String batchType, BigDecimal totalNum, BigDecimal totalAmt,
			String workerName, String worlerPhoneNo, String workerId,
			String useWay,String status,String remark) {
		this.bpsNo = bpsNo;
		this.bpsName = bpsName;
		this.batchNo = batchNo;
		this.custNo = custNo;
		this.orgCode = orgCode;
		this.custName = custName;
		this.elsignature = elsignature;
		this.batchType = batchType;
		this.totalNum = totalNum;
		this.totalAmt = totalAmt;
		this.workerName = workerName;
		this.worlerPhoneNo = worlerPhoneNo;
		this.workerId = workerId;
		this.useWay = useWay;
		this.status = status;
		this.remark = remark;
	}

	// Property accessors

	
	
	
	public String getId() {
		return this.id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return this.bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}

	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getElsignature() {
		return this.elsignature;
	}

	public void setElsignature(String elsignature) {
		this.elsignature = elsignature;
	}

	public String getBatchType() {
		return this.batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public BigDecimal getTotalNum() {
		return this.totalNum;
	}

	public void setTotalNum(BigDecimal totalNum) {
		this.totalNum = totalNum;
	}

	public BigDecimal getTotalAmt() {
		return this.totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getWorkerName() {
		return this.workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getWorlerPhoneNo() {
		return this.worlerPhoneNo;
	}

	public void setWorlerPhoneNo(String worlerPhoneNo) {
		this.worlerPhoneNo = worlerPhoneNo;
	}

	public String getWorkerId() {
		return this.workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getUseWay() {
		return this.useWay;
	}

	public void setUseWay(String useWay) {
		this.useWay = useWay;
	}

	public String getIsPoolOutEnd() {
		return isPoolOutEnd;
	}

	public void setIsPoolOutEnd(String isPoolOutEnd) {
		this.isPoolOutEnd = isPoolOutEnd;
	}

}