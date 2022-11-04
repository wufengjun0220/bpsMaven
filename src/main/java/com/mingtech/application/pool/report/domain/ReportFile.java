package com.mingtech.application.pool.report.domain;

import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.pool.common.PoolComm;

/**
 * 报表文件
 * @author h2
 *
 */
public class ReportFile implements java.io.Serializable{
	
	private String id;//主键
	private String templateId;//通用模板id
	private String memberCode;//会员编码
	private String reportSeqNo;//报表序号
	private String reportName; //报表模板名称
	private String fileName; //报表文件名称
	private String filePath;//模板文件存放相对路径含文件名
	private String qryCondition;//查询条件
	private String status;//状态:0未生成、1已生成、2生成失败、3正在生成
	private String timeModel;//时间模式
	private String timeSelect;//时间选择
	private Date createTime;//创建时间
	private Date finishTime;//报表生成时间
	private String userId;//最后维护用户ID
	private String userNm;//最后维护用户名称
	private String deptId;//上传机构ID
	private String deptName;//上传机构名称
	
	//以下字段只做页面显示或参数传递
	private String statusDesc;//状态描述
	private Date startDate;//开始时间
	private Date endDate;//结束时间
	private String timeModelName;//时间模式


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getQryCondition() {
		return qryCondition;
	}

	public void setQryCondition(String qryCondition) {
		this.qryCondition = qryCondition;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
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

	public String getReportSeqNo() {
		return reportSeqNo;
	}

	public void setReportSeqNo(String reportSeqNo) {
		this.reportSeqNo = reportSeqNo;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getTimeModel() {
		return timeModel;
	}

	public void setTimeModel(String timeModel) {
		this.timeModel = timeModel;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTimeSelect() {
		return timeSelect;
	}

	public void setTimeSelect(String timeSelect) {
		this.timeSelect = timeSelect;
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

	public String getTimeModelName() {
		timeModelName ="";
		String[] times = this.getTimeSelect().split(",");
		if (PoolComm.TIME_MODEL_01.equals(this.getTimeModel())){
			timeModelName = "本"+times[0]+"年";
		}
		if (PoolComm.TIME_MODEL_02.equals(this.getTimeModel())){
			timeModelName = "本年"+times[0]+"月";
		}
		if (PoolComm.TIME_MODEL_03.equals(this.getTimeModel())){
			timeModelName = times[0]+"年";
		}
		if (PoolComm.TIME_MODEL_04.equals(this.getTimeModel())){
			timeModelName = times[0]+"年"+times[1]+"月";
		}
		if (PoolComm.TIME_MODEL_05.equals(this.getTimeModel())){
			timeModelName = times[0]+"年"+times[1]+"月至"+times[2]+"年"+times[3]+"月";
		}
		if (PoolComm.TIME_MODEL_06.equals(this.getTimeModel())){
			timeModelName = "本"+times[0]+"年"+times[1]+"月至本"+times[2]+"年"+times[3]+"月";
		}
		return timeModelName;
	}

	public void setTimeModelName(String timeModelName) {
		this.timeModelName = timeModelName;
	}
}