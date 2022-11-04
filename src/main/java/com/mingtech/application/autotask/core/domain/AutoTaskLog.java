package com.mingtech.application.autotask.core.domain;

import java.io.Serializable;
import java.util.Date;

import com.mingtech.application.autotask.core.AutoTaskInstance;


public class AutoTaskLog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String taskId;//任务ID
	private Date startTime;//开始时间
	private Date endTime;
	private Date runDate;//执行日期
	private String status;//状态
	private String errMessage;//错误信息
	
	private String statusDesc;
	/**
	 * @return Returns the endTime.
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime The endTime to set.
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return Returns the errMessage.
	 */
	public String getErrMessage() {
		return errMessage;
	}
	/**
	 * @param errMessage The errMessage to set.
	 */
	public void setErrMessage(String errMessage) {
		if(errMessage!=null && errMessage.length()>600){
			errMessage = errMessage.substring(0, 600);
		}
		this.errMessage = errMessage;
	}
	/**
	 * @return Returns the runDate.
	 */
	public Date getRunDate() {
		return runDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @param runDate The runDate to set.
	 */
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	/**
	 * @return Returns the startTime.
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime The startTime to set.
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return Returns the taskId.
	 */
	public String getTaskId() {
		return taskId;
	}
	/**
	 * @param taskId The taskId to set.
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getStatusDesc() {
		//1未启动；2运行中；3已完成；4异常";	
		if(AutoTaskInstance.STATUS_RUN.equals(status)){
			return "运行中";
		}else if(AutoTaskInstance.STATUS_FINISH.equals(status)){
			return "已完成";
		}else if(AutoTaskInstance.STATUS_ERROR.equals(status)){
			return "运行异常";
		}else{
			return "";
		}
	}
	
}
