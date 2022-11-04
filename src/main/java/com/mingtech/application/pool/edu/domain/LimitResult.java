package com.mingtech.application.pool.edu.domain;

import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.common.domain.Asset;


public class LimitResult {
	
	private Boolean result;
	
	private String resultReason;
	
	private	Map limitMap;
	
	private List limitList;

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public String getResultReason() {
		return resultReason;
	}

	public void setResultReason(String resultReason) {
		this.resultReason = resultReason;
	}

	public Map getLimitMap() {
		return limitMap;
	}

	public void setLimitMap(Map limitMap) {
		this.limitMap = limitMap;
	}

	public List getLimitList() {
		return limitList;
	}

	public void setLimitList(List limitList) {
		this.limitList = limitList;
	}

	

	
	
}

