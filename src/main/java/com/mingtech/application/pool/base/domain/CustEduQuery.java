package com.mingtech.application.pool.base.domain;

import java.math.BigDecimal;

public class CustEduQuery {
	private String custName;    //客户名称
	private String plCommId;    //客户组织机构代码（页面展示票据池编号）
	private BigDecimal bdCrdtTotal;     //客户衍生总额度
	private BigDecimal bdCrdtUsed;      //已用额度
	private BigDecimal bdCrdtFree;      //剩余额度
	private BigDecimal crdtFrzd;        //冻结额度 	
	private String poolType;
	private String custnumber;//客户号
	
	//liuxiaodong add 20190221
	private BigDecimal lowerTotalEdu;//低风险额度
	private BigDecimal lowerEffTotalEdu;//低风险票据额度
	private BigDecimal heightEffTotalEdu;//高风险票据额度
	private BigDecimal bailAccount; //保证金额度
	private String custOrgcode;//页面展示组织机构代码
	
	//wufengjun 20190225
	private BigDecimal usedHighRiskAmount ;//已用高风险额度
	private BigDecimal usedLowRiskAmount ;//已用低风险额度
	private BigDecimal freeHighRiskAmount;//未用高风险额度
	private BigDecimal freeLowRiskAmount;//未用低风险额度
	private BigDecimal zeroEduAmount;//未产生额度票据总金额
	private BigDecimal bailAmount;//保证金余额 
	private BigDecimal bailAmountTotal;//保证金总额 
	
	private BigDecimal riskLowCreditTotalAmount;//低风险担保合同限额
	private BigDecimal riskLowCreditUsedAmount;//低风险担保合同已用额度
	private BigDecimal riskLowCreditFreeAmount;//低风险担保合同剩余额度
	
	private String isGroup;
	private String poolMode;
	private String poolModeName;//额度模式
	
	public String getPoolMode() {
		return poolMode;
	}
	public void setPoolMode(String poolMode) {
		this.poolMode = poolMode;
	}
	public String getPoolModeName() {
		String name = "";
		if("01".equals(this.poolMode)){
			name="总量模式";
		}else if("02".equals(this.poolMode)){
			name="期限配比";
		}
		return name;
	}
	public void setPoolModeName(String poolModeName) {
		this.poolModeName = poolModeName;
	}
	public BigDecimal getRiskLowCreditTotalAmount() {
		return riskLowCreditTotalAmount;
	}
	public void setRiskLowCreditTotalAmount(BigDecimal riskLowCreditTotalAmount) {
		this.riskLowCreditTotalAmount = riskLowCreditTotalAmount;
	}
	public BigDecimal getRiskLowCreditUsedAmount() {
		return riskLowCreditUsedAmount;
	}
	public void setRiskLowCreditUsedAmount(BigDecimal riskLowCreditUsedAmount) {
		this.riskLowCreditUsedAmount = riskLowCreditUsedAmount;
	}
	public BigDecimal getRiskLowCreditFreeAmount() {
		return riskLowCreditFreeAmount;
	}
	public void setRiskLowCreditFreeAmount(BigDecimal riskLowCreditFreeAmount) {
		this.riskLowCreditFreeAmount = riskLowCreditFreeAmount;
	}
	public BigDecimal getBailAmountTotal() {
		return bailAmountTotal;
	}
	public void setBailAmountTotal(BigDecimal bailAmountTotal) {
		this.bailAmountTotal = bailAmountTotal;
	}
	public String getCustnumber() {
		return custnumber;
	}
	public void setCustnumber(String custnumber) {
		this.custnumber = custnumber;
	}
	public BigDecimal getLowerTotalEdu() {
		return lowerTotalEdu;
	}
	public void setLowerTotalEdu(BigDecimal lowerTotalEdu) {
		this.lowerTotalEdu = lowerTotalEdu;
	}
	public BigDecimal getLowerEffTotalEdu() {
		return lowerEffTotalEdu;
	}
	public void setLowerEffTotalEdu(BigDecimal lowerEffTotalEdu) {
		this.lowerEffTotalEdu = lowerEffTotalEdu;
	}
	public BigDecimal getHeightEffTotalEdu() {
		return heightEffTotalEdu;
	}
	public void setHeightEffTotalEdu(BigDecimal heightEffTotalEdu) {
		this.heightEffTotalEdu = heightEffTotalEdu;
	}
	public BigDecimal getBailAccount() {
		return bailAccount;
	}
	public void setBailAccount(BigDecimal bailAccount) {
		this.bailAccount = bailAccount;
	}
	public BigDecimal getCrdtFrzd() {
		return crdtFrzd;
	}
	public void setCrdtFrzd(BigDecimal crdtFrzd) {
		this.crdtFrzd = crdtFrzd;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getPlCommId() {
		return plCommId;
	}
	public void setPlCommId(String plCommId) {
		this.plCommId = plCommId;
	}
	public BigDecimal getBdCrdtTotal() {
		return bdCrdtTotal;
	}
	public void setBdCrdtTotal(BigDecimal bdCrdtTotal) {
		this.bdCrdtTotal = bdCrdtTotal;
	}
	public BigDecimal getBdCrdtUsed() {
		return bdCrdtUsed;
	}
	public void setBdCrdtUsed(BigDecimal bdCrdtUsed) {
		this.bdCrdtUsed = bdCrdtUsed;
	}
	public BigDecimal getBdCrdtFree() {
		return bdCrdtFree;
	}
	public void setBdCrdtFree(BigDecimal bdCrdtFree) {
		this.bdCrdtFree = bdCrdtFree;
	}
	
	public CustEduQuery(){}
	public String getPoolType() {
		return poolType;
	}
	public void setPoolType(String poolType) {
		this.poolType = poolType;
	}
	public BigDecimal getUsedHighRiskAmount() {
		return usedHighRiskAmount;
	}
	public void setUsedHighRiskAmount(BigDecimal usedHighRiskAmount) {
		this.usedHighRiskAmount = usedHighRiskAmount;
	}
	public BigDecimal getUsedLowRiskAmount() {
		return usedLowRiskAmount;
	}
	public void setUsedLowRiskAmount(BigDecimal usedLowRiskAmount) {
		this.usedLowRiskAmount = usedLowRiskAmount;
	}
	public BigDecimal getFreeHighRiskAmount() {
		return freeHighRiskAmount;
	}
	public void setFreeHighRiskAmount(BigDecimal freeHighRiskAmount) {
		this.freeHighRiskAmount = freeHighRiskAmount;
	}
	public BigDecimal getFreeLowRiskAmount() {
		return freeLowRiskAmount;
	}
	public void setFreeLowRiskAmount(BigDecimal freeLowRiskAmount) {
		this.freeLowRiskAmount = freeLowRiskAmount;
	}
	public BigDecimal getZeroEduAmount() {
		return zeroEduAmount;
	}
	public void setZeroEduAmount(BigDecimal zeroEduAmount) {
		this.zeroEduAmount = zeroEduAmount;
	}
	public BigDecimal getBailAmount() {
		return bailAmount;
	}
	public void setBailAmount(BigDecimal bailAmount) {
		this.bailAmount = bailAmount;
	}
	public String getIsGroup() {
		return isGroup;
	}
	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}
	public String getCustOrgcode() {
		return custOrgcode;
	}
	public void setCustOrgcode(String custOrgcode) {
		this.custOrgcode = custOrgcode;
	}
	
	
}
