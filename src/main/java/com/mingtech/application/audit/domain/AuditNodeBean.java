package com.mingtech.application.audit.domain;

import java.util.Date;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: ice
* @日期: 2019-07-19 下午02:25:05
* @描述: [AuditNodeBean]审批节点参数定义
*/
public class AuditNodeBean implements java.io.Serializable{

	private String nodeType;//节点类型:1自动、0人工
	private String nodeName;//岗位名称
	private String optRole;//执行角色
	private String nodeNum;//执行序号
	private String auditUserNm;//审批人名称
	private Date auditDate;//审批时间
	private String auditResult;//审批结果:1通过、0驳回
	private String nextNodeNum;//下一岗节点编号
	
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getOptRole() {
		return optRole;
	}
	public void setOptRole(String optRole) {
		this.optRole = optRole;
	}
	public String getNodeNum() {
		return nodeNum;
	}
	public void setNodeNum(String nodeNum) {
		this.nodeNum = nodeNum;
	}
	public String getAuditUserNm() {
		return auditUserNm;
	}
	public void setAuditUserNm(String auditUserNm) {
		this.auditUserNm = auditUserNm;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}
	public String getAuditResult() {
		return auditResult;
	}
	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}
	public String getNextNodeNum() {
		return nextNodeNum;
	}
	public void setNextNodeNum(String nextNodeNum) {
		this.nextNodeNum = nextNodeNum;
	}
}