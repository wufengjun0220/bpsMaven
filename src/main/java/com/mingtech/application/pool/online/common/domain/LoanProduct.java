package com.mingtech.application.pool.online.common.domain;

/**
 * 核心放款接口产品代码字段映射表
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-20
 * @copyright 北明明润（北京）科技有限责任公司
 */

public class LoanProduct implements java.io.Serializable {


	private static final long serialVersionUID = 822002878403374672L;
	private String id;
	private String loanProductNo;//放款产品代码
	private String loanProductName;//放款产品名称
	private String deductionProductNo;//贷款产品代码
	private String deductionProductName;//贷款产品名称
	private String subjectNo;//科目号

	
	public LoanProduct() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoanProductNo() {
		return this.loanProductNo;
	}

	public void setLoanProductNo(String loanProductNo) {
		this.loanProductNo = loanProductNo;
	}

	public String getLoanProductName() {
		return this.loanProductName;
	}

	public void setLoanProductName(String loanProductName) {
		this.loanProductName = loanProductName;
	}

	public String getDeductionProductNo() {
		return this.deductionProductNo;
	}

	public void setDeductionProductNo(String deductionProductNo) {
		this.deductionProductNo = deductionProductNo;
	}

	public String getDeductionProductName() {
		return this.deductionProductName;
	}

	public void setDeductionProductName(String deductionProductName) {
		this.deductionProductName = deductionProductName;
	}

	public String getSubjectNo() {
		return this.subjectNo;
	}

	public void setSubjectNo(String subjectNo) {
		this.subjectNo = subjectNo;
	}

}