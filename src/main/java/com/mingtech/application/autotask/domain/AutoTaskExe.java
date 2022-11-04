package com.mingtech.application.autotask.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * <p>自动任务执行流水实体</p>
 * @author h2
 * @date 2021年04月29日
 * @version 1.0
 * <p>修改记录</p>
 * h2   新建类     2021年04月29日
 */	
public class AutoTaskExe{
	
	private String id;//任务ID
	private String memberCode;//会员编码
	private String taskId;//务ID
	private String treeCode;//树节点
	private int resetCout;//重复处理次数
	private String reqParams;//请求参数
	private String status;//0未启动、1处理完成 2排队中、3处理中、4处理失败
	private String proceStatus;//流程状态 20210602新增 流程状态--0未处理、1处理完成、3处理中、4处理失败
	private String resultDesc;//处理结果描述
	private String productId;//业务类型ID
	private String busiId;//原业务ID
	private Date startDate;//开始时间
	private Date endDate;//结束时间
	private String redirectUrl;//原业务详情访问页面路由地址
	private Date createDate;//创建日期
	private int errorCount=0;//错误次数
	private String delFlag;//逻辑删除标记 D已删除 N未删除
	//20210617新增字段
	private String batchNo;//批次号
	private String bpsNo;  //票据池编号
	private String bpsName; //票据池名称
	private String deptId; //操作机构
	private String deptName; //操作机构名称


	//已下字段只做参数传递或页面显示
	private String statusDesc;//状态中文

	public String getId() {
		return id;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getResetCout() {
		return resetCout;
	}

	public void setResetCout(int resetCout) {
		this.resetCout = resetCout;
	}

	public String getReqParams() {
		return reqParams;
	}

	public void setReqParams(String reqParams) {
		this.reqParams = reqParams;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getBusiId() {
		return busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
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

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getStatusDesc() {
		String statusDesc = "";
		if("0".equals(this.getStatus() )|| StringUtils.isEmpty(this.getStatus())){
			statusDesc = "未启动";
		}else if("1".equals(this.getStatus())){
			statusDesc = "处理完成";
		}else if("2".equals(this.getStatus())){
			statusDesc = "排队中";
		}else if("3".equals(this.getStatus())){
			statusDesc = "处理中";
		}else if("-".equals(this.getStatus())){
			statusDesc = "处理失败";
		}
		return statusDesc;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getProceStatus() {
		return proceStatus;
	}

	public void setProceStatus(String proceStatus) {
		this.proceStatus = proceStatus;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getBpsNo() {
		return bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
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
}
