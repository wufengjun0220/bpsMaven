/**
 * 
 */
package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.base.domain.PoolCollect;
import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
 * 托收明细（票据池）
 * @author wbyecheng
 * 
 */
public class DraftPoolCollect extends PoolCollect {
	
	/** 票据基本信息 **/
	private String SBillNo; // 票号
	private String SBillType; // 票据类型
	private String SBillMedia; // 票据介质
	private Date DIssueDt; // 出票日
	private Date DDueDt; // 到期日
	private BigDecimal FBillAmount; // 票面金额
	private PoolBillInfo poolBillInfo; //大票表对象

	/** 承兑人信息 **/
	private String SAcceptor; // 承兑方名称
	private String SAcceptorBankName;// 承兑方行名称
	private String SAcceptorBankCode;// 承兑方行行号
	private String acceptorOrgCode;/* 承兑方组织机构代码 */
	
	/** 出票人信息 **/
	private String SIssuerBankName;// 出票人开户行名称(直接从大票取值)

	/** 持票人信息 **/
	private String SOwnerName; // 持票方全称(提示付款人)
	private String SOwnerBankCode; // 持票方行号
	private String SOwnerBankName; // 持票方开户行名
	private String SOwnerAccount; // 持票方账号
	
	/** 托收信息 **/
	private DraftPoolCollectBatch collectBatch; // 批次id
	private String SOperatorId; // 操作人id
	private Date DCollectDt; // 托收日期
	private String SBillFrom; // 票据来源
	private String SPaypromOrgCode; // 提示付款人组织机构代码
	
	/**
	 * 弃用
	private Integer WorkflowCaseId;// 工作流id
	private String SBillStatus; // 票据状态
	private Integer workItemId;// 工作项ID
	private Date startDate; // 委托收款记账页面到期开始日期 没有映射到数据库
	private Date endDate; // 委托收款记账页面到期截止日期 没有映射到数据库
	private String billinfoId;// 票据id
	private String SAcceptorBankPhone;// 承兑行电话号码
	 */

	/** 展示用 **/
	private String SBillStatusName;// 票据状态名
	private String SBillTypeName;// 票据类型名
	private String SBillMediaName;// 票据介质名称

	
	/**待确认**/
	
	/** 网银接口使用属性 */
	private String overdueFlag;// 逾期标识
	private String overdueFlagName;// 逾期标识名称
	private String overdueCause;// 逾期原因
	private String ifSysInner;// 是否系统内
	private String aterAcct;// 承兑人账号
	private BigDecimal clewAmount;// 提示金额
	private Date aplyDate;// 提示付款申请日期

	private Date rvetDt;// 提示付款回复日期
	private String rvetMk;// 提示付款回复标记
	private String ifPrxyCstmRvet;// 是否代客户回复
	private String rfseCode;// 拒付理由代码
	private String rfseRmakInfo;// 拒付备注信息

	private String pyerKind;// 提示付款人类别

	private String ElctrncSgntr;// 电子签名

	private String ElctrncSgntrFlag;// 电子签章标示

	private String SMsgStatus;// 报文状态
	private String SMsgStatusName;// 报文状态中文名
	private String SMsgCode;// 报文错误码

	// private String clewPymtRmak;//提示付款备注
	private String pyerRemk;// 提示付款人备注
	private String signRmak;// 签收备注

	private String SAgcySvcrName;// 承接行名称
	private String SAgcySvcr;// 承接行行号



	private String collectOperSts;// 托收批次状态
	private String collectOperStsName;// 托收批次状态

	private Integer printCount;// 打印次数

	private String printFlag;// 是否已打印

	private String coreBatchNo;// 核心批次号
	private String collectAddr;// 托收地址--承兑行地址
	private String collectPostcode;// 托收邮编--承兑行邮编

	private String sequenceId;// 行内序号

	private String tuoshouShouhuiStautus;// 托收收回记账状态
	private String tuoshouShouhuiStautusName;// 托收收回记账状态


	/** 以下字段用于导出excel使用 */
	private String phone;// 电话/电挂
	private String categoryCode;// 行别代码
	private String discountInName;// 贴入方名称1 即贴现支行
	private String discountInAccount;// 贴入人账号 即贴现帐号
	private Date discBatchDate;// 贴现申请日 纸票：贴现日
	private BigDecimal FRate;// 利率
	private Long FIntDays;// 计息天数
	private BigDecimal FInt;// 应付利息
	private BigDecimal FBuyPayment;// 贴现买入方实付金额
	private String discountOutName;// 贴出方名称1 即贴现申请人

	private String totalMoneyShow;
	private String totalSizeShow;

	private String endrCounts;// 纸票登记用-背书次数
	private String lastOwner;// 纸票登记用-最后一手持票人
	private String originServiceSn; // 记账流水号
	private String originServiceTime; // 记账日期
	private String applyTlrNo;
	private String accountTlrNo;
	private String coreSvrSeqNo;// 核心流水
	private String printTime;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getDiscountInName() {
		return discountInName;
	}

	public void setDiscountInName(String discountInName) {
		this.discountInName = discountInName;
	}

	public String getDiscountInAccount() {
		return discountInAccount;
	}

	public void setDiscountInAccount(String discountInAccount) {
		this.discountInAccount = discountInAccount;
	}

	public Date getDiscBatchDate() {
		return discBatchDate;
	}

	public void setDiscBatchDate(Date discBatchDate) {
		this.discBatchDate = discBatchDate;
	}

	public BigDecimal getFRate() {
		return FRate;
	}

	public void setFRate(BigDecimal rate) {
		FRate = rate;
	}

	public Long getFIntDays() {
		return FIntDays;
	}

	public void setFIntDays(Long intDays) {
		FIntDays = intDays;
	}

	public BigDecimal getFInt() {
		return FInt;
	}

	public void setFInt(BigDecimal int1) {
		FInt = int1;
	}

	public BigDecimal getFBuyPayment() {
		return FBuyPayment;
	}

	public void setFBuyPayment(BigDecimal buyPayment) {
		FBuyPayment = buyPayment;
	}

	public String getDiscountOutName() {
		return discountOutName;
	}

	public void setDiscountOutName(String discountOutName) {
		this.discountOutName = discountOutName;
	}

	/** default constructor */
	public DraftPoolCollect() {
	}

	/** full constructor */
	public DraftPoolCollect(PoolBillInfo poolBillInfo, String SBranchId,
			String SOperatorId, String SBillNo, String SBillType,
			Date DIssueDt, Date DDueDt, String SAcceptor,
			String SAcceptorBankName, String SAcceptorBankCode,
			String SOwnerName, String SOwnerBankCode, String SOwnerAccount,
			BigDecimal FBillAmount, Date DCollectDt,
			String SBillMedia, String SBillFrom, String SPaypromOrgCode) {
		this.poolBillInfo = poolBillInfo;
		super.setSBranchId(SBranchId);
		this.SOperatorId = SOperatorId;
		this.SBillNo = SBillNo;
		this.SBillType = SBillType;
		this.DIssueDt = DIssueDt;
		this.DDueDt = DDueDt;
		this.SAcceptor = SAcceptor;
		this.SAcceptorBankName = SAcceptorBankName;
		this.SAcceptorBankCode = SAcceptorBankCode;
		this.SOwnerName = SOwnerName;
		this.SOwnerBankCode = SOwnerBankCode;
		this.SOwnerAccount = SOwnerAccount;
		this.FBillAmount = FBillAmount;
		this.DCollectDt = DCollectDt;
		this.SBillMedia = SBillMedia;
		this.SBillFrom = SBillFrom;
		this.SPaypromOrgCode = SPaypromOrgCode;
	}

	// Property accessors

	public String getSBillMediaName() {
		return this.SBillMediaName = (String) PoolDictionaryCache.billMediaMap
				.get(this.getSBillMedia());
	}

	public void setSBillMediaName(String billMediaName) {
		SBillMediaName = billMediaName;
	}

//	public String getId() {
//		return this.id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

	public PoolBillInfo getPoolBillInfo() {
		return this.poolBillInfo;
	}

	public void setPoolBillInfo(PoolBillInfo poolBillInfo) {
		this.poolBillInfo = poolBillInfo;
	}

//	public String getSBranchId() {
//		return this.SBranchId;
//	}
//
//	public void setSBranchId(String SBranchId) {
//		this.SBranchId = SBranchId;
//	}

	public String getSOperatorId() {
		return this.SOperatorId;
	}

	public void setSOperatorId(String SOperatorId) {
		this.SOperatorId = SOperatorId;
	}

	public String getSBillNo() {
		return this.SBillNo;
	}

	public void setSBillNo(String SBillNo) {
		this.SBillNo = SBillNo;
	}

	public String getSBillType() {
		return this.SBillType;
	}

	public void setSBillType(String SBillType) {
		this.SBillType = SBillType;
	}

	public Date getDIssueDt() {
		return this.DIssueDt;
	}

	public void setDIssueDt(Date DIssueDt) {
		this.DIssueDt = DIssueDt;
	}

	public Date getDDueDt() {
		return this.DDueDt;
	}

	public void setDDueDt(Date DDueDt) {
		this.DDueDt = DDueDt;
	}

	public String getSAcceptor() {
		return this.SAcceptor;
	}

	public void setSAcceptor(String SAcceptor) {
		this.SAcceptor = SAcceptor;
	}

	public String getSAcceptorBankName() {
		return this.SAcceptorBankName;
	}

	public void setSAcceptorBankName(String SAcceptorBankName) {
		this.SAcceptorBankName = SAcceptorBankName;
	}

	public String getSAcceptorBankCode() {
		return this.SAcceptorBankCode;
	}

	public void setSAcceptorBankCode(String SAcceptorBankCode) {
		this.SAcceptorBankCode = SAcceptorBankCode;
	}

	public String getSOwnerName() {
		return this.SOwnerName;
	}

	public void setSOwnerName(String SOwnerName) {
		this.SOwnerName = SOwnerName;
	}

	public String getSOwnerBankCode() {
		return this.SOwnerBankCode;
	}

	public void setSOwnerBankCode(String SOwnerBankCode) {
		this.SOwnerBankCode = SOwnerBankCode;
	}

	public String getSOwnerAccount() {
		return this.SOwnerAccount;
	}

	public void setSOwnerAccount(String SOwnerAccount) {
		this.SOwnerAccount = SOwnerAccount;
	}

	public Date getDCollectDt() {
		return this.DCollectDt;
	}

	public void setDCollectDt(Date DCollectDt) {
		this.DCollectDt = DCollectDt;
	}

	public String getSBillMedia() {
		return this.SBillMedia;
	}

	public void setSBillMedia(String SBillMedia) {
		this.SBillMedia = SBillMedia;
	}

	public String getSBillFrom() {
		return this.SBillFrom;
	}

	public void setSBillFrom(String SBillFrom) {
		this.SBillFrom = SBillFrom;
	}

	public String getSPaypromOrgCode() {
		return this.SPaypromOrgCode;
	}

	public void setSPaypromOrgCode(String SPaypromOrgCode) {
		this.SPaypromOrgCode = SPaypromOrgCode;
	}

//	public String getSBillStatus() {
//		return SBillStatus;
//	}
//
//	public void setSBillStatus(String billStatus) {
//		SBillStatus = billStatus;
//	}

	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}

	public void setFBillAmount(BigDecimal billAmount) {
		FBillAmount = billAmount;
	}

	/* 票据状态名 */
	public String getSBillStatusName() {
		return SBillStatusName = PoolDictionaryCache
				.getStatusName(this.getPlStatus());
	}

	public void setSBillStatusName(String billStatusName) {
		SBillStatusName = billStatusName;
	}

	/* 票据类型名 */
	public String getSBillTypeName() {
		return this.SBillTypeName = PoolDictionaryCache.billTypeMap.get(
				this.getSBillType()).toString();
	}

	public void setSBillTypeName(String billTypeName) {
		this.SBillTypeName = billTypeName;
	}

//	/* 票据信息id */
//	public String getBillinfoId() {
//		return this.billinfoId = poolBillInfo.getBillinfoId();
//	}
//
//	public void setBillinfoId(String billinfoId) {
//		this.billinfoId = billinfoId;
//	}

//	public Integer getWorkflowCaseId() {
//		return WorkflowCaseId;
//	}
//
//	public void setWorkflowCaseId(Integer workflowCaseId) {
//		WorkflowCaseId = workflowCaseId;
//	}

	public String getOverdueFlag() {
		return overdueFlag;
	}

	public void setOverdueFlag(String overdueFlag) {
		this.overdueFlag = overdueFlag;
	}

	public String getOverdueCause() {
		return overdueCause;
	}

	public void setOverdueCause(String overdueCause) {
		this.overdueCause = overdueCause;
	}

	public String getIfSysInner() {
		return ifSysInner;
	}

	public void setIfSysInner(String ifSysInner) {
		this.ifSysInner = ifSysInner;
	}

	public String getPyerRemk() {
		return pyerRemk;
	}

	public void setPyerRemk(String pyerRemk) {
		this.pyerRemk = pyerRemk;
	}

	public String getPyerKind() {
		return pyerKind;
	}

	public void setPyerKind(String pyerKind) {
		this.pyerKind = pyerKind;
	}

	public String getAterAcct() {
		return aterAcct;
	}

	public void setAterAcct(String aterAcct) {
		this.aterAcct = aterAcct;
	}

	public BigDecimal getClewAmount() {
		return clewAmount;
	}

	public void setClewAmount(BigDecimal clewAmount) {
		this.clewAmount = clewAmount;
	}

	public Date getAplyDate() {
		return aplyDate;
	}

	public void setAplyDate(Date aplyDate) {
		this.aplyDate = aplyDate;
	}

	public String getElctrncSgntr() {
		return ElctrncSgntr;
	}

	public void setElctrncSgntr(String elctrncSgntr) {
		ElctrncSgntr = elctrncSgntr;
	}

	public String getSMsgStatus() {
		return SMsgStatus;
	}

	public void setSMsgStatus(String msgStatus) {
		SMsgStatus = msgStatus;
	}

	public String getSMsgCode() {
		return SMsgCode;
	}

	public void setSMsgCode(String msgCode) {
		SMsgCode = msgCode;
	}

	public Date getRvetDt() {
		return rvetDt;
	}

	public void setRvetDt(Date rvetDt) {
		this.rvetDt = rvetDt;
	}

	public String getRvetMk() {
		return rvetMk;
	}

	public void setRvetMk(String rvetMk) {
		this.rvetMk = rvetMk;
	}

	public String getIfPrxyCstmRvet() {
		return ifPrxyCstmRvet;
	}

	public void setIfPrxyCstmRvet(String ifPrxyCstmRvet) {
		this.ifPrxyCstmRvet = ifPrxyCstmRvet;
	}

	public String getRfseCode() {
		return rfseCode;
	}

	public void setRfseCode(String rfseCode) {
		this.rfseCode = rfseCode;
	}

	public String getRfseRmakInfo() {
		return rfseRmakInfo;
	}

	public void setRfseRmakInfo(String rfseRmakInfo) {
		this.rfseRmakInfo = rfseRmakInfo;
	}

//	public Integer getWorkItemId() {
//		return workItemId;
//	}
//
//	public void setWorkItemId(Integer workItemId) {
//		this.workItemId = workItemId;
//	}

	public String getAcceptorOrgCode() {
		return acceptorOrgCode;
	}

	public void setAcceptorOrgCode(String acceptorOrgCode) {
		this.acceptorOrgCode = acceptorOrgCode;
	}

	public String getSignRmak() {
		return signRmak;
	}

	public void setSignRmak(String signRmak) {
		this.signRmak = signRmak;
	}

	public String getSMsgStatusName() {
		return SMsgStatusName = PoolDictionaryCache.getStatusName(this
				.getSMsgStatus());
	}

	public void setSMsgStatusName(String msgStatusName) {
		SMsgStatusName = msgStatusName;
	}

	public String getSAgcySvcrName() {
		return SAgcySvcrName;
	}

	public void setSAgcySvcrName(String agcySvcrName) {
		SAgcySvcrName = agcySvcrName;
	}

	public String getSAgcySvcr() {
		return SAgcySvcr;
	}

	public void setSAgcySvcr(String agcySvcr) {
		SAgcySvcr = agcySvcr;
	}

	public String getSOwnerBankName() {
		return SOwnerBankName;
	}

	public void setSOwnerBankName(String ownerBankName) {
		SOwnerBankName = ownerBankName;
	}

	public String getElctrncSgntrFlag() {
		return ElctrncSgntrFlag;
	}

	public void setElctrncSgntrFlag(String elctrncSgntrFlag) {
		ElctrncSgntrFlag = elctrncSgntrFlag;
	}

	public String getOverdueFlagName() {
		if (this.overdueFlag.equals("02")) {
			overdueFlagName = "已逾期";
		} else {
			overdueFlagName = "未逾期";
		}
		return overdueFlagName;
	}

	public DraftPoolCollectBatch getCollectBatch() {
		return collectBatch;
	}

	public void setCollectBatch(DraftPoolCollectBatch collectBatch) {
		this.collectBatch = collectBatch;
	}

	public String getSIssuerBankName() {
		return poolBillInfo.getSIssuerBankName();
	}

	public String getCollectOperSts() {
		return collectBatch.getCollectOperSts();
	}

	public String getCollectOperStsName() {
		return collectOperStsName = (String) PoolDictionaryCache.dictionaryMap
				.get(collectBatch.getCollectOperSts());
	}

	public String getPrintFlag() {
		return printFlag;
	}

	public void setPrintFlag(String printFlag) {
		this.printFlag = printFlag;
	}

	public Integer getPrintCount() {
		return printCount;
	}

	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}

	public String getCoreBatchNo() {
		return coreBatchNo;
	}

	public void setCoreBatchNo(String coreBatchNo) {
		this.coreBatchNo = coreBatchNo;
	}

	public String getCollectAddr() {
		if (null != this.collectBatch) {
			return this.collectBatch.getCollectAddr();
		}
		return collectAddr;
	}

	public void setCollectAddr(String collectAddr) {
		this.collectAddr = collectAddr;
	}

	public String getCollectPostcode() {
		if (null != this.collectBatch) {
			return this.collectBatch.getCollectPostcode();
		}
		return this.collectPostcode;
	}

	public void setCollectPostcode(String collectPostcode) {
		this.collectPostcode = collectPostcode;
	}

	public String getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getTuoshouShouhuiStautus() {
		return tuoshouShouhuiStautus;
	}

	public void setTuoshouShouhuiStautus(String tuoshouShouhuiStautus) {
		this.tuoshouShouhuiStautus = tuoshouShouhuiStautus;
	}

	public String getTuoshouShouhuiStautusName() {
		return (String) PoolDictionaryCache.dictionaryMap
				.get(tuoshouShouhuiStautus);
	}

	public void setTuoshouShouhuiStautusName(String tuoshouShouhuiStautusName) {
		this.tuoshouShouhuiStautusName = tuoshouShouhuiStautusName;
	}

//	public String getSAcceptorBankPhone() {
//		return SAcceptorBankPhone;
//	}
//
//	public void setSAcceptorBankPhone(String acceptorBankPhone) {
//		SAcceptorBankPhone = acceptorBankPhone;
//	}

//	public Date getStartDate() {
//		return startDate;
//	}
//
//	public void setStartDate(Date startDate) {
//		this.startDate = startDate;
//	}
//
//	public Date getEndDate() {
//		return endDate;
//	}
//
//	public void setEndDate(Date endDate) {
//		this.endDate = endDate;
//	}

	public String getTotalMoneyShow() {
		return totalMoneyShow;
	}

	public void setTotalMoneyShow(String totalMoneyShow) {
		this.totalMoneyShow = totalMoneyShow;
	}

	public String getTotalSizeShow() {
		return totalSizeShow;
	}

	public void setTotalSizeShow(String totalSizeShow) {
		this.totalSizeShow = totalSizeShow;
	}

	public String getEndrCounts() {
		return endrCounts;
	}

	public void setEndrCounts(String endrCounts) {
		this.endrCounts = endrCounts;
	}

	public String getLastOwner() {
		return lastOwner;
	}

	public void setLastOwner(String lastOwner) {
		this.lastOwner = lastOwner;
	}

	public String getOriginServiceSn() {
		return originServiceSn;
	}

	public void setOriginServiceSn(String originServiceSn) {
		this.originServiceSn = originServiceSn;
	}

	public String getOriginServiceTime() {
		return originServiceTime;
	}

	public void setOriginServiceTime(String originServiceTime) {
		this.originServiceTime = originServiceTime;
	}

	public String getApplyTlrNo() {
		return applyTlrNo;
	}

	public void setApplyTlrNo(String applyTlrNo) {
		this.applyTlrNo = applyTlrNo;
	}

	public String getAccountTlrNo() {
		return accountTlrNo;
	}

	public void setAccountTlrNo(String accountTlrNo) {
		this.accountTlrNo = accountTlrNo;
	}

	public String getCoreSvrSeqNo() {
		return coreSvrSeqNo;
	}

	public void setCoreSvrSeqNo(String coreSvrSeqNo) {
		this.coreSvrSeqNo = coreSvrSeqNo;
	}

	public String getPrintTime() {
		return printTime;
	}

	public void setPrintTime(String printTime) {
		this.printTime = printTime;
	}

}
