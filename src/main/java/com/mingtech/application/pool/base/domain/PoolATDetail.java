package com.mingtech.application.pool.base.domain;


import java.math.BigDecimal;
import java.util.Date;

public class PoolATDetail {
	private String poolNo;             //票号
	private String poolType;           //类型
	private BigDecimal crdtTotal;      //总额度
	private BigDecimal crdtUsed;       //已用额度
	private BigDecimal crdtFree;       //可用额度
	private BigDecimal crdtFrzd;       //冻结额度
	private BigDecimal useEdu;         //本次占用额度
	private BigDecimal startDt;        //起息日
	private BigDecimal dueDt;          //到期日
	private String status;             //状态
	private BigDecimal billAmount;     //票面金额
	private Date plPaymentTm;          //预计回款日
	//private BigDecimal 
	
	/**
	 * 2015417 add zhaoding 添加票据介质等票面信息
	 */
	private String plAccptrSvcr; //承兑人开户行行号
	private String plDraftMedia; //票据介质
	private Date plIsseDt;	     //出票日
	/*******************************************/
	
	public BigDecimal getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}
	public Date getPlPaymentTm() {
		return plPaymentTm;
	}
	public void setPlPaymentTm(Date plPaymentTm) {
		this.plPaymentTm = plPaymentTm;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getStartDt() {
		return startDt;
	}
	public void setStartDt(BigDecimal startDt) {
		this.startDt = startDt;
	}
	public BigDecimal getDueDt() {
		return dueDt;
	}
	public void setDueDt(BigDecimal dueDt) {
		this.dueDt = dueDt;
	}
	public BigDecimal getCrdtFrzd() {
		return crdtFrzd;
	}
	public void setCrdtFrzd(BigDecimal crdtFrzd) {
		this.crdtFrzd = crdtFrzd;
	}
	public String getPoolNo() {
		return poolNo;
	}
	public void setPoolNo(String poolNo) {
		this.poolNo = poolNo;
	}
	public BigDecimal getCrdtTotal() {
		return crdtTotal;
	}
	public void setCrdtTotal(BigDecimal crdtTotal) {
		this.crdtTotal = crdtTotal;
	}
	public BigDecimal getCrdtUsed() {
		return crdtUsed;
	}
	public void setCrdtUsed(BigDecimal crdtUsed) {
		this.crdtUsed = crdtUsed;
	}
	public BigDecimal getCrdtFree() {
		return crdtFree;
	}
	public void setCrdtFree(BigDecimal crdtFree) {
		this.crdtFree = crdtFree;
	}
	
	public PoolATDetail(){
		
	}
	public String getPoolType() {
		return poolType;
	}
	public void setPoolType(String poolType) {
		this.poolType = poolType;
	}
	public BigDecimal getUseEdu() {
		return useEdu;
	}
	public void setUseEdu(BigDecimal useEdu) {
		this.useEdu = useEdu;
	}
	public String getPlAccptrSvcr() {
		return plAccptrSvcr;
	}
	public void setPlAccptrSvcr(String plAccptrSvcr) {
		this.plAccptrSvcr = plAccptrSvcr;
	}
	public String getPlDraftMedia() {
		return plDraftMedia;
	}
	public void setPlDraftMedia(String plDraftMedia) {
		this.plDraftMedia = plDraftMedia;
	}
	public Date getPlIsseDt() {
		return plIsseDt;
	}
	public void setPlIsseDt(Date plIsseDt) {
		this.plIsseDt = plIsseDt;
	}
}
