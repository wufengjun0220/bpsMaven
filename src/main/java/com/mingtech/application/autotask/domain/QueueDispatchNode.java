package com.mingtech.application.autotask.domain;

import java.util.Date;
import java.util.List;

/**
 * <p>队列分发业务节点配置实体</p>
 * @author 
 * @date 2021年06月03日
 * @version 1.0
 * <p>修改记录</p>
 *    新建类     2021年06月03日
 */	
public class QueueDispatchNode{
	
	private String id;//任务ID
	private String queueName;//队列名称
    private String queueNode;//队列分发节点
    private String queueNodeName;//队列分发节点名称
    private String deptId;//机构ID
    private String productId;//产品ID
	private String deptName;//机构名称
	private String createUserId;//创建用户id
	private String createUserName;//创建用户名称
	private Date createDate;//创建日期
	private String delFlag;//逻辑删除标记
	private String status;//状态-0新增、1启动、2停用

	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getQueueNodeName() {
		return queueNodeName;
	}
	public void setQueueNodeName(String queueNodeName) {
		this.queueNodeName = queueNodeName;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
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
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public String getQueueNode() {
		return queueNode;
	}
	public void setQueueNode(String queueNode) {
		this.queueNode = queueNode;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
