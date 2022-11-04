package com.mingtech.application.pool.creditmanage.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>融资业务用信流水登记历史</p>
 * @author h2
 * @date 2021年04月29日
 * @version 1.0
 * <p>修改记录</p>
 * h2   新建类     2021年04月29日
 */	
public class CreditRegisterHis{
	
	private String id;//组件
	private String apId;//资产池表主键
	private String bpsNo;//票据池协议编号
    private String busiId;//资产类表id
    private String busiNo;//客户资产池名称
    private String voucherType;//凭证类型-
	private String contractNo;//合同号
    private String isOnline;//证件号码
    private String busiType;//客户签约编号
    private String riskType;//资产编号-存账号或票据号
    private BigDecimal busiAmount;//业务金额(合同金额、借据金额)
    private BigDecimal occupyRatio;//占用比例-0.##-1
    private BigDecimal occupyAmount;//业务实际占用金额-上限=业务金额
    private BigDecimal occupyCredit;//实际占用额度
    private Date dueDt;//到期日
    private Date transDate;//交易日期(不含时分秒)
    private Date createDate;//创建日期(含时分秒)
    private Date updateDate;//更新日期(含时分秒)
    private String delFlag;//多级删除标记
    
	
	
	//已下字段只做参数传递或页面显示
	private String voucherTypeDesc;//凭证类型
	private String riskTypeDesc;//风险类型
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApId() {
		return apId;
	}
	public void setApId(String apId) {
		this.apId = apId;
	}
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getBusiId() {
		return busiId;
	}
	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}
	public String getBusiNo() {
		return busiNo;
	}
	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}
	public String getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public String getRiskType() {
		return riskType;
	}
	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}
	public BigDecimal getBusiAmount() {
		return busiAmount;
	}
	public void setBusiAmount(BigDecimal busiAmount) {
		this.busiAmount = busiAmount;
	}
	public BigDecimal getOccupyRatio() {
		return occupyRatio;
	}
	public void setOccupyRatio(BigDecimal occupyRatio) {
		this.occupyRatio = occupyRatio;
	}
	public BigDecimal getOccupyAmount() {
		return occupyAmount;
	}
	public void setOccupyAmount(BigDecimal occupyAmount) {
		this.occupyAmount = occupyAmount;
	}
	public BigDecimal getOccupyCredit() {
		return occupyCredit;
	}
	public void setOccupyCredit(BigDecimal occupyCredit) {
		this.occupyCredit = occupyCredit;
	}
	public Date getDueDt() {
		return dueDt;
	}
	public void setDueDt(Date dueDt) {
		this.dueDt = dueDt;
	}
	public Date getTransDate() {
		return transDate;
	}
	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getVoucherTypeDesc() {
		return voucherTypeDesc;
	}
	public void setVoucherTypeDesc(String voucherTypeDesc) {
		this.voucherTypeDesc = voucherTypeDesc;
	}
	public String getRiskTypeDesc() {
		return riskTypeDesc;
	}
	public void setRiskTypeDesc(String riskTypeDesc) {
		this.riskTypeDesc = riskTypeDesc;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	

    
}
