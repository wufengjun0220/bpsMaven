package com.mingtech.application.sysmanage.domain;

import java.util.Date;


/**
 * 工作任务移交实体
 * @author huboA
 * @hibernate.class table="T_TASK_TRANSFER"
 * @hibernate.cache usage="read-write"
 *
 */
public class TaskTransfer implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String STATU_ADD = "TR_01";//新增
	public static final String STATU_EFFECT = "TR_02";//生效
	public static final String STATU_END = "TR_03";//手工终止
	
	public static final String PRODUCT_ID = "3301";
	
	private String id;					//主键ID
	private String transferorUid;		//移交人用户ID
	private String transferorUname ;    //移交人用户名
	private String transferorUcode;    //移交人用户编号
	private String transferorUdeptId;  //移交人机构ID
	private String transferorUdeptName; //移交人机构名
	private String transferedUid;//被移交人用户ID
	private String transferedUname;//被移交人用户名
	private String transferedUcode;//被移交人用户编号
	private String transferedUdeptId;//被移交人机构ID
	private String transferedUdeptName;//被移交人机构名
	private String transferStatus;//移交状态
	private Date transferStartTime;//移交起始时间
	private Date transferStopTime;//移交截止时间
	private Date transferEndTime;//移交手工终止时间
	private String transferRemark;//移交说明
	private Date createTime;//主键ID
	
	private String transferStatusDesc;//状态描述
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTransferorUid() {
		return transferorUid;
	}
	public void setTransferorUid(String transferorUid) {
		this.transferorUid = transferorUid;
	}
	public String getTransferorUname() {
		return transferorUname;
	}
	public void setTransferorUname(String transferorUname) {
		this.transferorUname = transferorUname;
	}
	public String getTransferorUcode() {
		return transferorUcode;
	}
	public void setTransferorUcode(String transferorUcode) {
		this.transferorUcode = transferorUcode;
	}
	public String getTransferorUdeptId() {
		return transferorUdeptId;
	}
	public void setTransferorUdeptId(String transferorUdeptId) {
		this.transferorUdeptId = transferorUdeptId;
	}
	public String getTransferorUdeptName() {
		return transferorUdeptName;
	}
	public void setTransferorUdeptName(String transferorUdeptName) {
		this.transferorUdeptName = transferorUdeptName;
	}
	public String getTransferedUid() {
		return transferedUid;
	}
	public void setTransferedUid(String transferedUid) {
		this.transferedUid = transferedUid;
	}
	public String getTransferedUname() {
		return transferedUname;
	}
	public void setTransferedUname(String transferedUname) {
		this.transferedUname = transferedUname;
	}
	public String getTransferedUcode() {
		return transferedUcode;
	}
	public void setTransferedUcode(String transferedUcode) {
		this.transferedUcode = transferedUcode;
	}
	public String getTransferedUdeptId() {
		return transferedUdeptId;
	}
	public void setTransferedUdeptId(String transferedUdeptId) {
		this.transferedUdeptId = transferedUdeptId;
	}
	public String getTransferedUdeptName() {
		return transferedUdeptName;
	}
	public void setTransferedUdeptName(String transferedUdeptName) {
		this.transferedUdeptName = transferedUdeptName;
	}
	public String getTransferStatus() {
		return transferStatus;
	}
	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}
	public Date getTransferStartTime() {
		return transferStartTime;
	}
	public void setTransferStartTime(Date transferStartTime) {
		this.transferStartTime = transferStartTime;
	}
	public Date getTransferEndTime() {
		return transferEndTime;
	}
	public void setTransferEndTime(Date transferEndTime) {
		this.transferEndTime = transferEndTime;
	}
	public String getTransferRemark() {
		return transferRemark;
	}
	public void setTransferRemark(String transferRemark) {
		this.transferRemark = transferRemark;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getTransferStopTime() {
		return transferStopTime;
	}
	public void setTransferStopTime(Date transferStopTime) {
		this.transferStopTime = transferStopTime;
	}
	
	
	
}
