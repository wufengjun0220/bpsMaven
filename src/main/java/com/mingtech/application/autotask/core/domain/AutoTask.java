package com.mingtech.application.autotask.core.domain;

import java.io.Serializable;
import java.util.Date;

	
public class AutoTask implements Serializable{
	
	/**启用*/
	public final static String STATUS_OPEN = "1";
	/**关闭 */
	public final static String STATUS_CLOSE = "2";
	/**
	 * 自动执行
	 */
	public final static String EXEC_MODULE_AUTO = "0";
	/**
	 * 手工执行
	 */
	public final static String EXEC_MODULE_MAUAL = "1";

	/**
	 * 任务执行状态，0:初始任务 1:当日未执行 2:当日执行中 4:异常 5:人工触发执行中
	 */
	public final static String EXEC_STATUS_0="0";
	/**
	 * 1:当日未执行
	 */
	public final static String EXEC_STATUS_1="1";
	/**
	 *  2:当日执行中
	 */
	public final static String EXEC_STATUS_2="2";
	/**
	 * 4:异常
	 */
	public final static String EXEC_STATUS_4="4";
	/**
	 * 5:人工触发执行中
	 */
	public final static String EXEC_STATUS_5="5";
	
	private String id;
	private String name;//任务名称
	private String code;//任务编码 要唯一
	private String className;//执行的具体类
	//自动加载任务时 会加载  开启+自动执行
	private String status;//1开启 2关闭
	private String execModule;//任务执行模式 0：自动执行   1：手工执行
	
	private String cronExpr;//时间表达式
	private String dependTasks;//依赖的任务   写任务编码，并以,分割
	private Integer waitingTime;//依赖任务的等待时间 秒为单位 
	private Date workday;//当前或者下一执行日期;例如 2016-01-01当日执行完毕后 会将此设置为 2016-01-02
	private String execStatus;//执行状态任务执行状态，0:初始任务 1:当日未执行 2:当日执行中 4:异常
	private Integer orderNum;//排序号
	private String remark;//备注信息
	
	private String taskRunType;//任务调度方式   1：人工调度 0：自动执行；  属于辅助字段
	private String statusDesc;//1开启 2关闭
	private String execStatusDesc;//
	
	public String getTaskRunType() {
		return taskRunType;
	}
	public void setTaskRunType(String taskRunType) {
		this.taskRunType = taskRunType;
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	public String getExecModule() {
		if(execModule == null){
			return "0";
		}
		return execModule;
	}
	public void setExecModule(String execModule) {
		this.execModule = execModule;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCronExpr() {
		return cronExpr;
	}
	public void setCronExpr(String cronExpr) {
		this.cronExpr = cronExpr;
	}
	public String getDependTasks() {
		return dependTasks;
	}
	public void setDependTasks(String dependTasks) {
		this.dependTasks = dependTasks;
	}
	public Integer getWaitingTime() {
		return waitingTime==null?0:waitingTime;
	}
	public void setWaitingTime(Integer waitingTime) {
		this.waitingTime = waitingTime;
	}
	public String getExecStatus() {
		return execStatus;
	}
	public void setExecStatus(String execStatus) {
		this.execStatus = execStatus;
	}
	public Date getWorkday() {
		return workday;
	}
	public void setWorkday(Date workday) {
		this.workday = workday;
	}
	
	public String getStatusDesc() {
		if(STATUS_OPEN.equals(status)){
			return "开启";
		}else{
			return "关闭";
		}
	}
	
	public String getExecStatusDesc() {
		if(EXEC_STATUS_0.equals(execStatus)){
			return "初始任务";
		}else if(EXEC_STATUS_1.equals(execStatus)){
			return "当日未执行";
		}else if(EXEC_STATUS_2.equals(execStatus)){
			return "当日执行中";
		}else if(EXEC_STATUS_4.equals(execStatus)){
			return "异常";
		}else if(EXEC_STATUS_5.equals(execStatus)){
			return "人工触发执行中";
		}else{
			return "";
		}
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
