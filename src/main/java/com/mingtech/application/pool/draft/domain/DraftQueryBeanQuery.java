package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.common.PoolComm;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 封装查询条件
 * @author wbmengdepeng
 *
 */

public class DraftQueryBeanQuery{
	
	private String id;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startplDueDt;// 到期日
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endplDueDt;// 到期日
	private BigDecimal startplIsseAmt;// 票据金额开始
	private BigDecimal endplIsseAmt;// 票据金额结束
	private BigDecimal plIsseAmt;// 票据金额

	private String plApplyNm;// 申请人名称
	private String plDraftNb;// 票据号码
	private String plDraftMedia;// 票据介质
	private String plRecSvcr;// 业务经办行行号
	private String plAccptrSvcr;// 承兑人开户行行号
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date plIsseDt;// 出票日
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date plIsseDt1;// 出票日
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date plDueDt;//到期日
	private List plStatus;
	private DraftPoolInBatch draftPoolInBatch;
	
	private String plCommId;// 申请人组织机构代码
	private String productId;// 产品id
	private String plDraftType;// 票据类型
	private String branchId;// 机构ID
	
	//批次
	private String BatchNo;
	private String PlTradeType;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startplReqTime;//申请日
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endplReqTime;//申请日
	
	
	//asset中的属性
	private String assetNb;//资产号码
	private BigDecimal startassetAmt;//金额
	private BigDecimal endassetAmt;//金额
	private String assetType;//类型
	private String assetStatus;//状态
	/** 申请人信息  **/
	private String assetApplyNm;//申请人名称
	private String assetCommId;//申请人组织机构代码
	private String assetApplyAcctId;//申请人账号
	private String assetApplyAcctSvcr;//申请人开户行行号
	private String assetApplyAcctSvcrNm;//申请人开户行名称
	/** 经办人信息 **/
	private String assetRecSvcr;//业务经办行行号
	private String assetRecSvcrNm;//业务经办行名称
	//asset中的属性end
	
	/**
	 *库存 
	 */
	private List txType;
	//票据池编号 liuxiaodong add
	private String poolAgreement;
	private String poolName;//票据池名称
	private String assetAmt;//票据金额
	private String plDrwrNm;//出票人名称
	private String plDrwrAcctSvcrNm;//出票人 开户行名称
	private String plPyeeNm;//收款人名称
	private String plPyeeAcctSvcrNm;//收款人开户行名称
	private String isEduExist;//是否产生额度
	private String isEduExistName;//是否产生额度
	private String rickLevel;//风险等级
	private String rickLevelName;//风险等级
	private String custNo;//客户号
	private String plDrwrAcctSvcr; //出票人开户行行号
	private String plAccptrNm;   //承兑人/行名称
	private String plAccptrSvcrNm; //承兑人开户行名称
	private String plPyeeAcctSvcr;  //收款人开户行行号
	private Date plReqTime; //操作时间
	private String SBanEndrsmtFlag;//不得转让标记   0：可转让   1：不可转让
	private int delayDays;//顺延天数
	
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	private String hilrId;//持有id
	private String splitId;//拆分前的大票主键id
	
	/*** 融合改造新增字段  end*/
	
	
	public String getId() {
		return id;
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
	public void setId(String id) {
		this.id = id;
	}
	public String getRickLevel() {
		return rickLevel;
	}
	public void setRickLevel(String rickLevel) {
		this.rickLevel = rickLevel;
	}
	public Date getPlDueDt() {
		return plDueDt;
	}
	public void setPlDueDt(Date plDueDt) {
		this.plDueDt = plDueDt;
	}
	public String getAssetAmt() {
		return assetAmt;
	}
	public void setAssetAmt(String assetAmt) {
		this.assetAmt = assetAmt;
	}
	public String getPlDrwrNm() {
		return plDrwrNm;
	}
	public void setPlDrwrNm(String plDrwrNm) {
		this.plDrwrNm = plDrwrNm;
	}
	public String getPlDrwrAcctSvcrNm() {
		return plDrwrAcctSvcrNm;
	}
	public void setPlDrwrAcctSvcrNm(String plDrwrAcctSvcrNm) {
		this.plDrwrAcctSvcrNm = plDrwrAcctSvcrNm;
	}
	public String getPlPyeeNm() {
		return plPyeeNm;
	}
	public void setPlPyeeNm(String plPyeeNm) {
		this.plPyeeNm = plPyeeNm;
	}
	public String getPlPyeeAcctSvcrNm() {
		return plPyeeAcctSvcrNm;
	}
	public void setPlPyeeAcctSvcrNm(String plPyeeAcctSvcrNm) {
		this.plPyeeAcctSvcrNm = plPyeeAcctSvcrNm;
	}
	public String getIsEduExist() {
		return isEduExist;
	}
	public void setIsEduExist(String isEduExist) {
		this.isEduExist = isEduExist;
	}
	

	public String getIsEduExistName() {
		String name = "";
		if("0".equals(this.getIsEduExist())){
			name="否";
		}else if("1".equals(this.getIsEduExist())){
			name="是";
		}
		return name;
	}
	
	public String getRickLevelName() {
		String name = "";
		if(PoolComm.LOW_RISK.equals(this.getRickLevel())){
			name="低风险额度";
		}else if(PoolComm.HIGH_RISK.equals(this.getRickLevel())){
			name="高风险额度";
		}else if(PoolComm.NOTIN_RISK.equals(this.getRickLevel())){
			name="未产生额度";
		}
		return name;
	}
	public void setRickLevelName(String rickLevelName) {
		this.rickLevelName = rickLevelName;
	}
	public void setIsEduExistName(String isEduExistName) {
		this.isEduExistName = isEduExistName;
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getPoolAgreement() {
		return poolAgreement;
	}
	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}
	public String getPlApplyNm() {
		return plApplyNm;
	}
	public void setPlApplyNm(String plApplyNm) {
		this.plApplyNm = plApplyNm;
	}
	public String getPlDraftNb() {
		return plDraftNb;
	}
	public void setPlDraftNb(String plDraftNb) {
		this.plDraftNb = plDraftNb;
	}
	public String getPlDraftMedia() {
		return plDraftMedia;
	}
	public void setPlDraftMedia(String plDraftMedia) {
		this.plDraftMedia = plDraftMedia;
	}
	
	public String getPlRecSvcr() {
		return plRecSvcr;
	}
	public void setPlRecSvcr(String plRecSvcr) {
		this.plRecSvcr = plRecSvcr;
	}
	public List getPlStatus() {
		return plStatus;
	}
	public void setPlStatus(List plStatus) {
		this.plStatus = plStatus;
	}
	public Date getStartplDueDt() {
		return startplDueDt;
	}
	public void setStartplDueDt(Date startplDueDt) {
		this.startplDueDt = startplDueDt;
	}
	public Date getEndplDueDt() {
		return endplDueDt;
	}
	public void setEndplDueDt(Date endplDueDt) {
		this.endplDueDt = endplDueDt;
	}
	public String getBatchNo() {
		return BatchNo;
	}
	public void setBatchNo(String batchNo) {
		BatchNo = batchNo;
	}
	public Date getStartplReqTime() {
		return startplReqTime;
	}
	public void setStartplReqTime(Date startplReqTime) {
		this.startplReqTime = startplReqTime;
	}
	public Date getEndplReqTime() {
		return endplReqTime;
	}
	public void setEndplReqTime(Date endplReqTime) {
		this.endplReqTime = endplReqTime;
	}
	public String getPlTradeType() {
		return PlTradeType;
	}
	public void setPlTradeType(String plTradeType) {
		PlTradeType = plTradeType;
	}
	public String getPlCommId() {
		return plCommId;
	}
	public void setPlCommId(String plCommId) {
		this.plCommId = plCommId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getPlDraftType() {
		return plDraftType;
	}
	public void setPlDraftType(String plDraftType) {
		this.plDraftType = plDraftType;
	}
	public DraftPoolInBatch getDraftPoolInBatch() {
		return draftPoolInBatch;
	}
	public void setDraftPoolInBatch(DraftPoolInBatch draftPoolInBatch) {
		this.draftPoolInBatch = draftPoolInBatch;
	}
	public BigDecimal getStartplIsseAmt() {
		return startplIsseAmt;
	}
	public void setStartplIsseAmt(BigDecimal startplIsseAmt) {
		this.startplIsseAmt = startplIsseAmt;
	}
	public BigDecimal getEndplIsseAmt() {
		return endplIsseAmt;
	}
	public void setEndplIsseAmt(BigDecimal endplIsseAmt) {
		this.endplIsseAmt = endplIsseAmt;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getPlAccptrSvcr() {
		return plAccptrSvcr;
	}
	public void setPlAccptrSvcr(String plAccptrSvcr) {
		this.plAccptrSvcr = plAccptrSvcr;
	}
	public Date getPlIsseDt() {
		return plIsseDt;
	}
	public void setPlIsseDt(Date plIsseDt) {
		this.plIsseDt = plIsseDt;
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
	public String getAssetStatus() {
		return assetStatus;
	}
	public void setAssetStatus(String assetStatus) {
		this.assetStatus = assetStatus;
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
	public BigDecimal getStartassetAmt() {
		return startassetAmt;
	}
	public void setStartassetAmt(BigDecimal startassetAmt) {
		this.startassetAmt = startassetAmt;
	}
	public BigDecimal getEndassetAmt() {
		return endassetAmt;
	}
	public void setEndassetAmt(BigDecimal endassetAmt) {
		this.endassetAmt = endassetAmt;
	}
	public List getTxType() {
		return txType;
	}
	public void setTxType(List txType) {
		this.txType = txType;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getPlDrwrAcctSvcr() {
		return plDrwrAcctSvcr;
	}
	public void setPlDrwrAcctSvcr(String plDrwrAcctSvcr) {
		this.plDrwrAcctSvcr = plDrwrAcctSvcr;
	}
	public String getPlAccptrNm() {
		return plAccptrNm;
	}
	public void setPlAccptrNm(String plAccptrNm) {
		this.plAccptrNm = plAccptrNm;
	}
	public String getPlAccptrSvcrNm() {
		return plAccptrSvcrNm;
	}
	public void setPlAccptrSvcrNm(String plAccptrSvcrNm) {
		this.plAccptrSvcrNm = plAccptrSvcrNm;
	}
	public String getPlPyeeAcctSvcr() {
		return plPyeeAcctSvcr;
	}
	public void setPlPyeeAcctSvcr(String plPyeeAcctSvcr) {
		this.plPyeeAcctSvcr = plPyeeAcctSvcr;
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
	public String getSBanEndrsmtFlag() {
		return SBanEndrsmtFlag;
	}
	public void setSBanEndrsmtFlag(String sBanEndrsmtFlag) {
		SBanEndrsmtFlag = sBanEndrsmtFlag;
	}
	public Date getPlIsseDt1() {
		return plIsseDt1;
	}
	public void setPlIsseDt1(Date plIsseDt1) {
		this.plIsseDt1 = plIsseDt1;
	}
	public int getDelayDays() {
		return delayDays;
	}
	public void setDelayDays(int delayDays) {
		this.delayDays = delayDays;
	}

	
	
	
}
