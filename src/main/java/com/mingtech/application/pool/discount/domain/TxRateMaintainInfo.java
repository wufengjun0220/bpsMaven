package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 利率维护信息表
 * 
 *  table="t_rate_maintainInfo"
 * */
public class TxRateMaintainInfo {
	//	表对应字段
	private String id; 
	private String batchNo; 		//批次号
	private String effState; 		//生效状态	00-新建  SP-审批流程中  0-失效 1-生效  2-待生效
	private String maintainTime; 	//维护日期	
	private String effTime; 		//生效时间	
	private String handler; 		//经办人
	private String handlerNo; 		//经办人工号
	private String reviewer; 		//复核人
	private String reviewerNo; 		//复核人工号
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date lastUpdateTime; //最近更新时间
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date firstInsertTime; //首次插入时间
	private String rateType; 		//利率类型	01-指导  02-优惠  03-最优惠
	private BigDecimal bestRate;	//优惠利率
	
	
	//  查询条件
	private String maintainStartDate;
	private String maintainEndDate;
	
	private String effStartDate;
	private String effEndDate;
	
	private String expireStartDate;
	private String expireEndDate;
	
	private String rateFloatType;
	private String rateFloatNum;
	private String protocolNo;
	
	public String getProtocolNo() {
		return protocolNo;
	}
	public void setProtocolNo(String protocolNo) {
		this.protocolNo = protocolNo;
	}
	public String getRateFloatType() {
		return rateFloatType;
	}
	public void setRateFloatType(String rateFloatType) {
		this.rateFloatType = rateFloatType;
	}
	public String getRateFloatNum() {
		return rateFloatNum;
	}
	public void setRateFloatNum(String rateFloatNum) {
		this.rateFloatNum = rateFloatNum;
	}
	public String getExpireStartDate() {
		return expireStartDate;
	}
	public void setExpireStartDate(String expireStartDate) {
		this.expireStartDate = expireStartDate;
	}
	public String getExpireEndDate() {
		return expireEndDate;
	}
	public void setExpireEndDate(String expireEndDate) {
		this.expireEndDate = expireEndDate;
	}
	private String opreType;	//	利率操作  01-新增   04-失效
	
	private List<TxRateDetailBean> rateDetailBeans = new ArrayList<TxRateDetailBean>();
	private List<TxRateDetailBeanPO> rateDetailBeanPOs = new ArrayList<TxRateDetailBeanPO>();
	
	public List<TxRateDetailBean> getRateDetailBeans() {
		return rateDetailBeans;
	}
	public void setRateDetailBeans(List<TxRateDetailBean> rateDetailBeans) {
		this.rateDetailBeans = rateDetailBeans;
	}
	
	public String getOpreType() {
		return opreType;
	}
	public void setOpreType(String opreType) {
		this.opreType = opreType;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getEffTime() {
		return effTime;
	}
	public void setEffTime(String effTime) {
		this.effTime = effTime;
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
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	public String getReviewerNo() {
		return reviewerNo;
	}
	public void setReviewerNo(String reviewerNo) {
		this.reviewerNo = reviewerNo;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Date getFirstInsertTime() {
		return firstInsertTime;
	}
	public void setFirstInsertTime(Date firstInsertTime) {
		this.firstInsertTime = firstInsertTime;
	}
	public String getRateType() {
		return rateType;
	}
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}
	public BigDecimal getBestRate() {
		return bestRate;
	}
	public void setBestRate(BigDecimal bestRate) {
		this.bestRate = bestRate;
	}
	public List<TxRateDetailBeanPO> getRateDetailBeanPOs() {
		return rateDetailBeanPOs;
	}
	public void setRateDetailBeanPOs(List<TxRateDetailBeanPO> rateDetailBeanPOs) {
		this.rateDetailBeanPOs = rateDetailBeanPOs;
	} 
	
	
}
