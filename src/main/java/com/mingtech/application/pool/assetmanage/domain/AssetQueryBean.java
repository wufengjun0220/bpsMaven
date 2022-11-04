package com.mingtech.application.pool.assetmanage.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 资产查询实体
 * @author Ju Nana
 * @version v1.0
 * @date 2021-10-14
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class AssetQueryBean{
	
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
    private String delFlag;//逻辑删除标记 D已删除 N未删除  -------特别注意：这里D表示不计算到额度表中，N表示计算到额度表中

    private Date startDate;//开始时间
    private Date endDate;//结束时间
    private List<String> assetTypeList;//资产类型
	
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

	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public List<String> getAssetTypeList() {
		return assetTypeList;
	}
	public void setAssetTypeList(List<String> assetTypeList) {
		this.assetTypeList = assetTypeList;
	}
	
	

}
