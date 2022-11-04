package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * 中台系统数据对应类
 * */	
public class CenterPlatformBean {
	private String id;				//	主键id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	private String busi_id;			//	业务总开关id
	private String busiFlag;		//	业务总开关  	0关  	1开
	
	private String tx_id;			//	贴现业务总开关id
	private String txFlag;			//	贴现开关	0关	1开
	private String txFlagBeginTime;	//	贴现开关开始时间
	private String txFlagEndTime;	//	贴现开关结束时间
	private String startAmount;
	private String endAmount;
	private String iouNo;				//	借据号
	
	
	public String getIouNo() {
		return iouNo;
	}
	public void setIouNo(String iouNo) {
		this.iouNo = iouNo;
	}
	public String getStartAmount() {
		return startAmount;
	}
	public void setStartAmount(String startAmount) {
		this.startAmount = startAmount;
	}
	public String getEndAmount() {
		return endAmount;
	}
	public void setEndAmount(String endAmount) {
		this.endAmount = endAmount;
	}

	private String branchCode;		//	机构编号
	private String branchName;		//	机构名称
	private String superBranchNo;	//	上级机构号
	
	public String getSuperBranchNo() {
		return superBranchNo;
	}
	public void setSuperBranchNo(String superBranchNo) {
		this.superBranchNo = superBranchNo;
	}

	private String custNo;			//	客户号
	private String custName;		//	客户名
	private String onlineNo;		//	在线协议号
	private String businessNo;		//	贴现合同号
	
	
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	private int pageSize;			//	查询页数
	private int pageNum;			//	查询当前页
	
	private String acceptBankNo;		//	承兑行号
	private String acceptBankName;		//	承兑行行名
	private String effStartDate;		//	生效日期最小范围
	private String effEndDate;			//	生效日期最大范围
	private String workerName;			//	经办人
	private String workerNo;			//	经办人工号
	private String effDate;
	
	private String issuerName;			//	出票人
	
	private String opreaType;			//	业务操作类型  insert、update、delete
	
	private String txAmtFlag;			//	贴现金额控制开关
	private BigDecimal txMinAmt;			//	贴现最小金额
	private BigDecimal txMaxAmt;			//	贴现最大金额
	
	private String minAmount;
	private String maxAmount;
	
	public String getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(String minAmount) {
		this.minAmount = minAmount;
	}
	public String getMaxAmount() {
		return maxAmount;
	}
	public void setMaxAmount(String maxAmount) {
		this.maxAmount = maxAmount;
	}

	private String txTimeConfig;		//	贴现期限控制开关
	private String txTimeConfigType;	//	贴现期限控制类型
	private String txMinTimeConfig;		//	贴现期限控制最小时间
	private String txMaxTimeConfig;		//	贴现期限控制最大时间
	
	private String batchNo;				//	批次号
	private String effState;			//	状态
	private String maintainTime;		//	维护日期
	private String maintainStartDate;	//	维护日期查询起始范围
	private String maintainEndDate;		//	维护日期结束查询范围
	private String effTime;				//	生效日期
	private String rateType;			//	利率类型
	private String isHis;				//	是否历史
	private List<TxRateDetailBean> txRateDetailBeans;	//	利率详情
	
	private String mostFavorRate;			//	最优惠利率
	
	//	票据审价查询		客户名  客户号  协议编号  状态  贴现类型
	private String txType;				//	贴现类型
	private String txState;				//	贴现状态
	private String applyStartDate;		//	申请开始查询日期
	private String applyEndDate;		//	申请结束查询日期
	
	private String acptHeadBankName;	//	承兑总行行名
	private String acptHeadBankNo;		//	承兑总行行号
	private String acptBankType;
	public String getAcptBankType() {
		return acptBankType;
	}
	public void setAcptBankType(String acptBankType) {
		this.acptBankType = acptBankType;
	}

	private String maintainType;		//	维护类型
	
	private String signBranchName;		//	签约机构
	private String openStartDate;		//	协议开通开始查询时间
	private String openEndDate;			//	协议开通结束查询时间
	
	private String dueStartDate;		//	协议到期开始查询时间
	private String dueEndDate;			//	协议到期结束查询时间
	
	private String billNo;				//	票据号
	private String billAmt;				//	票据金额
	
	private String approvePassStartDate;	//	审批通过开始查询时间
	private String approvePassEndDate;		//	审批通过结束查询时间
	
	private String finalApproveBranch;		//	终审机构
	private String txReviewPriceBatchNo;	//	贴现定价审批编号	
	
	private String updateStartDate;			//	协议修改开始查询时间
	private String updateEndDate;			//	协议修改结束查询时间
	
	private String modifyType;				//	修改类型
	private String blackStatus;				//	黑名单状态
	
	private String childBillNoBegin;		//	子票号起
	private String childBillNoEnd;		//	子票号止
	
	public String getChildBillNoBegin() {
		return childBillNoBegin;
	}
	public void setChildBillNoBegin(String childBillNoBegin) {
		this.childBillNoBegin = childBillNoBegin;
	}
	public String getChildBillNoEnd() {
		return childBillNoEnd;
	}
	public void setChildBillNoEnd(String childBillNoEnd) {
		this.childBillNoEnd = childBillNoEnd;
	}
	public String getBusi_id() {
		return busi_id;
	}
	public void setBusi_id(String busi_id) {
		this.busi_id = busi_id;
	}
	public String getTx_id() {
		return tx_id;
	}
	public void setTx_id(String tx_id) {
		this.tx_id = tx_id;
	}
	
	public String getBlackStatus() {
		return blackStatus;
	}
	public void setBlackStatus(String blackStatus) {
		this.blackStatus = blackStatus;
	}
	public String getModifyType() {
		return modifyType;
	}
	public void setModifyType(String modifyType) {
		this.modifyType = modifyType;
	}
	public String getUpdateStartDate() {
		return updateStartDate;
	}
	public void setUpdateStartDate(String updateStartDate) {
		this.updateStartDate = updateStartDate;
	}
	public String getUpdateEndDate() {
		return updateEndDate;
	}
	public void setUpdateEndDate(String updateEndDate) {
		this.updateEndDate = updateEndDate;
	}
	public String getTxReviewPriceBatchNo() {
		return txReviewPriceBatchNo;
	}
	public void setTxReviewPriceBatchNo(String txReviewPriceBatchNo) {
		this.txReviewPriceBatchNo = txReviewPriceBatchNo;
	}
	public String getFinalApproveBranch() {
		return finalApproveBranch;
	}
	public void setFinalApproveBranch(String finalApproveBranch) {
		this.finalApproveBranch = finalApproveBranch;
	}
	public String getApprovePassStartDate() {
		return approvePassStartDate;
	}
	public void setApprovePassStartDate(String approvePassStartDate) {
		this.approvePassStartDate = approvePassStartDate;
	}
	public String getApprovePassEndDate() {
		return approvePassEndDate;
	}
	public void setApprovePassEndDate(String approvePassEndDate) {
		this.approvePassEndDate = approvePassEndDate;
	}
	public String getBillAmt() {
		return billAmt;
	}
	public void setBillAmt(String billAmt) {
		this.billAmt = billAmt;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public String getDueStartDate() {
		return dueStartDate;
	}
	public void setDueStartDate(String dueStartDate) {
		this.dueStartDate = dueStartDate;
	}
	public String getDueEndDate() {
		return dueEndDate;
	}
	public void setDueEndDate(String dueEndDate) {
		this.dueEndDate = dueEndDate;
	}
	public String getOpenStartDate() {
		return openStartDate;
	}
	public void setOpenStartDate(String openStartDate) {
		this.openStartDate = openStartDate;
	}
	public String getOpenEndDate() {
		return openEndDate;
	}
	public void setOpenEndDate(String openEndDate) {
		this.openEndDate = openEndDate;
	}
	public String getSignBranchName() {
		return signBranchName;
	}
	public void setSignBranchName(String signBranchName) {
		this.signBranchName = signBranchName;
	}
	public String getAcptHeadBankName() {
		return acptHeadBankName;
	}
	public void setAcptHeadBankName(String acptHeadBankName) {
		this.acptHeadBankName = acptHeadBankName;
	}
	public String getAcptHeadBankNo() {
		return acptHeadBankNo;
	}
	public void setAcptHeadBankNo(String acptHeadBankNo) {
		this.acptHeadBankNo = acptHeadBankNo;
	}
	public String getMaintainType() {
		return maintainType;
	}
	public void setMaintainType(String maintainType) {
		this.maintainType = maintainType;
	}
	public String getApplyStartDate() {
		return applyStartDate;
	}
	public void setApplyStartDate(String applyStartDate) {
		this.applyStartDate = applyStartDate;
	}
	public String getApplyEndDate() {
		return applyEndDate;
	}
	public void setApplyEndDate(String applyEndDate) {
		this.applyEndDate = applyEndDate;
	}
	public String getTxType() {
		return txType;
	}
	public void setTxType(String txType) {
		this.txType = txType;
	}
	public String getTxState() {
		return txState;
	}
	public void setTxState(String txState) {
		this.txState = txState;
	}
	public String getBusiFlag() {
		return busiFlag;
	}
	public void setBusiFlag(String busiFlag) {
		this.busiFlag = busiFlag;
	}
	public String getTxFlag() {
		return txFlag;
	}
	public void setTxFlag(String txFlag) {
		this.txFlag = txFlag;
	}
	public String getTxFlagBeginTime() {
		return txFlagBeginTime;
	}
	public void setTxFlagBeginTime(String txFlagBeginTime) {
		this.txFlagBeginTime = txFlagBeginTime;
	}
	public String getTxFlagEndTime() {
		return txFlagEndTime;
	}
	public void setTxFlagEndTime(String txFlagEndTime) {
		this.txFlagEndTime = txFlagEndTime;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getOnlineNo() {
		return onlineNo;
	}
	public void setOnlineNo(String onlineNo) {
		this.onlineNo = onlineNo;
	}

	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
	public String getEffDate() {
		return effDate;
	}
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}
	public String getAcceptBankNo() {
		return acceptBankNo;
	}
	public void setAcceptBankNo(String acceptBankNo) {
		this.acceptBankNo = acceptBankNo;
	}
	public String getAcceptBankName() {
		return acceptBankName;
	}
	public void setAcceptBankName(String acceptBankName) {
		this.acceptBankName = acceptBankName;
	}
	public String getEffStartDate() {
		return effStartDate;
	}
	public void setEffStartDate(String effStartDate) {
		this.effStartDate = effStartDate;
	}
	public String getEffEndDate() {
		return effEndDate;
	}
	public void setEffEndDate(String effEndDate) {
		this.effEndDate = effEndDate;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public String getWorkerNo() {
		return workerNo;
	}
	public void setWorkerNo(String workerNo) {
		this.workerNo = workerNo;
	}
	public String getOpreaType() {
		return opreaType;
	}
	public void setOpreaType(String opreaType) {
		this.opreaType = opreaType;
	}
	public String getIssuerName() {
		return issuerName;
	}
	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
	public String getTxAmtFlag() {
		return txAmtFlag;
	}
	public void setTxAmtFlag(String txAmtFlag) {
		this.txAmtFlag = txAmtFlag;
	}
	public BigDecimal getTxMinAmt() {
		return txMinAmt;
	}
	public void setTxMinAmt(BigDecimal txMinAmt) {
		this.txMinAmt = txMinAmt;
	}
	public BigDecimal getTxMaxAmt() {
		return txMaxAmt;
	}
	public void setTxMaxAmt(BigDecimal txMaxAmt) {
		this.txMaxAmt = txMaxAmt;
	}
	public String getTxTimeConfig() {
		return txTimeConfig;
	}
	public void setTxTimeConfig(String txTimeConfig) {
		this.txTimeConfig = txTimeConfig;
	}
	public String getTxTimeConfigType() {
		return txTimeConfigType;
	}
	public void setTxTimeConfigType(String txTimeConfigType) {
		this.txTimeConfigType = txTimeConfigType;
	}
	public String getTxMinTimeConfig() {
		return txMinTimeConfig;
	}
	public void setTxMinTimeConfig(String txMinTimeConfig) {
		this.txMinTimeConfig = txMinTimeConfig;
	}
	public String getTxMaxTimeConfig() {
		return txMaxTimeConfig;
	}
	public void setTxMaxTimeConfig(String txMaxTimeConfig) {
		this.txMaxTimeConfig = txMaxTimeConfig;
	}
	public String getRateType() {
		return rateType;
	}
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}
	public String getIsHis() {
		return isHis;
	}
	public void setIsHis(String isHis) {
		this.isHis = isHis;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getEffState() {
		return effState;
	}
	public void setEffState(String effState) {
		this.effState = effState;
	}
	public String getMaintainTime() {
		return maintainTime;
	}
	public void setMaintainTime(String maintainTime) {
		this.maintainTime = maintainTime;
	}
	public String getMaintainStartDate() {
		return maintainStartDate;
	}
	public void setMaintainStartDate(String maintainStartDate) {
		this.maintainStartDate = maintainStartDate;
	}
	public String getMaintainEndDate() {
		return maintainEndDate;
	}
	public void setMaintainEndDate(String maintainEndDate) {
		this.maintainEndDate = maintainEndDate;
	}
	public String getEffTime() {
		return effTime;
	}
	public void setEffTime(String effTime) {
		this.effTime = effTime;
	}
	public List<TxRateDetailBean> getTxRateDetailBeans() {
		return txRateDetailBeans;
	}
	public void setTxRateDetailBeans(List<TxRateDetailBean> txRateDetailBeans) {
		this.txRateDetailBeans = txRateDetailBeans;
	}
	public String getMostFavorRate() {
		return mostFavorRate;
	}
	public void setMostFavorRate(String mostFavorRate) {
		this.mostFavorRate = mostFavorRate;
	}
}
