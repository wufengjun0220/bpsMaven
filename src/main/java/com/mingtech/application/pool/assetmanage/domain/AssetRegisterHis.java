package com.mingtech.application.pool.assetmanage.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>资产登记历史流水实体</p>
 * @author h2
 * @date 2021年04月29日
 * @version 1.0
 * <p>修改记录</p>
 * h2   新建类     2021年04月29日
 */	
public class AssetRegisterHis{
	
	private String id;//组件
	private String apId;//资产池表主键
    private String atId;//资产类表id
    private String custPoolName;//客户资产池名称
    private String certType;//证件类型:01组织机构代码、02统一授信编码、03客户号
    private String certCode;//证件号码
    private String bpsNo;//客户签约编号
    private String assetNo;//资产编号-存账号或票据号
    private String assetType;//资产类型-ED_10低风险票据、ED_20高风险票据、ED_21活期保证金、ED_22定期保证金
    private String riskType;//风险类型-低风险FX_01、高风险FX_02、不在风险名单FX_03
    private BigDecimal assetAmount;//资金金额
    private Date assetDueDt;//资产到期日
    private Date assetDelayDueDt;//资产延迟到期日
    private String stockOutType;//出库类型-01支取、02到期
    private Date transDate;//交易日期(不含时分秒)
    private Date createDate;//创建日期(含时分秒)
    private Date updateDate;//更新日期(含时分秒)
    private String delFlag;//多级删除标记
    
	
	//已下字段只做参数传递或页面显示
	private String assetTypeDesc;//状态中文
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
	public String getAtId() {
		return atId;
	}
	public void setAtId(String atId) {
		this.atId = atId;
	}
	public String getCustPoolName() {
		return custPoolName;
	}
	public void setCustPoolName(String custPoolName) {
		this.custPoolName = custPoolName;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}
	public String getCertCode() {
		return certCode;
	}
	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}
	
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getAssetNo() {
		return assetNo;
	}
	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getRiskType() {
		return riskType;
	}
	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}
	public BigDecimal getAssetAmount() {
		return assetAmount;
	}
	public void setAssetAmount(BigDecimal assetAmount) {
		this.assetAmount = assetAmount;
	}
	public Date getAssetDueDt() {
		return assetDueDt;
	}
	public void setAssetDueDt(Date assetDueDt) {
		this.assetDueDt = assetDueDt;
	}
	public Date getAssetDelayDueDt() {
		return assetDelayDueDt;
	}
	public void setAssetDelayDueDt(Date assetDelayDueDt) {
		this.assetDelayDueDt = assetDelayDueDt;
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

	public String getAssetTypeDesc() {
		return assetTypeDesc;
	}
	
	public String getRiskTypeDesc() {
		return riskTypeDesc;
	}
	public String getStockOutType() {
		return stockOutType;
	}
	public void setStockOutType(String stockOutType) {
		this.stockOutType = stockOutType;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	

    
}
