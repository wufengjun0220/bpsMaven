package com.mingtech.application.pool.common.domain;

import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.pool.common.PoolDictionaryCache;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产基础类
 * @author wbzhaoding
 *
 */

public abstract class Asset{

	private String id;
	private String assetNb;//资产号码
	private BigDecimal assetAmt;//金额
	private String assetType;//类型
	private String floatType;//浮动类型
	private String rickLevel;//风险等级
	private String isEduExist;   //用于判断该票据是否可以产生额度
	private String assetStatus;//状态
	private Date crtTm;//创建时间
	private String plRemark;//备注
	private String at;
	private String riskComment;//风险说明：
	private String blackFlag;//黑名单标志	  BLACKLIST = "01"; // 黑名单   GRAYLIST = "02"; // 灰名单

	private String assetTypeName;//票据类型中文名称
	
	
	/** 申请人信息  **/
	private String assetApplyNm;//申请人名称
	private String assetCommId;//客户号(用作 核心客户号)
	private String assetApplyAcctId;//申请人账号
	private String assetApplyAcctSvcr;//申请人开户行行号
	private String assetApplyAcctSvcrNm;//申请人开户行名称

	/** 费率，总费用 **/
	private BigDecimal chargeRate;   //费率     
	private BigDecimal totalCharge;  //总费用
	
	/** 经办人信息 **/
	private String assetRecSvcr;//业务经办行行号
	private String assetRecSvcrNm;//业务经办行名称
	private String workerName;// 经办人姓名
	private String acctBankNum;    //记账网点
	private String branchId;       // 机构ID

	/** 额度信息 **/
	private BigDecimal assetLimitTotal = new BigDecimal("0");//衍生额度 
	private BigDecimal assetLimitUsed = new BigDecimal("0");//已用额度
	private BigDecimal assetLimitFree = new BigDecimal("0");//可用额度
	private BigDecimal assetLimitFrzd = new BigDecimal("0");//已冻结额度

	private String accNo;//电票签约账号
	private String custNo;//核心客户号		
	private String guaranteeNo;//担保编号
	
	private PedProtocolDto protocol; //用于传值
	
	private String TXFlag;//强贴标志 1为强贴  0为强贴完成
	private String poolAgreement;//票据池编号
	private String lockz;//锁票标记
	private String BtFlag;//商票保贴额度处理标识	1是占用成功 	0释放成功     空：未占用
	private String accptrOrg;//承兑人组织机构代码
	private String poperBeatch;//纸票保管地机构  用于核心纸票出池查询
	private String operatoUser;//纸票入池柜员   用于柜面纸票出池查询
	private String custName;//客户号
	private String socialCode;//社会信用代码
	private String auditStatus;//审批状态
	private String auditStatusDesc;//审批状态
	
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	private String hilrId;//持有id
	private String splitId;//拆分前的大票主键id
	private String tranId;//交易id
	
	/*** 融合改造新增字段  end*/

	public String getCustName() {
		return custName;
	}

	public String getTranId() {
		return tranId;
	}

	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getBeginRangeNo() {
		return beginRangeNo;
	}

	public void setBeginRangeNo(String beginRangeNo) {
		this.beginRangeNo = beginRangeNo;
	}

	public String getEndRangeNo() {
		return endRangeNo;
	}

	public void setEndRangeNo(String endRangeNo) {
		this.endRangeNo = endRangeNo;
	}

	public BigDecimal getStandardAmt() {
		return standardAmt;
	}

	public void setStandardAmt(BigDecimal standardAmt) {
		this.standardAmt = standardAmt;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public String getDraftSource() {
		return draftSource;
	}

	public void setDraftSource(String draftSource) {
		this.draftSource = draftSource;
	}

	public String getSplitFlag() {
		return splitFlag;
	}

	public void setSplitFlag(String splitFlag) {
		this.splitFlag = splitFlag;
	}

	public String getSocialCode() {
		return socialCode;
	}

	public void setSocialCode(String socialCode) {
		this.socialCode = socialCode;
	}

	public String getPoperBeatch() {
		return poperBeatch;
	}

	public void setPoperBeatch(String poperBeatch) {
		this.poperBeatch = poperBeatch;
	}

	public String getOperatoUser() {
		return operatoUser;
	}

	public void setOperatoUser(String operatoUser) {
		this.operatoUser = operatoUser;
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

	public String getLockz() {
		return lockz;
	}

	public void setLockz(String lockz) {
		this.lockz = lockz;
	}
	
	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getPoolAgreement() {
		return poolAgreement;
	}

	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}

	public String getTXFlag() {
		return TXFlag;
	}

	public void setTXFlag(String tXFlag) {
		TXFlag = tXFlag;
	}

	public void setNULL(){
		this.id = null;
	}
	
	public Asset() {
	}

	public Asset(String assetType,String floatType,String rickLevel){
		this.setAssetType(assetType);
		this.setFloatType(floatType);
		this.setRickLevel(rickLevel);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void addUsedLimit(BigDecimal amt){
		assetLimitUsed = assetLimitUsed.add(amt);
		initLimit();
	}
	
	public void subUsedLimit(BigDecimal amt){
		assetLimitUsed = assetLimitUsed.subtract(amt);
		initLimit();
	}
	
	public void frzdLimit(BigDecimal amt){
		assetLimitFrzd = assetLimitFrzd.add(amt);
		initLimit();
	}
	
	public void unfrLimit(BigDecimal amt){
		assetLimitFrzd = assetLimitFrzd.subtract(amt);
		initLimit();
	}

	public void initLimit(){
		assetLimitFree = assetLimitTotal.subtract(assetLimitUsed).subtract(assetLimitFrzd);
	}
	
	/**
	 * 获取可冻结的额度
	 * @return
	 */
	public BigDecimal getUnFzLimit(){
		return this.assetLimitTotal.subtract(assetLimitFrzd);
	}
	
	public String getAssetNb() {
		return assetNb;
	}
	public void setAssetNb(String assetNb) {
		this.assetNb = assetNb;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getIsEduExist() {
		return isEduExist;
	}
	public void setIsEduExist(String isEduExist) {
		this.isEduExist = isEduExist;
	}
	public String getAssetRecSvcr() {
		return assetRecSvcr;
	}
	public void setAssetRecSvcr(String assetRecSvcr) {
		this.assetRecSvcr = assetRecSvcr;
	}
	public String getAssetRecSvcrNm() {
		return assetRecSvcrNm;
	}
	public void setAssetRecSvcrNm(String assetRecSvcrNm) {
		this.assetRecSvcrNm = assetRecSvcrNm;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public String getAssetStatus() {
		return assetStatus;
	}
	public void setAssetStatus(String assetStatus) {
		this.assetStatus = assetStatus;
	}

	public String getAcctBankNum() {
		return acctBankNum;
	}
	public void setAcctBankNum(String acctBankNum) {
		this.acctBankNum = acctBankNum;
	}
	public BigDecimal getAssetAmt() {
		return assetAmt;
	}

	public BigDecimal getChargeRate() {
		return chargeRate;
	}
	public BigDecimal getTotalCharge() {
		return totalCharge;
	}

	public BigDecimal getAssetLimitFree() {
		return assetLimitFree;
	}

	public String getAt() {
		return at;
	}

	public void setAt(String at) {
		this.at = at;
	}

	public String getFloatType() {
		return floatType;
	}

	public void setFloatType(String floatType) {
		this.floatType = floatType;
	}

	public String getRickLevel() {
		return rickLevel;
	}

	public void setRickLevel(String rickLevel) {
		this.rickLevel = rickLevel;
	}


	public BigDecimal getAssetLimitTotal() {
		return assetLimitTotal;
	}


	public BigDecimal getAssetLimitUsed() {
		return assetLimitUsed;
	}


	public BigDecimal getAssetLimitFrzd() {
		return assetLimitFrzd;
	}


	public void setAssetLimitFree(BigDecimal assetLimitFree) {
		this.assetLimitFree = assetLimitFree;
	}


	public Date getCrtTm() {
		return crtTm;
	}


	public void setCrtTm(Date crtTm) {
		this.crtTm = crtTm;
	}


	public String getAssetApplyNm() {
		return assetApplyNm;
	}


	public void setAssetApplyNm(String assetApplyNm) {
		this.assetApplyNm = assetApplyNm;
	}


	public String getAssetCommId() {
		return assetCommId;
	}


	public void setAssetCommId(String assetCommId) {
		this.assetCommId = assetCommId;
	}


	public String getAssetApplyAcctId() {
		return assetApplyAcctId;
	}


	public void setAssetApplyAcctId(String assetApplyAcctId) {
		this.assetApplyAcctId = assetApplyAcctId;
	}


	public String getAssetApplyAcctSvcr() {
		return assetApplyAcctSvcr;
	}


	public void setAssetApplyAcctSvcr(String assetApplyAcctSvcr) {
		this.assetApplyAcctSvcr = assetApplyAcctSvcr;
	}


	public String getAssetApplyAcctSvcrNm() {
		return assetApplyAcctSvcrNm;
	}


	public void setAssetApplyAcctSvcrNm(String assetApplyAcctSvcrNm) {
		this.assetApplyAcctSvcrNm = assetApplyAcctSvcrNm;
	}

	public void setAssetAmt(BigDecimal assetAmt) {
		this.assetAmt = assetAmt;
	}

	public void setChargeRate(BigDecimal chargeRate) {
		this.chargeRate = chargeRate;
	}

	public void setTotalCharge(BigDecimal totalCharge) {
		this.totalCharge = totalCharge;
	}

	public void setAssetLimitTotal(BigDecimal assetLimitTotal) {
		this.assetLimitTotal = assetLimitTotal;
	}

	public void setAssetLimitUsed(BigDecimal assetLimitUsed) {
		this.assetLimitUsed = assetLimitUsed;
	}

	public void setAssetLimitFrzd(BigDecimal assetLimitFrzd) {
		this.assetLimitFrzd = assetLimitFrzd;
	}

	public String getPlRemark() {
		return plRemark;
	}

	public void setPlRemark(String plRemark) {
		this.plRemark = plRemark;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
	public String getAssetTypeName() {
		String typename = "";
		if (this.assetType.equals("AC03")) {
			typename = "三月期存单";
		} else if (this.assetType.equals("AC04")) {
			typename = "六月期存单";
		} else if (this.assetType.equals("AC05")) {
			typename = "一年期存单";
		} else {
			typename = PoolDictionaryCache.getBillType(this.assetType);
		}
		return typename;
	}

	public String getRiskComment() {
		return riskComment;
	}

	public void setRiskComment(String riskComment) {
		this.riskComment = riskComment;
	}

	public String getBlackFlag() {
		return blackFlag;
	}

	public void setBlackFlag(String blackFlag) {
		this.blackFlag = blackFlag;
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

	public PedProtocolDto getProtocol() {
		return protocol;
	}

	public void setProtocol(PedProtocolDto protocol) {
		this.protocol = protocol;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getAuditStatusDesc() {
		return auditStatusDesc = DictionaryCache.getFromPoolDictMap(String.valueOf(auditStatus));
	}

	public void setAuditStatusDesc(String auditStatusDesc) {
		this.auditStatusDesc = auditStatusDesc;
	}

	public String getHilrId() {
		return hilrId;
	}

	public void setHilrId(String hilrId) {
		this.hilrId = hilrId;
	}

	public String getSplitId() {
		return splitId;
	}

	public void setSplitId(String splitId) {
		this.splitId = splitId;
	}
	
	
}