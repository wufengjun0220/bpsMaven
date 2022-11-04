package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.PoolDictionaryCache;
import com.mingtech.application.pool.common.domain.Asset;

public class DraftPool extends Asset {

	/** 基本信息 **/
	private String plDraftMedia;// 票据介质
	private String plIsseAmtValue;// 币种
	private Date plIsseDt;// 出票日
	private Date plDueDt;// 到期日
	private Date plPaymentTm;// 预计回款日

	/** 出票人信息 **/
	private String plDrwrNm;// 出票人名称
	private String plDrwrAcctId;// 出票人账号
	private String plDrwrAcctName;// 出票人账号名称
	private String plDrwrAcctSvcr;// 出票人开户行行号
	private String plDrwrAcctSvcrNm;// 出票人开户行名称
	
	/** 收款人信息 **/
	private String plPyeeNm;// 收款人名称
	private String plPyeeAcctId;// 收款人账号
	private String plPyeeAcctName;// 收款人账号名称
	private String plPyeeAcctSvcr;// 收款人开户行行号
	private String plPyeeAcctSvcrNm;// 收款人开户行名称
	
	/** 承兑人信息 **/
	private String plAccptrNm;// 承兑人名称
	private String plAccptrId;// 承兑人账号
	private String plAccptrAcctName;// 承兑人账号名称
	private String plAccptrSvcr;// 承兑人开户行行号
	private String plAccptrSvcrNm;// 承兑人开户行名称
	
	/** 池业务信息 **/
	private Date plTm;// 入池日期
	private BigDecimal eduDiscount;// 额度打折率
	private String plCollateralizationId;// 质押业务ID
	private String plSameCity;// 同城异地标识
	private String lastTransType;// 最后一次业务类型
	private String lastTransId;// 最后一次业务id，关联业务表di
	

	/** 发托业务信息 **/
	private PoolBillInfo poolBillInfo;// 票据信息对象
	private String productId;// 产品id


	/** 待确认 **/
	// 2010-9-19添加，与业务表保持一致
	private String plAccptrAddress; // 承兑人地址
	private String plAccptrProto;// 承兑协议号
	private Date plAccptrDt;// 承兑日
	private String plTradeProto;// 交易合同号码	
	private BigDecimal curUseAmt; // 本次占用金额
	private String chargeFlag;// 扣费成功失败标识

	/**
	 * 展示用
	 */
	private Date impawnDate;// 质押日，不存数据库，用于衍生额度计算器
	private BigDecimal thsFreezeAmt;// 本次冻结额度， 不存数据库
	private BigDecimal thsDefreezeAmt;// 本次解冻额度，不存数据库
	
	private String endFlag;//终结标识 0：未终结 1：已终结  该标识用来记录同一张票据是否在该表中已发生过业务，主要针对同一张票据二次入池的场景。

	private String elsignature; //电子签名
	private String doBatchNo;//出池后期操作批次号    
	private String feeBatchNo;//服务费批次号

	private Date lastOperTm;//最后一次操作时间
	private String lastOperName;//最后一次操作说明
	
	private  String creditObjType;//额度主体类型  1-同业额度  2-对公额度
	private String guarantDiscName;//保贴人名称           
	private String guarantDiscNo;  //保贴编号 
	private String acptHeadBankNo;//承兑行总行行号，用于占用mis额度时候使用
	private String acptHeadBankName;//承兑行总行行号，用于占用mis额度时候使用
	
	private String lastRiskLevel;//最近一次的风险类型，该字段用于承兑行在黑名单中发生变化时候记录上一次风险类型
	private  int delayDays;//顺延天数
	private String cpFlag;//财票标识
	
	
	
	public String getPlDrwrAcctName() {
		return plDrwrAcctName;
	}

	public void setPlDrwrAcctName(String plDrwrAcctName) {
		this.plDrwrAcctName = plDrwrAcctName;
	}

	public String getPlPyeeAcctName() {
		return plPyeeAcctName;
	}

	public void setPlPyeeAcctName(String plPyeeAcctName) {
		this.plPyeeAcctName = plPyeeAcctName;
	}

	public String getPlAccptrAcctName() {
		return plAccptrAcctName;
	}

	public void setPlAccptrAcctName(String plAccptrAcctName) {
		this.plAccptrAcctName = plAccptrAcctName;
	}

	public String getAcptHeadBankName() {
		return acptHeadBankName;
	}

	public void setAcptHeadBankName(String acptHeadBankName) {
		this.acptHeadBankName = acptHeadBankName;
	}

	public String getCpFlag() {
		return cpFlag;
	}

	public void setCpFlag(String cpFlag) {
		this.cpFlag = cpFlag;
	}

	public String getAcptHeadBankNo() {
		return acptHeadBankNo;
	}

	public void setAcptHeadBankNo(String acptHeadBankNo) {
		this.acptHeadBankNo = acptHeadBankNo;
	}
	
	
	
	
	public int getDelayDays() {
		return delayDays;
	}

	public void setDelayDays(int delayDays) {
		this.delayDays = delayDays;
	}

	public String getLastRiskLevel() {
		return lastRiskLevel;
	}

	public void setLastRiskLevel(String lastRiskLevel) {
		this.lastRiskLevel = lastRiskLevel;
	}

	public String getCreditObjType() {
		return creditObjType;
	}

	public void setCreditObjType(String creditObjType) {
		this.creditObjType = creditObjType;
	}

	public String getGuarantDiscName() {
		return guarantDiscName;
	}

	public void setGuarantDiscName(String guarantDiscName) {
		this.guarantDiscName = guarantDiscName;
	}

	public String getGuarantDiscNo() {
		return guarantDiscNo;
	}

	public void setGuarantDiscNo(String guarantDiscNo) {
		this.guarantDiscNo = guarantDiscNo;
	}

	public String getLastOperName() {
		return lastOperName;
	}

	public void setLastOperName(String lastOperName) {
		this.lastOperName = lastOperName;
	}

	public Date getLastOperTm() {
		return lastOperTm;
	}

	public void setLastOperTm(Date lastOperTm) {
		this.lastOperTm = lastOperTm;
	}

	/**
	弃用
	private BigDecimal scEdu; // 计算的额度
	/* 总笔数、总金额，不存数据库 
	private String totalAmt; // 总金额
	private String totalEdu; // 总额度
	private String totalNum; // 总笔数
	private String sumTotalCharge; // 总手续费合计
	 */
	
	public String getFeeBatchNo() {
		return feeBatchNo;
	}

	public void setFeeBatchNo(String feeBatchNo) {
		this.feeBatchNo = feeBatchNo;
	}

	//提至基类
	//private String isEduExist; // 用于判断该票据是否可以产生额度
	//private String plRemark;// 备注
	/**
	 * 无参构造方法私有，构建改使用AssetFactory类
	 */
	public DraftPool() {
	}

	public DraftPool(String assetType, String floatType, String rickLevel) {
		super(assetType, floatType, rickLevel);
	}
	
	
	public String getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}

//	public String getSumTotalCharge() {
//		return sumTotalCharge;
//	}
//
//	public void setSumTotalCharge(String sumTotalCharge) {
//		this.sumTotalCharge = sumTotalCharge;
//	}
//
//	public String getIsEduExist() {
//		return isEduExist;
//	}
//
//	public void setIsEduExist(String isEduExist) {
//		this.isEduExist = isEduExist;
//	}
//
//	public BigDecimal getScEdu() {
//		return scEdu;
//	}
//
//	public void setScEdu(BigDecimal scEdu) {
//		this.scEdu = scEdu;
//	}


	public String getPlDraftMedia() {
		return this.plDraftMedia;
	}

	public void setPlDraftMedia(String plDraftMedia) {
		this.plDraftMedia = plDraftMedia;
	}


	public String getPlIsseAmtValue() {
		return this.plIsseAmtValue;
	}

	public void setPlIsseAmtValue(String plIsseAmtValue) {
		this.plIsseAmtValue = plIsseAmtValue;
	}

	public Date getPlIsseDt() {
		return this.plIsseDt;
	}



	public void setPlIsseDt(Date plIsseDt) {
		this.plIsseDt = plIsseDt;
	}

	public Date getPlDueDt() {
		return this.plDueDt;
	}

	public void setPlDueDt(Date plDueDt) {
		this.plDueDt = plDueDt;
	}

	public String getPlDrwrNm() {
		return this.plDrwrNm;
	}

	public void setPlDrwrNm(String plDrwrNm) {
		this.plDrwrNm = plDrwrNm;
	}

	public String getPlDrwrAcctId() {
		return this.plDrwrAcctId;
	}

	public void setPlDrwrAcctId(String plDrwrAcctId) {
		this.plDrwrAcctId = plDrwrAcctId;
	}

	public String getPlDrwrAcctSvcr() {
		return this.plDrwrAcctSvcr;
	}

	public void setPlDrwrAcctSvcr(String plDrwrAcctSvcr) {
		this.plDrwrAcctSvcr = plDrwrAcctSvcr;
	}

	public String getPlDrwrAcctSvcrNm() {
		return this.plDrwrAcctSvcrNm;
	}

	public void setPlDrwrAcctSvcrNm(String plDrwrAcctSvcrNm) {
		this.plDrwrAcctSvcrNm = plDrwrAcctSvcrNm;
	}

	public String getPlPyeeNm() {
		return this.plPyeeNm;
	}

	public void setPlPyeeNm(String plPyeeNm) {
		this.plPyeeNm = plPyeeNm;
	}

	public String getPlPyeeAcctId() {
		return this.plPyeeAcctId;
	}

	public void setPlPyeeAcctId(String plPyeeAcctId) {
		this.plPyeeAcctId = plPyeeAcctId;
	}

	public String getPlPyeeAcctSvcr() {
		return this.plPyeeAcctSvcr;
	}

	public void setPlPyeeAcctSvcr(String plPyeeAcctSvcr) {
		this.plPyeeAcctSvcr = plPyeeAcctSvcr;
	}

	public String getPlPyeeAcctSvcrNm() {
		return this.plPyeeAcctSvcrNm;
	}

	public void setPlPyeeAcctSvcrNm(String plPyeeAcctSvcrNm) {
		this.plPyeeAcctSvcrNm = plPyeeAcctSvcrNm;
	}

	public String getPlAccptrNm() {
		return this.plAccptrNm;
	}

	public void setPlAccptrNm(String plAccptrNm) {
		this.plAccptrNm = plAccptrNm;
	}

	public String getPlAccptrId() {
		return this.plAccptrId;
	}

	public void setPlAccptrId(String plAccptrId) {
		this.plAccptrId = plAccptrId;
	}

	public String getPlAccptrSvcr() {
		return this.plAccptrSvcr;
	}

	public void setPlAccptrSvcr(String plAccptrSvcr) {
		this.plAccptrSvcr = plAccptrSvcr;
	}

	public String getPlAccptrSvcrNm() {
		return this.plAccptrSvcrNm;
	}

	public void setPlAccptrSvcrNm(String plAccptrSvcrNm) {
		this.plAccptrSvcrNm = plAccptrSvcrNm;
	}

	public Date getPlPaymentTm() {
		return this.plPaymentTm;
	}

	public void setPlPaymentTm(Date plPaymentTm) {
		this.plPaymentTm = plPaymentTm;
	}


	public String getPlCollateralizationId() {
		return this.plCollateralizationId;
	}

	public void setPlCollateralizationId(String plCollateralizationId) {
		this.plCollateralizationId = plCollateralizationId;
	}


	public Date getPlTm() {
		return plTm;
	}

	public void setPlTm(Date plTm) {
		this.plTm = plTm;
	}

	public String getPlSameCity() {
		return plSameCity;
	}

	public void setPlSameCity(String plSameCity) {
		this.plSameCity = plSameCity;
	}


	public String getLastTransType() {
		return lastTransType;
	}

	public void setLastTransType(String lastTransType) {
		this.lastTransType = lastTransType;
	}

	public String getLastTransId() {
		return lastTransId;
	}

	public void setLastTransId(String lastTransId) {
		this.lastTransId = lastTransId;
	}

	public BigDecimal getEduDiscount() {
		return eduDiscount == null ? new BigDecimal("0.0") : eduDiscount;
	}

	public void setEduDiscount(BigDecimal eduDiscount) {
		this.eduDiscount = eduDiscount;
	}

	public Date getImpawnDate() {
		return impawnDate;
	}

	public void setImpawnDate(Date impawnDate) {
		this.impawnDate = impawnDate;
	}

	public String getPlDraftMediaName() {
		return PoolDictionaryCache.getBillMedia(this.plDraftMedia);
	}



	public BigDecimal getThsFreezeAmt() {
		return thsFreezeAmt;
	}

	public void setThsFreezeAmt(BigDecimal thsFreezeAmt) {
		this.thsFreezeAmt = thsFreezeAmt;
	}

	public BigDecimal getThsDefreezeAmt() {
		return thsDefreezeAmt;
	}

	public void setThsDefreezeAmt(BigDecimal thsDefreezeAmt) {
		this.thsDefreezeAmt = thsDefreezeAmt;
	}

	public String getPlAccptrAddress() {
		return plAccptrAddress;
	}

	public void setPlAccptrAddress(String plAccptrAddress) {
		this.plAccptrAddress = plAccptrAddress;
	}

	public String getPlAccptrProto() {
		return plAccptrProto;
	}

	public void setPlAccptrProto(String plAccptrProto) {
		this.plAccptrProto = plAccptrProto;
	}

	public Date getPlAccptrDt() {
		return plAccptrDt;
	}

	public void setPlAccptrDt(Date plAccptrDt) {
		this.plAccptrDt = plAccptrDt;
	}

	public String getPlTradeProto() {
		return plTradeProto;
	}

	public void setPlTradeProto(String plTradeProto) {
		this.plTradeProto = plTradeProto;
	}

//	public String getPlRemark() {
//		return plRemark;
//	}
//
//	public void setPlRemark(String plRemark) {
//		this.plRemark = plRemark;
//	}

	public PoolBillInfo getPoolBillInfo() {
		return poolBillInfo;
	}

	public void setPoolBillInfo(PoolBillInfo poolBillInfo) {
		this.poolBillInfo = poolBillInfo;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}


//	public String getTotalAmt() {
//		return totalAmt;
//	}
//
//	public void setTotalAmt(String totalAmt) {
//		this.totalAmt = totalAmt;
//	}
//
//	public String getTotalEdu() {
//		return totalEdu;
//	}
//
//	public void setTotalEdu(String totalEdu) {
//		this.totalEdu = totalEdu;
//	}
//
//	public String getTotalNum() {
//		return totalNum;
//	}
//
//	public void setTotalNum(String totalNum) {
//		this.totalNum = totalNum;
//	}


	public BigDecimal getCurUseAmt() {
		return curUseAmt;
	}

	public void setCurUseAmt(BigDecimal curUseAmt) {
		this.curUseAmt = curUseAmt;
	}

	public String getChargeFlag() {
		return chargeFlag;
	}

	public void setChargeFlag(String chargeFlag) {
		this.chargeFlag = chargeFlag;
	}

	public String getElsignature() {
		return elsignature;
	}

	public void setElsignature(String elsignature) {
		this.elsignature = elsignature;
	}

	public String getDoBatchNo() {
		return doBatchNo;
	}

	public void setDoBatchNo(String doBatchNo) {
		this.doBatchNo = doBatchNo;
	}

	
}
