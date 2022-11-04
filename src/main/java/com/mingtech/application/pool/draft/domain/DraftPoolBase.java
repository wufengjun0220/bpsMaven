/**
 * 
 */
package com.mingtech.application.pool.draft.domain;

import com.mingtech.application.pool.base.domain.PoolBase;
import com.mingtech.application.pool.common.PoolDictionaryCache;
import com.mingtech.application.pool.trust.domain.DraftStorage;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wbyecheng
 * 
 *         票据资产入池业务明细实体
 * 
 */
public class DraftPoolBase extends PoolBase {
	private static final long serialVersionUID = 643166176204994989L;
	private PoolQueryBean queryBean;
	/** 基础信息 **/
	private String plDraftNb;// 票据号码
	private String plDraftMedia;// 票据介质
	private String plDraftType;// 票据类型
	private Date plIsseDt;// 出票日
	private Date plDueDt;// 到期日
	private Date plPaymentTm;// 预计回款日
	
	/** 出票人信息 **/
	private String plDrwrAcctName;// 出票人账户名称
	private String plDrwrNm;// 出票人名称
	private String plDrwrAcctId;// 出票人账号
	private String plDrwrAcctSvcr;// 出票人开户行行号
	private String plDrwrAcctSvcrNm;// 出票人开户行名称
	/** 收款人信息 **/
	private String plPyeeAcctName;// 收款人账户名称
	private String plPyeeNm;// 收款人名称
	private String plPyeeAcctId;// 收款人账号
	private String plPyeeAcctSvcr;// 收款人开户行行号
	private String plPyeeAcctSvcrNm;// 收款人开户行名称
	/** 承兑人信息 **/
	private String plAccptrAcctName;// 承兑人账户名称
	private String plAccptrNm;// 承兑人名称
	private String plAccptrId;// 承兑人账号
	private String plAccptrAddress; // 承兑人地址
	private String plAccptrSvcr;// 承兑人开户行行号
	private String plAccptrSvcrNm;// 承兑人开户行名称
	
	/** 质押率、费率信息 **/
	//弃用系统自动计算额度打折率，保留额度打折率
	//private BigDecimal sysEduDiscount;// 系统自动计算额度打折率
	private BigDecimal eduDiscount;// 额度打折率
	private BigDecimal chargeRate;// 费率
	private BigDecimal totalCharge;// 总费用
	
	/** 账务信息 **/
	private Date accoutDate; // 记账日期
	private String plRecSvcr;// 业务经办行行号
	private String plRecSvcrNm;// 业务经办行名称
	private String SOperatorId;// 操作员id
	
	private String msgStatus;// 电票报文状态
	
	private String reqSource;// 数据来源
	private String forbidFlag;//禁止背书标识
	private String plSameCity;// 同城异地标识
	private String productId;// 产品id
	private String paperQueryId;// 纸票查询报文id
	private DraftPool draftPool;// 对应票据池表
	private DraftStorage draftStroage;// 对应票据池表

	private String plAccptrProto;// 承兑协议号
	private Date plAccptrDt;// 承兑日
	private String plTradeProto;// 交易合同号码
	private String sumTotalCharge; // add 20150512 zhaoding 增加合计手续费

	private String coreBatchNo;// 核心批次号打印通知单使用
	
	private String postCode;// 承兑人邮编
	private String phone;// 承兑人电话/电挂
	private BigDecimal scEdu; // 额度 不存数据库
	private String workerName;// 经办人姓名
	
	private String hostSeqNo;//主机流水号	记账成功后记录
	/** 弃用 **/
	/**
	private String sumNum; // 总笔数
	private String sumMoney; // 总金额
	private String StorageSts;// 库存状态
	private String storageStsName;// 显示 库存状态中文
	private String address;// 承兑人地址
	
	 */
	/*****************************************************************************/

	/* 页面显示中文名称,不保存数据库 */
	private String plDraftMediaName;// 票据介质中文名称
	private String plDraftTypeName;// 票据类型中文名称
	private String plStatusName;// 状态中文名称
	private String plTradeTypeName;// 代保管/票据池名称
	private String reqSourceName;// 数据来源中文名称
	private String msgStatusName;// 电票报文状态名称

	/*风险票信息*/
	private String rickLevel;//风险等级：高低风险使用预留
	private String riskComment;//风险说明
	private String blackFlag;//黑名单标志	  BLACKLIST = "01"; // 黑名单   GRAYLIST = "02"; // 灰名单
	private String elsignature; //电子签名
	
	private String endFlag;//终结标识 0：未终结 1：已终结  该标识用来记录同一张票据是否在该表中已发生过业务，主要针对同一张票据二次入池的场景。
	
	
	private  String creditObjType;//额度主体类型  1-同业额度  2-对公额度
	private String guarantDiscName;//保贴人名称           
	private String guarantDiscNo;  //保贴编号
	private String acptHeadBankNo;//承兑行总行行号，用于占用mis额度时候使用
	private String acptHeadBankName;//承兑行总行行名，用于占用mis额度时候使用
	
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	private String hilrId;//持有id
	private String splitId;//拆分前的大票主键id
	private String transId;//交易ID
	
	/*** 融合改造新增字段  end*/
	
	
	public String getTransId() {
		return transId;
	}

	

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



	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getAcptHeadBankName() {
		return acptHeadBankName;
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

	public void setAcptHeadBankName(String acptHeadBankName) {
		this.acptHeadBankName = acptHeadBankName;
	}

	public String getAcptHeadBankNo() {
		return acptHeadBankNo;
	}

	public void setAcptHeadBankNo(String acptHeadBankNo) {
		this.acptHeadBankNo = acptHeadBankNo;
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

	public void setPlDraftMediaName(String plDraftMediaName) {
		this.plDraftMediaName = plDraftMediaName;
	}

	public void setPlDraftTypeName(String plDraftTypeName) {
		this.plDraftTypeName = plDraftTypeName;
	}

	public void setPlStatusName(String plStatusName) {
		this.plStatusName = plStatusName;
	}

	public void setPlTradeTypeName(String plTradeTypeName) {
		this.plTradeTypeName = plTradeTypeName;
	}

	public void setReqSourceName(String reqSourceName) {
		this.reqSourceName = reqSourceName;
	}

	public void setMsgStatusName(String msgStatusName) {
		this.msgStatusName = msgStatusName;
	}

	public String getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}

	public String getBlackFlag() {
		return blackFlag;
	}

	public void setBlackFlag(String blackFlag) {
		this.blackFlag = blackFlag;
	}

    //新增字段 20181226
    private String custNo;//核心客户号
    private String custName;//客户名称

	public String getElsignature() {
		return elsignature;
	}

	public void setElsignature(String elsignature) {
		this.elsignature = elsignature;
	}

	public String getForbidFlag() {
		return forbidFlag;
	}

	public void setForbidFlag(String forbidFlag) {
		this.forbidFlag = forbidFlag;
	}

	public String getSumTotalCharge() {
		return sumTotalCharge;
	}

	public void setSumTotalCharge(String sumTotalCharge) {
		this.sumTotalCharge = sumTotalCharge;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public BigDecimal getScEdu() {
		return scEdu;
	}

	public void setScEdu(BigDecimal scEdu) {
		this.scEdu = scEdu;
	}

	public String getPlDraftNb() {
		return this.plDraftNb;
	}

	public void setPlDraftNb(String plDraftNb) {
		this.plDraftNb = plDraftNb;
	}

	public String getPlDraftMedia() {
		return this.plDraftMedia;
	}

	public void setPlDraftMedia(String plDraftMedia) {
		this.plDraftMedia = plDraftMedia;
	}

	public String getPlDraftType() {
		return this.plDraftType;
	}

	public void setPlDraftType(String plDraftType) {
		this.plDraftType = plDraftType;
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

	public String getPlRecSvcr() {
		return this.plRecSvcr;
	}

	public void setPlRecSvcr(String plRecSvcr) {
		this.plRecSvcr = plRecSvcr;
	}

	public String getPlRecSvcrNm() {
		return this.plRecSvcrNm;
	}

	public void setPlRecSvcrNm(String plRecSvcrNm) {
		this.plRecSvcrNm = plRecSvcrNm;
	}

	public String getReqSource() {
		return reqSource;
	}

	public void setReqSource(String reqSource) {
		this.reqSource = reqSource;
	}

	public String getPlSameCity() {
		return plSameCity;
	}

	public void setPlSameCity(String plSameCity) {
		this.plSameCity = plSameCity;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSOperatorId() {
		return SOperatorId;
	}

	public void setSOperatorId(String operatorId) {
		SOperatorId = operatorId;
	}

	public String getPlDraftMediaName() {
		return PoolDictionaryCache.getBillMedia(this.plDraftMedia);
	}

	public String getPlDraftTypeName() {
		String typename = "";
		if (this.plDraftType.equals("AC03")) {
			typename = "三月期存单";
		} else if (this.plDraftType.equals("AC04")) {
			typename = "六月期存单";
		} else if (this.plDraftType.equals("AC05")) {
			typename = "一年期存单";
		} else {
			typename = PoolDictionaryCache.getBillType(this.plDraftType);
		}
		return typename;
	}

	public String getPlStatusName() {
		return PoolDictionaryCache.getFromPoolDictMap(this.getPlStatus());
	}

	public String getPlTradeTypeName() {
		return plTradeTypeName = PoolDictionaryCache.getFromPoolDictMap(this
				.getPlTradeType());
	}

	public String getReqSourceName() {
		return reqSourceName = PoolDictionaryCache.getFromPoolDictMap(this
				.getReqSource());
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

	public String getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(String msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getMsgStatusName() {
		return PoolDictionaryCache.getFromPoolDictMap(this.getMsgStatus());
	}

	public String getPaperQueryId() {
		return paperQueryId;
	}

	public void setPaperQueryId(String paperQueryId) {
		this.paperQueryId = paperQueryId;
	}

	public BigDecimal getEduDiscount() {
		return eduDiscount;
	}

	public void setEduDiscount(BigDecimal eduDiscount) {
		this.eduDiscount = eduDiscount;
	}

	public Date getPlPaymentTm() {
		return plPaymentTm;
	}

	public void setPlPaymentTm(Date plPaymentTm) {
		this.plPaymentTm = plPaymentTm;
	}

	public DraftPool getDraftPool() {
		return draftPool;
	}

	public void setDraftPool(DraftPool draftPool) {
		this.draftPool = draftPool;
	}

	public DraftStorage getDraftStroage() {
		return draftStroage;
	}

	public void setDraftStroage(DraftStorage draftStroage) {
		this.draftStroage = draftStroage;
	}

	public BigDecimal getChargeRate() {
		return chargeRate;
	}

	public void setChargeRate(BigDecimal chargeRate) {
		this.chargeRate = chargeRate;
	}

	public BigDecimal getTotalCharge() {
		return totalCharge;
	}

	public void setTotalCharge(BigDecimal totalCharge) {
		this.totalCharge = totalCharge;
	}

	public Date getAccoutDate() {
		return accoutDate;
	}

	public void setAccoutDate(Date accoutDate) {
		this.accoutDate = accoutDate;
	}

	public String getCoreBatchNo() {
		return coreBatchNo;
	}

	public void setCoreBatchNo(String coreBatchNo) {
		this.coreBatchNo = coreBatchNo;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public PoolQueryBean getQueryBean() {
		return queryBean;
	}

	public void setQueryBean(PoolQueryBean queryBean) {
		this.queryBean = queryBean;
	}

	public String getHostSeqNo() {
		return hostSeqNo;
	}

	public void setHostSeqNo(String hostSeqNo) {
		this.hostSeqNo = hostSeqNo;
	}

	public String getRickLevel() {
		return rickLevel;
	}

	public void setRickLevel(String rickLevel) {
		this.rickLevel = rickLevel;
	}

	public String getRiskComment() {
		return riskComment;
	}

	public void setRiskComment(String riskComment) {
		this.riskComment = riskComment;
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
}
