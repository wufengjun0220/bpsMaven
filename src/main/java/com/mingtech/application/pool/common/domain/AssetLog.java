package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 版权所有:(C)2013-2018 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: zhaoding
 * @描述: [AssetLog]资产池实体日志
 */
public class AssetLog {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private String id;
	private String assetid;
	
	private Date operDate;
	private String operUserId;
	private String operUserName;
	private String operType;
	private String desc;

	private BigDecimal assetLimitTotal;
	private BigDecimal assetLimitUsed;
	private BigDecimal assetLimitFree;
	private BigDecimal assetLimitFrzd;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getAssetid() {
		return assetid;
	}

	public void setAssetid(String assetid) {
		this.assetid = assetid;
	}

	public Date getOperDate() {
		return operDate;
	}

	public void setOperDate(Date operDate) {
		this.operDate = operDate;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOperUserId() {
		return operUserId;
	}

	public void setOperUserId(String operUserId) {
		this.operUserId = operUserId;
	}

	public String getOperUserName() {
		return operUserName;
	}

	public void setOperUserName(String operUserName) {
		this.operUserName = operUserName;
	}

	public BigDecimal getAssetLimitTotal() {
		return assetLimitTotal;
	}

	public void setAssetLimitTotal(BigDecimal assetLimitTotal) {
		this.assetLimitTotal = assetLimitTotal;
	}

	public BigDecimal getAssetLimitUsed() {
		return assetLimitUsed;
	}

	public void setAssetLimitUsed(BigDecimal assetLimitUsed) {
		this.assetLimitUsed = assetLimitUsed;
	}

	public BigDecimal getAssetLimitFree() {
		return assetLimitFree;
	}

	public void setAssetLimitFree(BigDecimal assetLimitFree) {
		this.assetLimitFree = assetLimitFree;
	}

	public BigDecimal getAssetLimitFrzd() {
		return assetLimitFrzd;
	}

	public void setAssetLimitFrzd(BigDecimal assetLimitFrzd) {
		this.assetLimitFrzd = assetLimitFrzd;
	}

}
