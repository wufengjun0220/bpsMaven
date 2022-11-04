package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;

import com.mingtech.application.pool.base.domain.PoolBaseBatch;
import com.mingtech.application.pool.common.PoolDictionaryCache;

public class DraftPoolBaseBatch extends PoolBaseBatch{
	
	/** 提至公共类 **/
	//private String id;// 主键ID
	//private String branchId;// 机构ID
	//private String batchNo;// 批次号
	//private String plApplyNm;// 申请人名称
	//private String plCommId;// 申请人组织机构代码
	//private String plApplyAcctId;// 申请人账号
	//private String plApplyAcctSvcr;// 申请人开户行行号
	//private String plApplyAcctSvcrNm;// 申请人开户行名称
	//private String plStatus;// 状态
	//private String plTradeType;// 代保管YW_02/票据池YW_01 存单池 YW_03
	//private String plRemark;// 备注
	//private Date plReqTime;// 申请日期
	//private BigDecimal totleAmount = new BigDecimal(0);// 票面总金额
	//private Integer totleBill = new Integer(0);// 总笔数
	
	private static final long serialVersionUID = -7577875315847116407L;
	/** 票据基本信息 **/
	private String plDraftMedia;// 票据介质
	private String plDraftType;// 票据类型
	
	/** 账务信息 **/
	private String plRecSvcr;// 业务经办行行号
	private String plRecSvcrNm;// 业务经办行名称
	private String plOperId;// 客户经理名称
	private String productId;// 产品id
	private String SOperatorId;// 操作员id
	private String customerManagerId;// 客户经理id
	
	/** 费率、质押率信息 **/
	private BigDecimal chargeRate;// 费率
	private BigDecimal totalCharge;// 总费用
	
	/* 页面显示中文名称 */
	private String plDraftMediaName;// 票据介质中文名称
	private String plDraftTypeName;// 票据类型中文名称
	private String plStatusName;// 状态中文名称
	
	private PoolQueryBean queryBean;
	private String workerId;// 经办人id
	private String workerName;// 经办人姓名
	/** 废弃 **/

	//private Integer workItemId; // 工作任务ID
	//private Integer caseId; // 主流程ID
	//private String sequenceId;// 行内序号
	//private String sumMoney; // 总笔数
	//private String sumNum; // 总金额
	//private String totalBatch; // 总批数e
	//private String ifDraftOrPool;// 区分是代保管还是入池 1代表代保管 2代表入池
	//private String StorageSts;// 库存状态
	//private String storageStsName;// 显示 库存状态中文
//	public String getIfDraftOrPool() {
//		return ifDraftOrPool;
//	}
//
//	public void setIfDraftOrPool(String ifDraftOrPool) {
//		this.ifDraftOrPool = ifDraftOrPool;
//	}
//
//	public String getSumMoney() {
//		return sumMoney;
//	}
//
//	public void setSumMoney(String sumMoney) {
//		this.sumMoney = sumMoney;
//	}
//
//	public String getSumNum() {
//		return sumNum;
//	}
//
//	public void setSumNum(String sumNum) {
//		this.sumNum = sumNum;
//	}
//
//	public String getTotalBatch() {
//		return totalBatch;
//	}
//
//	public void setTotalBatch(String totalBatch) {
//		this.totalBatch = totalBatch;
//	}
//
//	public String getId() {
//		return this.id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public String getPlApplyNm() {
//		return this.plApplyNm;
//	}
//
//	public void setPlApplyNm(String plApplyNm) {
//		this.plApplyNm = plApplyNm;
//	}
//
//	public String getPlCommId() {
//		return this.plCommId;
//	}
//
//	public void setPlCommId(String plCommId) {
//		this.plCommId = plCommId;
//	}
//
//	public String getPlApplyAcctId() {
//		return this.plApplyAcctId;
//	}
//
//	public void setPlApplyAcctId(String plApplyAcctId) {
//		this.plApplyAcctId = plApplyAcctId;
//	}
//
//	public String getPlApplyAcctSvcr() {
//		return this.plApplyAcctSvcr;
//	}
//
//	public void setPlApplyAcctSvcr(String plApplyAcctSvcr) {
//		this.plApplyAcctSvcr = plApplyAcctSvcr;
//	}
//
//	public String getPlApplyAcctSvcrNm() {
//		return this.plApplyAcctSvcrNm;
//	}
//
//	public void setPlApplyAcctSvcrNm(String plApplyAcctSvcrNm) {
//		this.plApplyAcctSvcrNm = plApplyAcctSvcrNm;
//	}

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

//	public String getPlStatus() {
//		return this.plStatus;
//	}
//
//	public void setPlStatus(String plStatus) {
//		this.plStatus = plStatus;
//	}
//
//	public String getPlTradeType() {
//		return this.plTradeType;
//	}
//
//	public void setPlTradeType(String plTradeType) {
//		this.plTradeType = plTradeType;
//	}

	public String getPlOperId() {
		return this.plOperId;
	}

	public void setPlOperId(String plOperId) {
		this.plOperId = plOperId;
	}

//	public String getPlRemark() {
//		return this.plRemark;
//	}
//
//	public void setPlRemark(String plRemark) {
//		this.plRemark = plRemark;
//	}
//
//	public Date getPlReqTime() {
//		return this.plReqTime;
//	}
//
//	public void setPlReqTime(Date plReqTime) {
//		this.plReqTime = plReqTime;
//	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

//	public BigDecimal getTotleAmount() {
//		return totleAmount;
//	}
//
//	public void setTotleAmount(BigDecimal totleAmount) {
//		this.totleAmount = totleAmount;
//	}
//
//	public Integer getTotleBill() {
//		return totleBill;
//	}
//
//	public void setTotleBill(Integer totleBill) {
//		this.totleBill = totleBill;
//	}
//
//	public String getBatchNo() {
//		return batchNo;
//	}
//
//	public void setBatchNo(String batchNo) {
//		this.batchNo = batchNo;
//	}
//
//	public Integer getWorkItemId() {
//		return workItemId;
//	}
//
//	public void setWorkItemId(Integer workItemId) {
//		this.workItemId = workItemId;
//	}
//
//	public Integer getCaseId() {
//		return caseId;
//	}
//
//	public void setCaseId(Integer caseId) {
//		this.caseId = caseId;
//	}

	public String getSOperatorId() {
		return SOperatorId;
	}

	public void setSOperatorId(String operatorId) {
		SOperatorId = operatorId;
	}

	public String getPlDraftMediaName() {
		plDraftMediaName = PoolDictionaryCache.getBillMedia(this.getPlDraftMedia());
		return plDraftMediaName;
	}

	public void setPlDraftMediaName(String plDraftMediaName) {
		this.plDraftMediaName = plDraftMediaName;
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
		// plDraftTypeName = DictionaryCache.getBillType(this.plDraftType);
		// return typename;
	}

	public void setPlDraftTypeName(String plDraftTypeName) {
		this.plDraftTypeName = plDraftTypeName;
	}

	public String getPlStatusName() {
		plStatusName = PoolDictionaryCache.getFromPoolDictMap(this
				.getPlStatus());
		return plStatusName;
	}

	public void setPlStatusName(String plStatusName) {
		this.plStatusName = plStatusName;
	}

//	public String getSequenceId() {
//		return sequenceId;
//	}
//
//	public void setSequenceId(String sequenceId) {
//		this.sequenceId = sequenceId;
//	}

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

	public String getCustomerManagerId() {
		return customerManagerId;
	}

	public void setCustomerManagerId(String customerManagerId) {
		this.customerManagerId = customerManagerId;
	}

//	public String getWorkerId() {
//		return workerId;
//	}
//
//	public void setWorkerId(String workerId) {
//		this.workerId = workerId;
//	}
//
	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
//
//	public String getBranchId() {
//		return branchId;
//	}
//
//	public void setBranchId(String branchId) {
//		this.branchId = branchId;
//	}
//
//	public String getStorageSts() {
//		return StorageSts;
//	}
//
//	public void setStorageSts(String storageSts) {
//		StorageSts = storageSts;
//	}
//
//	public String getStorageStsName() {
//		if ("0".equals(this.StorageSts)) {
//			return "已入库";
//		}
//		return "未入库";
//	}
//
//	public void setStorageStsName(String storageStsName) {
//		this.storageStsName = storageStsName;
//	}

	public PoolQueryBean getQueryBean() {
		return queryBean;
	}

	public void setQueryBean(PoolQueryBean queryBean) {
		this.queryBean = queryBean;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}
}
