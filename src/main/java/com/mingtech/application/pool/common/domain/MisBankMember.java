package com.mingtech.application.pool.common.domain;

import java.util.Date;


/**
 * MIS系统承兑行高低风险信息查询表，该表每日日终由数据平台删除并更新最新的数据到票据池库中，票据池系统不做任何处理，只做查询
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-30
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class MisBankMember implements java.io.Serializable {


	private static final long serialVersionUID = -4586494918954566232L;
	private String id;
	private String serialno;//流水号
	private String memberId;//会员代码
	private String bankname;//行名
	private String bankno;//支付行号
	private String lowflag;//是否低风险银行  Y 是
	private String status;//是否有效  Y 有效'
	private Date createtime;//创建时间


	public MisBankMember() {
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

}