package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TxProtocolDetailBean {
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String protocolNo;				//	协议编号
	private String protocolType;			//	在线协议类型
	private String protocolStatus;			//	状态
	
	
	private String discountBankNo;			//	贴入行行号
	private String telphone;				//	手机
	private String landPhone;				//	座机
	private String discountBankName;		//	贴入行名称
	
	private String signBranchName;			//	签约机构
	
	private String protocolOpenControl;		//	在线协议开关
	
	/**
	 * 协议历史变更返回字段
	 * **/
	private String creditLineNo;			//	基本授信额度编号
	private String hisCreditLineNo;			//	历史基本授信额度编号
	private String misCustNo;				//	mis客户号/信贷客户编号
	private String hisMisCustNo;			//	mis客户号/信贷客户编号
	private String custNo;					//	核心客户号
	private String hisCustNo;				//	核心客户号
//	private String cmsClientNoList;			//  历史网银客户集合	
//	private String hisCmsClientNoList;		//  历史网银客户集合	
	private String custName;				//	客户名称
	private String hisCustName;				//	历史客户名称
	private BigDecimal limitAmt;			//	在线贴现总额
	private BigDecimal hisLimitAmt;			//	历史在线贴现总额
	private BigDecimal usedAmt;				//	已用额度
	private BigDecimal hisUsedAmt;			//	历史已用额度
	private BigDecimal availableAmt;		//	可用额度
	private BigDecimal hisAvailableAmt;		//	历史可用额度
	private String protocolDueDate;			//	协议到期日
	private String hisProtocolDueDate;		//	历史协议到期日
	private String isEx; 					//	是否前手
	private String hisIsEx; 				//	历史是否前手
	private String accountEntryNo;			//	入账账号、放款账号
	private String hisAccountEntryNo;		//	历史入账账号、放款账号
	private String accountEntryName;		//	入账账户名称、放款账号名称
	private String hisAccountEntryName;		//	历史入账账户名称、放款账号名称
	private String accountEntryDevNo;		//	入账机构所号
	private String hisAccountEntryDevNo;	//	历史入账机构所号
	private String accountEntryDevName;		//	入账机构名称
	private String hisAccountEntryDevName;	//	历史入账机构名称
	private String entryBankNo;				//	贴入行号
	private String hisEntryBankNo;			//	历史贴入行号
	private String entryBankName;			//	贴入行名
	private String hisEntryBankName;		//	历史贴入行名
	private String rateFloatType;			//	利率浮动方式	01-基准一致 02-逐项调整 03-固定利率 04-实点浮动-
	private String hisRateFloatType;		//	历史利率浮动方式	01-基准一致 02-逐项调整 03-固定利率 04-实点浮动-
	private String rateFloatNum;			//	利率浮动值
	private String hisRateFloatNum;			//	历史利率浮动值
	private String contractOrgNo;			//	签约机构所号、经办
	private String hisContractOrgNo;		//	历史签约机构所号
	private String contractOrgName;			//	签约机构名称
	private String HisContractOrgName;		//	历史签约机构名称
	private String handler;					//	经办人
	private String hisHandler;				//	历史经办人
	private String handlerNo;				//	经办人工号
	private String hisHandlerNo;			//	历史经办人工号
	private String protocolOpenDate;			//	协议开通日
	private String hisProtocolOpenDate;		//	历史协议开通日
	private String protocolChangeDate;		//	协议变更日期
	
	private List<TxContactDetailBean> txContactDetailBeans;	//	短信通知信息
	private List<TxExDetailBean> exDetailBeans;				//	前手信息
	
	private List<TxRateAdjustInfo> txRateAdjustInfos;
	
	List<TxRateDetailBeanPO> txRateDetailBeanPOs = new ArrayList<TxRateDetailBeanPO>();
	
	public List<TxRateDetailBeanPO> getTxRateDetailBeanPOs() {
		return txRateDetailBeanPOs;
	}
	public void setTxRateDetailBeanPOs(List<TxRateDetailBeanPO> txRateDetailBeanPOs) {
		this.txRateDetailBeanPOs = txRateDetailBeanPOs;
	}
	private String updateCotent;			//	修改内容
	private String updateDate;				//	修改日期
	

	
	public String getProtocolOpenDate() {
		return protocolOpenDate;
	}
	public void setProtocolOpenDate(String protocolOpenDate) {
		this.protocolOpenDate = protocolOpenDate;
	}
	public String getHisProtocolOpenDate() {
		return hisProtocolOpenDate;
	}
	public void setHisProtocolOpenDate(String hisProtocolOpenDate) {
		this.hisProtocolOpenDate = hisProtocolOpenDate;
	}
	public String getHisCreditLineNo() {
		return hisCreditLineNo;
	}
	public void setHisCreditLineNo(String hisCreditLineNo) {
		this.hisCreditLineNo = hisCreditLineNo;
	}
	public String getHisMisCustNo() {
		return hisMisCustNo;
	}
	public void setHisMisCustNo(String hisMisCustNo) {
		this.hisMisCustNo = hisMisCustNo;
	}
	public String getHisCustNo() {
		return hisCustNo;
	}
	public void setHisCustNo(String hisCustNo) {
		this.hisCustNo = hisCustNo;
	}
	public String getHisCustName() {
		return hisCustName;
	}
	public void setHisCustName(String hisCustName) {
		this.hisCustName = hisCustName;
	}
	public BigDecimal getHisLimitAmt() {
		return hisLimitAmt;
	}
	public void setHisLimitAmt(BigDecimal hisLimitAmt) {
		this.hisLimitAmt = hisLimitAmt;
	}
	public BigDecimal getHisUsedAmt() {
		return hisUsedAmt;
	}
	public void setHisUsedAmt(BigDecimal hisUsedAmt) {
		this.hisUsedAmt = hisUsedAmt;
	}
	public BigDecimal getHisAvailableAmt() {
		return hisAvailableAmt;
	}
	public void setHisAvailableAmt(BigDecimal hisAvailableAmt) {
		this.hisAvailableAmt = hisAvailableAmt;
	}
	public String getHisProtocolDueDate() {
		return hisProtocolDueDate;
	}
	public void setHisProtocolDueDate(String hisProtocolDueDate) {
		this.hisProtocolDueDate = hisProtocolDueDate;
	}
	public String getHisIsEx() {
		return hisIsEx;
	}
	public void setHisIsEx(String hisIsEx) {
		this.hisIsEx = hisIsEx;
	}
	public String getAccountEntryNo() {
		return accountEntryNo;
	}
	public void setAccountEntryNo(String accountEntryNo) {
		this.accountEntryNo = accountEntryNo;
	}
	public String getHisAccountEntryNo() {
		return hisAccountEntryNo;
	}
	public void setHisAccountEntryNo(String hisAccountEntryNo) {
		this.hisAccountEntryNo = hisAccountEntryNo;
	}
	public String getAccountEntryName() {
		return accountEntryName;
	}
	public void setAccountEntryName(String accountEntryName) {
		this.accountEntryName = accountEntryName;
	}
	public String getHisAccountEntryName() {
		return hisAccountEntryName;
	}
	public void setHisAccountEntryName(String hisAccountEntryName) {
		this.hisAccountEntryName = hisAccountEntryName;
	}
	public String getHisAccountEntryDevNo() {
		return hisAccountEntryDevNo;
	}
	public void setHisAccountEntryDevNo(String hisAccountEntryDevNo) {
		this.hisAccountEntryDevNo = hisAccountEntryDevNo;
	}
	public String getHisAccountEntryDevName() {
		return hisAccountEntryDevName;
	}
	public void setHisAccountEntryDevName(String hisAccountEntryDevName) {
		this.hisAccountEntryDevName = hisAccountEntryDevName;
	}
	public String getHisEntryBankNo() {
		return hisEntryBankNo;
	}
	public void setHisEntryBankNo(String hisEntryBankNo) {
		this.hisEntryBankNo = hisEntryBankNo;
	}
	public String getHisEntryBankName() {
		return hisEntryBankName;
	}
	public void setHisEntryBankName(String hisEntryBankName) {
		this.hisEntryBankName = hisEntryBankName;
	}
	public String getHisRateFloatType() {
		return hisRateFloatType;
	}
	public void setHisRateFloatType(String hisRateFloatType) {
		this.hisRateFloatType = hisRateFloatType;
	}
	public String getHisRateFloatNum() {
		return hisRateFloatNum;
	}
	public void setHisRateFloatNum(String hisRateFloatNum) {
		this.hisRateFloatNum = hisRateFloatNum;
	}
	public String getHisContractOrgNo() {
		return hisContractOrgNo;
	}
	public void setHisContractOrgNo(String hisContractOrgNo) {
		this.hisContractOrgNo = hisContractOrgNo;
	}
	public String getHisContractOrgName() {
		return HisContractOrgName;
	}
	public void setHisContractOrgName(String hisContractOrgName) {
		HisContractOrgName = hisContractOrgName;
	}
	public String getHisHandler() {
		return hisHandler;
	}
	public void setHisHandler(String hisHandler) {
		this.hisHandler = hisHandler;
	}
	public String getHisHandlerNo() {
		return hisHandlerNo;
	}
	public void setHisHandlerNo(String hisHandlerNo) {
		this.hisHandlerNo = hisHandlerNo;
	}
	
	public String getMisCustNo() {
		return misCustNo;
	}
	public void setMisCustNo(String misCustNo) {
		this.misCustNo = misCustNo;
	}
	public String getTelphone() {
		return telphone;
	}
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	public String getLandPhone() {
		return landPhone;
	}
	public void setLandPhone(String landPhone) {
		this.landPhone = landPhone;
	}
	
	public String getRateFloatNum() {
		return rateFloatNum;
	}
	public void setRateFloatNum(String rateFloatNum) {
		this.rateFloatNum = rateFloatNum;
	}
	
	public String getEntryBankNo() {
		return entryBankNo;
	}
	public void setEntryBankNo(String entryBankNo) {
		this.entryBankNo = entryBankNo;
	}
	public String getEntryBankName() {
		return entryBankName;
	}
	public void setEntryBankName(String entryBankName) {
		this.entryBankName = entryBankName;
	}
	public List<TxRateAdjustInfo> getTxRateAdjustInfos() {
		return txRateAdjustInfos;
	}
	public void setTxRateAdjustInfos(List<TxRateAdjustInfo> txRateAdjustInfos) {
		this.txRateAdjustInfos = txRateAdjustInfos;
	}

	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public BigDecimal getLimitAmt() {
		return limitAmt;
	}
	public void setLimitAmt(BigDecimal limitAmt) {
		this.limitAmt = limitAmt;
	}
	public String getUpdateCotent() {
		return updateCotent;
	}
	public void setUpdateCotent(String updateCotent) {
		this.updateCotent = updateCotent;
	}
	
	public List<TxRateAdjustInfo> getIntroBillInfoBeans() {
		return txRateAdjustInfos;
	}
	public void setIntroBillInfoBeans(List<TxRateAdjustInfo> txRateAdjustInfos) {
		this.txRateAdjustInfos = txRateAdjustInfos;
	}
	public List<TxExDetailBean> getExDetailBeans() {
		return exDetailBeans;
	}
	public void setExDetailBeans(List<TxExDetailBean> exDetailBeans) {
		this.exDetailBeans = exDetailBeans;
	}
	public List<TxContactDetailBean> getTxContactDetailBeans() {
		return txContactDetailBeans;
	}
	public void setTxContactDetailBeans(
			List<TxContactDetailBean> txContactDetailBeans) {
		this.txContactDetailBeans = txContactDetailBeans;
	}
	public String getProtocolNo() {
		return protocolNo;
	}
	public void setProtocolNo(String protocolNo) {
		this.protocolNo = protocolNo;
	}
	public String getProtocolType() {
		return protocolType;
	}
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
	public String getProtocolStatus() {
		return protocolStatus;
	}
	public void setProtocolStatus(String protocolStatus) {
		this.protocolStatus = protocolStatus;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public BigDecimal getTotalAmt() {
		return limitAmt;
	}
	public void setTotalAmt(BigDecimal limitAmt) {
		this.limitAmt = limitAmt;
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
	public String getDiscountBankNo() {
		return discountBankNo;
	}
	public void setDiscountBankNo(String discountBankNo) {
		this.discountBankNo = discountBankNo;
	}
	public String getDiscountBankName() {
		return discountBankName;
	}
	public void setDiscountBankName(String discountBankName) {
		this.discountBankName = discountBankName;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public String getHandlerNo() {
		return handlerNo;
	}
	public void setHandlerNo(String handlerNo) {
		this.handlerNo = handlerNo;
	}
	public String getSignBranchName() {
		return signBranchName;
	}
	public void setSignBranchName(String signBranchName) {
		this.signBranchName = signBranchName;
	}
	public String getProtocolDueDate() {
		return protocolDueDate;
	}
	public void setProtocolDueDate(String protocolDueDate) {
		this.protocolDueDate = protocolDueDate;
	}
	public String getProtocolChangeDate() {
		return protocolChangeDate;
	}
	public void setProtocolChangeDate(String protocolChangeDate) {
		this.protocolChangeDate = protocolChangeDate;
	}
	public String getProtocolOpenControl() {
		return protocolOpenControl;
	}
	public void setProtocolOpenControl(String protocolOpenControl) {
		this.protocolOpenControl = protocolOpenControl;
	}
	public String getCreditLineNo() {
		return creditLineNo;
	}
	public void setCreditLineNo(String creditLineNo) {
		this.creditLineNo = creditLineNo;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getRateFloatType() {
		return rateFloatType;
	}
	public void setRateFloatType(String rateFloatType) {
		this.rateFloatType = rateFloatType;
	}
	public String getAccountEntryDevNo() {
		return accountEntryDevNo;
	}
	public void setAccountEntryDevNo(String accountEntryDevNo) {
		this.accountEntryDevNo = accountEntryDevNo;
	}
	public String getAccountEntryDevName() {
		return accountEntryDevName;
	}
	public void setAccountEntryDevName(String accountEntryDevName) {
		this.accountEntryDevName = accountEntryDevName;
	}
	public String getContractOrgNo() {
		return contractOrgNo;
	}
	public void setContractOrgNo(String contractOrgNo) {
		this.contractOrgNo = contractOrgNo;
	}
	public String getContractOrgName() {
		return contractOrgName;
	}
	public void setContractOrgName(String contractOrgName) {
		this.contractOrgName = contractOrgName;
	}
	public String getIsEx() {
		return isEx;
	}
	public void setIsEx(String isEx) {
		this.isEx = isEx;
	}
	
	
	public TxProtocolDetailBean() {
		super();
	}
	
}
