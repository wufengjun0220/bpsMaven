package com.mingtech.application.pool.report.domain;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.framework.common.util.StringUtil;

import java.util.Date;

public class RReportModel implements java.io.Serializable {

	// Fields
	private String id;
	private String reportName;//模板名称
	private String busiType;//业务类型
	private String fileName;//文件名称
	private String filePath;//文件路径
	private String timeModel;//时间模式
	private String timeSelect;//时间选择
	private Date uploadDate;//上传日期
	private Date createDate;//报表生成日期
	private String userId;//上传用户ID
	private String userNm;//上传用户名称
	private String deptId;//上传机构ID
	private String deptName;//上传机构名称
	private String status;//报表状态 :0未生成、1已生成、2生成失败、3正在生成
	private String remark;//备注
	//页面使用
	private Date startDate;//开始时间
	private Date endDate;//结束时间
	private String busiTypeName;//业务类型

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTimeModel() {
		return timeModel;
	}

	public void setTimeModel(String timeModel) {
		this.timeModel = timeModel;
	}

	public String getTimeSelect() {
		return timeSelect;
	}

	public void setTimeSelect(String timeSelect) {
		this.timeSelect = timeSelect;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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

	public String getBusiTypeName() {
		busiTypeName = "";
		String[] types = StringUtil.split(this.busiType,",");
		for (int i=0;i<types.length;i++){
			String type = types[i];
			if (PoolComm.BUSI_TYPE_01.equals(type)) {
				busiTypeName = busiTypeName+"票据池签约客户数&";
			}
			if (PoolComm.BUSI_TYPE_02.equals(type)) {
				busiTypeName = busiTypeName+"票据池总签约客户数&";
			}
			if (PoolComm.BUSI_TYPE_03.equals(type)) {
				busiTypeName = busiTypeName+"票据池线下银承发生金额&";
			}if (PoolComm.BUSI_TYPE_04.equals(type)) {
				busiTypeName = busiTypeName+"票据池线下流贷发生金额&";
			}if (PoolComm.BUSI_TYPE_05.equals(type)) {
				busiTypeName = busiTypeName+"票据池线下保函发生金额&";
			}if (PoolComm.BUSI_TYPE_06.equals(type)) {
				busiTypeName = busiTypeName+"票据池线下信用证发生金额&";
			}if (PoolComm.BUSI_TYPE_07.equals(type)) {
				busiTypeName = busiTypeName+"票据池线上银承发生金额&";
			}if (PoolComm.BUSI_TYPE_08.equals(type)) {
				busiTypeName = busiTypeName+"票据池线上流贷发生金额&";
			}if (PoolComm.BUSI_TYPE_09.equals(type)) {
				busiTypeName = busiTypeName+"票据池线下融资金额&";
			}if (PoolComm.BUSI_TYPE_10.equals(type)) {
				busiTypeName = busiTypeName+"票据池线上融资金额&";
			}
		}
		return busiTypeName.substring(0,busiTypeName.length()-1);
	}

	public void setBusiTypeName(String busiTypeName) {
		this.busiTypeName = busiTypeName;
	}
}