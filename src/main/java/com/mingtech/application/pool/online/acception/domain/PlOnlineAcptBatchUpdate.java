package com.mingtech.application.pool.online.acception.domain;

/**
 * 银承批次记录表
 */
public class PlOnlineAcptBatchUpdate {
	
	private String id;                        //主键ID
	private String batchId;                   //批次id
	private int succCount;                     //银承批次下明细签收处理完成的条数
	private int failCount;                     //银承批次下明未用退回细处理完成的条数
	private int count;                     //银承批次下明细处理完成的条数
	private String ids;	//记录未用退回的明细id
	
	
	public PlOnlineAcptBatchUpdate() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSuccCount() {
		return succCount;
	}
	public void setSuccCount(int succCount) {
		this.succCount = succCount;
	}
	public int getFailCount() {
		return failCount;
	}
	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	
	
}
