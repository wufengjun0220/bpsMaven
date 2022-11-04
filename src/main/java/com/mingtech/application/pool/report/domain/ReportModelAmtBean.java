package com.mingtech.application.pool.report.domain;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;
import java.util.Date;
@ExcelIgnoreUnannotated
public class ReportModelAmtBean implements java.io.Serializable {

	// Fields\
	@ExcelProperty(value="年份")
	private int  d1;//年
	@ExcelProperty(value="月份")
	private int  d2;//月
	@ExcelProperty(value="票据池签约客户数")
	private int count1 = 0;//票据池签约客户数
	@ExcelProperty(value="票据池总签约客户数")
	private int count2 = 0;//票据池总签约客户数
	@ExcelProperty(value="票据池线下银承发生金额")
	private BigDecimal amt3 = new BigDecimal("0");//票据池线下银承发生金额
	@ExcelProperty(value="票据池线下流贷发生金额")
	private BigDecimal amt4  = new BigDecimal("0");//票据池线下流贷发生金额
	@ExcelProperty(value="票据池线下保函发生金额")
	private BigDecimal amt5  = new BigDecimal("0");//票据池线下保函发生金额
	@ExcelProperty(value="票据池线下信用证发生金额")
	private BigDecimal amt6  = new BigDecimal("0");//票据池线下信用证发生金额
	@ExcelProperty(value="票据池线上银承发生金额")
	private BigDecimal amt7  = new BigDecimal("0");//票据池线上银承发生金额
	@ExcelProperty(value="票据池线上流贷发生金额")
	private BigDecimal amt8  = new BigDecimal("0");//票据池线上流贷发生金额
	@ExcelProperty(value="票据池线下融资金额")
	private BigDecimal amt9  = new BigDecimal("0");//票据池线下融资金额
	@ExcelProperty(value="票据池线上融资金额")
	private BigDecimal amt10  = new BigDecimal("0");//票据池线上融资金额

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public BigDecimal getAmt3() {
		return amt3;
	}

	public void setAmt3(BigDecimal amt3) {
		this.amt3 = amt3;
	}

	public BigDecimal getAmt4() {
		return amt4;
	}

	public void setAmt4(BigDecimal amt4) {
		this.amt4 = amt4;
	}

	public BigDecimal getAmt5() {
		return amt5;
	}

	public void setAmt5(BigDecimal amt5) {
		this.amt5 = amt5;
	}

	public BigDecimal getAmt6() {
		return amt6;
	}

	public void setAmt6(BigDecimal amt6) {
		this.amt6 = amt6;
	}

	public BigDecimal getAmt7() {
		return amt7;
	}

	public void setAmt7(BigDecimal amt7) {
		this.amt7 = amt7;
	}

	public BigDecimal getAmt8() {
		return amt8;
	}

	public void setAmt8(BigDecimal amt8) {
		this.amt8 = amt8;
	}

	public BigDecimal getAmt9() {
		return amt9;
	}

	public void setAmt9(BigDecimal amt9) {
		this.amt9 = amt9;
	}

	public BigDecimal getAmt10() {
		return amt10;
	}

	public void setAmt10(BigDecimal amt10) {
		this.amt10 = amt10;
	}


	public int getD1() {
		return d1;
	}

	public void setD1(int d1) {
		this.d1 = d1;
	}

	public int getD2() {
		return d2;
	}

	public void setD2(int d2) {
		this.d2 = d2;
	}
}