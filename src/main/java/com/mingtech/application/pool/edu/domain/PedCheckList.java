package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 网银对账票据明细
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-5-27
 */

public class PedCheckList implements java.io.Serializable {

	private static final long serialVersionUID = -2032812008703461476L;
	private String id;  
	private String batchNo; //批次号
	private String billType;//票据类型
	private String billNo;//票据号码
	private BigDecimal billAmount;//票面金额
	private Date isseDt;//出票日
	private Date dueDt;//到期日
	private String accptrNm;//承兑人名称
	private String sbillMedia;//票据介质
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private String pledgeNo;//质押清单编号
	/*** 融合改造新增字段  end*/
	
	// Constructors

	/** default constructor */
	public PedCheckList() {
	}

	/** full constructor */
	public PedCheckList(String batchNo, String billType, String billNo,
			BigDecimal billAmount, Date isseDt, Date dueDt, String accptrNm) {
		this.batchNo = batchNo;
		this.billType = billType;
		this.billNo = billNo;
		this.billAmount = billAmount;
		this.isseDt = isseDt;
		this.dueDt = dueDt;
		this.accptrNm = accptrNm;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public String getPledgeNo() {
		return pledgeNo;
	}

	public void setPledgeNo(String pledgeNo) {
		this.pledgeNo = pledgeNo;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getBillType() {
		return this.billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillNo() {
		return this.billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public BigDecimal getBillAmount() {
		return this.billAmount;
	}

	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}

	public Date getIsseDt() {
		return this.isseDt;
	}

	public void setIsseDt(Date isseDt) {
		this.isseDt = isseDt;
	}

	public Date getDueDt() {
		return this.dueDt;
	}

	public void setDueDt(Date dueDt) {
		this.dueDt = dueDt;
	}

	public String getAccptrNm() {
		return this.accptrNm;
	}

	public void setAccptrNm(String accptrNm) {
		this.accptrNm = accptrNm;
	}

	public String getSbillMedia() {
		return sbillMedia;
	}

	public void setSbillMedia(String sbillMedia) {
		this.sbillMedia = sbillMedia;
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
	
}