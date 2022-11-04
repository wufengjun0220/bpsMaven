package com.mingtech.application.sysmanage.domain;

import java.util.Date;


/**
 * 任务移交角色明细实体
 * @author huboA
 * @hibernate.class table="T_TASK_TRANSFER_ROLE"
 * @hibernate.cache usage="read-write"
 *
 */
public class TaskTransferRole implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	
	private String id;					//主键ID
	private String transferId;		//工作任务移交表ID
	private String transferedRoleId ;    //移交角色ID
	private String transferedRoleName;    //移交角色名称
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTransferId() {
		return transferId;
	}
	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}
	public String getTransferedRoleId() {
		return transferedRoleId;
	}
	public void setTransferedRoleId(String transferedRoleId) {
		this.transferedRoleId = transferedRoleId;
	}
	public String getTransferedRoleName() {
		return transferedRoleName;
	}
	public void setTransferedRoleName(String transferedRoleName) {
		this.transferedRoleName = transferedRoleName;
	}
}
