package com.mingtech.application.autotask.core;

import java.io.Serializable;
import java.util.Date;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 虚拟的 任务监控使用 对象
 * @author Administrator
 *
 */
public class AutoTaskInstance implements Serializable{
	private static final long serialVersionUID = -3587122976651672073L;
	/**未启动*/
	public final static String STATUS_UNUSED = "1";
	/**运行中*/
	public final static String STATUS_RUN = "2";
	/**已完成*/
	public final static String STATUS_FINISH = "3";
	/**异常*/
	public final static String STATUS_ERROR = "4";		

	private String taskId;
	private String name;
	private String status;//1未启动；2运行中；3已完成；4异常";	
	private Date startTime;
	private Date endTime;
	private Date runDate;
	private int rate;
	
	private String startTimeStr;
	private String endTimeStr;
	private String runDateStr;
	
	
		
	/**
	 * @return Returns the endTimeStr.
	 */
	public String getEndTimeStr() {
		if (endTime == null)
			return "";		
		return DateUtils.getTime(endTime, "yyyy-MM-dd hh:mm:ss");
		
	}

	/**
	 * @param endTimeStr The endTimeStr to set.
	 */
	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	/**
	 * @return Returns the runDateStr.
	 */
	public String getRunDateStr() {
		if (runDate == null)
			return "";
		return DateUtils.getTime(runDate, "yyyy-MM-dd");
	}

	/**
	 * @param runDateStr The runDateStr to set.
	 */
	public void setRunDateStr(String runDateStr) {
		this.runDateStr = runDateStr;
	}

	/**
	 * @return Returns the startTimeStr.
	 */
	public String getStartTimeStr() {
		if (this.startTime == null)
			return "";
		return DateUtils.getTime(startTime, "yyyy-MM-dd hh:mm:ss");
	}

	/**
	 * @param startTimeStr The startTimeStr to set.
	 */
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

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
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the runDate.
	 */
	public Date getRunDate() {
		return runDate;
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


	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return Returns the rate.
	 */
	public int getRate() {
		return rate;
	}

	/**
	 * @param rate The rate to set.
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}
	
	

}
