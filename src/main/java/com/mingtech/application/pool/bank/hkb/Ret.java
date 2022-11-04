package com.mingtech.application.pool.bank.hkb;

import java.util.List;

public class Ret {
	private String RET_CODE;//交易返回代码
	private String RET_MSG;//交易返回信息
	
	private List someList;//用来传值的list
	private List someList2;//用来传值的list
	private List someList3;//用来传值的list
	private String Error_MSG;//错误返回信息

	public Ret() {
		super();
	}
	public String getRET_CODE() {
		return RET_CODE;
	}
	public void setRET_CODE(String rET_CODE) {
		RET_CODE = rET_CODE;
	}
	public String getRET_MSG() {
		return RET_MSG;
	}
	public void setRET_MSG(String rET_MSG) {
		RET_MSG = rET_MSG;
	}
	public List getSomeList() {
		return someList;
	}
	public void setSomeList(List someList) {
		this.someList = someList;
	}
	public List getSomeList2() {
		return someList2;
	}
	public void setSomeList2(List someList2) {
		this.someList2 = someList2;
	}
	public List getSomeList3() {
		return someList3;
	}
	public void setSomeList3(List someList3) {
		this.someList3 = someList3;
	}
	public String getError_MSG() {
		return Error_MSG;
	}
	public void setError_MSG(String error_MSG) {
		Error_MSG = error_MSG;
	}
	
	

}
