package com.mingtech.application.pool.online.loan.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 在线流贷支付计划支付记录实体类
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-19
 * @copyright 北明明润（北京）科技有限责任公司
 */

public class PlCrdtPayList implements java.io.Serializable {


	private static final long serialVersionUID = -8564026379794185321L;

	private String id;                                     
	private String payPlanId;     //对应支付计划表ID      
	private String serialNo;      //序列号
	private String loanNo;        //借据编号                                    
	private String contractNo;    //合同编号                                    
	private String onlineCrdtNo;  //在线流贷协议编号                                
	private String bpsNo;         //票据池编号                                   
	private String loanAcctNo;    //付款账号                                    
	private String loanAcctName;  //付款户名                                    
	private String deduAcctNo;    //收款人名称                                   
	private String deduAcctName;  //收款人账号                                   
	private String deduBankCode;  //收款人开户行行号                                
	private String deduBankName;  //收款人开户行名称                                
	private String isLocal;       //是否跨行 0-否 1-是                            
	private BigDecimal payAmt;    //本次支付金额                                  
	private String status;        //状态 00-初始化 01-支付成功 02-支付失败               
	private String operatorType;  //操作类型 0-付款交易  1-还款交易   2-修改    3-贷款未用归还            
	private String transChanel;   //渠道 EBK-网银 BPS-票据池                       
	private String usage;         //用途 网银码值                                 
	private String postscript;    //附言                                      
	private String payDesc;       //支付结果说明                                  
	private String payReqSerNo;   //支付申请流水号                                 
	private Date payAcctDate;   //账务日期，即支付申请日期，核心查证支付结果的时候需要用到
	private String payRespSerNo;  // 支付结果流水号     
	private String repayFlowNo;   //贷款归还/提前还款流水号--用来标记同一个批次的还款申请
	private Date createDate; //创建时间                                    
	private Date updateTime; //最近修改时间           
	private Date taskDate; //最近修改时间           


	public PlCrdtPayList() {
	}


	

	public Date getTaskDate() {
		return taskDate;
	}




	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}




	public Date getPayAcctDate() {
		return payAcctDate;
	}




	public void setPayAcctDate(Date payAcctDate) {
		this.payAcctDate = payAcctDate;
	}




	public String getRepayFlowNo() {
		return repayFlowNo;
	}



	public void setRepayFlowNo(String repayFlowNo) {
		this.repayFlowNo = repayFlowNo;
	}



	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPayPlanId() {
		return this.payPlanId;
	}

	public void setPayPlanId(String payPlanId) {
		this.payPlanId = payPlanId;
	}

	
	
	public String getSerialNo() {
		return serialNo;
	}



	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}



	public String getLoanNo() {
		return this.loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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
		return this.loanAcctName;
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

	public String getIsLocal() {
		return this.isLocal;
	}

	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}

	public BigDecimal getPayAmt() {
		return this.payAmt;
	}

	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
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

	public String getTransChanel() {
		return this.transChanel;
	}

	public void setTransChanel(String transChanel) {
		this.transChanel = transChanel;
	}

	public String getUsage() {
		return this.usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getPostscript() {
		return this.postscript;
	}

	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}

	public String getPayDesc() {
		return this.payDesc;
	}

	public void setPayDesc(String payDesc) {
		this.payDesc = payDesc;
	}

	public String getPayReqSerNo() {
		return this.payReqSerNo;
	}

	public void setPayReqSerNo(String payReqSerNo) {
		this.payReqSerNo = payReqSerNo;
	}

	public String getPayRespSerNo() {
		return this.payRespSerNo;
	}

	public void setPayRespSerNo(String payRespSerNo) {
		this.payRespSerNo = payRespSerNo;
	}



	public Date getCreateDate() {
		return createDate;
	}



	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}



	public Date getUpdateTime() {
		return updateTime;
	}



	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	


}