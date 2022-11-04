/**
 * 
 */
package com.mingtech.application.pool.base.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wbyecheng
 * 
 * 资产池业务明细基础实体
 * 
 */
public class PoolBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;// 主键ID
	private String branchId;// 机构ID
	//private String plBatchId;// 批次ID  废弃，在子类中实现
	private String plIsseAmtValue;// 币种
	private BigDecimal plIsseAmt;// 总金额
	private Date plReqTime;// 申请时间
	private String plApplyNm;// 申请人名称
	private String plCommId;// 申请人组织机构代码
	private String plUSCC;//申请人统一信用代码
	private String plApplyAcctId;// 申请人账号
	private String plApplyAcctSvcr;// 申请人开户行行号
	private String plApplyAcctSvcrNm;// 申请人开户行名称
	private String plStatus;// 状态
	private String plTradeType;// 代保管YW_02/票据池 YW_01 存单池YW_03
	private String plRemark;// 备注

	private String accNo;//电票签约账号
	private String guaranteeNo;//担保编号
	
	private String devSeqNo;//核心记账第三方流水号
	private String poolAgreement;//票据池编号
	private String BtFlag;//商票保贴标识 	1是占用成功 	0或空是释放成功或未占用
	private String accptrOrg;//承兑人组织机构代码
	private String TXFlag;//强贴标识
	private Date taskDate;//修改时间--调度任务使用
	

	public Date getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}

	public String getTXFlag() {
		return TXFlag;
	}

	public void setTXFlag(String TXFlag) {
		this.TXFlag = TXFlag;
	}

	public String getAccptrOrg() {
		return accptrOrg;
	}

	public void setAccptrOrg(String accptrOrg) {
		this.accptrOrg = accptrOrg;
	}

	public String getBtFlag() {
		return BtFlag;
	}

	public void setBtFlag(String btFlag) {
		BtFlag = btFlag;
	}

	public String getPoolAgreement() {
		return poolAgreement;
	}

	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}

	public void setNULL(){
		this.id = null;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

//	public String getPlBatchId() {
//		return plBatchId;
//	}
//
//	public void setPlBatchId(String plBatchId) {
//		this.plBatchId = plBatchId;
//	}

	public String getPlIsseAmtValue() {
		return plIsseAmtValue;
	}

	public void setPlIsseAmtValue(String plIsseAmtValue) {
		this.plIsseAmtValue = plIsseAmtValue;
	}

	public BigDecimal getPlIsseAmt() {
		return plIsseAmt;
	}

	public void setPlIsseAmt(BigDecimal plIsseAmt) {
		this.plIsseAmt = plIsseAmt;
	}

	public Date getPlReqTime() {
		return plReqTime;
	}

	public void setPlReqTime(Date plReqTime) {
		this.plReqTime = plReqTime;
	}

	public String getPlApplyNm() {
		return plApplyNm;
	}

	public void setPlApplyNm(String plApplyNm) {
		this.plApplyNm = plApplyNm;
	}

	public String getPlCommId() {
		return plCommId;
	}

	public void setPlCommId(String plCommId) {
		this.plCommId = plCommId;
	}

	public String getPlApplyAcctId() {
		return plApplyAcctId;
	}

	public void setPlApplyAcctId(String plApplyAcctId) {
		this.plApplyAcctId = plApplyAcctId;
	}

	public String getPlStatus() {
		return plStatus;
	}

	public void setPlStatus(String plStatus) {
		this.plStatus = plStatus;
	}

	public String getPlTradeType() {
		return plTradeType;
	}

	public void setPlTradeType(String plTradeType) {
		this.plTradeType = plTradeType;
	}

	public String getPlRemark() {
		return plRemark;
	}

	public void setPlRemark(String plRemark) {
		this.plRemark = plRemark;
	}

	public String getPlUSCC() {
		return plUSCC;
	}

	public void setPlUSCC(String plUSCC) {
		this.plUSCC = plUSCC;
	}

	public String getPlApplyAcctSvcr() {
		return plApplyAcctSvcr;
	}

	public void setPlApplyAcctSvcr(String plApplyAcctSvcr) {
		this.plApplyAcctSvcr = plApplyAcctSvcr;
	}

	public String getPlApplyAcctSvcrNm() {
		return plApplyAcctSvcrNm;
	}

	public void setPlApplyAcctSvcrNm(String plApplyAcctSvcrNm) {
		this.plApplyAcctSvcrNm = plApplyAcctSvcrNm;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getGuaranteeNo() {
		return guaranteeNo;
	}

	public void setGuaranteeNo(String guaranteeNo) {
		this.guaranteeNo = guaranteeNo;
	}

	public String getDevSeqNo() {
		return devSeqNo;
	}

	public void setDevSeqNo(String devSeqNo) {
		this.devSeqNo = devSeqNo;
	}
	
	
}
