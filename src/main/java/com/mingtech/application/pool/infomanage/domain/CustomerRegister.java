package com.mingtech.application.pool.infomanage.domain;

import java.util.Date;

/**
 * 汉口银行客户信息登记表
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-17
 * @copyright 北明明润（北京）科技有限责任公司
 */

public class CustomerRegister implements java.io.Serializable {

	private static final long serialVersionUID = 6699499613567631656L;
	private String id;
	private String custNo;//核心客户号
	private String custName;//客户名称
	private String firstDateSource;//首次数据来源：PJC010 PJC033 PJE014 
	private Date firstSignDate;//首次签约日期
	private Date createDate;//创建时间
	private Date updateDate;//更新时间
	private int olPrdtSerialNo;//在线业务主业务合同序列号，用于记录在线业务主业务合同生成的编号
	private int olLoanSerialNo;//在线业务借据序列号，用于记录在线业务借据生成的编号
	
	


	public int getOlPrdtSerialNo() {
		return olPrdtSerialNo;
	}


	public void setOlPrdtSerialNo(int olPrdtSerialNo) {
		this.olPrdtSerialNo = olPrdtSerialNo;
	}


	public int getOlLoanSerialNo() {
		return olLoanSerialNo;
	}


	public void setOlLoanSerialNo(int olLoanSerialNo) {
		this.olLoanSerialNo = olLoanSerialNo;
	}


	/** default constructor */
	public CustomerRegister() {
	}


	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public String getFirstDateSource() {
		return firstDateSource;
	}


	public void setFirstDateSource(String firstDateSource) {
		this.firstDateSource = firstDateSource;
	}


	public Date getFirstSignDate() {
		return this.firstSignDate;
	}

	public void setFirstSignDate(Date firstSignDate) {
		this.firstSignDate = firstSignDate;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}