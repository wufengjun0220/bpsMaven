package com.mingtech.application.pool.assetmanage.domain;

import java.util.Date;

/**
 * <p>资产分类实体</p>
 * @author zjt
 * @date 2021年05月19日
 * @version 1.0
 */
public class AssetTypeManageHis {
	
	private String id;//任务ID
	private String  atManageId;//资产分类管理表ID
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


	public String getAtManageId() {
		return atManageId;
	}

	public void setAtManageId(String atManageId) {
		this.atManageId = atManageId;
	}
}
