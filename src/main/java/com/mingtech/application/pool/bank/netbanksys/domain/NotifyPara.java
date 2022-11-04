package com.mingtech.application.pool.bank.netbanksys.domain;

public class NotifyPara {
	
	int totalSize; //记录总数
	int DQHK;      //到期还款
	int DQTS;      //到期托收
	int XYDQ;      //协议到期
	
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getDQHK() {
		return DQHK;
	}
	public void setDQHK(int dQHK) {
		DQHK = dQHK;
	}
	public int getDQTS() {
		return DQTS;
	}
	public void setDQTS(int dQTS) {
		DQTS = dQTS;
	}
	public int getXYDQ() {
		return XYDQ;
	}
	public void setXYDQ(int xYDQ) {
		XYDQ = xYDQ;
	}
	
	
	

}
