package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;

/**
 * 信贷产品查询结果集
 * @author yixiaolong
 *
 */
public class CreditPrdtQuery {
	private String custName;    //客户名称
	private BigDecimal crdtAmt;      //信贷金额
	private BigDecimal useAmt;      //使用额度
	private String crdtBankName;    //处理机构
	private String cutalCnt;        //按户合计笔数
	
	private String totalNum;       //总笔数
	
	private String sumNum;          //总笔数
	private String sumMoney;   //总金额
	private String totalCust;  //总户数

	public String getSumNum() {
		return sumNum;
	}
	public void setSumNum(String sumNum) {
		this.sumNum = sumNum;
	}
	public String getSumMoney() {
		return sumMoney;
	}
	public void setSumMoney(String sumMoney) {
		this.sumMoney = sumMoney;
	}
	public String getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public BigDecimal getCrdtAmt() {
		return crdtAmt;
	}
	public void setCrdtAmt(BigDecimal crdtAmt) {
		this.crdtAmt = crdtAmt;
	}
	public BigDecimal getUseAmt() {
		return useAmt;
	}
	public void setUseAmt(BigDecimal useAmt) {
		this.useAmt = useAmt;
	}
	public String getCrdtBankName() {
		return crdtBankName;
	}
	public void setCrdtBankName(String crdtBankName) {
		this.crdtBankName = crdtBankName;
	}
	public String getCutalCnt() {
		return cutalCnt;
	}
	public void setCutalCnt(String cutalCnt) {
		this.cutalCnt = cutalCnt;
	}
	public String getTotalCust() {
		return totalCust;
	}
	public void setTotalCust(String totalCust) {
		this.totalCust = totalCust;
	}
}
