package com.mingtech.application.audit.domain;

import java.util.Date;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: yufei
 * @日期: Jun 17, 2009 6:02:07 PM
 * @描述: [ApproveDto]审批意见对象，保存所有工作流程中审批的详细信息
 */
public class ApproveDto implements java.io.Serializable{

	// Fields
	private String approveId;// 主键ID
	private String bussinessId;// 业务主键ID
	private String approveUserNm;// 审批人名称
	private String approveUserId;//审批人id
	private String approveDept;    //审批人所在机构名称
	private String approveDeptId;//审批人所在机构ID
	private String approveFlag;// 审批标识
	private String approveComment;// 审批内容
	private String processId;//审批受理申请(流程)id
	private Date approveDate;// 审批日期
	private String approveRole;//审批人角色 20170927 增加
	private String nodeNum;//审批节点编号
	private String nextNodeNum;//下一岗审批节点编号,-1说明从当前节点终止该流程
	
	
	/**以下字段只作为页面显示或参数传递**/
	private String approveFlagDes;//审批结果描述
	
	public String getApproveId() {
		return approveId;
	}
	public void setApproveId(String approveId) {
		this.approveId = approveId;
	}
	public String getBussinessId() {
		return bussinessId;
	}
	public void setBussinessId(String bussinessId) {
		this.bussinessId = bussinessId;
	}
	public String getApproveUserNm() {
		return approveUserNm;
	}
	public void setApproveUserNm(String approveUserNm) {
		this.approveUserNm = approveUserNm;
	}
	public String getApproveUserId() {
		return approveUserId;
	}
	public void setApproveUserId(String approveUserId) {
		this.approveUserId = approveUserId;
	}
	public String getApproveDept() {
		return approveDept;
	}
	public void setApproveDept(String approveDept) {
		this.approveDept = approveDept;
	}
	public String getApproveDeptId() {
		return approveDeptId;
	}
	public void setApproveDeptId(String approveDeptId) {
		this.approveDeptId = approveDeptId;
	}
	public String getApproveFlag() {
		return approveFlag;
	}
	public void setApproveFlag(String approveFlag) {
		this.approveFlag = approveFlag;
	}
	public String getApproveComment() {
		return approveComment;
	}
	public void setApproveComment(String approveComment) {
		this.approveComment = approveComment;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public Date getApproveDate() {
		return approveDate;
	}
	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}
	public String getApproveRole() {
		return approveRole;
	}
	public void setApproveRole(String approveRole) {
		this.approveRole = approveRole;
	}
	public String getNodeNum() {
		return nodeNum;
	}
	public void setNodeNum(String nodeNum) {
		this.nodeNum = nodeNum;
	}
	public String getNextNodeNum() {
		return nextNodeNum;
	}
	public void setNextNodeNum(String nextNodeNum) {
		this.nextNodeNum = nextNodeNum;
	}
	public String getApproveFlagDes() {
		if("1".equals(this.approveFlag)){
			approveFlagDes="同意-1";
		}else if("0".equals(this.approveFlag)){
			approveFlagDes="不同意-0";
		}else if("-1".equals(this.approveFlag)){
			approveFlagDes="终止--1";
		}
		return approveFlagDes;
	}
	
}