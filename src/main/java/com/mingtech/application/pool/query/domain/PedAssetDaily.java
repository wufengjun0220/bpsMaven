package com.mingtech.application.pool.query.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 每日资产表：该表日终跑批时候生成数据，记录当日票据池所有客户资产数据
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-1
 */
public class PedAssetDaily implements java.io.Serializable {

	private static final long serialVersionUID = 6697978884290783909L;
	private String id;            //主键ID           
	private String batchId;       //批次ID     
	private String bpsNo;         //票据池编号    
	private String bpsName;       //票据池名称    
	private String custNo;        //客户号      
	private String custName;      //客户名称     
	private Date createDate;      //创建日期     
	private String assetType;     //资产类型     01-票据    02-活期保证金
	private BigDecimal amt;           //金额       
	private String billNo;        //票号       
	private String billMedia;     //票据介质     1-纸票	2-电子
	private String billType;      //票据类型     AC01-银票  AC02-商票
	private Date issueDt;         //出票日      
	private Date dueDt;           //到期日      
	private String banEndrsmtFlag;//不得转让标记   0-可转让 1-不可转让
	private String drwrName;      //出票人名称    
	private String drwrBankNo;    //出票人开户行行号 
	private String acptName;      //承兑人/承兑行名称
	private String acptBankName;  //承兑人开户行名称 
	private String acptBankNo;    //承兑人开户行行号 
	private String pyeeName;      //收款人名称    
	private String pyeeBankName;  //收款人开户行行名 
	private String pyeeBankNo;    //收款人开户行行号 
	private Date createTime; //创建时间     
	private String SIssuerAccount;// 出票人账号
	private String SPayeeAccount; // 收款人账号

	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	
	private String SIssuerAcctName;// 出票人账号名称
	private String plAccptrAcctName;// 承兑人账号名称
	private String plPyeeAcctName;// 收款人账号名称
	private String plAccptrAcctNo;// 承兑人账号
	private String plAccptr;// 承兑人名称
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	
	/*** 融合改造新增字段  end*/
	
	public String getSIssuerAccount() {
		return SIssuerAccount;
	}

	public String getSIssuerAcctName() {
		return SIssuerAcctName;
	}

	public void setSIssuerAcctName(String sIssuerAcctName) {
		SIssuerAcctName = sIssuerAcctName;
	}

	public String getPlAccptrAcctName() {
		return plAccptrAcctName;
	}

	public void setPlAccptrAcctName(String plAccptrAcctName) {
		this.plAccptrAcctName = plAccptrAcctName;
	}

	public String getPlPyeeAcctName() {
		return plPyeeAcctName;
	}

	public void setPlPyeeAcctName(String plPyeeAcctName) {
		this.plPyeeAcctName = plPyeeAcctName;
	}

	public String getPlAccptrAcctNo() {
		return plAccptrAcctNo;
	}

	public void setPlAccptrAcctNo(String plAccptrAcctNo) {
		this.plAccptrAcctNo = plAccptrAcctNo;
	}

	public String getPlAccptr() {
		return plAccptr;
	}

	public void setPlAccptr(String plAccptr) {
		this.plAccptr = plAccptr;
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

	public void setSIssuerAccount(String sIssuerAccount) {
		SIssuerAccount = sIssuerAccount;
	}

	public String getSPayeeAccount() {
		return SPayeeAccount;
	}

	public void setSPayeeAccount(String sPayeeAccount) {
		SPayeeAccount = sPayeeAccount;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchId() {
		return this.batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return this.bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getAssetType() {
		return this.assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}


	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public String getBillNo() {
		return this.billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getBillMedia() {
		return this.billMedia;
	}

	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}

	public String getBillType() {
		return this.billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public Date getIssueDt() {
		return this.issueDt;
	}

	public void setIssueDt(Date issueDt) {
		this.issueDt = issueDt;
	}

	public Date getDueDt() {
		return this.dueDt;
	}

	public void setDueDt(Date dueDt) {
		this.dueDt = dueDt;
	}

	public String getBanEndrsmtFlag() {
		return this.banEndrsmtFlag;
	}

	public void setBanEndrsmtFlag(String banEndrsmtFlag) {
		this.banEndrsmtFlag = banEndrsmtFlag;
	}

	public String getDrwrName() {
		return this.drwrName;
	}

	public void setDrwrName(String drwrName) {
		this.drwrName = drwrName;
	}

	public String getDrwrBankNo() {
		return this.drwrBankNo;
	}

	public void setDrwrBankNo(String drwrBankNo) {
		this.drwrBankNo = drwrBankNo;
	}

	public String getAcptName() {
		return this.acptName;
	}

	public void setAcptName(String acptName) {
		this.acptName = acptName;
	}

	public String getAcptBankName() {
		return this.acptBankName;
	}

	public void setAcptBankName(String acptBankName) {
		this.acptBankName = acptBankName;
	}

	public String getAcptBankNo() {
		return this.acptBankNo;
	}

	public void setAcptBankNo(String acptBankNo) {
		this.acptBankNo = acptBankNo;
	}

	public String getPyeeName() {
		return this.pyeeName;
	}

	public void setPyeeName(String pyeeName) {
		this.pyeeName = pyeeName;
	}

	public String getPyeeBankName() {
		return this.pyeeBankName;
	}

	public void setPyeeBankName(String pyeeBankName) {
		this.pyeeBankName = pyeeBankName;
	}

	public String getPyeeBankNo() {
		return this.pyeeBankNo;
	}

	public void setPyeeBankNo(String pyeeBankNo) {
		this.pyeeBankNo = pyeeBankNo;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}