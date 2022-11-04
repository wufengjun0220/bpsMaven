package com.mingtech.application.pool.common.domain;

import java.util.Date;


/**
 * 该表用来存储日终同步的mis系统的承兑人高低风险信息表信息，该表与MIS表比较，若存在差异，更新为MIS最新的，只存高风险
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-30
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class BankMember implements java.io.Serializable {

	private static final long serialVersionUID = -4486789332885196674L;
	private String id;
	private String serialno;//流水号
	private String memberId;//会员代码
	private String bankname;//行名
	private String bankno;//支付行号
	private String lowflag;//是否低风险银行  Y 是
	private String status;//是否有效  Y 有效'
	private Date createtime;//创建时间
	private Date updatetime;//更新时间

	public BankMember() {
	}



	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSerialno() {
		return this.serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public String getMemberId() {
		return this.memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getBankname() {
		return this.bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBankno() {
		return this.bankno;
	}

	public void setBankno(String bankno) {
		this.bankno = bankno;
	}

	public String getLowflag() {
		return this.lowflag;
	}

	public void setLowflag(String lowflag) {
		this.lowflag = lowflag;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

}