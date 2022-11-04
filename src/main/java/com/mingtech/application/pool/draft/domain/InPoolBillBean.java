package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class InPoolBillBean {
	private String serial; //序号
	private String poperBeatch;//保管机构
	private String assetNb;//票号
	private BigDecimal assetAmt;//金额
	private BigDecimal FBillAmount;//票面金额
	private Date plIsseDt;//出票日
	private Date plDueDt;//到期日
	private String plDrwrNm;//出票人名称
	private String plDrwrAcctId;//出票人账号
	private String plDrwrAcctSvcrNm;//出票人开户行
	private String plPyeeNm;//收款人名称
	private String plPyeeAcctId;//收款人账号
	private String plPyeeAcctSvcrNm;//收款人开户行
	private String plAccptrSvcrNm;//承兑人开户行	
	private String plAccptrNm;//承兑人/付款人	
	
	private String marginAccount;//保证金账号
	private String marginAccountName;//保证金账号名
	private String SDealStatus;//状态  DS_02已入池   DS_03出池处理中  DS_04已出池

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date plIsseDtStart;// 出票日开始
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date plIsseDtEnd;// 出票日结束
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date plDueDtStart;// 到期日开始
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date plDueDtEnd;// 到期日结束
	
	private BigDecimal assetAmtStart;
	private BigDecimal assetAmtEnd;
	
	
	
	public String getPlAccptrNm() {
		return plAccptrNm;
	}
	public void setPlAccptrNm(String plAccptrNm) {
		this.plAccptrNm = plAccptrNm;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getPoperBeatch() {
		return poperBeatch;
	}
	public void setPoperBeatch(String poperBeatch) {
		this.poperBeatch = poperBeatch;
	}
	public String getAssetNb() {
		return assetNb;
	}
	public void setAssetNb(String assetNb) {
		this.assetNb = assetNb;
	}
	public BigDecimal getAssetAmt() {
		return assetAmt;
	}
	public void setAssetAmt(BigDecimal assetAmt) {
		this.assetAmt = assetAmt;
	}
	public Date getPlIsseDtStart() {
		return plIsseDtStart;
	}
	public void setPlIsseDtStart(Date plIsseDtStart) {
		this.plIsseDtStart = plIsseDtStart;
	}
	public Date getPlIsseDtEnd() {
		return plIsseDtEnd;
	}
	public void setPlIsseDtEnd(Date plIsseDtEnd) {
		this.plIsseDtEnd = plIsseDtEnd;
	}
	public Date getPlDueDtStart() {
		return plDueDtStart;
	}
	public void setPlDueDtStart(Date plDueDtStart) {
		this.plDueDtStart = plDueDtStart;
	}
	public Date getPlDueDtEnd() {
		return plDueDtEnd;
	}
	public void setPlDueDtEnd(Date plDueDtEnd) {
		this.plDueDtEnd = plDueDtEnd;
	}
	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}
	public void setFBillAmount(BigDecimal fBillAmount) {
		FBillAmount = fBillAmount;
	}
	public Date getPlIsseDt() {
		return plIsseDt;
	}
	public void setPlIsseDt(Date plIsseDt) {
		this.plIsseDt = plIsseDt;
	}
	public Date getPlDueDt() {
		return plDueDt;
	}
	public void setPlDueDt(Date plDueDt) {
		this.plDueDt = plDueDt;
	}
	public String getPlDrwrNm() {
		return plDrwrNm;
	}
	public void setPlDrwrNm(String plDrwrNm) {
		this.plDrwrNm = plDrwrNm;
	}
	public String getPlDrwrAcctId() {
		return plDrwrAcctId;
	}
	public void setPlDrwrAcctId(String plDrwrAcctId) {
		this.plDrwrAcctId = plDrwrAcctId;
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
	public String getPlPyeeAcctId() {
		return plPyeeAcctId;
	}
	public void setPlPyeeAcctId(String plPyeeAcctId) {
		this.plPyeeAcctId = plPyeeAcctId;
	}
	public String getPlPyeeAcctSvcrNm() {
		return plPyeeAcctSvcrNm;
	}
	public void setPlPyeeAcctSvcrNm(String plPyeeAcctSvcrNm) {
		this.plPyeeAcctSvcrNm = plPyeeAcctSvcrNm;
	}
	public String getPlAccptrSvcrNm() {
		return plAccptrSvcrNm;
	}
	public void setPlAccptrSvcrNm(String plAccptrSvcrNm) {
		this.plAccptrSvcrNm = plAccptrSvcrNm;
	}
	public String getMarginAccount() {
		return marginAccount;
	}
	public void setMarginAccount(String marginAccount) {
		this.marginAccount = marginAccount;
	}
	public String getMarginAccountName() {
		return marginAccountName;
	}
	public void setMarginAccountName(String marginAccountName) {
		this.marginAccountName = marginAccountName;
	}
	public String getSDealStatus() {
		return SDealStatus;
	}
	public void setSDealStatus(String sDealStatus) {
		SDealStatus = sDealStatus;
	}
	public BigDecimal getAssetAmtStart() {
		return assetAmtStart;
	}
	public void setAssetAmtStart(BigDecimal assetAmtStart) {
		this.assetAmtStart = assetAmtStart;
	}
	public BigDecimal getAssetAmtEnd() {
		return assetAmtEnd;
	}
	public void setAssetAmtEnd(BigDecimal assetAmtEnd) {
		this.assetAmtEnd = assetAmtEnd;
	}
}
