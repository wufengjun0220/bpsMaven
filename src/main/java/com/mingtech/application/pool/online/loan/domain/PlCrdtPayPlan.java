package com.mingtech.application.pool.online.loan.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.ecds.common.DictionaryCache;

/**
 * PlCrdtPayPlan entity. @author MyEclipse Persistence Tools
 */

public class PlCrdtPayPlan implements java.io.Serializable {

	// Fields

	private String id;
	private String crdtId;        //支付批次id
	private String serialNo;      //序列号
	private String contractNo;    //合同编号
	private String loanNo;        //借据编号
	private String onlineCrdtNo;  //在线流贷协议编号
	private String bpsNo;         //票据池编号
	private String loanAcctNo;    //付款账号
	private String loanAcctName;//付款户名
	private String deduAcctNo;    //收款人账号
	private String deduAcctName;  //收款人名称
	private String deduBankCode;  //收款人开户行行号
	private String deduBankName;  //收款人开户行名称
	private String isLocal;       //是否为本行  0：他行1：本行
	private BigDecimal totalAmt;  //总金额：支付计划最初生成时候的金额
	private BigDecimal usedAmt;   //已支付金额：客户从网银真实支付出去的金额
	private BigDecimal repayAmt;  //取消支付金额：客户支付计划修改掉的金额+还款金额
	private String status;        //状态
	private String appNo;         //经办人
	private Date createDate;      //创建时间
	private Date updateTime;      //最近修改时间
	private String operatorType;  //操作
	private String acctFlow;//记账流水
	private Date acctDate;//记账日期
	private String busiNo;//业务处理编码
	private String proFlow;//前置流水
	private String usage;//用途
	private String postscript;//附言
	private String statusDesc;
	private String operationType;//操作类型    0：未处理   1贷款归还   2：修改支付  3:处理完成
	private String operaStatus;//操作状态 SP_05 未处理   SP_01 提交审批
	private String operaBatch;//操作批次号
	private String detailId;//明細id
	
	private BigDecimal payTotalAmt;//支付总金额（不落库字段） = 总金额 - 取消支付金额
	private BigDecimal waitPayAmt;//待支付总金额（不落库字段）= 总金额 - 取消支付金额 - 已支付金额

	/** default constructor */
	public PlCrdtPayPlan() {
	}
	



	public BigDecimal getPayTotalAmt() {
		payTotalAmt = this.getTotalAmt().subtract(this.getRepayAmt());
		return payTotalAmt;
	}


	public void setPayTotalAmt(BigDecimal payTotalAmt) {
		this.payTotalAmt = payTotalAmt;
	}

	public BigDecimal getWaitPayAmt() {
		waitPayAmt = this.getTotalAmt().subtract(this.getRepayAmt()).subtract(this.getUsedAmt());
		return waitPayAmt;
	}



	public void setWaitPayAmt(BigDecimal waitPayAmt) {
		this.waitPayAmt = waitPayAmt;
	}



	public String getDetailId() {
		return detailId;
	}


	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getLoanNo() {
		return this.loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public String getOnlineCrdtNo() {
		return this.onlineCrdtNo;
	}

	public void setOnlineCrdtNo(String onlineCrdtNo) {
		this.onlineCrdtNo = onlineCrdtNo;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getLoanAcctNo() {
		return this.loanAcctNo;
	}

	public void setLoanAcctNo(String loanAcctNo) {
		this.loanAcctNo = loanAcctNo;
	}
	

	public String getLoanAcctName() {
		return loanAcctName;
	}

	public void setLoanAcctName(String loanAcctName) {
		this.loanAcctName = loanAcctName;
	}


	public String getDeduAcctNo() {
		return this.deduAcctNo;
	}

	public void setDeduAcctNo(String deduAcctNo) {
		this.deduAcctNo = deduAcctNo;
	}

	public String getDeduAcctName() {
		return this.deduAcctName;
	}

	public void setDeduAcctName(String deduAcctName) {
		this.deduAcctName = deduAcctName;
	}

	public String getDeduBankCode() {
		return this.deduBankCode;
	}

	public void setDeduBankCode(String deduBankCode) {
		this.deduBankCode = deduBankCode;
	}

	public String getDeduBankName() {
		return this.deduBankName;
	}

	public void setDeduBankName(String deduBankName) {
		this.deduBankName = deduBankName;
	}

	public BigDecimal getTotalAmt() {
		return this.totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public BigDecimal getRepayAmt() {
		return this.repayAmt;
	}

	

	public void setRepayAmt(BigDecimal repayAmt) {
		this.repayAmt = repayAmt;
	}


	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperatorType() {
		return this.operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	public String getAppNo() {
		return this.appNo;
	}

	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getSerialNo() {
		return serialNo;
	}


	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public BigDecimal getUsedAmt() {
		return usedAmt;
	}

	public void setUsedAmt(BigDecimal usedAmt) {
		this.usedAmt = usedAmt;
	}


	public String getCrdtId() {
		return crdtId;
	}


	public void setCrdtId(String crdtId) {
		this.crdtId = crdtId;
	}


	public String getIsLocal() {
		return isLocal;
	}


	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}


	public String getAcctFlow() {
		return acctFlow;
	}


	public void setAcctFlow(String acctFlow) {
		this.acctFlow = acctFlow;
	}


	public Date getAcctDate() {
		return acctDate;
	}


	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}


	public String getBusiNo() {
		return busiNo;
	}


	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}


	public String getProFlow() {
		return proFlow;
	}


	public void setProFlow(String proFlow) {
		this.proFlow = proFlow;
	}


	public String getUsage() {
		return usage;
	}


	public void setUsage(String usage) {
		this.usage = usage;
	}


	public String getPostscript() {
		return postscript;
	}


	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}


	public String getStatusDesc() {
		String statusDesc=DictionaryCache.getCrdtStatuspMap(this.getStatus());
		if(StringUtils.isBlank(statusDesc)){
			statusDesc=this.getStatus();
		}
		return statusDesc;
	}


	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}


	public String getOperationType() {
		return operationType;
	}


	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}


	public String getOperaStatus() {
		return operaStatus;
	}


	public void setOperaStatus(String operaStatus) {
		this.operaStatus = operaStatus;
	}


	public String getOperaBatch() {
		return operaBatch;
	}


	public void setOperaBatch(String operaBatch) {
		this.operaBatch = operaBatch;
	}
	
	
	
	
	


}