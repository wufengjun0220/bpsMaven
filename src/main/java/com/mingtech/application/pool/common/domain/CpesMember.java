package com.mingtech.application.pool.common.domain;

import java.util.Date;

/**
 * 票交所会员信息表--日终数据平台从电票系统抽数获得
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-31
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class CpesMember implements java.io.Serializable {


	private static final long serialVersionUID = -4453173472958845928L;
	private Long id;               //主键ID           
	private String memberId;       //会员代码          
	private String memberTypeCode; //会员类别代码        
	private String memberName;     //会员名称          
	private String memberStatus;   //会员状态          
	private String memberBankNo;   //会员大额行号        
	private String clearMode;      //清算模式          
	private Integer operDt;        //操作日期          
	private String isClearCheck;   //是否开通结算确认      
	private String isPlatform;     //是否平台内         
	private String certDn;         //证书DN          
	private String certSn;         //证书SN          
	private String lastCertDn;     //上一个DN         
	private String memberSysStatus;//会员故障状态        
	private String custNo;         //客户号           
	private String settleConfirm;  //财务公司ECDS线上清算权限
	private Date createTime;       //创建时间          
	private Date updateTime;       //修改时间          


	public CpesMember() {
	}


	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMemberId() {
		return this.memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberTypeCode() {
		return this.memberTypeCode;
	}

	public void setMemberTypeCode(String memberTypeCode) {
		this.memberTypeCode = memberTypeCode;
	}

	public String getMemberName() {
		return this.memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberStatus() {
		return this.memberStatus;
	}

	public void setMemberStatus(String memberStatus) {
		this.memberStatus = memberStatus;
	}

	public String getMemberBankNo() {
		return this.memberBankNo;
	}

	public void setMemberBankNo(String memberBankNo) {
		this.memberBankNo = memberBankNo;
	}

	public String getClearMode() {
		return this.clearMode;
	}

	public void setClearMode(String clearMode) {
		this.clearMode = clearMode;
	}

	public Integer getOperDt() {
		return this.operDt;
	}

	public void setOperDt(Integer operDt) {
		this.operDt = operDt;
	}

	public String getIsClearCheck() {
		return this.isClearCheck;
	}

	public void setIsClearCheck(String isClearCheck) {
		this.isClearCheck = isClearCheck;
	}

	public String getIsPlatform() {
		return this.isPlatform;
	}

	public void setIsPlatform(String isPlatform) {
		this.isPlatform = isPlatform;
	}

	public String getCertDn() {
		return this.certDn;
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String getCertSn() {
		return this.certSn;
	}

	public void setCertSn(String certSn) {
		this.certSn = certSn;
	}

	public String getLastCertDn() {
		return this.lastCertDn;
	}

	public void setLastCertDn(String lastCertDn) {
		this.lastCertDn = lastCertDn;
	}

	public String getMemberSysStatus() {
		return this.memberSysStatus;
	}

	public void setMemberSysStatus(String memberSysStatus) {
		this.memberSysStatus = memberSysStatus;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getSettleConfirm() {
		return this.settleConfirm;
	}

	public void setSettleConfirm(String settleConfirm) {
		this.settleConfirm = settleConfirm;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}