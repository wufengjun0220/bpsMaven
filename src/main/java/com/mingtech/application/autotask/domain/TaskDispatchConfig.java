package com.mingtech.application.autotask.domain;

import java.util.Date;
import java.util.List;

/**
 * <p>自动任务调度配置实体</p>
 * @author h2
 * @date 2021年04月29日
 * @version 1.0
 * <p>修改记录</p>
 * h2   新建类     2021年04月29日
 */	
public class TaskDispatchConfig{
	
	private String id;//任务ID
	private String parentId;//父任务ID
	private String memberCode;//会员编码
	private String taskName;//任务名称
	private String taskType;//任务类型
	private String taskNo;//任务编号
	private String taskDesc;//任务描述
	private String status;//状态-0新增、1启动、2停用
	private String className;//执行的具体类
	private String sleepTime;//延迟时间-毫秒
	private String waiteTime;//等待时间-分
	private int resetCout;//重复处理次数
	private String queueName;//队列名称
	private String treeCode;//树叶子节点
	private int sortNum;//排序序号
	private String deptId;//机构ID
	private String deptName;//机构名称
	private String createUserId;//创建用户id
	private String createUserName;//创建用户名称
	private Date createDate;//创建日期
	private String updateUserId;//更新用户id
	private String updateUserName;//更新用户名
	private Date updateDate;//更新日期
	private String delFlag;//逻辑删除标记
	private String productId;//业务类型ID
    private String queueNode;//队列分发节点
	private String taskSource;//资源跳转url
	private String taskSourceId;//资源跳转Id
	
	//已下字段只做参数传递或页面显示
	private String taskTypeDesc;//任务类型中文
	private String statusDesc;//状态中文
	private String delFlagDesc;//逻辑删除中文
	private String verId;
	private List<TaskDispatchConfig> children;
	
	public String getQueueNode() {
		return queueNode;
	}
	public void setQueueNode(String queueNode) {
		this.queueNode = queueNode;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getTaskNo() {
		return taskNo;
	}
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}
	public String getTaskDesc() {
		return taskDesc;
	}
	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSleepTime() {
		return sleepTime;
	}
	public void setSleepTime(String sleepTime) {
		this.sleepTime = sleepTime;
	}
	public String getWaiteTime() {
		return waiteTime;
	}
	public void setWaiteTime(String waiteTime) {
		this.waiteTime = waiteTime;
	}
	public int getResetCout() {
		return resetCout;
	}
	public void setResetCout(int resetCout) {
		this.resetCout = resetCout;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
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
	
	public String getTaskTypeDesc() {
		if("AUTO".equals(this.getTaskType())){
			taskTypeDesc="自动";
		}else if("HAND".equals(this.getTaskType())){
			taskTypeDesc="自动";
		}else {
			taskTypeDesc="等待";
		}
		return taskTypeDesc;
	}

	public String getStatusDesc() {
		if("0".equals(this.getStatus())){
			statusDesc ="新增";
		}else if ("1".equals(this.getStatus())){
			statusDesc ="启动";
		}else{
			statusDesc ="停用";
		}
		return statusDesc;
	}
	public String getDelFlagDesc() {
		return delFlagDesc;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public int getSortNum() {
		return sortNum;
	}
	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public String getVerId() {
		return verId;
	}

	public void setVerId(String verId) {
		this.verId = verId;
	}

	public List<TaskDispatchConfig> getChildren() {
		return children;
	}

	public void setChildren(List<TaskDispatchConfig> children) {
		this.children = children;
	}

	public String getTaskSource() {
		return taskSource;
	}

	public void setTaskSource(String taskSource) {
		this.taskSource = taskSource;
	}

	public String getTaskSourceId() {
		return taskSourceId;
	}

	public void setTaskSourceId(String taskSourceId) {
		this.taskSourceId = taskSourceId;
	}
}
