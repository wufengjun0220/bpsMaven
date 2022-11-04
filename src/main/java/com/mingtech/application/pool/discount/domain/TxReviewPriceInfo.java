package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.domain.ApproveDto;

/**
 * 贴现审价信息
 * */
public class TxReviewPriceInfo {
	private String id;
	private String txReviewPriceBatchNo;			//	贴现定价审批编号	
	
	private String billNo;							//	票据号码/票据包号
	private String billType;						//	票据类型
	private String acptBankName;					//	承兑行名
	private String acptBankNo;						//	承兑行号
	private BigDecimal billAmt;						//	票面金额（元）
	
	private BigDecimal approveAmt;					//	审批额度
	private BigDecimal currentAmt;					//	当前额度
	
	
	private BigDecimal usedAmt;						//	已贴现金额
	private BigDecimal availableAmt;				//	未贴现金额
	
	private BigDecimal reduceAmt;					//	调减金额
	
	private BigDecimal applyTxRate;						//	申请贴现利率（%）
	private BigDecimal guidanceRate;	//	guidance_rate	指导利率
	
	private String txType;							//	贴现类型  01：直贴、02：直转通、03：再贴现  04:模式一  05：模式二
	private String txPattern;						//	模式		01：票据  02：额度一	02：额度三
	private String custNo;							//	核心客户号
	private String custName;						//	客户名称
	private String onlineNo;						//	贴现协议号
	private String applyState;						//	状态  00：新建、SP_02：审批中、SP_04：审批通过待发送、1：生效、2：待生效、0：失效
	private String applyDate;						//	申请日期
	private String effDate;							//	生效日期
	private String dueDate;							//	到期日期
	private String finalApproveBranch;				//	终审机构
	private String approveBranchType;				//	审批层级  01：条线   02：分支行   03：资产负债管理部  04：金融市场部
	
	private String approveBranchNo;
	private String approveBranchName;
	private String approveDate;
	
	private String workerName;						//	经办人
	private String workerNo;						//	经办人工号
	private String workerBranch;					//	经办机构
	
	private String acptBankType;					//	承兑行别
	private String txTerm;							//	贴现期限
	private BigDecimal favorRate;		//	favor_rate		优惠利率
	private BigDecimal bestFavorRate;	//	best_favor_rate	最优惠利率
	private String tjId;
	
	
	public String getTjId() {
		return tjId;
	}
	public void setTjId(String tjId) {
		this.tjId = tjId;
	}
	private TxReviewPriceDetail txReviewPriceDetail; 	//  详情信息
	private List<IntroBillInfoBean> introBillInfoBeans = new ArrayList<IntroBillInfoBean>();
	private List<TxReduceInfoBean> txReduceInfoBeans = new ArrayList<TxReduceInfoBean>();
	private List<ApproveDto> approveDtos = new ArrayList<ApproveDto>();
	private List<ApproveAuditDto> approveAuditDtos = new ArrayList<ApproveAuditDto>();
	
	
	public List<ApproveAuditDto> getApproveAuditDtos() {
		return approveAuditDtos;
	}
	public void setApproveAuditDtos(List<ApproveAuditDto> approveAuditDtos) {
		this.approveAuditDtos = approveAuditDtos;
	}
	public List<ApproveDto> getApproveDtos() {
		return approveDtos;
	}
	public void setApproveDtos(List<ApproveDto> approveDtos) {
		this.approveDtos = approveDtos;
	}
	public String getApproveBranchType() {
		return approveBranchType;
	}
	public void setApproveBranchType(String approveBranchType) {
		this.approveBranchType = approveBranchType;
	}
	public String getApproveBranchNo() {
		return approveBranchNo;
	}
	public void setApproveBranchNo(String approveBranchNo) {
		this.approveBranchNo = approveBranchNo;
	}
	public String getApproveBranchName() {
		return approveBranchName;
	}
	public void setApproveBranchName(String approveBranchName) {
		this.approveBranchName = approveBranchName;
	}
	public String getApproveDate() {
		return approveDate;
	}
	public void setApproveDate(String approveDate) {
		this.approveDate = approveDate;
	}
	
	public BigDecimal getApplyTxRate() {
		return applyTxRate;
	}
	public BigDecimal getGuidanceRate() {
		return guidanceRate;
	}
	public void setGuidanceRate(BigDecimal guidanceRate) {
		this.guidanceRate = guidanceRate;
	}
	public BigDecimal getFavorRate() {
		return favorRate;
	}
	public void setFavorRate(BigDecimal favorRate) {
		this.favorRate = favorRate;
	}
	public BigDecimal getBestFavorRate() {
		return bestFavorRate;
	}
	public void setBestFavorRate(BigDecimal bestFavorRate) {
		this.bestFavorRate = bestFavorRate;
	}
	public void setApplyTxRate(BigDecimal applyTxRate) {
		this.applyTxRate = applyTxRate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BigDecimal getApproveAmt() {
		return approveAmt;
	}
	public void setApproveAmt(BigDecimal approveAmt) {
		this.approveAmt = approveAmt;
	}
	public BigDecimal getCurrentAmt() {
		return currentAmt;
	}
	public void setCurrentAmt(BigDecimal currentAmt) {
		this.currentAmt = currentAmt;
	}
	public BigDecimal getReduceAmt() {
		return reduceAmt;
	}
	public void setReduceAmt(BigDecimal reduceAmt) {
		this.reduceAmt = reduceAmt;
	}
	public String getAcptBankType() {
		return acptBankType;
	}
	public void setAcptBankType(String acptBankType) {
		this.acptBankType = acptBankType;
	}
	public String getTxTerm() {
		return txTerm;
	}
	public void setTxTerm(String txTerm) {
		this.txTerm = txTerm;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getAcptBankName() {
		return acptBankName;
	}
	public void setAcptBankName(String acptBankName) {
		this.acptBankName = acptBankName;
	}
	public String getAcptBankNo() {
		return acptBankNo;
	}
	public void setAcptBankNo(String acptBankNo) {
		this.acptBankNo = acptBankNo;
	}
	public BigDecimal getBillAmt() {
		return billAmt;
	}
	public void setBillAmt(BigDecimal billAmt) {
		this.billAmt = billAmt;
	}
	public BigDecimal getUsedAmt() {
		return usedAmt;
	}
	public void setUsedAmt(BigDecimal usedAmt) {
		this.usedAmt = usedAmt;
	}
	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}
	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}
	
	public String getEffDate() {
		return effDate;
	}
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getFinalApproveBranch() {
		return finalApproveBranch;
	}
	public void setFinalApproveBranch(String finalApproveBranch) {
		this.finalApproveBranch = finalApproveBranch;
	}
	
	public List<IntroBillInfoBean> getIntroBillInfoBeans() {
		return introBillInfoBeans;
	}
	public void setIntroBillInfoBeans(List<IntroBillInfoBean> introBillInfoBeans) {
		this.introBillInfoBeans = introBillInfoBeans;
	}
	public String getTxReviewPriceBatchNo() {
		return txReviewPriceBatchNo;
	}
	public void setTxReviewPriceBatchNo(String txReviewPriceBatchNo) {
		this.txReviewPriceBatchNo = txReviewPriceBatchNo;
	}
	public String getTxType() {
		return txType;
	}
	public void setTxType(String txType) {
		this.txType = txType;
	}
	public String getTxPattern() {
		return txPattern;
	}
	public void setTxPattern(String txPattern) {
		this.txPattern = txPattern;
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
	public String getApplyState() {
		return applyState;
	}
	public void setApplyState(String applyState) {
		this.applyState = applyState;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
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
	public String getWorkerBranch() {
		return workerBranch;
	}
	public void setWorkerBranch(String workerBranch) {
		this.workerBranch = workerBranch;
	}
	public TxReviewPriceDetail getTxReviewPriceDetail() {
		return txReviewPriceDetail;
	}
	public void setTxReviewPriceDetail(TxReviewPriceDetail txReviewPriceDetail) {
		this.txReviewPriceDetail = txReviewPriceDetail;
	}
	public List<TxReduceInfoBean> getTxReduceInfoBeans() {
		return txReduceInfoBeans;
	}
	public void setTxReduceInfoBeans(List<TxReduceInfoBean> txReduceInfoBeans) {
		this.txReduceInfoBeans = txReduceInfoBeans;
	}
}
