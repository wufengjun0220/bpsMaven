package com.mingtech.application.ecds.common.domain;

/**
 * FlowNo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class FlowNoDto implements java.io.Serializable {

	// Fields

	private String flowId;//主键
	private String SDate;//日期
	private String SSeq;//序号

	// Constructors

	/** default constructor */
	public FlowNoDto() {
	}


	// Property accessors

	public String getFlowId() {
		return this.flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}


	
	public String getSDate(){
		return SDate;
	}


	
	public void setSDate(String date){
		SDate = date;
	}


	
	public String getSSeq(){
		return SSeq;
	}


	
	public void setSSeq(String seq){
		SSeq = seq;
	}

}