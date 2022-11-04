package com.mingtech.application.pool.draft.web;

import java.util.List;

import com.mingtech.application.pool.common.domain.PoolBillInfo;

public class BillBean {
	private PoolBillInfo billInfo;
	private List endorseInfoList;
	private List registerGuarnteeList;
	private List acceptanceGuarnteeList;
	public PoolBillInfo getBillInfo() {
		return billInfo;
	}
	public void setBillInfo(PoolBillInfo billInfo) {
		this.billInfo = billInfo;
	}
	
	public List getEndorseInfoList() {
		return endorseInfoList;
	}
	public void setEndorseInfoList(List endorseInfoList) {
		this.endorseInfoList = endorseInfoList;
	}
	public List getRegisterGuarnteeList() {
		return registerGuarnteeList;
	}
	public void setRegisterGuarnteeList(List registerGuarnteeList) {
		this.registerGuarnteeList = registerGuarnteeList;
	}
	public List getAcceptanceGuarnteeList() {
		return acceptanceGuarnteeList;
	}
	public void setAcceptanceGuarnteeList(List acceptanceGuarnteeList) {
		this.acceptanceGuarnteeList = acceptanceGuarnteeList;
	}
	
	
}
