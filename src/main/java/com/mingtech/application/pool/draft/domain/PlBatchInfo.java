package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 出池后续处理票据批次信息表
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-5-27
 */

public class PlBatchInfo implements java.io.Serializable {

	private static final long serialVersionUID = 7718547944644323116L;
	private String id;                 //主键ID          
	private String bpsNo;              //票据池编号      
	private String bpsName;            //票据池名称      
	private String custNo;             //客户号          
	private String custName;           //客户名称        
	private String doBatchNo;          //操作批次号      
	private String ESign;              //电子签名        
	private String outMode;            //出池模式          CCMS_01:质押出池     CCMS_02:贴现出池     CCMS_03:背书出池
	private String endorseeAcctNo;     //被背书人账号    
	private String endorseeName;       //被背书人        
	private String endorseeOpenBank;   //被背书行        
	private String remark;             //备注            
	private String discountInBankCode; //贴入行行号      
	private String discountInBankName; //贴入行名称      
	private String discountMode;       //贴现方式        
	private String onlineSettleFlag;   //清算标识        
	private String unendorseFlag;      //禁止背书标识    
	private BigDecimal discountIntRate;    //贴现利率        
	private Date discountDate;         //贴现日期        
	private BigDecimal redeemIntRate;      //赎回利率        
	private Date redeemOpenDate;       //赎回开放日      
	private Date redeemEndDate;        //赎回截止日      
	private String enterAcctNo;        //入账账号        
	private String enterAcctName;      //入账户名        
	private String enterBankCode;      //入账行号        
	private String invoiceNo;          //发票号          
	private String contractNo;         //合同编号        
	private String pledgeeType;        //质权类型        
	private String pledgeeAcctNo;      //质权人账号      
	private String pledgeeName;        //质权人名称      
	private String pledgeeOpenBank;    //质权人开户行行号
	private Date outTime;              //出池时间        
	private String doFlag;             //处理标识    0：未处理    1：已处理

	private String discountRateMade;             //贴现利息方式
	private String thirdPayRate;             //第三方支付比例
	private BigDecimal rateAmt;             //实付利息金额
	
	
	
	// Constructors

	/** default constructor */
	public PlBatchInfo() {
	}

	/** full constructor */
	public PlBatchInfo(String bpsNo, String bpsName, String custNo,
			String custName, String doBatchNo, String ESign, String outMode,
			String endorseeAcctNo, String endorseeName,
			String endorseeOpenBank, String remark, String discountInBankCode,
			String discountInBankName, String discountMode,
			String onlineSettleFlag, String unendorseFlag,
			Date discountDate,
			Date redeemOpenDate, Date redeemEndDate, String enterAcctNo,
			String enterAcctName, String enterBankCode, String invoiceNo,
			String contractNo, String pledgeeType, String pledgeeAcctNo,
			String pledgeeName, String pledgeeOpenBank, Date outTime,
			String doFlag) {
		this.bpsNo = bpsNo;
		this.bpsName = bpsName;
		this.custNo = custNo;
		this.custName = custName;
		this.doBatchNo = doBatchNo;
		this.ESign = ESign;
		this.outMode = outMode;
		this.endorseeAcctNo = endorseeAcctNo;
		this.endorseeName = endorseeName;
		this.endorseeOpenBank = endorseeOpenBank;
		this.remark = remark;
		this.discountInBankCode = discountInBankCode;
		this.discountInBankName = discountInBankName;
		this.discountMode = discountMode;
		this.onlineSettleFlag = onlineSettleFlag;
		this.unendorseFlag = unendorseFlag;
		this.discountDate = discountDate;
		this.redeemOpenDate = redeemOpenDate;
		this.redeemEndDate = redeemEndDate;
		this.enterAcctNo = enterAcctNo;
		this.enterAcctName = enterAcctName;
		this.enterBankCode = enterBankCode;
		this.invoiceNo = invoiceNo;
		this.contractNo = contractNo;
		this.pledgeeType = pledgeeType;
		this.pledgeeAcctNo = pledgeeAcctNo;
		this.pledgeeName = pledgeeName;
		this.pledgeeOpenBank = pledgeeOpenBank;
		this.outTime = outTime;
		this.doFlag = doFlag;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDoBatchNo() {
		return this.doBatchNo;
	}

	public void setDoBatchNo(String doBatchNo) {
		this.doBatchNo = doBatchNo;
	}

	public String getESign() {
		return this.ESign;
	}

	public void setESign(String ESign) {
		this.ESign = ESign;
	}

	public String getOutMode() {
		return this.outMode;
	}

	public void setOutMode(String outMode) {
		this.outMode = outMode;
	}

	public String getEndorseeAcctNo() {
		return this.endorseeAcctNo;
	}

	public void setEndorseeAcctNo(String endorseeAcctNo) {
		this.endorseeAcctNo = endorseeAcctNo;
	}

	public String getEndorseeName() {
		return this.endorseeName;
	}

	public void setEndorseeName(String endorseeName) {
		this.endorseeName = endorseeName;
	}

	public String getEndorseeOpenBank() {
		return this.endorseeOpenBank;
	}

	public void setEndorseeOpenBank(String endorseeOpenBank) {
		this.endorseeOpenBank = endorseeOpenBank;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDiscountInBankCode() {
		return this.discountInBankCode;
	}

	public void setDiscountInBankCode(String discountInBankCode) {
		this.discountInBankCode = discountInBankCode;
	}

	public String getDiscountInBankName() {
		return this.discountInBankName;
	}

	public void setDiscountInBankName(String discountInBankName) {
		this.discountInBankName = discountInBankName;
	}

	public String getDiscountMode() {
		return this.discountMode;
	}

	public void setDiscountMode(String discountMode) {
		this.discountMode = discountMode;
	}

	public String getOnlineSettleFlag() {
		return this.onlineSettleFlag;
	}

	public void setOnlineSettleFlag(String onlineSettleFlag) {
		this.onlineSettleFlag = onlineSettleFlag;
	}

	public String getUnendorseFlag() {
		return this.unendorseFlag;
	}

	public void setUnendorseFlag(String unendorseFlag) {
		this.unendorseFlag = unendorseFlag;
	}

	public BigDecimal getDiscountIntRate() {
		return this.discountIntRate;
	}

	public void setDiscountIntRate(BigDecimal discountIntRate) {
		this.discountIntRate = discountIntRate;
	}

	public Date getDiscountDate() {
		return this.discountDate;
	}

	public void setDiscountDate(Date discountDate) {
		this.discountDate = discountDate;
	}

	public BigDecimal getRedeemIntRate() {
		return this.redeemIntRate;
	}

	public void setRedeemIntRate(BigDecimal redeemIntRate) {
		this.redeemIntRate = redeemIntRate;
	}

	public Date getRedeemOpenDate() {
		return this.redeemOpenDate;
	}

	public void setRedeemOpenDate(Date redeemOpenDate) {
		this.redeemOpenDate = redeemOpenDate;
	}

	public Date getRedeemEndDate() {
		return this.redeemEndDate;
	}

	public void setRedeemEndDate(Date redeemEndDate) {
		this.redeemEndDate = redeemEndDate;
	}

	public String getEnterAcctNo() {
		return this.enterAcctNo;
	}

	public void setEnterAcctNo(String enterAcctNo) {
		this.enterAcctNo = enterAcctNo;
	}

	public String getEnterAcctName() {
		return this.enterAcctName;
	}

	public void setEnterAcctName(String enterAcctName) {
		this.enterAcctName = enterAcctName;
	}

	public String getEnterBankCode() {
		return this.enterBankCode;
	}

	public void setEnterBankCode(String enterBankCode) {
		this.enterBankCode = enterBankCode;
	}

	public String getInvoiceNo() {
		return this.invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getPledgeeType() {
		return this.pledgeeType;
	}

	public void setPledgeeType(String pledgeeType) {
		this.pledgeeType = pledgeeType;
	}

	public String getPledgeeAcctNo() {
		return this.pledgeeAcctNo;
	}

	public void setPledgeeAcctNo(String pledgeeAcctNo) {
		this.pledgeeAcctNo = pledgeeAcctNo;
	}

	public String getPledgeeName() {
		return this.pledgeeName;
	}

	public void setPledgeeName(String pledgeeName) {
		this.pledgeeName = pledgeeName;
	}

	public String getPledgeeOpenBank() {
		return this.pledgeeOpenBank;
	}

	public void setPledgeeOpenBank(String pledgeeOpenBank) {
		this.pledgeeOpenBank = pledgeeOpenBank;
	}

	public Date getOutTime() {
		return this.outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}

	public String getDoFlag() {
		return this.doFlag;
	}

	public void setDoFlag(String doFlag) {
		this.doFlag = doFlag;
	}

	public String getDiscountRateMade() {
		return discountRateMade;
	}

	public void setDiscountRateMade(String discountRateMade) {
		this.discountRateMade = discountRateMade;
	}

	public String getThirdPayRate() {
		return thirdPayRate;
	}

	public void setThirdPayRate(String thirdPayRate) {
		this.thirdPayRate = thirdPayRate;
	}

	public BigDecimal getRateAmt() {
		return rateAmt;
	}

	public void setRateAmt(BigDecimal rateAmt) {
		this.rateAmt = rateAmt;
	}

	

}