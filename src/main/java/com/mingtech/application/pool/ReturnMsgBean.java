package com.mingtech.application.pool;

import java.util.List;

import com.mingtech.application.pool.draft.domain.DraftPool;

public class ReturnMsgBean {
	
	public List<DraftPool> lowRiskDraftPool;//低风险票据信息列表
	public List<DraftPool> highRiskDraftPool;//高风险票据信息列表
	
	
	public List<DraftPool> getLowRiskDraftPool() {
		return lowRiskDraftPool;
	}
	public void setLowRiskDraftPool(List<DraftPool> lowRiskDraftPool) {
		this.lowRiskDraftPool = lowRiskDraftPool;
	}
	public List<DraftPool> getHighRiskDraftPool() {
		return highRiskDraftPool;
	}
	public void setHighRiskDraftPool(List<DraftPool> highRiskDraftPool) {
		this.highRiskDraftPool = highRiskDraftPool;
	}
	
	
	
	

}
