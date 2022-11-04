package com.mingtech.application.pool.assetmanage.domain;

import java.util.Date;

/**
 * <p>资产分类实体</p>
 * @author zjt
 * @date 2021年05月19日
 * @version 1.0
 */
public class AssetTypeManage {
	
	private String id;//任务ID
	private String assetType;//资产类型：ED_10低风险票据、ED_20高风险票据、ED_21活期保证金、ED_22定期保证金
	private String riskType;//风险类型-低风险FX_01、高风险FX_02、不在风险名单FX_03
	private String amountType;//金额类型：AMT01账户余额、AMT02票面金额
	private String duedateType;//到日期类型:1当天、2票据到期日
	private String holidayDelayType;//节假日顺延类型:0不顺延、1节假日
	private int assignDelayDay;//设定顺延天数
	private String updateUserId;//更新用户ID
	private String updateUserName;//更新用户名称
	private Date updateDate;//更新日期
	private String delFlag;//逻辑删除标记-N未删除、D已删除


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public String getDuedateType() {
		return duedateType;
	}

	public void setDuedateType(String duedateType) {
		this.duedateType = duedateType;
	}

	public String getHolidayDelayType() {
		return holidayDelayType;
	}

	public void setHolidayDelayType(String holidayDelayType) {
		this.holidayDelayType = holidayDelayType;
	}

	public int getAssignDelayDay() {
		return assignDelayDay;
	}

	public void setAssignDelayDay(int assignDelayDay) {
		this.assignDelayDay = assignDelayDay;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getUpdateUserName() {
		return updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}
}
