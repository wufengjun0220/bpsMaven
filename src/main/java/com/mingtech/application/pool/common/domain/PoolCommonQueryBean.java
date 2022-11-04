package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 票据池通用查询Bean
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-24
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class PoolCommonQueryBean {
	
	/** 基础信息 **/
	private String billNo;// 票据号码
	private String billMedia;// 票据介质
	private String billType;// 票据类型
	private Date isseDtStart;// 出票日-开始
	private Date isseDtEnd;// 出票日- 结束
	private Date dueDtStart;// 到期日- 开始
	private Date dueDtEnd;//到期日-结束
	private BigDecimal billAmtStart;//票面金额开始
	private BigDecimal billAmtEnd;//票面金额结束
	
	/** 出票人信息 **/
	private String drwrName;// 出票人名称
	private String drwrAcctNo;// 出票人账号
	private String drwrBankNo;// 出票人开户行行号
	private String drwrBankName;// 出票人开户行名称
	
	/** 收款人信息 **/
	private String pyeeName;// 收款人名称
	private String pyeeAcctNo;// 收款人账号
	private String pyeeBankNo;// 收款人开户行行号
	private String pyeeBankName;// 收款人开户行名称
	
	/** 承兑人信息 **/
	private String acptname;// 承兑人名称
	private String acptAcctNo;// 承兑人账号
	private String acptBankNo;// 承兑人开户行行号
	private String acptBankName;// 承兑人开户行名称
	private String acptAddress; // 承兑人地址
	
	
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public String getBillMedia() {
		return billMedia;
	}
	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public Date getIsseDtStart() {
		return isseDtStart;
	}
	public void setIsseDtStart(Date isseDtStart) {
		this.isseDtStart = isseDtStart;
	}
	public Date getIsseDtEnd() {
		return isseDtEnd;
	}
	public void setIsseDtEnd(Date isseDtEnd) {
		this.isseDtEnd = isseDtEnd;
	}
	public Date getDueDtStart() {
		return dueDtStart;
	}
	public void setDueDtStart(Date dueDtStart) {
		this.dueDtStart = dueDtStart;
	}
	public Date getDueDtEnd() {
		return dueDtEnd;
	}
	public void setDueDtEnd(Date dueDtEnd) {
		this.dueDtEnd = dueDtEnd;
	}
	public BigDecimal getBillAmtStart() {
		return billAmtStart;
	}
	public void setBillAmtStart(BigDecimal billAmtStart) {
		this.billAmtStart = billAmtStart;
	}
	public BigDecimal getBillAmtEnd() {
		return billAmtEnd;
	}
	public void setBillAmtEnd(BigDecimal billAmtEnd) {
		this.billAmtEnd = billAmtEnd;
	}
	public String getDrwrName() {
		return drwrName;
	}
	public void setDrwrName(String drwrName) {
		this.drwrName = drwrName;
	}
	public String getDrwrAcctNo() {
		return drwrAcctNo;
	}
	public void setDrwrAcctNo(String drwrAcctNo) {
		this.drwrAcctNo = drwrAcctNo;
	}
	public String getDrwrBankNo() {
		return drwrBankNo;
	}
	public void setDrwrBankNo(String drwrBankNo) {
		this.drwrBankNo = drwrBankNo;
	}
	public String getDrwrBankName() {
		return drwrBankName;
	}
	public void setDrwrBankName(String drwrBankName) {
		this.drwrBankName = drwrBankName;
	}
	public String getPyeeName() {
		return pyeeName;
	}
	public void setPyeeName(String pyeeName) {
		this.pyeeName = pyeeName;
	}
	public String getPyeeAcctNo() {
		return pyeeAcctNo;
	}
	public void setPyeeAcctNo(String pyeeAcctNo) {
		this.pyeeAcctNo = pyeeAcctNo;
	}
	public String getPyeeBankNo() {
		return pyeeBankNo;
	}
	public void setPyeeBankNo(String pyeeBankNo) {
		this.pyeeBankNo = pyeeBankNo;
	}
	public String getPyeeBankName() {
		return pyeeBankName;
	}
	public void setPyeeBankName(String pyeeBankName) {
		this.pyeeBankName = pyeeBankName;
	}
	public String getAcptname() {
		return acptname;
	}
	public void setAcptname(String acptname) {
		this.acptname = acptname;
	}
	public String getAcptAcctNo() {
		return acptAcctNo;
	}
	public void setAcptAcctNo(String acptAcctNo) {
		this.acptAcctNo = acptAcctNo;
	}
	public String getAcptAddress() {
		return acptAddress;
	}
	public void setAcptAddress(String acptAddress) {
		this.acptAddress = acptAddress;
	}
	public String getAcptBankNo() {
		return acptBankNo;
	}
	public void setAcptBankNo(String acptBankNo) {
		this.acptBankNo = acptBankNo;
	}
	public String getAcptBankName() {
		return acptBankName;
	}
	public void setAcptBankName(String acptBankName) {
		this.acptBankName = acptBankName;
	}
	

}
