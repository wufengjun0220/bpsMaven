package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 票据池服务费收费标准
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-1
 */

public class PlFeeScale implements java.io.Serializable {

	private static final long serialVersionUID = 6963986565065786163L;
	// Fields

	private String id;
	private BigDecimal everyYear;//年费（元/每年）
	private BigDecimal everyPiece;//逐笔收费（元/每笔）
	private Date createDate;//创建日期
	private Date isseDt;//生效日期
	private String branchNo;
	private String productType;//产品类型    FEE_01:票据池服务费
	private String crtName;//创建人姓名

	// Constructors

	/** default constructor */
	public PlFeeScale() {
	}

	/** full constructor */
	public PlFeeScale(BigDecimal everyYear, BigDecimal everyPiece, Date createDate,
			Date isseDt,String branchNo,String productType) {
		this.everyYear = everyYear;
		this.everyPiece = everyPiece;
		this.createDate = createDate;
		this.isseDt = isseDt;
		this.branchNo = branchNo;
		this.productType = productType;
		this.crtName =  crtName;
	}

	// Property accessors
	
	public String getId() {
		return this.id;
	}

	public String getBranchNo() {
		return branchNo;
	}

	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getEveryYear() {
		return this.everyYear;
	}

	public void setEveryYear(BigDecimal everyYear) {
		this.everyYear = everyYear;
	}

	public BigDecimal getEveryPiece() {
		return this.everyPiece;
	}

	public void setEveryPiece(BigDecimal everyPiece) {
		this.everyPiece = everyPiece;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getIsseDt() {
		return this.isseDt;
	}

	public void setIsseDt(Date isseDt) {
		this.isseDt = isseDt;
	}

	public String getCrtName() {
		return crtName;
	}

	public void setCrtName(String crtName) {
		this.crtName = crtName;
	}
	
	

}