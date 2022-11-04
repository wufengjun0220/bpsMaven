/**
 * 
 */
package com.mingtech.application.pool.base.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
 * @author wbyecheng
 * 
 * 资产池业务批次基础实体
 * 
 */
public class PoolBaseBatch implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;// 主键ID
	private String branchId;// 机构ID
	private String batchNo;// 批次号
	private BigDecimal totleAmount;// 总金额
	private Integer totleBill;// 总笔数
	private String plTradeType;// 代保管YW_02/票据池YW_01 存单池 YW_03
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date plReqTime;//申请时间
	private String plApplyNm;// 申请人名称
	private String plCommId;// 申请人组织机构代码
	private String plUSCC;//申请人统一信用代码
	private String plApplyAcctId;// 申请人账号
	private String plApplyAcctSvcr;// 申请人开户行行号
	private String plApplyAcctSvcrNm;// 申请人开户行名称
	private String plStatus;// 状态
	private String plStatusName;// 状态
	private String plRemark;// 备注

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	/* 票据状态名 */
	public String getPlStatusNameString() {
		return plStatusName = PoolDictionaryCache
				.getStatusName(this.getPlStatus());
	}

	public void setPlStatusName(String plStatusName) {
		this.plStatusName = plStatusName;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public BigDecimal getTotleAmount() {
		return totleAmount;
	}

	public void setTotleAmount(BigDecimal totleAmount) {
		this.totleAmount = totleAmount;
	}

	public Integer getTotleBill() {
		return totleBill;
	}

	public void setTotleBill(Integer totleBill) {
		this.totleBill = totleBill;
	}

	public String getPlTradeType() {
		return plTradeType;
	}

	public void setPlTradeType(String plTradeType) {
		this.plTradeType = plTradeType;
	}

	public String getPlApplyNm() {
		return plApplyNm;
	}

	public Date getPlReqTime() {
		return plReqTime;
	}

	public void setPlReqTime(Date plReqTime) {
		this.plReqTime = plReqTime;
	}

	public void setPlApplyNm(String plApplyNm) {
		this.plApplyNm = plApplyNm;
	}

	public String getPlCommId() {
		return plCommId;
	}

	public void setPlCommId(String plCommId) {
		this.plCommId = plCommId;
	}

	public String getPlApplyAcctId() {
		return plApplyAcctId;
	}

	public void setPlApplyAcctId(String plApplyAcctId) {
		this.plApplyAcctId = plApplyAcctId;
	}

	public String getPlStatus() {
		return plStatus;
	}

	public void setPlStatus(String plStatus) {
		this.plStatus = plStatus;
	}

	public String getPlRemark() {
		return plRemark;
	}

	public void setPlRemark(String plRemark) {
		this.plRemark = plRemark;
	}

	public String getPlUSCC() {
		return plUSCC;
	}

	public void setPlUSCC(String plUSCC) {
		this.plUSCC = plUSCC;
	}

	public String getPlApplyAcctSvcr() {
		return plApplyAcctSvcr;
	}

	public void setPlApplyAcctSvcr(String plApplyAcctSvcr) {
		this.plApplyAcctSvcr = plApplyAcctSvcr;
	}

	public String getPlApplyAcctSvcrNm() {
		return plApplyAcctSvcrNm;
	}

	public void setPlApplyAcctSvcrNm(String plApplyAcctSvcrNm) {
		this.plApplyAcctSvcrNm = plApplyAcctSvcrNm;
	}

}
