package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商票保贴额度占用实体 
 * @Description 该实体不再只是商票使用，银票额度占用也放到该表中    --20191105
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-1
 */

public class PedGuaranteeCredit implements java.io.Serializable {

	// Fields
	private static final long serialVersionUID = -4986121065041576136L;
	private String id;
	private String bpsNo;//票据池编号
	private String bpsName;//票据池名称
	private String custNo;//客户号
	private String custName;//客户名称
	private String billNo;//票号
	private BigDecimal billAmt;//票面金额
	private String acceptor;//承兑人（保贴人）
	private String acceptorOrg;//承兑人（保贴人）组织机构代码
	private String status;//状态    1:占用  0：释放
	private Date createTime;//创建时间
	private String billType;//票据类型   AC01银票   AC02商票 
	
	private  String creditObjType;//额度主体类型  1-同业额度  2-对公额度
	private String guarantDiscName;//保贴人名称           
	private String guarantDiscNo;  //保贴编号   
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	
	/*** 融合改造新增字段  end*/
	
	
	
	
	public String getCreditObjType() {
		return creditObjType;
	}

	public String getBeginRangeNo() {
		return beginRangeNo;
	}

	public void setBeginRangeNo(String beginRangeNo) {
		this.beginRangeNo = beginRangeNo;
	}

	public String getEndRangeNo() {
		return endRangeNo;
	}

	public void setEndRangeNo(String endRangeNo) {
		this.endRangeNo = endRangeNo;
	}

	public void setCreditObjType(String creditObjType) {
		this.creditObjType = creditObjType;
	}

	public String getGuarantDiscName() {
		return guarantDiscName;
	}

	public void setGuarantDiscName(String guarantDiscName) {
		this.guarantDiscName = guarantDiscName;
	}

	public String getGuarantDiscNo() {
		return guarantDiscNo;
	}

	public void setGuarantDiscNo(String guarantDiscNo) {
		this.guarantDiscNo = guarantDiscNo;
	}

	//liuxiaodong add
	private String isGroup;
	// Constructors

	/** default constructor */
	public PedGuaranteeCredit() {
	}

	/** full constructor */
	public PedGuaranteeCredit(String bpsNo, String bpsName, String custNo,
			String custName, String billNo, BigDecimal billAmt, String acceptor,
			String status, Date createTime, String acceptorOrg, String isGroup,String billType) {
		this.bpsNo = bpsNo;
		this.bpsName = bpsName;
		this.custNo = custNo;
		this.custName = custName;
		this.billNo = billNo;
		this.billAmt = billAmt;
		this.acceptor = acceptor;
		this.acceptorOrg = acceptorOrg;
		this.status = status;
		this.createTime = createTime;
		this.isGroup = isGroup;
		this.billType = billType;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public String getAcceptorOrg() {
		return acceptorOrg;
	}

	public void setAcceptorOrg(String acceptorOrg) {
		this.acceptorOrg = acceptorOrg;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return this.bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
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

	public String getBillNo() {
		return this.billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public BigDecimal getBillAmt() {
		return this.billAmt;
	}

	public void setBillAmt(BigDecimal billAmt) {
		this.billAmt = billAmt;
	}

	public String getAcceptor() {
		return this.acceptor;
	}

	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}
	
	
	
}