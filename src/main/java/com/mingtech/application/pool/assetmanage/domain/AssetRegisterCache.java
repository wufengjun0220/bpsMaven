package com.mingtech.application.pool.assetmanage.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产登记临时缓存表
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-7
 * @copyright 北明明润（北京）科技有限责任公司
 */

public class AssetRegisterCache implements java.io.Serializable {


	private static final long serialVersionUID = 3024044732127710497L;


	private String id;            //主键                                              
	private String apId;          //资产池PED_ASSET_POOL表ID                           
	private String atId;          //资产类PED_ASSET_TYPE表ID                           
	private String custPoolName;  //客户资产池名称                                        
	private String certType;      //证件类型:01组织机构代码、02统一授信编码、03客户号                   
	private String certCode;      //证件号                                            
	private String bpsNo;         //客户票据池编号                                        
	private String assetNo;       //资产编号-存账号或票据号                                   
	private String assetType;     //资产类-ED_10低风险票据、ED_11高风险票据、ED_21活期保证金、ED_22定期保证金
	private String riskType;      //风险类型-低风险FX_01、高风险FX_02、不在风险名单FX_03             
	private BigDecimal assetAmount;   //资产金额                                                               
	private Date assetDueDt;//资产到期日
    private Date assetDelayDueDt;//资产延迟到期日
	private Date transDate;       //交易日期(不含时分秒)                                    
	private Date createDate;      //创建日期(含时分秒)                                     
	private Date updateDate;      //更新日期(含时分秒)                                     
	private String flowNo;        //流水号  

	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApId() {
		return this.apId;
	}

	public void setApId(String apId) {
		this.apId = apId;
	}

	public String getAtId() {
		return this.atId;
	}

	public void setAtId(String atId) {
		this.atId = atId;
	}

	public String getCustPoolName() {
		return this.custPoolName;
	}

	public void setCustPoolName(String custPoolName) {
		this.custPoolName = custPoolName;
	}

	public String getCertType() {
		return this.certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertCode() {
		return this.certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getAssetNo() {
		return this.assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public String getAssetType() {
		return this.assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getRiskType() {
		return this.riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public BigDecimal getAssetAmount() {
		return this.assetAmount;
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
		return this.transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getFlowNo() {
		return this.flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

}